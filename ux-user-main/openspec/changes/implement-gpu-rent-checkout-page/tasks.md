# Tasks: implement-gpu-rent-checkout-page

## 任务列表

### Task 1: 创建租用相关 API 模块

**目标**: 创建 `src/api/computeRent.ts`，定义租用确认页面需要的所有 API 接口

**步骤**:
1. 创建文件 `src/api/computeRent.ts`
2. 定义 TypeScript 类型接口：
   - `MirrorItem` - 镜像信息
   - `MirrorVersion` - 镜像版本
   - `AccountInfo` - 账户信息
   - `CalculateFeeParams` / `FeeResult` - 费用计算
   - `CreateOrderParams` / `OrderResult` - 订单创建
3. 实现 API 函数：
   - `getGpuResourceDetail(resourceId)` - 获取 GPU 详情
   - `getMirrorList(params)` - 获取镜像列表
   - `getMirrorVersions(mirrorId)` - 获取镜像版本
   - `getAccountInfo()` - 获取账户信息
   - `calculateRentFee(params)` - 计算费用
   - `createRentOrder(params)` - 创建订单

**文件**: `src/api/computeRent.ts`

**依赖**: 需要 `src/utils/request.ts` 的 request 函数

---

### Task 2: 修改列表页跳转逻辑

**目标**: 修改 `src/view/computeList/new.vue`，点击租用按钮时跳转到租用确认页面

**步骤**:
1. 导入 `useRouter` from 'vue-router'
2. 在 `setup` 中获取 `router` 实例
3. 修改 `handleRent` 方法：
   ```typescript
   function handleRent(gpu: GpuMarketItem) {
     router.push({
       path: '/computeRent',
       query: {
         resourceId: gpu.resourceId.toString()
       }
     })
   }
   ```

**文件**: `src/view/computeList/new.vue`

**修改位置**: 约第 173-175 行

---

### Task 3: 添加路由配置

**目标**: 在 `src/routes/index.ts` 中添加租用确认页面的路由

**步骤**:
1. 在静态路由列表中添加：
   ```typescript
   {
     path: '/computeRent',
     name: 'computeRent',
     component: () => import('@/view/computeRent/index.vue'),
     meta: {
       title: '确认租用',
       requiresAuth: true
     }
   }
   ```
2. 确保路由配置在正确的位置（与其他 Home 子路由放在一起）

**文件**: `src/routes/index.ts`

---

### Task 4: 创建租用确认页面主组件

**目标**: 创建 `src/view/computeRent/index.vue`，实现完整的租用确认页面

**步骤**:

1. **创建页面文件** `src/view/computeRent/index.vue`

2. **模板结构**:
   ```vue
   <template>
     <div class="compute-rent-page">
       <!-- 页面标题 -->
       <div class="page-header">
         <el-button link @click="goBack">
           <el-icon><ArrowLeft /></el-icon>
           返回
         </el-button>
         <h1>确认租用</h1>
       </div>
       
       <!-- 错误提示 -->
       <el-alert
         v-if="error"
         :title="error"
         type="error"
         closable
         @close="error = ''"
       />
       
       <!-- 主要内容区 -->
       <div v-loading="loading" class="main-content">
         <template v-if="gpuResource">
           <div class="content-grid">
             <!-- 左侧：配置信息 -->
             <div class="left-column">
               <GpuConfigCard :gpu="gpuResource" />
             </div>
             
             <!-- 右侧：租用设置 -->
             <div class="right-column">
               <RentSettingsPanel
                 v-model="rentConfig"
                 :gpu-model="gpuResource.model"
                 :account-info="accountInfo"
                 @fee-change="updateFee"
               />
             </div>
           </div>
           
           <!-- 底部：费用汇总和提交 -->
           <RentSubmitPanel
             :fee-result="feeResult"
             :loading="submitting"
             @submit="handleSubmit"
           />
         </template>
       </div>
     </div>
   </template>
   ```

3. **Script 实现**:
   - 使用 Composition API
   - 从 URL query 获取 `resourceId`
   - 加载 GPU 详情、账户信息、镜像列表
   - 监听配置变化，实时计算费用
   - 提交订单处理

