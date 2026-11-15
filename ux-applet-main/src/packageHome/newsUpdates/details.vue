<template>
	<NavCom title="新闻详情" bgColor="#fff" />
	<view class="content">
		<view class="pd">
			<view class="fwb fz-40 mt24 lh-56">{{ dataForm.name }}</view>
			<view class="content-time mt12 fz-24 lh-34 flex-1 mb40">
				<up-image
					width="34rpx"
					height="34rpx"
					:src="$ICON('/image/active_time.png')"
					bgColor="transparent"
				/>
				<view class="ml8">{{ dataForm.createTime }} </view>
			</view>
			<up-parse
				:content="dataForm.content"
				:showImgMenu="false"
				:tagStyle="styles"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
import { getNewsDetailApi } from "@/api/model/news";
import onShare from "@/hook/useShare";
const { onShareAppMessage, onShareTimeline, currentId } = onShare(
	"/packageHome/newsUpdates/details"
);
const styles = reactive({ img: "display: block !important" });
const dataForm = ref<TKeyValue>({});
const getDetails = async () => {
	uni.showLoading({ title: "加载中", mask: true });
	const { data } = await getNewsDetailApi(currentId.value);
	dataForm.value = data;
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
		padding: 0 30rpx 24rpx;
	}
	&-time {
		color: #999999;
	}
}
</style>
