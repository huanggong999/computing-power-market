<template>
	<NavCom title="发票详情" bgColor="#fff" />
	<view class="invoice-detail">
		<view class="card card1">
			<view class="title">{{ invoice.INVOICE_DATA.invoiceType == '1' ? '增值税普通发票' : '增值税专用发票' }}</view>
			<view class="status" :style="invoiceStatus == '开票中' ? { color: '#FBA201' } : {}">{{ invoiceStatus }}</view>
		</view>
		<view class="card list">
			<view class="item">
				<view class="label">发票号码</view>
				<view class="value">{{ invoice.INVOICE_DATA.invoiceNumber }}</view>
			</view>
			<view class="item">
				<view class="label">开票金额</view>
				<view class="value">{{ invoice.INVOICE_DATA.amount }}</view>
			</view>
		</view>
		<view class="card list">
			<view class="item">
				<view class="label">抬头类型</view>
				<view class="value">{{ invoice.INVOICE_DATA.companyContactName ? '企业' : '个人' }}</view>
			</view>
			<view class="item">
				<view class="label">发票抬头</view>
				<view class="value">{{ invoice.INVOICE_DATA.invoiceTitle }}</view>
			</view>
			<view class="item">
				<view class="label">税号</view>
				<view class="value">{{ invoice.INVOICE_DATA.companyCode }}</view>
			</view>
			<view class="item">
				<view class="label">发票内容</view>
				<view class="value">服务费</view>
			</view>
		</view>
		<view class="card list">
			<view class="item">
				<view class="label">邮箱</view>
				<view class="value">{{ invoice.INVOICE_DATA.email }}</view>
			</view>
		</view>
		<view class="footer">
			<!-- <button v-if="invoice.INVOICE_DATA.status == 1" class="btn">取消开票</button> -->
			<!-- <button v-if="invoice.INVOICE_DATA.status == 2" class="btn-check">查看发票</button> -->
		</view>
	</view>
</template>

<script setup lang="ts">
import { useInvoice } from "@/store/modules/invoice";
import { defineProps, } from "vue";
const invoice = useInvoice()
const invoiceStatus = computed(() => {
	switch (invoice.INVOICE_DATA.status) {
		case 1:
			return '开票中'
		case 2:
			return '已开票'
		case 3:
			return '开票失败'
	}
})
const props = defineProps({
	id: {
		type: Number,
		default: () => { },
	},
});
</script>

<style lang="scss" scoped>
.invoice-detail {
	padding: 200rpx 24rpx 98rpx 24rpx;
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	height: 85vh;

	.card {
		background: #FFFFFF;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
		padding: 34rpx 28rpx;
	}

	.item {
		display: flex;
		align-items: center;
		gap: 40rpx;

		.label {
			min-width: 112rpx;
			font-weight: 400;
			font-size: 28rpx;
			color: #83889D;
		}

		.value {
			white-space: nowrap;
			font-weight: 400;
			font-size: 28rpx;
			color: #000000;
		}
	}

	.list {
		display: flex;
		flex-direction: column;
		gap: 34rpx;
	}

	.card1 {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-weight: 400;
		font-size: 28rpx;
		color: #000000;
	}

	.footer {
		margin-top: auto;

		.btn {

			height: 80rpx;
			background: #FFFFFF;
			border-radius: 10rpx;
			font-family: PingFang SC, PingFang SC;
			font-weight: 400;
			font-size: 28rpx;
			color: #3972FD;

			text-align: center;

		}

		.btn-check {

			height: 80rpx;
			background: #3972FD;
			border-radius: 10rpx;

			font-weight: 400;
			font-size: 28rpx;
			color: #FFFFFF;

			text-align: center;

		}
	}
}
</style>
