# 算力市场-新 动态化改造 Design Document

## 1. 概述

本文档详细描述算力市场-新页面动态化改造的技术设计方案，包括接口设计、数据模型、前后端交互流程等。

## 2. 架构设计

### 2.1 系统架构

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   前端 (Vue3)   │◄────►│   后端 (Java)   │◄────►│   MySQL DB      │
│                 │      │                 │      │                 │
│ • 筛选组件      │      │ • Controller    │      │ • gpu_resource  │
│ • 资源卡片      │      │ • Service       │      │ • gpu_spec      │
│ • 分页组件      │      │ • Mapper        │      │ • gpu_price     │
└─────────────────┘      └─────────────────┘      └─────────────────┘
```

### 2.2 数据流

```
1. 页面加载 → 调用筛选接口获取筛选项
2. 用户选择筛选条件 → 调用分页接口获取资源列表
3. 渲染资源卡片（包含价格、库存聚合数据）
4. 用户点击租用 → 跳转详情/租用页面
```

## 3. 接口设计

### 3.1 接口概览

| 接口路径 | 方法 | 说明 |
|---------|------|------|
| `/pc/gpu/spec/options` | GET | 获取GPU规格筛选项（型号+显存） |
| `/pc/gpu/region/list` | GET | 获取地区列表 |
| `/pc/gpu/zone/list` | GET | 获取专区列表 |
| `/pc/gpu/market/page` | GET | 分页查询资源列表 |
| `/pc/gpu/market/detail/{id}` | GET | 获取资源详情 |

### 3.2 接口详情

#### 3.2.1 获取GPU规格筛选项

**请求信息**
- 路径: `/pc/gpu/spec/options`
- 方法: `GET`
- 认证: 公开访问

**响应参数**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码 |
| msg | string | 提示信息 |
| data | object | 数据对象 |
| data.models | array | GPU型号列表 |
| data.models[].value | string | 型号值 |
| data.models[].label | string | 型号名称 |
| data.vrams | array | 显存列表 |
| data.vrams[].value | string | 显存值 |
| data.vrams[].label | string | 显存名称 |

**响应示例**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "models": [
      {"value": "RTX 5090", "label": "RTX 5090"},
      {"value": "RTX 4090", "label": "RTX 4090"},
      {"value": "A800", "label": "A800"}
    ],
    "vrams": [
      {"value": "32 GB", "label": "32 GB"},
      {"value": "96 GB", "label": "96 GB"},
      {"value": "24 GB", "label": "24 GB"}
    ]
  }
}
```

#### 3.2.2 获取地区列表

**请求信息**
- 路径: `/pc/gpu/region/list`
- 方法: `GET`
- 认证: 公开访问

**响应参数**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码 |
| msg | string | 提示信息 |
| data | array | 地区列表 |
| data[].regionCode | string | 地区编码 |
| data[].regionName | string | 地区名称 |

**响应示例**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {"regionCode": "northwest-b", "regionName": "西北B区"},
    {"regionCode": "beijing-b", "regionName": "北京B区"},
    {"regionCode": "chongqing-a", "regionName": "重庆A区"}
  ]
}
```

#### 3.2.3 获取专区列表

**请求信息**
- 路径: `/pc/gpu/zone/list`
- 方法: `GET`
- 认证: 公开访问

**响应参数**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码 |
| msg | string | 提示信息 |
| data | array | 专区列表 |
| data[].zoneCode | string | 专区编码 |
| data[].zoneName | string | 专区名称 |

**响应示例**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {"zoneCode": "v100", "zoneName": "V100专区"},
    {"zoneCode": "a800", "zoneName": "A800专区"},
    {"zoneCode": "mthreads", "zoneName": "摩尔线程专区"}
  ]
}
```

#### 3.2.4 分页查询资源列表（核心接口）

**请求信息**
- 路径: `/pc/gpu/market/page`
- 方法: `GET`
- 认证: 公开访问

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认10 |
| model | string | 否 | GPU型号筛选 |
| vram | string | 否 | 显存筛选 |
| regionCode | string | 否 | 地区编码筛选 |
| zoneCode | string | 否 | 专区编码筛选 |
| keyword | string | 否 | 关键词搜索（机器编号、型号等） |

**响应参数**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码 |
| msg | string | 提示信息 |
| data | object | 分页数据 |
| data.total | int | 总条数 |
| data.list | array | 资源列表 |
| data.list[].id | long | 资源ID |
| data.list[].resourceNo | string | 资源编号 |
| data.list[].machineId | string | 机器编号 |
| data.list[].regionName | string | 地区名称 |
| data.list[].zoneName | string | 专区名称 |
| data.list[].model | string | GPU型号 |
| data.list[].vram | string | 显存大小 |
| data.list[].architecture | string | 架构 |
| data.list[].cudaVersion | string | CUDA版本 |
| data.list[].cpuCores | int | CPU核心数 |
| data.list[].cpuModel | string | CPU型号 |
| data.list[].memorySize | string | 内存大小 |
| data.list[].systemDisk | string | 系统盘 |
| data.list[].dataDisk | string | 数据盘 |
| data.list[].expandable | string | 可扩容容量 |
| data.list[].rentableUntil | string | 可租截止日期 |
| data.list[].availableCount | int | 空闲GPU数 |
| data.list[].totalCount | int | 总GPU数 |
| data.list[].hourlyPrice | decimal | 时租价格 |
| data.list[].monthlyPrice | decimal | 月租价格 |
| data.list[].discountRate | string | 折扣率 |

