package com.lingyang.cloud.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.config.GpuPodProperties;
import com.lingyang.cloud.client.dto.BatchRenewRequest;
import com.lingyang.cloud.client.dto.CreateInstanceRequest;
import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import com.lingyang.cloud.client.dto.GpuPodCreateResponse;
import com.lingyang.cloud.client.dto.InstanceListResponse;
import com.lingyang.cloud.client.dto.InstanceMonitorResponse;
import com.lingyang.cloud.client.dto.InstanceResponse;
import com.lingyang.cloud.client.dto.InstanceToolsResponse;
import com.lingyang.cloud.client.dto.RenewRequest;
import com.lingyang.cloud.client.dto.ResetPasswordRequest;
import com.lingyang.cloud.client.dto.SetInstanceNameRequest;
import com.lingyang.cloud.client.dto.ShutdownScheduleRequest;
import com.lingyang.cloud.client.dto.SshInfoResponse;
import com.lingyang.cloud.config.RestTemplateConfig;
import com.lingyang.cloud.model.vo.VncInfoVO;
import com.lingyang.common.core.exception.http.HttpResultException;
import com.lingyang.common.core.exception.http.HttpServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * GPU Pod API 客户端
 * 用于与 GPU Pod 服务进行 HTTP 通信
 */
@Slf4j
@Component
public class GpuPodApiClient {

    @Autowired
    private GpuPodProperties properties;

    @Autowired
    private GpuPodTokenManager tokenManager;

    @Autowired
    private GpuPodTenantProvider tenantProvider;

