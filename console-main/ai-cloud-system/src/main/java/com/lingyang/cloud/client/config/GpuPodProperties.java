package com.lingyang.cloud.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GPU Pod 服务配置属性
 */
@Component
@ConfigurationProperties(prefix = "gpu-pod")
public class GpuPodProperties {

    /**
     * 基础 URL
     */
    private String baseUrl = "http://124.174.46.14:8000";

    /**
     * API 版本
     */
    private String apiVersion = "v1";

    /**
     * 连接超时（毫秒）
     */
    private int connectTimeout = 10000;

    /**
     * 读取超时（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * 租户 ID（用于换取 JWT Token）
     */
    private String tenantId;

    /**
     * 租户名称（用于换取 JWT Token）
     */
    private String tenantName;

    /**
     * API Key（用于换取 JWT Token）
     */
    private String apiKey;

    /**
     * 管理员密钥（用于创建租户或租户 API Key）
     */
    private String adminAuthKey;

    /**
     * Token 提前过期时间（秒），用于在真正过期前刷新
     */
    private long tokenRefreshAheadSeconds = 300;

    /**
     * 是否模拟创建 GPU Pod，用于本地验证订单流程
     */
    private boolean mockCreateEnabled = false;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAdminAuthKey() {
        return adminAuthKey;
    }

    public void setAdminAuthKey(String adminAuthKey) {
        this.adminAuthKey = adminAuthKey;
    }

    public long getTokenRefreshAheadSeconds() {
        return tokenRefreshAheadSeconds;
    }

    public void setTokenRefreshAheadSeconds(long tokenRefreshAheadSeconds) {
        this.tokenRefreshAheadSeconds = tokenRefreshAheadSeconds;
    }

    public boolean isMockCreateEnabled() {
        return mockCreateEnabled;
    }

    public void setMockCreateEnabled(boolean mockCreateEnabled) {
        this.mockCreateEnabled = mockCreateEnabled;
    }
}