**响应示例**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "resourceNo": "RES-20250513-001",
        "machineId": "D21机",
        "regionName": "西北B区",
        "zoneName": "A800专区",
        "model": "A800",
        "vram": "80 GB",
        "architecture": "Ampere",
        "cudaVersion": "12.2",
        "cpuCores": 128,
        "cpuModel": "Intel Xeon Platinum 8480+",
        "memorySize": "2048 GB",
        "systemDisk": "500 GB SSD",
        "dataDisk": "8 TB NVMe",
        "expandable": "16 TB",
        "rentableUntil": "2026-12-31",
        "availableCount": 3,
        "totalCount": 8,
        "hourlyPrice": 45.00,
        "monthlyPrice": 28000.00,
        "discountRate": "85%"
      }
    ]
  }
}
```

#### 3.2.5 获取资源详情

**请求信息**
- 路径: `/pc/gpu/market/detail/{id}`
- 方法: `GET`
- 认证: 需要登录

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 资源ID（Path参数） |

**响应参数**：同分页接口中的单条数据结构

## 4. 数据模型映射

### 4.1 数据库表关系

```
gpu_resource (主表)
    ├── gpu_resource_spec (1:1) - 规格信息
    ├── gpu_region (n:1) - 地区信息
    ├── gpu_zone (n:1) - 专区信息
    ├── gpu_resource_price (1:n) - 价格信息
    └── gpu_resource_stock (1:1) - 库存信息
```

### 4.2 VO对象设计

```java
// GpuMarketItemVO.java - 资源列表项
@Data
public class GpuMarketItemVO {
    // 基础信息
    private Long id;
    private String resourceNo;
    private String machineId;
    
    // 地区/专区
    private String regionName;
    private String zoneName;
    
    // GPU规格
    private String model;
    private String vram;
    private String architecture;
    private String cudaVersion;
    
    // 硬件配置
    private Integer cpuCores;
    private String cpuModel;
    private String memorySize;
    private String systemDisk;
    private String dataDisk;
    private String expandable;
    
    // 租期
    private String rentableUntil;
    
    // 库存
    private Integer availableCount;
    private Integer totalCount;
    
    // 价格
    private BigDecimal hourlyPrice;
    private BigDecimal monthlyPrice;
    private String discountRate;
}

// GpuMarketFilterVO.java - 筛选项
@Data
public class GpuMarketFilterVO {
    private List<OptionItem> models;
    private List<OptionItem> vrams;
    private List<RegionItem> regions;
    private List<ZoneItem> zones;
    
    @Data
    public static class OptionItem {
        private String value;
        private String label;
    }
    
    @Data
    public static class RegionItem {
        private String regionCode;
        private String regionName;
    }
    
    @Data
    public static class ZoneItem {
        private String zoneCode;
        private String zoneName;
    }
}
```

## 5. 关键SQL查询示例

### 5.1 资源列表分页查询（核心）

```sql
SELECT
    gr.id,
    gr.resource_no,
    gr.machine_id,
    gre.region_name,
    gz.zone_name,
    grs.model,
    grs.vram,
    grs.architecture,
    gr.cuda_version,
    gr.cpu_cores,
    gr.cpu_model,
    gr.memory_size,
    gr.system_disk,
    gr.data_disk,
    gr.expandable,
    gr.rentable_until,
    grst.available_count,
    grst.total_count,
    MAX(CASE WHEN grp.billing_type = 'hourly' THEN grp.unit_price END) as hourly_price,
    MAX(CASE WHEN grp.billing_type = 'monthly' THEN grp.unit_price END) as monthly_price,
    MAX(CASE WHEN grp.billing_type = 'hourly' THEN grp.discount_rate END) as discount_rate
