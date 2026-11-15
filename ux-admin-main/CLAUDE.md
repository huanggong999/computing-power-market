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

这是一个基于 **Vue 3 + Vite** 的前端管理后台项目，使用 **pnpm** 作为包管理器。

```bash
# 安装依赖
pnpm install

# 启动开发服务器（默认端口由 .env.development 中的 VITE_PORT 决定，自动打开浏览器）
pnpm dev

# 生产构建
pnpm build

# 预览生产构建
pnpm preview
```

### 环境配置

项目使用 Vite 的 `loadEnv` 加载环境变量：

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境配置（代理地址、端口、标题等） |
| `.env.production` | 生产环境配置 |
| `.env` | 通用配置 |

关键环境变量：
- `VITE_APP_BASE_API` — API 基础路径，默认 `/api`
- `VITE_PROXY_URL` — 开发代理目标地址，当前指向 `http://115.190.35.186:8081`
- `VITE_PUBLIC_PATH` — 公共基础路径，默认 `/`
- `VITE_GLOB_APP_TITLE` — 页面标题，默认"逸云数智"

## 项目概述

**逸云数智管理后台** — AI 智算项目管理后台。基于 Vue 3 + TypeScript + Element Plus 构建，提供云资源管理、订单管理、用户中心、财务管理、运营配置、内容管理等模块的后台管理能力。

### 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Vue 3.3.8、TypeScript 5.4.5 |
| 构建工具 | Vite 5 |
| UI 组件库 | Element Plus 2.8 |
| 状态管理 | Pinia 2 + pinia-plugin-persistedstate |
| 路由 | Vue Router 4（Hash 模式） |
| HTTP 客户端 | Axios |
| 样式 | Sass/SCSS |
| 图标 | @element-plus/icons-vue、unplugin-icons |
| 编辑器 | vditor（Markdown）、@wangeditor/editor（富文本） |
| 表单设计 | @form-create/element-ui、@form-create/designer |
| 工具库 | dayjs、js-cookie、mitt、nprogress、@vueuse/core |

## 架构概览

### 目录结构

```
src/
├── api/                    # API 接口（按业务域拆分）
│   ├── types/              # API 类型定义
│   ├── Login.ts            # 登录相关
│   ├── account.ts          # 账户管理
│   ├── userCenter.ts       # 用户中心
│   ├── financialCenter.ts  # 财务中心
│   ├── order/              # 订单相关
│   ├── contentManagement/  # 内容管理
│   ├── productManagement/  # 产品管理
│   ├── promotionManagement/# 推广管理
│   ├── operate/            # 运营管理
│   └── system/             # 系统管理
├── views/                  # 页面视图（与 api 目录对应）
│   ├── Login/
│   ├── home/
│   ├── contentManagement/  # 内容管理（banner、文档、云服务器、算力服务器等）
│   ├── financialCenter/    # 财务中心（账单、发票、充值、订单合同等）
│   ├── operate/            # 运营管理（配置、优惠券、消息、活动等）
│   ├── order/              # 订单管理（订单列表、工单）
│   ├── productManagement/  # 产品管理（产品列表、采购表单、咨询等）
│   ├── promotionManagement/# 推广管理（活动配置、推广大使、返利、提现）
│   ├── system/             # 系统管理（账号、菜单、角色）
│   └── userCenter/         # 用户中心（用户管理、企业认证、授信、代金券）
├── components/             # 公共组件
│   ├── ProTable/           # 高级表格
│   ├── ProForm/            # 高级表单
│   ├── SearchForm/         # 搜索表单
│   ├── WangEditor/         # 富文本编辑器封装
│   ├── MarkdownEditor/     # Markdown 编辑器封装
│   ├── Upload/             # 上传组件
│   ├── ImportExcel/        # Excel 导入
│   └── ...
├── router/
│   ├── index.ts            # 路由入口（导航守卫、白名单、动态路由初始化）
│   ├── modules/
│   │   ├── staticRouter.ts # 静态路由（登录、404、首页等）
│   │   └── dynamicRouter.ts# 动态路由（根据后端菜单生成）
├── store/
│   ├── modules/
│   │   ├── auth.ts         # 权限与菜单
│   │   ├── user.ts         # 用户信息
│   │   ├── global.ts       # 全局状态（主题、布局、语言等）
│   │   ├── tabs.ts         # 标签页状态
│   │   └── keepAlive.ts    # 缓存页面状态
├── layout/                 # 布局系统
│   ├── LayoutVertical/     # 纵向布局
│   ├── LayoutClassic/      # 经典布局
│   ├── LayoutTransverse/   # 横向布局
│   ├── LayoutColumns/      # 分栏布局
│   └── components/         # 布局公共组件（Header、Menu、Tabs、ThemeDrawer 等）
├── hooks/                  # 组合式函数（Composables）
├── utils/
│   ├── request.ts          # Axios 封装（请求/响应拦截）
│   ├── auth.ts             # Token 读写
│   └── ...
├── styles/                 # 全局样式、主题变量
├── config/
│   ├── index.ts            # 全局常量（首页、登录页、白名单、主题色）
│   ├── serviceLoading.ts   # 全屏 Loading 控制
│   └── nprogress.ts        # 路由进度条配置
└── types/                  # 全局类型定义
```

