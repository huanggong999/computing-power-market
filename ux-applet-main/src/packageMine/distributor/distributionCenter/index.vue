<template>
	<NavCom title="逸起赚" />
	<view class="content">
		<view class="bg" />
		<view class="content-userInfo between mt16 pl30">
			<view class="flex">
				<image
					class="content-userInfo-avatar mr16"
					:src="UserInfo.avatar"
					mode="aspectFill"
				/>
				<view class="fwb fz-28 lh-40">
					{{ UserInfo.customerName }}
				</view>
			</view>
			<!-- <view class="content-userInfo-rules flex fz-24 lh-34">
				<image
					class="content-userInfo-rules-icon mr4"
					:src="$ICON('/mine/rules.png')"
				/>
				<view> 规则 </view>
			</view> -->
		</view>
		<view class="pd">
			<view class="content-amount between">
				<view>
					<view class="fz-28 tip lh-40"> 可提现金额 </view>
					<view class="fwb mt12 fz-36 lh-50">
						{{ dataForm.canWithdrawalAmount }}
					</view>
				</view>
				<view
					class="content-amount-btn lh-36 fz-26"
					@click="openWithdrawal"
				>
					提现
				</view>
			</view>
			<view class="mt24">
				<MyServices @handlerClick="open" :bankInfo="dataForm" />
			</view>
			<view class="mt24">
				<up-image
					width="702rpx"
					height="752rpx"
					:src="$ICON('/mine/commission_play.png')"
				/>
			</view>
		</view>

		<PromotionalPoster
			:visible="isShowPromotionalPoster"
			:dataForm="dataForm"
			@close="close"
		/>
	</view>
</template>

<script setup lang="ts">
import { useAuth } from "@/store";
import MyServices from "./components/MyServices.vue";
import { getExtendDetailApi } from "@/api/model/mine";
import { myShowToast } from "@/utils";
import PromotionalPoster from "./components/PromotionalPoster.vue";
import onShare from "@/hook/useShare";

const { onShareAppMessage, onShareTimeline } = onShare();
const UserInfo = useAuth().USER_INFO;

const dataForm = ref<TKeyValue>({});

//#region // 推广海报

const isShowPromotionalPoster = ref(false);
const close = () => (isShowPromotionalPoster.value = false);
const open = () => (isShowPromotionalPoster.value = true);

//#endregion

const openWithdrawal = () => {
	if (!dataForm.value.bankUserName) return myShowToast("请先绑定银行卡");
	uni.navigateTo({
		url: `/packageMine/distributor/withdrawal/index?amount=${Number(
			dataForm.value.canWithdrawalAmount
		)}`,
	});
};

const getData = async () => {
	const { data } = await getExtendDetailApi();
	dataForm.value = data;
};
onShow(() => getData());
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	position: relative;
	.bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 606rpx;
		display: block;
		z-index: 1;
		background: linear-gradient(
			142deg,
			#f6f9f8 0%,
			#edf4fd 41%,
			rgba(237, 244, 253, 0) 100%
		);
	}
	.pd {
		padding: 24rpx;
		position: relative;
		z-index: 2;
	}
	&-userInfo {
		position: relative;
		z-index: 2;
		display: flex;
		&-avatar {
			width: 60rpx;
			height: 60rpx;
			border-radius: 50%;
			border: 2rpx solid #fff;
		}
		&-rules {
			color: #fff;
			padding: 8rpx 24rpx 8rpx 16rpx;
			border-radius: 34rpx 0rpx 0rpx 34rpx;
			background: rgba(0, 0, 0, 0.5);
			&-icon {
				width: 34rpx;
				height: 34rpx;
			}
		}
	}
	&-amount {
		padding: 40rpx 32rpx;
		background: linear-gradient(9deg, #ffffff 0%, #e6eeff 100%);
		border: 4rpx solid #ffffff;
		border-radius: 16rpx;
		align-items: flex-end;
		.tip {
			color: #83889d;
		}
		&-btn {
			background: #3972fd;
			border-radius: 34rpx;
			padding: 16rpx 60rpx;
			color: #fff;
		}
	}
}
</style>
