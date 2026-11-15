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

这是一个基于 **uni-app + Vue 3 + Vite** 的跨端小程序项目，支持微信小程序、H5、App 等多端发行。

```bash
# 安装依赖
npm install

# H5 开发
npm run dev:h5

# 微信小程序开发
npm run dev:mp-weixin

# App 开发
npm run dev:app

# 生产构建
npm run build:h5
npm run build:mp-weixin
```

### 环境配置

环境变量文件存放在 `env/` 目录下（Vite 通过 `envDir` 指定）：

| 文件 | 说明 |
|------|------|
| `env/.env.development` | 开发环境配置 |
| `env/.env.production` | 生产环境配置 |

关键环境变量：
- `VITE_APP_TITLE` — 应用标题
- `VITE_APP_API_BASEURL` — API 基础地址
- `VITE_APP_PORT` — 开发服务器端口
- `VITE_APP_PROXY` — 是否开启代理
- `VITE_APP_APPID` — 应用 APPID
- `VITE_APP_ASSETS_URL` — 静态资源地址
- `VITE_APP_SOCKET_URL` — WebSocket 地址

开发代理配置：`/api` → `VITE_APP_API_BASEURL`，rewrite 去掉 `/api` 前缀。

## 项目概述

**逸云智算小程序** — AI 智算平台移动端应用。基于 uni-app Vue3 构建，提供云资源浏览、控制台管理、账单查询、发票申请、订单管理、分销推广、用户认证等功能，支持微信小程序、H5、App 多端运行。

### 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | uni-app 3.0 (Vue 3.5.13) |
| 语言 | TypeScript 5.7.3 |
| 构建工具 | Vite 4 |
| UI 组件库 | uview-plus 3.4.1 |
| 状态管理 | Pinia 2 + pinia-plugin-persistedstate |
| 国际化 | vue-i18n 9.1.9 |
| 工具库 | dayjs、crypto-js、decimal.js、qs、clipboard、uqrcodejs |
| 微信 SDK | weixin-js-sdk |
| 表单设计 | form-create-uni |
| 样式 | Sass/SCSS |

## 架构概览

### 目录结构

```
src/
├── pages/                    # 主包页面
│   ├── index/                # 首页
│   ├── login/                # 登录/注册
│   ├── console/              # 控制台
│   ├── agic/                 # AGI-C
│   ├── bill/                 # 账单
│   ├── invoice/              # 发票管理（列表/申请/详情）
│   ├── mine/                 # 个人中心
│   ├── webView/              # 文档预览
│   ├── transferPage/         # 中转页
│   └── setting/index         # 设置
├── packageHome/              # 分包：新闻动态
│   └── newsUpdates/
├── packageConsole/           # 分包：控制台列表/详情
│   ├── list/
│   └── details/
├── packageAgic/              # 分包：AGI-C 资源详情/购买
│   ├── details/
│   └── buy/
├── packageInvoice/           # 分包：发票抬头/邮箱管理
│   ├── titleManagement/
│   └── emailManagement/
├── packageMine/              # 分包：我的（充值、订单、收支、分销）
│   ├── topUp/
│   ├── transactionHistory/
│   ├── orderCenter/
│   └── distributor/          # 分销商（申请、中心、提现、订单、团队、银行卡）
├── packageSetting/           # 分包：设置（账号、企业认证、实名认证、改密）
│   ├── accountDetail/
│   ├── mineDetail/
│   └── businessVerification/
├── packageActive/            # 分包：活动中心
│   └── activeCenter/
├── api/                      # API 接口
│   ├── model/                # 按业务模块拆分的接口文件
│   │   ├── home.ts           # 首页
│   │   ├── console.ts        # 控制台
│   │   ├── user.ts           # 用户
│   │   ├── auth.ts           # 认证
│   │   ├── order.ts          # 订单
│   │   ├── bill.ts           # 账单
│   │   ├── invoice.ts        # 发票
│   │   ├── mine.ts           # 我的
│   │   ├── activity.ts       # 活动
│   │   ├── news.ts           # 新闻
│   │   ├── networkProduct.ts # 网络产品
│   │   └── file.ts           # 文件
│   └── index.ts              # 自动聚合所有 model 模块
├── components/               # 公共组件
│   ├── MyTabbar/             # 自定义 TabBar
│   ├── MyNavTitle/           # 导航标题栏
│   ├── MyEmpty/              # 空状态
│   ├── MyDropdown/           # 下拉菜单
│   ├── MyStatus/             # 状态标签
│   ├── NavCom/               # 导航组件
│   ├── LazyLoad/             # 懒加载
│   └── ua-markdown/          # Markdown 渲染
├── store/
│   ├── modules/
│   │   ├── auth.ts           # 认证信息（Token、OpenID、UnionID、用户信息）
│   │   ├── product.ts        # 产品相关状态
│   │   ├── agicProduct.ts    # AGI-C 产品状态
│   │   └── invoice.ts        # 发票相关状态
│   └── index.ts              # 统一导出
├── utils/
│   ├── request.ts            # HTTP 请求封装（uni.request 拦截器）
│   ├── tool.ts               # 通用工具函数（localStorage 读写等）
│   ├── index.ts              # 工具函数聚合
│   ├── time.ts               # 时间处理
│   ├── image.ts              # 图片处理
│   ├── CanvasTools.ts        # Canvas 工具
│   ├── Enum.ts               # 枚举常量
│   ├── EventData.ts          # 事件数据
│   ├── loadJS.ts             # JS 动态加载
│   └── icon.ts               # 图标相关
├── config/
│   └── index.ts              # 全局配置（API 地址、超时、Token 配置等）
├── common/                   # 公共代码
├── hook/                     # 组合式函数（Composables）
├── mixins/                   # Mixins
├── locale/                   # 国际化语言包
├── styles/                   # 全局样式
├── types/                    # 全局类型定义
├── static/                   # 静态资源
│   ├── tabbar/               # TabBar 图标
│   ├── login/                # 登录页素材
│   └── mine/                 # 个人中心素材
├── uni_modules/              # uni 原生模块
│   └── form-create-uni_1.0.0/# 表单设计器
└── mp_ecard_sdk/             # 腾讯云 E 证通 SDK
```

