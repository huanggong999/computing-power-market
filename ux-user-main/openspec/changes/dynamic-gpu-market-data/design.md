## Context

`/computeListNew` 页面当前由三个用户端文件组成：

- `src/view/computeList/new.vue`：维护筛选条件、排序、分页和列表加载。
- `src/components/GpuFilterPanel/index.vue`：展示计费方式、地区、专区、GPU 型号、GPU 数量筛选项。
- `src/components/GpuCard/index.vue`：展示每条 GPU 资源卡片。

页面现有展示/交互清单：

| 区域 | 字段/功能 |
| --- | --- |
| 筛选 | 计费方式、地区、专区、GPU 型号多选、GPU 数量 |
| 排序 | 价格、显存、型号；升序/降序切换 |
| 分页 | total、上一页/下一页、页码、pageSize、跳页 |
| 卡片顶部 | 地区、机器 ID、机器 UUID、可租用至、缓存优化标识 |
| 卡片标题 | GPU 型号、显存、空闲数量、总量 |
| 每 GPU 分配 | CPU 核数、CPU 型号、内存 |
| 硬盘 | 系统盘、数据盘、可扩容容量 |
| 其他 | GPU 驱动、CUDA 版本 |
| 价格区 | 当前计费方式价格、折扣价、折扣率、可租按钮 |
| 空状态 | 无数据提示、清空筛选 |

后端已经存在 GPU 市场相关表和接口：

| 页面字段 | 当前表/字段 | 是否满足 |
| --- | --- | --- |
| resourceId/resourceNo | `gpu_resource.id/resource_no` | 满足 |
| machineId/machineUuid | `gpu_resource.machine_id/machine_uuid` | 满足 |
| region/regionCode | `gpu_region.region_name` + `gpu_resource.region_code` | 满足 |
| zone/zoneCode | `gpu_zone.zone_name` + `gpu_resource.zone_code` | 满足 |
| model/vram | `gpu_resource_spec.model/vram` | 满足 |
| gpuCount | `gpu_resource.gpu_count` | 满足 |
| gpuDriver/cudaVersion | `gpu_resource.gpu_driver/cuda_version` | 满足 |
| cacheOptimized | `gpu_resource.cache_optimized` | 满足 |
| cpuCores/cpuModel | `gpu_resource.cpu_cores/cpu_model` | 满足 |
| memory/systemDisk/dataDisk/expandable | `gpu_resource.memory_size/system_disk/data_disk/expandable` | 满足 |
| rentableUntil | `gpu_resource.rentable_until` | 满足 |
| status | `gpu_resource.status` | 满足 |
| availableCount/totalCount/rentableCount | `gpu_resource_stock.available_count/total_count` | 满足 |
| price/discountPrice/discountRate | `gpu_resource_price.unit_price/discount_price/discount_rate` | 满足 |
| billingType | `gpu_resource_price.billing_type` | 满足，但枚举大小写不一致 |

表设计结论：

- **满足当前页面动态化所需数据，不需要新增业务表。**
- 当前唯一不确定点是“专区是否需要隶属于地区”。现有 `gpu_zone` 是全局专区表，页面也是全局显示专区，因此当前不补表。
- 如果产品要求地区切换后只显示该地区下的专区，应补充：
  - 简单方案：`ALTER TABLE gpu_zone ADD COLUMN region_code VARCHAR(32) COMMENT '所属地区编码';`
  - 多对多方案：新增 `gpu_region_zone(region_code, zone_code)`。
  - 当前建议暂不做，避免过度设计。

当前主要缺口在代码一致性而非表结构：

1. 用户端 API 文件已存在 `/pc/gpu/market/meta`、`/pc/gpu/market/list`，但需要确认请求参数能被后端分页组件和 `List<String>` 正确接收。
2. 后端 PC 接口已存在，但 `detail` 仍是空实现；列表页暂不依赖详情，可作为后续。
3. 管理端资源抽屉使用 `regionId/zoneId`，后端 `GpuResourceEdit` 接收的是 `regionCode/zoneCode`。
4. 管理端价格枚举使用 `HOURLY/DAILY/WEEKLY/MONTHLY`，后端表和 PC 接口使用 `hourly/daily/weekly/monthly`。
5. 管理端资源 API 声明了 `/update`、GET 删除/状态接口，但后端实际提供的是 `POST /resource/save`、`POST /resource/status`、`DELETE /resource/{id}`。
6. 管理端部分字段 UI 使用数字输入（内存/磁盘/可扩容），后端表定义为字符串（如 `512 GB`、`1 TB NVMe`、`8 TB`），需要统一展示/提交格式。
7. 已通过浏览器打开 `http://localhost:4173/#/computeListNew`；当前页面能发起 GPU 接口请求，但 `/pc/gpu/market/meta` 返回业务 `code=500`，导致筛选项为空；`/pc/gpu/market/list` 返回 `code=200` 但数据结构是 `pageNo/pageSize/pageTotal/dataTotal/list`，而用户端当前读取 `data.total`，需要适配 `dataTotal` 或统一 PageResult 契约。

## Goals / Non-Goals

**Goals:**

