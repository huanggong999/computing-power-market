/// <reference types="vite/client" />

declare type TKeyValue<T = any> = Record<string, T>;
declare interface ViteEnv {
	readonly VITE_APP_TITLE: string; // 标题
	readonly VITE_APP_API_BASEURL: string; // 请求API
	readonly VITE_APP_ASSETS_URL: string; // 前端存储桶
	readonly VITE_APP_ASSETS_LOCAL: string; // 是否本地资源
	readonly VITE_APP_PROXY: string; // 是否启用代理
	readonly VITE_APP_SOCKET_URL: string; // Socket地址
	readonly VITE_APP_APPID: string; // 应用APPID

	// 更多环境变量...
}
interface ImportMeta {
	readonly env: ViteEnv;
}

declare interface TSysConfig {
	/**
	 * 应用名称
	 * */
	readonly APP_NAME: string;
	/**
	 * 接口地址
	 * */
	readonly API_URL: string;
	/**
	 * Socket地址
	 * */
	readonly APP_SOCKET_URL: string;
	/**
	 * 请求APPID
	 * */
	readonly APP_ID: string;
	/**
	 * 资源地址
	 * */
	readonly ASSETS_URL: string;
	/**
	 * 是否本地资源
	 */
	readonly ASSETS_LOCAL: string;
	/**
	 * 请求超时时间
	 * */
	readonly TIMEOUT: number;
	/**
	 * Token名称
	 * */
	readonly TOKEN_NAME: string;
	/**
	 * Token前缀，注意最后有个空格，如不需要需设置空字符串
	 * */
	readonly TOKEN_PREFIX: string;
	/**
	 * 追加其他头
	 * */
	readonly HEADERS: TKeyValue;
	/**
	 * 是否加密localStorage, 为空不加密，可填写AES(模式ECB,移位Pkcs7)加密
	 * */
	readonly LS_ENCRYPTION: "AES" | "ECB" | "PKcs7" | "";
	/**
	 * localStorageAES加密秘钥，位数建议填写8的倍数
	 * */
	readonly LS_ENCRYPTION_key: string;
	// 更多全局变量...
}
