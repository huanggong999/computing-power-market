<template>
	<view class="content">
		<MyNavTitle
			:title="navParam === 'register' ? '注册' : '忘记密码'"
			color="#000"
		/>
		<view class="content-head">{{
			navParam === "register" ? "手机快捷注册" : "忘记密码"
		}}</view>
		<view class="tip mt8 mb60">{{
			navParam === "register"
				? "欢迎注册逸云数智"
				: "使用手机号重新编辑密码"
		}}</view>
		<!-- 注册表单 -->
		<up-form
			v-if="navParam === 'register'"
			labelPosition="left"
			:model="registerForm"
			ref="registerFormRef"
		>
			<up-form-item
				label="用户名"
				prop="registerForm.customerName"
				borderBottom
			>
				<up-input
					v-model="registerForm.customerName"
					border="none"
					placeholder="请输入用户名,5-20个字符"
				></up-input>
			</up-form-item>
			<up-form-item
				label="设置密码"
				prop="registerForm.password"
				borderBottom
			>
				<up-input
					:password="true"
					v-model="registerForm.password"
					border="none"
					placeholder="请输入登录密码"
				></up-input>
			</up-form-item>
			<up-form-item label="手机号" prop="registerForm.phone" borderBottom>
				<up-input
					v-model="registerForm.phone"
					border="none"
					placeholder="请输入手机号"
				></up-input>
			</up-form-item>
			<up-form-item label="验证码" prop="registerForm.code" borderBottom>
				<up-input
					v-model="registerForm.code"
					border="none"
					placeholder="请输入验证码"
				>
					<template #suffix>
						<view class="split"></view>
						<view
							@click="getPhoneCodeRegister"
							v-if="codeTime === 0"
							class="blue-btn"
							>获取验证码</view
						>
						<view v-show="codeTime > 0" class="blue-btn"
							>{{ codeTime }}s</view
						>
					</template></up-input
				>
			</up-form-item>
		</up-form>
		<!-- 忘记密码表单 -->
		<up-form
			v-if="navParam === 'forget'"
			labelPosition="left"
			:model="forgetForm"
			ref="forgetFormRef"
		>
			<up-form-item label="手机号" prop="forgetForm.phone" borderBottom>
				<up-input
					v-model="forgetForm.phone"
					border="none"
					placeholder="请输入手机号"
				></up-input>
			</up-form-item>
			<up-form-item label="验证码" prop="forgetForm.name" borderBottom>
				<up-input
					v-model="forgetForm.code"
					border="none"
					placeholder="请输入验证码"
				>
					<template #suffix>
						<view class="split"></view>
						<view
							@click="getPhoneCodeForget"
							v-if="codeTime === 0"
							class="blue-btn"
							>获取验证码</view
						>
						<view v-show="codeTime > 0" class="blue-btn"
							>{{ codeTime }}s</view
						>
					</template></up-input
				>
			</up-form-item>
			<up-form-item
				label="设置密码"
				prop="forgetForm.password"
				borderBottom
			>
				<up-input
					:password="true"
					v-model="forgetForm.password"
					border="none"
					placeholder="请输入登录密码"
				></up-input>
			</up-form-item>
			<up-form-item label="再次确认" prop="forgetForm.name" borderBottom>
				<up-input
					:password="true"
					v-model="forgetForm.rePassword"
					border="none"
					placeholder="请再次输入登陆密码"
				></up-input>
			</up-form-item>
		</up-form>
		<view class="btn">
			<button class="login-btn" @click="btnHandle">
				{{ navParam === "register" ? "注册" : "确认" }}
			</button>
		</view>
	</view>
</template>

<script lang="ts" setup>
import api from "@/api";
import { registerAPI, resetPasswordAPI } from "@/api/model/user";
import { useAuth } from "@/store";
import tool from "@/utils/tool";

const Auth = useAuth();

const navParam = ref("forget");
onLoad((options?) => {
	navParam.value = options?.type;
});
// 注册
const registerFormRef = ref();
const registerForm = ref({
	customerName: "",
	phone: "",
	password: "",
	code: "",
});
// 获取验证码
const codeTime = ref(0);
const getCode = () => {
	if (registerForm.value.phone === "") {
		uni.showToast({
			title: "请输入手机号",
			icon: "none",
		});
		return;
	}
	codeTime.value = 60;
	const interval = setInterval(() => {
		codeTime.value--;
		if (codeTime.value === 0) {
			clearInterval(interval);
		}
	}, 1000);
};

