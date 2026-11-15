# GPU Pod 实例生命周期管理

## 概述

GPU Pod 实例生命周期管理功能允许用户在算力市场平台中创建、管理和释放 GPU Pod 算力资源。该功能对接外部的 GPU Pod 管理服务，提供完整的实例生命周期管理能力。

## 功能需求

## 新增需求

### 需求:
用户可以查看自己的 GPU Pod 实例列表，支持分页、筛选和排序。

#### 场景:
作为已登录用户，我想要查看我的 GPU Pod 实例列表，以便管理我的算力资源。

**输入**：
- 分页参数：page, pageSize
- 筛选条件：status（状态）, region（区域）
- 排序字段：createTime, status 等

**输出**：
- 实例列表，包含以下字段：
  - id: 实例ID
  - name: 实例名称
  - region: 区域
  - zone: 可用区
  - status: 状态（running, stopped, starting, stopping, creating, error, releasing, released）
  - gpuType: GPU类型
  - gpuCount: GPU数量
  - cpuCores: CPU核数
  - memory: 内存大小(GB)
  - systemDisk: 系统盘大小(GB)
  - dataDisk: 数据盘大小(GB)
  - billingType: 计费类型(on_demand, hourly, daily, weekly, monthly, yearly)
  - pricePerHour: 每小时价格
  - createTime: 创建时间
  - expireTime: 到期时间
  - sshInfo: SSH连接信息
  - healthStatus: 健康状态

**业务规则**：
- 只返回当前登录用户的实例
- 状态筛选支持多选
- 默认按创建时间倒序排列

### 需求:
用户可以创建新的 GPU Pod 实例。

#### 场景:
作为已登录用户，我想要创建一个新的 GPU Pod 实例，以便运行我的 AI 训练或推理任务。

**输入**：
- region: 区域（必填）
- zone: 可用区（可选，默认自动分配）
- gpuSpec: GPU规格
  - model: GPU型号（必填）
  - count: GPU数量（必填，默认1）
- image: 镜像地址（必填）
- billing: 计费配置
  - mode: 计费模式（必填，默认on_demand）
  - duration: 购买时长（包周期模式必填）
- resource: 资源配置
  - cpu: CPU核数（可选，默认4）
  - memory: 内存大小（可选，默认16Gi）
  - systemDisk: 系统盘大小（可选，默认30Gi）
  - dataDisk: 数据盘大小（可选，默认50Gi）
- podName: 自定义Pod名称（可选）

**输出**：
- 创建的实例信息

**业务规则**：
- 创建前需校验账户余额是否充足
- 指定区域的GPU资源是否充足
- 镜像地址必须有效且可访问
- 创建成功后的实例初始状态为creating

### 需求:
用户可以启动已停止的 GPU Pod 实例。

#### 场景:
作为实例所有者，我想要启动一个已停止的实例，以便继续使用算力资源。

**输入**：
- instanceId: 实例ID（路径参数）
- region: 区域（可选，从实例详情获取）

**输出**：
- 操作结果

**业务规则**：
- 只有状态为stopped的实例才能启动
- 启动前需校验账户余额（按量计费模式）
- 启动成功后的实例状态变为starting，最终变为running

### 需求:
用户可以停止运行中的 GPU Pod 实例。

#### 场景:
作为实例所有者，我想要停止一个运行中的实例，以节省算力费用。

**输入**：
- instanceId: 实例ID（路径参数）
- region: 区域（可选，从实例详情获取）

**输出**：
- 操作结果

**业务规则**：
- 只有状态为running的实例才能停止
- 停止实例会停止计费（按量计费模式）
- 停止成功后的实例状态变为stopping，最终变为stopped

### 需求:
用户可以重启 GPU Pod 实例。

#### 场景:
作为实例所有者，我想要重启一个实例，以解决可能的软件问题或应用新的配置。

**输入**：
- instanceId: 实例ID（路径参数）
- region: 区域（可选，从实例详情获取）

**输出**：
- 操作结果

**业务规则**：
- 只有状态为running或stopped的实例才能重启
- 重启会重新加载实例配置
- 重启成功后的实例状态先变为restarting，最终变为running

### 需求:
用户可以释放（删除）GPU Pod 实例。

#### 场景:
作为实例所有者，我想要释放一个不再需要的实例，以停止所有相关费用。

**输入**：
- instanceId: 实例ID（路径参数）
- region: 区域（可选，从实例详情获取）

**输出**：
- 操作结果