4. **样式**: 
   - 使用 SCSS
   - 响应式布局（大屏左右两栏，小屏堆叠）
   - 参考现有项目样式规范

**文件**: `src/view/computeRent/index.vue`

**依赖**:
- `src/api/computeRent.ts` - API 接口
- `src/api/gpuMarket.ts` - GPU 类型定义
- `src/components/GpuConfigCard/index.vue` - GPU 配置卡片（可复用或内嵌）
- `src/components/RentSettingsPanel/index.vue` - 租用设置面板
- `src/components/RentSubmitPanel/index.vue` - 提交面板

---

### Task 5: 创建子组件（可选拆分）

**目标**: 将租用确认页面拆分为可复用的子组件

**子组件列表**:

#### 5.1 GpuConfigCard - GPU 配置信息卡片

**文件**: `src/components/GpuConfigCard/index.vue`

**Props**:
```typescript
interface GpuConfigCardProps {
  gpu: GpuMarketItem
}
```

**内容**:
- GPU 型号、显存（标题突出显示）
- 地区、可用区
- 机器 ID、UUID
- CPU、内存、硬盘配置列表
- CUDA 版本、驱动版本

---

#### 5.2 MirrorSelector - 镜像选择器

**文件**: `src/components/MirrorSelector/index.vue`

**Props**:
```typescript
interface MirrorSelectorProps {
  gpuModel: string  // 用于过滤支持的镜像
  modelValue: {
    mirrorId: string
    versionId: string
  }
}
```

**内容**:
- 镜像类型标签（PyTorch、TensorFlow、Miniconda、Other）
- 镜像卡片列表（图标+名称+描述）
- 版本选择下拉框

---

#### 5.3 BillingSelector - 计费方式选择器

**文件**: `src/components/BillingSelector/index.vue` （可内联）

**Props**:
```typescript
interface BillingSelectorProps {
  modelValue: 'hourly' | 'daily' | 'monthly'
  unitPrice: {
    hourly: number
    daily: number
    monthly: number
  }
}
```

---

#### 5.4 FeeCalculator - 费用计算器

**文件**: `src/components/FeeCalculator/index.vue` （可内联）

**Props**:
```typescript
interface FeeCalculatorProps {
  unitPrice: number
  quantity: number
  duration?: number  // 默认 1
}
```

**计算逻辑**:
- 小计 = 单价 × 数量 × 时长
- 折扣 = 根据会员折扣计算
- 总计 = 小计 - 折扣

---

### Task 6: 实现 API 接口

**目标**: 实现 `src/api/computeRent.ts` 中的所有 API 函数

**步骤**:

1. **定义类型接口**:
   ```typescript
   export interface MirrorItem {
     id: string
     name: string
     type: 'pytorch' | 'tensorflow' | 'miniconda' | 'other'
     icon: string
     description: string
     supportedGpuModels: string[]
   }
   
   export interface MirrorVersion {
     id: string
     mirrorId: string
     version: string
     cudaVersion: string
     pythonVersion: string
     description: string
     frameworks?: {
       pytorch?: string
       tensorflow?: string
       [key: string]: string | undefined
     }
   }
   
   export interface AccountInfo {
     userId: number
     username: string
     balance: number
     frozenAmount: number
     availableBalance: number
   }
   
   export interface CalculateFeeParams {
     resourceId: number
     billingType: 'hourly' | 'daily' | 'monthly'
     quantity: number
     duration?: number
   }
   
   export interface FeeResult {
     unitPrice: number
     quantity: number
     duration: number
     subtotal: number
     discount: number
     total: number
   }
   
   export interface CreateOrderParams {
     resourceId: number
     mirrorId: string
     mirrorVersionId: string
     billingType: 'hourly' | 'daily' | 'monthly'
     quantity: number
     duration?: number
     agreeProtocol: boolean
   }
   
   export interface OrderResult {
     orderId: string
     orderNo: string
     status: 'pending' | 'paid' | 'processing' | 'completed'
     totalAmount: number
     paidAmount: number
     createTime: string
     instanceInfo?: {
       instanceId: string
       instanceName: string
       status: string
     }
   }
   ```

