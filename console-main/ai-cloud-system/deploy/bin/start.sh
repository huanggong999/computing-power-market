#!/usr/bin/env bash
# 启动 ai-cloud-system

set -eu

# 切到脚本所在目录的上一级（deploy 根）
cd "$(dirname "$0")/.."
DEPLOY_HOME="$(pwd)"

APP_NAME="ai-cloud-system"
JAR_FILE="${DEPLOY_HOME}/lib/${APP_NAME}.jar"
CONFIG_DIR="${DEPLOY_HOME}/config"
LOG_DIR="${DEPLOY_HOME}/logs"
PID_FILE="${DEPLOY_HOME}/${APP_NAME}.pid"

PROFILE="${PROFILE:-prod}"
SERVER_PORT="${SERVER_PORT:-8082}"
JAVA_OPTS="${JAVA_OPTS:--server -Xms1g -Xmx2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}/heapdump.hprof -Dfile.encoding=UTF-8}"

if [ ! -f "${JAR_FILE}" ]; then
  echo "[ERROR] jar 不存在：${JAR_FILE}"
  echo "        请先在开发机执行：cd ai-cloud-system && mvn clean package"
  echo "        然后把 target/${APP_NAME}.jar 复制到 deploy/lib/"
  exit 1
fi

if [ -f "${PID_FILE}" ]; then
  OLD_PID=$(cat "${PID_FILE}")
  if kill -0 "${OLD_PID}" 2>/dev/null; then
    echo "[WARN] ${APP_NAME} 已在运行 (pid=${OLD_PID})，如需重启请用 restart.sh"
    exit 0
  else
    rm -f "${PID_FILE}"
  fi
fi

mkdir -p "${LOG_DIR}"

# config/ 目录里的 application-*.yml 会覆盖 jar 内的同名配置（Spring Boot 外部化配置约定）
nohup java ${JAVA_OPTS} \
  -Dspring.profiles.active="${PROFILE}" \
  -Dspring.config.additional-location="file:${CONFIG_DIR}/" \
  -Dserver.port="${SERVER_PORT}" \
  -jar "${JAR_FILE}" \
  > "${LOG_DIR}/stdout.log" 2>&1 &

NEW_PID=$!
echo "${NEW_PID}" > "${PID_FILE}"

echo "[OK] ${APP_NAME} 已启动 (pid=${NEW_PID})"
echo "     profile=${PROFILE}"
echo "     port=${SERVER_PORT}"
echo "     jar=${JAR_FILE}"
echo "     stdout=${LOG_DIR}/stdout.log"
echo "     业务日志见 ${LOG_DIR}/ 下按 logback-spring.xml 配置生成的文件"
