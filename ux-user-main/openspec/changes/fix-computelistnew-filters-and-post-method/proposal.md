# Proposal: fix-computelistnew-filters-and-post-method

## Why

算力市场列表页面（computeListNew）存在两个问题需要修复：
1. 当前使用 GET 方式请求后端接口，需要改为 POST 方式以满足后端接口规范
2. 计费方式、地区、GPU型号、GPU数量等过滤条件当前不生效，需要修复参数传递逻辑

## What Changes

1. **修改 API 请求方式**：将 `src/api/gpuMarket.ts` 中的 `getGpuMarketList` 从 GET 改为 POST 请求
   - 将 `method: 'get'` 改为 `method: 'post'`
   - 将 `params` 改为 `data` 作为请求体
   - 移除 `paramsSerializer`（POST 请求不需要）

2. **修复过滤条件传递**：确保 `src/view/computeList/new.vue` 中的参数结构正确传递给后端
   - 验证参数名映射：`billingType`, `regionCode`, `zoneCode`, `gpuModels`, `gpuCount`
   - 确保空值使用 `undefined` 而不是空字符串或空数组
   - 确保数组参数（`gpuModels`）正确传递

## Capabilities

### New Capabilities
- 无新增功能，仅修复现有功能

### Modified Capabilities
- **gpu-market-api**: GPU 市场列表查询接口从 GET 改为 POST，参数传递方式调整
  - 请求方法变更：GET → POST
  - 参数传递变更：URL query → Request body
  - 数组参数处理：序列化字符串 → JSON 数组

## Impact

### 前端代码
- `src/api/gpuMarket.ts` - `getGpuMarketList` 函数实现修改
- `src/view/computeList/new.vue` - 验证参数传递逻辑（可能无需修改）

### 后端接口
- 需要后端 `/pc/gpu/market/list` 接口支持 POST 方法
- 后端需要从 request body 中解析参数而非 URL query

### 依赖
- 无新增依赖
- 依赖现有的 axios 和 Element Plus

### 风险评估
- **低风险**：仅修改请求方式和参数传递，不涉及业务逻辑变更
- **依赖后端**：需要后端配合支持 POST 接口
- **回退方案**：如 POST 遇到问题，可回退到 GET 但需后端配合
