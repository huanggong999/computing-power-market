<template>
	<view class="service">
		<view class="service-tip">
			<u-image
				width="40rpx"
				height="40rpx"
				radius="50%"
				:src="$ICON(`/mine/${icon}.png`)"
			/>
		</view>
		<view class="service-info ml10">
			<view>{{ label }}</view>
			<image
				class="service-info-icon"
				:src="$ICON('/mine/arrow_right.png')"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{ label: string; icon: string }>();
</script>

<style lang="scss" scoped>
.service {
	background: #ffffff;
	border-radius: 24rpx;
	padding: 28rpx 16rpx 28rpx 24rpx;
	display: flex;
	align-items: center;
	font-size: 28rpx;
	color: #333333;

	&-tip {
		width: 40rpx;
		height: 40rpx;
	}
	&-info {
		display: flex;
		align-items: center;
		justify-content: space-between;
		width: calc(100% - 50rpx);
		&-icon {
			width: 36rpx;
			height: 36rpx;
		}
	}
}
</style>
