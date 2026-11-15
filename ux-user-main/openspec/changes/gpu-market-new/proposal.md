## Why

`docs/gpu-marketplace` 是一个独立开发的 GPU 算力市场前端原型项目，使用 Vue 3 + Ant Design Vue 构建。需要将其核心页面内容（算力市场列表页）整合到主项目 `ux-user-main` 的"算力市场-new"导航 tab 下，以替换现有的占位页面，为用户提供完整的 GPU 浏览、筛选、排序和租用体验。

## What Changes

- 在 `ux-user-main` 项目中重写 `src/view/computeList/new.vue`，将现有占位页面替换为完整的算力市场列表页
- 新增 `GpuFilterPanel` 组件：提供 GPU 型号、显存、CPU、内存、区域、价格区间、可用状态等多维度筛选
- 新增 `GpuCard` 组件：横向卡片展示 GPU 规格、价格、卡数选择及租用操作
- 集成排序功能（按价格/显存/型号，升序/降序切换）
- 集成分页功能（支持页码和每页条数切换）
- 使用 Element Plus 组件库重写样式，适配主项目的浅色主题和响应式布局
- 保留 `docs/gpu-marketplace` 的 12 条模拟 GPU 数据作为页面数据源

## Capabilities

### New Capabilities
- `gpu-market-list`: GPU 算力市场列表页，包含筛选、排序、分页、卡片展示等核心功能
- `gpu-filter`: GPU 多维度筛选面板，支持型号、显存、CPU、内存、区域、价格、状态筛选
- `gpu-card`: GPU 实例卡片组件，展示规格信息、实时计价、卡数选择和租用操作

### Modified Capabilities
- 无现有规范变更

## Impact

- 受影响的文件：`src/view/computeList/new.vue`、`src/components/GpuFilterPanel/index.vue`、`src/components/GpuCard/index.vue`
- 路由：`/computeListNew` 已有路由配置，无需变更
- 导航：`TopBar` 已有"算力市场-new"菜单项，无需变更
- 依赖：仅使用项目已有依赖（Vue 3、Element Plus、Vue Router），无新增依赖
- 数据：当前使用内联模拟数据，后续可替换为真实 API 接口
