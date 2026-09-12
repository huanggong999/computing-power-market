# DEBUG REPORT

- 症状：在官网控制台点击顶部“算力市场-new”后被提示重新登录。
- 根因：`ux-user-main/src/components/TopBar/index.vue` 的顶部菜单跳转逻辑被改为修改 `window.location.href` 并强制 reload。整页刷新后重新初始化用户信息，触发登录态校验，导致跳转登录页。
- 修复：恢复为应用内 `router.push(link)`，不再强制刷新页面。
- 验证：`ux-user-main` 执行 `npm run build` 通过。
- 状态：DONE_WITH_CONCERNS（当前浏览器会话无法复现交互，但根因由近期提交差异确认，构建验证通过）。
