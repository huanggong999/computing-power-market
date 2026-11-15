# GPU Pod 定时关机功能

## 概述

GPU Pod 定时关机功能允许用户为 GPU Pod 实例设置定时关机任务。用户可以在指定的时间点自动关闭实例，避免忘记关机导致的资源浪费和不必要的费用支出。

## 功能需求

## 新增需求

### 需求:
用户可以为指定的 GPU Pod 实例设置定时关机任务。

#### 场景:
作为实例所有者，我想要为我的 GPU Pod 实例设置定时关机任务，以便在指定时间自动关闭实例，避免资源浪费。

**输入**：
- instanceId: 实例ID（路径参数）
- shutdownTime: 关机时间（ISO 8601格式，如：2026-05-27T22:00:00）

**输出**：
- 操作结果

**业务规则**：
- 只能为自己创建的实例设置定时关机
- 实例状态必须为 running 才能设置定时关机
- 关机时间必须晚于当前时间，且至少提前5分钟
- 每个实例同时只能有一个有效的定时关机任务
- 设置新的定时关机会自动覆盖已存在的定时关机任务

### 需求:
用户可以取消已设置的定时关机任务。

#### 场景:
作为实例所有者，我想要取消之前设置的定时关机任务，以便继续使用实例。

**输入**：
- instanceId: 实例ID（路径参数）
- shutdownTime: null 或不传（表示取消）

**输出**：
- 操作结果

**业务规则**：
- 只能取消自己创建的实例的定时关机任务
- 取消一个不存在的定时关机任务返回成功（幂等性）

### 需求:
用户可以查询指定实例的定时关机任务状态。

#### 场景:
作为实例所有者，我想要查询我的实例是否设置了定时关机任务，以便了解自动关机的时间安排。

**输入**：
- instanceId: 实例ID

**输出**：
- 定时关机任务信息：
  - hasSchedule: 是否有定时关机任务
  - shutdownTime: 关机时间（如果有）
  - remainingTime: 剩余时间（秒，可选）

**业务规则**：
- 只能查询自己创建的实例的定时关机状态

## 接口规范

### 后端接口

```java
@PostMapping("/instances/{id}/shutdown-schedule")
@Operation(summary = "设置/取消定时关机")
public Result<Void> setShutdownSchedule(
    @PathVariable String id,
    @RequestBody ShutdownScheduleDTO dto);

@GetMapping("/instances/{id}/shutdown-schedule")
@Operation(summary = "查询定时关机状态")
public Result<ShutdownScheduleVO> getShutdownSchedule(@PathVariable String id);
```

### 前端 API

```typescript
// 设置/取消定时关机
export function setShutdownSchedule(
  id: string, 
  shutdownTime: string | null
) {
  return request.post(`/pc/gpu-pod/instances/${id}/shutdown-schedule`, {
    shutdownTime
  })
}

// 查询定时关机状态
export function getShutdownSchedule(id: string) {
  return request.get(`/pc/gpu-pod/instances/${id}/shutdown-schedule`)
}
```

### 请求/响应示例

#### 设置定时关机

**请求**：
```
POST /pc/gpu-pod/instances/a1b2c3d4e5f6/shutdown-schedule
Content-Type: application/json

{
  "shutdownTime": "2026-05-27T22:00:00"
}
```

**响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

#### 取消定时关机

**请求**：
```
POST /pc/gpu-pod/instances/a1b2c3d4e5f6/shutdown-schedule
Content-Type: application/json

{
  "shutdownTime": null
}
```

#### 查询定时关机状态

**请求**：
```
GET /pc/gpu-pod/instances/a1b2c3d4e5f6/shutdown-schedule
```

**响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "hasSchedule": true,
    "shutdownTime": "2026-05-27T22:00:00",
    "remainingTime": 7200
  }
}
```

## 前端交互设计

### 定时关机设置对话框

- **触发方式**：在"更多操作"菜单中点击"设置定时关机"
- **展示内容**：
  - 当前定时关机状态（如果有）
  - 日期时间选择器（可选）
  - 倒计时显示（如果已设置）
  - "立即取消"按钮（如果已设置）
- **操作按钮**：
  - "设置" - 保存定时关机任务
  - "取消" - 关闭对话框

### 使用流程

#### 设置定时关机

1. 用户在实例列表中找到目标实例
2. 点击"更多"按钮，选择"设置定时关机"
3. 弹出定时关机设置对话框
4. 用户在日期时间选择器中选择关机时间
5. 点击"设置"按钮保存
6. 系统提示设置成功，对话框关闭
7. 实例列表中该实例显示定时关机倒计时

#### 取消定时关机

1. 用户进入定时关机设置对话框
2. 如果已设置定时关机，显示当前设置的关机时间
3. 用户点击"立即取消"按钮
4. 系统确认是否取消
5. 确认后取消定时关机任务
6. 系统提示取消成功

## 技术实现要点

### 定时任务管理

由于 GPU Pod 服务本身负责实际的定时关机执行，后端主要做以下工作：

1. **请求转发**：将用户设置的定时关机请求转发给 GPU Pod 服务
2. **状态查询**：查询 GPU Pod 服务获取当前定时关机状态
3. **取消任务**：向 GPU Pod 服务发送取消定时关机请求

### 时间处理

1. **时区处理**：前端展示使用用户本地时区，与GPU Pod服务通信使用UTC时间
2. **时间校验**：
   - 关机时间必须晚于当前时间
   - 关机时间至少提前5分钟
   - 最大支持设置7天内的定时关机

### 前端倒计时

1. **实时更新**：使用 setInterval 每秒更新倒计时显示
2. **自动刷新**：倒计时结束时自动刷新实例列表
3. **后台运行**：即使对话框关闭，实例列表上也显示倒计时

## 安全考虑

1. **权限控制**：只能操作自己的实例的定时关机
2. **时间限制**：不允许设置过去的时间或太近的时间
3. **频率限制**：限制设置/取消定时关机的频率，防止滥用
4. **操作审计**：记录定时关机的设置、修改、取消操作

## 错误处理

| 错误场景 | 错误码 | 错误信息 | 处理建议 |
|---------|--------|---------|---------|
| 时间格式错误 | 400 | 时间格式不正确 | 使用正确的ISO 8601格式 |
| 时间已过 | 400 | 关机时间不能早于当前时间 | 选择未来的时间 |
| 时间太近 | 400 | 关机时间必须至少提前5分钟 | 选择更晚的时间 |
| 时间太远 | 400 | 关机时间不能超过7天 | 选择7天内的时间 |
| 实例不存在 | 404 | 实例不存在 | 检查实例ID |
| 无权操作 | 403 | 无权操作此实例 | 只能操作自己的实例 |
| 实例未运行 | 409 | 实例未运行 | 只有运行中的实例才能设置定时关机 |
| GPU Pod服务不可用 | 503 | 服务暂时不可用 | 稍后重试 |

## 依赖关系

本功能依赖于以下功能：
- `gpu-pod-instance-management`: 需要先完成实例管理功能，才能获取实例ID设置定时关机
