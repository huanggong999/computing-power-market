interface PayData {
	nonceStr: string;
	paySign: string;
	timeStamp: string;
	signType: string;
	packageValue: string;
}

interface PaymentResult {
	code: number;
	msg: string;
}

export const weChatPay = (payData: PayData): Promise<PaymentResult> => {
	return new Promise((resolve) => {
		const { nonceStr, paySign, timeStamp, signType, packageValue } =
			payData;
		uni.requestPayment({
			provider: "wxpay",
			timeStamp,
			nonceStr, // 随机字符串
			paySign,
			signType, // 签名，这里用的 MD5/RSA 签名
			package: packageValue, // 统一下单接口返回的 prepay_id 参数值
			orderInfo: {}, // 添加 orderInfo 属性
			success: (res) => {
				if (res.errMsg === "requestPayment:ok") {
					resolve({ code: 1, msg: "支付成功" });
				}
			},
			fail: (err) => {
				if (err.errMsg === "requestPayment:fail cancel") {
					resolve({ code: 0, msg: "支付已取消" });
				}
			},
		});
	});
};
