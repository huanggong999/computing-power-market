import { ConfigEnv, UserConfig, defineConfig, loadEnv } from "vite";
import { resolve } from "path";
import pkg from "./package.json";
import dayjs from "dayjs";
import { wrapperEnv } from "./build/getEnv";
import { createVitePlugins } from "./build/plugins";

const { dependencies, devDependencies, name, version } = pkg;
const __APP_INFO__ = {
  pkg: { dependencies, devDependencies, name, version },
  lastBuildTime: dayjs().format("YYYY-MM-DD HH:mm:ss"),
};
// 路径
const alias = { "@": resolve(__dirname, "./src") };
export default defineConfig(({ mode }: ConfigEnv): UserConfig => {
  const root = process.cwd();
  const env = loadEnv(mode, root);
  const ViteEnv = wrapperEnv(env);

  return {
    base: ViteEnv.VITE_PUBLIC_PATH,
    root,
    define: {
      __APP_INFO__: JSON.stringify(__APP_INFO__),
    },
    plugins: createVitePlugins(ViteEnv),
    resolve: { alias },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/var.scss";`,
        },
      },
    },
    server: {
      host: "0.0.0.0",
      port: ViteEnv.VITE_PORT,
      open: ViteEnv.VITE_OPEN,
      cors: true,
      proxy: {
        [ViteEnv.VITE_APP_BASE_API]: {
          target: ViteEnv.VITE_PROXY_URL,
          changeOrigin: true,
          rewrite: (path) => path.replace(new RegExp(`^/api`), ""),
        },
      },
    },
    esbuild: {
      pure: env.VITE_DROP_CONSOLE ? ["console.log", "debugger"] : [],
    },
    build: {
      outDir: "dist", // 默认打包位置
      minify: "esbuild",
      reportCompressedSize: false,
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          // Static resource classification and packaging
          chunkFileNames: "assets/js/[name]-[hash].js",
          entryFileNames: "assets/js/[name]-[hash].js",
          assetFileNames: "assets/[ext]/[name]-[hash].[ext]",
        },
      },
    },
  };
});
