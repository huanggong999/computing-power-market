<template>
	<view class="content">
		<image class="bg" :src="$ICON('/login/bg.png')" />
		<MyNavTitle title="登录" color="#000" />
		<view class="content-head">欢迎使用逸云数智</view>
		<view class="login-way">
			<view :class="loginWay === 'password'
				? 'login-way-item active'
				: 'login-way-item'
				" @click="loginWay = 'password'">
				密码登录
			</view>
			<view class="split"></view>
			<view :class="loginWay === 'code'
				? 'login-way-item active'
				: 'login-way-item'
				" style="padding-left: 48rpx" @click="loginWay = 'code'">
				验证码登录
			</view>
		</view>
		<view class="login-form">
			<up-input shape="circle" prefixIcon="account" v-model="form.username" placeholder="请输入手机号"
				class="log-input" />
			<up-input shape="circle" type="password" v-if="loginWay === 'password'" prefixIcon="lock"
				v-model="form.password" placeholder="请输入密码" />
			<view class="authCode" v-if="loginWay === 'password'">
				<up-input shape="circle" prefixIcon="lock" v-model="form.code" placeholder="请输入验证码" />
				<image :src="codeUrl" @click="getAuthCode" class="login-code-img" />
			</view>
			<up-input shape="circle" v-if="loginWay === 'code'" placeholder="请输入验证码" prefixIcon="lock"
				v-model="form.code">
				<template #suffix>
					<view class="split"></view>
					<view @click="getPhoneCode" v-if="codeTime === 0" class="blue-btn">获取验证码</view>
					<view v-show="codeTime > 0" class="blue-btn">{{ codeTime }}s</view>
				</template>
			</up-input>
			<view class="tip" @click="IsChecked = !IsChecked">

				<image style="width:28rpx;height:28rpx;margin-right:10rpx;" :src="$ICON(`/image/${IsChecked ? 'IsCheck' : 'check'}.png`)
					" />
				<view class="tip-text">我已阅读并同意 <view class="blue-btn"
						@click.stop="toUrl('https://eyunai.net/#/docsView/TermsOfService')">服务条款 </view>
					和<view class="blue-btn" @click.stop="toUrl('https://eyunai.net/#/docsView/TermsOfService')">隐私政策
					</view>
				</view>
			</view>
		</view>
		<view class="btn">
			<button class="login-btn" @click="loginHandle">立即登录</button>
			<view class="other-btns">
				<view class="other-btn mr24" @click="toPage('forget')">忘记密码</view>
				<view class="split"></view>
				<view class="other-btn ml24" @click="toPage('register')">账号注册</view>
			</view>
		</view>
		<view class="wechat-login" @click="wechatLogin">
			<image class="icon" :src="$ICON('/login/wechat-btn.png')" />
			<text class="text">微信登录</text>
		</view>
		<!-- <button open-type="getPhoneNumber" @getphonenumber="onGetPhoneNumber">123213</button> -->

	</view>
</template>

<script lang="ts" setup>
import { useAuth } from "@/store";
import api from "@/api";
import user, { bindExtendRelevanceApi, getUnionId } from "@/api/model/user";
const showWebview = ref(false)
const webviewUrl = ref('')
const Auth = useAuth();
const loginWay = ref("password");
const form = ref<any>({
	username: "",
	password: "",
	code: "",
	uid: ""
});
const IsChecked = ref(false)
// 获取图片验证码
const codeUrl = ref('')

const getAuthCode = () => {
	api.user.getAuthCode.get({
		type: "math",
	}).then(({ data }: any) => {
		codeUrl.value = 'data:image/jpeg;base64,' + data.img
		form.value.uid = data.uid
	});
}

