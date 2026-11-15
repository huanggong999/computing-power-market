## Context

"算力市场-new" 页面 (`src/view/computeList/new.vue`) 已完成功能开发，但在实际使用中发现两处 UI 细节问题：页面头部的大标题"算力市场"与顶部导航栏的 tab 名称重复，造成信息冗余；左侧筛选面板 (`GpuFilterPanel`) 的字体过小，阅读体验不佳。

## Goals / Non-Goals

**Goals:**
- 移除页面头部重复的"算力市场"标题和副标题，让页面更简洁
- 将筛选面板中的选项字体从当前大小增大到与页面其他文本协调的层级

**Non-Goals:**
- 不调整页面布局结构
- 不修改筛选逻辑或交互行为
- 不修改 GPU 卡片样式

## Decisions

**1. 直接删除标题区域而非隐藏**
- 直接移除 `.header-content` 中的 `<h1>` 和 `<p>` 元素及其对应的样式
- 理由：标题内容完全与导航重复，没有保留价值
- 替代方案：使用 `v-if="false"` 隐藏（rejected，增加无意义代码）

**2. 筛选面板字体统一增大**
- 将 `GpuFilterPanel` 中所有选项文字（checkbox、radio、slider 标签）的字体从当前大小统一增大
- 具体：`.el-checkbox`、`.el-radio` 的内联文字从默认 14px 增大到 15px，面板标题保持 16px
- 理由：一次调整即可覆盖所有选项，保持视觉一致性

## Risks / Trade-offs

- **[Risk]** 字体增大后可能导致长选项文字换行或溢出
  - **Mitigation**：筛选面板宽度 280px 足够容纳，且选项文字通常较短（如"RTX 4090"、"≤ 32核"）
