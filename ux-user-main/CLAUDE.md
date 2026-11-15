# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供关于本代码仓库的行为规范与项目指引。

---

## 行为准则

**权衡：** 以下准则倾向于谨慎而非速度。对于简单任务，请自行判断。

### 1. 编码前思考

**不要假设。不要隐藏困惑。暴露权衡。**

在实现之前：
- 明确陈述你的假设。如果不确定，请提问。
- 如果存在多种理解方式，请呈现出来——不要默默选择。
- 如果存在更简单的方案，请说明。必要时提出反对意见。
- 如果有不清楚的地方，停下来。指出困惑之处。提问。

### 2. 简单优先

**最少代码解决问题。不做推测性实现。**

- 不添加超出需求的功能。
- 不为一次性代码创建抽象。
- 不添加未被要求的"灵活性"或"可配置性"。
- 不处理不可能发生的场景的错误。
- 如果你写了 200 行但 50 行就能解决，重写它。

问自己："资深工程师会觉得这过度复杂吗？"如果是，简化。

### 3. 精准修改

**只触碰必须修改的部分。只清理自己造成的混乱。**

编辑现有代码时：
- 不要"改进"相邻的代码、注释或格式。
- 不要重构没有问题的代码。
- 遵循现有风格，即使你自己会采用不同方式。
- 如果你注意到无关的死代码，提一下——不要删除它。

当你的修改产生孤立代码时：
- 删除你的修改导致不再使用的导入/变量/函数。
- 不要删除预先存在的死代码，除非被要求。

检验标准：每一行修改都应能直接追溯到用户的请求。

### 4. 目标驱动执行

**定义成功标准。循环验证直到通过。**

将任务转化为可验证的目标：
- "添加验证" → "为无效输入编写测试，然后让它们通过"
- "修复 bug" → "编写能复现问题的测试，然后让它通过"
- "重构 X" → "确保重构前后测试都能通过"

对于多步骤任务，简要说明计划：
```
1. [步骤] → 验证：[检查]
2. [步骤] → 验证：[检查]
3. [步骤] → 验证：[检查]
```

明确的成功标准能让你独立迭代。弱标准（"让它能跑起来"）需要不断澄清。

---

**这些准则有效体现在：** diff 中不必要修改更少、因过度复杂导致的重写更少，以及澄清问题出现在实现之前而非错误之后。

---

## 构建与运行

这是一个基于 **Vue 3 + Vite** 的 PC 端门户网站项目，使用 **npm** 作为包管理器。

```bash
# 安装依赖
npm install

# 启动开发服务器（默认端口由 .env.development 中的 VITE_PORT 决定）
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview
```

### 环境配置

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境配置（代理地址、端口、标题等） |
| `.env.production` | 生产环境配置 |
| `.env` | 通用配置 |

关键环境变量：
- `VITE_APP_BASE_API` — API 基础路径，默认 `/api`
- `VITE_PROXY_URL` — 开发代理目标地址，默认 `https://eyunai.net/api`
- `VITE_PUBLIC_PATH` — 公共基础路径，默认 `/`
- `VITE_GLOB_APP_TITLE` — 页面标题，默认"AI智算"
- `VITE_APP_SOCKET_URL` — WebSocket 地址
- `VITE_DROP_CONSOLE` — 构建时是否移除 console

开发代理配置：`/api` → `VITE_PROXY_URL`，rewrite 去掉 `/api` 前缀。

## 项目概述

**AI智算项目门户端** — 逸云智算平台 PC 端门户网站。基于 Vue 3 + TypeScript + Element Plus 构建，提供云资源浏览、算力市场、模型市场、应用市场、云服务器管理、容器服务、账单查询、订单管理、活动中心、推广分销等面向用户的功能。

### 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Vue 3.4.37、TypeScript 5.5.3 |
| 构建工具 | Vite 5.4.1 |
| UI 组件库 | Element Plus 2.8.1 |
| 状态管理 | Pinia 2 + pinia-plugin-persistedstate |
| 路由 | Vue Router 4（Hash 模式） |
| HTTP 客户端 | Axios |
| 国际化 | vue-i18n 10 |
| 图表 | echarts 5.5.1 |
| Markdown 预览 | @kangc/v-md-editor 2.3.18 |
| 表单设计 | @form-create/element-ui 3.2.17 |
| 样式 | Sass/SCSS |
| 适配 | postcss-px-to-viewport（设计稿宽度 1980px） |

