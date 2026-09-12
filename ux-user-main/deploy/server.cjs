// 静态托管 + /api 反代,适合"前端打包后丢到 Linux,需要在部署时塑定后端地址"的场景
// 用法: PORT=5181 BACKEND_URL=http://10.0.0.10:8082 node server.cjs

const path = require("path");
const express = require("express");
const { createProxyMiddleware } = require("http-proxy-middleware");

const PORT = parseInt(process.env.PORT || "5181", 10);
const HOST = process.env.HOST || "0.0.0.0";
const BACKEND_URL = process.env.BACKEND_URL || "http://127.0.0.1:8082";
const DIST_DIR = path.resolve(__dirname, "dist");

const app = express();

// /api 反代到真实后端;前端 axios 的 baseURL 是 /api,这里 rewrite 去掉前缀
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

// 静态文件(含 dist 根下的 PDF / favicon 等)
app.use(express.static(DIST_DIR, { index: "index.html", extensions: ["html"] }));

// SPA fallback (Hash 模式其实用不到,但兜底一下)
app.get("*", (_req, res) => {
  res.sendFile(path.join(DIST_DIR, "index.html"));
});

const server = app.listen(PORT, HOST, () => {
  console.log(`[ux-user-main] listening on http://${HOST}:${PORT}`);
  console.log(`[ux-user-main] /api -> ${BACKEND_URL}`);
  console.log(`[ux-user-main] dist  = ${DIST_DIR}`);
});

server.on('error', (error) => {
  if (error.code === 'EADDRINUSE') {
    console.error(`[ux-user-main] port ${PORT} is already in use; stop the existing process or choose another PORT`);
    process.exitCode = 1;
    return;
  }
  console.error('[ux-user-main] server error:', error);
  process.exitCode = 1;
});
