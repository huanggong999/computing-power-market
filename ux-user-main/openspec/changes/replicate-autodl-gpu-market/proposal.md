## Why

现有"算力市场-new"页面的 UI 设计与主流算力平台差距较大，用户体验不够直观。参考 AutoDL 算力市场页面（https://www.autodl.com/market/list）的设计，其筛选条件置顶、GPU 卡片三栏信息布局、价格展示等设计更为专业清晰。需要完全复刻该页面的布局结构和内容展示方式，以提升用户体验。

## What Changes

- 重构 `src/view/computeList/new.vue` 页面布局：
  - 筛选面板从左侧改为顶部横向标签式布局
  - 计费方式（按量计费/包日/包周/包月）单选按钮组
  - 地区选择（西北B区/北京B区/重庆A区等）标签式单选
  - 专区筛选（V100专区/A800专区等）标签式选择
  - GPU型号复选框组，带数量统计（如 RTX 5090 (45/1736)）
  - GPU数量（1-12卡）单选按钮组
- 重构 GPU 卡片组件 `GpuCard`：
  - 卡片头部：区域/机房、机器ID、可租用至日期、空闲/总量、缓存优化标签
  - 三栏信息布局：每GPU分配（CPU、内存）、硬盘（系统盘、数据盘+可扩容）、其它（GPU驱动、CUDA版本）
  - 右侧价格区：时价、会员折扣价、"N卡可租"按钮
- 更新数据模型，增加机器ID、可租用期限、空闲/总量统计、GPU驱动版本、CUDA版本等字段
- 样式使用当前网页主体色调（蓝色系），但布局和内容 100% 复刻 AutoDL

## Capabilities

### New Capabilities
- `gpu-market-filter-top`: 顶部横向筛选面板（计费方式、地区、专区、GPU型号、GPU数量）
- `gpu-card-autodl-style`: AutoDL 风格 GPU 卡片（三栏信息布局、价格区、租用按钮）

### Modified Capabilities
- 无

## Impact

- 受影响的文件：`src/view/computeList/new.vue`、`src/components/GpuCard/index.vue`、`src/components/GpuFilterPanel/index.vue`
- 数据模型扩展：增加 machineId、rentableUntil、availableCount、totalCount、gpuDriver、cudaVersion 等字段
- 无后端 API 变更（使用模拟数据）
