#!/usr/bin/env bash
# 在 *开发机* 上执行,产出可交付给运维的 tgz 包。
# 用法:
#   cd ux-admin-main
#   ./deploy/build-package.sh
#
# 产出: ux-admin-main-deploy-YYYYMMDD-HHMM.tgz (放在项目根目录)
#       展开后是一个 ux-admin-main-deploy/ 目录,直接 ./install.sh 即可上线

set -eu

cd "$(dirname "$0")/.."
PROJECT_ROOT="$(pwd)"
DEPLOY_DIR="${PROJECT_ROOT}/deploy"
PKG_NAME="ux-admin-main-deploy"
TS="$(date +%Y%m%d-%H%M)"
OUT_DIR="${PROJECT_ROOT}/${PKG_NAME}"
TGZ="${PROJECT_ROOT}/${PKG_NAME}-${TS}.tgz"

# 1. 安装前端依赖
if [ ! -d node_modules ]; then
  if command -v pnpm >/dev/null 2>&1; then
    pnpm install
  else
    echo "[WARN] pnpm 未安装,改用 npm"
    npm install
  fi
fi

# 2. 编译前端
if command -v pnpm >/dev/null 2>&1; then
  pnpm build
else
  npm run build
fi

if [ ! -d dist ]; then
  echo "[ERROR] dist/ 没生成,构建失败"
  exit 1
fi

# 3. 组装交付目录
rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"
cp -r dist "${OUT_DIR}/dist"
cp "${DEPLOY_DIR}/server.cjs"   "${OUT_DIR}/server.cjs"
cp "${DEPLOY_DIR}/package.json" "${OUT_DIR}/package.json"
cp "${DEPLOY_DIR}/install.sh"   "${OUT_DIR}/install.sh"
cp "${DEPLOY_DIR}/app"          "${OUT_DIR}/app"
cp "${DEPLOY_DIR}/.env.example" "${OUT_DIR}/.env.example"
chmod +x "${OUT_DIR}/install.sh" "${OUT_DIR}/app"

# 4. 把部署说明一起打进去 (可选)
if [ -f "${PROJECT_ROOT}/DEPLOYMENT.md" ]; then
  cp "${PROJECT_ROOT}/DEPLOYMENT.md" "${OUT_DIR}/DEPLOYMENT.md"
elif [ -f "${PROJECT_ROOT}/DEPLOYMENT-ux-admin-main.md" ]; then
  cp "${PROJECT_ROOT}/DEPLOYMENT-ux-admin-main.md" "${OUT_DIR}/DEPLOYMENT-ux-admin-main.md"
fi

# 5. tar
cd "${PROJECT_ROOT}"
tar czf "${TGZ}" "${PKG_NAME}"
rm -rf "${OUT_DIR}"

echo
echo "=================================================="
echo "  打包完成: ${TGZ}"
echo "  下一步:"
echo "    scp ${TGZ##*/} user@server:/opt/"
echo "    ssh user@server"
echo "    cd /opt && tar xzf ${TGZ##*/} && cd ${PKG_NAME}"
echo "    BACKEND_URL=http://127.0.0.1:8082 ./install.sh"
echo "=================================================="
