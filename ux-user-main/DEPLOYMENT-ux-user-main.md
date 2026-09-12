# ux-user-main Linux 部署使用文档

本文档说明如何把 `ux-user-main`(逸云数智门户端,PC 用户网站)部署到 Linux 服务器。

> 本项目和 `ux-admin-main` 同属一套技术栈,部署流程几乎一致。差异:**默认监听 5181**(避开 admin 的 5180),并且适配域名:1980px 下的 viewport 转换 + Hash 路由。

---

## 1. 项目概览

- **类型**:纯前端项目(Vue 3 + Vite + Element Plus)
- **构建产物**:静态文件 `dist/`(包含 `assets/`、`index.html`、隐私协议 PDF、AI.ico 等)
- **路由**:Hash 模式
- **运行模式**:Node 进程(`express` + `http-proxy-middleware`)托管 `dist/`,并把 `/api` 反代到由对方部署时塑定的后端
- **设计稿**:1980px,所有 px 通过 `postcss-px-to-viewport` 自动转 vw
- **WebSocket**:前端 `VITE_APP_SOCKET_URL` 直连第三方 wss(`wss://api.bountymissions.com/websocket`),**不走本进程反代**;如果对方业务用了它,确保服务器能出网到该域名

---

## 2. 端到端流程

```
┌──────────────────┐                ┌─────────────────────────────────┐
│ 开发机 (你)       │                │  Linux 服务器 (运维 / 客户)      │
│                  │                │                                 │
│ pnpm install     │                │                                 │
│ pnpm build       │                │                                 │
│ build-package.sh ├── tgz ────────►│  tar xzf ux-user-main-deploy*   │
│                  │                │  ./install.sh                   │
│                  │                │  (输入或传入 BACKEND_URL)        │
│                  │                │  → http://server:5181/         │
└──────────────────┘                └─────────────────────────────────┘
```

---

## 3. 开发机:打包

### 3.1 环境

| 项目 | 版本 |
|------|------|
| Node.js | 18+ |
| pnpm | 8+ (推荐),或用 npm |

### 3.2 一键打包

```bash
cd ux-user-main
./deploy/build-package.sh
```

会做的事:

1. `pnpm install` (没装依赖时)
2. `pnpm build` 生成 `dist/`
3. 组装 `ux-user-main-deploy/`,内含:
   - `dist/`
   - `server.cjs` — Node 静态托管 + /api 反代
   - `package.json` — 运行时只依赖 express + http-proxy-middleware
   - `install.sh` — 一键安装脚本
   - `app` — 启停脚本(start/stop/restart/status/logs)
   - `.env.example` — 配置样例
4. `tar czf ux-user-main-deploy-YYYYMMDD-HHMM.tgz`

最终在项目根目录下产出一个 tgz 文件。

### 3.3 上传到服务器

```bash
scp ux-user-main-deploy-*.tgz user@<server>:/opt/
```

---

## 4. 服务器:一键安装

### 4.1 服务器要求

| 项目 | 要求 |
|------|------|
| 系统 | 任意主流 Linux |
| Node.js | 18+ |
| 出网 | 能装 npm 包(express、http-proxy-middleware);如果用 WebSocket,还需出网到 `api.bountymissions.com` |
| 端口 | 默认 5181,可改 |

#### 装 Node.js 18+

```bash
# CentOS / Anolis / RHEL
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs

# Ubuntu / Debian
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo bash -
sudo apt-get install -y nodejs

# 验证
node -v
```

### 4.2 解包 + 一键安装

```bash
ssh user@<server>
cd /opt
tar xzf ux-user-main-deploy-*.tgz
cd ux-user-main-deploy

# 方式 A:交互式输入
./install.sh

# 方式 B:非交互
BACKEND_URL=http://127.0.0.1:8082 ./install.sh

# 方式 C:塑定端口 + 注册 systemd
BACKEND_URL=http://127.0.0.1:8082 PORT=5181 USE_SYSTEMD=1 sudo -E ./install.sh
```

`install.sh` 会:

1. 检查 Node.js >= 18
2. 检查 `dist/` 存在
3. `npm install --omit=dev` 安装运行时两个依赖
4. 生成 `.env`
5. 试运行 3 秒,失败会打印 `logs/stdout.log` 末尾 50 行
6. 可选 `USE_SYSTEMD=1` 注册 systemd 单元

完成后访问:`http://<server>:5181/`

---

## 5. 运行控制

```bash
cd /opt/ux-user-main-deploy

./app start
./app status
./app logs
./app stop
./app restart
```

---

## 6. 配置说明

`.env` 三个变量:

```env
BACKEND_URL=http://127.0.0.1:8082
PORT=5181
HOST=0.0.0.0
```

### 6.1 后端在哪几种情况

| 后端位置 | BACKEND_URL |
|----------|-------------|
| 同一台 Linux 上的 ai-cloud-system(prod profile) | `http://127.0.0.1:8082` |
| 同一台机器 dev profile | `http://127.0.0.1:8081` |
| 内网另一台机器 | `http://10.0.0.10:8082` |
| 公网域名 | `https://ai.api.lingyangtech.com` |

> `.env.production` 里历史的 `VITE_PROXY_URL=https://ai.api.lingyangtech.com` **只在 dev 环境的 vite-server 用**,生产已被本部署套件的 `BACKEND_URL` 取代。

### 6.2 与 ux-admin-main 同机部署

