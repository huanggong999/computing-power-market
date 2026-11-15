# computeListNew 前端写死数据梳理

本文梳理 `ux-user-main` 中 `computeListNew` 算力市场页和 `computeRent` 创建实例页仍由前端维护的静态数据。

原则：**后台已经有维护入口或维护表的数据，用户端页面跟后台联动；后台没有维护能力的数据，暂时允许前端写死。**

## 范围

| 页面 / 模块 | 路径 | 说明 |
|---|---|---|
| 算力市场列表页 | `ux-user-main/src/view/computeList/new.vue` | 路由 `/computeListNew` |
| 算力市场筛选面板 | `ux-user-main/src/components/GpuFilterPanel/index.vue` | 地区、专区、GPU 型号、GPU 数量、计费方式筛选 |
| 算力市场卡片 | `ux-user-main/src/components/GpuCard/index.vue` | 单个 GPU 资源卡片展示 |
| 创建实例页 | `ux-user-main/src/view/computeRent/index.vue` | 从算力市场点击租用后进入 |
| 创建实例 API 封装 | `ux-user-main/src/api/computeRent.ts` | GPU 详情、镜像、账户、计费、下单接口封装 |
| 算力市场 API 封装 | `ux-user-main/src/api/gpuMarket.ts` | 市场列表和筛选元数据接口封装 |

## 已联动后台的数据

这些数据已经来自后台维护的 GPU 资源体系，不属于前端写死。

| 数据 | 用户端来源 | 后台来源 / 维护入口 | 说明 |
|---|---|---|---|
| GPU 资源列表 | `/pc/gpu/market/list` | GPU 资源管理 | 资源卡片列表来自后台上架资源 |
| 地区筛选 | `/pc/gpu/market/meta` | GPU 地区管理 | 只返回已上架资源实际使用的地区 |
| 专区筛选 | `/pc/gpu/market/meta` | GPU 专区管理 | 只返回已上架资源实际使用的专区 |
| GPU 型号筛选 | `/pc/gpu/market/meta` | GPU 规格管理 | 基于启用规格和上架资源统计 |
| GPU 数量筛选 | `/pc/gpu/market/meta` | GPU 资源管理 | 当前从已上架资源的 `gpuCount` 推导 |
| 计费方式筛选 | `/pc/gpu/market/meta` | GPU 资源价格配置 | 当前从已上架资源的价格表 `billingType` 推导 |
| 价格 / 折扣价 / 折扣率 | `/pc/gpu/market/list`、`/pc/gpu/market/detail/{resourceId}` | GPU 资源价格配置 | 卡片和创建页价格来自后台价格表 |
| 库存 | `/pc/gpu/market/list`、`/pc/gpu/market/detail/{resourceId}` | GPU 资源库存配置 | 空闲/总量来自后台库存表 |
| CPU / 内存 / 硬盘 / 驱动 / CUDA | `/pc/gpu/market/list`、`/pc/gpu/market/detail/{resourceId}` | GPU 资源管理 | 卡片和创建页主机信息来自后台资源表 |
| 账户余额 | `/pc/customer/info` | 客户账户体系 | 创建页底部余额来自后台 |
| 费用计算 | 当前资源详情 `prices` | GPU 资源价格配置 | 创建页基础价格来自后端返回价格规则，前端做数量、周期、折扣金额计算 |
| 创建订单 | `/pc/gpu/rent/order` | GPU 租用接口 | 创建按钮提交后端接口 |

## 仍由前端写死的数据

### 1. 排序选项

位置：`ux-user-main/src/view/computeList/new.vue`

```vue
<el-radio-button label="price">价格</el-radio-button>
<el-radio-button label="vram">显存</el-radio-button>
<el-radio-button label="model">型号</el-radio-button>
```

默认排序字段：

```ts
const sortBy = ref('price')
const sortOrder = ref('asc')
```

是否应后台维护：**暂不需要。**

原因：这是页面交互能力，不是业务数据。后台接口已经支持 `price / vram / model` 三种排序字段，前端写死按钮是合理的。

### 2. 筛选里的“全部”选项

位置：`ux-user-main/src/components/GpuFilterPanel/index.vue`

```vue
<el-radio-button label="">全部</el-radio-button>
<el-checkbox>全部</el-checkbox>
<el-radio-button :label="undefined">全部</el-radio-button>
```

覆盖范围：

- 地区全部
- 专区全部
- GPU 型号全部
- GPU 数量全部

是否应后台维护：**不需要。**

原因：这是前端筛选控件的语义，表示不传对应筛选条件。不是后台业务数据。

### 3. 计费单位映射

