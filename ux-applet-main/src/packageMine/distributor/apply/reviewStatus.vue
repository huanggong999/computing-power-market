<template>
	<NavCom title="审核状态" bgColor="#fff" />
	<view class="content">
		<image class="content-icon" :src="$ICON(statusIcon)" />
		<view class="content-status mt48 fwb fz-36"> {{ statusText }} </view>
		<view class="mt70 fz-28 lh-44 content-tip">{{ statusTip }} </view>
		<view class="content-submit fz-28 lh-48" @click="submit">
			{{ submitText }}
		</view>
	</view>
</template>

<script lang="ts" setup>
const statusIcon = computed(
	() => `/mine/${status.value === "1" ? "under_review" : "rejected"}.png`
);
// 1 审核中 3 审核不通过
const status = ref("2");
const statusText = computed(() =>
	status.value === "1" ? "正在审核中！" : "审核不通过"
);

const tip = ref("");
const statusTip = computed(() =>
	status.value === "1"
		? "您的资料已经提交，请耐心等待 工作人员审核结果"
		: `不通过原因：${tip.value}`
);
const submitText = computed(() => (status.value === "1" ? "返回" : "重新申请"));
const submit = () => {
	if (status.value === "1") return uni.navigateBack();
	uni.redirectTo({
		url: "/packageMine/distributor/apply/applicationOpenDistributor",
	});
};

onLoad((options) => {
	status.value = options?.status;
	tip.value = options?.tip;
});
</script>

<style lang="scss" scoped>
.content {
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
