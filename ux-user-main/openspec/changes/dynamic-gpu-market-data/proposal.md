## Why

`/computeListNew` “算力市场-新”页面目前依赖静态/演示数据，无法反映管理后台维护的 GPU 资源、库存、价格和上下架状态。后台已经存在一套 GPU 资源管理表与部分 PC 端接口，本次变更将把页面切换为接口驱动，并补齐管理端与接口层的缺口，确保用户看到的数据来自后台维护结果。

## What Changes

- 将用户端 `computeListNew` 页面所需数据统一从 PC 端接口获取：筛选元数据、资源分页列表、排序结果和空状态均基于接口响应。
- 梳理页面字段与现有表结构映射：资源、规格、地区、专区、价格、库存 6 张表基本满足页面展示字段。
- 不新增独立业务表；仅补齐现有表/代码使用缺口：
  - 统一计费类型枚举为 `hourly/daily/weekly/monthly`，避免管理端目前 `HOURLY/DAILY/...` 与后端表设计不一致。
  - 管理端保存资源时提交 `regionCode`、`zoneCode`，避免当前抽屉使用 `regionId`、`zoneId` 导致后端字段接收不到。
  - 管理端编辑时需要可回填价格、库存、磁盘、CUDA、驱动、缓存优化等完整字段。
  - 如需“专区从属于地区”的业务约束，需要给 `gpu_zone` 补充 `region_code` 字段；当前页面和表设计把专区当作全局筛选项，可暂不补。
- 完善 PC 端接口行为：
  - `/pc/gpu/market/meta` 返回后台已启用的地区、专区、GPU 型号统计、可用 GPU 数量选项、计费类型。
  - `/pc/gpu/market/list` 按后台上架资源返回分页列表，支持计费方式、地区、专区、GPU 型号、GPU 数量、价格/显存/型号排序。
  - 修正分页参数命名和多值参数传递，确保前端 `pageNum/pageSize/gpuModels[]` 能被后端正确接收或适配。
- 保持 `/computeListNew` 页面现有视觉和交互不变，仅替换数据来源并修正与接口字段不一致的问题。

## Capabilities

### New Capabilities
- `dynamic-gpu-market-data`: 用户端算力市场新页面基于后台维护的 GPU 资源数据动态展示、筛选、排序和分页。

### Modified Capabilities

## Impact

- 用户端前端：
  - `ux-user-main/src/view/computeList/new.vue`
  - `ux-user-main/src/components/GpuFilterPanel/index.vue`
  - `ux-user-main/src/components/GpuCard/index.vue`
  - `ux-user-main/src/api/gpuMarket.ts`
- 管理后台前端：
  - `ux-admin-main/src/views/gpu/resource/index.vue`
  - `ux-admin-main/src/views/gpu/resource/components/ResourceDrawer.vue`
  - `ux-admin-main/src/api/gpuResource.ts`
- 后端：
  - `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/PcGpuMarketController.java`
  - `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/system/SystemGpuResourceController.java`
  - `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuResourceServiceImpl.java`
  - `console-main/ai-cloud-system/src/main/resources/mapper/GpuResourceMapper.xml`
  - GPU 相关 VO/Edit/DTO/Mapper/Service 类
- 数据库：
  - 复用 `gpu_resource_spec`、`gpu_region`、`gpu_zone`、`gpu_resource`、`gpu_resource_price`、`gpu_resource_stock`。
  - 暂不新增表；如产品要求专区按地区级联，再补充 `gpu_zone.region_code` 或新增专区-地区关联表。
- 风险：
  - 当前管理端资源保存接口路径/HTTP 方法与后端不完全一致，需要同步修正。
  - 当前管理端部分字段使用数字输入，但后端表字段是带单位字符串，需要明确保存格式或在提交前拼接单位。
  - 浏览器页面验证暂被 Chrome 远程调试权限阻塞，需要授权后做最终 UI 联调。
