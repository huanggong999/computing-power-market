<template>
	<NavCom title="审核状态" />
	<view class="content">
		<image class="content-icon" :src="$ICON(`/mine/companyStatus_bg_${status}.png`)" />
		<view class="content-status mt48 fwb fz-36">
			{{ defaultEnum.statusText }}
		</view>
		<view class="mt70 fz-28 lh-44 content-tip">
			{{ defaultEnum.statusTip }}
		</view>
		<view class="content-submit fz-28 lh-48" @click="handleNavigation">
			{{ defaultEnum.submitText }}
		</view>
	</view>
</template>

<script setup lang="ts">
const status = ref("2");
const tip = ref("");

const defaultEnum = computed(() => ({
	statusText: status.value === "2" ? "正在审核中！" : "审核不通过",
	statusTip:
		status.value === "2"
			? "您的资料已经提交，请耐心等待 工作人员审核结果"
			: ` `,
	submitText: status.value === "2" ? "返回" : "重新申请",
}));

const handleNavigation = () => {
	if (status.value === "2") return uni.navigateBack();
	uni.redirectTo({ url: "/packageSetting/businessVerification/index" });
};
onLoad((options) => {
	status.value = options?.status;
	tip.value = options?.tip?.trim() || "";
});
</script>

<style lang="scss" scoped>
.content {
	padding-top: 176rpx;
	padding: 176rpx 178rpx 176rpx 180rpx;
	text-align: center;
	position: relative;

	&-icon {
		margin-top: 176rpx;
		width: 230rpx;
		height: 230rpx;
	}

	&-status {
		color: #333;
	}

	&-tip {
		color: #83889d;
	}

	&-submit {
		margin-top: 108rpx;
		background: #3972fd;
		border-radius: 44rpx;
		color: #fff;
		padding: 20rpx 0;
	}
}
</style>
