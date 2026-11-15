# GPU 规格字段对比分析

> **日期**: 2026/06/13  
> **来源**: 对比 AutoDL 算力市场 (https://www.autodl.com/market/list) 与 逸云数智本地系统 GPU 规格管理  
> **目的**: 对齐 GPU 规格展示字段，找出缺失项

---

## 一、AutoDL 算力市场页面信息

### 1.1 可选 GPU 型号（共 1293 条实例）

| GPU 型号 | 可用/总量 | 显存 |
|---------|----------|------|
| RTX 5090 | 283/1784 | 32 GB |
| RTX PRO 6000 | 266/1564 | - |
| RTX 6000D | 25/54 | - |
| vGPU-32GB | 706/2033 | 32 GB |
| vGPU-48GB | 196/922 | 48 GB |
| RTX 5090 D | 15/48 | - |
| H800 | 20/96 | - |
| RTX 4090D | 26/1096 | - |
| RTX 4090 | 37/1832 | - |
| RTX 3090 | 0/210 | - |
| RTX 3080 Ti | 68/368 | - |
| CPU | 12/328 | - |
| ... | ... | ... |

### 1.2 可选地区
西北B区、北京B区、重庆A区、内蒙B区、北京A区、佛山区、V100专区、A800专区、摩尔线程专区、L20专区

### 1.3 计费方式
按量计费、包日、包周、包月

### 1.4 实例卡片展示字段（以 RTX 5090 为例）

```
西北B区 / C07机 mm22sg4l46          可租用至：2027-02-01
RTX 5090 / 32 GB                   空闲/总量 1 / 8

每GPU分配                          硬盘                          其它
CPU: 25 核，Xeon(R) Platinum 8470Q  系统盘: 30 GB                  GPU驱动: 580.105.08
内存: 90 GB                         数据盘: 50 GB，可扩容 4572 GB   CUDA版本: ≤ 13.0

价格: ¥3.03/时
会员最低享7.9折 ¥2.39/时
```

### 1.5 GPU 规格层面特有字段
- **单精度 TFLOPS**: 如 `82.58 TFLOPS`（鼠标悬停 CUDA 版本旁 `?` 图标显示）
- **半精度/Tensor TFLOPS**: 如 `165.2 Tensor TFLOPS`

---

## 二、本地系统 GPU 规格管理（`gpu_resource_spec`）

### 2.1 当前字段

| 字段名 | 类型 | 说明 | 示例 |
|-------|------|------|------|
| `id` | Long | 规格ID | 1 |
| `model` | String | GPU型号 | RTX 5090 |
| `vram` | String | 显存 | 32 GB |
| `architecture` | String | 架构 | Blackwell |
| `description` | String | 描述 | NVIDIA RTX 5090，顶级消费级显卡 |
| `tags` | String(JSON) | 标签 | ["热门","最新"] |
| `sort_order` | Integer | 排序 | 1 |
| `status` | Integer | 状态 1启用 0禁用 | 1 |

### 2.2 管理界面展示列
序号、ID、型号、显存(GB)、架构、描述、标签、排序、状态、操作

---

## 三、字段对比分析

### 3.1 已对齐字段 ✅

| AutoDL 展示 | 本地字段 | 说明 |
|------------|---------|------|
| GPU 型号 | `model` | ✅ 已有 |
| 显存 | `vram` | ✅ 已有 |
| 架构 | `architecture` | ✅ 已有 |
| 描述 | `description` | ✅ 已有 |
| 标签 | `tags` | ✅ 已有 |

### 3.2 GPU 规格层面缺少字段 ❌

| 建议新增字段 | 类型 | 必要性 | 用途 |
|-------------|------|--------|------|
| `single_precision_tflops` | DECIMAL / String | ⭐⭐⭐ **高** | 单精度浮点性能，AutoDL 卡片展示 |
| `half_precision_tflops` | DECIMAL / String | ⭐⭐⭐ **高** | 半精度/Tensor 性能，AutoDL 卡片展示 |
| `image_url` | String | ⭐⭐ 中 | GPU 型号图标/图片，前端展示更美观 |
| `vram_type` | String | ⭐⭐ 中 | 显存类型：GDDR6X / HBM3 / HBM2e |
| `tdp` | Integer | ⭐ 低 | 显卡功耗（W），辅助信息 |

### 3.3 实例层面字段（不应放在规格表）

| AutoDL 展示字段 | 应归属表 | 本地是否已有 |
|----------------|---------|------------|
| 地区/机房 | `gpu_resource` | ✅ 已有 |
| 可租用至 | `gpu_resource` | ✅ 已有 |
| 空闲/总量 | `gpu_resource_stock` | ✅ 已有 |
| CPU 核数 + 型号 | `gpu_resource` | ✅ 已有 |
| 内存 | `gpu_resource` | ✅ 已有 |
| 系统盘/数据盘 | `gpu_resource` | ✅ 已有 |
| GPU 驱动版本 | `gpu_resource` | ✅ 已有 |
| CUDA 版本 | `gpu_resource` | ✅ 已有 |
| 价格 | `gpu_resource_price` | ✅ 已有 |

---

## 四、现有实例级表结构

### 4.1 `gpu_resource` — GPU 资源主表

| 字段名 | 说明 | AutoDL 对应 |
|-------|------|------------|
| `resource_no` | 资源编号 | - |
| `machine_id` | 机器编号 | C07机 |
| `machine_uuid` | 机器唯一标识 | mm22sg4l46 |
| `region_code` | 地区编码 | 西北B区 |
| `zone_code` | 专区编码 | - |
| `spec_id` | 关联规格ID | RTX 5090 / 32 GB |
| `gpu_count` | 单台机器GPU数量 | 8 |
| `gpu_driver` | GPU驱动版本 | 580.105.08 |
| `cuda_version` | CUDA版本 | ≤ 13.0 |
| `cache_optimized` | 是否缓存优化 | 缓存优化标签 |
| `cpu_cores` | CPU核心数 | 25 核 |
| `cpu_model` | CPU型号全称 | Xeon(R) Platinum 8470Q |
| `memory_size` | 内存大小 | 90 GB |
| `system_disk` | 系统盘 | 30 GB |
| `data_disk` | 数据盘 | 50 GB |
| `expandable` | 可扩容容量 | 4572 GB |
| `rentable_until` | 可租用截止日期 | 2027-02-01 |
| `status` | 状态 1上架 2下架 3维护中 | - |

### 4.2 `gpu_resource_stock` — 实时库存表

| 字段名 | 说明 | AutoDL 对应 |
|-------|------|------------|
| `resource_id` | 关联资源ID | - |
| `available_count` | 空闲GPU数量 | 1 |
| `total_count` | GPU总量 | 8 |
| `last_sync_time` | 上次同步时间 | - |

### 4.3 `gpu_resource_price` — 价格表

| 字段名 | 说明 | AutoDL 对应 |
|-------|------|------------|
| `resource_id` | 关联资源ID | - |
| `billing_type` | 计费方式 hourly/daily/weekly/monthly | 按量计费/包日/包周/包月 |
| `unit_price` | 单价 | ¥3.03/时 |
| `discount_price` | 折扣价 | ¥2.39/时 |
| `discount_rate` | 折扣率 | 7.9折 |
| `currency` | 币种 | CNY |

---

## 五、结论与建议

### 5.1 结论

| 层级 | 状态 |
|------|------|
| GPU 规格层面 (`gpu_resource_spec`) | ⚠️ **缺少 TFLOPS 性能指标** |
| 实例层面 (`gpu_resource` + `gpu_resource_stock` + `gpu_resource_price`) | ✅ **已全部设计到位** |

### 5.2 建议

1. **必加字段**（高优先级）：
   - `single_precision_tflops`（单精度 TFLOPS）
   - `half_precision_tflops`（半精度/Tensor TFLOPS）
   - 这是 AutoDL 在 GPU 规格维度上展示的核心性能指标，直接决定用户选卡决策。

2. **可选字段**（中低优先级）：
   - `image_url`：GPU 型号图标，前端展示更美观
   - `vram_type`：显存类型（GDDR6X / HBM3 等）
   - `tdp`：显卡功耗

3. **实例级字段不要加到规格表**：
   - CPU、内存、硬盘、驱动、CUDA、价格等已分别维护在 `gpu_resource`、`gpu_resource_stock`、`gpu_resource_price` 表中，同一型号的不同机器配置可能不同，应独立维护。

---

## 六、附录：关键代码文件

| 文件路径 | 说明 |
|---------|------|
| `ai-cloud-system/src/main/java/com/lingyang/cloud/entity/GpuResourceSpecEntity.java` | GPU 规格实体 |
| `ai-cloud-system/src/main/java/com/lingyang/cloud/entity/GpuResourceEntity.java` | GPU 资源实体 |
| `ai-cloud-system/src/main/java/com/lingyang/cloud/entity/GpuResourceStockEntity.java` | 库存实体 |
| `ai-cloud-system/src/main/java/com/lingyang/cloud/entity/GpuResourcePriceEntity.java` | 价格实体 |
| `ai-cloud-system/src/main/java/com/lingyang/cloud/model/vo/system/GpuSpecVO.java` | 规格 VO |
| `ai-cloud-system/src/main/java/com/lingyang/cloud/model/edit/gpu/GpuSpecEdit.java` | 规格编辑参数 |
| `ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuResourceSpecServiceImpl.java` | 规格服务实现 |