## 架构概览

### 目录结构

```
src/
├── view/                       # 页面视图
│   ├── Home/                   # 首页（包含子路由容器）
│   ├── Application/            # 应用市场（列表/详情）
│   ├── modelList/              # 模型市场（列表/详情）
│   ├── dataList/               # 数据市场（列表/详情）
│   ├── news/                   # 新闻资讯（列表/详情）
│   ├── computeList/            # 算力列表
│   ├── networkProduct/         # 网络产品列表
│   ├── networkProductDetail/   # 网络产品详情
│   ├── networkProductForm/     # 网络产品表单
│   ├── largeModel/             # AI 大模型
│   ├── partner/                # 合作伙伴
│   ├── helpDocs/               # 帮助文档
│   ├── invite/                 # 邀请推广
│   ├── activityCenter/         # 活动中心（列表/详情）
│   ├── activity/               # 活动页
│   ├── Cloud/                  # 云服务器管理
│   │   ├── instance/           # 实例列表
│   │   ├── instanceDetail/     # 实例详情
│   │   ├── instancePay/        # 实例支付
│   │   ├── createInstance/     # 创建实例
│   │   ├── imageManagement/    # 镜像管理
│   │   └── resource/           # 资源管理
│   ├── ContainerConsole/       # 容器控制台
│   │   ├── containerList/      # 容器列表
│   │   ├── containerPay/       # 容器支付
│   │   ├── mirrorWarehouse/    # 镜像仓库
│   │   └── resourcesOverview/  # 资源概览
│   ├── Console/                # 用户控制台（含发票）
│   ├── AccountManagement/      # 账户管理
│   │   ├── accountCenter/      # 账户中心
│   │   ├── authenticationCenter/ # 认证中心
│   │   ├── rechargeCenter/     # 充值中心
│   │   └── voucherCollectionCenter/ # 代金券中心
│   ├── Order/                  # 订单管理
│   ├── aigcOrder/              # AIGC 订单
│   ├── ExpenseBill/            # 费用账单
│   ├── ContractManagement/     # 合同管理
│   ├── storage/                # 对象存储
│   │   ├── createBucket/       # 创建存储桶
│   │   ├── detail/             # 存储详情
│   │   └── object/             # 对象管理
│   ├── payControl/             # 支付控制
│   ├── popularize/             # 推广管理
│   ├── Login/                  # 登录
│   └── docsView/               # 文档页面（协议、隐私政策、服务条款）
├── components/                 # 公共组件
│   ├── AppCard/                # 应用卡片
│   ├── Banner/                 # 轮播图
│   ├── Breadcrumb/             # 面包屑
│   ├── CouponCard/             # 优惠券卡片
│   ├── Footer/                 # 页脚
│   ├── TopBar/                 # 顶部栏
│   ├── UserBar/                # 用户栏
│   ├── Language/               # 语言切换
│   ├── ProTable/               # 高级表格
│   ├── ProForm/                # 高级表单
│   ├── SearchForm/             # 搜索表单
│   ├── Pagination/             # 分页
│   ├── Upload/                 # 上传组件
│   ├── QrCode/                 # 二维码
│   ├── weChatPay/              # 微信支付
│   ├── navCard/                # 导航卡片
│   ├── productCard/            # 产品卡片
│   ├── serverCard/             # 服务器卡片
│   ├── showBox/                # 展示框
│   ├── showCard/               # 展示卡片
│   ├── TypeArea/               # 类型区域
│   ├── TipTitle/               # 提示标题
│   ├── Search/                 # 搜索
│   ├── MyEmpty/                # 空状态
│   ├── ImagePreview/           # 图片预览
│   └── ErrorMessage/           # 错误页面（404）
├── layout/                     # 布局系统
│   ├── index.vue               # 布局入口（含侧边栏、主内容区）
│   ├── Main.vue                # 主内容区
│   ├── SubMenu.vue             # 侧边栏子菜单
│   └── components/             # 布局组件
├── routes/                     # 路由配置
│   ├── index.ts                # 路由入口（静态路由 + 导航守卫）
│   └── dynamicRouter.ts        # 动态路由初始化
├── store/
│   ├── modules/
│   │   ├── useAuth.ts          # 权限与菜单
│   │   ├── user.ts             # 用户信息、登录态
│   │   ├── global.ts           # 全局状态
│   │   ├── agic.ts             # AGI-C 相关
│   │   ├── order.ts            # 订单相关
│   │   ├── networkProduct.ts   # 网络产品相关
│   │   └── interface.ts        # 接口相关状态
│   └── index.ts                # 统一导出
├── api/                        # API 接口
│   └── types/                  # API 类型定义
├── utils/
│   ├── request.ts              # Axios 封装
│   ├── auth.ts                 # Token 读写
│   └── ...                     # 其他工具
├── languages/                  # 国际化语言包
│   └── modules/                # 各语言文件
├── hooks/                      # 组合式函数（Composables）
├── directives/                 # 自定义指令
│   └── modules/
├── config/
│   └── index.ts                # 全局常量（首页、登录页、白名单等）
├── styles/                     # 全局样式
├── types/                      # 全局类型定义
└── assets/                     # 静态资源
    ├── images/                 # 图片
    ├── icon/                   # 图标
    ├── js/                     # JS 文件
    ├── json/                   # JSON 数据
    └── userSideImage/          # 用户侧图片
```

