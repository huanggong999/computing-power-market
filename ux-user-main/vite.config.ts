import { ConfigEnv, UserConfig, defineConfig, loadEnv } from "vite";
import { resolve } from "path";
import pkg from "./package.json";
import dayjs from "dayjs";
import { wrapperEnv } from "./build/getEnv";
import { createVitePlugins } from "./build/plugins";
import postCssPxToViewport from "postcss-px-to-viewport";

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
    // resolve: {
    //   alias: {
    //     "@": resolve(__dirname, "src"),
    //   },
    // },
    // css: {
    //   preprocessorOptions: {
    //     scss: {
    //       // additionalData: `@import "@/styles/var.scss";`,
    //       additionalData: `@import "@/styles/reset.scss";`,
    //     },
    //   },
    // },
    //

    css: {
      postcss: {
        plugins: [
          postCssPxToViewport({
            unitToConvert: "px", // 要转化的单位
            viewportWidth: 1980, // UI设计稿的宽度
            unitPrecision: 6, // 转换后的精度，即小数点位数
            propList: ["*"], // 指定转换的css属性的单位，*代表全部css属性的单位都进行转换
            viewportUnit: "vw", // 指定需要转换成的视窗单位，默认vw
            fontViewportUnit: "vw", // 指定字体需要转换成的视窗单位，默认vw
            selectorBlackList: ["ignore-"], // 指定不转换为视窗单位的类名，
            minPixelValue: 1, // 默认值1，小于或等于1px则不进行转换
            mediaQuery: true, // 是否在媒体查询的css代码中也进行转换，默认false
            replace: true, // 是否转换后直接更换属性值
            landscape: false, // 是否处理横屏情况
          }),
        ],
      },
    },
    //

    server: {
      host: "0.0.0.0",
      port: ViteEnv.VITE_PORT,
      open: ViteEnv.VITE_OPEN,
      cors: true,
      proxy: {
        // "/api": {
        //   target: "http://192.168.1.27:8081",
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
      assetsDir: "assets/js/",
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
