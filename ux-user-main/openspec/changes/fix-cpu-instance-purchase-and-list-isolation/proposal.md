## Why

独立 CPU 实例购买页目前只做了部分字段校验，并把耗时的云主机创建过程绑定在一次同步请求上，用户会长时间停留在等待页面，创建失败也缺少稳定的状态反馈。同时，CPU 实例 Tab 的布局与 GPU 实例 Tab 不一致，实例来源隔离不足可能导致 GPU 列表展示 CPU 实例并误调用 GPU 释放路径。

## What Changes

- 为 CPU 购买表单增加前端基本校验：地域、可用区、纯 CPU 规格、计费方式、时长、系统盘、数据盘和购买数量；沿用系统盘 `20–2048 GiB`、数据盘 `0–8192 GiB`、购买数量 `1–10`、包月/包年时长 `1–36` 的范围。
- 基本校验通过后显示“提交中”；后端持久化受理后立即返回可查询凭据并显示“创建中”，不阻塞页面等待云端创建完成，支持离页和刷新恢复、幂等提交及部分成功反馈。
- 统一 CPU 创建状态展示，至少覆盖创建中、运行中、已停止、失败/异常，并展示服务端返回的失败原因。
- 将 CPU 实例 Tab 调整为与 GPU 实例 Tab 一致的页面结构、统计卡片、筛选区、表格密度、空状态、分页和详情展示方式。
- 从 `/#/cloud/gpuInstance` 页面移除“创建实例”“购买 CPU 实例”等购买入口；CPU 购买入口只保留在算力市场页面。
- 对 GPU 和 CPU 列表、详情及释放操作增加来源隔离：GPU 列表只展示 GPU 实例，CPU 列表只展示 `origin=cpu_instance` 实例，释放前再次校验实例来源。

## Capabilities

### New Capabilities

- `cpu-purchase-validation-and-async-creation`: CPU 购买参数校验、创建中状态、异步结果和失败反馈。
- `cpu-instance-list-experience`: CPU 实例列表与 GPU 列表统一的布局、筛选、状态和详情体验。
- `gpu-cpu-instance-source-isolation`: GPU/CPU 列表过滤、来源标识和生命周期操作隔离。

### Modified Capabilities

无。

## Impact

- 用户端 `ux-user-main/src/view/computeList/new.vue`、`src/view/Cloud/InstanceList/index.vue`、CPU 表格和详情组件、实例类型与 API 封装。
- 门户后端 GPU 实例列表 DTO、转换逻辑和释放前来源校验；CPU 适配接口保持租户隔离并透传上游状态字段。
- 本提案的文档归属 `ux-user-main`，`console-main` 的持久化创建任务、状态查询、幂等受理和来源保护是配套后端交付依赖；原上游 CPU 接口仅定义同步结果，不能假定已经提供异步受理能力。
- 原变更 `gpu-instance-resource-resize-and-cpu-instance` 尚未归档，其文档保持不动；本变更补充和修正其 CPU 用户体验与隔离边界，不重复实现 GPU 资源变更。
- 需要补充前端单元/接口测试及浏览器验证，覆盖校验失败、长时间创建、创建失败、GPU/CPU Tab 展示和释放路由。