2. **实现 API 函数**:
   ```typescript
   import request from '@/utils/request'
   
   // 获取 GPU 资源详情
   export function getGpuResourceDetail(resourceId: number) {
     return request({
       url: `/pc/gpu/resource/${resourceId}`,
       method: 'get'
     })
   }
   
   // 获取镜像列表
   export function getMirrorList(params?: { gpuModel?: string }) {
     return request({
       url: '/pc/gpu/mirror/list',
       method: 'get',
       params
     })
   }
   
   // 获取镜像版本
   export function getMirrorVersions(mirrorId: string) {
     return request({
       url: `/pc/gpu/mirror/${mirrorId}/versions`,
       method: 'get'
     })
   }
   
   // 获取用户账户信息
   export function getAccountInfo() {
     return request({
       url: '/pc/account/info',
       method: 'get'
     })
   }
   
   // 计算租用费用
   export function calculateRentFee(data: CalculateFeeParams) {
     return request({
       url: '/pc/gpu/rent/calculate',
       method: 'post',
       data
     })
   }
   
   // 创建租用订单
   export function createRentOrder(data: CreateOrderParams) {
     return request({
       url: '/pc/gpu/rent/order',
       method: 'post',
       data
     })
   }
   ```

**文件**: `src/api/computeRent.ts`

---

### Task 7: 添加路由配置

**目标**: 在路由配置中添加租用确认页面的路由

**步骤**:

1. 打开 `src/routes/index.ts`
2. 在 `home` 路由的 `children` 中添加：
   ```typescript
   {
     path: '/computeRent',
     name: 'computeRent',
     component: () => import('@/view/computeRent/index.vue'),
     meta: {
       title: '确认租用',
       requiresAuth: true
     }
   }
   ```
3. 确保路由顺序合理（可以放在 `computeListNew` 之后）

**文件**: `src/routes/index.ts`

---

### Task 8: 修改列表页租用按钮逻辑

**目标**: 修改 `src/view/computeList/new.vue` 中的 `handleRent` 方法，实现页面跳转

**步骤**:

1. 导入 `useRouter`：
   ```typescript
   import { useRouter } from 'vue-router'
   ```

2. 在 `setup` 中获取 router：
   ```typescript
   const router = useRouter()
   ```

3. 修改 `handleRent` 方法：
   ```typescript
   function handleRent(gpu: GpuMarketItem) {
     router.push({
       path: '/computeRent',
       query: {
         resourceId: gpu.resourceId.toString()
       }
     })
   }
   ```

4. 移除或注释掉原来的 `ElMessage.success` 调用

**文件**: `src/view/computeList/new.vue`

**修改位置**: script 部分，约第 173-175 行

---

### Task 9: 创建租用确认页面主体

**目标**: 创建 `src/view/computeRent/index.vue`，实现完整的租用确认页面

**步骤**:

1. **创建文件** `src/view/computeRent/index.vue`

2. **编写模板**（结构参考设计文档）

3. **编写 Script**：
   - 从 URL query 获取 `resourceId`
   - 加载 GPU 详情、账户信息、镜像列表
   - 响应式数据管理
   - 费用计算
   - 订单提交

4. **编写样式**：
   - SCSS 格式
   - 响应式布局
   - 与项目现有样式一致

**文件**: `src/view/computeRent/index.vue`

**依赖**:
- `src/api/computeRent.ts`
- `src/api/gpuMarket.ts` (类型定义)
- Element Plus 组件

---

### Task 10: 测试验证

**目标**: 完成开发后进行全面测试

**测试项**:

1. **页面加载测试**:
   - 从列表页点击租用，正确跳转到确认页
   - URL 参数正确传递
   - 页面数据加载正常

2. **功能测试**:
   - 镜像选择正常
   - 版本选择正常
   - 计费方式切换正常
   - 数量选择正常
   - 费用实时计算正确

3. **边界情况测试**:
   - 余额不足提示
   - 库存不足提示
   - 未登录跳转登录
   - 无效资源 ID 处理

4. **提交测试**:
   - 订单创建成功跳转
   - 参数校验失败提示
   - 网络错误处理