### 路由架构

项目采用 **Hash 模式**路由，结合**静态路由**与**动态路由**：

**静态路由**（`routes/index.ts`）：
- `/` → 重定向到 `/home`
- `/home` — 首页容器，包含多个子路由（应用市场、模型市场、数据市场、新闻、算力列表、网络产品、AI大模型、合作伙伴、帮助文档、邀请、活动中心等）
- `/login` — 登录页
- `/docsView/*` — 静态文档页（产品协议、隐私政策、服务条款、信息控制）
- `/layout` — 控制台布局容器（动态路由挂载点）
- `/:catchAll(.*)` — 404

**动态路由**（`routes/dynamicRouter.ts`）：
- 通过 `initDynamicRouter()` 根据后端返回的菜单权限动态生成
- 使用 `import.meta.glob("@/view/**/*.vue")` 批量导入视图组件
- 动态路由挂载到 `layout` 路由下（`router.addRoute("layout", ...)`）
- `meta.isFull` 为 true 的路由直接添加到根级

**路由守卫逻辑**：
1. 白名单路由（`/aiModel`, `/networkProduct`, `/networkProductDetail`）无需登录
2. `LOGIN_PAGE_LIST` 中的页面需要登录，未登录跳转 `/login`
3. 有 Token 但菜单未加载 → 调用 `initDynamicRouter()` 加载权限菜单

### 布局系统

- **首页布局**：`Home/index.vue` 作为首页容器，使用自定义布局（顶部栏、Banner、内容区、页脚）
- **控制台布局**：`layout/index.vue` 作为后台管理布局，包含侧边栏导航（`SubMenu.vue`）和主内容区（`Main.vue`）
- 动态路由页面默认挂载在 `layout` 布局下

### 状态管理（Pinia）

| Store | 用途 |
|-------|------|
| `useAuth` | 菜单权限列表、扁平化菜单、设置权限菜单 |
| `user` | 用户信息、Token、登录态、获取用户信息 |
| `global` | 全局状态 |
| `agic` | AGI-C 相关状态 |
| `order` | 订单相关状态 |
| `networkProduct` | 网络产品相关状态 |
| `interface` | 接口相关状态 |

### HTTP 请求封装

基于 Axios 的 `src/utils/request.ts`：

- **baseURL**：`import.meta.env.VITE_APP_BASE_API`，默认 `/api`
- **超时**：60 秒
- **请求拦截**：自动携带 `Authorization: Bearer <token>`（可通过 `isToken: false` 关闭）
- **响应拦截**：
  - `code === 200` → 返回 `res.data`
  - `code === 401` → `ElMessage.error` 提示登录过期，清空 token，跳转登录页
  - `code ∈ [403, 500, 501, 1001, 5000]` → `ElMessage.error`
  - 其他错误 → `ElNotification.error`

