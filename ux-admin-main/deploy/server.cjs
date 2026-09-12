// 静态托管 + /api 反代,适合"前端打包后丢到 Linux,需要在部署时塑定后端地址"的场景
// 用法: PORT=5180 BACKEND_URL=http://10.0.0.10:8082 node server.cjs
//
// 依赖: express + http-proxy-middleware,体积小,启动快
// 不依赖 nginx;Hash 路由模式无需 history fallback,但仍兜了一份以兼容未来切 history 模式

const path = require("path");
const express = require("express");
const { createProxyMiddleware } = require("http-proxy-middleware");

const PORT = parseInt(process.env.PORT || "5180", 10);
const HOST = process.env.HOST || "0.0.0.0";
const BACKEND_URL = process.env.BACKEND_URL || "http://127.0.0.1:8082";
const DIST_DIR = path.resolve(__dirname, "dist");

const app = express();

// /api 反代到真实后端,前端 axios 的 baseURL 是 /api,这里要 rewrite 把 /api 前缀去掉
app.use(
  "/api",
  createProxyMiddleware({
    target: BACKEND_URL,
    changeOrigin: true,
    pathRewrite: { "^/api": "" },
    onError(err, _req, res) {
      console.error("[proxy error]", err.message);
      if (!res.headersSent) {
        res.writeHead(502, { "Content-Type": "application/json; charset=utf-8" });
      }
      res.end(JSON.stringify({ code: 502, msg: "后端连接失败: " + err.message }));
    },
  })
);

// 静态文件
app.use(express.static(DIST_DIR, { index: "index.html", extensions: ["html"] }));

// SPA fallback (Hash 模式其实用不到,但兜底一下,future-proof)
app.get("*", (_req, res) => {
  res.sendFile(path.join(DIST_DIR, "index.html"));
});

app.listen(PORT, HOST, () => {
  console.log(`[ux-admin-main] listening on http://${HOST}:${PORT}`);
  console.log(`[ux-admin-main] /api -> ${BACKEND_URL}`);
  console.log(`[ux-admin-main] dist  = ${DIST_DIR}`);
});
