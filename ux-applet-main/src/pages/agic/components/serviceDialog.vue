<template>
	<view class="dialog-mask">
		<view class="service-dialog">
			<image class="close" style="width:58rpx;height:58rpx;" :src="$ICON('/image/close_btn.png')"
				@click="close" />
			<image :src="$ICON('/image/form_code.png')" />
		</view>
		<view class="btns">
			<up-button class="btn" @click="close">已添加,返回首页</up-button>
			<up-button class="btn" type="primary" @click="saveCode">保存二维码</up-button>
		</view>
	</view>

</template>

<script setup lang="ts">
import { saveNetworkImage } from '@/utils/image'


const props = defineProps({
	info: {
		type: Object,
		default: () => { },
	},
})
const emits = defineEmits(['close'])
const close = () => {
	emits('close')
}
const saveCode = () => {
	saveNetworkImage('https://ai-cloud-system.tos-cn-beijing.volces.com/applet/image/service_code.png')
}
</script>

<style lang="scss" scoped>
.dialog-mask {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 200;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;

	.service-dialog {


		display: flex;
		flex-direction: column;
		gap: 30rpx;

		image {
			width: 630rpx;
			height: 922rpx;
		}



	}

	.close {
		position: absolute;
		left: 84%;
		width: 58rpx;
		height: 58rpx;
	}

	.btns {
		width: 327px;
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		align-items: center;
		gap: 22rpx;

		margin-top: 30rpx;

		.btn {
			width: 100%;
			height: 72rpx;
			background: #FFFFFF;
			border-radius: 38rpx 38rpx 38rpx 38rpx;
			font-weight: 400;
			font-size: 28rpx;
			margin: 0;
		}
	}
}
</style>