- 让 `/computeListNew` 页面所有列表数据、筛选项、排序结果、分页数量都来自后端接口。
- 复用现有 GPU 市场表结构，不新增表；只在设计中记录可选的专区-地区关联补充方案。
- 让管理后台维护的上架资源、库存、价格能被 PC 端市场列表立即读取。
- 修正前后端字段/枚举/接口路径不一致问题，确保新增或编辑资源后能进入市场列表。
- 保持现有页面视觉结构和交互方式不变，降低前端改动范围。

**Non-Goals:**

- 不实现租用下单流程；卡片“可租”按钮仍只负责选择/提示，后续再接订单。
- 不新增 Redis 缓存或搜索引擎；当前数据量用 MySQL 查询即可。
- 不重构整个 GPU 管理模块，只修正与市场动态化直接相关的缺口。
- 不强制实现专区按地区级联，除非产品确认该关系。
- 不迁移旧 `/computeList` 页面。

## Decisions

### 1. 复用现有 6 张 GPU 市场表，不新增独立展示表

当前页面每个字段都能映射到现有表：

- `gpu_resource_spec`：GPU 型号、显存、规格排序。
- `gpu_region`：地区筛选与展示名称。
- `gpu_zone`：专区筛选与展示名称。
- `gpu_resource`：机器、配置、驱动、CUDA、缓存优化、租期、上下架状态。
- `gpu_resource_price`：计费方式价格、折扣价、折扣率。
- `gpu_resource_stock`：空闲/总量。

选择复用的原因：后台已经围绕这些表实现资源维护页面，新增展示表会产生同步问题。备选方案是新增 `gpu_market_item` 聚合表，但会增加维护成本并需要同步任务，不适合当前范围。

### 2. PC 列表接口返回前端卡片所需的扁平 VO

`/pc/gpu/market/list` 继续返回 `GpuMarketItemVO`，包含前端卡片直接使用的扁平字段。后端在 service 层聚合资源、规格、地区、库存、价格。

理由：前端卡片不需要理解后端表结构，接口契约更稳定。备选方案是返回嵌套对象（resource/spec/price/stock），但会增加前端映射逻辑。

建议响应示例：

```json
{
  "code": 200,
  "data": {
    "total": 6,
    "list": [
      {
        "resourceId": "1",
        "resourceNo": "RES-20250513-001",
        "model": "A800",
        "vram": "80 GB",
        "region": "西北B区",
        "regionCode": "northwest-b",
        "machineId": "D21机",
        "machineUuid": "3y80s00ajm",
        "rentableUntil": "2026-12-31",
        "availableCount": 3,
        "totalCount": 8,
        "cacheOptimized": true,
        "cpuCores": 128,
        "cpuModel": "Intel Xeon Platinum 8480+",
        "memory": "2048 GB",
        "systemDisk": "500 GB SSD",
        "dataDisk": "8 TB NVMe",
        "expandable": "16 TB",
        "gpuDriver": "535.104.05",
        "cudaVersion": "12.2",
        "price": "45.00",
        "discountPrice": "38.25",
        "discountRate": "85%",
        "rentableCount": 3
      }
    ]
  }
}
```

### 3. 计费类型统一使用小写枚举

统一使用：

- `hourly`：按量计费/小时
- `daily`：包日
- `weekly`：包周
- `monthly`：包月

原因：数据库迁移、后端注释、PC 用户端 API 都已经使用小写。管理端 `ResourceDrawer.vue` 目前使用大写，应改为小写，否则用户端按 `hourly` 查询不到后台保存的 `HOURLY` 价格。

### 4. 管理端提交地区/专区编码，而不是 ID

后端 `GpuResourceEdit` 期望 `regionCode` 和 `zoneCode`，数据库也是 code 字段。管理端地区/专区下拉应以 `regionCode/zoneCode` 作为 value。

备选方案是后端兼容 `regionId/zoneId` 后再查询 code，但这会让 Edit 参数与表结构不一致。当前选择修正管理端字段，保持契约简单。

### 5. 分页参数优先兼容项目 PageQuery 约定

用户端当前传 `pageNum/pageSize`。后端 `PageQuery` 的标准参数可能是 `pageNo/pageSize` 或项目自定义常量，应实施时确认 `PageQuery.PAGE_NO_NAME`。设计上建议：

- 前端改为项目后端实际识别的分页参数；或
- 后端 PC 接口显式接收 `pageNum` 并转换为 `PageQuery`。

优先选择前端适配，减少后端特殊逻辑。

### 6. 过滤项 meta 只返回可用数据

`/pc/gpu/market/meta` 应返回：

- 启用地区：`gpu_region.status = 1 AND del_flag = 0`
- 启用专区：`gpu_zone.status = 1 AND del_flag = 0`
- GPU 型号统计：只统计上架资源关联的启用规格，避免筛选出无结果型号
- GPU 数量：建议从上架资源 `gpu_resource.gpu_count` distinct 生成；如果为空再降级到默认 `[1,2,3,4,5,6,7,8,10,12]`
- 计费类型：固定枚举或从价格表 distinct 生成。当前建议固定枚举，保证 UI 稳定。