    @Value("${gpu.scheduler.auth-key:}")
    private String schedulerAuthKey;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.restTemplate = RestTemplateConfig.create(getClientHttpRequestFactory());
        log.info("GpuPodApiClient initialized with baseUrl: {}", properties.getBaseUrl());
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        return factory;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        if (StringUtils.isNotBlank(resolveAdminAuthKey())) {
//            return headers;
//        }
        GpuPodTenantProvider.Tenant tenant = tenantProvider.getCurrentTenant();
        String token = tokenManager.getAccessToken(tenant.tenantId(), tenant.tenantName());
        if (token != null && !token.isEmpty()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    private String buildApiUrl(String path) {
        String url = properties.getBaseUrl() + "/api/" + properties.getApiVersion() + path;
        String adminAuthKey = resolveAdminAuthKey();
        if (StringUtils.isBlank(adminAuthKey)) {
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + "authKey="
                + UriUtils.encodeQueryParam(adminAuthKey, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String buildTenantApiUrl(String path) {
        return properties.getBaseUrl() + "/api/" + properties.getApiVersion() + path;
    }

    private String resolveAdminAuthKey() {
        return StringUtils.defaultIfBlank(properties.getAdminAuthKey(), schedulerAuthKey);
    }

    /**
     * 创建实例
     */
    public InstanceResponse createInstance(CreateInstanceRequest request) {
        String url = buildApiUrl("/instances");
        try {
            log.info("Creating GPU Pod instance for tenant: {}", request.getTenantId());
            HttpEntity<CreateInstanceRequest> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<InstanceResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, InstanceResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to create instance: {}", e.getResponseBodyAsString(), e);
            return createErrorResponse("创建实例失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create instance", e);
            return createErrorResponse("创建实例失败: " + e.getMessage());
        }
    }

    /**
     * 创建 GPU Pod（对接外部 /api/v1/gpu/pod/create）
     */
    public GpuPodCreateResponse createGpuPod(GpuPodCreateRequest request) {
        String url = buildTenantApiUrl("/gpu/pod/create");
        try {
            log.info("发起gpu租用请求url:{},请求参数:{}", url, JSON.toJSONString(request));
            HttpEntity<GpuPodCreateRequest> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<GpuPodCreateResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, GpuPodCreateResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error(e.getMessage(), e);
            GpuPodCreateResponse response = new GpuPodCreateResponse();
            response.setSuccess(false);
            response.setMessage("实例创建服务返回异常，请稍后重试或联系管理员处理");
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            GpuPodCreateResponse response = new GpuPodCreateResponse();
            response.setSuccess(false);
            response.setMessage("实例创建服务暂时不可用，请稍后重试或联系管理员处理");
            return response;
        }
    }

    /**
     * 获取实例详情
     */
    public InstanceResponse getInstance(String instanceId, String tenantId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            log.info("Getting instance details: {}", instanceId);
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<InstanceResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, InstanceResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to get instance: {}", e.getResponseBodyAsString(), e);
            return createErrorResponse("获取实例失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get instance", e);
            return createErrorResponse("获取实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取 Pod 创建阶段状态。
     */
    public JSONObject getPodStatus(String podName, String tenantId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/gpu/pod/{podName}"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(podName)
                .toUriString();
        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return JSON.parseObject(response.getBody());
        } catch (RestClientResponseException e) {
            JSONObject result = new JSONObject();
            boolean notFound = e.getRawStatusCode() == 404;
            result.put("success", false);
            result.put("status", notFound ? "not_found" : "error");
            result.put("display_status", notFound ? "未找到" : "异常");
            result.put("message", e.getResponseBodyAsString());
            return result;
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("success", false);
            result.put("status", "error");
            result.put("display_status", "异常");
            result.put("message", e.getMessage());
            return result;
        }
    }

    /**
     * 获取当前租户的自定义镜像列表。
     */
    public JSONObject listMyImages(String tenantId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/images/mine"))
                .queryParam("tenant_id", tenantId)
                .toUriString();
        return exchangeForJson(url, HttpMethod.GET, null, "获取我的镜像列表");
    }

    /**
     * 获取 Docker push 命令。
     */
    public JSONObject getMyImagePushCommand(String tenantId, Map<String, Object> request) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/images/mine/push-command"))
                .queryParam("tenant_id", tenantId)
                .toUriString();
        return exchangeForJson(url, HttpMethod.POST, request, "获取镜像上传命令");
    }

    /**
     * 获取 Docker pull 命令。repo 包含斜杠，作为 path segment 传给外部接口前需要编码。
     */
    public JSONObject getMyImagePullCommand(String tenantId, String repo, String tag) {
        String encodedRepo = UriUtils.encodePathSegment(repo, StandardCharsets.UTF_8);
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/images/mine/" + encodedRepo + "/pull-command"))
                .queryParam("tenant_id", tenantId)
                .queryParam("tag", StringUtils.defaultIfBlank(tag, "latest"))
                .build(true)
                .toUriString();
        return exchangeForJson(url, HttpMethod.GET, null, "获取镜像下载命令");
    }

    /**
     * 删除当前租户下的镜像 tag。
     */
    public JSONObject deleteMyImage(String tenantId, String repo, String tag) {
        String encodedRepo = UriUtils.encodePathSegment(repo, StandardCharsets.UTF_8);
        String encodedTag = UriUtils.encodePathSegment(StringUtils.defaultIfBlank(tag, "latest"), StandardCharsets.UTF_8);
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/images/mine/" + encodedRepo + "/tags/" + encodedTag))
                .queryParam("tenant_id", tenantId)
                .build(true)
                .toUriString();
        return exchangeForJson(url, HttpMethod.DELETE, null, "删除我的镜像");
    }

    private JSONObject exchangeForJson(String url, HttpMethod method, Object body, String action) {
        try {
            HttpEntity<?> entity = body == null ? new HttpEntity<>(createHeaders()) : new HttpEntity<>(body, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            String responseBody = response.getBody();
            return StringUtils.isBlank(responseBody) ? new JSONObject() : JSON.parseObject(responseBody);
        } catch (RestClientResponseException e) {
            log.error("{}失败: {}", action, e.getResponseBodyAsString(), e);
            return buildExternalError(e.getResponseBodyAsString(), action + "失败");
        } catch (Exception e) {
            log.error("{}失败", action, e);
            return buildExternalError(e.getMessage(), action + "失败");
        }
    }

    private JSONObject buildExternalError(String message, String fallbackMessage) {
        JSONObject result = new JSONObject();
        result.put("success", false);
        result.put("message", StringUtils.defaultIfBlank(message, fallbackMessage));
        return result;
    }

    /**
     * 获取实例列表
     */
    public InstanceListResponse listInstances(String tenantId, Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances"))
                .queryParam("tenant_id", tenantId);
        if (params != null) {
            params.forEach((key, value) -> {
                if (value != null) {
                    builder.queryParam(key, value);
                }
            });
        }
        String url = builder.toUriString();
        try {
            log.info("Listing instances for tenant: {}", tenantId);
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<InstanceListResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, InstanceListResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to list instances: {}", e.getResponseBodyAsString(), e);
            return createListErrorResponse("获取实例列表失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to list instances", e);
            return createListErrorResponse("获取实例列表失败: " + e.getMessage());
        }
    }

    /**
     * 启动实例
     */
    public void startInstance(String instanceId, String tenantId) {
        String url = buildApiUrl("/instances/{instanceId}/start");
        performAction(instanceId, tenantId, url, "启动");
    }

    /**
     * 停止实例
     */
    public void stopInstance(String instanceId, String tenantId) {
        String url = buildApiUrl("/instances/{instanceId}/stop");
        performAction(instanceId, tenantId, url, "停止");
    }

    /**
     * 重启实例
     */
    public void restartInstance(String instanceId, String tenantId) {
        String url = buildApiUrl("/instances/{instanceId}/restart");
        performAction(instanceId, tenantId, url, "重启");
    }

    /**
     * 释放实例
     */
    public void releaseInstance(String instanceId, String tenantId) {
        String url = buildApiUrl("/instances/{instanceId}/release");
        performAction(instanceId, tenantId, url, "释放");
    }


    /**
     * 重置实例密码
     */
    public InstanceResponse resetPassword(String instanceId, String tenantId, ResetPasswordRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/reset-password"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            HttpEntity<ResetPasswordRequest> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<InstanceResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, InstanceResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to reset password: {}", e.getResponseBodyAsString(), e);
            return createErrorResponse("重置密码失败: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to reset password", e);
            return createErrorResponse("重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 续费实例
     */
    public void renewInstance(String instanceId, String tenantId, RenewRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/renew"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            HttpEntity<RenewRequest> entity = new HttpEntity<>(request, createHeaders());
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to renew instance: {}", e.getResponseBodyAsString(), e);
            throw new HttpServiceException("续费实例失败: " + e.getResponseBodyAsString());
        }
    }

    /**
     * 批量续费实例
     */
    public void batchRenewInstances(String tenantId, BatchRenewRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/batch-renew"))
                .queryParam("tenant_id", tenantId)
                .toUriString();
        try {
            HttpEntity<BatchRenewRequest> entity = new HttpEntity<>(request, createHeaders());
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to batch renew instances: {}", e.getResponseBodyAsString(), e);
            throw new HttpServiceException("批量续费实例失败: " + e.getResponseBodyAsString());
        }
    }

    /**
     * 设置实例名称（外部API）
     */
    public void setInstanceNameExternal(String instanceId, String tenantId, SetInstanceNameRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/name"))
                .queryParam("tenant_id", tenantId)
                .queryParam("name", request.getName())
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to set instance name: {}", e.getResponseBodyAsString(), e);
            throw new HttpServiceException("设置实例名称失败: " + e.getResponseBodyAsString());
        }
    }

    /**
     * 获取实例快捷工具
     */
    public InstanceToolsResponse getInstanceTools(String instanceId, String tenantId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/tools"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<InstanceToolsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, InstanceToolsResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to get instance tools: {}", e.getResponseBodyAsString(), e);
            InstanceToolsResponse response = new InstanceToolsResponse();
            response.setSuccess(false);
            response.setMessage("获取快捷工具失败: " + e.getResponseBodyAsString());
            return response;
        } catch (Exception e) {
            log.error("Failed to get instance tools", e);
            InstanceToolsResponse response = new InstanceToolsResponse();
            response.setSuccess(false);
            response.setMessage("获取快捷工具失败: " + e.getMessage());
            return response;
        }
    }

    /**
     * 获取实例 SSH 连接信息
     */
    public SshInfoResponse getSshInfo(String instanceId, String tenantId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/ssh"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            log.info("Getting SSH info for instance: {}", instanceId);
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<SshInfoResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, SshInfoResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to get SSH info: {}", e.getResponseBodyAsString(), e);
            throw new HttpServiceException("获取SSH连接信息失败: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to get SSH info", e);
            throw new HttpServiceException("获取SSH连接信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取实例 VNC 连接信息
     */
    public VncInfoVO getVncInfo(String instanceId, String tenantId) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/vnc"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            log.info("Getting VNC info for instance: {}", instanceId);
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<VncInfoVO> response = restTemplate.exchange(url, HttpMethod.GET, entity, VncInfoVO.class);
            VncInfoVO vncInfo = response.getBody();
            if (vncInfo != null) {
                vncInfo.setWebsocketUrl(appendWebsocketToken(vncInfo.getWebsocketUrl()));
            }
            return vncInfo;
        } catch (RestClientResponseException e) {
            log.error("Failed to get VNC info: {}", e.getResponseBodyAsString(), e);
            throw new HttpServiceException("获取VNC连接信息失败: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to get VNC info", e);
            throw new HttpServiceException("获取VNC连接信息失败: " + e.getMessage());
        }
    }

    private String appendWebsocketToken(String websocketUrl) {
        if (StringUtils.isBlank(websocketUrl) || websocketUrl.contains("token=")) {
            return websocketUrl;
        }
        GpuPodTenantProvider.Tenant tenant = tenantProvider.getCurrentTenant();
        String token = tokenManager.getAccessToken(tenant.tenantId(), tenant.tenantName());
        if (StringUtils.isBlank(token)) {
            return websocketUrl;
        }
        return UriComponentsBuilder.fromUriString(websocketUrl)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    /**
     * 获取实例监控数据
     */
    public InstanceMonitorResponse getInstanceMonitor(String instanceId, String tenantId, Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/monitor"))
                .queryParam("tenant_id", tenantId);
        if (params != null) {
            params.forEach((key, value) -> {
                if (value != null) {
                    builder.queryParam(key, value);
                }
            });
        }
        String url = builder.buildAndExpand(instanceId).toUriString();
        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<InstanceMonitorResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, InstanceMonitorResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to get instance monitor: {}", e.getResponseBodyAsString(), e);
            InstanceMonitorResponse response = new InstanceMonitorResponse();
            response.setSuccess(false);
            response.setMessage("获取监控数据失败: " + e.getResponseBodyAsString());
            return response;
        } catch (Exception e) {
            log.error("Failed to get instance monitor", e);
            InstanceMonitorResponse response = new InstanceMonitorResponse();
            response.setSuccess(false);
            response.setMessage("获取监控数据失败: " + e.getMessage());
            return response;
        }
    }

    private void performAction(String instanceId, String tenantId, String url, String action) {
        String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            log.info("{} instance: {}", action, instanceId);
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(fullUrl, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to {} instance: {}", action, e.getResponseBodyAsString(), e);
            throw new HttpServiceException(action + "实例失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to {} instance", action, e);
            throw new RuntimeException(action + "实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取实例日志
     */
    public String getInstanceLogs(String instanceId, String tenantId, int tailLines) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/logs"))
                .queryParam("tenant_id", tenantId)
                .queryParam("tailLines", tailLines)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            log.info("Getting logs for instance: {}", instanceId);
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed to get instance logs: {}", e.getResponseBodyAsString(), e);
            return "获取实例日志失败: " + e.getMessage();
        } catch (Exception e) {
            log.error("Failed to get instance logs", e);
            return "获取实例日志失败: " + e.getMessage();
        }
    }

    /**
     * 设置关机计划
     */
    public void setShutdownSchedule(String instanceId, String tenantId, ShutdownScheduleRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/shutdown-schedule"))
                .queryParam("tenant_id", tenantId)
                .buildAndExpand(instanceId)
                .toUriString();
        try {
            log.info("Setting shutdown schedule for instance: {}", instanceId);
            HttpEntity<ShutdownScheduleRequest> entity = new HttpEntity<>(request, createHeaders());
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to set shutdown schedule: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("设置关机计划失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to set shutdown schedule", e);
            throw new RuntimeException("设置关机计划失败: " + e.getMessage());
        }
    }

    // Helper methods to create error responses

    private InstanceResponse createErrorResponse(String message) {
        InstanceResponse response = new InstanceResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    private InstanceListResponse createListErrorResponse(String message) {
        InstanceListResponse response = new InstanceListResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

}
