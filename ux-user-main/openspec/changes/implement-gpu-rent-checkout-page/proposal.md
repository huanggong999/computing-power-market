# Proposal: implement-gpu-rent-checkout-page

## Why

当前算力市场列表页面（computeListNew）已实现 GPU 卡片展示和筛选功能，用户点击"租用"按钮后仅显示消息提示，未实现完整的租用流程。为了提供完整的用户体验，需要实现租用确认页面，让用户能够：

1. 查看选中 GPU 的详细配置信息
2. 选择镜像环境（PyTorch、TensorFlow 等）
3. 确认计费方式和租用数量
4. 查看实时费用计算
5. 提交租用订单

参考 AutoDL 的租用流程设计，实现类似的租用确认页面。

## What Changes

### 新增功能

1. **租用确认页面** (`/computeRent`)
   - 左侧展示 GPU 配置信息（只读）
   - 右侧提供租用设置（镜像、计费、数量）
   - 底部显示费用汇总和确认按钮

2. **API 接口**
   - 获取镜像列表及版本
   - 获取用户账户信息（余额、代金券）
   - 计算租用费用
   - 创建租用订单

3. **页面跳转**
   - 列表页点击"租用"→跳转租用确认页
   - 确认租用后→跳转订单详情或控制台

### 修改文件

1. **新建**:
   - `src/view/computeRent/index.vue` - 租用确认页面
   - `src/api/computeRent.ts` - 租用相关 API
   - `src/components/MirrorSelector/index.vue` - 镜像选择组件

2. **修改**:
   - `src/view/computeList/new.vue` - 修改 `handleRent` 方法实现跳转
   - `src/routes/index.ts` - 添加 `/computeRent` 路由

## Capabilities

### New Capabilities

- `gpu-rent-checkout`: GPU 租用确认和订单创建功能
  - 展示 GPU 详细配置信息
  - 镜像环境选择
  - 计费方式和数量配置
  - 实时费用计算
  - 订单创建和提交

### Modified Capabilities

- 无现有功能需要修改需求

## Impact

### 前端代码
- `src/view/computeRent/` - 新增租用确认页面
- `src/api/computeRent.ts` - 新增 API 接口
- `src/components/MirrorSelector/` - 新增镜像选择组件
- `src/view/computeList/new.vue` - 修改跳转逻辑
- `src/routes/index.ts` - 添加路由配置

### 后端接口
需要后端提供以下 API：
- `GET /pc/gpu/mirror/list` - 获取镜像列表
- `GET /pc/gpu/mirror/{id}/versions` - 获取镜像版本
- `GET /pc/customer/info` - 获取用户账户信息
- `POST /pc/gpu/rent/calculate` - 计算租用费用
- `POST /pc/gpu/rent/order` - 创建租用订单

### AutoDL 对齐补充

本变更后续应以 AutoDL 创建实例页为交互参照，而不是停留在简单确认页。差异和详细工作见 `autodl-alignment.md`。

需要补齐的关键能力：

- 页面结构调整为 `算力市场 / 创建实例` 流程。
- 增加计费规则入口和计费说明。
- 增加“选择主机”表格，展示主机 ID、算力型号/显存、空闲 GPU、每 GPU 分配、CPU 型号、硬盘、驱动/CUDA、单卡价格。
- GPU 数量改为按空闲 GPU 生成的单选按钮。
- 增加数据盘免费容量和扩容配置。
- 增加实例规格摘要，并随 GPU 数量和磁盘扩容实时变化。
- 镜像选择改为基础镜像、社区镜像、我的镜像 + 版本下拉。
- 底部费用区对齐 AutoDL：日常费用、配置费用、费用明细、账户余额、余额不足去充值、取消、创建并开机。
- 后端需要提供创建页初始化、完整费用计算和订单创建所需字段。

### 依赖
- 使用现有 Element Plus 组件
- 使用现有 Vue Router
- 使用现有 Axios 封装

### 风险评估
- **低风险**：基于现有页面结构和组件开发
- **依赖后端**：需要后端配合提供 API 接口
- **兼容性问题**：需要确保与现有实例管理功能兼容
