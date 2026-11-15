import wx from "weixin-js-sdk";
// 获取微信配置信息
const getWxConfig = (result: TKeyValue) => {
	return new Promise((resolve, reject) => {
		const { appId, nonceStr, paySign, timeStamp } = result;
		wx.config({
			debug: false, // 开启调试模式
			appId: appId, // appId 必填，公众号的唯一标识
			timestamp: timeStamp, // 必填，生成签名的时间戳
			nonceStr: nonceStr, // 必填，生成签名的随机串
			signature: paySign, // 必填，签名
			jsApiList: ["chooseWXPay"], // 必填，需要使用的JS接口列表
		});
		resolve(result);
	});
};

const wxPay = (payData: TKeyValue) => {
	console.log("🚀 ~ wxPay ~ payData:", payData);
	return new Promise((resolve) => {
		getWxConfig(payData).then((result: any) => {
			const { appId, nonceStr, paySign, timeStamp, signType } = result;
			wx.ready(() => {
				wx.chooseWXPay({
					timestamp: timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小，部分系统取到的值为毫秒级，需要转换成秒(10位数字)
					nonceStr: nonceStr, // 支付签名随机串，不长于 32 位
					package: result.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=\*\*\*）
					signType: signType, // 签名方式：MD5,提醒后端用到加密的地方都用MD5
					paySign: paySign, // 支付签名：后端注意生成规则 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
					success: (res) => {
						// 支付成功回调:微信团队郑重提示：不保证绝对可靠，切记！
						// 据说部分ios手机，成功的回调不执行，所以为了保证回调执行，在complete里也做了回调执行
						if (res.errMsg === "chooseWXPay:ok") {
							resolve({ code: 1, message: "支付成功" });
						}
					},
					fail: (error) => {
						console.log(error);
						resolve({ code: 0, message: error });
					},
					cancel: (res) => {
						console.log(res);
						resolve({ code: 0, message: "用户取消支付" });
					},
				});
			});
		});
	});
};
export { wxPay };