**业务规则**：
- 释放操作不可逆，实例数据将永久删除
- 释放前需要用户二次确认
- 释放成功后的实例状态变为releasing，最终变为released

## 非功能需求

### 性能需求

- 实例列表查询响应时间 < 1s
- 实例操作（启动/停止/重启）响应时间 < 500ms
- 创建实例接口响应时间 < 3s（异步处理）

### 可用性需求

- 系统可用性 > 99.9%
- 与GPU Pod服务的连接需要实现熔断降级机制
- 支持Token过期自动刷新

### 安全需求

- 所有接口需要用户认证
- 用户只能操作自己的实例
- 敏感信息（SSH密码）需要加密传输和存储
- 操作日志需要记录审计信息

## 接口规范

### 后端Controller接口

```java
@RestController
@RequestMapping("/pc/gpu-pod")
@Tag(name = "PC端-GPU Pod实例管理")
public class GpuPodController {
    
    @GetMapping("/instances")
    @Operation(summary = "获取实例列表")
    public Result<PageResult<GpuPodInstanceVO>> list(InstanceQuery query);
    
    @PostMapping("/instances")
    @Operation(summary = "创建实例")
    public Result<GpuPodInstanceVO> create(@RequestBody CreateInstanceDTO dto);
    
    @GetMapping("/instances/{id}")
    @Operation(summary = "获取实例详情")
    public Result<GpuPodInstanceVO> detail(@PathVariable String id);
    
    @PostMapping("/instances/{id}/start")
    @Operation(summary = "启动实例")
    public Result<Void> start(@PathVariable String id);
    
    @PostMapping("/instances/{id}/stop")
    @Operation(summary = "停止实例")
    public Result<Void> stop(@PathVariable String id);
    
    @PostMapping("/instances/{id}/restart")
    @Operation(summary = "重启实例")
    public Result<Void> restart(@PathVariable String id);
    
    @PostMapping("/instances/{id}/release")
    @Operation(summary = "释放实例")
    public Result<Void> release(@PathVariable String id);
    
    @PostMapping("/instances/{id}/reset-password")
    @Operation(summary = "重置密码")
    public Result<String> resetPassword(@PathVariable String id);
    
    @PutMapping("/instances/{id}/name")
    @Operation(summary = "设置名称")
    public Result<Void> setName(@PathVariable String id, @RequestBody SetNameDTO dto);
    
    @PostMapping("/instances/{id}/shutdown-schedule")
    @Operation(summary = "设置定时关机")
    public Result<Void> setShutdownSchedule(@PathVariable String id, @RequestBody ShutdownScheduleDTO dto);
    
    @GetMapping("/instances/{id}/ssh")
    @Operation(summary = "获取SSH信息")
    public Result<SshInfoVO> getSshInfo(@PathVariable String id);
    
    @GetMapping("/instances/{id}/logs")
    @Operation(summary = "获取Pod日志")
    public Result<String> getLogs(@PathVariable String id, @RequestParam int tailLines);
}
```

## 错误处理

### 错误码定义

| 错误码 | 说明 | 处理建议 |
|-------|------|---------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数是否符合规范 |
| 401 | 未授权 | 检查用户是否登录 |
| 403 | 禁止访问 | 检查用户权限 |
| 404 | 实例不存在 | 检查实例ID是否正确 |
| 409 | 状态冲突 | 实例当前状态不支持该操作 |
| 429 | 请求过于频繁 | 降低请求频率 |
| 500 | 服务器内部错误 | 联系管理员 |
| 503 | GPU Pod服务不可用 | 检查GPU Pod服务状态 |

### 常见错误场景

1. **实例状态不符合操作要求**
   - 启动一个已经在运行中的实例
   - 停止一个已经停止的实例
   - 解决方案：操作前检查实例状态，返回明确的错误信息

2. **Token过期或无效**
   - GPU Pod服务返回401错误
   - 解决方案：自动刷新Token并重试请求

3. **网络连接失败**
   - 无法连接到GPU Pod服务
   - 解决方案：实现熔断降级，返回友好错误提示

## 测试策略

### 单元测试

- Controller层：测试接口参数校验、权限检查、响应格式
- Service层：测试业务逻辑、数据转换、异常处理
- Client层：测试HTTP调用、Token刷新、重试机制

### 集成测试

- 与GPU Pod服务的集成测试
- 端到端的实例生命周期测试
- 并发场景测试

### 性能测试

- 接口响应时间测试
- 并发用户数测试
- 与GPU Pod服务的连接稳定性测试
