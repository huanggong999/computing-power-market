<template>
	<view class="content">
		<scroll-view
			refresher-background="transparent"
			style="height: 100%"
			scroll-with-animation
			scroll-y
			@scrolltolower="reachBottom"
			refresher-enabled
			@refresherrefresh="refreshData"
			:refresher-triggered="triggered"
		>
			<view
				class="content-item mb20"
				v-for="(item, index) in dataList"
				:key="index"
			>
				<ResourceBillCard :currentItem="item" />
			</view>
			<MyEmpty
				text="暂无内容"
				icon="/image/bill_empty.png"
				:loadText="loadText"
				:IsEmpty="dataList.length !== 0"
			/>
		</scroll-view>
	</view>
</template>

<script setup lang="ts">
import { billPage } from "@/api/model/bill";
import { usePageList } from "@/hook/pageList";
import ResourceBillCard from "./ResourceBillCard.vue";

const props = defineProps<{ month: string }>();

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
	api: billPage,
	requestAuto: false,
});
watch(
	() => props.month,
	(val) => {
		searchParam.value.month = val;
		searchFn();
	}
);
defineExpose({ searchFn });
onMounted(() => getList());
</script>

<style scoped lang="scss">
.content {
	height: 1052rpx;
	overflow: auto;
	position: relative;
}
</style>
