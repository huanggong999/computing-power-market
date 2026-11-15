# 算力市场-新 动态化改造 Proposal

## 1. 项目背景

**当前状态**：算力市场-新页面（`/#/computeListNew`）的数据是**静态写死**在前端代码中，包括GPU型号、地区、专区、资源列表等所有数据。

**目标**：基于管理后台已维护的数据（数据库表），开发后端接口，让页面数据全部通过接口动态获取。

## 2. 页面功能与数据需求分析

基于页面截图和代码分析，页面包含以下功能模块：

### 2.1 筛选区域
| 筛选项 | 选项来源 | 对应数据表 |
|--------|----------|------------|
| GPU型号 | 全部 + gpu_resource_spec.model | gpu_resource_spec |
| 显存 | 全部 + gpu_resource_spec.vram | gpu_resource_spec |
| 地区 | 全部 + gpu_region.region_name | gpu_region |
| 专区 | 全部 + gpu_zone.zone_name | gpu_zone |

### 2.2 资源列表卡片
每张卡片展示以下信息：

| 展示字段 | 数据来源 | 对应字段 |
|----------|----------|----------|
| 机器编号 | gpu_resource.machine_id | machine_id |
| 地区 | gpu_region.region_name | region_name |
| GPU型号 | gpu_resource_spec.model | model |
| 显存 | gpu_resource_spec.vram | vram |
| 架构 | gpu_resource_spec.architecture | architecture |
| CUDA版本 | gpu_resource.cuda_version | cuda_version |
| CPU核心数 | gpu_resource.cpu_cores | cpu_cores |
| CPU型号 | gpu_resource.cpu_model | cpu_model |
| 内存大小 | gpu_resource.memory_size | memory_size |
| 系统盘 | gpu_resource.system_disk | system_disk |
| 数据盘 | gpu_resource.data_disk | data_disk |
| 可扩容容量 | gpu_resource.expandable | expandable |
| 可租用截止日期 | gpu_resource.rentable_until | rentable_until |
| 库存-空闲GPU数 | gpu_resource_stock.available_count | available_count |
| 库存-总GPU数 | gpu_resource_stock.total_count | total_count |
| 时租价格 | gpu_resource_price.unit_price (hourly) | unit_price |
| 月租价格 | gpu_resource_price.unit_price (monthly) | unit_price |
| 折扣率 | gpu_resource_price.discount_rate | discount_rate |

### 2.3 操作功能
- **立即租用**：跳转租用页面（需资源ID）
- **查看详情**：查看资源详情（需资源ID）

## 3. 现有表结构评估

### 3.1 现有表清单
| 表名 | 用途 | 状态 |
|------|------|------|
| gpu_resource_spec | GPU规格定义 | ✅ 满足 |
| gpu_region | 地区配置 | ✅ 满足 |
| gpu_zone | 专区配置 | ✅ 满足 |
| gpu_resource | 资源主表 | ✅ 满足 |
| gpu_resource_price | 价格表 | ✅ 满足 |
| gpu_resource_stock | 库存表 | ✅ 满足 |

### 3.2 评估结论
**所有现有表结构已完全满足页面数据需求**，无需新增或修改表结构。

现有表设计覆盖了：
- ✅ GPU型号、显存等规格信息 (gpu_resource_spec)
- ✅ 地区、专区等位置信息 (gpu_region, gpu_zone)
- ✅ 资源详细信息 (gpu_resource)
- ✅ 多维度价格信息 (gpu_resource_price)
- ✅ 实时库存信息 (gpu_resource_stock)

## 4. 需要开发的接口清单

### 4.1 筛选数据接口（已有，复用或补充）
| 接口 | 用途 | 状态 |
|------|------|------|
| GET /pc/gpu/spec/list | 获取GPU型号/显存列表 | 需开发 |
| GET /pc/gpu/region/list | 获取地区列表 | 需开发 |
| GET /pc/gpu/zone/list | 获取专区列表 | 需开发 |

### 4.2 资源列表接口（核心）
| 接口 | 用途 | 参数 | 响应 |
|------|------|------|------|
| GET /pc/gpu/market/page | 分页查询资源列表 | page, size, model, vram, region, zone, keyword | 资源列表+价格+库存 |

### 4.3 资源详情接口
| 接口 | 用途 | 参数 | 响应 |
|------|------|------|------|
| GET /pc/gpu/market/detail/{id} | 获取资源详情 | resourceId | 资源详情+价格+库存 |

## 5. 工作量评估

### 5.1 后端开发工作量（Java）
| 任务 | 预估工时 | 说明 |
|------|----------|------|
| 筛选列表接口（3个） | 2h | spec/region/zone列表查询 |
| 资源分页查询接口 | 4h | 复杂联表查询+价格库存聚合 |
| 资源详情接口 | 2h | 单条资源详情 |
| 联调测试 | 2h | 接口调试修正 |
| **后端合计** | **10h** | |

### 5.2 前端开发工作量（Vue3）
| 任务 | 预估工时 | 说明 |
|------|----------|------|
| 筛选数据接口对接（3个） | 2h | 替换静态筛选项为接口数据 |
| 资源列表接口对接 | 3h | 替换静态卡片为接口数据 |
| 加载状态/空状态处理 | 1h | loading、空列表等状态 |
| 错误处理 | 1h | 接口失败处理 |
| 分页功能 | 1h | 分页组件对接 |
| **前端合计** | **9h** | |

### 5.3 总工作量
| 角色 | 工时 |
|------|------|
| 后端开发 | 10h |
| 前端开发 | 9h |
| **总计** | **19h** |

## 6. 建议排期

| 阶段 | 任务 | 工期 |
|------|------|------|
| Day 1 | 后端接口开发（筛选+分页+详情） | 1天 |
| Day 2 | 前端接口对接+基础功能 | 1天 |
| Day 3 | 联调测试+优化+上线 | 0.5天 |

**总工期：2.5天**

## 7. 风险评估

| 风险 | 等级 | 应对措施 |
|------|------|----------|
| 多表联查性能问题 | 中 | 加索引、考虑Redis缓存 |
| 库存实时性 | 低 | 定时同步+实时查询结合 |
| 价格/库存数据不一致 | 低 | 数据库事务保证 |

## 8. 后续优化建议（非本次范围）

1. **搜索功能**：支持关键词搜索（型号、地区等）
2. **排序功能**：支持按价格、库存等排序
3. **缓存优化**：热点数据Redis缓存
4. **实时库存**：WebSocket推送库存变化
5. **推荐算法**：基于用户行为的智能推荐

---

**结论**：现有表结构已完全满足需求，无需修改。本次工作主要集中在接口开发和前后端对接，总工作量约19小时，建议工期2.5天。
