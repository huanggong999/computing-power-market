#!/usr/bin/env bash
# ux-user-main 一键安装脚本
#
# 用法:
#   ./install.sh                            # 交互式输入 BACKEND_URL/PORT
#   BACKEND_URL=http://10.0.0.10:8082 ./install.sh
#   BACKEND_URL=http://10.0.0.10:8082 PORT=5181 USE_SYSTEMD=1 ./install.sh

set -eu

cd "$(dirname "$0")"
DEPLOY_HOME="$(pwd)"
APP_NAME="ux-user-main"

echo "=================================================="
echo "  ${APP_NAME} 一键安装"
echo "  目录: ${DEPLOY_HOME}"
echo "=================================================="

# 1. 检查 Node
if ! command -v node >/dev/null 2>&1; then
  echo "[ERROR] 未检测到 node 命令"
  echo "        请先安装 Node.js 18+,例如:"
  echo "          curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -"
  echo "          sudo yum install -y nodejs    # CentOS / Anolis"
  echo "        或:"
  echo "          curl -fsSL https://deb.nodesource.com/setup_20.x | sudo bash -"
  echo "          sudo apt-get install -y nodejs   # Ubuntu / Debian"
  exit 1
fi

NODE_MAJOR=$(node -p 'process.versions.node.split(".")[0]')
if [ "${NODE_MAJOR}" -lt 18 ]; then
  echo "[ERROR] Node.js 版本过低: $(node -v),需要 >= 18"
  exit 1
fi
echo "[OK] Node.js: $(node -v)"

# 2. 检查 dist
if [ ! -d "${DEPLOY_HOME}/dist" ]; then
  echo "[ERROR] dist/ 不存在: ${DEPLOY_HOME}/dist"
  echo "        交付包应该包含 dist/。如果是从 git clone 拉的代码,先执行:"
  echo "          cd .. && pnpm install && pnpm build"
  echo "          cp -r dist deploy/"
  exit 1
fi
echo "[OK] dist/ 存在"

# 3. 安装运行时依赖
echo "[INFO] 安装运行时依赖..."
if command -v pnpm >/dev/null 2>&1; then
  pnpm install --prod --silent
elif command -v npm >/dev/null 2>&1; then
  npm install --omit=dev --silent --no-audit --no-fund
else
  echo "[ERROR] 未检测到 pnpm 或 npm"
  exit 1
fi
echo "[OK] 依赖安装完成"

# 4. 生成 .env
ENV_FILE="${DEPLOY_HOME}/.env"
if [ ! -f "${ENV_FILE}" ]; then
  if [ -z "${BACKEND_URL:-}" ]; then
    if [ -t 0 ]; then
      printf "请输入后端 API 地址 (例如 http://127.0.0.1:8082): "
      read -r BACKEND_URL
    else
      echo "[ERROR] 非交互模式必须传 BACKEND_URL,例如:"
      echo "        BACKEND_URL=http://127.0.0.1:8082 ./install.sh"
      exit 1
    fi
  fi
  PORT="${PORT:-5181}"
  HOST_ENV="${HOST:-0.0.0.0}"
  cat > "${ENV_FILE}" <<EOF
BACKEND_URL=${BACKEND_URL}
PORT=${PORT}
HOST=${HOST_ENV}
EOF
  echo "[OK] 生成 .env"
else
  echo "[INFO] .env 已存在,跳过生成 (如需修改,直接编辑 ${ENV_FILE})"
fi

cat "${ENV_FILE}"

# 5. 试运行
echo "[INFO] 试运行 3 秒,确认能拉起..."
chmod +x "${DEPLOY_HOME}/app"
"${DEPLOY_HOME}/app" start
sleep 3
if "${DEPLOY_HOME}/app" status >/dev/null 2>&1; then
  echo "[OK] 启动成功"
  PORT_FROM_ENV=$(grep -E '^PORT=' "${ENV_FILE}" | cut -d= -f2)
  echo "     访问: http://<server>:${PORT_FROM_ENV}/"
else
  echo "[ERROR] 启动失败,日志:"
  echo "          ${DEPLOY_HOME}/app logs"
  tail -n 50 "${DEPLOY_HOME}/logs/stdout.log" 2>/dev/null || true
  exit 1
fi

# 6. systemd (可选)
if [ "${USE_SYSTEMD:-0}" = "1" ]; then
  if [ "$(id -u)" -ne 0 ]; then
    echo "[WARN] USE_SYSTEMD=1 需要 root,跳过 systemd 注册"
  else
    UNIT_FILE="/etc/systemd/system/${APP_NAME}.service"
    cat > "${UNIT_FILE}" <<EOF
[Unit]
Description=${APP_NAME}
After=network.target

[Service]
Type=forking
WorkingDirectory=${DEPLOY_HOME}
ExecStart=${DEPLOY_HOME}/app start
ExecStop=${DEPLOY_HOME}/app stop
ExecReload=${DEPLOY_HOME}/app restart
PIDFile=${DEPLOY_HOME}/${APP_NAME}.pid
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
    systemctl daemon-reload
    systemctl enable "${APP_NAME}"
    echo "[OK] 已注册 systemd: ${UNIT_FILE}"
    echo "     systemctl start/stop/restart/status ${APP_NAME}"
  fi
fi

echo
echo "=================================================="
echo "  完成。常用命令:"
echo "    ${DEPLOY_HOME}/app status"
echo "    ${DEPLOY_HOME}/app logs"
echo "    ${DEPLOY_HOME}/app restart"
echo "=================================================="