### 页面路由与分包

项目使用 uni-app 的 `pages.json` 配置路由，采用**主包 + 分包**架构优化小程序包体积：

**主包页面（pages）**：
- `pages/index/index` — 首页
- `pages/console/index` — 控制台
- `pages/agic/index` — AGI-C
- `pages/bill/index` — 账单
- `pages/mine/index` — 个人中心
- `pages/login/index` — 登录
- `pages/invoice/index` — 发票管理
- `pages/webView/index` — 文档预览

**分包（subPackages）**：
| 分包名 | 功能 |
|--------|------|
| `packageHome` | 新闻动态 |
| `packageConsole` | 控制台列表/详情 |
| `packageAgic` | AGI-C 资源详情/购买 |
| `packageInvoice` | 发票抬头/邮箱管理 |
| `packageMine` | 充值、订单、收支、分销商 |
| `packageSetting` | 账号信息、企业/实名认证、修改密码 |
| `packageActive` | 活动中心 |

**自定义 TabBar**：
底部 5 个 Tab：首页、控制台、AGI-C、账单、个人中心。`custom: true` 表示使用自定义组件实现 TabBar。

### API 接口组织

- 接口文件存放于 `src/api/model/`，按业务域拆分（如 `home.ts`、`console.ts`、`user.ts` 等）
- `src/api/index.ts` 使用 `import.meta.globEager("./model/*.ts")` 自动聚合所有模块，以文件名作为模块名导出
- 调用方式：`api.home.getBanner()`、`api.order.createOrder()` 等

### HTTP 请求封装

基于 `uni.request` 和 `uni.addInterceptor`，位于 `src/utils/request.ts`：

- **拦截器**：`uni.addInterceptor("request", ...)` 和 `uni.addInterceptor("uploadFile", ...)` 统一处理请求
- **地址拼接**：非 `http` 开头的 URL 自动拼接 `sysConfig.API_URL`
- **超时**：30 秒
- **Token**：自动从 `tool.get("TOKEN")` 读取并附加到请求头 `Authorization: Bearer <token>`
- **响应处理**：
  - `code === 200` → resolve
  - `code === 401` → Toast 提示"登录已过期"，2 秒后跳转登录页
  - 其他错误 → Toast 提示 `msg` 或"服务器错误"

提供 `http.get(url, data, config)`、`http.post(url, data, config)`、`http.delete(url, data, config)`、`http.put(url, data, config)`、`http.upload(url, filePath, config)` 方法。

### 状态管理（Pinia）

| Store | 用途 |
|-------|------|
| `auth` | Token、OpenID、UnionID、用户信息、场景值（SCENE），持久化到 localStorage |
| `product` | 产品相关状态 |
| `agicProduct` | AGI-C 产品相关状态 |
| `invoice` | 发票相关状态 |

`tool.ts` 封装了 `localStorage` 读写，支持对象自动 JSON 序列化/反序列化。

