<template>
	<view class="content">
		<view class="fz-30 lh-42 fwb">我的服务</view>
		<view class="content-cellList between mt30">
			<view
				class="content-cellList-item between"
				v-for="(item, index) in cellList"
				:key="index"
				@click="handlerClick(item)"
			>
				<u-image
					width="64rpx"
					height="64rpx"
					bgColor="transparent"
					:src="$ICON(`/mine/${item.icon}.png`)"
				/>
				<view class="cell-item-label mt6 fz-24 lh-34">
					{{ item.label }}
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
const props = defineProps<{ bankInfo: TKeyValue }>();

const cellList = ref([
	{
		label: "收益明细",
		icon: "distribution_order_icon",
		url: "/packageMine/distributor/distributionOrder/index",
	},
	{
		label: "我的团队",
		icon: "my_team_icon",
		url: "/packageMine/distributor/team/index",
	},
	{
		label: "银行卡管理",
		icon: "bank_card_icon",
		url: "/packageMine/distributor/bank/cardManagement",
	},
	{ label: "推广海报", icon: "promotional_poster_icon", url: "" },
]);

const emit = defineEmits(["handlerClick"]);
const handlerClick = (item: TKeyValue) => {
	if (item.label === "推广海报") return emit("handlerClick");
	if (item.label === "银行卡管理") {
		const encodedData = encodeURIComponent(JSON.stringify(props.bankInfo));
		item.url = `/packageMine/distributor/bank/cardManagement?bankData=${encodedData}`;
	}
	uni.navigateTo({ url: item.url });
};
</script>

<style lang="scss" scoped>
.content {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 30rpx 24rpx 34rpx 24rpx;
	&-cellList {
		padding: 0 14rpx 0 16rpx;
		display: flex;
		&-item {
			flex-direction: column;
		}
	}
}
</style>