**测试方法**:
- 手动功能测试
- 查看浏览器 Network 面板确认 API 调用
- 查看 Console 是否有报错

---

## 执行顺序

按照依赖关系，建议按以下顺序执行：

1. **Task 1**: 创建 API 模块（基础依赖）
2. **Task 2**: 修改列表页跳转（可并行）
3. **Task 3**: 添加路由配置（可并行）
4. **Task 9**: 创建租用确认页面主体（依赖 Task 1）
5. **Task 10**: 测试验证（依赖前面所有任务）

**可选优化**（在主体功能完成后）：
- 子组件封装（如果页面逻辑复杂）
- 性能优化
- 更多边界情况处理

---

## 文件清单

### 新建文件
1. `src/api/computeRent.ts` - API 模块
2. `src/view/computeRent/index.vue` - 租用确认页面

### 修改文件
1. `src/view/computeList/new.vue` - 修改 handleRent 方法
2. `src/routes/index.ts` - 添加路由

### 可选新建（复杂时拆分）
1. `src/components/GpuConfigCard/index.vue` - GPU 配置卡片
2. `src/components/MirrorSelector/index.vue` - 镜像选择器
3. `src/components/FeeCalculator/index.vue` - 费用计算器

---

## AutoDL 对齐补充任务

参考 `autodl-alignment.md` 中的差异说明继续实施。目标不是简单美化当前确认页，而是把页面信息架构调整为 AutoDL 风格的“创建实例”流程。

### Task 11: 调整页面骨架为创建实例流程

**目标**: 将当前“确认租用”页调整为 AutoDL 式创建实例表单。

**步骤**:
1. 增加面包屑：`算力市场 / 创建实例`。
2. 将标题语义从“确认租用”调整为“创建实例/创建并开机”流程。
3. 保留返回能力，但弱化为辅助导航。

**文件**: `src/view/computeRent/index.vue`

---

### Task 12: 对齐计费方式区域

**目标**: 计费方式区域与 AutoDL 保持一致。

**步骤**:
1. 将 `hourly` 展示为“按量计费”。
2. 补齐 `weekly` 包周选项。
3. 保留包日、包月。
4. 增加“计费规则”链接。
5. 增加计费说明文案：创建完主机后仍然可以转换计费方式；按量计费价格以实例开机时为准。
6. 切换计费方式时刷新价格、费用明细和提交参数。

**文件**:
- `src/view/computeRent/index.vue`
- `src/api/computeRent.ts`

---

### Task 13: 增加选择主机表格

**目标**: 用 AutoDL 式“选择主机”表格替代当前分散配置卡片作为主选择区。

**步骤**:
1. 增加主机表格，当前 `resourceId` 对应主机默认选中。
2. 表格列包含：主机 ID、算力型号/显存、空闲 GPU、每 GPU 分配、CPU 型号、硬盘、驱动/CUDA、价格（单卡）。
3. 支持点击行切换选中主机。
4. 切换主机后联动 GPU 数量、实例规格、镜像可选项、费用。
5. 价格列按当前计费方式显示单位。

**文件**:
- `src/view/computeRent/index.vue`
- `src/api/computeRent.ts`
- 后端创建页初始化/主机列表接口

---

### Task 14: 将 GPU 数量改为单选按钮组

**目标**: 对齐 AutoDL 的 GPU 数量选择交互。

**步骤**:
1. 移除数字输入器。
2. 根据当前主机 `availableCount` 生成 `1..availableCount` 的单选按钮。
3. 默认数量为路由传参数量或 1。
4. 数量变化时实时更新实例规格、费用和提交参数。

**文件**: `src/view/computeRent/index.vue`

---

### Task 15: 增加数据盘扩容配置

**目标**: 补齐 AutoDL 的数据盘区域。

**步骤**:
1. 展示免费数据盘容量。
2. 增加“需要扩容”复选框。
3. 勾选后展示扩容容量选择/输入和可扩容上限。
4. 扩容费用计入费用明细。
5. 后端资源详情或初始化接口返回免费容量、可扩容容量、扩容价格。

