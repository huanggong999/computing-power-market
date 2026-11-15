<template>
	<NavCom title="充值" />
	<view class="content">
		<view class="bg" />
		<view class="pd">
			<AccountInfo />
			<view class="mt24">
				<RechargeAmount ref="rechargeAmountRef" />
			</view>
			<view class="mt40" v-if="activeCouponList.length">
				<Voucher :list="activeCouponList" />
			</view>
		</view>
		<view class="content-button">
			<view class="content-button-tip fz-24 lh-34 flex">
				<image
					class="content-button-tip-image"
					:src="$ICON('/mine/tip_icon.png')"
				/>
				线上充值当天到账，只能用于逸云数智消耗，消耗后可开发票
			</view>
			<view class="content-button-btn mt20" @click="createOrderFn">
				立即充值
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { myShowToast } from "@/utils";
import AccountInfo from "./components/AccountInfo.vue";
import RechargeAmount from "./components/RechargeAmount.vue";
import Voucher from "./components/Voucher.vue";
import {
	createOrderApi,
	getActiveCouponListApi,
	remitAddApi,
} from "@/api/model/mine";
import { weChatPay } from "@/common/weChatPay";

const activeCouponList = ref<TKeyValue[]>([]);
const getCouponListFn = async () => {
	const { data } = await getActiveCouponListApi();
	activeCouponList.value = data;
};

const payParams = ref<TKeyValue>({});

const rechargeAmountRef = ref();

// 创建订单
const createOrderFn = async () => {
	const { amount, onlinePayType, remitItem } = rechargeAmountRef.value;
	if (!amount) return myShowToast("请输入充值金额");

	if (onlinePayType === "REMIT_PAY") return remitPay(remitItem);
	payParams.value = {
		amount,
		onlinePayType,
		orderType: "BALANCE",
		isApplet: true,
	};
	// todo: 暂时处理
	await new Promise((resolve, reject) => {
		uni.login({
			provider: "weixin",
			onlyAuthorize: true,
			success: function (event) {
				const { code } = event; // 客户端成功获取授权临时票据（code）
				payParams.value.loginCode = code;
				resolve(""); // 登录成功后resolve，继续执行后续代码
				console.log("1", payParams.value.loginCode);
			},
			fail: function (err) {
				// 登录授权失败
				uni.showToast({
					title: "请授权",
					icon: "error",
					mask: true,
				});
				reject(err); // 登录失败后reject，停止后续代码执行
			},
		});
	});
	const { data } = await createOrderApi(payParams.value);
	const { code, msg } = await weChatPay(data.onlinePayParam);
	myShowToast(msg, code === 1 ? "success" : "error");
	uni.$u.sleep(2000).then(() => {
		if (code === 1) return uni.switchTab({ url: "/pages/mine/index" });
	});
};
// 对公打款
const remitPay = async (remitItem: TKeyValue) => {
	if (!remitItem.remitName) return myShowToast("请输入汇款主体");
	await remitAddApi(remitItem);
	myShowToast("您已成功提交申请,请等待审核~", "success");
};

onLoad(() => getCouponListFn());
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	position: relative;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);

	.bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 480rpx;
		display: block;
		z-index: 1;
		background: linear-gradient(180deg, #d7e2f6 0%, #f3f4f6 100%);
	}

	.pd {
		padding: 24rpx;
		position: relative;
		z-index: 2;
	}

	&-button {
		background-color: #fff;
		padding: 24rpx;
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
		position: fixed;
		bottom: 0;
		width: 100%;
		z-index: 9;
		box-sizing: border-box;

		&-tip {
			color: #7f7f7f;

			&-image {
				width: 34rpx;
				height: 34rpx;
			}
		}

		&-btn {
			padding: 20rpx 0;
			border-radius: 44rpx;
			background: #3972fd;
			color: #fffffe;
			text-align: center;
		}
	}
}
</style>
