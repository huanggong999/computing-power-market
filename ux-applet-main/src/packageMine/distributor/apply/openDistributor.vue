<template>
	<NavCom title="成为合作伙伴" bgColor="#fff" />
	<view class="content">
		<image
			class="content-bg"
			width="100%"
			:src="$ICON('/mine/open_distributor_bg.png')"
			mode="widthFix"
		/>
		<image
			class="content-btn"
			:src="$ICON('/mine/application_btn.png')"
			@click="application"
		/>
	</view>
</template>

<script setup lang="ts">
const application = () =>
	uni.navigateTo({
		url: "/packageMine/distributor/apply/applicationOpenDistributor",
	});
</script>

<style lang="scss" scoped>
.content {
	padding-top: 162rpx;
	position: relative;
	background-color: #fff;
	&-bg {
		width: 100%;
	}
	&-btn {
		position: absolute;
		width: 616rpx;
		height: 88rpx;
		left: 50%;
		top: 1292rpx;
		transform: translate(-50%);
	}
}
</style>
