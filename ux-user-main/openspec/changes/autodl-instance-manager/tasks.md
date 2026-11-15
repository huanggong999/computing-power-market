# 实现任务清单: AutoDL 实例管理功能

**变更名称**: autodl-instance-manager
**状态**: 待开始
**预计工时**: 48小时（6个工作日）
**优先级**: P0 - 核心业务功能

---

## 执行计划概览

```
Week 1 (Day 1-3): MVP 核心功能
├── Day 1: 基础框架 + API 层
├── Day 2: 列表页面 + 筛选 + 分页
└── Day 3: 单实例操作 + 状态管理

Week 1 (Day 4-5) + Week 2 (Day 1): 完整功能
├── Day 4: 批量操作
├── Day 5: 实例详情抽屉
└── Day 6: 监控图表

Week 2 (Day 2-3): 测试与优化
├── 联调测试
├── 性能优化
└── Bug 修复
```

---

## Phase 1: 基础框架 (Day 1) - 4小时

### Task 1.1: 创建目录结构
**优先级**: P0 | **预计耗时**: 30分钟

```
src/
├── view/Cloud/InstanceList/
│   ├── index.vue                    # 主页面入口
│   ├── components/
│   │   ├── FilterBar.vue            # 筛选栏
│   │   ├── InstanceTable.vue        # 实例表格
│   │   ├── BatchActions.vue         # 批量操作栏
│   │   ├── InstanceDrawer.vue       # 详情抽屉
│   │   ├── MetricsPanel.vue         # 监控面板
│   │   └── StatusTag.vue            # 状态标签
│   ├── composables/
│   │   ├── useInstanceList.ts       # 列表逻辑
│   │   ├── useInstanceOperation.ts  # 操作逻辑
│   │   └── useMetrics.ts            # 监控逻辑
│   ├── types.ts                     # 本地类型
│   └── style.scss                   # 本地样式
├── api/
│   └── instance.ts                  # 实例API
├── types/
│   └── instance.ts                  # 全局类型定义
└── store/modules/
    └── useInstance.ts               # Pinia Store
```

**验收标准**:
- [ ] 所有目录和空文件已创建
- [ ] 可以在浏览器访问 `/cloud/instance` 路由（显示空白页即可）

---

### Task 1.2: 定义类型和常量
**优先级**: P0 | **预计耗时**: 45分钟

**文件**: `src/types/instance.ts`

内容要求:
1. `InstanceStatus` 枚举（12个状态）
2. `StatusMap` 状态映射（标签/类型/颜色）
3. `Instance` 接口（所有字段）
4. `InstanceFilters` 筛选条件
5. `InstanceListParams` 列表参数
6. `InstanceListResult` 列表结果
7. `InstanceMetrics` 监控数据

**验收标准**:
- [ ] 所有类型定义完整，无 TypeScript 错误
- [ ] 可以在其他文件中正常 import

---

### Task 1.3: 创建 API 层
**优先级**: P0 | **预计耗时**: 45分钟

**文件**: `src/api/instance.ts`

需要实现的接口:
1. `getInstanceList` - 获取列表
2. `getInstanceDetail` - 获取详情
3. `startInstance` - 开机
4. `stopInstance` - 关机
5. `restartInstance` - 重启
6. `releaseInstance` - 释放
7. `batchOperateInstances` - 批量操作
8. `getInstanceMetrics` - 获取监控
9. `getGpuTypes` - GPU类型
10. `getRegions` - 区域列表

**Mock 数据准备**:
```typescript
// 开发阶段使用 mock
const mockInstances: Instance[] = [
  {
    id: 'ins-123456',
    uuid: 'uuid-xxx',
    name: '测试实例-01',
    status: InstanceStatus.RUNNING,
    gpuType: 'RTX 4090',
    gpuCount: 1,
    gpuMemory: 24,
    cpuCores: 16,
    memory: 64,
    systemDisk: 100,
    dataDisk: 500,
    region: '北京A区',
    regionCode: 'bj-a',
    privateIp: '10.0.1.100',
    publicIp: '123.45.67.89',
    sshPort: 22,
    sshCommand: 'ssh root@123.45.67.89',
    rootPassword: 'Pass1234!',
    jupyterUrl: 'http://123.45.67.89:8888',
    createdAt: '2024-01-15 10:30:00',
    expiredAt: '2024-02-15 10:30:00',
    billingType: 'hourly',
    pricePerHour: 2.5
  }
  // ... 更多 mock 数据
]
```