**文件**:
- `src/view/computeRent/index.vue`
- `src/api/computeRent.ts`
- 后端资源详情/初始化接口

---

### Task 16: 增加实例规格摘要

**目标**: 增加 AutoDL 式“实例规格”汇总区。

**步骤**:
1. 展示 GPU 型号为 `型号 * N卡`。
2. CPU 核数按每卡分配乘以 GPU 数量计算。
3. 内存按每卡分配乘以 GPU 数量计算。
4. 展示系统盘和数据盘。
5. 数据盘显示免费容量或扩容后的容量。

**文件**: `src/view/computeRent/index.vue`

---

### Task 17: 重构镜像选择为镜像来源 + 版本下拉

**目标**: 从 PyTorch/TensorFlow/Miniconda 分类调整为 AutoDL 的基础镜像、社区镜像、我的镜像。

**步骤**:
1. 镜像来源单选：基础镜像、社区镜像、我的镜像。
2. 社区镜像显示 hot 标签。
3. 增加“没有我要的环境？”入口。
4. 基础镜像下展示说明文案。
5. 版本下拉支持搜索/选择框架名、框架版本、Python 版本、CUDA 版本。
6. 后端镜像接口返回镜像来源、框架、版本、Python、CUDA、是否热门。

**文件**:
- `src/view/computeRent/index.vue`
- `src/api/computeRent.ts`
- 后端镜像接口

---

### Task 19: 对齐底部费用与操作区

**目标**: 将费用卡片改为 AutoDL 式底部费用汇总和操作区。

**步骤**:
1. 常驻展示日常费用。
2. 常驻展示配置费用。
3. 增加“费用明细”入口。
4. 展示账户余额。
5. 余额不足时展示“余额不足去充值”。
6. 增加“取消”按钮。
7. 提交按钮文案改为“创建并开机”。
8. 余额不足、库存不足、镜像未选时禁用提交并给出明确提示。

**文件**: `src/view/computeRent/index.vue`

---

### Task 20: 增加费用明细弹层

**目标**: 补齐 AutoDL 的费用明细说明能力。

**步骤**:
1. 费用明细展示价格计算公式。
2. 展示会员折扣（没有则显示“无”）。
3. 明细表包含实例费用、数据盘费用。
4. 每项展示原价、优惠金额、最终价格。
5. 明细随计费方式、GPU 数量、扩容变化刷新。

**文件**:
- `src/view/computeRent/index.vue`
- `src/api/computeRent.ts`
- 后端费用计算接口

---

### Task 21: 扩展后端创建页数据能力

**目标**: 后端接口支撑 AutoDL 式创建实例页面，而不仅是单资源详情。

**步骤**:
1. 增加或扩展创建页初始化接口，返回可选主机列表、默认选中主机、价格、库存、磁盘、镜像、账户余额。
2. 费用计算接口支持计费方式、GPU 数量、数据盘扩容、会员折扣。
3. 创建订单接口支持主机 ID、GPU 数量、数据盘扩容、镜像来源、镜像版本、计费方式。
4. 所有接口字段与前端 `computeRent.ts` 类型保持一致。

**文件**:
- `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/`
- `src/api/computeRent.ts`

---

### Task 22: AutoDL 对齐验收

**目标**: 以浏览器实际页面验证对齐效果。

**验收项**:
1. 页面首屏包含面包屑、计费方式、选择主机表格。
2. GPU 数量为单选按钮，数量来自当前主机空闲 GPU。
3. 实例规格随 GPU 数量变化。
4. 镜像区域包含基础镜像、社区镜像、我的镜像和版本下拉。
5. 底部费用区展示日常费用、配置费用、费用明细、账户余额、取消、创建并开机。
6. 余额不足时展示充值入口。
7. 切换计费方式、主机、GPU 数量、镜像后，费用和提交参数同步更新。
8. 对比 AutoDL 页面时，核心信息层级和交互顺序保持一致。

**验证方式**:
- 打开 `http://localhost:4173/#/computeRent?resourceId=16`。
- 与 AutoDL 创建实例页逐段对比。
- 查看浏览器 Network，确认初始化、费用计算、创建订单接口返回正常。
