<template>
	<NavCom title="实名认证" bgColor="#fff" />
	<view class="content">
		<image src="../../static/mine/autonym.png" />
		<view class="tips"> 请进行人脸识别 </view>
		<view class="btn" @click="begin">立即刷脸认证</view>
	</view>
</template>

<script setup lang="ts">
import { getEidResultApi, getEidTokenApi } from "@/api/model/auth";
// @ts-ignore
import { startEid } from "@/mp_ecard_sdk/main";
import { myShowToast } from "@/utils";
const eToken = ref();
const isDone = ref(false);
onLoad((option?) => {
	eToken.value = option?.eToken;
	if (option?.q) {
		//扫码进入
		const qrUrl = decodeURIComponent(option?.q); // 获取到二维码原始链接内容
		//GetWxMiniProgramUrlParam() 这个方法是解析链接地址中的参数方法
		option = GetWxMiniProgramUrlParam(qrUrl); //此处就是我们要获取的参数 json
		eToken.value = option?.eToken;
	}
	console.log(eToken.value);
});
// 拆解路由参数
const GetWxMiniProgramUrlParam = (url: any) => {
	let theRequest: any = {};
	if (url.indexOf("#") != -1) {
		const str = url.split("#")[1];
		const strs = str.split("&");
		for (let i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = decodeURI(
				strs[i].split("=")[1]
			);
		}
	} else if (url.indexOf("?") != -1) {
		const str = url.split("?")[1];
		const strs = str.split("&");
		for (let i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = decodeURI(
				strs[i].split("=")[1]
			);
		}
	}
	return theRequest;
};
const begin = () => {
	const options = {
		data: {
			token: eToken.value,
		},
		verifyDoneCallback(res: any) {
			const { token, verifyDone } = res;
			console.log("收到核身完成的res:", res);
			console.log("核身的token是:", token);
			console.log("是否完成核身:", verifyDone);
			if (verifyDone) {
				getEidResultApi().then((res1: any) => {
					console.log("res1", res1.data);
					if (res1.data) {
						isDone.value = true;
						myShowToast("实名认证成功");
						uni.$u.sleep(2000).then(() => {
							uni.switchTab({ url: "/pages/mine/index" });
						});
					}
				});
			}
		},
	};
	console.log("options", options);
	startEid(options);
};
// watch(() => isDone.value, (val) => {
// 	getEidResultApi().then((res1: any) => {
// 		if (res1.data) {
// 			myShowToast('实名认证成功');
// 			uni.$u.sleep(2000).then(() => {
// 				uni.redirectTo({ url: '/pages/mine/index' });
// 			});
// 		}
// 	})
// })
// 设置定时器，每一秒调用一次getEidResultApi
</script>

<style lang="scss" scoped>
.content {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	padding-top: 400rpx;
	gap: 10rpx;

	image {
		height: 248rpx;
		width: 248rpx;
		padding: 10rpx;
	}

	.tips {
		font-weight: 400;
		font-size: 30rpx;
	}

	.btn {
		width: 340rpx;
		height: 80rpx;
		background: #3972fd;
		border-radius: 44rpx 44rpx 44rpx 44rpx;

		font-weight: 500;
		font-size: 28rpx;
		color: #fffffe;
		display: flex;
		justify-content: center;
		align-items: center;
	}
}
</style>
