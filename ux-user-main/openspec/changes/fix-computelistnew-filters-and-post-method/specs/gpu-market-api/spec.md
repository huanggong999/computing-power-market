# Spec: gpu-market-api

## Overview

GPU 市场列表查询接口规范定义了获取 GPU 算力资源列表的 API 契约。

## Change Summary

本规范描述从 GET 请求迁移到 POST 请求的变更：
- **请求方法**: GET → POST
- **参数传递**: URL query parameters → Request body (JSON)
- **数组参数处理**: URL 序列化字符串 → JSON 数组

## API Specification

### Endpoint

```
POST /pc/gpu/market/list
```

### Request

#### Headers

| Header | Value | Required |
|--------|-------|----------|
| `Content-Type` | `application/json` | Yes |
| `Authorization` | `Bearer <token>` | No |

#### Body

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `billingType` | `string` | No | 计费方式: `hourly`, `daily`, `monthly` |
| `regionCode` | `string` | No | 地区编码 |
| `zoneCode` | `string` | No | 可用区编码 |
| `gpuModels` | `string[]` | No | GPU 型号列表 |
| `gpuCount` | `number` | No | GPU 数量 |
| `sortBy` | `string` | No | 排序字段: `price`, `vram`, `model` |
| `sortOrder` | `string` | No | 排序顺序: `asc`, `desc` |
| `pageNo` | `number` | No | 页码 (默认: 1) |
| `pageSize` | `number` | No | 每页数量 (默认: 10) |

#### Example Request Body

```json
{
  "billingType": "hourly",
  "regionCode": "cn-hangzhou",
  "gpuModels": ["A100", "H100"],
  "gpuCount": 2,
  "sortBy": "price",
  "sortOrder": "asc",
  "pageNo": 1,
  "pageSize": 20
}
```

### Response

#### Success Response (200 OK)

```typescript
{
  code: number      // 200 表示成功
  msg: string      // 消息
  data: {
    pageNo: number
    pageSize: number
    pageTotal: number
    dataTotal: number
    list: GpuMarketItem[]
  }
}
```

#### GpuMarketItem

| Field | Type | Description |
|-------|------|-------------|
| `resourceId` | `number` | 资源 ID |
| `model` | `string` | GPU 型号 |
| `vram` | `string` | 显存大小 |
| `region` | `string` | 地区名称 |
| `price` | `string` | 价格 |
| `availableCount` | `number` | 可用数量 |
| ... | ... | 其他字段 |

#### Error Response

| HTTP Status | Code | Description |
|-------------|------|-------------|
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未授权 |
| 500 | 500 | 服务器内部错误 |

## Frontend Implementation

### API Client (src/api/gpuMarket.ts)

```typescript
export function getGpuMarketList(data: {
  billingType?: string
  regionCode?: string
  zoneCode?: string
  gpuModels?: string[]
  gpuCount?: number
  sortBy?: string
  sortOrder?: string
  pageNo?: number
  pageSize?: number
}) {
  return request({
    url: '/pc/gpu/market/list',
    method: 'post',
    data,
  })
}
```

### Usage Example

```typescript
import { getGpuMarketList } from '@/api/gpuMarket'

const params = {
  billingType: 'hourly',
  regionCode: 'cn-hangzhou',
  gpuModels: ['A100'],
  sortBy: 'price',
  sortOrder: 'asc',
  pageNo: 1,
  pageSize: 10,
}

const { data } = await getGpuMarketList(params)
```

## Migration Guide

### From GET to POST

#### Before (GET)

```typescript
// Request
GET /pc/gpu/market/list?billingType=hourly&regionCode=cn-hangzhou&gpuModels=A100&gpuModels=H100

// Code
request({
  url: '/pc/gpu/market/list',
  method: 'get',
  params: { billingType, regionCode, gpuModels },
  paramsSerializer: (params) => { /* serialize arrays */ }
})
```

#### After (POST)

```typescript
// Request
POST /pc/gpu/market/list
Content-Type: application/json

{
  "billingType": "hourly",
  "regionCode": "cn-hangzhou",
  "gpuModels": ["A100", "H100"]
}

// Code
request({
  url: '/pc/gpu/market/list',
  method: 'post',
  data: { billingType, regionCode, gpuModels }
})
```

## Testing

### Unit Test Example

```typescript
describe('getGpuMarketList', () => {
  it('should call POST /pc/gpu/market/list', async () => {
    const mockData = {
      billingType: 'hourly',
      regionCode: 'cn-hangzhou',
    }

    await getGpuMarketList(mockData)

    expect(request).toHaveBeenCalledWith({
      url: '/pc/gpu/market/list',
      method: 'post',
      data: mockData,
    })
  })
})
```

## References

- [Axios Request Config](https://axios-http.com/docs/req_config)
- [REST API Design Best Practices](https://restfulapi.net/)
