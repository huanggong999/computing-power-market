import { PluginOption } from "vite";
import vue from "@vitejs/plugin-vue";
// | vite插件setup name属性生效
import vueSetupExtend from "vite-plugin-vue-setup-extend";
// | 自动导入vue中hook reactive ref等
import AutoImport from "unplugin-auto-import/vite";
// | 自动导入ui-组件 比如说ant-design-vue  element-plus等
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import viteCompression from "vite-plugin-compression";

/**
 * @description: 创建 vite 插件
 * @param ViteEnv
 */
export const createVitePlugins = (
  ViteEnv: ViteEnv
): (PluginOption | PluginOption[])[] => {
  return [
    vue(),
    vueSetupExtend(),
    AutoImport({
      imports: ["vue", "vue-router"],
      dts: "src/auto-import.d.ts",
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      dts: "src/components.d.ts",
      resolvers: [ElementPlusResolver()],
    }),
    // 创建打包压缩配置
    createCompression(ViteEnv),
  ];
};

declare interface ViteEnv {
  readonly VITE_GLOB_APP_TITLE: string; // 标题
  readonly VITE_OPEN: boolean; // 是否打开浏览器
  readonly VITE_PORT: number; // 端口
  readonly VITE_REPORT: boolean; // 打包后是否生成包分析文件
  readonly VITE_APP_ENV: "development" | "production"; // 环境
  readonly VITE_APP_BASE_API: string; // 请求API
  readonly VITE_DROP_CONSOLE: boolean; // 打包时是否删除 console
  readonly VITE_PUBLIC_PATH: string; // 公共基础路径
  readonly VITE_BUILD_COMPRESS: "gzip" | "brotli" | "gzip,brotli" | "none"; // 打包压缩方式
  readonly VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE: boolean; // 是否删除原文件
  // 更多环境变量...
}
/**
 * @description: 根据 compress 配置，生成不同的压缩规则
 * @param ViteEnv
 */
const createCompression = (viteEnv: ViteEnv): PluginOption | PluginOption[] => {
  const {
    VITE_BUILD_COMPRESS = "none",
    VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE,
  } = viteEnv;
  const compressList = VITE_BUILD_COMPRESS.split(",");
  const plugins: PluginOption[] = [];
  if (compressList.includes("gzip")) {
    plugins.push(
      viteCompression({
        ext: ".gz",
        algorithm: "gzip",
        deleteOriginFile: VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE,
      })
    );
  }
  if (compressList.includes("brotli")) {
    plugins.push(
      viteCompression({
        ext: ".br",
        algorithm: "brotliCompress",
        deleteOriginFile: VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE,
      })
    );
  }
  return plugins;
};
