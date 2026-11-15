/*
 * @Descripttion: 请求封装
 * @version: 1.0
 */
import sysConfig from "@/config";
import tool from "@/utils/tool";
// 拦截器配置
const httpInterceptor = {
	// 拦截前触发
	invoke(options: any) {
		if (!options.header) {
			options.header = {};
		}
		// 1. 非 http 开头需拼接地址
		if (!options.url.startsWith("http")) {
			options.url = sysConfig.API_URL + options.url;
		}
		// 2. 请求超时
		options.timeout = sysConfig.TIMEOUT;
		// 3. 添加 token 请求头标识
		// TODO 暂时
		const token = tool.get("TOKEN");
		if (token) {
			options.header[sysConfig.TOKEN_NAME] =
				sysConfig.TOKEN_PREFIX + token;
		}
		/* 合并配置项 */
		Object.assign(options.header, sysConfig.HEADERS);
	},
};
// 拦截 request 请求
uni.addInterceptor("request", httpInterceptor);
// 拦截 uploadFile 文件上传
uni.addInterceptor("uploadFile", httpInterceptor);

interface RequestOptions extends UniApp.RequestOptions {
	returnAll?: boolean;
}
interface UploadOptions extends UniApp.UploadFileOption {
	returnAll?: boolean;
}

const request = (config: RequestOptions) => {
	return new Promise((resolve, reject) => {
		uni.request({
			...config,
			// eslint-disable-next-line consistent-return
			success: ({ data }: any) => {
				if (config.returnAll) {
					return resolve(data);
				}
				if (data.code === 401) {
					uni.showToast({
						title: "登录已过期",
						icon: "none",
						mask: true,
					});
					uni.$u.sleep(2000).then(() => {
						// ! 跳转登录页
						uni.navigateTo({
							url: "/pages/login/index?isClear=true",
						});
					});
					resolve(data);
				} else if (data?.code === 200) {
					resolve(data);
				} else {
					uni.showToast({
						title: data?.msg || "服务器错误",
						icon: "none",
					});
					reject({});
				}
			},
			fail: (err: any) => {
				reject(err);
			},
		});
	});
};
const uploadFile = (config: UploadOptions) => {
	/* 发送请求 */
	return new Promise((resolve, reject) => {
		uni.uploadFile({
			...config,
			success: (res: any) => {
				const data = JSON.parse(res.data);
				if (data?.code === 401) {
					uni.showToast({
						title: "登录信息已过期",
						icon: "none",
					});
					uni.reLaunch({
						url: "/pages/index/index",
					});
					resolve(data);
				} else if (data?.code === 200) {
					resolve(data);
				} else {
					uni.showToast({
						title: data?.msg || "服务器错误",
						icon: "none",
					});
					resolve(data);
				}
			},
			fail: (err: any) => {
				reject(err);
			},
		});
	});
};
const http = {
	/** get 请求
	 * @param  {string} url 接口地址
	 * @param  {object} data 请求参数
	 * @param  {object} config 参数
	 */
	get(url: string, data = {}, config = {}) {
		return new Promise((resolve, reject) => {
			request({
				method: "GET",
				url,
				data,
				...config,
			})
				.then((response) => {
					resolve(response);
				})
				.catch((error) => {
					reject(error);
				});
		});
	},
	/** post 请求
	 * @param  {string} url 接口地址
	 * @param  {object} data 请求参数
	 * @param  {object} config 参数
	 */
	post(url: string, data = {}, config = {}) {
		return new Promise((resolve, reject) => {
			request({
				method: "POST",
				url,
				data,
				...config,
			})
				.then((response) => {
					resolve(response);
				})
				.catch((error) => {
					reject(error);
				});
		});
	},
	/** delete 请求
	 * @param  {string} url 接口地址
	 * @param  {object} data 请求参数
	 * @param  {object} config 参数
	 */
	delete(url: string, data = {}, config = {}) {
		return new Promise((resolve, reject) => {
			request({
				method: "DELETE",
				url,
				data,
				...config,
			})
				.then((response) => {
					resolve(response);
				})
				.catch((error) => {
					reject(error);
				});
		});
	},
	/** put 请求
	 * @param  {string} url 接口地址
	 * @param  {object} data 请求参数
	 * @param  {object} config 参数
	 */
	put(url: string, data = {}, config = {}) {
		return new Promise((resolve, reject) => {
			request({
				method: "PUT",
				url,
				data,
				...config,
			})
				.then((response) => {
					resolve(response);
				})
				.catch((error) => {
					reject(error);
				});
		});
	},
	/** 上传请求
	 * @param  {string} url 接口地址
	 * @param  {object} data 请求参数
	 * @param  {object} config 参数
	 */
	upload(url: string, data: any, config = {}) {
		return new Promise((resolve, reject) => {
			uploadFile({
				url,
				filePath: data,
				...config,
			})
				.then((response) => {
					resolve(response);
				})
				.catch((error) => {
					reject(error);
				});
		});
	},
};

export default http;