// 忘记密码
const forgetFormRef = ref();
const forgetForm = ref({
	phone: "",
	password: "",
	rePassword: "",
	code: "",
});

// 获取手机验证码(注册)
const getPhoneCodeRegister = () => {
	if (registerForm.value.phone.trim() === "") {
		uni.showToast({
			title: "请输入手机号",
			icon: "none",
		});
		return;
	}
	codeTime.value = 60;
	api.user.getAuthCode
		.get({
			type: "SMS",
			phone: registerForm.value.phone,
			tmsg: "2",
		})
		.then((res: any) => {
			if (res.code == 500) {
				uni.showToast({
					title: "验证码未失效",
					icon: "none",
				});
			} else {
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

// 获取手机验证码(忘记)
const getPhoneCodeForget = () => {
	if (forgetForm.value.phone.trim() === "") {
		uni.showToast({
			title: "请输入手机号",
			icon: "none",
		});
		return;
	}
	codeTime.value = 60;
	api.user.getAuthCode
		.get({
			type: "SMS",
			phone: forgetForm.value.phone,
			tmsg: "2",
		})
		.then((res: any) => {
			if (res.code == 500) {
				uni.showToast({
					title: "验证码未失效",
					icon: "none",
				});
			} else {
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

// 注册事件
const btnHandle = () => {
	if (navParam.value == "register") {
		if (
			registerForm.value.customerName &&
			registerForm.value.phone &&
			registerForm.value.password &&
			registerForm.value.code
		) {
			const data = {
				customerName: registerForm.value.customerName,
				phone: registerForm.value.phone,
				password: registerForm.value.password,
				code: registerForm.value.code,
				uid: registerForm.value.phone,
				vcode: Auth.GET_SCENE() ?? "",
			};
			registerAPI(data).then((res: any) => {
				if (res.code === 200) {
					uni.showToast({
						title: "注册成功,请前往登录。",
						icon: "none",
					});
					setTimeout(() => {
						// uni.navigateBack({ delta: 1 })
						uni.reLaunch({ url: "/pages/login/index" });
					}, 1500);
				}
			});
		} else {
			uni.showToast({
				title: "请完善信息",
				icon: "none",
			});
			return;
		}
	} else {
		if (
			forgetForm.value.phone &&
			forgetForm.value.password &&
			forgetForm.value.rePassword &&
			forgetForm.value.code
		) {
			if (forgetForm.value.password !== forgetForm.value.rePassword) {
				uni.showToast({
					title: "两次密码不一致",
					icon: "none",
				});
				return;
			}
			const data = {
				phone: forgetForm.value.phone,
				password: forgetForm.value.password,
				code: forgetForm.value.code,
				uid: forgetForm.value.phone,
			};
			resetPasswordAPI(data).then((res: any) => {
				if (res.code === 200) {
					uni.showToast({
						title: "重置成功",
						icon: "none",
					});
					setTimeout(() => {
						uni.navigateBack({ delta: 1 });
					}, 1500);
				}
			});
		} else {
			uni.showToast({
				title: "请完善信息",
				icon: "none",
			});
			return;
		}
	}
};
</script>
<style lang="scss" scoped>
.content {
	padding-top: 104rpx;
	padding-bottom: 24rpx;
	margin-left: 40rpx;
	margin-right: 40rpx;

	.content-head {
		margin-top: 104rpx;
		color: #000;
		z-index: 1;
		font-weight: bold;
		font-size: 44rpx;
	}

	.tip {
		font-weight: 400;
		font-size: 26rpx;
		color: #83889d;
	}

	::v-deep .u-form-item__body__left__content {
		padding-right: 20rpx;
		white-space: nowrap;
	}

	::v-deep .u-form-item__body__right {
		padding-left: 60rpx;
	}

	.blue-btn {
		padding: 0 10rpx;
		color: #409eff;
	}

	.login-form {
		margin-top: 80rpx;
		display: flex;
		flex-direction: column;
		gap: 40rpx;

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
