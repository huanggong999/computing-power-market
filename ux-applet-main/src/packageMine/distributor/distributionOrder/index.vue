<template>
	<NavCom title="收益明细" bgColor="#ffffff" />
	<view class="content">
		<MyTabs
			:list="tabList"
			:currentType="activeFilterType"
			@handler-click="handlerClick"
		/>

		<view class="mt20 pr24 pl24">
			<OrderList :list="dataList" />
		</view>
		<MyEmpty
			:IsEmpty="dataList.length !== 0"
			icon="/mine/order_empty.png"
			:loadText="loadText"
			text="暂无明细"
		/>
	</view>
</template>

<script setup lang="ts">
import { usePageList } from "@/hook/pageList";
import MyTabs from "./components/MyTabs.vue";
import OrderList from "./components/OrderList.vue";
import { getExtendOrderPageApi } from "@/api/model/mine";

const tabList = reactive([
	{ label: "全部", settlementStatus: "" },
	{ label: "待结算", settlementStatus: 1 },
	{ label: "已完成", settlementStatus: 2 },
]);

const activeFilterType = ref("全部");

// 定义方法
const handlerClick = (item: TKeyValue) => {
	activeFilterType.value = item.label;
	searchParam.value.settlementStatus = item.settlementStatus;
	if (!item.settlementStatus) delete searchParam.value.settlementStatus;
	searchFn();
};

const { dataList, loadText, searchParam, getList, searchFn } = usePageList({
	api: getExtendOrderPageApi,
	requestAuto: false,
});
onShow(() => getList());
</script>

<style lang="scss" scoped>
.content {
	padding-top: 162rpx;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
}
</style>