位置一：`ux-user-main/src/components/GpuCard/index.vue`

```ts
const units: Record<string, string> = {
  hourly: '时',
  daily: '日',
  weekly: '周',
  monthly: '月',
}
```

位置二：`ux-user-main/src/view/computeRent/index.vue`

```ts
const units: Record<string, string> = {
  hourly: '时',
  daily: '日',
  weekly: '周',
  monthly: '月'
}
```

是否应后台维护：**建议后续改为后台返回。**

现状说明：计费方式列表现在来自后台 `/pc/gpu/market/meta`，但“价格单位文案”仍由前端根据编码映射。比如 `hourly` 显示 `/时`。

建议：后端的 `GpuBillingTypeVO` 可以增加 `unitName` 字段，例如：

```json
{ "code": "hourly", "name": "按小时", "unitName": "时" }
```

这样卡片和创建页不用维护同一份映射。

### 4. 创建实例页计费方式按钮

位置：`ux-user-main/src/view/computeRent/index.vue`

当前状态：**已改为联动当前资源详情的 `prices`。**

```vue
<el-radio-button
  v-for="item in billingOptions"
  :key="item.billingType"
  :label="item.billingType"
>
  {{ item.billingTypeName || billingTypeNameMap[item.billingType] || item.billingType }}
</el-radio-button>
```

说明：`billingOptions` 来自当前资源详情的 `gpuResource.prices`。如果某个资源只维护了按小时价格，创建页就只显示按小时，不再显示按日、按周、按月，避免点击后后端费用计算返回“价格不存在”。

是否仍有前端写死：**计费方式名称兜底仍在前端。**

```ts
const billingTypeNameMap: Record<string, string> = {
  hourly: '按量计费',
  daily: '包日',
  weekly: '包周',
  monthly: '包月'
}
```

建议：后端后续可在价格项或计费方式元数据中返回 `billingTypeName` 和 `unitName`，前端仅保留兜底。

### 5. 计费规则和提示文案

位置：`ux-user-main/src/view/computeRent/index.vue`

```vue
<el-link type="primary" class="billing-rules-link">计费规则</el-link>
```

```text
创建完主机后仍然可以转换计费方式。如选择按量计费，价格发生变动以实例开机时的价格为准
```

是否应后台维护：**暂不需要，除非运营要频繁调整。**

原因：这是静态说明文案。如果未来计费规则需要运营后台配置，可以抽成公告/配置项。

### 6. 镜像来源 Tab

位置：`ux-user-main/src/view/computeRent/index.vue`

```vue
<el-radio-button label="base">基础镜像</el-radio-button>
<el-radio-button label="community">社区镜像</el-radio-button>
<el-radio-button label="my">我的镜像</el-radio-button>
```

相关提示文案：

```text
基础镜像包含常用基本软件，如深度学习框架、Miniconda 等；如需其他软件可创建后安装。
社区镜像由用户分享，包含常用深度学习环境配置。
暂无我的镜像
```

是否应后台维护：**当前不需要。**

原因：目前后台没有 GPU 镜像维护表和后台管理页。镜像体系还不是后台可维护数据。

### 7. 镜像列表

位置：`ux-user-main/src/api/computeRent.ts`

```ts
const staticMirrors: MirrorItem[] = [
  {
    id: 'pytorch',
    name: 'PyTorch',
    type: 'pytorch',
    icon: 'pytorch',
    description: 'PyTorch 深度学习环境',
    supportedGpuModels: [...]
  },
  {
    id: 'tensorflow',
    name: 'TensorFlow',
    type: 'tensorflow',
    icon: 'tensorflow',
    description: 'TensorFlow 深度学习环境',
    supportedGpuModels: [...]
  },
  {
    id: 'miniconda',
    name: 'Miniconda',
    type: 'miniconda',
    icon: 'miniconda',
    description: '基础 Python/Conda 环境',
    supportedGpuModels: [...]
  }
]
```

是否应后台维护：**当前先前端写死。**

原因：后台没有 GPU 镜像维护入口。原后端 `/pc/gpu/mirror/list` 也是代码硬编码，不是真后台维护。按当前原则，先放在前端更清晰。

当前行为：基础镜像列表固定返回前端静态列表，不按 GPU 型号过滤。

### 8. 镜像版本列表

位置：`ux-user-main/src/api/computeRent.ts`

```ts
const staticMirrorVersions: Record<string, MirrorVersion[]> = {
  pytorch: [...],
  tensorflow: [...],
  miniconda: [...]
}
```

包含版本：