### UI 组件库

项目使用 **uview-plus** 作为 UI 组件库，通过 `pages.json` 的 `easycom` 配置自动按需引入：

```json
{
  "easycom": {
    "autoscan": true,
    "custom": {
      "^u--(.*)": "uview-plus/components/u-$1/u-$1.vue",
      "^up-(.*)": "uview-plus/components/u-$1/u-$1.vue",
      "^u-([^-].*)": "uview-plus/components/u-$1/u-$1.vue"
    }
  }
}
```

自定义组件存放于 `src/components/`，通过 `unplugin-vue-components` 自动导入（生成 `src/components.d.ts`）。

`unplugin-auto-import` 自动导入 `vue` 和 `uni-app` 的 API（生成 `src/auto-imports.d.ts`）。

## 开发规范

### 页面开发

- 页面文件存放于 `src/pages/`（主包）或 `src/packageXXX/`（分包）
- 每个页面一个目录，包含 `index.vue` 作为入口
- 页面样式通过 `pages.json` 中的 `style` 配置导航栏标题、背景色、是否下拉刷新等
- `navigationStyle: "custom"` 表示自定义导航栏（隐藏原生导航栏）

### 组件开发

- 公共组件存放于 `src/components/`，使用 PascalCase 命名（如 `MyTabbar`、`MyEmpty`）
- 组件内使用 uview-plus 的组件时无需手动引入，easycom 会自动处理
- Vue 组合式 API 和 uni-app API 无需手动 import，auto-import 会自动处理

### 样式约定

- 全局样式变量可通过 `uni.scss` 或 `src/styles/` 管理
- Vite 配置中预留了 SCSS `additionalData` 入口（当前注释状态，可按需开启）
- 页面背景色默认 `#F5F5F5`

## 配置说明

### Vite 配置（`vite.config.ts`）

- **路径别名**：`@` → `./src`
- **环境目录**：`env/`（通过 `envDir` 指定）
- **基础路径**：`./`（相对路径，适配小程序）
- **开发服务器**：`host: "0.0.0.0"`，端口由环境变量 `VITE_APP_PORT` 决定
- **代理**：`/api` → `VITE_APP_API_BASEURL`，rewrite 去掉 `/api`
- **构建**：
  - H5 打包时静态资源输出到 `static/assets/`
  - JS/CSS 分别输出到 `static/js/`、`static/css/`
  - 生产环境移除 `debugger`
- **插件**：
  - `@dcloudio/vite-plugin-uni` — uni-app 官方 Vite 插件
  - `unplugin-vue-components` — 自定义组件自动导入
  - `unplugin-auto-import` — Vue / uni-app API 自动导入
  - `vite-plugin-restart` — 修改 `vite.config.ts` 自动重启

### 全局配置（`src/config/index.ts`）

| 配置项 | 说明 |
|--------|------|
| `APP_NAME` | 应用名称（来自环境变量） |
| `API_URL` | 接口地址（开发环境走代理，生产环境直连） |
| `APP_SOCKET_URL` | WebSocket 地址 |
| `APP_ID` | 应用 APPID |
| `ASSETS_URL` | 静态资源地址 |
| `TIMEOUT` | 请求超时 30 秒 |
| `TOKEN_NAME` | `Authorization` |
| `TOKEN_PREFIX` | `Bearer ` |
| `LS_ENCRYPTION` | localStorage 加密方式（当前为空，不加密） |
| `LS_ENCRYPTION_key` | AES 加密密钥 |

## 重要约定

- **多端兼容**：代码需考虑微信小程序、H5、App 的差异，优先使用 uni-app 跨端 API，避免直接使用 `wx.` 原生 API。
- **主包体积控制**：新增页面优先放入合适的分包，避免主包超过 2MB 限制。
- **页面路由**：跳转分包页面使用完整路径，如 `uni.navigateTo({ url: "/packageMine/topUp/index" })`。
- **TabBar 页面**：TabBar 配置的页面（首页、控制台、AGI-C、账单、个人中心）需用 `uni.switchTab` 跳转，不能用 `uni.navigateTo`。
- **登录态**：Token 通过 `tool.get("TOKEN")` / `tool.set("TOKEN", token)` 读写，同时同步到 Pinia `auth` store。401 统一跳转 `/pages/login/index?isClear=true`。
- **请求封装**：统一使用 `http.get` / `http.post` 等方法，不直接调用 `uni.request`。
- **自定义导航栏**：多数页面设置了 `navigationStyle: "custom"`，需自行在页面内实现导航栏（如使用 `MyNavTitle` 组件）。