如果 admin 和 user 都装在同一台 Linux:

| 项目 | 默认端口 |
|------|----------|
| ux-admin-main | 5180 |
| ux-user-main  | 5181 |
| ai-cloud-system (后端) | 8082 |

不会冲突,可以同时安装、同时跑。

---

## 7. systemd 托管(可选)

`install.sh` 加 `USE_SYSTEMD=1` 自动注册。手动版:

`/etc/systemd/system/ux-user-main.service`:

```ini
[Unit]
Description=ux-user-main
After=network.target

[Service]
Type=forking
WorkingDirectory=/opt/ux-user-main-deploy
ExecStart=/opt/ux-user-main-deploy/app start
ExecStop=/opt/ux-user-main-deploy/app stop
ExecReload=/opt/ux-user-main-deploy/app restart
PIDFile=/opt/ux-user-main-deploy/ux-user-main.pid
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now ux-user-main
sudo systemctl status ux-user-main
journalctl -u ux-user-main -f
```

---

## 8. 升级流程

### 8.1 在开发机重新打包

```bash
cd ux-user-main
./deploy/build-package.sh
scp ux-user-main-deploy-*.tgz user@<server>:/opt/
```

### 8.2 在服务器替换 dist 并重启

完整方式:

```bash
ssh user@<server>
cd /opt
mv ux-user-main-deploy ux-user-main-deploy.bak.$(date +%Y%m%d_%H%M%S)
tar xzf ux-user-main-deploy-*.tgz
cd ux-user-main-deploy
cp ../ux-user-main-deploy.bak.*/.env . 2>/dev/null || true
./install.sh
```

只换 dist(轻量):

```bash
cd /opt/ux-user-main-deploy
tar xzf /opt/ux-user-main-deploy-*.tgz --strip-components=1 -C . 'ux-user-main-deploy/dist'
./app restart
```

---

## 9. 健康检查

```bash
ss -ltnp | grep 5181

curl -sI http://127.0.0.1:5181/

# 反代是否通
curl -sI http://127.0.0.1:5181/api/login
```

---

## 10. 日志

| 文件 | 内容 |
|------|------|
| `logs/stdout.log` | nohup stdout/stderr,启动失败必看 |

```bash
./app logs
# 等价于
tail -f /opt/ux-user-main-deploy/logs/stdout.log
```

---

## 11. 安全建议

1. **限定监听地址**:门户对外的话 `HOST=0.0.0.0`;只开内网测试就 `HOST=127.0.0.1`
2. **关 console**:`.env.production` 已配 `VITE_DROP_CONSOLE = true`
3. **后端 CORS**:走 `/api` 反代,前端浏览器看到的请求是同源,后端不必为 user 端开 CORS

---

## 12. 常见问题

### 12.1 `node: command not found`

照 4.1 装 Node.js 18+。

### 12.2 启动后访问 5181 拿到 502

- 看 `./app logs`,会有 `[proxy error]`
- `curl <BACKEND_URL>` 验证后端能连通
- 多半是 `BACKEND_URL` 写错或后端没启动

### 12.3 端口被占

```bash
vi /opt/ux-user-main-deploy/.env   # 改 PORT=5182
./app restart
```

### 12.4 页面空白 / 资源 404

- `ls /opt/ux-user-main-deploy/dist/index.html`
- `.env.production` 的 `VITE_PUBLIC_PATH=/`,要部署到子路径(如 `/portal/`)需要打包前改这一行

### 12.5 字号 / 间距全乱

`postcss-px-to-viewport` 按 1980px 设计稿转 vw。生产页面在很窄/很宽屏可能与预期不一致——这是设计选择,不是部署问题。

### 12.6 WebSocket 连不上

`VITE_APP_SOCKET_URL` 是 *直连第三方 wss*,不经本进程反代。检查:
- 服务器到 `api.bountymissions.com:443` 出网
- 浏览器到该域名出网
- 如果业务不需要 socket,忽略即可

### 12.7 想改后端地址不重启

不行,Node 启动时已经把 `BACKEND_URL` 烤进 `createProxyMiddleware`。改 `.env` 后 `./app restart`。

### 12.8 不想用一键脚本,手动跑

```bash
cd /opt/ux-user-main-deploy
npm install --omit=dev
PORT=5181 BACKEND_URL=http://127.0.0.1:8082 node server.cjs
```

---

## 13. 卸载

```bash
cd /opt/ux-user-main-deploy
./app stop

sudo systemctl disable --now ux-user-main 2>/dev/null || true
sudo rm -f /etc/systemd/system/ux-user-main.service
sudo systemctl daemon-reload

cd /opt
rm -rf ux-user-main-deploy ux-user-main-deploy-*.tgz
```

---

## 14. 快速参考

### 14.1 开发机

```bash
cd ux-user-main
./deploy/build-package.sh
# → ux-user-main-deploy-YYYYMMDD-HHMM.tgz
```

### 14.2 服务器(全新部署)

```bash
cd /opt
tar xzf ux-user-main-deploy-*.tgz
cd ux-user-main-deploy
BACKEND_URL=http://127.0.0.1:8082 ./install.sh
# → http://<server>:5181/
```

### 14.3 服务器(运行控制)

```bash
cd /opt/ux-user-main-deploy
./app start | stop | restart | status | logs
```

---

**适用版本**:ux-user-main(missionreward) 0.0.0
**最后更新**:2026-06-20
