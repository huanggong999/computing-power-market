## 上下文

后台管理端 `/system/gpu/volcano/catalog` 与官网 `/pc/gpu/market/volcano/catalog` 都通过 `GpuSchedulerApiClient` 聚合调度器的火山云目录、实时可售和价格数据。官网 `computeListNew` 将目录转换为 `source = volcano` 的临时资源行，跳转到 `computeRent` 后仍从目录草稿恢复配置；外部 GPU 下单由 `PcGpuRentController.calculateExternal()` 实时查询火山价格并生成订单。

当前目录中的 `price` / `priceMonthly` 是火山实际价格。直接用平台售价覆盖这两个字段会破坏下单成本校验、订单快照和既有前端价格解析，因此需要引入并行的价格口径。

## 目标 / 非目标

**目标：**

- 管理端可按 `region + instanceTypeId + billingType` 维护火山云 GPU 平台售价。
- 官网目录同时保留火山实际价和平台售价，并在对客展示、费用计算、余额扣费中使用平台售价。
- 下单时服务端重新查询火山实时价格并校验有效性；无效则拒单。
- 订单和订单资源记录火山实际价、平台售价、总价和溢价信息，保证后续对账可追溯。
- 未维护平台售价时保持现有行为，使用火山实际价作为对客价格。

**非目标：**

- 不修改调度器或火山云价格来源。
- 不改变自建 GPU 资源的定价模型。
- 不做批量调价规则、定时调价或按比例自动调价。
- 不在本变更中重构订单结算体系。

## 表结构设计

### 新增表：`volcano_gpu_sale_price`

用于保存火山云 GPU 的平台售价配置。表按 `region_code + instance_type_id + billing_type` 唯一定位一条当前生效配置；火山实际价仍以目录实时查询结果为准，不用本表快照替代下单校验。

