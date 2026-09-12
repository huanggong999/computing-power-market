// vite.config.ts
import { defineConfig, loadEnv } from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/vite@5.4.9_@types+node@20.16.14_sass@1.80.3/node_modules/vite/dist/node/index.js";
import { resolve } from "path";

// package.json
var package_default = {
  name: "project",
  private: true,
  version: "0.0.0",
  type: "module",
  scripts: {
    dev: "vite --open",
    build: "vue-tsc && vite build",
    preview: "vite preview"
  },
  dependencies: {
    "@element-plus/icons-vue": "^2.1.0",
    "@form-create/designer": "^3",
    "@form-create/element-ui": "^3.2.17",
    "@types/node": "^20.9.3",
    "@vueuse/core": "^11.1.0",
    "@wangeditor/editor": "^5.1.23",
    "@wangeditor/editor-for-vue": "^5.1.12",
    axios: "^1.6.2",
    dayjs: "^1.11.10",
    "element-plus": "^2.8.0",
    "js-cookie": "^3.0.5",
    mitt: "^3.0.1",
    nprogress: "^0.2.0",
    pinia: "^2.1.7",
    "pinia-plugin-persistedstate": "^3.2.0",
    vditor: "^3.10.7",
    "vite-plugin-compression": "^0.5.1",
    "vite-plugin-vue-setup-extend": "^0.4.0",
    vue: "^3.3.8",
    "vue-router": "^4.2.5"
  },
  devDependencies: {
    "@types/js-cookie": "^3.0.6",
    "@types/nprogress": "^0.2.3",
    "@vitejs/plugin-vue": "^4.5.0",
    sass: "^1.69.5",
    typescript: "^5.4.5",
    "unplugin-auto-import": "^0.16.7",
    "unplugin-icons": "^0.18.1",
    "unplugin-vue-components": "^0.25.2",
    vite: "^5.0.0",
    "vue-tsc": "^1.8.22"
  },
  main: "index.js",
  license: "MIT"
};

// vite.config.ts
import dayjs from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/dayjs@1.11.13/node_modules/dayjs/dayjs.min.js";

// build/getEnv.ts
var wrapperEnv = (envConf) => {
  const ret = {};
  for (const envName of Object.keys(envConf)) {
    let realName = envConf[envName].replace(/\\n/g, "\n");
    realName = realName === "true" ? true : realName === "false" ? false : realName;
    if (envName === "VITE_PORT") realName = Number(realName);
    if (envName === "VITE_PROXY") {
      try {
        realName = JSON.parse(realName);
      } catch (error) {
      }
    }
    ret[envName] = realName;
  }
  return ret;
};

// build/plugins.ts
import vue from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/@vitejs+plugin-vue@4.6.2_vite@5.4.9_@types+node@20.16.14_sass@1.80.3__vue@3.5.12_typescript@5.4.5_/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import vueSetupExtend from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/vite-plugin-vue-setup-extend@0.4.0_vite@5.4.9_@types+node@20.16.14_sass@1.80.3_/node_modules/vite-plugin-vue-setup-extend/dist/index.mjs";
import AutoImport from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/unplugin-auto-import@0.16.7_@vueuse+core@11.1.0_vue@3.5.12_typescript@5.4.5___rollup@4.24.0/node_modules/unplugin-auto-import/dist/vite.js";
import Components from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/unplugin-vue-components@0.25.2_@babel+parser@7.25.9_rollup@4.24.0_vue@3.5.12_typescript@5.4.5_/node_modules/unplugin-vue-components/dist/vite.mjs";
import { ElementPlusResolver } from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/unplugin-vue-components@0.25.2_@babel+parser@7.25.9_rollup@4.24.0_vue@3.5.12_typescript@5.4.5_/node_modules/unplugin-vue-components/dist/resolvers.mjs";
import viteCompression from "file:///Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main/node_modules/.pnpm/vite-plugin-compression@0.5.1_vite@5.4.9_@types+node@20.16.14_sass@1.80.3_/node_modules/vite-plugin-compression/dist/index.mjs";
var createVitePlugins = (ViteEnv) => {
  return [
    vue(),
    vueSetupExtend(),
    AutoImport({
      imports: ["vue", "vue-router"],
      dts: "src/auto-import.d.ts",
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      dts: "src/components.d.ts",
      resolvers: [ElementPlusResolver()]
    }),
    // 创建打包压缩配置
    createCompression(ViteEnv)
  ];
};
var createCompression = (viteEnv) => {
  const {
    VITE_BUILD_COMPRESS = "none",
    VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE
  } = viteEnv;
  const compressList = VITE_BUILD_COMPRESS.split(",");
  const plugins = [];
  if (compressList.includes("gzip")) {
    plugins.push(
      viteCompression({
        ext: ".gz",
        algorithm: "gzip",
        deleteOriginFile: VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE
      })
    );
  }
  if (compressList.includes("brotli")) {
    plugins.push(
      viteCompression({
        ext: ".br",
        algorithm: "brotliCompress",
        deleteOriginFile: VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE
      })
    );
  }
  return plugins;
};

