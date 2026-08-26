package com.lingyang.cloud.client;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class GpuSchedulerApiClientTest {

    @Test
    public void createHeadersUsesCurrentLoggedInTenantToken() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient();
        TrackingTokenManager tokenManager = new TrackingTokenManager();
        GpuPodTenantProvider tenantProvider = new GpuPodTenantProvider() {
            @Override
            public Tenant getCurrentTenant() {
                return new Tenant("current-user-id", "当前客户");
            }
        };

        setField(client, "tokenManager", tokenManager);
        setField(client, "tenantProvider", tenantProvider);

        Method createHeaders = GpuSchedulerApiClient.class.getDeclaredMethod("createHeaders");
        createHeaders.setAccessible(true);
        HttpHeaders headers = (HttpHeaders) createHeaders.invoke(client);

        assertEquals("current-user-id", tokenManager.tenantId);
        assertEquals("当前客户", tokenManager.tenantName);
        assertEquals("Bearer current-user-token", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void clusterNodesUrlIncludesConfiguredAdminAuthKey() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient();
        setField(client, "baseUrl", "http://124.174.46.14:8000");
        setField(client, "adminAuthKey", "gpuscheduler-admin-2026");

        Method buildUrl = GpuSchedulerApiClient.class.getDeclaredMethod("buildSchedulerClustersNodesUrl");
        buildUrl.setAccessible(true);

        assertEquals(
                "http://124.174.46.14:8000/api/v1/admin/clusters/nodes?authKey=gpuscheduler-admin-2026",
                buildUrl.invoke(client));
    }

    @Test
    public void clusterSummaryUrlIncludesClusterIdAndConfiguredAdminAuthKey() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient();
        setField(client, "baseUrl", "http://124.174.46.14:8000");
        setField(client, "adminAuthKey", "gpuscheduler-admin-2026");

        Method buildUrl = GpuSchedulerApiClient.class
                .getDeclaredMethod("buildSchedulerClusterSummaryUrl", String.class);
        buildUrl.setAccessible(true);

        assertEquals(
                "http://124.174.46.14:8000/api/v1/admin/clusters/summary"
                        + "?cluster_id=2158294521df08b2b705caf98752961f"
                        + "&authKey=gpuscheduler-admin-2026",
                buildUrl.invoke(client, "2158294521df08b2b705caf98752961f"));
    }

    @Test
    public void monitorOverviewUrlIncludesConfiguredAdminAuthKey() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient();
        setField(client, "baseUrl", "http://124.174.46.14:8000");
        setField(client, "adminAuthKey", "gpuscheduler-admin-2026");

        Method buildUrl = GpuSchedulerApiClient.class.getDeclaredMethod("buildSchedulerMonitorOverviewUrl");
        buildUrl.setAccessible(true);

        assertEquals(
                "http://124.174.46.14:8000/api/v1/admin/monitor/overview?authKey=gpuscheduler-admin-2026",
                buildUrl.invoke(client));
    }

    @Test
    public void monitorOverviewUrlUsesSchedulerAuthKeyFallback() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient();
        setField(client, "baseUrl", "http://124.174.46.14:8000");
        setField(client, "adminAuthKey", "");
        setField(client, "schedulerAuthKey", "gpuscheduler-admin-2026");

        Method buildUrl = GpuSchedulerApiClient.class.getDeclaredMethod("buildSchedulerMonitorOverviewUrl");
        buildUrl.setAccessible(true);

        assertEquals(
                "http://124.174.46.14:8000/api/v1/admin/monitor/overview?authKey=gpuscheduler-admin-2026",
                buildUrl.invoke(client));
    }

    @Test
    public void createAdminHeadersDoesNotRequestTenantToken() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient();
        setField(client, "tokenManager", new TrackingTokenManager());

        Method createHeaders = GpuSchedulerApiClient.class.getDeclaredMethod("createAdminHeaders");
        createHeaders.setAccessible(true);
        HttpHeaders headers = (HttpHeaders) createHeaders.invoke(client);

        assertEquals(null, headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static class TrackingTokenManager extends GpuPodTokenManager {
        private String tenantId;
        private String tenantName;

        @Override
        public String getAccessToken() {
            throw new AssertionError("不应使用配置文件中的固定租户");
        }

        @Override
        public String getAccessToken(String tenantId, String tenantName) {
            this.tenantId = tenantId;
            this.tenantName = tenantName;
            return "current-user-token";
        }
    }
}