| 字段 | 类型 | 约束 / 默认值 | 说明 |
| --- | --- | --- | --- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 售价配置 ID |
| `region_code` | `VARCHAR(64)` | `NOT NULL` | 火山云地域编码 |
| `instance_type_id` | `VARCHAR(128)` | `NOT NULL` | 火山云实例规格 ID |
| `gpu_model` | `VARCHAR(64)` | `NOT NULL` | GPU 型号快照 |
| `gpu_memory` | `VARCHAR(32)` | `NULL` | GPU 显存快照 |
| `gpu_count` | `INT` | `NOT NULL DEFAULT 0` | 实例 GPU 卡数快照 |
| `billing_type` | `VARCHAR(16)` | `NOT NULL` | 计费类型：`on_demand` / `hourly` / `daily` / `weekly` / `monthly` |
| `upstream_price` | `DECIMAL(12,4)` | `NULL` | 保存时的火山实际价快照，仅用于管理端展示与差异对比 |
| `sale_price` | `DECIMAL(12,4)` | `NOT NULL` | 平台售价，必须大于 0 |
| `currency` | `VARCHAR(8)` | `NOT NULL DEFAULT 'CNY'` | 币种，当前仅支持 `CNY` |
| `status` | `TINYINT(1)` | `NOT NULL DEFAULT 1` | `1` 启用，`0` 停用 |
| `remark` | `VARCHAR(256)` | `NULL` | 备注 |
| `create_by` | `VARCHAR(64)` | `NULL` | 创建人 |
| `create_by_id` | `BIGINT` | `NULL` | 创建人 ID |
| `create_time` | `DATETIME` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `update_by` | `VARCHAR(64)` | `NULL` | 更新人 |
| `update_by_id` | `BIGINT` | `NULL` | 更新人 ID |
| `update_time` | `DATETIME` | `NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |
| `del_flag` | `TINYINT(1)` | `NOT NULL DEFAULT 0` | 删除标记；“恢复默认价”采用物理删除，避免软删数据占用唯一键 |

索引与约束：

- `UNIQUE KEY uk_region_instance_billing (region_code, instance_type_id, billing_type)`
- `INDEX idx_region_billing (region_code, billing_type)`
- `INDEX idx_instance_type (instance_type_id)`
- `INDEX idx_gpu_model (gpu_model)`
- `CHECK (sale_price > 0)`
- `CHECK (upstream_price IS NULL OR upstream_price >= 0)`
- `CHECK (currency = 'CNY')`
- `CHECK (billing_type IN ('on_demand', 'hourly', 'daily', 'weekly', 'monthly'))`
- `CHECK (status IN (0, 1))`
- `CHECK (del_flag IN (0, 1))`

参考 DDL：

```sql
CREATE TABLE IF NOT EXISTS `volcano_gpu_sale_price` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '售价配置ID',
  `region_code` VARCHAR(64) NOT NULL COMMENT '火山云地域编码',
  `instance_type_id` VARCHAR(128) NOT NULL COMMENT '火山云实例规格ID',
  `gpu_model` VARCHAR(64) NOT NULL COMMENT 'GPU型号',
  `gpu_memory` VARCHAR(32) DEFAULT NULL COMMENT 'GPU显存',
  `gpu_count` INT NOT NULL DEFAULT 0 COMMENT '实例GPU卡数',
  `billing_type` VARCHAR(16) NOT NULL COMMENT '计费类型 on_demand/hourly/daily/weekly/monthly',
  `upstream_price` DECIMAL(12,4) DEFAULT NULL COMMENT '保存时火山实际价快照',
  `sale_price` DECIMAL(12,4) NOT NULL COMMENT '平台售价',
  `currency` VARCHAR(8) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 1启用 0停用',
  `remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_by_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_by_id` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记 0正常 1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_region_instance_billing` (`region_code`, `instance_type_id`, `billing_type`),
  INDEX `idx_region_billing` (`region_code`, `billing_type`),
  INDEX `idx_instance_type` (`instance_type_id`),
  INDEX `idx_gpu_model` (`gpu_model`),
  CONSTRAINT `chk_volcano_sale_price` CHECK (`sale_price` > 0),
  CONSTRAINT `chk_volcano_upstream_price` CHECK (`upstream_price` IS NULL OR `upstream_price` >= 0),
  CONSTRAINT `chk_volcano_currency` CHECK (`currency` = 'CNY'),
  CONSTRAINT `chk_volcano_billing_type` CHECK (`billing_type` IN ('on_demand', 'hourly', 'daily', 'weekly', 'monthly')),
  CONSTRAINT `chk_volcano_price_status` CHECK (`status` IN (0, 1)),
  CONSTRAINT `chk_volcano_price_del_flag` CHECK (`del_flag` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='火山云GPU平台售价配置表';
```

如果目标 MySQL 版本或运维规范不启用 `CHECK` 约束，上述规则必须在 Service 层保留等价校验。`DECIMAL(12,4)` 可表示最高 `999,999,999.9999` 的金额，并保留计费精度。

字段口径补充：

- 本表只保存当前生效配置，不承担历史价格流水职责；当前通过审计字段追踪最后修改。
- `gpu_model`、`gpu_memory`、`gpu_count`、`upstream_price` 是保存配置时的目录快照，方便管理端识别规格和展示差额。
- 售价匹配仅使用唯一键中的三个业务字段；快照字段变化不触发新配置。
- “恢复默认价”物理删除匹配行，后续目录和下单自然回退实时火山价。

### 订单侧不新增表

`sys_order` 与 `sys_order_source` 不需要为本需求新增字段或新表：

- 订单资源 `unit_price` 记录火山实时单价（成本口径）。
- `premium_price` 记录平台售价单价（结算单价，不填差额）。
- `final_unit_price` 记录用户结算单价，当前等于 `premium_price`。
- `configDetail.pricing` 保存 `upstreamPrice`（火山实时单价）、`salePrice`（平台售价单价）、`premiumUnitAmount`（结算减成本差额）、`billingType`、`quantity`、`duration`、`currency` 快照。

订单创建、对账和报表可以直接区分成本价与销售价，不需要反查当前配置表。