FROM gpu_resource gr
LEFT JOIN gpu_region gre ON gr.region_code = gre.region_code
LEFT JOIN gpu_zone gz ON gr.zone_code = gz.zone_code
LEFT JOIN gpu_resource_spec grs ON gr.spec_id = grs.id
LEFT JOIN gpu_resource_stock grst ON gr.id = grst.resource_id
LEFT JOIN gpu_resource_price grp ON gr.id = grp.resource_id
WHERE gr.del_flag = 0
  AND gr.status = 1
  AND (#{model} IS NULL OR grs.model = #{model})
  AND (#{vram} IS NULL OR grs.vram = #{vram})
  AND (#{regionCode} IS NULL OR gr.region_code = #{regionCode})
  AND (#{zoneCode} IS NULL OR gr.zone_code = #{zoneCode})
  AND (#{keyword} IS NULL OR gr.machine_id LIKE CONCAT('%', #{keyword}, '%') OR grs.model LIKE CONCAT('%', #{keyword}, '%'))
GROUP BY gr.id
ORDER BY gr.sort_order ASC, gr.create_time DESC
```

### 5.2 所需索引

```sql
-- 资源列表查询优化索引
CREATE INDEX idx_gpu_resource_status ON gpu_resource(status);
CREATE INDEX idx_gpu_resource_region ON gpu_resource(region_code);
CREATE INDEX idx_gpu_resource_zone ON gpu_resource(zone_code);
CREATE INDEX idx_gpu_resource_spec ON gpu_resource(spec_id);
CREATE INDEX idx_gpu_price_resource ON gpu_resource_price(resource_id, billing_type);
CREATE INDEX idx_gpu_stock_resource ON gpu_resource_stock(resource_id);
```

## 6. 接口开发任务分解

### 6.1 后端开发任务

| 序号 | 任务 | 文件路径 | 说明 |
|------|------|----------|------|
| 1 | 新增VO对象 | `model/vo/pc/GpuMarketItemVO.java` | 资源列表项VO |
| 2 | 新增VO对象 | `model/vo/pc/GpuMarketFilterVO.java` | 筛选项VO |
| 3 | 新增QueryParam | `model/dto/GpuMarketQueryParam.java` | 列表查询参数 |
| 4 | 修改Controller | `api/controller/pc/PcGpuMarketController.java` | 新增5个接口 |
| 5 | 修改Service | `service/GpuResourceService.java` | 新增业务方法 |
| 6 | 实现Service | `service/impl/GpuResourceServiceImpl.java` | 实现业务逻辑 |
| 7 | 修改Mapper | `mapper/GpuResourceMapper.java` | 新增查询方法 |
| 8 | 编写XML | `mapper/GpuResourceMapper.xml` | 复杂SQL |

### 6.2 前端开发任务

| 序号 | 任务 | 说明 |
|------|------|------|
| 1 | API接口定义 | 新增API调用方法 |
| 2 | 筛选项动态化 | 替换静态筛选项为接口数据 |
| 3 | 资源列表对接 | 替换静态卡片为接口数据 |
| 4 | 加载状态 | 添加loading、空状态 |
| 5 | 分页功能 | 分页组件对接 |
| 6 | 错误处理 | 接口失败处理 |

## 7. 前后端交互流程

### 7.1 页面初始化流程

```
前端 -> 后端: GET /pc/gpu/spec/options
后端 --> 前端: GPU型号+显存列表
前端 -> 后端: GET /pc/gpu/region/list
后端 --> 前端: 地区列表
前端 -> 后端: GET /pc/gpu/zone/list
后端 --> 前端: 专区列表
前端 -> 前端: 渲染筛选区域
```

### 7.2 资源列表查询流程

```
用户 -> 前端: 选择筛选条件/关键词
前端 -> 后端: GET /pc/gpu/market/page?page=1&size=10&model=xxx
后端 -> 数据库: 多表联查（resource+spec+region+price+stock）
数据库 --> 后端: 查询结果
后端 --> 前端: 分页数据（含价格库存）
前端 -> 前端: 渲染资源卡片列表
```

## 8. 安全性设计

### 8.1 接口权限

| 接口 | 权限要求 | 说明 |
|------|----------|------|
| 筛选项接口 | 公开访问 | 无需登录 |
| 资源列表接口 | 公开访问 | 无需登录 |
| 资源详情接口 | 需登录 | 租用前需登录 |

### 8.2 防护措施

- **SQL注入**：使用MyBatis参数绑定，禁止字符串拼接SQL
- **越权访问**：资源列表只返回status=1（上架）的数据
- **敏感信息**：不返回机器UUID等内部字段

## 9. 性能优化

### 9.1 数据库优化

- **索引**：在WHERE条件和JOIN字段上建立索引
- **分页**：使用LIMIT/OFFSET，避免大数据量查询
- **按需查询**：只查询页面需要的字段

### 9.2 缓存策略（后续优化）

- **筛选项缓存**：规格、地区、专区数据缓存1小时
- **资源列表缓存**：热点数据缓存5分钟
- **库存实时性**：库存不走缓存，实时查询

## 10. 部署与测试

### 10.1 测试用例

| 场景 | 预期结果 |
|------|----------|
| 无筛选条件 | 返回所有上架资源 |
| 按型号筛选 | 只返回该型号资源 |
| 按地区筛选 | 只返回该地区资源 |
| 关键词搜索 | 返回匹配机器编号/型号的资源 |
| 分页查询 | 正确返回分页数据 |
| 已下架资源 | 不出现在列表中 |

### 10.2 上线 checklist

- [ ] 接口开发完成
- [ ] 单元测试通过
- [ ] 联调测试通过
- [ ] 数据库索引已添加
- [ ] 前端对接完成
- [ ] 性能测试通过
- [ ] 上线发布

---

**文档版本**: v1.0  
**编写日期**: 2025-05-21  
**编写人**: Claude Code
