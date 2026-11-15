/*
 * @Descripttion: 配置文件
 * @version: 1.0
 */
const DEFAULT_CONFIG: TSysConfig = {
	// 标题
	APP_NAME: import.meta.env.VITE_APP_TITLE,

	// 接口地址
	API_URL:
		import.meta.env.MODE === "development" &&
		import.meta.env.VITE_APP_PROXY === "true"
			? "/api"
			: import.meta.env.VITE_APP_API_BASEURL,

	APP_SOCKET_URL: import.meta.env.VITE_APP_SOCKET_URL,

	// 请求APPID
	APP_ID: import.meta.env.VITE_APP_APPID,

	// 资源地址
	ASSETS_URL: import.meta.env.VITE_APP_ASSETS_URL,

	// 是否本地资源
	ASSETS_LOCAL: import.meta.env.VITE_APP_ASSETS_LOCAL,
	// 请求超时
	TIMEOUT: 30000,

	// TokenName
	TOKEN_NAME: "Authorization",

	// Token前缀，注意最后有个空格，如不需要需设置空字符串
	TOKEN_PREFIX: "Bearer ",

	// 追加其他头
	HEADERS: {},

	//是否加密localStorage, 为空不加密，可填写AES(模式ECB,移位Pkcs7)加密
	LS_ENCRYPTION: "",

	//localStorageAES加密秘钥，位数建议填写8的倍数
	LS_ENCRYPTION_key: "2XNN4K8LC0ELVWN4",
};

export default DEFAULT_CONFIG;
