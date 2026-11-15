<template>
	<view class="fwb fz34 lh-48 between">
		<view> {{ title }} </view>
		<view class="tip flex" v-if="isMore" @click="handleMore">
			<view>查看更多</view>
			<up-image
				width="40rpx"
				height="40rpx"
				:src="$ICON('/image/arrow_right.png')"
				bgColor="transparent"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ title: string; isMore?: boolean }>(), {
	isMore: true,
});
const emit = defineEmits(["handleMore"]);
const handleMore = () => emit("handleMore");
</script>

<style lang="scss" scoped>
.tip {
	font-size: 28rpx;
	font-weight: 400;
	color: #999999;
	line-height: 40rpx;
}
</style>
