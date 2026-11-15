import CryptoJS from "crypto-js";
import sysConfig from "@/config";

const crypto: ICrypto = {
	MD5(data: string) {
		return CryptoJS.MD5(data).toString();
	},
	//BASE64加解密
	BASE64: {
		encrypt(data: string) {
			return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(data));
		},
		decrypt(cipher: string) {
			return CryptoJS.enc.Base64.parse(cipher).toString(
				CryptoJS.enc.Utf8
			);
		},
	},
	//AES加解密
	AES: {
		encrypt(data: string, secretKey: string) {
			if (secretKey.length % 8 != 0) {
				console.warn(
					"[Crypto error]: 秘钥长度需为8的倍数，否则解密将会失败。"
				);
			}
			const result = CryptoJS.AES.encrypt(
				data,
				CryptoJS.enc.Utf8.parse(secretKey),
				{
					iv: CryptoJS.enc.Utf8.parse(""),
					mode: CryptoJS.mode.ECB,
					padding: CryptoJS.pad.Pkcs7,
				}
			);
			return result.toString();
		},
		decrypt(cipher: string, secretKey: string) {
			const result = CryptoJS.AES.decrypt(
				cipher,
				CryptoJS.enc.Utf8.parse(secretKey),
				{
					iv: CryptoJS.enc.Utf8.parse(""),
					mode: CryptoJS.mode.ECB,
					padding: CryptoJS.pad.Pkcs7,
				}
			);
			return CryptoJS.enc.Utf8.stringify(result);
		},
	},
};

const tool: ITool = {
	set: function (key: string, data: any, dateTime: number = 0) {
		// 加密
		if (sysConfig.LS_ENCRYPTION == "AES") {
			data = crypto.AES.encrypt(
				JSON.stringify(data),
				sysConfig.LS_ENCRYPTION_key
			);
		}
		const cacheValue = {
			content: data,
			dateTime:
				Number(dateTime) === 0
					? 0
					: new Date().getTime() + Number(dateTime) * 1000,
		};
		return uni.setStorageSync(key, JSON.stringify(cacheValue));
	},
	get: function (key: string) {
		try {
			const value = JSON.parse(uni.getStorageSync(key));
			if (value) {
				let nowTime = new Date().getTime();
				if (nowTime > value.dateTime && value.dateTime != 0) {
					this.remove(key);
					return null;
				}
				// 解密
				if (sysConfig.LS_ENCRYPTION == "AES") {
					value.content = JSON.parse(
						crypto.AES.decrypt(
							value.content,
							sysConfig.LS_ENCRYPTION_key
						)
					);
				}
				return value.content;
			}
			return null;
		} catch (err) {
			return null;
		}
	},
	remove: function (key: string) {
		return uni.removeStorageSync(key);
	},
	clear: function () {
		return uni.clearStorageSync();
	},
};

export default tool;
export { tool, crypto };
