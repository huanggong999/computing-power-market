<template>
	<view class="content">
		<image class="bg" :src="$ICON('/login/bg.png')" />
		<button v-if="isBind" class="login-btn" @click="toLogin">
			已有账号，请先登录
		</button>
	</view>
</template>

<script setup lang="ts">
import { getUnionId } from "@/api/model/user";
import { useAuth } from "@/store";

const Auth = useAuth();
const toLogin = () => uni.redirectTo({ url: "/pages/login/index" });
const isBind = ref(false);
const transfer = () => {
	if (!!Auth.TOKEN) return;

	uni.login({
		provider: "weixin",
		onlyAuthorize: true,
		success: ({ code }) => {
			getUnionId({ code }).then(({ data }: any) => {
				isBind.value = data.isBind;
				if (!data.isBind)
					return uni.redirectTo({
						url: "/pages/login/register?type=register",
					});
			});
		},
	});
};

onLoad(() => transfer());
</script>

<style lang="scss" scoped>
.content {
	padding: 20rpx;
	width: 100%;
	box-sizing: border-box;
}
.bg {
	width: 100%;
	height: 458rpx;
	height: 658rpx;
}
.login-btn {
	width: 100%;
	height: 94rpx;
	background: #3972fd;
	border-radius: 48rpx;
	font-size: 30rpx;
	color: #ffffff;
	display: flex;
	justify-content: center;
	align-items: center;
	margin-top: 100rpx;
}
</style>
