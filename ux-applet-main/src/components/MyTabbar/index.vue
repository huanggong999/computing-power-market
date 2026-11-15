<template>
	<view class="tabBarEmpty" v-if="placeholder"></view>
	<view class="tabBar" :style="customStyle">
		<view class="tabBar-item" v-for="item, index in tabList" :key="index" @click="handlerChange(item)">
			<img class="tabBar-item-icon" :src="checkIcon(item)" />
			<view :class="['tabBar-item-label', checkActive(item) && 'tabBar-item-active']">
				{{ item.text }}
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { tabBar } from "@/pages.json";
const props = defineProps({
	bgColor: {
		type: String,
		default: "#ffffff"
	},
	showShadow: {
		type: Boolean,
		default: true
	},
	/**
	 * fixed定位固定在底部时，是否生成一个等高元素防止塌陷
	 */
	placeholder: {
		type: Boolean,
		default: true
	}
});
const customStyle = computed(() => {
	let style = ''
	if (props.bgColor) {
		style += `background:${props.bgColor};`
	}
	if (props.showShadow) {
		style += `box-shadow: 0rpx -6rpx 12rpx 2rpx rgba(229, 229, 229, 0.4);`
	}
	return style
})
const active = computed(() => uni.$u.page())
const tabList = computed(() => tabBar.list)
const checkActive = (row: any) => {
	if (active.value === `/${row.pagePath}`) {
		return true
	} else {
		return false
	}
}
const checkIcon = (row: any) => {
	if (checkActive(row)) {
		return `../../${row.selectedIconPath}`
	} else {
		return `../../${row.iconPath}`
	}
}
const handlerChange = (item: any) => {
	if (checkActive(item)) return
	uni.switchTab({
		url: `/${item.pagePath}`
	})
}
onMounted(() => {
	// #ifndef H5 || APP-PLUS
	uni.hideHomeButton()
	// #endif
	// uni.hideTabBar()
})
</script>

<style lang="scss" scoped>
.tabBarEmpty {
	height: calc(98rpx + constant(safe-area-inset-bottom));
	height: calc(98rpx + env(safe-area-inset-bottom));
}

.tabBar {
	position: fixed;
	bottom: 0;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	display: flex;
	align-items: stretch;
	justify-content: space-between;
	width: 100%;
	z-index: 666;

	&-item {
		height: 98rpx;
		position: relative;
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-direction: column;

		&-icon {
			width: 48rpx;
			height: 48rpx;
			display: block;
			margin-bottom: 2rpx;

		}

		&-label {
			font-weight: 400;
			font-size: 20rpx;
			color: #999999;
		}

		&-active {
			color: #000000;
		}
	}
}
</style>