### 路由架构

项目采用 **Hash 模式**路由，结合**静态路由**与**动态路由**：

- **静态路由**：登录页、404、首页等固定路由，定义在 `router/modules/staticRouter.ts`
- **动态路由**：根据后端返回的菜单列表动态生成，通过 `initDynamicRouter()` 在路由守卫中初始化
- **路由白名单**：`ROUTER_WHITE_LIST = ["/login", "/500", "/test"]`，无需登录即可访问
- **导航守卫逻辑**：
  1. 无 Token 且非白名单 → 跳转登录页
  2. 有 Token 但菜单未加载 → 调用 `initDynamicRouter()` 加载权限菜单
  3. 访问登录页且已登录 → 留在当前页

### 状态管理（Pinia）

| Store | 用途 |
|-------|------|
| `auth` | 菜单列表、按钮权限、扁平化菜单、扁平化权限列表 |
| `user` | 用户信息、登录逻辑 |
| `global` | 主题色、暗黑模式、布局模式、语言、组件大小、面包屑等 |
| `tabs` | 已打开标签页、当前激活标签 |
| `keepAlive` | 需要缓存的页面名称列表 |

### HTTP 请求封装

基于 Axios 的 `src/utils/request.ts`：

- **baseURL**：`import.meta.env.VITE_APP_BASE_API`，默认 `/api`
- **超时**：10 秒
- **请求拦截**：自动携带 `Authorization: Bearer <token>`（可通过 `isToken: false` 关闭）；默认显示全屏 Loading（可通过 `noLoading: true` 关闭）
- **响应拦截**：
  - `code === 200` → 返回 `res.data`
  - `code === 401` → Token 过期，提示重新登录并跳转
  - `code ∈ [400, 403, 404, 500, 1001, 1002]` → `ElMessage.error`
  - 其他错误码 → `ElNotification.error`

### 布局系统

支持 4 种布局模式，通过 `global` store 切换：

- **纵向布局（LayoutVertical）**：左侧菜单 + 顶部 Header
- **经典布局（LayoutClassic）**：顶部菜单 + 顶部 Header
- **横向布局（LayoutTransverse）**：顶部横向菜单
- **分栏布局（LayoutColumns）**：左侧窄栏（一级菜单）+ 右侧宽栏（子菜单）+ 内容区

每种布局通过 `layout/components/` 中的公共组件（Header、Menu、Tabs、Main、Footer、ThemeDrawer）组合而成。

### 关键组件约定

**ProTable**：
- 位于 `components/ProTable/`
- 封装 Element Plus 的 `el-table` + `el-pagination`，统一表格/分页逻辑
- 通常配合 `components/SearchForm/` 使用

