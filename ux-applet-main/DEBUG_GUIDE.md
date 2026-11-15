# 逸云智算小程序(ux-applet) — VS Code H5 端调试与开发指南

> 项目类型:**uni-app + Vue 3 + Vite + TypeScript**
> 目标端:**H5**(浏览器)
> 编辑器:VS Code + Chrome
> 文档版本:2026-05

> 提示:本项目本质是一个 uni-app 跨端工程,可同时编译到微信小程序、App 等。本指南**只覆盖 H5 端** — 这是日常开发最快、调试最舒服的端,80% 的业务/接口/样式都可以在这里调通,只在涉及微信特有 API 时再切换到小程序端。

---

## 目录

- [第 1 步:环境准备](#第-1-步环境准备)
- [第 2 步:VS Code 安装与扩展](#第-2-步vs-code-安装与扩展)
- [第 3 步:打开项目](#第-3-步打开项目)
- [第 4 步:安装依赖](#第-4-步安装依赖)
- [第 5 步:配置环境变量](#第-5-步配置环境变量)
- [第 6 步:启动 H5 开发服务器](#第-6-步启动-h5-开发服务器)
- [第 7 步:配置 VS Code 调试器](#第-7-步配置-vs-code-调试器)
- [第 8 步:打断点 + 调试](#第-8-步打断点--调试)
- [第 9 步:修改代码的标准工作流](#第-9-步修改代码的标准工作流)
- [第 10 步:常见问题速查](#第-10-步常见问题速查)
- [第 11 步:每日工作流总结](#第-11-步每日工作流总结)

---

## 第 1 步:环境准备

### 1.1 安装 Node.js

终端检查:

```bash
node -v   # 需要 ≥ 18,推荐 18 或 20
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

### 1.2 安装 pnpm 或继续用 npm

项目同时存在 `package-lock.json` 和 `pnpm-lock.yaml`,两个都能用,但**只挑一个**:

- **推荐 pnpm**:速度快、磁盘占用少
  ```bash
  npm install -g pnpm
  ```
- 如果继续用 npm,后续命令把 `pnpm` 替换为 `npm` 即可。

> ⚠️ 千万别两个都用 — 会产生不一致的依赖。

### 1.3 装 Chrome

任何版本都行,VS Code 调试要用它。

---

## 第 2 步:VS Code 安装与扩展

### 2.1 装 VS Code

<https://code.visualstudio.com/>

### 2.2 必装扩展

打开左侧 **Extensions**(`⇧⌘X`),搜索并安装:

| 扩展名 | 搜索关键字 | 作用 |
|--------|-----------|------|
| **Vue - Official** | `Vue.volar` | `.vue` 文件高亮、类型推断(Volar) |
| **uni-helper** | `uni-helper.uni-helper-vscode` | uni-app API/组件智能提示 |
| **uni-app-schemas** | `uni-app-schemas` | `pages.json` / `manifest.json` 提示 |
| **EditorConfig** | `editorconfig.editorconfig` | 读项目根的 `.editorconfig` |
| **i18n Ally** | `lokalise.i18n-ally` | 国际化(`src/locale/`)字串可视化 |

### 2.3 (可选)禁用冲突扩展

以前装过 `octref.vetur` 必须禁用(和 Volar 冲突):
Extensions 面板搜 `Vetur` → `Disable (Workspace)`。

---

## 第 3 步:打开项目

二选一:

**方法 A — 终端命令**

```bash
cd /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-applet-main
code .
```

**方法 B — VS Code UI**

`File` → `Open Folder...` → 选 `ux-applet-main` → `Open`。

---

## 第 4 步:安装依赖

### 4.1 打开内置终端

VS Code 内按 `⌃` + `` ` ``(Control + 反引号),终端默认在项目根目录。

### 4.2 安装

```bash
pnpm install
# 或 npm install
```

第一次大约 1~3 分钟。看到 `Done in xxs` 即成功。

### 4.3 网络慢的国内备选

```bash
pnpm config set registry https://registry.npmmirror.com
pnpm install
```

### 4.4 验证

```bash
ls node_modules | head
```

能看到 `vue`、`vite`、`@dcloudio` 等目录即成功。

---

## 第 5 步:配置环境变量

环境变量统一放在 `env/` 目录(注意:**不是项目根**,这是 uni-app 的非默认布局)。

### 5.1 检查 `env/.env.development`

```ini
VITE_APP_API_BASEURL = 'https://eyunai.net/api'   # 后端接口地址
VITE_APP_PORT        = 2800                        # H5 端口
VITE_APP_PROXY       = false                       # 是否走代理
VITE_APP_APPID       = 'wx4a9bd7a194d518e5'        # 微信 AppID(H5 端用不到)
VITE_APP_ASSETS_URL  = '...'                       # CDN 资源地址
```

### 5.2 ⚠️ macOS 用户注意一个**已知潜在问题**

`vite.config.ts:8` 写的是:

```ts
const envBase = `${process.cwd()}\\env`;   // Windows 反斜杠
```

在 macOS 下这条路径其实是 `/path/to/project\env`,字面上不正确。好在主 `envDir: resolve(__dirname, "env")` 是对的,大多数环境变量能正常加载,但 `server.port`(读 `VITE_APP_PORT`)和 `server.proxy.target`(读 `VITE_APP_API_BASEURL`)可能失效 — 表现为:

- H5 端口不是 2800,而是 vite 默认 5173
- 代理 `/api` 找不到目标

**临时修复**(确认现象后再改,不要直接提交):

```ts
const envBase = resolve(process.cwd(), "env");
```

如果你跑起来端口/接口都对,说明系统行为没受影响,可以不动。

---

## 第 6 步:启动 H5 开发服务器

### 6.1 启动

```bash
pnpm dev:h5
# 或 npm run dev:h5
```

成功输出大致:

```
  VITE v4.x.x  ready in 1500 ms

  ➜  Local:   http://localhost:2800/
  ➜  Network: http://192.168.x.x:2800/
```

> 这个终端**不要关闭**,关掉就停了。后续要新开终端就点终端右上角 `+`。

### 6.2 浏览器手动访问验证

在普通 Chrome 里打开 `http://localhost:2800` — 应能看到登录页或首页。能看到说明前端跑起来了,**先把这一步跑通再继续**。

> 如果跨域报错,确认是通过 Vite 代理访问(`pnpm dev:h5` 启动),而不是直接打开 `index.html`。

---

## 第 7 步:配置 VS Code 调试器

目标:让你在 `.vue` / `.ts` 文件里点行号打断点,程序能停下来,看变量。

### 7.1 创建 `launch.json`

1. 左侧切到 **Run and Debug** 面板(`⇧⌘D`)
2. 点中间蓝色 **"create a launch.json file"**
3. 选 **"Web App (Chrome)"**
4. VS Code 会在 `.vscode/launch.json` 生成模板

### 7.2 替换为以下内容

把 `.vscode/launch.json` 整个替换为:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Debug uni-app H5 (Chrome)",
      "url": "http://localhost:2800",
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
> - `url`:开发服务器地址,必须和 `pnpm dev:h5` 输出的 `Local` 一致(若实际不是 2800,改这里)
> - `webRoot`:源码根,这里是项目根
> - `sourceMapPathOverrides`:Vite 虚拟路径映射回真实源文件,**没有这个断点不会命中**

保存(`⌘S`)。

---

## 第 8 步:打断点 + 调试

### 8.1 启动调试器

1. 确保第 6 步的 `pnpm dev:h5` 还在跑(终端是绿色的)
2. **Run and Debug** 面板顶部下拉选 `Debug uni-app H5 (Chrome)`
3. 按 **F5**(或点绿色 ▶️)
4. VS Code 会拉起一个**全新的 Chrome 窗口**(被调试器接管),自动打开 `localhost:2800`

> ⚠️ 这是被调试器接管的 Chrome 实例,**不要用日常 Chrome 自己手动开页面** — 那个 Chrome 没接调试器,断点不会命中。

### 8.2 打第一个断点 — 调试 HTTP 请求拦截器

举个最实用的例子:看接口请求带了什么 token、走了哪个 baseURL。

1. 资源管理器打开 `src/utils/request.ts`
2. 找到 `uni.addInterceptor("request", ...)` 内部逻辑(大约在文件中段)
3. 鼠标移到行号左侧 → 点出现的红圆点 → 出现实心红点 = 断点已设
4. 切回被调试的 Chrome,触发一次接口调用(刷新页面、点登录按钮)
5. VS Code 自动跳到前台,这一行高亮黄色 = 命中断点

### 8.3 调试常用快捷键

| 操作 | 快捷键 | 说明 |
|------|--------|------|
| 继续 (Continue) | F5 | 跑到下一个断点 |
| 单步跳过 (Step Over) | F10 | 执行当前行,不进入函数 |
| 单步进入 (Step Into) | F11 | 进入被调用函数 |
| 跳出 (Step Out) | ⇧F11 | 跳出当前函数 |
| 停止调试 | ⇧F5 | 关闭被调试的 Chrome |
| 重启调试 | ⇧⌘F5 | 重新启动调试 |

左侧四个面板:
- **VARIABLES** — 当前作用域所有变量值
- **WATCH** — 自己加表达式监控
- **CALL STACK** — 调用栈
- **BREAKPOINTS** — 所有断点列表

底部 **DEBUG CONSOLE** — 直接输入 JS 表达式,如 `config.headers` 立即看值。

### 8.4 验证 source map 工作正常

如果断点显示**空心灰圈**,鼠标悬停提示 `Unverified breakpoint`:
- 检查 `launch.json` 中 `webRoot` 与 `sourceMapPathOverrides`
- 确认 `pnpm dev:h5` 在跑,而不是访问了打包后的产物
- 在 Chrome DevTools(F12) → Sources 标签看左侧能否找到原始 `.vue` / `.ts` 路径

### 8.5 (可选)装 Vue Devtools

Chrome 应用商店搜 **"Vue.js devtools"** 安装,装完后 F12 多出 **Vue** 标签:
- Components — 看组件树、props、data
- Pinia — 看所有 store 状态、修改记录
- Router — 看当前路由参数

---

## 第 9 步:修改代码的标准工作流

### 9.1 先认识关键目录(只列最常改的 5 个)

```
src/
├── pages/         ← 主包页面(首页、登录、控制台、账单、个人中心等)
├── packageXXX/    ← 分包页面(packageMine、packageSetting、packageInvoice ...)
├── api/model/     ← 后端接口(home.ts、user.ts、order.ts ...)
├── components/    ← 公共组件(MyTabbar、MyNavTitle、MyEmpty ...)
└── store/modules/ ← Pinia 状态(auth、product、invoice ...)
```

### 9.2 改一段页面文字(最简单的修改)

**目标**:把"用户中心"页的标题改一下。

1. `⇧⌘F` 打开全局搜索 → 搜旧文字
2. 找到 `src/pages/mine/index.vue` 或 `src/packageMine/...`
3. 双击打开,改 `<template>` 中的文本
4. `⌘S` 保存
5. Chrome 自动热更新(HMR),不用手动刷新
6. 切到被调试的 Chrome 看效果

### 9.3 改一个接口

**目标**:给"获取订单列表"加分页参数。

1. `⇧⌘F` 搜接口名 → `src/api/model/order.ts`
2. 改参数定义:
   ```ts
   export const getOrderList = (data: { page: number; size: number; status?: string }) =>
     http.post('/order/list', data);
   ```
3. `⌘点击` 函数名 → VS Code 跳到调用方
4. 在调用方传新参数
5. 在 `src/utils/request.ts` 拦截器和调用方回调里都打断点
6. F5 调试 → 触发请求 → 看请求体/响应是否符合预期

### 9.4 改一个公共组件

公共组件如 `MyEmpty` / `MyNavTitle`:

1. 改 `src/components/MyEmpty/index.vue`
2. 保存 → 所有用到此组件的页面自动热更新
3. 如果热更新报错,Chrome 右上角会弹红色错误条;按提示去 VS Code 改正,保存即可

### 9.5 加一个新页面(以"我的优惠券"为例)

虽然 H5 端没有"主包/分包"包体积限制,但项目同时要编译到小程序,**继续遵循分包约定**:

1. 在 `src/packageMine/coupon/` 下新建 `index.vue`:
   ```vue
   <template>
     <view class="coupon-page">
       <MyNavTitle title="我的优惠券" />
       <MyEmpty v-if="!list.length" />
       <view v-for="item in list" :key="item.id">{{ item.name }}</view>
     </view>
   </template>

   <script setup lang="ts">
   import api from "@/api";
   const list = ref<any[]>([]);
   onMounted(async () => {
     list.value = await api.user.getCouponList();
   });
   </script>
   ```
2. **注册路由**:打开 `src/pages.json`,在 `subPackages` → `packageMine` → `pages` 数组追加:
   ```json
   { "path": "coupon/index", "style": { "navigationBarTitleText": "我的优惠券" } }
   ```
3. 保存 → 在调用方:
   ```ts
   uni.navigateTo({ url: "/packageMine/coupon/index" });
   ```

### 9.6 改完前的最终检查

提交前推荐:

```bash
# 类型检查
pnpm vue-tsc --noEmit

# 跑一遍生产构建,确认没编译错误
pnpm build:h5
```

---

## 第 10 步:常见问题速查

| 现象 | 原因 / 处理 |
|------|------------|
| `pnpm dev:h5` 启动后端口不是 2800 | 见第 5.2 节 — `vite.config.ts:8` 反斜杠路径在 macOS 失效 |
| `EADDRINUSE: 2800` | 端口被占用,改 `env/.env.development` 中 `VITE_APP_PORT` 为别的端口(如 2801) |
| `.vue` 文件没语法高亮 | 没装 Volar,或装了 Vetur 没禁用 |
| 断点显示空心灰圈(Unverified) | dev 服务器没启动 / Chrome 不是调试器拉起的那个 / `webRoot` 写错 |
| 改了代码不刷新 | dev 进程已死,看终端报错;或语法错误导致 HMR 中断,修好后会自动恢复 |
| 401 一直跳登录 | Token 过期,或 `VITE_APP_API_BASEURL` 后端地址不通,先在浏览器试一下接口能否访问 |
| 跨域报错 | 必须走 `pnpm dev:h5` 启动(用 Vite 代理),不能直接打开 `index.html` |
| 控制台 `console.log` 不输出 | `vite.config.ts:79` 配的是 `drop: ["debugger"]`,只删 debugger,console 应保留;若仍不输出确认是 dev 还是 prod 环境 |
| `tool.get("TOKEN")` 拿不到值 | 登录链路异常或被 storage 清掉,检查 `src/store/modules/auth.ts` 和 `src/utils/tool.ts` 的 set/get 顺序 |
| 用了 `wx.xxx` API 在 H5 报 `wx is not defined` | H5 端没有 wx 全局,代码必须用 `uni.xxx` 跨端 API,见 [uni-app 文档](https://uniapp.dcloud.net.cn/api/) |

---

## 第 11 步:每日工作流总结

```text
早上:
1. 打开 VS Code
2. ⌃ + ` 打开终端 → pnpm dev:h5
3. F5 → 调试 Chrome 自动起来,自动访问 localhost:2800

开发:
4. 改代码 → 保存 → Chrome 自动热更新 → 看效果
5. 出 bug → 打断点 → F10/F11 单步 → DEBUG CONSOLE 查变量 → 修

收工:
6. ⇧F5 停调试 → 终端 Ctrl+C 停 dev 服务器
7. (提交前)pnpm vue-tsc --noEmit && pnpm build:h5 确认能通过
```

---

## 附录:相关链接

- uni-app 官方文档:<https://uniapp.dcloud.net.cn/>
- uview-plus(UI 组件库)文档:<https://uiadmin.net/uview-plus/>
- Pinia 文档:<https://pinia.vuejs.org/>
- Vue 3 文档:<https://cn.vuejs.org/>

---

**有疑问按这个顺序排查:**
1. VS Code 终端报错信息(最重要)
2. 浏览器 DevTools Console
3. Network 面板看接口请求/响应
4. 第 10 节常见问题表
5. 还不行 — 把上面 1-3 的截图发给同事
