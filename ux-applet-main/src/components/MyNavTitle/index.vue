<template>
	<view class="content" :style="{ color }"> {{ title }} </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ title: string; color?: string }>(), {
	color: "#000000",
});
</script>

<style lang="scss" scoped>
.content {
	position: absolute;
	top: 105rpx;
	left: 34rpx;
	z-index: 2;
	font-size: 34rpx;
	color: #ffffff;
	font-weight: bold;
}
</style>