**验收标准**:
- [ ] 所有 API 函数可正常调用
- [ ] 使用 Mock 数据可以返回正确的数据结构
- [ ] 网络错误有基本处理

---

### Task 1.4: 创建 Pinia Store
**优先级**: P0 | **预计耗时**: 45分钟

**文件**: `src/store/modules/useInstance.ts`

需要实现:
1. State: instanceList, total, loading, currentInstance, currentMetrics, gpuTypes, regions
2. Getters: runningInstances, stoppedInstances
3. Actions: 所有 API 调用方法 + resetState

**验收标准**:
- [ ] Store 可以正常注册到 Pinia
- [ ] 可以在组件中使用 store 获取/修改状态

---

## Phase 2: 列表页面核心功能 (Day 2) - 8小时

### Task 2.1: 筛选栏组件 (FilterBar)
**优先级**: P0 | **预计耗时**: 2小时

**功能**:
- GPU类型多选下拉
- 区域多选下拉
- 状态多选下拉
- 关键词搜索输入框
- 搜索按钮
- 重置按钮

**Props**:
```typescript
interface Props {
  modelValue: InstanceFilters
  gpuTypes: string[]
  regions: { code: string; name: string }[]
}

interface Emits {
  search: () => void
  reset: () => void
}
```

**验收标准**:
- [ ] 所有筛选条件可正常选择/输入
- [ ] 点击搜索触发 search 事件并带上筛选条件
- [ ] 点击重置清空所有条件并触发 reset 事件

---

### Task 2.2: 实例表格组件 (InstanceTable)
**优先级**: P0 | **预计耗时**: 3小时

**列定义**:
1. 复选框列（批量操作）
2. 实例名称（带点击事件，可复制）
3. GPU类型/数量（如 "RTX 4090 x 1"）
4. 区域
5. 状态（使用 StatusTag 组件）
6. 创建时间
7. 过期时间
8. 操作列（开机/关机/重启/释放按钮，根据状态显示不同按钮）

**Props**:
```typescript
interface Props {
  data: Instance[]
  loading: boolean
  pagination: {
    currentPage: number
    pageSize: number
    total: number
  }
}

interface Emits {
  pageChange: (page: number) => void
  sizeChange: (size: number) => void
  sortChange: (column: any) => void
  rowClick: (row: Instance) => void
  action: (action: string, row: Instance) => void
  'update:selected': (selected: Instance[]) => void
}
```

**验收标准**:
- [ ] 表格正确显示所有列和数据
- [ ] 分页器可正常切换页码和每页条数
- [ ] 点击行触发 rowClick 事件
- [ ] 点击操作按钮触发 action 事件
- [ ] 多选框可正常选择，触发 update:selected 事件

---

### Task 2.3: 状态标签组件 (StatusTag)
**优先级**: P0 | **预计耗时**: 1小时

**Props**:
```typescript
interface Props {
  status: InstanceStatus
  showText?: boolean  // 是否显示文字，默认true
  size?: 'default' | 'small' | 'large'
}
```

**实现要点**:
- 使用 `StatusMap` 获取状态对应的颜色和文字
- 使用 Element Plus 的 `el-tag` 组件或自定义样式
- 支持小圆点模式（showText=false）

**验收标准**:
- [ ] 所有状态正确显示对应颜色和文字
- [ ] showText=false 时只显示小圆点

---

### Task 2.4: 主页面框架搭建
**优先级**: P0 | **预计耗时**: 2小时

**文件**: `src/view/Cloud/InstanceList/index.vue`

**功能**:
- 整合 FilterBar、InstanceTable 组件
- 使用 composable 管理业务逻辑
- 响应式布局

**验收标准**:
- [ ] 页面可正常访问，显示筛选栏和表格
- [ ] 筛选条件变化时重新加载列表
- [ ] 分页切换时重新加载列表

---

## Phase 3: 操作功能 (Day 3) - 6小时

### Task 3.1: 单实例操作
**优先级**: P0 | **预计耗时**: 2小时

**文件**: `src/view/Cloud/InstanceList/composables/useInstanceOperation.ts`

**功能**:
- 开机、关机、重启、释放操作
- 操作前确认弹窗
- 操作后状态轮询
- 错误处理

**验收标准**:
- [ ] 点击操作按钮显示确认弹窗
- [ ] 确认后发送 API 请求
- [ ] 操作成功后刷新列表
- [ ] 支持中的实例显示 loading 状态

