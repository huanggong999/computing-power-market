package com.lingyang.cloud.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.config.GpuPodProperties;
import com.lingyang.cloud.config.RestTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;

/**
 * Adapter for v2.1 GPU resize/bandwidth and independent CPU APIs.
 * The browser never calls the upstream service directly.
 */
@Slf4j
@Component
public class InstanceResourceAdapterClient {
    @Autowired
    private GpuPodProperties properties;
    @Autowired
    private GpuPodTokenManager tokenManager;
    @Autowired
    private GpuPodTenantProvider tenantProvider;

    private RestTemplate restTemplate;
    private RestTemplate catalogRestTemplate;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(Math.max(properties.getReadTimeout(), 600_000));
        restTemplate = RestTemplateConfig.create(factory);
        SimpleClientHttpRequestFactory catalogFactory = new SimpleClientHttpRequestFactory();
        catalogFactory.setConnectTimeout(properties.getConnectTimeout());
        catalogFactory.setReadTimeout(Math.min(properties.getReadTimeout(), 30_000));
        catalogRestTemplate = RestTemplateConfig.create(catalogFactory);
    }

    public JSONObject getResizeOptions(String instanceId) {
        return get("/instances/" + encode(instanceId) + "/resize/options", Map.of("tenant_id", tenantId()));
    }

    public JSONObject submitResize(String instanceId, JSONObject body) {
        return post("/instances/" + encode(instanceId) + "/resize", body, Map.of("tenant_id", tenantId()));
    }

    public JSONObject listResizeTasks(String instanceId) {
        return get("/instances/" + encode(instanceId) + "/resize/tasks", Map.of("tenant_id", tenantId()));
    }

    public JSONObject getResizeTask(String instanceId, String taskId) {
        return get("/instances/" + encode(instanceId) + "/resize/tasks/" + encode(taskId), Map.of("tenant_id", tenantId()));
    }

    public JSONObject changeBandwidth(String instanceId, JSONObject body) {
        return post("/instances/" + encode(instanceId) + "/bandwidth", body, Map.of("tenant_id", tenantId()));
    }

    public JSONObject listCpuRegions() {
        return getPublic("/cpu/regions", Map.of());
    }

    public JSONObject listZones(String region) {
        Map<String, String> params = new java.util.LinkedHashMap<>();
        params.put("scene", "cpu");
        if (StringUtils.isNotBlank(region)) params.put("region", region);
        return getPublic("/zones", params);
    }

    public JSONObject listCpuInstanceTypes(String region) {
        return getPublic("/cpu/instance-types", Map.of("region", region, "include_price", false));
    }

    public JSONObject listCpuPrices(String region, boolean refresh) {
        return getPublic("/cpu/prices", Map.of("region", region, "refresh", refresh));
    }

    public JSONObject listCpuInstances(boolean includeReleased) {
        return get("/cpu/instances", Map.of("tenant_id", tenantId(), "include_released", includeReleased));
    }

    public JSONObject getCpuInstance(String instanceId) {
        return get("/cpu/instances/" + encode(instanceId), Map.of("tenant_id", tenantId()));
    }

    public JSONObject purchaseCpu(JSONObject body) {
        return post("/cpu/instances", body, Map.of("tenant_id", tenantId()));
    }

    public JSONObject startCpuInstance(String instanceId) {
        requireCpuSource(instanceId);
        return post("/cpu/instances/" + encode(instanceId) + "/start", null, Map.of("tenant_id", tenantId()));
    }

    public JSONObject stopCpuInstance(String instanceId, boolean force) {
        requireCpuSource(instanceId);
        return post("/cpu/instances/" + encode(instanceId) + "/stop", null,
                Map.of("tenant_id", tenantId(), "force", force));
    }

    public JSONObject restartCpuInstance(String instanceId, boolean force) {
        requireCpuSource(instanceId);
        return post("/cpu/instances/" + encode(instanceId) + "/restart", null,
                Map.of("tenant_id", tenantId(), "force", force));
    }

    public JSONObject getCpuSshInfo(String instanceId) {
        requireCpuSource(instanceId);
        return get("/cpu/instances/" + encode(instanceId) + "/ssh", Map.of("tenant_id", tenantId()));
    }

    public JSONObject resetCpuPassword(String instanceId, JSONObject body) {
        requireCpuSource(instanceId);
        return post("/cpu/instances/" + encode(instanceId) + "/reset-password", body,
                Map.of("tenant_id", tenantId()));
    }

    public JSONObject renameCpuInstance(String instanceId, JSONObject body) {
        requireCpuSource(instanceId);
        return put("/cpu/instances/" + encode(instanceId) + "/name", body,
                Map.of("tenant_id", tenantId()));
    }

    public JSONObject releaseCpuInstance(String instanceId) {
        // The scoped DELETE validates the instance itself. A preflight detail
        // call can fail while an instance is still creating, which used to
        // block releasing a creating CPU host that already has an instance ID.
        return delete("/cpu/instances/" + encode(instanceId), Map.of("tenant_id", tenantId()));
    }

    private void requireCpuSource(String id) {
        JSONObject response = getCpuInstance(id);
        JSONObject instance = response.getJSONObject("instance");
        if (instance == null) instance = response;
        if (Boolean.FALSE.equals(response.getBoolean("success")) || !"cpu_instance".equals(instance.getString("origin"))) {
            throw new IllegalArgumentException("实例来源不匹配，请刷新列表后重试");
        }
    }

    /** Explicit tenant credentials for durable background work; never inherit request thread state. */
    public JSONObject purchaseCpuForTenant(JSONObject body, String tenantId, String tenantName) {
        return exchangeForTenant(HttpMethod.POST, "/cpu/instances", body, Map.of("tenant_id", tenantId),
                new GpuPodTenantProvider.Tenant(tenantId, tenantName));
    }

    private JSONObject get(String path, Map<String, ?> params) {
        return exchange(HttpMethod.GET, path, null, params, true);
    }

    private JSONObject getPublic(String path, Map<String, ?> params) {
        return exchange(HttpMethod.GET, path, null, params, false);
    }

    private JSONObject post(String path, JSONObject body, Map<String, ?> params) {
        return exchange(HttpMethod.POST, path, body, params, true);
    }

    private JSONObject put(String path, JSONObject body, Map<String, ?> params) {
        return exchange(HttpMethod.PUT, path, body, params, true);
    }

    private JSONObject delete(String path, Map<String, ?> params) {
        return exchange(HttpMethod.DELETE, path, null, params, true);
    }

    private JSONObject exchange(HttpMethod method, String path, JSONObject body, Map<String, ?> params, boolean authenticated) {
        return exchangeForTenant(method, path, body, params, authenticated ? tenantProvider.getCurrentTenant() : null);
    }

    private JSONObject exchangeForTenant(HttpMethod method, String path, JSONObject body, Map<String, ?> params,
                                         GpuPodTenantProvider.Tenant tenant) {
        LinkedMultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        params.forEach((key, value) -> query.add(key, String.valueOf(value)));
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl() + path)
                .queryParams(query)
                .build(true).toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (tenant != null) {
            String token = tokenManager.getAccessToken(tenant.tenantId(), tenant.tenantName());
            if (StringUtils.isNotBlank(token)) headers.setBearerAuth(token);
        }
        HttpEntity<String> entity = new HttpEntity<>(body == null ? null : body.toJSONString(), headers);
        try {
            boolean catalog = path.equals("/cpu/regions") || path.equals("/cpu/prices")
                    || path.equals("/cpu/instance-types") || path.equals("/zones");
            ResponseEntity<String> response = catalog
                    ? catalogRestTemplate.exchange(url, method, entity, String.class)
                    : restTemplate.exchange(url, method, entity, String.class);
            String payload = response.getBody();
            return StringUtils.isBlank(payload) ? new JSONObject() : JSON.parseObject(payload);
        } catch (RestClientResponseException e) {
            log.warn("Upstream adapter request failed: {} {} status={} body={}", method, url, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private String baseUrl() {
        return properties.getBaseUrl() + "/api/" + properties.getApiVersion();
    }

    private String tenantId() {
        return tenantProvider.getCurrentTenant().tenantId();
    }

    private String encode(String value) {
        return UriComponentsBuilder.fromPath("/{value}").buildAndExpand(value).encode().toUriString().substring(1);
    }
}