**ProForm / SearchForm**：
- `ProForm`：表单生成与编辑
- `SearchForm`：查询条件表单，通常位于表格上方

**Upload**：
- 封装文件上传逻辑，支持多图/单图/文件上传
- 通常对接后端或第三方对象存储

**WangEditor / MarkdownEditor**：
- `WangEditor`：富文本编辑器封装
- `MarkdownEditor`：基于 vditor 的 Markdown 编辑器封装

## 开发规范

### API 接口组织

- 接口按业务域拆分文件，存放于 `src/api/`
- 类型定义存放于 `src/api/types/`，按模块命名（如 `account.d.ts`、`userCenter.d.ts`）
- 统一使用 `request` 实例发起请求，返回类型通常为 `Result<T>` 或 `PageResult<T>`

### 视图与路由

- 视图文件存放于 `src/views/`，目录结构与 `src/api/` 对应
- 每个业务模块一个目录，目录下可包含 `index.vue` 和子组件
- 组件命名使用 PascalCase，文件命名使用 camelCase 或 kebab-case

### 组件注册

- 使用 `unplugin-vue-components` 和 `unplugin-auto-import` 自动导入 Vue、Element Plus、@vueuse 的 API 和组件
- 自定义公共组件存放于 `src/components/`，无需手动注册即可在模板中使用

### 样式约定

- 全局样式变量定义于 `src/styles/var.scss`，通过 Vite 的 `additionalData` 自动注入每个 SCSS 文件
- 主题配色通过 `src/styles/theme/` 管理，支持动态切换
- 默认主题色：`#009688`（可在 `src/config/index.ts` 修改 `DEFAULT_PRIMARY`）

## 配置说明

### Vite 配置（`vite.config.ts`）

- **路径别名**：`@` → `./src`
- **SCSS 全局注入**：`@use "@/styles/var.scss";`
- **开发服务器**：
  - `host: "0.0.0.0"`
  - 端口由 `.env` 中的 `VITE_PORT` 决定
  - 代理规则：`/api` → `VITE_PROXY_URL`，rewrite 去掉 `/api` 前缀
- **构建优化**：
  - 生产环境自动移除 `console.log` 和 `debugger`（由 `VITE_DROP_CONSOLE` 控制）
  - 静态资源分类打包到 `assets/js/`、`assets/css/` 等目录
- **插件**：通过 `build/plugins.ts` 统一管理（Vue、自动导入、组件自动导入、Icons、Setup 语法糖扩展、Gzip 压缩等）

### 环境变量

| 变量 | 说明 |
|------|------|
| `VITE_APP_ENV` | 环境标识（development / production） |
| `VITE_PUBLIC_PATH` | 部署基础路径 |
| `VITE_APP_BASE_API` | API 基础路径（默认 `/api`） |
| `VITE_PROXY_URL` | 开发代理目标 |
| `VITE_DROP_CONSOLE` | 构建时是否移除 console |
| `VITE_GLOB_APP_TITLE` | 应用标题 |
| `VITE_GLOB_APP_LOGO` | Logo 标识 |
| `VITE_GLOB_APP_ICO` | Favicon 标识 |

## 重要约定

- **无根 POM**：这是一个纯前端项目，后端接口通过代理访问。
- **路由模式**：使用 `createWebHashHistory`，部署时无需服务端配置。
- **Token 存储**：通过 `js-cookie` 读写，key 为 `token`。
- **登录页**：`LOGIN_URL = "/login"`，首页：`HOME_URL = "/home"`。
- **权限控制**：基于后端返回的菜单列表生成动态路由，`meta.hideMenu` 控制菜单隐藏，`meta.isKeepAlive` 控制页面缓存。
- **请求 Loading**：默认全局 Loading，接口传 `{ noLoading: true }` 可关闭。
- **响应码处理**：`401` 统一处理为登出并刷新页面，其他错误码通过 `ElMessage` 或 `ElNotification` 提示。
