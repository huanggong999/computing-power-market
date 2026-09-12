#!/usr/bin/env bash
# 停止 ai-cloud-system

set -eu

cd "$(dirname "$0")/.."
DEPLOY_HOME="$(pwd)"

APP_NAME="ai-cloud-system"
PID_FILE="${DEPLOY_HOME}/${APP_NAME}.pid"
WAIT_SECS="${WAIT_SECS:-30}"

if [ ! -f "${PID_FILE}" ]; then
  echo "[INFO] 未发现 pid 文件，${APP_NAME} 可能未运行"
  exit 0
fi

PID=$(cat "${PID_FILE}")
if ! kill -0 "${PID}" 2>/dev/null; then
  echo "[INFO] 进程 ${PID} 已不存在，清理 pid 文件"
  rm -f "${PID_FILE}"
  exit 0
fi

echo "[INFO] 发送 SIGTERM 给 pid=${PID}（最多等待 ${WAIT_SECS}s）"
kill "${PID}"

for _ in $(seq 1 "${WAIT_SECS}"); do
  if ! kill -0 "${PID}" 2>/dev/null; then
    rm -f "${PID_FILE}"
    echo "[OK] ${APP_NAME} 已停止"
    exit 0
  fi
  sleep 1
done

echo "[WARN] 优雅停止超时，发送 SIGKILL"
kill -9 "${PID}" || true
rm -f "${PID_FILE}"
echo "[OK] ${APP_NAME} 已强制停止"
