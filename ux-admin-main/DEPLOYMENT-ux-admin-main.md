# ux-admin-main Linux 部署使用文档

本文档说明如何把 `ux-admin-main` 这个 Vue 3 + Vite 管理后台前端项目部署到 Linux 服务器。

---

## 1. 项目概览

- **类型**:纯前端项目(Vue 3 + Vite + Element Plus)
- **构建产物**:静态文件 `dist/`
- **路由**:Hash 模式(部署对 nginx 兜底友好)
- **运行模式**:Node 进程(`express` + `http-proxy-middleware`)托管 `dist/`,并把 `/api` 反代到由对方部署时塑定的后端
- **不依赖** nginx / Docker / pm2

---

## 2. 端到端流程

```
┌──────────────────┐                ┌─────────────────────────────────┐
│ 开发机 (你)       │                │  Linux 服务器 (运维 / 客户)      │
│                  │                │                                 │
│ pnpm install     │                │                                 │
│ pnpm build       │                │                                 │
│ build-package.sh ├── tgz ────────►│  tar xzf ux-admin-main-deploy*  │
│                  │                │  ./install.sh                   │
│                  │                │  (输入或传入 BACKEND_URL)        │
│                  │                │  → http://server:5180/         │
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
cd ux-admin-main
./deploy/build-package.sh
```

会做的事:

1. `pnpm install` (没装依赖时)
2. `pnpm build` 生成 `dist/`
3. 组装 `ux-admin-main-deploy/`,内含:
   - `dist/`
   - `server.cjs` — Node 静态托管 + /api 反代
   - `package.json` — 运行时只依赖 express + http-proxy-middleware
   - `install.sh` — 一键安装脚本
   - `app` — 启停脚本(start/stop/restart/status/logs)
   - `.env.example` — 配置样例
4. `tar czf ux-admin-main-deploy-YYYYMMDD-HHMM.tgz`

最终在项目根目录下产出一个 tgz 文件,这就是给运维 / 客户的交付包。

### 3.3 上传到服务器

```bash
scp ux-admin-main-deploy-*.tgz user@<server>:/opt/
```

---

## 4. 服务器:一键安装

### 4.1 服务器要求

| 项目 | 要求 |
|------|------|
| 系统 | 任意主流 Linux(CentOS / Ubuntu / Anolis 等) |
| Node.js | 18+ |
| 出网 | 能装 npm 包(express、http-proxy-middleware) |
| 端口 | 默认 5180,可改 |

#### 装 Node.js 18+

```bash
# CentOS / Anolis / RHEL
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs

# Ubuntu / Debian
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo bash -
sudo apt-get install -y nodejs

# 验证
node -v   # 期望 v18 / v20 / v22
```

### 4.2 解包 + 一键安装

```bash
ssh user@<server>
cd /opt
tar xzf ux-admin-main-deploy-*.tgz
cd ux-admin-main-deploy

# 方式 A:交互式输入后端地址
./install.sh

# 方式 B:非交互(自动化场景)
BACKEND_URL=http://127.0.0.1:8082 ./install.sh

# 方式 C:同时塑定端口和注册 systemd
BACKEND_URL=http://127.0.0.1:8082 PORT=5180 USE_SYSTEMD=1 sudo -E ./install.sh
```

`install.sh` 会做:

1. 检查 Node.js >= 18
2. 检查 `dist/` 存在
3. `npm install --omit=dev` 安装运行时两个依赖
4. 生成 `.env`(从环境变量读 BACKEND_URL/PORT/HOST,缺失则交互输入)
5. **试运行 3 秒**,确认能拉起,失败会打印 `logs/stdout.log` 末尾 50 行
6. 可选 `USE_SYSTEMD=1` 时注册 systemd 单元(需要 root)

完成后访问:`http://<server>:5180/`

---

## 5. 运行控制

```bash
cd /opt/ux-admin-main-deploy

./app start      # 启动
./app status     # 查看状态(运行中: exit=0;未运行: exit=3)
./app logs       # tail -f 标准输出
./app stop       # 优雅停止(SIGTERM,15s 后 SIGKILL)
./app restart    # 重启
```

---

## 6. 配置说明

`.env` 文件支持三个变量:

```env
# 必填:后端 API 地址。前端 axios 的 baseURL 是 /api,server.cjs 把 /api 反代到这里,
# 同时去掉 /api 前缀(对应原 vite.config.ts 中的 rewrite 行为)
BACKEND_URL=http://127.0.0.1:8082

# 监听端口,默认 5180
PORT=5180

# 监听地址,默认 0.0.0.0(所有网卡)。需要只本机访问就改成 127.0.0.1
HOST=0.0.0.0
```

修改后:

```bash
./app restart
```

### 6.1 后端在哪几种情况

| 后端位置 | BACKEND_URL |
|----------|-------------|
| 同一台 Linux 上的 ai-cloud-system(prod profile) | `http://127.0.0.1:8082` |
| 同一台机器 dev profile | `http://127.0.0.1:8081` |
| 内网另一台机器 | `http://10.0.0.10:8082` |
| 公网域名 | `https://api.example.com` |

> 公网 https 后端时 `http-proxy-middleware` 会自动握手,无需额外配置。

---

## 7. 用 systemd 托管(可选,推荐)

