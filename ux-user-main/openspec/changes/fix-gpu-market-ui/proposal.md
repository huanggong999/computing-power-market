## Why

"算力市场-new" 页面存在 UI 细节问题：左上角的页面标题"算力市场"与导航栏 tab 名称重复，属于多余信息；左侧筛选面板的字体过小，影响可读性。需要对页面进行微调以提升用户体验。

## What Changes

- 移除 `src/view/computeList/new.vue` 页面头部的"算力市场"大标题和副标题描述
- 增大 `src/components/GpuFilterPanel/index.vue` 中筛选选项的字体大小，使其与页面整体视觉层级协调
- 保留排序选项、筛选功能和 GPU 卡片列表等其他页面元素不变

## Capabilities

### New Capabilities
- 无

### Modified Capabilities
- 无（纯 UI 样式调整，不涉及功能规范变更）

## Impact

- 受影响的文件：`src/view/computeList/new.vue`、`src/components/GpuFilterPanel/index.vue`
- 无 API 变更、无依赖变更、无行为变更
