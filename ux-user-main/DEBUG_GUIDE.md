# AI智算门户端(ux-user-main) — VS Code 编译运行调试指南

> 项目类型: **Vue 3 + Vite + TypeScript + Element Plus**  PC 端门户网站
> 编辑器: VS Code + Chrome
> 文档版本: 2026-05

---

## 目录

- [第 1 步: 环境准备](#第-1-步-环境准备)
- [第 2 步: VS Code 安装与扩展](#第-2-步-vs-code-安装与扩展)
- [第 3 步: 打开项目](#第-3-步打开项目)
- [第 4 步: 安装依赖](#第-4-步安装依赖)
- [第 5 步: 调整端口(避免 sudo)](#第-5-步调整端口避免-sudo)
- [第 6 步: 启动开发服务器](#第-6-步启动开发服务器)
- [第 7 步: 配置 VS Code 调试器](#第-7-步配置-vs-code-调试器)
- [第 8 步: 打断点 + 调试](#第-8-步打断点--调试)
- [第 9 步: 修改代码的标准工作流](#第-9-步修改代码的标准工作流)
- [第 10 步: 项目特有注意点](#第-10-步项目特有注意点)
- [第 11 步: 常见问题速查](#第-11-步常见问题速查)
- [第 12 步: 每日工作流总结](#第-12-步每日工作流总结)

---

## 第 1 步: 环境准备

### 1.1 安装 Node.js

终端检查:

```bash
node -v   # 需要 ≥ 18, 推荐 18 或 20
npm -v    # 需要 ≥ 9
```

未安装请用 [nvm](https://github.com/nvm-sh/nvm)(macOS):

```bash
brew install nvm
mkdir -p ~/.nvm
# 把以下两行加到 ~/.zshrc:
# export NVM_DIR="$HOME/.nvm"
# [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
source ~/.zshrc
nvm install 20
nvm use 20
```

### 1.2 安装 Chrome

VS Code 调试需要 Chrome 浏览器。任何版本都行。

---

## 第 2 步: VS Code 安装与扩展

### 2.1 装 VS Code

<https://code.visualstudio.com/>

### 2.2 必装扩展

打开左侧 **Extensions**(`⇧⌘X`), 搜索并安装:

| 扩展名 | 搜索关键字 | 作用 |
|--------|-----------|------|
| **Vue - Official** | `Vue.volar` | `.vue` 文件语法高亮、类型推断 |
| **Better Comments** | `aaron-bond.better-comments` | 彩色注释(项目已推荐) |
| **ESLint** | `dbaeumer.vscode-eslint` | 代码检查 |
| **Prettier - Code: formatter** | `esbenp.prettier-vscode` | 格式化(可选) |
| **i18n Ally** | `lokalise.i18n-ally` | 国际化字串可视化(`src/languages/`) |

### 2.3 (可选) 禁用冲突扩展

如果以前装过 `octref.vetur`, **必须禁用**, 否则和 Volar 冲突:
Extensions 面板搜 `Vetur` → `Disable (Workspace)`。

---

## 第 3 步: 打开项目

二选一:

**方法 A — 终端命令**

```bash
cd /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main
code .
```

**方法 B — VS Code UI**

`File` → `Open Folder...` → 选 `ux-user-main` 目录 → `Open`。

---

## 第 4 步: 安装依赖

### 4.1 打开内置终端

VS Code 内按 `⌃` + `` ` ``(Control + 反引号)。终端默认在项目根目录。

### 4.2 安装

```bash
npm install
```

第一次大约 1~3 分钟。看到 `added xxx packages` 即成功。

### 4.3 网络慢的国内备选

```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 4.4 验证

```bash
ls node_modules | head
```

能看到 `vue`、`vite`、`element-plus` 等目录即可。

---

## 第 5 步: 调整端口(避免 sudo)

`.env` 中默认 `VITE_PORT = 80`, macOS 上 80 端口需要 root 权限, 直接 `npm run dev` 会报 `EACCES`。

### 5.1 修改端口

VS Code 左侧资源管理器打开 `.env`, 把:

```ini
VITE_PORT = 80
```

改成:

```ini
VITE_PORT = 5173
```

保存(`⌘S`)。

> 之后浏览器访问地址变成 `http://localhost:5173`。

---

## 第 6 步: 启动开发服务器

### 6.1 在终端启动

```bash
npm run dev
```

成功标志(终端输出大概长这样):

```
  VITE v5.x.x  ready in 800 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://192.168.x.x:5173/
```

`.env` 里 `VITE_OPEN = true` 会自动打开默认浏览器。

> ⚠️ 这个终端**不要关闭**, 关掉就停服务了。后面调试要再开一个新终端 (右上角 `+` 图标)。

### 6.2 验证页面可访问

浏览器看到登录页或首页, 说明前端服务已 OK。接口返回 401 / 跨域报错都暂时不用管, 先把"页面能跑"这步打通。

---

## 第 7 步: 配置 VS Code 调试器

目标: 让你在 `.vue` / `.ts` 文件里点行号打断点, 程序能停下来, 看变量。

### 7.1 创建 `launch.json`

1. 左侧切到 **Run and Debug** 面板 (快捷键 `⇧⌘D`)。
2. 点中间蓝色文字 **"create a launch.json file"**。
3. 弹出选择面板, 选 **"Web App (Chrome)"**。
4. VS Code 会在 `.vscode/` 下生成 `launch.json`。

### 7.2 替换为以下内容

把 `.vscode/launch.json` 整个内容替换为:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Debug Vite (Chrome)",
      "url": "http://localhost:5173",
      "webRoot": "${workspaceFolder}",
      "sourceMapPathOverrides": {
        "webpack:///src/*": "${webRoot}/src/*",
        "/src/*": "${webRoot}/src/*",
        "/@fs/*": "/*"
      }
    }
  ]
}
```

> 关键字段说明:
> - `url`: 开发服务器地址, 必须和 `npm run dev` 输出的 `Local` 一致。
> - `webRoot`: 源码根目录, 这里是项目根。
> - `sourceMapPathOverrides`: Vite 的虚拟路径映射回真实源文件, **没有这个断点不会命中**。

保存(`⌘S`)。

---

## 第 8 步: 打断点 + 调试

### 8.1 确保 dev 服务器已在跑

第 6 步那个终端必须是绿色的、正在运行 `npm run dev`。如果不是, 先启动它。

### 8.2 启动调试器

1. **Run and Debug** 面板 (`⇧⌘D`) 顶部下拉框选 `Debug Vite (Chrome)`。
2. 点旁边绿色 ▶️, 或按 **F5**。
3. VS Code 会拉起一个全新的 Chrome 窗口 (注意: 这是被调试器接管的实例, **不要用你日常的 Chrome 自己手动开页面**)。
4. 这个 Chrome 自动访问 `http://localhost:5173`。

### 8.3 打第一个断点

举个具体例子: 调试 axios 请求拦截器。

1. 资源管理器打开 `src/utils/request.ts`。
2. 找到请求拦截器函数 (大约在文件中段的 `service.interceptors.request.use(...)`)。
3. 鼠标移到行号左侧 → 点出现的红圆点 → 出现实心红点 = 断点已设。
4. 切回被调试的 Chrome, 刷新页面或触发一次接口调用。
5. VS Code 自动跳到前台, 这一行高亮黄色 = 命中断点。

### 8.4 调试时会用到的功能

| 操作 | 快捷键 | 作用 |
|------|--------|------|
| 继续 (Continue) | F5 | 跑到下一个断点 |
| 单步跳过 (Step Over) | F10 | 执行当前行, 不进入函数内部 |
| 单步进入 (Step Into) | F11 | 进入被调用函数 |
| 跳出 (Step Out) | ⇧F11 | 跳出当前函数 |
| 停止调试 | ⇧F5 | 关闭被调试的 Chrome |

左侧面板可以看到:
- **VARIABLES**: 当前作用域所有变量值
- **WATCH**: 自己加表达式监控
- **CALL STACK**: 调用栈
- **BREAKPOINTS**: 所有断点列表

底部 **DEBUG CONSOLE** 可以直接输入 JS 表达式, 例如输入 `config.headers` 立即看值。

### 8.5 验证 source map 工作正常

如果断点显示**空心灰圆**, 鼠标悬停提示 `Unverified breakpoint`:
- 检查 `launch.json` 中 `webRoot` 与 `sourceMapPathOverrides`。
- 确认 `npm run dev` 在跑, 而不是访问了打包后的 `dist`。
- 在 Chrome DevTools (F12) → Sources 标签看左侧能否找到原始 `.vue` / `.ts` 路径。

---

## 第 9 步: 修改代码的标准工作流

### 9.1 先理解项目结构 (关键 6 个目录)

```
src/
├── view/            ← 页面在这里 (按业务模块分目录)
│   ├── Home/        ← 首页及子路由容器
│   ├── Cloud/       ← 云服务器管理
│   ├── ContainerConsole/ ← 容器控制台
│   ├── Console/     ← 用户控制台
│   ├── Login/       ← 登录
│   └── ...
├── api/             ← 后端接口调用
├── components/      ← 公共组件
├── routes/          ← 路由 (静态 + 动态)
├── store/           ← 状态管理 (Pinia)
├── layout/          ← 布局系统 (首页布局 + 控制台布局)
└── languages/       ← 国际化语言包
```

### 9.2 修改流程举例: 改一个页面文字

假设要把首页 Banner 标题改一下。

1. **定位文件**
   `⇧⌘F` 打开全局搜索, 搜旧文字 → 找到对应 `src/view/Home/xxx.vue`。
2. **打开并修改**
   双击文件, 改 `<template>` 中的文字。
3. **保存(⌘S)**
   保存瞬间, Chrome 页面**自动局部刷新**(HMR, 热模块替换), 不用手动刷新。
4. **验证**
   切到被调试的 Chrome, 看页面上文字是否变了。

### 9.3 修改流程举例: 改一个接口逻辑

假设要给某个列表加分页参数。

1. **找到 api 调用**
   搜接口名 → `src/api/xxx.ts`, 改请求参数定义和函数。
2. **找到调用方**
   `⌘点击` 函数名跳到使用处 (Volar 提供跳转)。
3. **改 view 代码**
   修改 `src/view/.../xxx.vue` 中调用方式。
4. **打断点验证**
   在 `request.ts` 拦截器和 view 的回调里都打断点, F5 调试, 确认请求体、响应正确。
5. **保存**
   HMR 自动应用。

### 9.4 修改流程举例: 改一个组件

公共组件比如 `ProTable`:

1. 修改 `src/components/ProTable/index.vue` 或子文件。
2. 所有用到 `ProTable` 的页面都会自动热更新。
3. 如果热更新失败 (报错), HMR 会在浏览器右上角弹红色错误条; 按提示去 VS Code 里改正, 保存即可。

### 9.5 改完检查类型

修改 TypeScript 文件后, 运行一次:

```bash
npm run build
```

会执行 `vue-tsc` 做类型检查 + `vite build` 打包。如果类型错误会在终端报红色, 按行号修就行。这步不是调试必须, 但发版/提交前一定要做。

---

## 第 10 步: 项目特有注意点

### 10.1 响应式适配 — px 自动转 vw

项目使用 **postcss-px-to-viewport** 进行响应式适配, 设计稿基准宽度 **1980px**:

- 写 CSS 时直接用 `px` 单位, 构建时自动转为 `vw`。
- 如果某个元素**不需要转换**, 给它的类名加 `ignore-` 前缀。
- 配置在 `vite.config.ts:44-62`。

> 这意味着你在浏览器 DevTools 里看到的元素尺寸是 vw, 不是 px, 这是正常的。

### 10.2 路由模式 — Hash 模式

项目使用 `createWebHashHistory`, 部署时无需服务端配置。URL 形如:

```
http://localhost:5173/#/home
http://localhost:5173/#/layout/console
```

### 10.3 两套布局

| 布局 | 用途 | 对应页面 |
|------|------|---------|
| `Home` 布局 | 门户首页、市场列表等前台页面 | `view/Home/` 下的子路由 |
| `layout` 布局 | 用户控制台、云服务器、账单等后台页面 | 动态路由挂载到 `layout` 下 |

新增页面前先判断属于哪套布局, 再决定路由配置位置:
- 前台页面 → `src/routes/index.ts` 静态路由
- 控制台页面 → 后端返回菜单, 由 `dynamicRouter.ts` 动态挂载

### 10.4 国际化

- 语言包存放于 `src/languages/modules/`
- 页面中使用 `$t('key')` 或 `t('key')`
- 切换语言通过 `src/components/Language/` 组件

### 10.5 环境变量文件

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境: 代理地址、端口、标题等 |
| `.env.production` | 生产环境配置 |
| `.env` | 通用配置 (端口、自动打开浏览器等) |

关键变量:
- `VITE_APP_BASE_API` — API 基础路径, 默认 `/api`
- `VITE_PROXY_URL` — 开发代理目标, 默认 `https://eyunai.net/api`
- `VITE_PUBLIC_PATH` — 部署基础路径, 默认 `/`
- `VITE_GLOB_APP_TITLE` — 页面标题, 默认 "AI智算"

---

## 第 11 步: 常见问题速查

| 现象 | 原因 / 解决 |
|------|------------|
| `npm run dev` 报 `EADDRINUSE: 80` | 80 端口被占用, 改 `.env` 的 `VITE_PORT` 为 5173 |
| `npm run dev` 报 `EACCES` | macOS 80 端口需要 root, 改 `.env` 的 `VITE_PORT` |
| 断点显示空心(unverified) | source map 没对上; 确认 `npm run dev` 在跑; 检查 `webRoot` 路径 |
| `.vue` 文件没语法高亮 | Volar 没装, 或 Vetur 没禁用 |
| 改了代码不刷新 | dev 服务器挂了, 看终端, 可能是语法错误, 修好后会自动恢复 |
| 401 不停跳登录 | 后端 token 过期, 重新登录; 或 `.env.development` 中 `VITE_PROXY_URL` 后端地址不通 |
| 跨域报错 | 开发环境已通过 vite 代理 `/api → VITE_PROXY_URL`, 别直接打开 `index.html` |
| 页面样式错乱、尺寸不对 | 本项目用 postcss-px-to-viewport, 设计稿 1980px; 确保浏览器窗口宽度 >= 1280px 查看效果 |
| 国际化文字没显示 | 检查 `src/languages/modules/` 对应语言文件里是否有该 key |
| 控制台没 `console.log` 输出 | 生产构建会被 `esbuild.pure` 干掉, 但 `npm run dev` 不会; 若被干掉看 `.env.development` 中 `VITE_DROP_CONSOLE` |

---

## 第 12 步: 每日工作流总结

```text
早上:
1. 打开 VS Code
2. ⌃ + ` 打开终端 → npm run dev
3. F5 → 调试 Chrome 自动打开 http://localhost:5173

开发:
4. 改代码: 在 VS Code 编辑 → 保存 → Chrome 自动热更新
5. 调试: F5 启调试 Chrome → 打断点 → F10/F11 单步

收工:
6. ⇧F5 停调试 → 终端 Ctrl+C 停 dev 服务器
7. (提交前) npm run build 确认类型/构建无错误
```

---

## 附录: 相关链接

- Vue 3 文档: <https://cn.vuejs.org/>
- Vite 文档: <https://cn.vitejs.dev/>
- Element Plus 文档: <https://element-plus.org/zh-CN/>
- Pinia 文档: <https://pinia.vuejs.org/zh/>
- vue-i18n 文档: <https://vue-i18n.intlify.dev/>
- postcss-px-to-viewport 文档: <https://github.com/evrone/postcss-px-to-viewport>

---

**有疑问按这个顺序排查:**
1. VS Code 终端报错信息 (最重要)
2. 浏览器 DevTools Console
3. Network 面板看接口请求/响应
4. 第 11 节常见问题表
5. 还不行 — 把上面 1-3 的截图发给同事