### 适配方案

项目使用 **postcss-px-to-viewport** 进行响应式适配：

- **设计稿宽度**：1980px
- **转换单位**：vw
- **转换精度**：6 位小数
- **排除类名**：`ignore-` 前缀的类名不转换
- **媒体查询**：支持媒体查询中的 px 转换

这意味着编写 CSS 时直接使用 px 单位，构建时会自动转换为 vw。

### 国际化

使用 **vue-i18n** 实现多语言：

- 语言包存放于 `src/languages/modules/`
- 组件 `Language/` 提供语言切换功能
- 项目中需使用 `$t('key')` 或 `t('key')` 进行文本国际化

### Markdown 预览

使用 **@kangc/v-md-editor** 实现 Markdown 内容渲染。

### 表单设计

使用 **@form-create/element-ui** 实现动态表单渲染。

## 开发规范

### 页面开发

- 页面文件存放于 `src/view/`，按业务模块组织
- 列表页通常包含 `list/index.vue` 和 `detail/index.vue`
- 首页子路由作为 `Home` 的 children 配置在静态路由中
- 控制台页面通过动态路由挂载到 `layout` 下

### 组件开发

- 公共组件存放于 `src/components/`，使用 PascalCase 命名
- 使用 `unplugin-vue-components` 自动导入自定义组件
- 使用 `unplugin-auto-import` 自动导入 Vue API
- Element Plus 组件无需手动引入

### API 接口

- 接口文件存放于 `src/api/`，按业务域拆分
- 类型定义存放于 `src/api/types/`
- 统一使用 `request` 实例发起请求

### 样式约定

- 使用 SCSS 编写样式
- px 单位在构建时会自动转为 vw（基于 1980px 设计稿）
- 需要排除转换的类名使用 `ignore-` 前缀

## 配置说明

### Vite 配置（`vite.config.ts`）

- **路径别名**：`@` → `./src`
- **基础路径**：由 `VITE_PUBLIC_PATH` 决定，默认 `/`
- **开发服务器**：`host: "0.0.0.0"`，端口由 `VITE_PORT` 决定
- **代理**：`/api` → `VITE_PROXY_URL`，rewrite 去掉 `/api`
- **构建**：
  - 生产环境自动移除 `console.log` 和 `debugger`（由 `VITE_DROP_CONSOLE` 控制）
  - 静态资源输出到 `assets/js/`、`assets/css/` 等目录
- **全局变量**：`__APP_INFO__` 包含包信息和构建时间
- **插件**：通过 `build/plugins.ts` 统一管理（Vue、自动导入、组件自动导入、Setup 语法糖扩展、Gzip 压缩等）

### PostCSS 配置

通过 `vite.config.ts` 的 `css.postcss.plugins` 配置 `postcss-px-to-viewport`：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `viewportWidth` | 1980 | UI 设计稿宽度 |
| `unitPrecision` | 6 | 转换精度 |
| `propList` | `["*"]` | 全部 CSS 属性转换 |
| `viewportUnit` | `vw` | 视窗单位 |
| `selectorBlackList` | `["ignore-"]` | 排除的类名前缀 |
| `minPixelValue` | 1 | 小于等于 1px 不转换 |
| `mediaQuery` | true | 媒体查询中转换 |

## 重要约定

- **路由模式**：使用 `createWebHashHistory`，部署时无需服务端配置。
- **设计稿基准**：1980px，所有 px 值在构建时会自动转为 vw。
- **Token 存储**：通过 `js-cookie` 读写，key 由 `auth.ts` 管理。
- **登录态**：`user.IsUserLogin` 控制登录状态，`LOGIN_PAGE_LIST` 定义需要登录的页面。
- **白名单**：`['/aiModel', '/networkProduct', '/networkProductDetail']` 无需登录即可访问。
- **动态路由**：控制台页面通过后端菜单权限动态生成，组件路径映射到 `src/view/` 下的 `.vue` 文件。
- **布局区分**：
  - 门户页面（首页、市场等）使用 `Home` 布局
  - 控制台页面（云服务器、容器、账单等）使用 `layout` 布局
- **请求超时**：60 秒，长于常规项目，可能涉及文件上传或复杂查询。
