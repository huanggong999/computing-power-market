<template>
	<view class="content" :style="backgroundImage">
		<view class="pt30 pl40 fz-30 fwb lh-42"> 账户信息 </view>
		<view class="content-price mt50 lh-80 flex fwb">
			<view class="fz-28">￥</view>
			<view class="fz-56"> {{ userInfo.balance }} </view>
		</view>
		<view class="content-tip mt4 fz-26 flex"> 当前余额 </view>

		<view class="content-userInfo mt26 between">
			<view class="content-userInfo-left flex">
				<up-image
					width="60rpx"
					height="60rpx"
					:src="userInfo.avatar"
					shape="circle"
				/>
				<view class="fz-26 ml20"> {{ userInfo.customerName }} </view>
			</view>
			<view class="content-userInfo-right fz-26">
				ID: {{ userInfo.userId }}
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { useAuth } from "@/store";
import { buildIcon } from "@/utils/icon";

const backgroundImage = computed(() => ({
	backgroundImage: `url(${buildIcon("/mine/account_info_bg.png")})`,
}));

const Auth = useAuth();
const userInfo = computed(() => Auth.USER_INFO || {});
</script>

<style lang="scss" scoped>
.content {
	width: 702rpx;
	height: 402rpx;
	background-repeat: no-repeat;
	background-size: 100% 100%;
	&-price {
		align-items: baseline;
		color: #3972fd;
	}
	&-tip {
		color: #666;
	}
	&-userInfo {
		background: linear-gradient(180deg, #ffffff 0%, #f7f8fb 100%);
		border-radius: 12rpx;
		padding: 28rpx 30rpx;
		margin: 0 8rpx;
		&-left {
			color: #0f0f2b;
		}
		&-right {
			color: #7f7f7f;
		}
	}
}
</style>
