<template>
	<view class="invoice-apply">
		<view class="select-bar">
			<view :class="type === 1 ? 'active select-option' : 'select-option'" @click="type = 1">按账期开票
			</view>
			<view :class="type === 2 ? 'active select-option' : 'select-option'" @click="type = 2">按消费时间开票
			</view>
		</view>
		<view class="select-list" v-if="type === 1">
			<scroll-view refresher-background="transparent" style="height: 100%" scroll-with-animation scroll-y
				@scrolltolower="reachBottom" refresher-enabled @refresherrefresh="refreshData"
				:refresher-triggered="triggered">
				<up-checkbox-group @change="handleGroupChange">
					<selectOption v-for="item in dataList" type="date" :key="item.id" :data="item"
						@change="handleOptionChange" />
				</up-checkbox-group>
			</scroll-view>
		</view>
		<view class="select-list" v-if="type === 2">
			<scroll-view refresher-background="transparent" style="height: 100%" scroll-with-animation scroll-y
				@scrolltolower="reachBottom" refresher-enabled @refresherrefresh="refreshData"
				:refresher-triggered="triggered">
				<up-checkbox-group @change="handleGroupChange">
					<selectOption v-for="item in dataList" type="time" :key="item.id" :data="item"
						@change="handleOptionChange" />
				</up-checkbox-group>
			</scroll-view>
		</view>
		<view class="footer">
			<button class="btn" @click="nextStep">下一步</button>
		</view>
	</view>
</template>

<script setup lang="ts">
import { usePageList } from '@/hook/pageList';
import selectOption from './components/selectOption.vue'
import { getEnableInvoiceListApi } from '@/api/model/invoice';
import { useInvoice } from '@/store/modules/invoice';
const INVOICE = useInvoice()
const type = ref<number>(1)
// 获取页面数据
const {
	dataList,
	getList,
	triggered,
	refreshData,
	reachBottom,
	loadText,
	searchParam,
	searchFn,
} = usePageList({
	api: getEnableInvoiceListApi,
	requestAuto: false,
});

const initPage = () => {
	searchParam.value.type = 1
	getList()
}
onMounted(() => {
	initPage()
})
watch(() => type.value, (newVal) => {
	searchParam.value.type = newVal
	selectedValues.value = []
	dataList.value = []
	getList()
})

const selectedValues = ref<any>([])
const selectAmounts = ref<any>(0)
// 单个选项选中处理
const handleOptionChange = (e: any) => {
	const { value, selected, amount } = e
	console.log('value', value)
	console.log('amount', amount)
	if (selected) {
		selectedValues.value.push(value)
		selectAmounts.value += Number(amount)
	}
	else {
		selectedValues.value = selectedValues.value.filter((item: any) => item !== value);
		selectAmounts.value -= Number(amount)
	}
}
// 当前选中的值集合
const handleGroupChange = (e: any) => {
	console.log('当前值', selectedValues.value)
}

// 下一步
const nextStep = () => {
	if (selectedValues.value.length <= 0) {
		uni.showToast({
			title: '请选择开票账单',
			icon: 'none',
		})
	} else {
		// 如果按账期开票，则需要拆分billIds
		let billIds: any = []
		if (type.value === 1) {
			selectedValues.value.forEach((item: any) => {
				item.split(',').map((el: any) => {
					billIds.push(el)
				})
			})
		} else {
			billIds = selectedValues.value
		}
		INVOICE.SET_BILLIDS(billIds)
		INVOICE.SET_BILL_AMOUNT(selectAmounts.value)
		INVOICE.SET_INVOICE_TYPE(type.value)
		uni.navigateTo({
			url: '/pages/invoice/finalApply',
		})
	}
}
</script>

<style lang="scss" scoped>
.invoice-apply {
	padding: 200rpx 24rpx 98rpx 24rpx;
	position: relative;
	background: linear-gradient(180deg, #E6EEFE 0%, #ECF3FE 100%) no-repeat;
	background-size: 100% 288rpx;

	.select-bar {
		height: 72rpx;
		background: #FFFFFF;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
		display: grid;
		grid-template-columns: 1fr 1fr;
		z-index: 1;

		.select-option {
			display: flex;
			justify-content: center;
			align-items: center;
			height: 72rpx;
			border-radius: 16rpx 16rpx 16rpx 16rpx;
			font-weight: 400;
			font-size: 28rpx;
			color: #000000;
		}

		.active {
			background: #3972FD;
			font-weight: 400;
			font-size: 28rpx;
			color: #FFFFFF;
		}
	}

	.select-list {
		margin-top: 48rpx;
		height: 1140rpx;
		overflow: auto;
		margin-bottom: 100rpx;
	}

	.footer {
		position: fixed;
		top: 90%;


		.btn {
			width: 702rpx;
			height: 80rpx;
			background: #3972FD;
			border-radius: 44rpx 44rpx 44rpx 44rpx;
			font-weight: 400;
			font-size: 28rpx;
			color: #FFFFFF;
			text-align: center;

		}
	}
}
</style>
