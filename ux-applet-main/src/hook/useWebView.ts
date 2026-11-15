import jWeixin from "weixin-js-sdk";
const useWebView = () => {
	let _webEnv = false;
	const navigateTo = (path: string, query = {}) => {
		if (_webEnv) {
			jWeixin.miniProgram.navigateTo({
				url: "/" + path + uni.$u.queryParams(query),
			});
		} else {
			window.location.href = `weixin://dl/business/?appid=${
				import.meta.env.VITE_APP_APPID
			}&path=${path}&query=${encodeURIComponent(
				uni.$u.queryParams(query, false)
			)}&env_version=release`;
		}
	};
	const postMessage = (type: string, message = {}) => {
		if (_webEnv) {
			jWeixin.miniProgram.postMessage({
				data: {
					type,
					message,
				},
			});
		}
	};
	const getEnv = () => _webEnv;
	const initWebView = () => {
		console.group(`${new Date()} jWeixin.miniProgram.getEnv begin`);
		jWeixin.miniProgram.getEnv((e) => {
			if (e.miniprogram) {
				_webEnv = true;
			} else {
				_webEnv = false;
			}
		});
		console.log(
			"初始化完毕,当前环境为：" + (_webEnv ? "微信小程序" : "H5")
		);
		console.groupEnd();
	};
	initWebView();
	return {
		navigateTo,
		getEnv,
		postMessage,
	};
};

export { useWebView };
