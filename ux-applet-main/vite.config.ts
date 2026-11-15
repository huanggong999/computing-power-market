import { resolve } from "path";
import { defineConfig, loadEnv } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import ViteRestart from "vite-plugin-restart";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";

const envBase = resolve(process.cwd(), "env");
// https://vitejs.dev/config/
export default ({ mode }) =>
	defineConfig({
		base: "./",
		plugins: [
			Components({
				// 引入组件的,包括自定义组件
				// 存放的位置
				dts: "src/components.d.ts",
				dirs: ["src/components"],
			}),
			uni(),
			AutoImport({
				imports: ["vue", "uni-app"],
				dts: "src/auto-imports.d.ts", // 安装好依赖后，重新运行编译即可自动在根目录下生成此声明文件
			}),
			ViteRestart({
				// 通过这个插件，在修改vite.config.js文件则不需要重新运行也生效配置
				restart: ["vite.config.ts"],
			}),
		],
		envDir: resolve(__dirname, "env"),
		resolve: {
			// 配置别名
			alias: {
				"@": resolve(__dirname, "src"),
			},
		},
		define: {
			__VUE_I18N_FULL_INSTALL__: true,
			__VUE_I18N_LEGACY_API__: true,
			__INTLIFY_PROD_DEVTOOLS__: false,
		},
		css: {
			// css预处理器
			preprocessorOptions: {
				scss: {
					// 因为uni.scss可以全局使用，这里根据自己的需求调整
					// additionalData: '@import "./src/styles/global.scss";',
				},
			},
		},
		// 开发服务器配置
		server: {
			host: "0.0.0.0",
			port: Number(loadEnv(mode, envBase).VITE_APP_PORT),
			// 请求代理
			proxy: {
				// 个人习惯，这里就用/dev作为前缀了
				"/api": {
					target: loadEnv(mode, envBase).VITE_APP_API_BASEURL,
					changeOrigin: true,
					// 路径重写，去掉/dev
					rewrite: (path) => path.replace(/^\/api/, ""),
				},
			},
		},
		build: {
			/** 配置h5打包js,css,img分别在不同文件夹start */
			assetsDir: "static/assets",
			rollupOptions: {
				output: {
					chunkFileNames: "static/js/[name]-[hash].js",
					entryFileNames: "static/js/[name]-[hash].js",
					assetFileNames: "static/[ext]/[name]-[hash].[ext]",
				},
			},
			/** 配置h5打包js,css,img分别在不同文件夹end */
		},
		esbuild: {
			drop: ["debugger"],
		},
	});