// vite.config.ts
var __vite_injected_original_dirname = "/Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-admin-main";
var { dependencies, devDependencies, name, version } = package_default;
var __APP_INFO__ = {
  pkg: { dependencies, devDependencies, name, version },
  lastBuildTime: dayjs().format("YYYY-MM-DD HH:mm:ss")
};
var alias = { "@": resolve(__vite_injected_original_dirname, "./src") };
var vite_config_default = defineConfig(({ mode }) => {
  const root = process.cwd();
  const env = loadEnv(mode, root);
  const ViteEnv = wrapperEnv(env);
  return {
    base: ViteEnv.VITE_PUBLIC_PATH,
    root,
    define: {
      __APP_INFO__: JSON.stringify(__APP_INFO__)
    },
    plugins: createVitePlugins(ViteEnv),
    resolve: { alias },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/var.scss";`
        }
      }
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
          rewrite: (path) => path.replace(new RegExp(`^/api`), "")
        }
      }
    },
    esbuild: {
      pure: env.VITE_DROP_CONSOLE ? ["console.log", "debugger"] : []
    },
    build: {
      outDir: "dist",
      // 默认打包位置
      minify: "esbuild",
      reportCompressedSize: false,
      chunkSizeWarningLimit: 2e3,
      rollupOptions: {
        output: {
          // Static resource classification and packaging
          chunkFileNames: "assets/js/[name]-[hash].js",
          entryFileNames: "assets/js/[name]-[hash].js",
          assetFileNames: "assets/[ext]/[name]-[hash].[ext]"
        }
      }
    }
  };
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiLCAicGFja2FnZS5qc29uIiwgImJ1aWxkL2dldEVudi50cyIsICJidWlsZC9wbHVnaW5zLnRzIl0sCiAgInNvdXJjZXNDb250ZW50IjogWyJjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZGlybmFtZSA9IFwiL1VzZXJzL2xpdWppYXhpbmcvY29kaW5nL2NvbXB1dGluZy1wb3dlci1tYXJrZXQveWl5dW5zaHV6aS91eC1hZG1pbi1tYWluXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCIvVXNlcnMvbGl1amlheGluZy9jb2RpbmcvY29tcHV0aW5nLXBvd2VyLW1hcmtldC95aXl1bnNodXppL3V4LWFkbWluLW1haW4vdml0ZS5jb25maWcudHNcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfaW1wb3J0X21ldGFfdXJsID0gXCJmaWxlOi8vL1VzZXJzL2xpdWppYXhpbmcvY29kaW5nL2NvbXB1dGluZy1wb3dlci1tYXJrZXQveWl5dW5zaHV6aS91eC1hZG1pbi1tYWluL3ZpdGUuY29uZmlnLnRzXCI7aW1wb3J0IHsgQ29uZmlnRW52LCBVc2VyQ29uZmlnLCBkZWZpbmVDb25maWcsIGxvYWRFbnYgfSBmcm9tIFwidml0ZVwiO1xuaW1wb3J0IHsgcmVzb2x2ZSB9IGZyb20gXCJwYXRoXCI7XG5pbXBvcnQgcGtnIGZyb20gXCIuL3BhY2thZ2UuanNvblwiO1xuaW1wb3J0IGRheWpzIGZyb20gXCJkYXlqc1wiO1xuaW1wb3J0IHsgd3JhcHBlckVudiB9IGZyb20gXCIuL2J1aWxkL2dldEVudlwiO1xuaW1wb3J0IHsgY3JlYXRlVml0ZVBsdWdpbnMgfSBmcm9tIFwiLi9idWlsZC9wbHVnaW5zXCI7XG5cbmNvbnN0IHsgZGVwZW5kZW5jaWVzLCBkZXZEZXBlbmRlbmNpZXMsIG5hbWUsIHZlcnNpb24gfSA9IHBrZztcbmNvbnN0IF9fQVBQX0lORk9fXyA9IHtcbiAgcGtnOiB7IGRlcGVuZGVuY2llcywgZGV2RGVwZW5kZW5jaWVzLCBuYW1lLCB2ZXJzaW9uIH0sXG4gIGxhc3RCdWlsZFRpbWU6IGRheWpzKCkuZm9ybWF0KFwiWVlZWS1NTS1ERCBISDptbTpzc1wiKSxcbn07XG4vLyBcdThERUZcdTVGODRcbmNvbnN0IGFsaWFzID0geyBcIkBcIjogcmVzb2x2ZShfX2Rpcm5hbWUsIFwiLi9zcmNcIikgfTtcbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZygoeyBtb2RlIH06IENvbmZpZ0Vudik6IFVzZXJDb25maWcgPT4ge1xuICBjb25zdCByb290ID0gcHJvY2Vzcy5jd2QoKTtcbiAgY29uc3QgZW52ID0gbG9hZEVudihtb2RlLCByb290KTtcbiAgY29uc3QgVml0ZUVudiA9IHdyYXBwZXJFbnYoZW52KTtcblxuICByZXR1cm4ge1xuICAgIGJhc2U6IFZpdGVFbnYuVklURV9QVUJMSUNfUEFUSCxcbiAgICByb290LFxuICAgIGRlZmluZToge1xuICAgICAgX19BUFBfSU5GT19fOiBKU09OLnN0cmluZ2lmeShfX0FQUF9JTkZPX18pLFxuICAgIH0sXG4gICAgcGx1Z2luczogY3JlYXRlVml0ZVBsdWdpbnMoVml0ZUVudiksXG4gICAgcmVzb2x2ZTogeyBhbGlhcyB9LFxuICAgIGNzczoge1xuICAgICAgcHJlcHJvY2Vzc29yT3B0aW9uczoge1xuICAgICAgICBzY3NzOiB7XG4gICAgICAgICAgYWRkaXRpb25hbERhdGE6IGBAdXNlIFwiQC9zdHlsZXMvdmFyLnNjc3NcIjtgLFxuICAgICAgICB9LFxuICAgICAgfSxcbiAgICB9LFxuICAgIHNlcnZlcjoge1xuICAgICAgaG9zdDogXCIwLjAuMC4wXCIsXG4gICAgICBwb3J0OiBWaXRlRW52LlZJVEVfUE9SVCxcbiAgICAgIG9wZW46IFZpdGVFbnYuVklURV9PUEVOLFxuICAgICAgY29yczogdHJ1ZSxcbiAgICAgIHByb3h5OiB7XG4gICAgICAgIFtWaXRlRW52LlZJVEVfQVBQX0JBU0VfQVBJXToge1xuICAgICAgICAgIHRhcmdldDogVml0ZUVudi5WSVRFX1BST1hZX1VSTCxcbiAgICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsXG4gICAgICAgICAgcmV3cml0ZTogKHBhdGgpID0+IHBhdGgucmVwbGFjZShuZXcgUmVnRXhwKGBeL2FwaWApLCBcIlwiKSxcbiAgICAgICAgfSxcbiAgICAgIH0sXG4gICAgfSxcbiAgICBlc2J1aWxkOiB7XG4gICAgICBwdXJlOiBlbnYuVklURV9EUk9QX0NPTlNPTEUgPyBbXCJjb25zb2xlLmxvZ1wiLCBcImRlYnVnZ2VyXCJdIDogW10sXG4gICAgfSxcbiAgICBidWlsZDoge1xuICAgICAgb3V0RGlyOiBcImRpc3RcIiwgLy8gXHU5RUQ4XHU4QkE0XHU2MjUzXHU1MzA1XHU0RjREXHU3RjZFXG4gICAgICBtaW5pZnk6IFwiZXNidWlsZFwiLFxuICAgICAgcmVwb3J0Q29tcHJlc3NlZFNpemU6IGZhbHNlLFxuICAgICAgY2h1bmtTaXplV2FybmluZ0xpbWl0OiAyMDAwLFxuICAgICAgcm9sbHVwT3B0aW9uczoge1xuICAgICAgICBvdXRwdXQ6IHtcbiAgICAgICAgICAvLyBTdGF0aWMgcmVzb3VyY2UgY2xhc3NpZmljYXRpb24gYW5kIHBhY2thZ2luZ1xuICAgICAgICAgIGNodW5rRmlsZU5hbWVzOiBcImFzc2V0cy9qcy9bbmFtZV0tW2hhc2hdLmpzXCIsXG4gICAgICAgICAgZW50cnlGaWxlTmFtZXM6IFwiYXNzZXRzL2pzL1tuYW1lXS1baGFzaF0uanNcIixcbiAgICAgICAgICBhc3NldEZpbGVOYW1lczogXCJhc3NldHMvW2V4dF0vW25hbWVdLVtoYXNoXS5bZXh0XVwiLFxuICAgICAgICB9LFxuICAgICAgfSxcbiAgICB9LFxuICB9O1xufSk7XG4iLCAie1xuICBcIm5hbWVcIjogXCJwcm9qZWN0XCIsXG4gIFwicHJpdmF0ZVwiOiB0cnVlLFxuICBcInZlcnNpb25cIjogXCIwLjAuMFwiLFxuICBcInR5cGVcIjogXCJtb2R1bGVcIixcbiAgXCJzY3JpcHRzXCI6IHtcbiAgICBcImRldlwiOiBcInZpdGUgLS1vcGVuXCIsXG4gICAgXCJidWlsZFwiOiBcInZ1ZS10c2MgJiYgdml0ZSBidWlsZFwiLFxuICAgIFwicHJldmlld1wiOiBcInZpdGUgcHJldmlld1wiXG4gIH0sXG4gIFwiZGVwZW5kZW5jaWVzXCI6IHtcbiAgICBcIkBlbGVtZW50LXBsdXMvaWNvbnMtdnVlXCI6IFwiXjIuMS4wXCIsXG4gICAgXCJAZm9ybS1jcmVhdGUvZGVzaWduZXJcIjogXCJeM1wiLFxuICAgIFwiQGZvcm0tY3JlYXRlL2VsZW1lbnQtdWlcIjogXCJeMy4yLjE3XCIsXG4gICAgXCJAdHlwZXMvbm9kZVwiOiBcIl4yMC45LjNcIixcbiAgICBcIkB2dWV1c2UvY29yZVwiOiBcIl4xMS4xLjBcIixcbiAgICBcIkB3YW5nZWRpdG9yL2VkaXRvclwiOiBcIl41LjEuMjNcIixcbiAgICBcIkB3YW5nZWRpdG9yL2VkaXRvci1mb3ItdnVlXCI6IFwiXjUuMS4xMlwiLFxuICAgIFwiYXhpb3NcIjogXCJeMS42LjJcIixcbiAgICBcImRheWpzXCI6IFwiXjEuMTEuMTBcIixcbiAgICBcImVsZW1lbnQtcGx1c1wiOiBcIl4yLjguMFwiLFxuICAgIFwianMtY29va2llXCI6IFwiXjMuMC41XCIsXG4gICAgXCJtaXR0XCI6IFwiXjMuMC4xXCIsXG4gICAgXCJucHJvZ3Jlc3NcIjogXCJeMC4yLjBcIixcbiAgICBcInBpbmlhXCI6IFwiXjIuMS43XCIsXG4gICAgXCJwaW5pYS1wbHVnaW4tcGVyc2lzdGVkc3RhdGVcIjogXCJeMy4yLjBcIixcbiAgICBcInZkaXRvclwiOiBcIl4zLjEwLjdcIixcbiAgICBcInZpdGUtcGx1Z2luLWNvbXByZXNzaW9uXCI6IFwiXjAuNS4xXCIsXG4gICAgXCJ2aXRlLXBsdWdpbi12dWUtc2V0dXAtZXh0ZW5kXCI6IFwiXjAuNC4wXCIsXG4gICAgXCJ2dWVcIjogXCJeMy4zLjhcIixcbiAgICBcInZ1ZS1yb3V0ZXJcIjogXCJeNC4yLjVcIlxuICB9LFxuICBcImRldkRlcGVuZGVuY2llc1wiOiB7XG4gICAgXCJAdHlwZXMvanMtY29va2llXCI6IFwiXjMuMC42XCIsXG4gICAgXCJAdHlwZXMvbnByb2dyZXNzXCI6IFwiXjAuMi4zXCIsXG4gICAgXCJAdml0ZWpzL3BsdWdpbi12dWVcIjogXCJeNC41LjBcIixcbiAgICBcInNhc3NcIjogXCJeMS42OS41XCIsXG4gICAgXCJ0eXBlc2NyaXB0XCI6IFwiXjUuNC41XCIsXG4gICAgXCJ1bnBsdWdpbi1hdXRvLWltcG9ydFwiOiBcIl4wLjE2LjdcIixcbiAgICBcInVucGx1Z2luLWljb25zXCI6IFwiXjAuMTguMVwiLFxuICAgIFwidW5wbHVnaW4tdnVlLWNvbXBvbmVudHNcIjogXCJeMC4yNS4yXCIsXG4gICAgXCJ2aXRlXCI6IFwiXjUuMC4wXCIsXG4gICAgXCJ2dWUtdHNjXCI6IFwiXjEuOC4yMlwiXG4gIH0sXG4gIFwibWFpblwiOiBcImluZGV4LmpzXCIsXG4gIFwibGljZW5zZVwiOiBcIk1JVFwiXG59XG4iLCAiY29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2Rpcm5hbWUgPSBcIi9Vc2Vycy9saXVqaWF4aW5nL2NvZGluZy9jb21wdXRpbmctcG93ZXItbWFya2V0L3lpeXVuc2h1emkvdXgtYWRtaW4tbWFpbi9idWlsZFwiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9maWxlbmFtZSA9IFwiL1VzZXJzL2xpdWppYXhpbmcvY29kaW5nL2NvbXB1dGluZy1wb3dlci1tYXJrZXQveWl5dW5zaHV6aS91eC1hZG1pbi1tYWluL2J1aWxkL2dldEVudi50c1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vVXNlcnMvbGl1amlheGluZy9jb2RpbmcvY29tcHV0aW5nLXBvd2VyLW1hcmtldC95aXl1bnNodXppL3V4LWFkbWluLW1haW4vYnVpbGQvZ2V0RW52LnRzXCI7ZXhwb3J0IGNvbnN0IHdyYXBwZXJFbnYgPSAoZW52Q29uZjogUmVjb3JkYWJsZSk6IFZpdGVFbnYgPT4ge1xuICBjb25zdCByZXQ6IGFueSA9IHt9O1xuICBmb3IgKGNvbnN0IGVudk5hbWUgb2YgT2JqZWN0LmtleXMoZW52Q29uZikpIHtcbiAgICBsZXQgcmVhbE5hbWUgPSBlbnZDb25mW2Vudk5hbWVdLnJlcGxhY2UoL1xcXFxuL2csIFwiXFxuXCIpO1xuICAgIHJlYWxOYW1lID1cbiAgICAgIHJlYWxOYW1lID09PSBcInRydWVcIiA/IHRydWUgOiByZWFsTmFtZSA9PT0gXCJmYWxzZVwiID8gZmFsc2UgOiByZWFsTmFtZTtcbiAgICBpZiAoZW52TmFtZSA9PT0gXCJWSVRFX1BPUlRcIikgcmVhbE5hbWUgPSBOdW1iZXIocmVhbE5hbWUpO1xuICAgIGlmIChlbnZOYW1lID09PSBcIlZJVEVfUFJPWFlcIikge1xuICAgICAgdHJ5IHtcbiAgICAgICAgcmVhbE5hbWUgPSBKU09OLnBhcnNlKHJlYWxOYW1lKTtcbiAgICAgIH0gY2F0Y2ggKGVycm9yKSB7fVxuICAgIH1cbiAgICByZXRbZW52TmFtZV0gPSByZWFsTmFtZTtcbiAgfVxuICByZXR1cm4gcmV0O1xufTtcbiIsICJjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZGlybmFtZSA9IFwiL1VzZXJzL2xpdWppYXhpbmcvY29kaW5nL2NvbXB1dGluZy1wb3dlci1tYXJrZXQveWl5dW5zaHV6aS91eC1hZG1pbi1tYWluL2J1aWxkXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCIvVXNlcnMvbGl1amlheGluZy9jb2RpbmcvY29tcHV0aW5nLXBvd2VyLW1hcmtldC95aXl1bnNodXppL3V4LWFkbWluLW1haW4vYnVpbGQvcGx1Z2lucy50c1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vVXNlcnMvbGl1amlheGluZy9jb2RpbmcvY29tcHV0aW5nLXBvd2VyLW1hcmtldC95aXl1bnNodXppL3V4LWFkbWluLW1haW4vYnVpbGQvcGx1Z2lucy50c1wiO2ltcG9ydCB7IFBsdWdpbk9wdGlvbiB9IGZyb20gXCJ2aXRlXCI7XG5pbXBvcnQgdnVlIGZyb20gXCJAdml0ZWpzL3BsdWdpbi12dWVcIjtcbi8vIHwgdml0ZVx1NjNEMlx1NEVGNnNldHVwIG5hbWVcdTVDNUVcdTYwMjdcdTc1MUZcdTY1NDhcbmltcG9ydCB2dWVTZXR1cEV4dGVuZCBmcm9tIFwidml0ZS1wbHVnaW4tdnVlLXNldHVwLWV4dGVuZFwiO1xuLy8gfCBcdTgxRUFcdTUyQThcdTVCRkNcdTUxNjV2dWVcdTRFMkRob29rIHJlYWN0aXZlIHJlZlx1N0I0OVxuaW1wb3J0IEF1dG9JbXBvcnQgZnJvbSBcInVucGx1Z2luLWF1dG8taW1wb3J0L3ZpdGVcIjtcbi8vIHwgXHU4MUVBXHU1MkE4XHU1QkZDXHU1MTY1dWktXHU3RUM0XHU0RUY2IFx1NkJENFx1NTk4Mlx1OEJGNGFudC1kZXNpZ24tdnVlICBlbGVtZW50LXBsdXNcdTdCNDlcbmltcG9ydCBDb21wb25lbnRzIGZyb20gXCJ1bnBsdWdpbi12dWUtY29tcG9uZW50cy92aXRlXCI7XG5pbXBvcnQgeyBFbGVtZW50UGx1c1Jlc29sdmVyIH0gZnJvbSBcInVucGx1Z2luLXZ1ZS1jb21wb25lbnRzL3Jlc29sdmVyc1wiO1xuaW1wb3J0IHZpdGVDb21wcmVzc2lvbiBmcm9tIFwidml0ZS1wbHVnaW4tY29tcHJlc3Npb25cIjtcblxuLyoqXG4gKiBAZGVzY3JpcHRpb246IFx1NTIxQlx1NUVGQSB2aXRlIFx1NjNEMlx1NEVGNlxuICogQHBhcmFtIFZpdGVFbnZcbiAqL1xuZXhwb3J0IGNvbnN0IGNyZWF0ZVZpdGVQbHVnaW5zID0gKFxuICBWaXRlRW52OiBWaXRlRW52XG4pOiAoUGx1Z2luT3B0aW9uIHwgUGx1Z2luT3B0aW9uW10pW10gPT4ge1xuICByZXR1cm4gW1xuICAgIHZ1ZSgpLFxuICAgIHZ1ZVNldHVwRXh0ZW5kKCksXG4gICAgQXV0b0ltcG9ydCh7XG4gICAgICBpbXBvcnRzOiBbXCJ2dWVcIiwgXCJ2dWUtcm91dGVyXCJdLFxuICAgICAgZHRzOiBcInNyYy9hdXRvLWltcG9ydC5kLnRzXCIsXG4gICAgICByZXNvbHZlcnM6IFtFbGVtZW50UGx1c1Jlc29sdmVyKCldLFxuICAgIH0pLFxuICAgIENvbXBvbmVudHMoe1xuICAgICAgZHRzOiBcInNyYy9jb21wb25lbnRzLmQudHNcIixcbiAgICAgIHJlc29sdmVyczogW0VsZW1lbnRQbHVzUmVzb2x2ZXIoKV0sXG4gICAgfSksXG4gICAgLy8gXHU1MjFCXHU1RUZBXHU2MjUzXHU1MzA1XHU1MzhCXHU3RjI5XHU5MTREXHU3RjZFXG4gICAgY3JlYXRlQ29tcHJlc3Npb24oVml0ZUVudiksXG4gIF07XG59O1xuXG4vKipcbiAqIEBkZXNjcmlwdGlvbjogXHU2ODM5XHU2MzZFIGNvbXByZXNzIFx1OTE0RFx1N0Y2RVx1RkYwQ1x1NzUxRlx1NjIxMFx1NEUwRFx1NTQwQ1x1NzY4NFx1NTM4Qlx1N0YyOVx1ODlDNFx1NTIxOVxuICogQHBhcmFtIFZpdGVFbnZcbiAqL1xuY29uc3QgY3JlYXRlQ29tcHJlc3Npb24gPSAodml0ZUVudjogVml0ZUVudik6IFBsdWdpbk9wdGlvbiB8IFBsdWdpbk9wdGlvbltdID0+IHtcbiAgY29uc3Qge1xuICAgIFZJVEVfQlVJTERfQ09NUFJFU1MgPSBcIm5vbmVcIixcbiAgICBWSVRFX0JVSUxEX0NPTVBSRVNTX0RFTEVURV9PUklHSU5fRklMRSxcbiAgfSA9IHZpdGVFbnY7XG4gIGNvbnN0IGNvbXByZXNzTGlzdCA9IFZJVEVfQlVJTERfQ09NUFJFU1Muc3BsaXQoXCIsXCIpO1xuICBjb25zdCBwbHVnaW5zOiBQbHVnaW5PcHRpb25bXSA9IFtdO1xuICBpZiAoY29tcHJlc3NMaXN0LmluY2x1ZGVzKFwiZ3ppcFwiKSkge1xuICAgIHBsdWdpbnMucHVzaChcbiAgICAgIHZpdGVDb21wcmVzc2lvbih7XG4gICAgICAgIGV4dDogXCIuZ3pcIixcbiAgICAgICAgYWxnb3JpdGhtOiBcImd6aXBcIixcbiAgICAgICAgZGVsZXRlT3JpZ2luRmlsZTogVklURV9CVUlMRF9DT01QUkVTU19ERUxFVEVfT1JJR0lOX0ZJTEUsXG4gICAgICB9KVxuICAgICk7XG4gIH1cbiAgaWYgKGNvbXByZXNzTGlzdC5pbmNsdWRlcyhcImJyb3RsaVwiKSkge1xuICAgIHBsdWdpbnMucHVzaChcbiAgICAgIHZpdGVDb21wcmVzc2lvbih7XG4gICAgICAgIGV4dDogXCIuYnJcIixcbiAgICAgICAgYWxnb3JpdGhtOiBcImJyb3RsaUNvbXByZXNzXCIsXG4gICAgICAgIGRlbGV0ZU9yaWdpbkZpbGU6IFZJVEVfQlVJTERfQ09NUFJFU1NfREVMRVRFX09SSUdJTl9GSUxFLFxuICAgICAgfSlcbiAgICApO1xuICB9XG4gIHJldHVybiBwbHVnaW5zO1xufTtcbiJdLAogICJtYXBwaW5ncyI6ICI7QUFBMFksU0FBZ0MsY0FBYyxlQUFlO0FBQ3ZjLFNBQVMsZUFBZTs7O0FDRHhCO0FBQUEsRUFDRSxNQUFRO0FBQUEsRUFDUixTQUFXO0FBQUEsRUFDWCxTQUFXO0FBQUEsRUFDWCxNQUFRO0FBQUEsRUFDUixTQUFXO0FBQUEsSUFDVCxLQUFPO0FBQUEsSUFDUCxPQUFTO0FBQUEsSUFDVCxTQUFXO0FBQUEsRUFDYjtBQUFBLEVBQ0EsY0FBZ0I7QUFBQSxJQUNkLDJCQUEyQjtBQUFBLElBQzNCLHlCQUF5QjtBQUFBLElBQ3pCLDJCQUEyQjtBQUFBLElBQzNCLGVBQWU7QUFBQSxJQUNmLGdCQUFnQjtBQUFBLElBQ2hCLHNCQUFzQjtBQUFBLElBQ3RCLDhCQUE4QjtBQUFBLElBQzlCLE9BQVM7QUFBQSxJQUNULE9BQVM7QUFBQSxJQUNULGdCQUFnQjtBQUFBLElBQ2hCLGFBQWE7QUFBQSxJQUNiLE1BQVE7QUFBQSxJQUNSLFdBQWE7QUFBQSxJQUNiLE9BQVM7QUFBQSxJQUNULCtCQUErQjtBQUFBLElBQy9CLFFBQVU7QUFBQSxJQUNWLDJCQUEyQjtBQUFBLElBQzNCLGdDQUFnQztBQUFBLElBQ2hDLEtBQU87QUFBQSxJQUNQLGNBQWM7QUFBQSxFQUNoQjtBQUFBLEVBQ0EsaUJBQW1CO0FBQUEsSUFD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
