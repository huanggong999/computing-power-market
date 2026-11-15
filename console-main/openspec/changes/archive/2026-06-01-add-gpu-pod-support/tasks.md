# 实现任务列表：GPU Pod 实例管理集成

## 后端任务 (ai-cloud-system)

### 1. 配置与客户端层

- [ ] **TASK-001**: 添加 GPU Pod 服务配置
  - 在 `application.yml` 中添加 `gpu-pod` 配置项（base-url、jwt-secret-key、api-key、tenant-id、tenant-name）
  - 创建 `GpuPodProperties.java` 配置类
  - 预估时间：0.5小时

- [ ] **TASK-002**: 实现 GPU Pod HTTP 客户端
  - 创建 `GpuPodClient.java`，封装对 GPU Pod 服务的 HTTP 调用
  - 实现 JWT Token 获取和缓存机制
  - 实现通用请求方法（GET/POST/PUT/DELETE）
  - 预估时间：4小时

### 2. Service 层

- [ ] **TASK-003**: 实现实例生命周期管理 Service
  - 创建 `GpuPodService.java` 接口和 `GpuPodServiceImpl.java` 实现
  - 实现方法：list/create/detail/start/stop/restart/release
  - 实现 GPU Pod 数据格式到前端 VO 的转换
  - 预估时间：6小时

- [ ] **TASK-004**: 实现更多操作 Service 方法
  - 在 `GpuPodService.java` 中新增方法：
    - resetPassword
    - setName
    - setShutdownSchedule
    - getSshInfo
    - getLogs
  - 预估时间：4小时

### 3. Controller 层

- [ ] **TASK-005**: 实现 GPU Pod 实例管理 Controller
  - 创建 `GpuPodController.java`
  - 实现接口：list/create/detail/start/stop/restart/release
  - 添加 Swagger 注解
  - 实现参数校验
  - 预估时间：4小时

- [ ] **TASK-006**: 实现更多操作 Controller 方法
  - 在 `GpuPodController.java` 中新增接口：
    - resetPassword
    - setName
    - setShutdownSchedule
    - getSshInfo
    - getLogs
  - 预估时间：3小时

### 4. DTO/VO 层

- [ ] **TASK-007**: 创建 DTO 和 VO 类
  - 创建包 `com.lingyang.cloud.model.dto.gpupod`
    - CreateInstanceDTO.java
    - SetNameDTO.java
    - ShutdownScheduleDTO.java
    - ResetPasswordDTO.java
  - 创建包 `com.lingyang.cloud.model.vo.gpupod`
    - GpuPodInstanceVO.java
    - GpuPodInstanceDetailVO.java
    - SshInfoVO.java
  - 预估时间：3小时

### 5. 测试与优化

- [ ] **TASK-008**: 编写单元测试
  - 为 GpuPodClient 编写测试
  - 为 GpuPodService 编写测试
  - 使用 Mockito 模拟外部依赖
  - 预估时间：4小时

- [ ] **TASK-009**: 集成测试与优化
  - 与前端联调测试
  - 性能测试（响应时间、并发）
  - 错误场景测试（网络异常、服务不可用等）
  - 预估时间：4小时

---

## 前端任务 (ux-user-main)

### 1. API 层

- [ ] **TASK-F001**: 更新 instance API
  - 修改 `src/api/instance.ts`
  - 新增/修改 GPU Pod 相关 API 调用
  - 调整请求路径和参数以适应新的后端接口
  - 预估时间：2小时

### 2. Store 层

- [ ] **TASK-F002**: 更新 useInstance store
  - 修改 `src/store/modules/useInstance.ts`
  - 调整 fetchInstanceList 方法调用新的 API
  - 确保数据结构和后端返回一致
  - 预估时间：2小时

### 3. 组件调整

- [ ] **TASK-F003**: 调整"更多操作"菜单
  - 修改 `src/view/Cloud/InstanceList/components/MoreActionsDialog.vue`
  - **移除以下菜单项**：
    - 更换镜像
    - 升降配
    - 扩容磁盘
  - **保留以下菜单项**：
    - 重启
    - 重置密码
    - 设置名称
    - 释放实例
  - **新增菜单项**：
    - 查看SSH信息
    - 查看Pod日志
    - 设置定时关机
  - 预估时间：3小时

### 4. 新增对话框组件

- [ ] **TASK-F004**: 实现 SSH 信息对话框
  - 创建 `src/view/Cloud/InstanceList/components/SshInfoDialog.vue`
  - 展示内容：
    - 主机地址（可复制）
    - 端口号
    - 用户名
    - 密码（遮罩显示，可切换明文/密文）
    - SSH 连接命令（一键复制）
  - 添加安全提示信息
  - 预估时间：3小时

- [ ] **TASK-F005**: 实现 Pod 日志对话框
  - 创建 `src/view/Cloud/InstanceList/components/PodLogsDialog.vue`
  - 展示内容：
    - 日志内容区域（等宽字体，深色背景，类似终端风格）
    - 行数选择器（50/100/200/500行）
    - 刷新按钮
    - 下载日志按钮
  - 实现日志自动滚动到底部
  - 预估时间：4小时

- [ ] **TASK-F006**: 实现定时关机对话框
  - 创建 `src/view/Cloud/InstanceList/components/ShutdownScheduleDialog.vue`
  - 展示内容：
    - 当前定时关机状态（如果有）
    - 日期时间选择器
    - 倒计时显示（如果已设置）
    - "立即取消"按钮（如果已设置）
  - 时间校验逻辑（必须晚于当前时间5分钟以上）
  - 预估时间：3小时

### 5. 页面集成

- [ ] **TASK-F007**: 更新 InstanceList 页面
  - 修改 `src/view/Cloud/InstanceList/index.vue`
  - 在适当位置引入新的对话框组件
  - 注册组件和处理事件
  - 确保与现有功能的兼容性
  - 预估时间：2小时

### 6. 测试与优化

- [ ] **TASK-F008**: 前端功能测试
  - 测试"更多操作"菜单的调整
  - 测试新增的三个对话框功能
  - 测试与后端的接口对接
  - 测试边界条件（实例状态、权限等）
  - 测试响应式布局
  - 预估时间：4小时

---

## 总计预估时间

### 后端任务：约 44 小时
- 配置与客户端层：4.5 小时
- Service 层：10 小时
- Controller 层：7 小时
- DTO/VO 层：3 小时
- 测试与优化：8 小时

### 前端任务：约 30 小时
- API 层：2 小时
- Store 层：2 小时
- 组件调整与新增：19 小时
- 页面集成与测试：7 小时

**总计：约 74 小时（约 9-10 人天）**

---

## 风险与建议

1. **GPU Pod 服务可用性**：GPU Pod 服务是外部依赖，可能存在不稳定的情况。建议实现熔断降级机制，在服务不可用时给出友好的错误提示。

2. **JWT Token 管理**：Token 过期可能导致请求失败。建议实现自动刷新机制，确保用户体验。

3. **前端状态同步**：实例状态可能在用户操作过程中发生变化。建议实现定时刷新机制，及时更新实例状态。

4. **大日志处理**：日志内容可能很大，需要考虑前端渲染性能。建议限制最大行数，并提供下载功能。
