<template>
	<view class="apply-form">
		<view class="card">
			<view class="item no-between">
				<view class="label">发票金额</view>
				<view class="blue-value">{{ amount }}</view>
			</view>
		</view>
		<view class="card">
			<view class="item">
				<view class="label">发票类型</view>
				<view class="value">增值税电子普通发票</view>
			</view>
			<view class="item">
				<view class="label">发票抬头</view>
				<view class="value">
					<picker @change="invoiceTitleChange" :value="invoiceTitle" :range="titleCols"
						range-key="invoiceTitle">
						<view class="uni-input" :style="invoiceTitle ? {} : { color: '#999999' }">{{ invoiceTitle ?
							titleCols[invoiceTitle].invoiceTitle : '请选择发票抬头 > ' }}</view>
					</picker>
				</view>
			</view>
			<view class="item">
				<view class="label">接收邮箱</view>
				<view class="value">
					<picker @change="invoiceEmailChange" :value="invoiceEmail" :range="emailCols" range-key="email">
						<view class="uni-input" :style="invoiceEmail ? {} : { color: '#999999' }">{{ invoiceEmail ?
							emailCols[invoiceEmail].email : '请选择电子邮箱 > ' }}</view>
					</picker>
				</view>
			</view>
			<view class="item">
				<view class="label">发票内容</view>
				<view class="value">服务费</view>
			</view>
		</view>
		<button class="btn" @click="apply">申请开票</button>

	</view>
</template>

<script setup lang="ts">
import { applyInvoiceApi, getInvoiceEmailListApi, getInvoiceTitleListApi } from "@/api/model/invoice";
import { useInvoice } from "@/store/modules/invoice";
import { defineProps, } from "vue";
const INVOICE = useInvoice()
const amount = INVOICE.GET_BILL_AMOUNT()
const type = INVOICE.GET_INVOICE_TYPE()
const billIds = INVOICE.GET_BILLIDS()
const titleCols = ref<any>([

]);
const invoiceTitle = ref(0)
const invoiceTitleChange = (e: any) => {
	invoiceTitle.value = e.detail.value
}
const emailCols = ref<any>([

]);

const invoiceEmail = ref(0)
const invoiceEmailChange = (e: any) => {
	invoiceEmail.value = e.detail.value
}
// 获取页面数据
const initPage = () => {
	getInvoiceTitleListApi().then((res: any) => {
		titleCols.value = res.data
	})
	getInvoiceEmailListApi().then((res: any) => {
		emailCols.value = res.data
	})
}
initPage()
// 申请开票
const apply = () => {
	const data = {
		billIds: billIds,
		invoiceTitleId: titleCols.value[invoiceTitle.value].id,
		invoiceEmailId: emailCols.value[invoiceEmail.value].id,
		invoicePrice: amount,
		type: type,
	}
	applyInvoiceApi(data).then((res: any) => {
		if (res.code === 200) {
			uni.showToast({
				title: '申请成功',
				icon: 'none'
			})
			uni.navigateTo({
				url: 'pages/invoice/index'
			})
		}
	})
}
</script>

<style lang="scss" scoped>
.apply-form {
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
		justify-content: space-between;
		padding: 17rpx;

		.label {
			font-weight: 400;
			font-size: 28rpx;
			color: #83889D;
		}

		.value {

			font-weight: 400;
			font-size: 28rpx;

		}
	}

	.no-between {
		justify-content: flex-start;
	}

	.blue-value {
		margin-left: 40rpx;
		font-weight: bold;
		font-size: 28rpx;
		color: #3972FD;

	}

	.btn {
		margin-top: 40rpx;
		height: 80rpx;
		width: 100%;
		background: #3972FD;
		border-radius: 44rpx 44rpx 44rpx 44rpx;
		font-weight: 400;
		font-size: 28rpx;
		color: #FFFFFF;
		display: flex;
		justify-content: center;
		align-items: center;
	}
}
</style>
