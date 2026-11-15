<template>
	<u-popup :show="visible" :closeOnClickOverlay="false" mode="center" bgColor="transparent">
		<view class="popup">
			<up-image width="630rpx" height="992rpx" :src="dataForm.qrCodeLink" />
			<image class="popup-close" @click="close" :src="$ICON('/mine/close_icon.png')" />

			<view class="popup-save fz-28 lh-40">
				<view class="item" @click="saveImage">保存海报</view>
				<button class="item lh-40 fz-28" @click.stop="share" open-type="share">
					分享给朋友
				</button>
			</view>
		</view>
	</u-popup>
</template>

<script setup lang="ts">
import onShare from "@/hook/useShare";
import { myShowToast } from "@/utils";
const {
	onShareAppMessage,
	onShareTimeline,
	currentImage,
	currentPath,
	otherParams,
} = onShare();
const props = defineProps<{ visible: boolean; dataForm: TKeyValue }>();
const saveImage = () => {
	uni.getImageInfo({
		src: props.dataForm.qrCodeLink,
		success: (image) => {
			uni.saveImageToPhotosAlbum({
				filePath: image.path,
				success: () => myShowToast("已保存到相册"),
				fail: () => myShowToast("保存失败"),
				complete: close,
			});
		},
	});
};
const share = () => {
	currentImage.value = props.dataForm.qrCodeLink;
	currentPath.value = "/pages/index/index";
	console.log(props.dataForm);
	otherParams.value = { scene: 't%3D' + props.dataForm.shareKey };
};

const emit = defineEmits(["close"]);
const close = () => emit("close");
</script>

<style lang="scss" scoped>
.popup {
	position: relative;

	&-close {
		width: 58rpx;
		height: 58rpx;
		position: absolute;
		top: 16rpx;
		right: 16rpx;
	}

	&-save {
		position: absolute;
		bottom: -112rpx;
		left: 50%;
		transform: translate(-50%);
		display: flex;
		justify-content: space-evenly;
		width: 100%;

		.item {
			background: #fff;
			border-radius: 38rpx;
			padding: 16rpx 54rpx;
			margin: 0;
		}
	}
}
</style>
