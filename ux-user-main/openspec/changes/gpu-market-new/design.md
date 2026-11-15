## Context

`docs/gpu-marketplace` 是一个使用 Vue 3 + Vite + Ant Design Vue 独立开发的 GPU 算力市场前端原型。主项目 `ux-user-main` 是一个基于 Vue 3 + Vite + Element Plus 的 PC 端门户网站，使用浅色主题和 postcss-px-to-viewport 响应式适配（设计稿 1980px）。

现有 `src/view/computeList/new.vue` 是一个占位页面，仅展示基本的 ServerCard 组件列表。需要将其替换为功能完整的算力市场列表页，包含多维度筛选、排序、分页和 GPU 卡片展示。

## Goals / Non-Goals

**Goals:**
- 将 `docs/gpu-marketplace` 的 Market.vue 核心功能完整迁移到 `src/view/computeList/new.vue`
- 使用 Element Plus 组件库重写所有 UI，适配主项目浅色主题
- 保持响应式布局，在 1200px 以下隐藏左侧筛选面板
- 提供 12 条模拟 GPU 数据用于演示和开发

**Non-Goals:**
- 不迁移 RentModal 租用弹窗（超出当前 tab 页面范围）
- 不迁移 Home.vue 首页内容（仅迁移 Market.vue）
- 不连接真实后端 API（继续使用模拟数据）
- 不修改现有算力市场（`/computeList`）和算力智选（`/diycomputeList`）页面

## Decisions

**1. 使用独立组件而非内联**
- 将筛选面板和 GPU 卡片拆分为 `GpuFilterPanel` 和 `GpuCard` 独立组件
- 理由：提高可维护性，避免单个文件过大，便于后续复用
- 替代方案：全部内联在 new.vue 中（ rejected，文件会超过 500 行）

**2. 模拟数据内联在页面中**
- 将 `docs/gpu-marketplace/src/stores/gpu.js` 的 mock 数据直接内联到 new.vue
- 理由：避免引入额外的 Pinia store，减少变更范围，后续替换为 API 调用简单
- 替代方案：创建新的 Pinia store（ rejected，当前无真实 API，增加不必要的抽象）

**3. 保持原有路由和导航不变**
- `/computeListNew` 路由和 TopBar 菜单已在前期工作中添加
- 理由：避免重复修改，当前路由配置已满足需求

**4. 样式适配方案**
- 移除 `docs/gpu-marketplace` 的深色 CSS 变量（`--bg-dark`、`--bg-card` 等）
- 使用 Element Plus 默认的浅色主题配色（`#303133`、`#606266`、`#e4e7ed` 等）
- 理由：与主项目视觉风格保持一致

## Risks / Trade-offs

- **[Risk]** `docs/gpu-marketplace` 使用 Ant Design Vue 的组件 API 与 Element Plus 存在差异，重写过程中可能遗漏某些交互细节
  - **Mitigation**：对照原页面功能逐一验证，确保筛选、排序、分页交互完整
- **[Risk]** 响应式布局在 1980px 设计稿基准下可能出现适配问题
  - **Mitigation**：使用 px 单位编写样式，依赖 postcss-px-to-viewport 自动转换
- **[Trade-off]** 当前使用模拟数据而非真实 API，页面功能完整但数据是静态的
  - 后续接入 API 时只需替换 `gpuList` 的数据源和加载逻辑
