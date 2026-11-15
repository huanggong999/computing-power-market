<template>
	<view class="account-card">
		<view class="fz-30 fwb lh-42">我的账户 </view>
		<view class="tip fz-28 mt24"> 余额（元） </view>
		<view class="between mt12">
			<view class="fz-36 fwb"> {{ balance }} </view>
			<view class="topUp" @click="toPage"> 充值 </view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { useAuth } from "@/store";
const props = defineProps({
	info: {
		type: Object,
		default: () => {},
	},
});
const Auth = useAuth();
const balance = computed(() => (props.info.balance ? props.info.balance : ""));

const toPage = () => {
	if (!Auth.checkLogin()) return;
	uni.navigateTo({ url: "/packageMine/topUp/index" });
};
</script>

<style lang="scss" scoped>
.account-card {
	padding: 30rpx 32rpx;
	background: linear-gradient(150deg, #e6eeff 0%, #ffffff 100%);
	border: 2rpx solid #ffffff;
	border-radius: 24rpx;

	.tip {
		color: #83889d;
	}

	.topUp {
		padding: 16rpx 56rpx;
		border-radius: 42rpx;
		background: #3972fd;
		color: #ffffff;
		font-weight: 500;
	}
}
</style>