`install.sh` 加 `USE_SYSTEMD=1` 跑就会自动注册。也可以手动:

`/etc/systemd/system/ux-admin-main.service`:

```ini
[Unit]
Description=ux-admin-main
After=network.target

[Service]
Type=forking
WorkingDirectory=/opt/ux-admin-main-deploy
ExecStart=/opt/ux-admin-main-deploy/app start
ExecStop=/opt/ux-admin-main-deploy/app stop
ExecReload=/opt/ux-admin-main-deploy/app restart
PIDFile=/opt/ux-admin-main-deploy/ux-admin-main.pid
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now ux-admin-main
sudo systemctl status ux-admin-main
journalctl -u ux-admin-main -f
```

---

## 8. 升级流程

### 8.1 在开发机重新打包

```bash
cd ux-admin-main
./deploy/build-package.sh
scp ux-admin-main-deploy-*.tgz user@<server>:/opt/
```

### 8.2 在服务器替换 dist 并重启

```bash
ssh user@<server>
cd /opt
# 备份当前版本
mv ux-admin-main-deploy ux-admin-main-deploy.bak.$(date +%Y%m%d_%H%M%S)
# 解包新版本
tar xzf ux-admin-main-deploy-*.tgz
cd ux-admin-main-deploy
# 复用旧 .env(避免再次输入后端地址)
cp ../ux-admin-main-deploy.bak.*/.env . 2>/dev/null || true
./install.sh   # 已存在 .env 时会跳过生成
```

或者只换 dist(更轻量):

```bash
cd /opt/ux-admin-main-deploy
tar xzf /opt/ux-admin-main-deploy-*.tgz --strip-components=1 -C . 'ux-admin-main-deploy/dist'
./app restart
```

---

## 9. 健康检查

```bash
# 端口探活
ss -ltnp | grep 5180

# 静态文件
curl -sI http://127.0.0.1:5180/

# 反代是否通(替换为后端某个真实路径)
curl -sI http://127.0.0.1:5180/api/login
```

---

## 10. 日志

| 文件 | 内容 |
|------|------|
| `logs/stdout.log` | nohup 重定向的 stdout/stderr,启动失败必看 |

实时查看:

```bash
./app logs
# 等价于
tail -f /opt/ux-admin-main-deploy/logs/stdout.log
```

---

## 11. 安全建议

1. **限定监听地址**:如果 admin 只给内网管理员用,把 `.env` 里 `HOST=127.0.0.1`,前面套 ssh 隧道或 nginx
2. **关浏览器 console 自动打印**:`.env.production` 里 `VITE_DROP_CONSOLE = true` 已开
3. **后端 CORS**:由于走 `/api` 反代,前端浏览器看到的请求是同源,后端不必为 admin 开 CORS

---

## 12. 常见问题

### 12.1 `node: command not found`

照 4.1 装 Node.js 18+。

### 12.2 启动后访问 5180 拿到 502 / 后端连接失败

- 看 `./app logs`,会有 `[proxy error]`
- `curl <BACKEND_URL>` 验证后端能连通
- 多半是 `BACKEND_URL` 写错或后端没启动

### 12.3 端口被占

```bash
echo "PORT=5181" > /tmp/p && cat /opt/ux-admin-main-deploy/.env >> /tmp/p
# 或者直接编辑 .env 改 PORT
vi /opt/ux-admin-main-deploy/.env
./app restart
```

### 12.4 页面打开是空白 / 资源 404

- `dist/` 是不是被 `install.sh` 找到了:`ls /opt/ux-admin-main-deploy/dist/index.html`
- `.env.production` 里 `VITE_PUBLIC_PATH = /`,如果客户要部署到子路径(例如 `/admin/`),需要在打包前改这一行,然后 `pnpm build` 重新打

### 12.5 想改后端地址不重启

不行,Node 进程启动时已经把 `BACKEND_URL` 烤进 `createProxyMiddleware`。改 `.env` 后必须 `./app restart`。

### 12.6 想关掉一键脚本,手动跑

```bash
cd /opt/ux-admin-main-deploy
npm install --omit=dev
PORT=5180 BACKEND_URL=http://127.0.0.1:8082 node server.cjs
```

---

## 13. 卸载

```bash
cd /opt/ux-admin-main-deploy
./app stop

# 用了 systemd
sudo systemctl disable --now ux-admin-main
sudo rm /etc/systemd/system/ux-admin-main.service
sudo systemctl daemon-reload

# 删除目录
cd /opt
rm -rf ux-admin-main-deploy ux-admin-main-deploy-*.tgz
```

---

## 14. 快速参考

### 14.1 开发机

```bash
cd ux-admin-main
./deploy/build-package.sh
# → ux-admin-main-deploy-YYYYMMDD-HHMM.tgz
```

### 14.2 服务器(全新部署)

```bash
cd /opt
tar xzf ux-admin-main-deploy-*.tgz
cd ux-admin-main-deploy
BACKEND_URL=http://127.0.0.1:8082 ./install.sh
# → 浏览器打开 http://<server>:5180/
```

### 14.3 服务器(运行控制)

```bash
cd /opt/ux-admin-main-deploy
./app start | stop | restart | status | logs
```

---

**适用版本**:ux-admin-main 0.0.0
**最后更新**:2026-06-20
