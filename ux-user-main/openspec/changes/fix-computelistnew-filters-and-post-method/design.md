# Design: fix-computelistnew-filters-and-post-method

## 变更概述

修复算力市场列表页面（computeListNew）的两个核心问题：
1. 后台接口从 GET 改为 POST 方式请求
2. 修复过滤条件不生效的问题（计费方式、地区、GPU型号、GPU数量）

## 问题分析

### 当前实现问题

#### 1. 请求方式问题

当前 `src/api/gpuMarket.ts` 中的 `getGpuMarketList` 函数使用 GET 请求：

```typescript
export function getGpuMarketList(params: {...}) {
  return request({
    url: '/pc/gpu/market/list',
    method: 'get',
    params,  // GET 参数
    paramsSerializer: {...}  // 序列化数组参数
  })
}
```

需要改为 POST 请求，将参数放在请求体（body）中。

#### 2. 过滤条件问题

当前 `src/view/computeList/new.vue` 中的过滤条件通过 `watch` 监听，自动触发 `loadGpuList`。问题可能出在：

- 参数名称与后端约定不一致
- 参数传递方式不正确（GET query vs POST body）

### 涉及的文件

1. `src/api/gpuMarket.ts` - API 接口定义
2. `src/view/computeList/new.vue` - 页面组件

## 实现方案

### 方案一：将 GET 改为 POST（推荐）

#### 修改 `src/api/gpuMarket.ts`

将 `getGpuMarketList` 从 GET 改为 POST：

```typescript
export function getGpuMarketList(data: {...}) {
  return request({
    url: '/pc/gpu/market/list',
    method: 'post',
    data,  // POST body
    // 移除 paramsSerializer，POST 不需要
  })
}
```

#### 修改 `src/view/computeList/new.vue`

确保参数结构与后端期望一致：

```typescript
const params = {
  billingType: filters.billingType,
  regionCode: filters.region || undefined,
  zoneCode: filters.zone || undefined,
  gpuModels: filters.gpuModel.length > 0 ? filters.gpuModel : undefined,
  gpuCount: filters.gpuCount || undefined,
  sortBy: sortBy.value,
  sortOrder: sortOrder.value,
  pageNo: currentPage.value,
  pageSize: pageSize.value,
}
```

### 技术细节

#### Axios GET vs POST

**GET 请求**：
- 参数通过 `params` 传递
- 参数附加在 URL 查询字符串中
- 需要 `paramsSerializer` 处理数组参数

**POST 请求**：
- 参数通过 `data` 传递
- 参数放在请求体中
- Content-Type 自动设置为 `application/json`
- 数组参数直接传递，无需特殊序列化

#### 参数命名约定

确保前端参数名与后端期望的一致：

| 前端过滤条件 | 前端参数名 | 后端期望参数名 |
|------------|----------|--------------|
| 计费方式 | billingType | billingType |
| 地区 | region | regionCode |
| 可用区 | zone | zoneCode |
| GPU型号 | gpuModel | gpuModels |
| GPU数量 | gpuCount | gpuCount |

## 风险与回退方案

### 风险

1. **后端 API 兼容性**：如果后端尚未支持 POST，需要后端配合修改
2. **参数格式变化**：GET 和 POST 的参数传递方式不同，需要确保格式正确

### 回退方案

如果 POST 方式遇到问题，可以回退到 GET 方式，但需要后端支持 CORS 和 URL 长度限制。

## 测试验证

### 单元测试

验证 API 函数：
1. 调用 `getGpuMarketList` 使用 POST 方式
2. 验证请求体包含正确参数

### 集成测试

验证页面功能：
1. 打开算力市场页面
2. 选择过滤条件（计费方式、地区、GPU型号、GPU数量）
3. 验证列表数据正确过滤
4. 验证分页功能正常

## 实施步骤

1. **修改 API 层**：`src/api/gpuMarket.ts` - 将 GET 改为 POST
2. **验证页面层**：`src/view/computeList/new.vue` - 确保参数传递正确
3. **联调测试**：与后端联调，验证接口正常工作
4. **回归测试**：确保其他功能不受影响
