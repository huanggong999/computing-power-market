## 为什么

当前算力市场平台的容器实例列表页面（`/cloud/instanceList`）仅支持对接火山引擎云的 ECS 实例。为了支持 GPU Pod 算力资源的管理，需要对接外部的 GPU Pod 管理服务（地址：124.174.46.14:8000）。该服务提供了完整的 GPU Pod 生命周期管理接口，包括创建、启动、停止、重启、释放实例，以及 SSH 信息查看、日志查看、定时关机等高级功能。

由于架构约束，前端 `ux-user-main` 不能直接调用 GPU Pod 服务，必须通过 `ai-cloud-system` 中转。因此需要新增一套完整的 Controller 和 Service 层来对接 GPU Pod 接口，并调整前端"更多操作"菜单，移除不支持的功能，新增 GPU Pod 特有的功能。

## 变更内容

### 后端变更（ai-cloud-system）

**新增文件**：
- `GpuPodController.java` - REST API 控制器，提供 `/pc/gpu-pod/**` 接口
- `GpuPodService.java` / `GpuPodServiceImpl.java` - 业务逻辑层
- `GpuPodClient.java` - HTTP 客户端，调用 GPU Pod 服务
- `GpuPodProperties.java` - 配置属性（服务地址、JWT密钥等）
- 相关的 DTO/VO 类

**功能实现**：
- 实例列表查询（对接 `GET /api/v1/instances`）
- 实例创建（对接 `POST /api/v1/gpu/pod/create`）
- 实例启动/停止/重启/释放（对接对应 POST 接口）
- 重置密码（对接 `POST /api/v1/instances/{id}/reset-password`）
- 设置名称（对接 `PUT /api/v1/instances/{id}/name`）
- 定时关机（对接 `POST /api/v1/instances/{id}/shutdown-schedule`）
- 查看SSH信息（对接 `GET /api/v1/instances/{id}/ssh`）
- 查看Pod日志（对接 `GET /api/v1/gpu/pod/{pod_name}/logs`）

### 前端变更（ux-user-main）

**移除的"更多操作"菜单项**（GPU Pod不支持）：
- 更换镜像
- 升降配
- 扩容磁盘

**新增的"更多操作"菜单项**（GPU Pod支持）：
- 查看SSH信息
- 查看Pod日志
- 设置定时关机

**新增/修改的文件**：
- `MoreActionsDialog.vue` - 调整菜单项
- `SshInfoDialog.vue` - 新增：SSH信息展示对话框
- `PodLogsDialog.vue` - 新增：Pod日志查看对话框
- `ShutdownScheduleDialog.vue` - 新增：定时关机设置对话框
- `instance.ts` - API接口调整

## 功能 (Capabilities)

### 新增功能

- `gpu-pod-instance-management`: GPU Pod 实例生命周期管理，包括创建、启动、停止、重启、释放实例，以及实例列表查询、详情查看等功能
- `gpu-pod-ssh-access`: GPU Pod SSH 访问管理，提供 SSH 连接信息查看功能
- `gpu-pod-log-viewer`: GPU Pod 日志查看功能，支持实时查看 Pod 日志
- `gpu-pod-scheduled-shutdown`: GPU Pod 定时关机功能，支持设置/取消定时关机任务

### 修改功能

- 无现有功能需求变更，本变更主要为新增 GPU Pod 支持能力

## 影响

### 受影响的系统/模块

1. **ai-cloud-system** (Java后端)
   - 新增 Controller 层：`GpuPodController`
   - 新增 Service 层：`GpuPodService` / `GpuPodServiceImpl`
   - 新增 Client 层：`GpuPodClient` (HTTP调用GPU Pod服务)
   - 新增配置：`application.yml` 中增加 GPU Pod 服务配置

2. **ux-user-main** (Vue前端)
   - 修改：`MoreActionsDialog.vue` (调整"更多操作"菜单)
   - 新增：`SshInfoDialog.vue` (SSH信息展示)
   - 新增：`PodLogsDialog.vue` (Pod日志查看)
   - 新增：`ShutdownScheduleDialog.vue` (定时关机)
   - 修改：`instance.ts` (API接口)

### 依赖的外部服务

- **GPU Pod 服务** (`124.174.46.14:8000`)
  - RESTful API 风格
  - JWT Bearer Token 认证
  - 需要提供 tenant_id、api_key 进行认证

### 配置变更

需在 `ai-cloud-system` 的 `application.yml` 中新增配置：

```yaml
gpu-pod:
  service:
    base-url: http://124.174.46.14:8000
    jwt-secret-key: ${GPU_POD_JWT_SECRET:gpu-scheduler-jwt-secret-key-change-me-in-production}
    api-key: ${GPU_POD_API_KEY:demo-api-key-2024}
    tenant-id: ${GPU_POD_TENANT_ID:tenant_001}
    tenant-name: ${GPU_POD_TENANT_NAME:AI科技公司}
```

### 风险评估

| 风险项 | 等级 | 说明 | 缓解措施 |
|-------|------|------|---------|
| GPU Pod服务不可用 | 高 | 外部依赖服务故障 | 实现熔断降级，返回友好错误提示 |
| JWT认证失败 | 中 | 密钥配置错误或过期 | 配置监控告警，定期检查 |
| 网络延迟 | 中 | 跨服务调用延迟 | 设置合理超时时间，异步处理 |
| 数据不一致 | 低 | 两端状态不同步 | 定期同步，提供刷新功能 |

### 回滚计划

1. 前端：保留原火山引擎 ECS 实例列表页面，GPU Pod 实例列表作为独立页面或切换开关
2. 后端：`GpuPodController` 独立部署，不影响原有 `PcConsoleController`
3. 数据库：GPU Pod 实例信息不存储本地，全部从 GPU Pod 服务实时查询

---

**变更准备完毕，等待评审后开始实现。**
