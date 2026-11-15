<template>
	<NavCom title="提现" bgColor="#fff" />
	<view class="content">
		<view class="content-top">
			<view class="content-top-box">
				<view class="fz-28 lh-40"> 可提现金额</view>
				<view class="between">
					<view class="fz-36 lh-50">{{ withdrawalAmount }}</view>
					<view
						class="fz-26 lh-36 content-top-box-details"
						@click="details"
					>
						提现明细
					</view>
				</view>
			</view>
		</view>
		<view class="content-bg" />

		<view class="pd">
			<view class="fz-26 lh-36 fwb mb20">
				<text>提现金额</text>
				<text class="tip ml5">
					(代扣费率{{ (config.withServiceScale * 100).toFixed(2) }}%)
				</text>
			</view>
			<up-input
				type="digit"
				placeholder="请输入提现金额"
				v-model="amount"
				border="none"
				:max="withdrawalAmount"
				:customStyle="customStyle"
			>
				<template #prefix><view> ￥ </view> </template>
				<template #suffix>
					<view class="suffix fz-" @click="withdrawAll">
						全部提现
					</view>
				</template>
			</up-input>

			<view
				class="mt100 content-btn fz-26 lh-36 flex"
				@click="withdrawalFn"
			>
				确认提现
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { extendConfigDetailApi, extendWithApi } from "@/api/model/mine";
import { myShowToast } from "@/utils";

const amount = ref(0);
const customStyle = {
	background: "#F7F8FB",
	borderRadius: "16rpx",
	padding: "30rpx",
};
// 提现方法
const withdrawalFn = async () => {
	if (amount.value >= withdrawalAmount.value) {
		amount.value = withdrawalAmount.value;
	}
	if (amount.value == 0) return myShowToast("暂无提现余额");
	await extendWithApi({ amount: amount.value });
	myShowToast("您的提现已申请，请耐心等待工作人员审核");
};
const withdrawAll = () => (amount.value = withdrawalAmount.value);

const details = () =>
	uni.navigateTo({
		url: "/packageMine/distributor/withdrawal/details",
	});

const config = ref<TKeyValue>({});
const getConfig = async () => {
	const { data } = await extendConfigDetailApi();
	config.value = data;
};

const withdrawalAmount = ref(0);
onLoad((options) => {
	withdrawalAmount.value = options?.amount || 0;
	getConfig();
});
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	background-color: #fff;
	height: 100vh;
	box-sizing: border-box;
	&-top {
		background: #f3f4f6;
		padding: 30rpx 30rpx 0;
		&-box {
			padding: 30rpx 30rpx 22rpx 30rpx;
			background: linear-gradient(180deg, #3972fd 0%, #3f97fe 100%);
			border-radius: 16rpx 16rpx 0rpx 0rpx;
			color: #fff;
			&-details {
				padding: 16rpx 34rpx;
				border-radius: 34rpx 34rpx 34rpx 34rpx;
				border: 2rpx solid #ffffff;
			}
		}
	}
	&-bg {
		height: 40rpx;
		border-radius: 16rpx 16rpx 0 0;
		background-color: #fff;
	}
	.pd {
		padding: 0 30rpx;
	}
	.tip {
		color: #83889d;
	}
	.suffix {
		color: #3972fd;
	}
	&-btn {
		padding: 22rpx 0;
		background: #3972fd;
		border-radius: 44rpx;
		color: #fff;
	}
}
</style>