| 镜像 | 版本 |
|---|---|
| PyTorch | PyTorch 2.1 / CUDA 12.1 / Python 3.10 |
| PyTorch | PyTorch 2.0 / CUDA 11.8 / Python 3.10 |
| TensorFlow | TensorFlow 2.15 / CUDA 12.1 / Python 3.10 |
| TensorFlow | TensorFlow 2.12 / CUDA 11.8 / Python 3.10 |
| Miniconda | Miniconda / Python 3.10 |
| Miniconda | Miniconda / Python 3.11 |

是否应后台维护：**当前先前端写死。**

原因同镜像列表。以后如果做 GPU 镜像管理，需要新增镜像表、镜像版本表和后台管理页。

### 9. 数据盘展示文案

位置：`ux-user-main/src/view/computeRent/index.vue`

```ts
return `免费${base} SSD`
return `免费${base} SSD + 扩容${expandSize.value}GB`
```

以及模板中：

```vue
<span class="free-disk">免费{{ gpuResource.dataDisk || '50GB' }}</span>
```

是否应后台维护：**部分应联动，部分是前端文案。**

说明：

- `gpuResource.dataDisk` 和 `gpuResource.expandable` 来自后台资源维护。
- “免费”和“SSD”是前端文案假设。
- `'50GB'` 是兜底值，理论上后台资源已经维护数据盘，不应长期依赖兜底。

建议：如果数据盘收费策略会变化，应由后端费用计算接口返回磁盘费用和展示文案。当前可先保留。

### 10. 费用明细结构和文案

位置：`ux-user-main/src/view/computeRent/index.vue`

静态结构包括：

```text
配置费用 = 单价 × GPU数量 × 时长
计费项 / 原价 / 折扣金额 / 最终价格
实例费用
数据盘费用
会员折扣
合计
```

是否应后台维护：**当前可前端写死结构，基础价格必须来自后端返回的价格规则。**

说明：

- 基础价格来自当前资源详情 `gpuResource.prices` 中的 `unitPrice` / `discountPrice`。
- 前端根据后端返回的基础价格计算原价、折扣金额、最终价格。
- 明细行结构是前端固定展示。
- 如果以后费用项增加，例如公网 IP、镜像存储、数据盘阶梯价，建议后端返回费用明细数组，前端循环渲染。

### 11. 可提交校验文案

位置：`ux-user-main/src/view/computeRent/index.vue`

```ts
if (!rentConfig.mirrorId || !rentConfig.versionId) return '请先选择镜像和版本'
if (!rentConfig.quantity || rentConfig.quantity <= 0) return 'GPU数量无效'
if (isBalanceInsufficient.value) return '余额不足，请充值'
```

是否应后台维护：**不需要。**

原因：这是前端交互提示。后端仍需保留真实校验，前端提示只用于更快反馈用户。

## 建议优先级

### P0：保持现状即可

这些前端写死是合理的：

- 排序按钮：价格 / 显存 / 型号
- “全部”筛选项
- 表单校验文案
- 基础费用明细结构

### P1：建议尽快联动后台

这些已经有后台数据基础，但前端仍有少量展示兜底：

1. **计费单位文案**
   - 当前 `hourly -> 时` 等映射写在两个组件里。
   - 建议后端 `GpuBillingTypeVO` 或资源价格项增加 `unitName`。

2. **计费方式名称兜底**
   - 创建实例页计费方式按钮已由 `gpuResource.prices` 驱动。
   - 前端仍保留 `hourly -> 按量计费` 等名称兜底。
   - 建议后端价格项稳定返回 `billingTypeName`。

### P2：未来做后台维护

这些当前没有后台维护能力，先前端写死是符合原则的：

1. **镜像列表**
2. **镜像版本列表**
3. **镜像来源分类：基础 / 社区 / 我的**

如果要后台化，需要新增：

- GPU 镜像表
- GPU 镜像版本表
- 支持 GPU 型号关联关系
- 后台镜像管理页面
- PC 端镜像查询接口改为查表

## 当前状态总结

| 类别 | 状态 | 结论 |
|---|---|---|
| 算力资源核心数据 | 已联动后台 | 后台维护后用户页自动变化 |
| 市场筛选数据 | 已联动后台 | GPU 数量、计费方式已从资源/价格推导 |
| 创建页镜像 | 前端写死 | 符合“后台未维护先写死”的原则 |
| 创建页计费方式 | 已联动当前资源价格 | 只显示当前资源实际维护过价格的计费方式 |
| 费用金额 | 前端按后端返回价格规则计算 | 基础价格来自当前资源详情 `prices`，前端只做数量、周期和折扣金额计算 |
| 费用明细展示结构 | 前端写死 | 当前可接受，费用项变复杂后再后台化 |
