# 官网算力市场跳登录回归

- 症状：从 `/console` 点击顶部“算力市场-new”后跳转 `/login`，并提示“登陆已过期，请重新登录”。
- 根因：官网公开路由 `/computeListNew` 被访问时，旧路由守卫会在令牌存在且动态菜单为空时调用 `initDynamicRouter()`；该流程请求 `/pc/customer/info`，本地登录态失效时返回 401，全局 axios 拦截器清除 token 并跳转登录。与此同时，官网新增的火山云目录代理在当前运行后端返回 401，也会触发同一个全局跳转逻辑。
- 修复：
  - `ux-user-main/src/routes/index.ts`：只对 `layout` 下的控制台路由初始化动态菜单，公开官网页面不再触发用户信息鉴权。
  - `ux-user-main/src/utils/request.ts`：增加 `skipAuthRedirect` 请求选项；标记为可选公开接口时，401 仅作为接口错误返回，不清除登录态、不跳登录。
  - `ux-user-main/src/api/volcanoGpu.ts`：火山云目录请求启用 `skipAuthRedirect`。
- 验证：官网构建成功；在无有效登录态下直接打开 `http://localhost:4173/#/computeListNew`，页面保持在算力市场页并能加载公开 GPU 市场列表，不再跳转登录。
- 备注：当前本地运行后端的 `/pc/gpu/volcano/catalog` 仍返回 401，后端最新公开权限代码需要重启/重新部署后才能正常展示火山云目录数据；前端已避免该接口失败影响官网页面和登录态。
