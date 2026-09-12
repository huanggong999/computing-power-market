# GPU启用地区信息接口文档

## 文档元数据

- **文档标识**: SYS-API-GPU启用地区
- **文档版本**: v1.0.0
- **创建日期**: 2026-08-11
- **文档状态**: 发布
- **文档分类**: 系统设计类
- **文档标签**:
  - 分类标签: SYS
  - 系统标签: [AI算力中心]
  - 模块标签: [GPU资源管理, GPU地区管理]
  - 业务域标签: [算力市场, 资源配置]
  - 关键词标签: [GPU地区, 启用地区, region, 接口文档]

## 1. 接口概述

本文档用于说明 `ai-cloud-system` 项目中可提供给调用方获取 GPU 启用地区信息的接口。

当前项目中地区数据由 `gpu_region` 表维护，核心字段包括地区编码、地区名称、排序值和启用状态。后台管理端已有只读列表接口，可直接返回所有启用且未删除的 GPU 地区。

## 2. 推荐调用接口

### 2.1 查询启用 GPU 地区列表

#### 基本信息

| 项目 | 内容 |
|------|------|
| 接口名称 | 查询启用 GPU 地区列表 |
| 接口路径 | `/system/gpu/region/list` |
| 请求方法 | `GET` |
| Content-Type | `application/json` |
| 是否分页 | 否 |
| 是否需要请求参数 | 否 |
| 当前鉴权 | 走系统管理端默认鉴权，调用方需携带有效登录态或访问令牌 |
| 数据来源 | `gpu_region` 表 |

#### 请求地址

开发环境示例：

```http
GET http://{host}:8081/system/gpu/region/list
```

生产环境示例：

```http
GET http://{host}:8082/system/gpu/region/list
```

说明：项目未配置统一 `context-path`，实际域名、网关前缀、端口以部署环境为准。

#### 请求头

| Header | 必填 | 示例 | 说明 |
|--------|------|------|------|
| Authorization | 是 | `Bearer {token}` | 当前接口属于系统管理端接口，默认需要认证 |
| Content-Type | 否 | `application/json` | GET 请求无请求体，可不传 |

#### 请求参数

无。

#### 响应字段

通用响应结构：

| 字段 | 类型 | 必返 | 说明 |
|------|------|------|------|
| code | integer | 是 | 响应状态码，`200` 表示成功 |
| msg | string | 是 | 响应消息，成功时为 `成功` |
| requestId | integer | 否 | 请求链路 ID |
| data | array | 是 | 启用地区列表 |

`data[]` 字段结构：

| 字段 | 类型 | 必返 | 示例 | 说明 |
|------|------|------|------|------|
| id | integer | 是 | `1` | 地区主键 ID |
| regionCode | string | 是 | `northwest-b` | 地区编码，调用方下单、筛选时应使用该值 |
| regionName | string | 是 | `西北B区` | 地区展示名称 |
| sortOrder | integer | 否 | `1` | 排序值，值越小越靠前 |
| status | integer | 是 | `1` | 状态，`1` 启用，`0` 禁用；本接口只返回启用数据 |

#### 成功响应示例

```json
{
  "requestId": 1000000001,
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "regionCode": "northwest-b",
      "regionName": "西北B区",
      "sortOrder": 1,
      "status": 1
    },
    {
      "id": 2,
      "regionCode": "beijing-b",
      "regionName": "北京B区",
      "sortOrder": 2,
      "status": 1
    }
  ]
}
```

#### 空数据响应示例

```json
{
  "requestId": 1000000002,
  "code": 200,
  "msg": "成功",
  "data": []
}
```

#### 常见错误码

| code | msg | 场景说明 | 处理建议 |
|------|-----|----------|----------|
| 401 | 未登录 | 未携带有效登录态或 token 已过期 | 重新获取 token 后重试 |
| 403 | 权限不足 | 当前账号无接口访问权限 | 联系管理员配置权限 |
| 404 | 请求地址不存在 | 环境地址、网关前缀或接口路径错误 | 核对部署地址和路由 |
| 500 | 服务器繁忙，请稍后再试 | 服务异常或数据库异常 | 稍后重试，必要时联系服务方排查日志 |

## 3. 数据过滤规则

