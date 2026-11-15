## Why

GPU 实例创建后，用户目前无法在门户端调整数据盘、CPU/内存或公网带宽，资源变化只能依赖后台或人工处理。平台也缺少不加入 GPU/Kubernetes 集群的独立 CPU 实例购买入口，无法覆盖通用 CPU 云主机场景。

接口文档已定义资源变更和独立 CPU 实例的稳定接口（64–76），现在需要在用户端接入这些能力，并保持 GPU 实例与独立 CPU 实例的展示和生命周期操作隔离。

## What Changes

- 在现有实例管理页增加 GPU/CPU 类型 Tab，分别请求 GPU Pod 实例接口和独立 CPU 实例接口。
- 为 GPU 实例增加资源变更入口：查询变更选项、数据盘扩容、CPU/内存变更、任务进度轮询和历史任务查看。
- 为 GPU 实例增加公网带宽调整入口，支持按目标 Mbps 扩容和缩容。
- 根据接口返回的 `backing`、能力开关和上下限动态渲染表单，区分火山云实例和平台自建节点的提示与限制。
- 增加独立 CPU 实例购买页，支持地域/可用区、CPU 规格、计费方式、系统盘和数据盘配置。
- CPU 购买支持按量付费、包月和包年，并通过接口返回的规格 ID 下单；实例不加入集群、不创建 GPU/Kubernetes 资源。
- 在实例管理页对 CPU 实例分流详情、开机、关机和释放操作，展示 CPU、内存、磁盘、公网 IP 和 SSH 信息。
- 预留现有 GPU 租用订单的 `orderType=RESOURCE_RESIZE` 扩展点，但本期不实现资源变更费用试算、支付和公网购买计费能力。

## Capabilities

### New Capabilities

- `gpu-instance-resource-resize`: GPU 实例资源变更、异步任务跟踪和公网带宽调整。
- `cpu-instance-purchase`: 独立 CPU 实例的规格浏览、购买、列表展示和生命周期管理。

### Modified Capabilities

- None.

## Impact

- 用户端 `ux-user-main`：实例管理页、实例详情抽屉、资源变更表单、CPU 购买页、API 类型和状态管理。
- 用户端路由与菜单：增加 CPU 购买入口，并在实例页增加 GPU/CPU 类型切换。
- 门户后端适配层：代理或封装文档定义的 `/api/v1/instances/*` 和 `/api/v1/cpu/*` 接口，透传租户、鉴权和异步任务状态。
- 现有 GPU 实例操作需要按实例来源和 `origin/backing` 分流；既有 GPU 创建、开关机、释放行为保持兼容。
- 本期不改 GPU/CPU 购买页的公网 IP 购买和计费规则，不实现资源变更支付流程；这些能力另立变更。
