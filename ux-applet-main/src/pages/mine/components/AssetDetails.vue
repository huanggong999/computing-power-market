<template>
	<view class="content">
		<view class="fw30 fwb">资产明细</view>
		<view class="content-list mt20">
			<view class="content-list-item" v-for="item in assetList" :key="item.label" @click="toPage(item.rightIcon)">
				<view class="content-list-item-title lh-40">
					{{ item.label }}
				</view>
				<view class="content-list-item-value lh-40 fwb mt6">
					{{ item.value }}
					<image v-if="item.rightIcon" class="icon" :src="$ICON('/mine/arrow_right.png')" />
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { useAuth } from "@/store";
const props = defineProps({
	info: {
		type: Object,
		default: () => { },
	},
});
const assetList = computed(() => [
	{ label: "优惠券(张)", value: props.info.couponNum ? props.info.couponNum : "0" },
	{ label: "代金券(张)", value: props.info.voucherBalance ? props.info.voucherBalance : "0" },
	{ label: "授信额(元)", value: props.info.creditAmount ? props.info.creditAmount : "0" },
	{ label: "可开发票金额", value: props.info.invoiceTotalAmount ? props.info.invoiceTotalAmount : "0", rightIcon: true },
]);
const Auth = useAuth()
const toPage = (flag?: boolean) => {
	if(!Auth.checkLogin()) return 
	if (flag) {
		uni.navigateTo({
			url: "/pages/invoice/index"
		});

	}

};
</script>

<style lang="scss" scoped>
.content {
	padding: 30rpx 32rpx 20rpx;
	border-radius: 24rpx;
	background-color: #fff;

	&-list {
		font-size: 28rpx;
		display: flex;
		flex-wrap: wrap;

		&-item {
			box-sizing: border-box;
			flex-shrink: 0;
			padding: 18rpx 0 20rpx 0;
			width: 50%;
			border-top: 2rpx solid #e5e5e5;

			&:nth-child(2n) {
				border-left: 2rpx solid #e5e5e5;
				padding-left: 26rpx;
			}

			&-title {
				color: #83889d;
			}

			&-value {
				display: flex;

				.icon {
					width: 40rpx;
					height: 40rpx;
				}
			}
		}
	}
}
</style>
