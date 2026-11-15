<template>
	<view class="content">
		<view class="fz-30 lh-42 between fwb">
			<view> 应付金额 </view>
			<view> ￥{{ currentItem.payPrice }} </view>
		</view>
		<view class="lh-34 fz-24 between mt16">
			<view class="tip"> 账期 ： {{ currentItem.bill }} </view>
			<MyStatus
				:text="currentItem.status"
				:plainFill="true"
				shape="circle"
				:type="tagColor(currentItem.status)"
				borderColor="transparent"
				size="mini"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{ currentItem: TKeyValue }>();
type TagType = "info" | "success" | "warning" | "error";
const tagColorEnum = { 出账中: "warning", 已结清: "success" };
const tagColor = (status: "出账中" | "已结清"): TagType =>
	tagColorEnum[status] as TagType;
</script>

<style lang="scss" scoped>
.content {
	padding: 30rpx 0;
	border-bottom: 2rpx solid #e5e5e5;
	.tip {
		color: #83889d;
	}
}
</style>
