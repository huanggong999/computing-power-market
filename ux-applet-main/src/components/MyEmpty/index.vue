<template>
	<view class="noMore flex fz-30">
		<view class="mt20" v-if="IsEmpty"> ———— {{ loadText }} ———— </view>
		<view v-else class="empty">
			<up-empty :text="text" :icon="$ICON(`${icon}`)" />
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{
	loadText: string;
	icon: string;
	text?: string;
	IsEmpty: boolean;
}>();
</script>

<style lang="scss" scoped>
.noMore {
	width: 100%;
	color: #83889d;
	.empty {
		margin-top: 350rpx;
	}
}
</style>
