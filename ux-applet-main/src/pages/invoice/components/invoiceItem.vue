<template>

	<view class="invoice-card">
		<view class="line line1">
			<view class="time">{{ data.applicationTime }}</view>
			<view class="status" :style="data.status == 1 ? 'color:#FBA201' : ''">{{ data.status == 1 ? '申请中' : '开票成功'
			}}
			</view>
		</view>
		<view class="line line2">
			<view class="name ">服务费<view class="tag">{{ data.medium }}</view>
			</view>
			<view class="amount">{{ data.amount }}元</view>
		</view>
		<view class="line line3">
			<!-- <button @click="checkInvoice" class="btn">查看发票</button> -->
			<button @click="checkDetail" class="btn">查看详情</button>
		</view>
	</view>
</template>

<script setup lang="ts">
import { useInvoice } from "@/store/modules/invoice";
import { defineProps, defineEmits, computed } from "vue";
const invoice = useInvoice()
const props = defineProps({
	data: {
		type: Object,
		default: () => { },
	},
	// id: {
	// 	type: Number,
	// 	default: () => { },
	// },
	// createTime: {
	// 	type: String,
	// 	default: false,
	// },
	// status: {
	// 	type: Number,
	// 	default: () => { },
	// },
	// name: {
	// 	type: String,
	// 	default: () => { },
	// },
	// type: {
	// 	type: String,
	// 	default: () => { },
	// },
	// amount: {
	// 	type: Number,
	// 	default: () => { },
	// },
});
// 查看发票
const checkInvoice = () => {
	uni.navigateTo({ url: `/pages/invoice/detail?id=${props.data.id}` })
}
// 查看详情
const checkDetail = () => {
	invoice.SET_INVOICE_DATA(props.data)
	uni.navigateTo({ url: `/pages/invoice/detail?id=${props.data.id}` })
}
</script>

<style lang="scss" scoped>
.invoice-card {
	display: grid;
	grid-template-rows: 1fr 104rpx 1fr;
	gap: 20rpx;

	height: 288rpx;
	background: #FFFFFF;
	border-radius: 16rpx;
	padding: 24rpx 0rpx;

	.line {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-left: 24rpx;
		padding-right: 24rpx;
	}

	.line1 {
		.time {
			font-weight: 400;
			font-size: 28rpx;
			color: #83889D;
		}

		.status {
			font-weight: 400;
			font-size: 28rpx;
			color: #83889D;
		}
	}

	.line2 {
		height: 104rpx;
		background: #F7F8FB;

		.name {
			display: flex;
			align-items: center;

			.tag {
				width: 104rpx;
				height: 40rpx;
				border-radius: 8rpx 8rpx 8rpx 8rpx;
				border: 1rpx solid #E5E5E5;
				font-weight: 400;
				font-size: 22rpx;
				color: #83889D;
				padding: 4rpx 8rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				margin-left: 12rpx;
			}
		}
	}

	.line3 {
		display: flex;
		align-items: center;
		gap: 24rpx;
		padding-left: 70%;

		.btn {
			min-width: 160rpx;
			height: 56rpx;
			border-radius: 8rpx 8rpx 8rpx 8rpx;
			border: 1rpx solid #E5E5E5;
			background-color: #FFFFFF;
			font-weight: 400;
			font-size: 26rpx;
			display: flex;
			justify-content: flex-end;
			align-items: center;
		}
	}
}
</style>
