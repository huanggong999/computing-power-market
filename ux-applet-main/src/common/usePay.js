const usePay = (payData) => {
	return new Promise((resolve) => {
		const { nonce_str, paySign, timeStamp, signType } = payData;
		const payment = {
			provider: "wxpay",
			nonceStr: nonce_str, // 随机字符串
			paySign,
			signType, // 签名，这里用的 MD5/RSA 签名
			timeStamp: String(timeStamp),
			package: payData.package, // 统一下单接口返回的 prepay_id 参数值
		};
		uni.requestPayment({
			...payment,
			success: (res) => {
				if (res.errMsg === 'requestPayment:ok') {
					resolve({ code: 1, msg: '支付成功' })
				}
			},
			fail: (err) => {
				if (err.errMsg === 'requestPayment:fail cancel') {
					resolve({ code: 0, msg: '支付已取消' })
				}
			}
		});
	});
};
export { usePay };
