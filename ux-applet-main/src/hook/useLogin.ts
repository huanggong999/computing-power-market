const useLogin = () => {
	const Authorize = () => {
		let local = encodeURIComponent(window.location.href); //获取当前页面地址作为回调地址
		let appid = "wx4a9bd7a194d518e5";
		window.location.href = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appid}&redirect_uri=${local}&response_type=code&scope=snsapi_userinfo#wechat_redirect`;
	};
	return {
		Authorize,
	};
};
export default useLogin;
