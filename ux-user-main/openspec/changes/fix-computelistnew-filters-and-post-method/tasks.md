# Tasks: fix-computelistnew-filters-and-post-method

## Task List

### Task 1: 修改 GPU 市场列表 API 为 POST 请求

**目标**：将 `src/api/gpuMarket.ts` 中的 `getGpuMarketList` 从 GET 改为 POST 请求

**步骤**：
1. 打开 `src/api/gpuMarket.ts`
2. 修改 `getGpuMarketList` 函数：
   - 将 `method: 'get'` 改为 `method: 'post'`
   - 将 `params` 改为 `data`
   - 移除 `paramsSerializer`（POST 请求不需要）
3. 验证类型定义是否需要调整（`params` 参数名改为 `data`）

**验证**：
- 函数签名正确，类型无误
- 请求方法为 POST
- 参数通过 request body 传递

**文件**：`src/api/gpuMarket.ts`

**行号参考**：第 90-118 行

---

### Task 2: 验证页面参数传递正确性

**目标**：确保 `src/view/computeList/new.vue` 中的参数结构与后端期望一致

**步骤**：
1. 打开 `src/view/computeList/new.vue`
2. 检查 `loadGpuList` 函数中的 `params` 对象（约第 101-111 行）
3. 验证参数映射关系：
   - `filters.billingType` → `billingType` ✓
   - `filters.region` → `regionCode` ✓
   - `filters.zone` → `zoneCode` ✓
   - `filters.gpuModel` → `gpuModels` ✓
   - `filters.gpuCount` → `gpuCount` ✓
4. 确保空值处理正确（使用 `undefined` 而不是空字符串或空数组）

**验证**：
- 参数名与后端 API 约定一致
- 数组参数正确处理（`gpuModels`）
- 空值过滤正确

**文件**：`src/view/computeList/new.vue`

**行号参考**：第 97-126 行

---

### Task 3: 测试验证过滤条件

**目标**：在本地验证过滤条件是否生效

**步骤**：
1. 启动开发服务器：`npm run dev`
2. 访问 `http://localhost:4173/#/computeListNew`
3. 测试以下过滤条件：
   - 计费方式切换（按时/按天/按月）
   - 地区选择
   - GPU 型号多选
   - GPU 数量选择
4. 验证每次筛选后：
   - 网络请求是否为 POST 方法
   - 请求体是否包含正确的参数
   - 列表数据是否正确过滤

**验证**：
- 所有过滤条件都能正确触发请求
- POST 请求体参数正确
- 列表数据按条件正确过滤

**文件**：无（验证步骤）

---

### Task 4: 检查 GPU 市场元数据接口（可选）

**目标**：确认元数据接口是否需要修改

**步骤**：
1. 检查 `src/api/gpuMarket.ts` 中的 `getGpuMarketMeta` 函数
2. 确认是否需要改为 POST：
   - 如果该接口用于获取筛选项元数据且不涉及敏感参数，可以保持 GET
   - 如果也需要改为 POST，按 Task 1 相同方式修改

**当前状态**：
- `getGpuMarketMeta` 使用 GET 请求，获取地区、可用区、GPU型号等元数据
- 该接口一般不需要改为 POST（无副作用的查询）

**建议**：保持 GET 不变，只修改列表查询接口

**文件**：`src/api/gpuMarket.ts`

**行号参考**：第 76-84 行

---

## 执行顺序

1. **Task 1**: 修改 API 请求方式为 POST（必须）
2. **Task 2**: 验证页面参数传递（检查，可能无需修改）
3. **Task 3**: 测试验证（必须，验证修复效果）
4. **Task 4**: 检查元数据接口（可选，建议保持 GET）

## 代码变更摘要

### 变更前（GET 请求）

```typescript
// src/api/gpuMarket.ts
export function getGpuMarketList(params: {...}) {
  return request({
    url: '/pc/gpu/market/list',
    method: 'get',
    params,
    paramsSerializer: {...}
  })
}
```

### 变更后（POST 请求）

```typescript
// src/api/gpuMarket.ts
export function getGpuMarketList(data: {...}) {
  return request({
    url: '/pc/gpu/market/list',
    method: 'post',
    data,
  })
}
```