watch(() => loginWay.value, (newVal) => {
	if (newVal === 'password') {
		getAuthCode()
	}
}, {
	immediate: true
})
// 获取验证码
const codeTime = ref(0);
const getPhoneCode = () => {
	if (form.value.username.trim() === "") {
		uni.showToast({
			title: "请输入手机号",
			icon: "none",
		});
		return;
	}
	codeTime.value = 60;
	api.user.getAuthCode.get({
		type: "SMS",
		phone: form.value.username,
		tmsg: "1",
	}).then((res: any) => {
		if (res.code == 500) {
			uni.showToast({
				title: "验证码未失效",
				icon: "none",
			});
		}
		else {
			uni.showToast({
				title: "验证码已发送",
				icon: "none",
			});
		}

	});
	const interval = setInterval(() => {
		codeTime.value--;
		if (codeTime.value === 0) {
			clearInterval(interval);
		}
	}, 1000);
};
// 同意条款
const isAgree = ref(false);
// 页面跳转
const toPage = (type: string) => {
	uni.navigateTo({
		url: `/pages/login/register?type=${type}`,
	});
}
//
const toUrl = (url: string) => {
	uni.navigateTo({ url: `/pages/webView/index?url=${url}` })
}
// 登录处理
const loginHandle = async () => {
	// if (!isAgree.value) {
	// 	uni.showToast({
	// 		title: "请先同意服务条款",
	// 		icon: "none",
	// 	});
	// 	return
	// }
	if (!IsChecked.value) {
		uni.showToast({
			title: "请先同意服务条款",
			icon: "none",
		});
		return
	}
	if (form.value.username.trim() === "") {
		uni.showToast({
			title: "请输入手机号",
			icon: "none",
		});
		return;
	}
	if (loginWay.value === 'password') {

		if (form.value.password.trim() === "") {
			uni.showToast({
				title: "请输入密码",
				icon: "none",
			});
			return;
		}
	} else if (loginWay.value === 'code') {
		if (form.value.code.trim() === "") {
			uni.showToast({
				title: "请输入验证码",
				icon: "none",
			});
			return;
		}
	}
	// 登录逻辑
	let params
	// 验证码登录
	if (loginWay.value == 'code') {
		params = {
			phone: form.value.username,
			code: form.value.code,
			uid: form.value.username,
			unionId: Auth.GET_UNIONID(),
			openId: Auth.GET_OPENID()
		}

	}
	// 密码登录
	else if (loginWay.value == 'password') {
		params = {
			username: form.value.username,
			password: form.value.password,
			uid: form.value.uid,
			code: form.value.code,
			unionId: Auth.GET_UNIONID(),
			openId: Auth.GET_OPENID()
		}
	}
	// todo: 获取用户授权,不同意就不允许登录
	await api.user.login.post(params, loginWay.value == 'password' ? "USERNAME_PASSWORD_LOGIN" : "SMS_LOGIN").then((res: any) => {
		if (res.code == 200) {
			uni.showToast({
				title: "登录成功",
				icon: "none",
			});
			const unionId = Auth.GET_UNIONID()
			Auth.SET_TOKEN(res.data.token)
			Auth.GetUserInfo()
			const scene = Auth.GET_SCENE()
			if (scene) {
				bindExtendRelevanceApi(scene)
				Auth.CLEAR_SCENE()
			}
			uni.switchTab({
				url: '/pages/index/index'
			})
		} else {
			uni.showToast({
				title: "密码或验证码错误",
				icon: "none",
			});
		}
	})
}
const wechatLogin = () => {
	if (!IsChecked.value) {
		uni.showToast({
			title: "请先同意服务条款",
			icon: "none",
		});
		return
	}
	uni.login({
		"provider": "weixin",
		onlyAuthorize: true,
		success: function (event) {
			const { code } = event
			//客户端成功获取授权临时票据（code）,向业务服务器发起登录请求。
			getUnionId({ code }).then((res: any) => {
				Auth.SET_OPENID(res.data.openId)
				Auth.SET_UNIONID(res.data.unionId)
				// 如果已经绑定了,则直接走unionId登录
				if (res.data.isBind) {
					uni.showLoading({
						title: '正在登录中...',
						mask: true
					})
					api.user.unionIdLogin.post({
						unionId: res.data.unionId,
						openId: res.data.openId,

					}).then((res: any) => {
						if (res.code === 200) {
							uni.hideLoading()
							Auth.SET_TOKEN(res.data.token)
							Auth.GetUserInfo()
							const scene = Auth.GET_SCENE()
							if (scene) {
								bindExtendRelevanceApi(scene)
								Auth.CLEAR_SCENE()
							}
							uni.switchTab({
								url: '/pages/index/index'
							})
						}
					})

				}
				// 如果未绑定,则提示用户进行绑定
				else {
					uni.showToast({
						title: '当前微信号暂未绑定平台账户,请自行登录/注册后系统将自动绑定',
						icon: 'none',
						mask: true,
						duration: 2500
					})
				}
			})
		},
		fail: function (err) {
			// 登录授权失败
			// err.code是错误码
			uni.showToast({
				title: '请授权',
				icon: 'error',
				mask: true
			})
		}
	})

}
// // 获取手机号
// const onGetPhoneNumber = (e: any) => {
// 	console.log(e)
// }
</script>
<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding-bottom: 24rpx;
	margin-left: 40rpx;
	margin-right: 40rpx;

	.bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 458rpx;
		display: block;
		z-index: 0;
	}

	.content-head {
		color: #000;
		margin-top: 224rpx;
		z-index: 1;
		width: 136px;
		font-weight: bold;
		font-size: 34rpx;
		white-space: nowrap;
	}

	.login-way {
		margin-top: 48rpx;
		display: flex;
		flex-direction: row;
		align-items: center;

		.split {
			width: 1rpx;
			height: 32rpx;
			background-color: #83889d;
		}

		.login-way-item {
			font-weight: bold;
			font-size: 22px;
			color: #83889d;
			line-height: 30px;
			padding-right: 48rpx;
		}

		.active {
			color: #000000;
		}
	}

	.login-form {
		margin-top: 80rpx;
		display: flex;
		flex-direction: column;
		gap: 40rpx;

		.authCode {
			display: flex;
			align-items: center;
			display: grid;
			grid-template-columns: 2fr 1fr;
			gap: 10rpx;

			image {
				width: 90%;
				height: 60rpx;
				cursor: pointer;
			}
		}

		.u-input {
			::v-deep .u-input__content {
				width: 100%;
				height: 47px;
				background: #f5f5f5;
				border-radius: 24px 24px 24px 24px !important;
			}
		}

		.blue-btn {
			padding: 0 10rpx;
			color: #409eff;
		}

		.tip {
			display: flex;
			flex-direction: row;
			align-items: center;

			.tip-text {
				display: flex;
				flex-direction: row;
				align-items: center;
				font-weight: 400;
				font-size: 13px;
				color: #000;
				line-height: 18px;
				white-space: nowrap;

				.blue-btn {
					padding: 0 10rpx;
					color: #409eff;
				}
			}
		}
	}

	.btn {
		margin-top: 80rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		gap: 40rpx;

		.login-btn {
			width: 100%;
			height: 94rpx;
			background: #3972fd;
			border-radius: 48rpx;
			font-weight: 400;
			font-size: 30rpx;
			color: #ffffff;
			display: flex;
			justify-content: center;
			align-items: center;
		}

		.other-btns {
			display: flex;
			flex-direction: row;
			justify-content: center;
			align-items: center;

			.other-btn {
				width: 104rpx;
				height: 36rpx;
				font-weight: 400;
				font-size: 26rpx;
				color: #83889d;
			}

			.split {
				width: 1rpx;
				height: 32rpx;
				background: #e5e5e5;
				border-radius: 0px 0px 0px 0px;
			}
		}
	}

	.wechat-login {
		margin-top: 160rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		gap: 16rpx;

		.icon {
			width: 57px;
			height: 57px;
			background: #eef8f2;
		}

		.text {
			font-weight: 400;
			font-size: 13px;
		}
	}
}
</style>
