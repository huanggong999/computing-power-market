# 容器实例（GPU Pod）前后端接口文档

> 本文档描述容器实例列表页（`/#/cloud/instanceList`）及其关联页面前端与后端 `GpuPodController` 的接口契约。  
> **后续新增接口请按文末 [Changelog](#changelog) 格式追加记录，并同步更新接口清单。**

---

## 1. 页面信息

| 项 | 值 |
|---|---|
| 前端路由 | `/#/cloud/instanceList` |
| 入口组件 | `ux-user-main/src/view/Cloud/InstanceList/index.vue` |
| 前端 API 文件 | `ux-user-main/src/api/instance.ts` |
| 前端 Store | `ux-user-main/src/store/modules/useInstance.ts` |
| 后端 Controller | `ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/GpuPodController.java` |
| 后端 Service | `ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuPodServiceImpl.java` |
| 基础路径 | `/pc/gpu-pod` |

---

## 2. 前端组件结构

```
InstanceList/index.vue
├── FilterBar.vue              # 筛选栏（名称/状态/区域/GPU型号/计费模式）
├── BatchActions.vue           # 批量操作栏（开机/关机/重启/释放）
├── InstanceTable.vue          # 实例表格 + 分页
│   └── 行操作：开机/关机/更多
│       └── 更多下拉：重启、重置密码、设置名称、释放实例
├── InstanceDrawer.vue         # 实例详情抽屉（基本信息/资源配置/网络/SSH/快捷操作/监控）
├── MoreActionsDialog.vue      # 更多操作弹窗
│   ├── ResetPasswordDialog.vue
│   └── SetNameDialog.vue
└── MetricsPanel.vue           # 实时监控面板（GPU/CPU/内存/磁盘/网络）
```

---

## 3. 接口清单

### 3.1 已对接接口

| 序号 | 功能 | 前端方法 | 前端调用 URL | 后端方法 | 后端 URL | 状态 |
|---|---|---|---|---|---|---|
| 1 | 获取实例列表 | `getInstanceList` | `GET /pc/gpu-pod/instances` | `listInstances` | `GET /pc/gpu-pod/instances` | ✅ 已对接 |
| 2 | 获取实例详情 | `getInstanceDetail` | `GET /pc/gpu-pod/instances/{id}` | `detail` | `GET /pc/gpu-pod/instances/{id}` | ✅ 已对接 |
| 3 | 创建实例 | — | — | `create` | `POST /pc/gpu-pod/instances` | ⚠️ 前端未实现页面 |
| 4 | 开机 | `startInstance` | `POST /pc/gpu-pod/instances/{id}/start` | `start` | `POST /pc/gpu-pod/instances/{id}/start` | ✅ 已对接 |
| 5 | 关机 | `stopInstance` | `POST /pc/gpu-pod/instances/{id}/stop` | `stop` | `POST /pc/gpu-pod/instances/{id}/stop` | ✅ 已对接 |
| 6 | 重启 | `restartInstance` | `POST /pc/gpu-pod/instances/{id}/restart` | `restart` | `POST /pc/gpu-pod/instances/{id}/restart` | ✅ 已对接 |
| 7 | 释放 | `releaseInstance` | `POST /pc/gpu-pod/instances/{id}/release` | `release` | `POST /pc/gpu-pod/instances/{id}/release` | ✅ 已对接 |
| 8 | 重置密码 | `resetPassword` | `POST /pc/gpu-pod/instances/{id}/reset-password` | `resetPassword` | `POST /pc/gpu-pod/instances/{id}/reset-password` | ✅ 已对接 |
| 9 | 设置名称 | `setInstanceName` | `PUT /pc/gpu-pod/instances/{id}/name` | `setName` | `PUT /pc/gpu-pod/instances/{id}/name` | ✅ 已对接 |
| 10 | 批量开机 | `batchStartInstances` | 前端循环调用单实例接口 | — | — | ✅ 已对接（前端聚合） |
| 11 | 批量关机 | `batchStopInstances` | 前端循环调用单实例接口 | — | — | ✅ 已对接（前端聚合） |
| 12 | 批量重启 | `batchRestartInstances` | 前端循环调用单实例接口 | — | — | ✅ 已对接（前端聚合） |
| 13 | 批量释放 | `batchReleaseInstances` | 前端循环调用单实例接口 | — | — | ✅ 已对接（前端聚合） |

### 3.2 后端已提供、前端未对接接口

| 序号 | 功能 | 后端方法 | 后端 URL | 备注 |
|---|---|---|---|---|
| 14 | 设置定时关机 | `setShutdownSchedule` | `POST /pc/gpu-pod/instances/{id}/shutdown-schedule` | 前端无 UI |
| 15 | 获取定时关机状态 | `getShutdownSchedule` | `GET /pc/gpu-pod/instances/{id}/shutdown-schedule` | 前端无 UI |
| 16 | 获取 SSH 连接信息 | `getSshInfo` | `GET /pc/gpu-pod/instances/{id}/ssh` | 前端详情抽屉直接展示 `instance` 字段 |
| 17 | 获取实例日志 | `getLogs` | `GET /pc/gpu-pod/instances/{id}/logs?tailLines={n}` | 前端无 UI |

### 3.3 已移除（后端不存在）

| 原前端功能 | 原前端调用 | 移除原因 |
|---|---|---|
| 更换镜像 | `POST /pc/instance/{id}/change-image` | 后端 `GpuPodController` 未提供对应接口 |
| 升降配 | `POST /pc/instance/{id}/upgrade` | 后端 `GpuPodController` 未提供对应接口 |
| 扩容磁盘 | `POST /pc/instance/{id}/expand-disk` | 后端 `GpuPodController` 未提供对应接口 |

---

## 4. 接口详情

### 4.1 获取实例列表

- **前端方法**：`getInstanceList(params)`
- **请求方式**：`GET`
- **请求 URL**：`/pc/gpu-pod/instances`
- **后端方法**：`GpuPodController.listInstances`

**Query 参数**：

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `pageNo` | int | 否 | 当前页，默认 1 |
| `pageSize` | int | 否 | 每页数量，默认 10 |
| `name` | string | 否 | 实例名称（模糊查询） |
| `status` | string | 否 | 状态 |
| `regionCode` | string | 否 | 区域编码 |
| `gpuModel` | string | 否 | GPU 型号 |
| `billingMode` | string | 否 | 计费模式 |

**前端参数映射**：

| 前端 filters 字段 | 映射后 Query 参数 | 说明 |
|---|---|---|
| `keyword` | `name` | 关键词模糊查询 |
| `regions[0]` | `regionCode` | 取第一个选中区域 |
| `gpuTypes[0]` | `gpuModel` | 取第一个选中 GPU 型号 |
| `statuses[0]` | `status` | 取第一个选中状态 |

**响应结构**：

```json
{
  "code": 200,
  "data": {
    "list": [GpuPodInstanceVO],
    "dataTotal": 100,
    "pageNo": 1,
    "pageSize": 10,
    "pageTotal": 10
  },
  "message": "success"
}
```

---

### 4.2 获取实例详情

- **前端方法**：`getInstanceDetail(id)`
- **请求方式**：`GET`
- **请求 URL**：`/pc/gpu-pod/instances/{id}`
- **后端方法**：`GpuPodController.detail`

**Path 参数**：

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `id` | string | 是 | 实例 ID |

**响应**：`Result<GpuPodInstanceVO>`

---

### 4.3 创建实例

- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances`
- **后端方法**：`GpuPodController.create`

**请求体**：`CreateGpuPodDTO`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `region` | string | 是 | 区域编码 |
| `zone` | string | 否 | 可用区编码 |
| `gpuSpec.model` | string | 是 | GPU 型号 |
| `gpuSpec.count` | int | 否 | GPU 数量，默认 1 |
| `image` | string | 是 | 镜像地址 |
| `billing.mode` | string | 是 | 计费模式 |
| `billing.duration` | int | 否 | 购买时长 |
| `resource.cpu` | string | 否 | CPU 核数 |
| `resource.memory` | string | 否 | 内存大小 |
| `resource.systemDisk` | string | 否 | 系统盘大小 |
| `resource.dataDisk` | string | 否 | 数据盘大小 |
| `podName` | string | 否 | 自定义 Pod 名称 |

**响应**：`Result<GpuPodInstanceVO>`

> ⚠️ 前端目前未实现创建实例页面（只有入口路由 `/cloud/createInstance`）。

---

### 4.4 开机

- **前端方法**：`startInstance(id)`
- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/start`
- **后端方法**：`GpuPodController.start`

**Path 参数**：

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `id` | string | 是 | 实例 ID |

**响应**：`Result<Void>`

---

### 4.5 关机

- **前端方法**：`stopInstance(id)`
- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/stop`
- **后端方法**：`GpuPodController.stop`

**Path 参数**：`id`

**响应**：`Result<Void>`

---

### 4.6 重启

- **前端方法**：`restartInstance(id)`
- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/restart`
- **后端方法**：`GpuPodController.restart`

**Path 参数**：`id`

**响应**：`Result<Void>`

---

### 4.7 释放

- **前端方法**：`releaseInstance(id)`
- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/release`
- **后端方法**：`GpuPodController.release`

**Path 参数**：`id`

**响应**：`Result<Void>`

> ⚠️ 释放为不可逆操作，前端会弹窗二次确认。

---

### 4.8 重置密码

- **前端方法**：`resetPassword(id, newPassword)`
- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/reset-password`
- **后端方法**：`GpuPodController.resetPassword`

**Path 参数**：`id`

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `newPassword` | string | 是 | 新密码（前端传入，后端当前实现不消费该字段，直接返回系统密码） |

**响应**：`Result<String>`（重置后的密码）

> ⚠️ 当前后端实现是从实例详情中读取现有密码返回，未真正执行密码重置逻辑。

---

### 4.9 设置实例名称

- **前端方法**：`setInstanceName(id, name)`
- **请求方式**：`PUT`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/name`
- **后端方法**：`GpuPodController.setName`

**Path 参数**：`id`

**请求体**：`SetNameDTO`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 实例名称 |

**响应**：`Result<Void>`

---

### 4.10 设置定时关机

- **请求方式**：`POST`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/shutdown-schedule`
- **后端方法**：`GpuPodController.setShutdownSchedule`

**Path 参数**：`id`

**请求体**：`SetShutdownScheduleDTO`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `shutdownTime` | string | 否 | 关机时间（ISO 8601 格式，如 `2026-05-27T22:00:00`），传 `null` 表示取消 |

**响应**：`Result<Void>`

> ⚠️ 前端目前无 UI 入口。

---

### 4.11 获取定时关机状态

- **请求方式**：`GET`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/shutdown-schedule`
- **后端方法**：`GpuPodController.getShutdownSchedule`

**Path 参数**：`id`

**响应**：`Result<ShutdownScheduleVO>`

```json
{
  "hasSchedule": true,
  "shutdownTime": "2026-05-27T22:00:00",
  "remainingTime": 3600
}
```

> ⚠️ 前端目前无 UI 入口。

---

### 4.12 获取 SSH 连接信息

- **请求方式**：`GET`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/ssh`
- **后端方法**：`GpuPodController.getSshInfo`

**Path 参数**：`id`

**响应**：`Result<SshInfoVO>`

```json
{
  "host": "192.168.1.1",
  "port": 22,
  "username": "root",
  "password": "******",
  "command": "ssh root@192.168.1.1 -p 22"
}
```

> 前端详情抽屉目前直接展示 `Instance` 类型中的 `sshCommand` / `rootPassword` / `sshPort` 字段，未单独调用该接口。

---

### 4.13 获取实例日志

- **请求方式**：`GET`
- **请求 URL**：`/pc/gpu-pod/instances/{id}/logs?tailLines={n}`
- **后端方法**：`GpuPodController.getLogs`

**Path 参数**：`id`

**Query 参数**：

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `tailLines` | int | 否 | 返回最后 N 行日志，默认 100 |

**响应**：`Result<String>`（日志内容）

> ⚠️ 前端目前无 UI 入口。

---

## 5. 类型定义

### 5.1 前端 `Instance` 类型

```typescript
interface Instance {
  id: string
  uuid: string
  name: string
  description?: string
  status: InstanceStatus
  statusMessage?: string
  gpuType: string
  gpuCount: number
  gpuMemory: number
  cpuCores: number
  cpuModel?: string
  memory: number
  systemDisk: number
  dataDisk: number
  systemDiskSize?: number
  dataDiskSize?: number
  region: string
  regionCode: string
  privateIp?: string
  publicIp?: string
  sshPort?: number
  sshCommand?: string
  rootPassword?: string
  jupyterUrl?: string
  createdAt: string
  expiredAt?: string
  startedAt?: string
  stoppedAt?: string
  billingType: 'hourly' | 'monthly'
  pricePerHour: number
}
```

### 5.2 后端 `GpuPodInstanceVO`

```java
@Data
public class GpuPodInstanceVO {
    private String id;
    private String name;
    private String region;
    private String regionName;
    private String zone;
    private String zoneName;
    private String status;
    private String statusText;
    private String gpuType;
    private Integer gpuCount;
    private String gpuMemory;
    private Integer cpuCores;
    private String memory;
    private Integer systemDisk;
    private Integer dataDisk;
    private String imageUrl;
    private String billingType;
    private String billingTypeText;
    private BigDecimal pricePerHour;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private LocalDateTime expireTime;
    private LocalDateTime scheduledShutdownTime;
    private SshInfoVO sshInfo;
    private HealthStatus healthStatus;
}
```

### 5.3 状态枚举

| 状态值 | 中文 | 前端显示 |
|---|---|---|
| `creating` | 创建中 | 创建中 |
| `running` | 运行中 | 运行中 |
| `stopped` | 已关机 | 已关机 |
| `starting` | 开机中 | 开机中 |
| `stopping` | 关机中 | 关机中 |
| `restarting` | 重启中 | 重启中 |
| `releasing` | 释放中 | 释放中 |
| `released` | 已释放 | 已释放 |
| `expired` | 已过期 | 已过期 |
| `error` | 错误 | 错误 |

---

## 6. 通用响应结构

后端统一返回 `Result<T>`：

```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```

分页结果使用 `PageResult<T>`：

```json
{
  "list": [ ... ],
  "dataTotal": 100,
  "pageNo": 1,
  "pageSize": 10,
  "pageTotal": 10
}
```

---

## 7. 批量操作说明

后端 **未提供批量接口**。前端通过 `Promise.all` 循环调用单实例接口实现批量操作：

```typescript
// api/instance.ts
export function batchStartInstances(ids: string[]) {
  return Promise.all(ids.map(id => startInstance(id)))
}
```

- 任一实例操作失败会导致整个 `Promise.all` reject
- 前端错误处理统一走 `catch`，显示"批量操作失败"

---

## 8. Changelog

### 2026-06-04 初版对齐

- 对接 `GpuPodController` 全部单实例操作接口（列表/详情/开机/关机/重启/释放/重置密码/设置名称）
- 批量操作改为前端循环调用单实例接口
- 移除后端不存在的操作：更换镜像、升降配、扩容磁盘
- 移除已废弃对话框组件：`ChangeImageDialog.vue`、`UpgradeDialog.vue`、`ExpandDiskDialog.vue`
- 保留后端有但前端未对接的接口记录：定时关机、SSH 信息、实例日志

---

## 9. 待办（TODO）

| 序号 | 事项 | 优先级 |
|---|---|---|
| 1 | 前端实现创建实例页面（`/cloud/createInstance`） | 高 |
| 2 | 前端对接定时关机 UI（设置/查看/取消） | 中 |
| 3 | 前端对接实例日志查看 UI | 中 |
| 4 | 后端 `resetPassword` 实现真正的密码重置逻辑（当前仅返回现有密码） | 中 |
| 5 | 评估是否需要后端提供真正的批量操作接口 | 低 |
