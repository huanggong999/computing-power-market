export const wrapperEnv = (envConf: Recordable): ViteEnv => {
  const ret: any = {};
  for (const envName of Object.keys(envConf)) {
    let realName = envConf[envName].replace(/\\n/g, "\n");
    realName =
      realName === "true" ? true : realName === "false" ? false : realName;
    if (envName === "VITE_PORT") realName = Number(realName);
    if (envName === "VITE_PROXY") {
      try {
        realName = JSON.parse(realName);
      } catch (error) {}
    }
    ret[envName] = realName;
  }
  return ret;
};

declare type Recordable<T = any> = Record<string, T>;

declare interface ViteEnv {
  readonly VITE_GLOB_APP_TITLE: string; // 标题
  readonly VITE_OPEN: boolean; // 是否打开浏览器
  readonly VITE_PORT: number; // 端口
  readonly VITE_REPORT: boolean; // 打包后是否生成包分析文件
  readonly VITE_APP_ENV: "development" | "production"; // 环境
  readonly VITE_APP_BASE_API: string; // 请求API
  readonly VITE_PROXY_URL: string; // 代理地址 /api
  readonly VITE_DROP_CONSOLE: boolean; // 打包时是否删除 console
  readonly VITE_PUBLIC_PATH: string; // 公共基础路径
  readonly VITE_BUILD_COMPRESS: "gzip" | "brotli" | "gzip,brotli" | "none"; // 打包压缩方式
  readonly VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE: boolean; // 是否删除原文件
  // 更多环境变量...
}