---

### Task 3.2: 批量操作栏组件 (BatchActions)
**优先级**: P0 | **预计耗时**: 2小时

**Props**:
```typescript
interface Props {
  selected: Instance[]
  instances: Instance[]
}

interface Emits {
  batchStart: (ids: string[]) => void
  batchStop: (ids: string[]) => void
  batchRestart: (ids: string[]) => void
  batchRelease: (ids: string[]) => void
}
```

**功能**:
- 显示已选数量
- 开机、关机、重启、释放批量按钮
- 根据选中实例状态启用/禁用按钮

**验收标准**:
- [ ] 正确显示已选实例数量
- [ ] 未选择实例时按钮禁用
- [ ] 批量操作触发对应事件

---

### Task 3.3: 批量操作逻辑
**优先级**: P0 | **预计耗时**: 2小时

**文件**: `useInstanceOperation.ts` 扩展

**功能**:
- 批量操作执行
- 进度提示
- 部分失败的错误处理
- 操作完成后的列表刷新

**验收标准**:
- [ ] 批量操作显示进度提示
- [ ] 部分失败时显示成功/失败统计
- [ ] 操作完成后刷新列表

---

## Phase 4: 实例详情 (Day 4) - 6小时

### Task 4.1: 实例详情抽屉组件 (InstanceDrawer)
**优先级**: P0 | **预计耗时**: 3小时

**Props**:
```typescript
interface Props {
  modelValue: boolean
  instance: Instance | null
  metrics: InstanceMetrics | null
  metricsLoading: boolean
}

interface Emits {
  'update:modelValue': (value: boolean) => void
  refresh: () => void
  action: (action: string, instance: Instance) => void
}
```

**内容区域**:
1. 头部：实例名称、状态、操作按钮
2. 基础信息：ID、UUID、创建时间、过期时间
3. 资源配置：GPU/CPU/内存/磁盘卡片展示
4. 网络信息：内网IP、公网IP、SSH端口
5. 登录信息：SSH命令、密码（带复制功能）
6. 监控图表：GPU使用率、显存占用

**验收标准**:
- [ ] 抽屉正确显示实例信息
- [ ] 所有信息可复制
- [ ] SSH命令/密码有复制按钮
- [ ] 监控图表正确显示（有数据时）

---

### Task 4.2: 监控数据逻辑
**优先级**: P1 | **预计耗时**: 2小时

**文件**: `src/view/Cloud/InstanceList/composables/useMetrics.ts`

**功能**:
- 获取监控数据
- 定时刷新（30秒）
- 数据格式化

**验收标准**:
- [ ] 成功获取并格式化监控数据
- [ ] 支持30秒自动刷新
- [ ] 切换实例时重新加载

---

### Task 4.3: 监控面板组件 (MetricsPanel)
**优先级**: P1 | **预计耗时**: 1小时

**Props**:
```typescript
interface Props {
  metrics: InstanceMetrics | null
  loading: boolean
}
```

**内容**:
- GPU使用率折线图
- 显存占用折线图
- CPU使用率折线图（可选）
- 内存使用折线图（可选）

**验收标准**:
- [ ] 正确显示监控图表
- [ ] loading 状态显示骨架屏
- [ ] 无数据时显示空状态

---

## Phase 5: 监控图表 (Day 5) - 6小时

### Task 5.1: MetricsChart 组件
**优先级**: P1 | **预计耗时**: 2小时

**文件**: `src/components/MetricsChart/index.vue`

**功能**:
- 基于 ECharts 的通用监控图表
- 支持折线图、面积图
- 自适应容器大小
- Tooltip 格式化

**验收标准**:
- [ ] 图表正确渲染
- [ ] 窗口resize时自适应
- [ ] Tooltip 显示正确

---

### Task 5.2: 实例详情抽屉完善
**优先级**: P1 | **预计耗时**: 2小时

**完善内容**:
- 添加所有监控图表
- 添加时间范围选择器
- 添加刷新按钮
- 添加导出功能（可选）

**验收标准**:
- [ ] 抽屉展示所有监控图表
- [ ] 时间范围可切换
- [ ] 支持手动刷新

---

### Task 5.3: 列表页监控预览
**优先级**: P2 | **预计耗时**: 2小时

**功能**:
- 在表格中显示实时状态指示器
- 运行中实例显示 GPU 利用率（可选）

**验收标准**:
- [ ] 列表实时反映实例状态
- [ ] 状态变化有视觉反馈

---

