<template>
	<view class="content">
		<view class="content-top flex">
			<up-image
				width="230rpx"
				height="230rpx"
				:src="$ICON('/mine/bank_info.png')"
				bgColor="transparent"
			/>
			<view class="fwb fz-40 lh-56 mt30">银行卡已绑定</view>
			<view class="content-top-tip fz-26 lh-40 mt20">
				为了保障正常银行卡提现，因此收集您的卡信息，我们全程保证隐私安全，请用户放心
			</view>
		</view>
		<view class="content-bankInfo">
			<view
				class="content-bankInfo-item between fz-28 lh-40"
				v-for="(item, index) in bankInfoList"
				:key="index"
			>
				<view class="label"> {{ item.label }} </view>
				<view class="value"> {{ props.bankData[item.value] }} </view>
			</view>
		</view>
		<view class="content-changeBank flex mt60 fz-26 lh-36" @click="addBank">
			更改银行卡信息
		</view>
	</view>
</template>

<script setup lang="ts">
const props = defineProps<{ bankData: TKeyValue }>();

const bankInfoList = ref([
	{ label: "持卡人", value: "bankUserName" },
	{ label: "卡号", value: "bankNo" },
	{ label: "所在支行", value: "bank" },
	{ label: "身份证号", value: "idCard" },
	{ label: "开户地址", value: "address" },
]);

const addBank = () => {
	const encodedData = encodeURIComponent(JSON.stringify(props.bankData));
	uni.redirectTo({
		url: `/packageMine/distributor/bank/addBank?bankData=${encodedData}`,
	});
};
</script>

<style lang="scss" scoped>
.content {
	&-top {
		padding: 40rpx 60rpx;
		flex-direction: column;
		color: #0f0f2b;
		&-tip {
			color: #83889d;
			text-align: center;
		}
	}
	&-bankInfo {
		padding: 8rpx 28rpx;
		background-color: #fff;
		border-radius: 24rpx;
		&-item {
			padding: 32rpx 0;
			border-bottom: 2rpx solid #e5e5e5;
			&:last-child {
				border-bottom: none;
			}
		}
	}
	&-changeBank {
		padding: 22rpx;
		color: #fff;
		background: #3972fd;
		border-radius: 44rpx;
	}
}
</style>
