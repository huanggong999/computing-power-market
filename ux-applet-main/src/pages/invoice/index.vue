<template>
	fa
	<NavCom title="发票管理" bgColor="#fff" />
	<view class="invoice-manage">
		<view class="review-card">
			<view class="line1">
				<view class="review-item">
					<view class="label">可开发票金额(元)</view>
					<view class="value">{{ pageData.amount }}</view>
				</view>
				<up-button :customStyle="{ width: '86px', margin: 0 }" type="primary" shape="circle" text="申请开票"
					@click="toApply"></up-button>
			</view>
			<view class="line2">
				<view class="review-item border-right">
					<view class="label">开票总额(元)</view>
					<view class="value">{{ pageData.totalAmount }}</view>
				</view>
				<view class="review-item">
					<view class="label">已开票金额(元)</view>
					<view class="value">{{ pageData.alreadyAmount }}</view>
				</view>
			</view>
		</view>
		<view class="manage-card">
			<view class="label mb20">发票管理</view>
			<view class="card-btns">
				<view class="card-btn" @click="moveHandler('title')">抬头管理<up-icon name="edit-pen" size="20"></up-icon>
				</view>
				<view class="card-btn" @click="moveHandler('email')">邮箱管理<up-icon name="edit-pen" size="20"></up-icon>
				</view>
			</view>
		</view>
		<view class="label">发票明细</view>
		<scroll-view refresher-background="transparent" style="height: 100%" scroll-with-animation scroll-y
			@scrolltolower="reachBottom" refresher-enabled @refresherrefresh="refreshData"
			:refresher-triggered="triggered">
			<view class="content-item mb20" v-for="(item, index) in dataList" :key="index">
				<invoiceItem :key="index" :data="item"></invoiceItem>
			</view>
			<MyEmpty text="暂无内容" icon="/image/noinvoice.png" :loadText="loadText" :IsEmpty="dataList.length !== 0" />
		</scroll-view>
	</view>
</template>

<script lang="ts" setup>
import { getInvoiceAmountListApi, getInvoiceListApi } from '@/api/model/invoice';
import invoiceItem from './components/invoiceItem.vue';
import { usePageList } from "@/hook/pageList";
const pageData = ref<any>()
const {
	dataList,
	getList,
	refreshData,
	reachBottom,
	loadText,
	triggered,
	searchParam,
	searchFn,
} = usePageList({ api: getInvoiceListApi, requestAuto: false });
const initPage = () => {
	// 获取开票金额
	getInvoiceAmountListApi().then((res: any) => {
		pageData.value = res.data
	})
	getList()
}
onMounted(() => {
	initPage()
})
const moveHandler = (type: String) => {
	if (type === 'title')
		uni.navigateTo({
			url: '/packageInvoice/titleManagement/index'
		})
	else
		uni.navigateTo({
			url: '/packageInvoice/emailManagement/index'
		})
}
const toApply = () => {
	uni.navigateTo({
		url: '/pages/invoice/apply'
	})
}
</script>
<style lang="scss" scoped>
.invoice-manage {
	padding: 200rpx 24rpx 0rpx;
	display: flex;
	flex-direction: column;
	gap: 26rpx;

	.review-card {
		height: 165px;
		background: linear-gradient(344deg, #FFFFFF 0%, #E6EEFF 100%);
		border-radius: 8px 8px 8px 8px;
		border: 2px solid #FFFFFF;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		padding: 40rpx 32rpx;

		.line1 {
			display: flex;
			align-items: center;
			justify-content: space-between;
		}

		.line2 {
			display: flex;
			align-items: center;
			justify-content: space-between;
		}

		.review-item {
			display: flex;
			flex-direction: column;
			gap: 12rpx;

			.label {
				font-weight: 400;
				font-size: 32rpx;
				color: #83889D;
			}

			.value {
				font-weight: bold;
				font-size: 44rpx;
				color: #000000;
			}
		}

		.border-right {
			padding-right: 80rpx;
			border-right: 1px solid;
			border-color: #E5E5E5;
		}
	}

	.manage-card {
		height: 124px;
		background: #FFFFFF;
		border-radius: 8px 8px 8px 8px;
		padding: 30rpx 24rpx;
		margin-bottom: 30rpx;

		.label {

			font-weight: bold;
			font-size: 30rpx;
			color: #000000;

		}

		.card-btns {
			display: flex;
			gap: 20rpx;

			.card-btn {
				width: 49%;
				height: 130rpx;
				background: #F7F8FB;
				border-radius: 16rpx;
				border: 2rpx solid #E5E5E5;
				display: flex;
				justify-content: center;
				align-items: center;
				font-weight: 400;
				font-size: 28rpx;
				color: #000000;

			}
		}
	}

	.label {
		font-family: PingFang SC, PingFang SC;
		font-weight: bold;
		font-size: 30rpx;
		color: #000000;
	}
}
</style>
