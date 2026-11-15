<template>
	<view class="content flex" @click="addBank">
		<up-image
			width="122rpx"
			height="122rpx"
			:src="$ICON('/mine/add_bank_icon.png')"
			bgColor="transparent"
		/>
		<view class="fwb fz-36 lh-56 mt24">添加银行卡</view>
		<view class="tip fz-28 lh-44 mt16">添加银行卡，用于佣金提现</view>
	</view>
</template>

<script setup lang="ts">
const props = defineProps<{ bankData: TKeyValue }>();
const addBank = () => {
	const encodedData = encodeURIComponent(JSON.stringify(props.bankData));
	uni.navigateTo({
		url: `/packageMine/distributor/bank/addBank?bankData=${encodedData}`,
	});
};
</script>

<style lang="scss" scoped>
.content {
	height: 420rpx;
	width: 702rpx;
	background: #ffffff;
	border-radius: 16rpx;
	color: #333333;
	flex-direction: column;
	.tip {
		color: #83889d;
	}
}
</style>
