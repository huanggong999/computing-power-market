<template>
	<NavCom title="活动详情" bgColor="#fff" />
	<view class="content">
		<view class="pd">
			<view class="fwb fz-40 mt24	 lh-56">{{ dataForm.name }}</view>
			<view class="content-time mt12 fz-24 lh-34 flex-1 mb40">
				<up-image
					width="34rpx"
					height="34rpx"
					:src="$ICON('/image/active_time.png')"
					bgColor="transparent"
				/>
				<view class="ml8">
					{{ `${dataForm.startDate} - ${dataForm.endDate}` }}
				</view>
			</view>
			<scroll-view
				refresher-background="transparent"
				style="height: 1000rpx"
				scroll-with-animation
				scroll-y
			>
				<up-parse
					:content="dataForm.details"
					:showImgMenu="false"
					:tagStyle="styles"
				/>
			</scroll-view>
		</view>

		<!-- <view class="content-link fz-24 lh-34 mt40"> -->
		<button class="content-link mt40 lh-34 fz-24" open-type="share">
			分享给朋友
		</button>
		<!-- </view> -->
	</view>
</template>

<script setup lang="ts">
import { getActiveDetailsApi } from "@/api/model/activity";
import onShare from "@/hook/useShare";
import { myShowToast } from "@/utils"; //
const { onShareAppMessage, onShareTimeline, currentId, currentImage } = onShare(
	`/packageActive/activeCenter/details`
);

const dataForm = ref<TKeyValue>({ name: "", startDate: "", details: "" });
const styles = reactive({ img: "display: block !important" });

const getDetails = async () => {
	uni.showLoading({ title: "加载中", mask: true });
	const { data } = await getActiveDetailsApi(currentId.value);
	dataForm.value = data;
	currentImage.value = dataForm.value.picture;
	uni.hideLoading();
};

onLoad((options) => {
	currentId.value = options?.id;
	getDetails();
});
</script>

<style>
page,
html,
body {
	background-color: #fff !important;
}
</style>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);

	position: relative;
	.pd {
		padding: 0 30rpx;
	}
	&-time {
		color: #999999;
	}
	&-link {
		padding: 24rpx 30rpx;
		width: 40%;
		border: none;
		color: #3972fd;
		background: #d7e2f6;
		border-radius: 36rpx;
	}
}
</style>
