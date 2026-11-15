<template>
	<view class="content between">
		<view
			class="content-item fz-32 lh-44 flex"
			:class="{ active: currentType === item.label }"
			v-for="(item, index) in list"
			:key="index"
			@click="handlerClick(item)"
		>
			{{ item.label }}
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{ list: TKeyValue[]; currentType: string }>();
const emit = defineEmits(["handlerClick"]);
const handlerClick = (item: TKeyValue) => emit("handlerClick", item);
</script>

<style lang="scss" scoped>
.content {
	background-color: #fff;
	justify-content: space-around;
	&-item {
		padding: 20rpx 0;
		color: #0f0f2b;
	}
	.active {
		position: relative;
		&::after {
			content: "";
			position: absolute;
			width: 30rpx;
			height: 6rpx;
			background: #3972fd;
			border-radius: 4rpx;
			bottom: 0;
		}
	}
}
</style>