### 7. 排序在 SQL 中白名单处理

现有 XML 用 `<choose>` 分支处理 `price/vram/model`，不会拼接任意字段，方向也只允许 `asc/desc` 分支，安全性可接受。实施时保持白名单，不使用 `${sortBy}` 或 `${sortOrder}`。

### 8. 显存排序暂按规格排序字段，不解析字符串

`vram` 是字符串（如 `32 GB`、`80 GB`），直接数值排序需要额外字段。现有 SQL 使用 `gpu_resource_spec.sort_order` 代表显存/规格排序。

如果后续要求严格按显存容量排序，应新增 `vram_gb INT` 到 `gpu_resource_spec`。当前页面只需要可控排序，先不补表。

## Risks / Trade-offs

- **[Risk] 管理端保存接口当前可能不可用。** `gpuResource.ts` 使用 `/resource/update`、GET 删除/状态，但后端没有这些路径。
  - **Mitigation:** 实施时同步修正管理端 API：新增/编辑都调用 `POST /system/gpu/resource/save`，状态调用 `POST /system/gpu/resource/status`，删除调用 `DELETE /system/gpu/resource/{id}`。

- **[Risk] 管理端编辑无法完整回填。** 后端 `resourceDetail` 目前是 TODO，编辑抽屉只用列表 row 打开，可能缺少 prices、stock、磁盘、驱动等完整字段。
  - **Mitigation:** 实施时补齐 `GET /system/gpu/resource/{id}` 返回完整资源详情，编辑时先查详情再打开抽屉。

- **[Risk] 单位格式不一致。** 管理端用数字输入内存/磁盘，后端和用户端展示期望字符串。
  - **Mitigation:** 选择一种提交契约：短期在管理端提交前拼接单位并用字符串输入；长期可拆成数值+单位字段。本次为避免表变更，建议改为字符串输入或提交前统一拼接 `GB`。

- **[Risk] `gpuModels` 多选参数序列化方式不匹配。** Axios 可能发送 `gpuModels[]=A&gpuModels[]=B`，Spring 端 `List<String> gpuModels` 通常期望 `gpuModels=A&gpuModels=B` 或逗号字符串。
  - **Mitigation:** 前端使用项目现有数组参数序列化规则，或改为逗号字符串并由后端拆分。

- **[Risk] PageHelper 分页总数丢失。** Service 里 `pageQuery.startPage()` 后返回 `PageResult.of(voList)`，如果 `voList` 已经由 stream 转成普通 list，可能丢失 PageInfo 总数。
  - **Mitigation:** 实施时验证 total；必要时在 mapper 返回分页原 list 后保留 total，或单独调用 `countGpuMarketList`。

- **[Risk] `/pc/gpu/market/meta` 当前返回业务 500。** 浏览器实测该接口 HTTP 200 但响应 `code=500,data=null,msg=服务器繁忙，请稍后再试`，导致筛选项为空。
  - **Mitigation:** 实施时优先排查 `gpuRegionService.listAllEnabled()`、`gpuZoneService.listAllEnabled()`、`gpuResourceSpecService.getModelStats()` 的异常栈，并保证 meta 任一分类为空时返回空数组而不是整体失败。

- **[Risk] 用户端分页字段和后端 PageResult 不一致。** 浏览器实测列表响应包含 `pageNo/pageSize/pageTotal/dataTotal/list`，但 `new.vue` 当前读取 `res.data.total`。
  - **Mitigation:** 实施时要么前端读取 `dataTotal`，要么后端 PC 接口包装为用户端约定的 `total/list`；推荐前端类型对齐项目通用 PageResult，减少后端特殊返回。

- **[Risk] Chrome 自动化验证依赖远程调试授权。** 当前用户已打开权限并完成页面检查，但后续如果重开浏览器仍可能出现授权弹窗。
  - **Mitigation:** 验收前确认 browser-act 能连接 Chrome，再进行 UI 冒烟测试。

## Migration Plan

1. 不执行破坏性数据迁移。
2. 确认当前数据库已包含 `V2025_05_13_001__gpu_market.sql` 中的 6 张表。
3. 如现有 `gpu_resource_price.billing_type` 已保存大写值，执行一次数据修正：
   - `HOURLY -> hourly`
   - `DAILY -> daily`
   - `WEEKLY -> weekly`
   - `MONTHLY -> monthly`
4. 发布后端 PC 接口和管理端资源接口修正。
5. 发布用户端 `/computeListNew` 页面接口联调修正。
6. 回滚策略：前端可回退到上一版本；数据库仅做枚举值大小写修正，必要时可反向更新。

## Open Questions

1. 专区是否需要按地区级联？如果需要，应选择 `gpu_zone.region_code` 还是 `gpu_region_zone` 关联表。
2. 内存、系统盘、数据盘、可扩容是否要求结构化筛选？如果后续要按容量筛选，应新增数值字段。
3. “租用”按钮下一步跳转到哪里？当前仅做动态数据展示，不接下单流程。
4. 计费方式展示文案“按量计费”是否统一表示“/小时”？卡片当前价格单位固定为 `/时`，当选择包日/包周/包月时需要改成对应单位。
