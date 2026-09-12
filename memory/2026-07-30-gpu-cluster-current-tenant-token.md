# DEBUG REPORT: GPU 集群调用使用当前登录租户 Token

- **Symptom:** 集群列表调用调度器接口时使用配置文件中的固定 `tenant-id`、`tenant-name`，不同登录客户可能复用同一租户 Token。
- **Root cause:** `GpuSchedulerApiClient.createHeaders()` 调用无参 `GpuPodTokenManager.getAccessToken()`，该方法读取 `GpuPodProperties` 中的固定租户；客户端同时保留了全局 `authKey/access-token` 旁路。
- **Fix:** `GpuSchedulerApiClient` 注入 `GpuPodTenantProvider`，每次请求读取当前登录用户的租户 ID 和名称，并调用 `getAccessToken(tenantId, tenantName)`；集群节点列表 URL 使用 `gpu-pod.admin-auth-key` 拼接管理员接口要求的 `authKey`，请求头仍携带当前租户 Bearer Token；开发配置中的固定租户已注释。
- **Evidence:** Java 17 执行 `mvn -DskipTests compile`，675 个源文件编译成功。
- **Regression test:** `GpuSchedulerApiClientTest#createHeadersUsesCurrentLoggedInTenantToken` 验证当前租户信息被传入 Token 管理器、无参固定租户方法不会被调用且请求头为当前用户 Bearer Token；`clusterNodesUrlIncludesConfiguredAdminAuthKey` 验证节点列表 URL 包含配置的管理员 `authKey`。
- **Related:** `GpuPodTokenManager` 已按 `tenantId` 分离 Token/API Key 缓存，并在租户不存在时先注册再换取 Token。
- **Status:** DONE
