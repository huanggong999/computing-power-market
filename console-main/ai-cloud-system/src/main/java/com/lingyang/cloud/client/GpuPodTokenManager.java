package com.lingyang.cloud.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingyang.cloud.client.config.GpuPodProperties;
import com.lingyang.cloud.config.RestTemplateConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * GPU Pod 服务 JWT Token 管理器
 * 负责通过租户凭证调用 /auth/token 获取 access_token，并按过期时间自动刷新
 */
@Slf4j
@Component
public class GpuPodTokenManager {

    @Autowired
    private GpuPodProperties properties;

    private RestTemplate restTemplate;

    private final ConcurrentMap<String, TenantTokenCache> tenantTokenCaches = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        this.restTemplate = RestTemplateConfig.create((ClientHttpRequestFactory) factory);
    }

    /**
     * 使用配置租户获取有效的 access_token，供后台管理类调用兼容使用。
     */
    public String getAccessToken() {
        return getAccessToken(properties.getTenantId(), properties.getTenantName());
    }

    /**
     * 获取指定租户的有效 access_token，自动处理过期刷新。
     * 每个租户使用独立缓存，避免不同登录客户之间复用 token。
     */
    public String getAccessToken(String tenantId, String tenantName) {
        if (StringUtils.isBlank(tenantId)) {
            throw new IllegalArgumentException("GPU Pod 租户ID不能为空");
        }
        TenantTokenCache cache = tenantTokenCaches.computeIfAbsent(tenantId, key -> new TenantTokenCache());
        if (cache.token != null && Instant.now().isBefore(cache.expiresAt)) {
            return cache.token;
        }
        synchronized (cache) {
            if (cache.token != null && Instant.now().isBefore(cache.expiresAt)) {
                return cache.token;
            }
            refreshToken(tenantId, tenantName, cache);
            return cache.token;
        }
    }

    private void refreshToken(String tenantId, String tenantName, TenantTokenCache cache) {
        String url = properties.getBaseUrl() + "/api/" + properties.getApiVersion() + "/auth/token";
        try {
            requestAndCacheToken(
                    url, tenantId, tenantName, resolveApiKey(tenantId, tenantName, cache), cache);
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 401 || e.getRawStatusCode() == 403) {
                log.warn("GPU Pod access token request rejected, preparing tenant api_key and retrying once: {}",
                        e.getResponseBodyAsString());
                cache.apiKey = prepareTenantApiKey(tenantId, tenantName);
                try {
                    requestAndCacheToken(url, tenantId, tenantName, cache.apiKey, cache);
                    return;
                } catch (RestClientResponseException retryException) {
                    log.error("Failed to fetch GPU Pod access token after api_key refresh: {}",
                            retryException.getResponseBodyAsString(), retryException);
                    throw new RuntimeException("获取 GPU Pod access_token 失败: " + retryException.getMessage());
                }
            }
            log.error("Failed to fetch GPU Pod access token: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("获取 GPU Pod access_token 失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to fetch GPU Pod access token", e);
            throw new RuntimeException("获取 GPU Pod access_token 失败: " + e.getMessage());
        }
    }

    private void requestAndCacheToken(
            String url, String tenantId, String tenantName, String apiKey, TenantTokenCache cache) {
        Map<String, String> body = new HashMap<>();
        body.put("tenantId", tenantId);
        body.put("tenantName", StringUtils.defaultIfBlank(tenantName, tenantId));
        body.put("apiKey", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        log.info("Requesting GPU Pod access token for tenant: {}", tenantId);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, TokenResponse.class);
        TokenResponse tokenResponse = response.getBody();
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("获取 GPU Pod access_token 响应为空");
        }
        cache.token = tokenResponse.getAccessToken();
        long expiresIn = tokenResponse.getExpiresIn() > 0 ? tokenResponse.getExpiresIn() : 3600;
        long aheadSeconds = Math.min(properties.getTokenRefreshAheadSeconds(), expiresIn / 2);
        cache.expiresAt = Instant.now().plusSeconds(expiresIn - aheadSeconds);
        log.info("GPU Pod access token refreshed for tenant: {}, expires at: {}", tenantId, cache.expiresAt);
    }

    private String resolveApiKey(String tenantId, String tenantName, TenantTokenCache cache) {
        if (tenantId.equals(properties.getTenantId()) && StringUtils.isNotBlank(properties.getApiKey())) {
            return properties.getApiKey();
        }
        if (StringUtils.isNotBlank(cache.apiKey)) {
            return cache.apiKey;
        }
        cache.apiKey = prepareTenantApiKey(tenantId, tenantName);
        return cache.apiKey;
    }

    private String prepareTenantApiKey(String tenantId, String tenantName) {
        String existingApiKey = fetchExistingTenantApiKey(tenantId);
        if (StringUtils.isNotBlank(existingApiKey)) {
            return existingApiKey;
        }
        return createTenantApiKey(tenantId, tenantName);
    }

    private String fetchExistingTenantApiKey(String tenantId) {
        String url = properties.getBaseUrl() + "/api/" + properties.getApiVersion()
                + "/admin/tenants/" + tenantId;
        if (StringUtils.isNotBlank(properties.getAdminAuthKey())) {
            url = url + "?authKey=" + properties.getAdminAuthKey();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            log.info("Fetching GPU tenant api_key for existing tenant: {}", tenantId);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<TenantResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, TenantResponse.class);
            TenantResponse tenantResponse = response.getBody();
            if (tenantResponse == null || StringUtils.isBlank(tenantResponse.getApiKey())) {
                throw new RuntimeException("查询 GPU 租户 api_key 响应为空");
            }
            log.info("GPU tenant api_key found for tenant: {}", tenantResponse.getTenantId());
            return tenantResponse.getApiKey();
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                log.info("GPU tenant is not registered yet: {}", tenantId);
                return null;
            }
            log.error("Failed to fetch GPU tenant api_key: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("查询 GPU 租户 api_key 失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to fetch GPU tenant api_key", e);
            throw new RuntimeException("查询 GPU 租户 api_key 失败: " + e.getMessage(), e);
        }
    }

    private String createTenantApiKey(String tenantId, String tenantName) {
        String url = properties.getBaseUrl() + "/api/" + properties.getApiVersion() + "/admin/tenants";
        if (StringUtils.isNotBlank(properties.getAdminAuthKey())) {
            url = url + "?authKey=" + properties.getAdminAuthKey();
        }

        Map<String, String> body = new HashMap<>();
        body.put("tenantId", tenantId);
        body.put("tenantName", StringUtils.defaultIfBlank(tenantName, tenantId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            log.info("Creating or refreshing GPU tenant api_key for tenant: {}", tenantId);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<TenantResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, TenantResponse.class);
            TenantResponse tenantResponse = response.getBody();
            if (tenantResponse == null || StringUtils.isBlank(tenantResponse.getApiKey())) {
                throw new RuntimeException("创建 GPU 租户 api_key 响应为空");
            }
            log.info("GPU tenant api_key prepared for tenant: {}", tenantResponse.getTenantId());
            return tenantResponse.getApiKey();
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 400 || e.getRawStatusCode() == 409) {
                log.info("GPU tenant may have been registered concurrently, fetching api_key: {}", tenantId);
                String existingApiKey = fetchExistingTenantApiKey(tenantId);
                if (StringUtils.isNotBlank(existingApiKey)) {
                    return existingApiKey;
                }
            }
            log.error("Failed to create GPU tenant api_key: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("创建 GPU 租户 api_key 失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to create GPU tenant api_key", e);
            throw new RuntimeException("创建 GPU 租户 api_key 失败: " + e.getMessage(), e);
        }
    }

    private static class TenantTokenCache {
        private volatile String token;
        private volatile String apiKey;
        private volatile Instant expiresAt = Instant.EPOCH;
    }

    @Data
    public static class TokenResponse {
        @JsonProperty("access_token")
        @JsonAlias("accessToken")
        private String accessToken;

        @JsonProperty("token_type")
        @JsonAlias("tokenType")
        private String tokenType;

        @JsonProperty("expires_in")
        @JsonAlias("expiresIn")
        private long expiresIn;

        @JsonProperty("tenant_id")
        @JsonAlias("tenantId")
        private String tenantId;

        @JsonProperty("tenant_name")
        @JsonAlias("tenantName")
        private String tenantName;
    }

    @Data
    public static class TenantResponse {
        private Boolean success;

        @JsonProperty("tenant_id")
        @JsonAlias("tenantId")
        private String tenantId;

        @JsonProperty("tenant_name")
        @JsonAlias("tenantName")
        private String tenantName;

        @JsonProperty("api_key")
        @JsonAlias("apiKey")
        private String apiKey;

        private String status;

        @JsonProperty("created_at")
        @JsonAlias("createdAt")
        private String createdAt;

        @JsonProperty("updated_at")
        @JsonAlias("updatedAt")
        private String updatedAt;
    }
}