接口内部调用 `GpuRegionService.listAllEnabled()`，当前过滤规则如下：

| 规则 | 说明 |
|------|------|
| `status = 1` | 只返回启用地区 |
| `del_flag = 0` | 只返回未删除地区 |
| `ORDER BY sort_order ASC` | 按排序值升序返回 |

调用方应以 `regionCode` 作为业务交互编码，不建议依赖 `id` 做跨系统传递。

## 4. 可选关联接口

### 4.1 查询 GPU 市场筛选元数据

#### 基本信息

| 项目 | 内容 |
|------|------|
| 接口名称 | 查询 GPU 市场筛选元数据 |
| 接口路径 | `/pc/gpu/market/meta` |
| 请求方法 | `GET` |
| 当前鉴权 | `permitAll`，当前代码允许匿名访问 |
| 主要用途 | 给 PC 算力市场页面提供筛选项 |

#### 响应中的地区字段

```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "regions": [
      {
        "regionCode": "northwest-b",
        "regionName": "西北B区"
      }
    ],
    "zones": [
      {
        "zoneCode": "a800",
        "zoneName": "A800专区"
      }
    ],
    "gpuModels": [],
    "gpuCounts": [8],
    "billingTypes": []
  }
}
```

#### 使用限制

该接口不是纯地区接口，它会先查询已上架的 GPU 市场资源，再从这些资源中提取地区编码并补充地区名称。因此：

| 差异点 | `/system/gpu/region/list` | `/pc/gpu/market/meta` |
|--------|----------------------------|------------------------|
| 返回范围 | 所有启用且未删除的地区 | 有上架市场资源关联到的地区 |
| 返回字段 | `id`、`regionCode`、`regionName`、`sortOrder`、`status` | `regionCode`、`regionName` |
| 鉴权 | 默认需要系统认证 | 当前允许匿名访问 |
| 推荐用途 | 对外提供启用地区主数据 | 前端市场筛选条件 |

如果调用方明确需要“所有启用地区主数据”，应优先使用 `/system/gpu/region/list`；如果调用方只需要“当前市场可筛选的地区”，可使用 `/pc/gpu/market/meta`。

## 5. 对外提供建议

当前最贴近需求的现有接口是 `/system/gpu/region/list`，但它属于系统管理端接口。若调用方是外部系统、前台系统或不方便接入管理端登录态，建议后续封装一个专用只读接口，例如：

```http
GET /pc/gpu/regions/enabled
```

建议封装后的响应只暴露必要字段：

```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "regionCode": "northwest-b",
      "regionName": "西北B区",
      "sortOrder": 1
    }
  ]
}
```

封装接口建议保留与现有 `GpuRegionService.listAllEnabled()` 一致的数据过滤规则，即 `status = 1`、`del_flag = 0`、按 `sort_order` 升序排序。

## 6. 调用示例

### 6.1 cURL

```bash
curl -X GET 'http://{host}:8081/system/gpu/region/list' \
  -H 'Authorization: Bearer {token}'
```

### 6.2 JavaScript

```javascript
async function fetchEnabledGpuRegions(token) {
  const response = await fetch('http://{host}:8081/system/gpu/region/list', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
  const result = await response.json();
  if (result.code !== 200) {
    throw new Error(result.msg || '查询GPU地区失败');
  }
  return result.data || [];
}
```

## 7. 实现依据

| 类型 | 文件 | 说明 |
|------|------|------|
| Controller | `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/system/SystemGpuResourceController.java` | `/system/gpu/region/list` 接口定义 |
| Service | `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuRegionServiceImpl.java` | 启用地区查询和过滤逻辑 |
| VO | `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/model/vo/system/GpuRegionVO.java` | 地区返回字段定义 |
| Entity | `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/entity/GpuRegionEntity.java` | `gpu_region` 表字段映射 |
| 可选接口 | `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/PcGpuMarketController.java` | `/pc/gpu/market/meta` 市场元数据接口 |

## 8. 变更记录

| 版本 | 日期 | 变更说明 | 作者 |
|------|------|----------|------|
| v1.0.0 | 2026-08-11 | 初版，整理 GPU 启用地区现有接口、响应字段和对外调用建议 | Codex |