## Phase 6: 测试与优化 (Day 6-7) - 12小时

### Task 6.1: 单元测试
**优先级**: P1 | **预计耗时**: 3小时

**测试范围**:
- composables 测试 (useInstanceList, useInstanceOperation, useMetrics)
- 工具函数测试
- Store 测试

**验收标准**:
- [ ] 核心逻辑测试覆盖率达到 80%
- [ ] 所有测试用例通过

---

### Task 6.2: 集成测试
**优先级**: P1 | **预计耗时**: 3小时

**测试场景**:
- 完整用户操作流程
- 各种状态流转
- 批量操作边界情况
- 网络异常处理

**验收标准**:
- [ ] 主要用户场景测试通过
- [ ] 异常处理符合预期

---

### Task 6.3: 性能优化
**优先级**: P2 | **预计耗时**: 3小时

**优化点**:
- 列表虚拟滚动（大数据量）
- 图表数据采样
- 图片/资源懒加载
- 组件按需加载

**验收标准**:
- [ ] 列表滚动流畅（60fps）
- [ ] 首屏加载 < 2s
- [ ] 内存占用合理

---

### Task 6.4: 代码审查与文档
**优先级**: P2 | **预计耗时**: 3小时

**工作内容**:
- 代码自查和重构
- 添加必要的注释
- 更新 README
- 编写使用说明

**验收标准**:
- [ ] 代码风格统一
- [ ] 关键逻辑有注释
- [ ] 文档完整可理解

---

## 任务检查清单总览

### Phase 1: 基础框架 ✅
- [ ] Task 1.1: 创建目录结构 (30min)
- [ ] Task 1.2: 定义类型和常量 (45min)
- [ ] Task 1.3: 创建 API 层 (45min)
- [ ] Task 1.4: 创建 Pinia Store (45min)

### Phase 2: 列表页面核心功能 ✅
- [ ] Task 2.1: 筛选栏组件 (2h)
- [ ] Task 2.2: 实例表格组件 (3h)
- [ ] Task 2.3: 状态标签组件 (1h)
- [ ] Task 2.4: 主页面框架 (2h)

### Phase 3: 操作功能 ✅
- [ ] Task 3.1: 单实例操作 (2h)
- [ ] Task 3.2: 批量操作栏组件 (2h)
- [ ] Task 3.3: 批量操作逻辑 (2h)

### Phase 4: 实例详情 ✅
- [ ] Task 4.1: 实例详情抽屉 (3h)
- [ ] Task 4.2: 监控数据逻辑 (2h)
- [ ] Task 4.3: 监控面板组件 (1h)

### Phase 5: 监控图表 ✅
- [ ] Task 5.1: MetricsChart 组件 (2h)
- [ ] Task 5.2: 实例详情抽屉完善 (2h)
- [ ] Task 5.3: 列表页监控预览 (2h)

### Phase 6: 测试与优化 ✅
- [ ] Task 6.1: 单元测试 (3h)
- [ ] Task 6.2: 集成测试 (3h)
- [ ] Task 6.3: 性能优化 (3h)
- [ ] Task 6.4: 代码审查与文档 (3h)

---

## 快速开始指南

### 第1步: 拉取最新代码
```bash
git pull origin main
```

### 第2步: 安装依赖（如有新增）
```bash
npm install
```

### 第3步: 启动开发服务器
```bash
npm run dev
```

### 第4步: 开始开发
按照 Task 清单顺序逐个完成，每个 Task 完成后在对应复选框打勾。

---

## 开发规范

### 代码提交规范
```
feat: 新增实例列表筛选功能
fix: 修复实例状态不刷新问题
docs: 添加 API 文档注释
style: 调整表格样式
refactor: 重构 useInstanceList 逻辑
test: 添加实例操作单元测试
```

### 分支管理
- 主分支: `main`
- 开发分支: `feature/instance-management`
- 个人分支: `feature/instance-management-{name}`

---

## 常见问题

### Q1: Mock 数据如何配置？
在 `vite.config.ts` 中配置 mock 插件，或在 `src/api/instance.ts` 中直接返回 mock 数据。

### Q2: 如何测试 WebSocket 实时状态？
先使用 Mock 定时更新状态，后续接入真实 WebSocket。

### Q3: 监控图表数据格式不对？
检查后端返回的 metrics 数据格式，确保时间戳是毫秒级。

---

**文档版本**: 1.0  
**最后更新**: 2026-05-24  
**负责人**: Claude