## 决策

1. 使用独立配置表保存平台售价。

   表按唯一键 `region_code + instance_type_id + billing_type` 保存 `sale_price`、状态、备注和审计字段。`instanceTypeId` 是最稳定的火山规格标识；`region_code` 用于隔离同规格在不同地域的价格；`billing_type` 使用现有规范值 `on_demand` / `hourly` / `daily` / `weekly` / `monthly`，并兼容 `on_demand` 与 `hourly` 的现有等价逻辑。

   替代方案：把售价写入现有 `gpu_resource_price`。该表服务于自建资源 `resourceId`，火山目录资源没有本地资源 ID，强行造资源会造成库存和资源管理语义冲突。

2. 不覆盖目录中的火山实际价字段。

   `price` / `priceMonthly` 继续表示火山实际价。在每个实例规格对象上新增 `salePrice` / `platformPrice`（或 `salePriceMonthly` / `platformPriceMonthly`）表示对客平台售价；前端类型同步声明这些字段。这样 `findGpuPrice()` 的实时校验口径不变，接口兼容旧前端。

   替代方案：直接改写 `price` 字段并在下单时另查实际价。虽然前端改动少，但字段语义会从“上游价格”变成“销售价”，下游一旦复用就容易把成本和收入算混。

3. 售价合并放在后端目录返回前完成。

   管理端与官网目录都从同一个聚合目录出发，在后端读取启用的售价配置并注入实例规格，可避免官网自行维护价格版本或管理端改动后长时间不同步。官网展示目录缓存仍只缓存上游目录，售价配置在返回时叠加；配置更新立即生效，不需要等待目录缓存过期。

4. 官网费用与下单金额必须由服务端计算。

   `computeListNew` 和 `computeRent` 页面只用平台售价做展示和本地预览。提交外部 GPU 订单时，后端按实时火山价校验可售，再按平台售价计算 `unitPrice`（销售价）、`subtotal/total`（应付）与 `premium`（销售价减火山价）。禁止信任 `podCreateRequest.pricing` 中由浏览器提交的价格作为扣费依据。

5. 订单快照同时保存双价格。

   订单主表继续保存用户应付金额。订单资源 `unit_price` 记录火山实时价，`premium_price` 与 `final_unit_price` 记录平台售价；`configDetail.pricing` 增加上游单价、平台售价单价、单规格溢价和计费周期快照。后续如需成本利润报表，可直接从快照口径取数。

## 风险 / 权衡

- 上游目录规格变化导致配置失效 -> 管理端展示配置匹配状态；售价服务按唯一键忽略已消失规格，目录仍返回默认火山价。
- 目录缓存与实时价格存在时间差 -> 下单必须实时调用 `getGpuCatalog()` 查询火山价格，页面缓存仅用于展示。
- `on_demand` 与 `hourly` 语义并存 -> 查询售价时保留现有等价回退，目录返回时按请求计费周期注入对应售价。
- 前端草稿在管理端改价后未刷新 -> 进入下单页和提交订单时都从服务端目录重新解析当前售价，避免仅依赖列表页旧值。
- 双价格增加对账复杂度 -> 订单快照显式命名 `upstreamPrice`、`salePrice`、`premiumAmount`，避免用注释或隐式字段推断。

## 迁移计划

1. 新增 `volcano_gpu_sale_price` 售价表，并发布后端目录合并、管理端维护接口和下单计费逻辑。
2. 发布后台管理端价格编辑页面。
3. 发布官网价格字段兼容逻辑；无配置时自动回退火山实际价，可灰度上线。
4. 验证管理端改价后官网列表、租用页、下单扣费和订单快照金额。
5. 回滚时停用售价配置即可回到火山实际价；售价表和管理入口可保留，不影响下单主流程。

## 开放问题

- 平台售价是否需要区分新购与续费价格，需在续费接入火山云资源时再确认。
- 是否需要在管理端展示历史调价记录，当前仅保留 `create/update` 审计字段。
