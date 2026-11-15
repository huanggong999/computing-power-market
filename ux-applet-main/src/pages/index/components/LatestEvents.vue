<template>
	<view class="content">
		<MyTitle title="最新活动" @handleMore="handleMore" />

		<view class="card mt20" @click="handlerClick">
			<up-image
				width="702rpx"
				height="356rpx"
				:src="props.item?.picture"
				bgColor="transparent"
			/>
			<view class="card-info between">
				<view class="fwb mle fz-30 lh-42">
					{{ props.item?.name }}
				</view>
				<button
					class="card-info-share ml20 lh-34 fz-24"
					@click.stop="share"
					open-type="share"
				>
					分享
				</button>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import MyTitle from "./MyTitle.vue";
import onShare from "@/hook/useShare";
const {
	onShareAppMessage,
	onShareTimeline,
	currentImage,
	currentId,
	currentPath,
	currentTitle,
} = onShare();
const props = defineProps<{ item: TKeyValue }>();
const handleMore = () =>
	uni.navigateTo({ url: "/packageActive/activeCenter/index" });

const handlerClick = () =>
	uni.navigateTo({
		url: `/packageActive/activeCenter/details?id=${props.item.id}`,
	});

const share = () => {
	currentImage.value = props.item.picture;
	currentId.value = props.item.id;
	currentTitle.value = props.item.name;
	currentPath.value = `/packageActive/activeCenter/details`;
};
onHide(() => {
	currentImage.value = "";
	currentId.value = "";
	currentPath.value = "/pages/index/index";
	currentTitle.value = "";
});
</script>

<style lang="scss" scoped>
.card {
	border-radius: 16rpx;
	background-color: #fff;
	overflow: hidden;

	&-info {
		padding: 16rpx 20rpx;
		align-items: baseline;
		&-share {
			color: #3972fd;
			border-radius: 6rpx;
			background: #d7e2f6;
			flex-shrink: 0;
			padding: 0 8rpx;
			margin: 0;
		}
	}
}
</style>
