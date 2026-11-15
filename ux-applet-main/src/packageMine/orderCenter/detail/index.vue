<template>
	<NavCom title="产品详情" bgColor="#fff" />
	<view class="content">
		<view class="card order-info">
			<view class="basic">
				<view class="no">订单号:{{ detailInfo.orderNo }}</view>
				<MyStatus
					:type="actualStatusEnum[status].type"
					:text="actualStatusEnum[status].text"
				/>
			</view>
			<view class="info-card">
				<view class="item">
					<view class="label">出口区域</view>
					<view class="value">{{ detailInfo.productName }}</view>
				</view>
				<view class="flx-2">
					<view class="item">
						<view class="label">客户名称</view>
						<view class="value">
							{{ detailInfo.agiCustomerName }}
						</view>
					</view>
					<view class="item">
						<view class="label">付费方式</view>
						<view class="value">
							{{
								detailInfo.chargeType == "POSTPAID_BY_MONTH"
									? "包月"
									: detailInfo.chargeType ==
									  "POSTPAID_BY_YEAR"
									? "包年"
									: "按天计费"
							}}
						</view>
					</view>
				</view>
				<view class="flx-2">
					<view class="item">
						<view class="label">账户数</view>
						<view class="value">{{ detailInfo.networkCount }}</view>
					</view>
					<view class="item">
						<view class="label">带宽</view>
						<view class="value">{{ detailInfo.bandwidth }}M</view>
					</view>
				</view>
				<view class="flx-2">
					<view class="item">
						<view class="label">IP数</view>
						<view class="value">{{ detailInfo.ipCount }}</view>
					</view>
				</view>
				<view class="item">
					<view class="label">联系电话</view>
					<view class="value">{{ detailInfo.mobile }}</view>
				</view>
				<view class="item">
					<view class="label">备注</view>
					<view class="value">
						{{ detailInfo.remark ? detailInfo.remark : "--" }}
					</view>
				</view>
			</view>
		</view>
		<view class="card order-info">
			<view class="card-title">账号信息</view>
			<view
				class="gray-card"
				v-for="(item, index) in detailInfo.userPwdList"
				:key="index"
			>
				<view class="email flex-1">
					<view class="mr10">{{ item.email }}</view>
					<up-copy :content="item.email">
						<up-icon color="#3972fd" name="attach" size="16" />
					</up-copy>
				</view>
				<view class="gray-card-info">
					<view class="label">初始密码</view>
					<view class="value flex-1">
						<view class="mr10">{{ item.pwd }}</view>
						<up-copy :content="item.pwd">
							<up-icon color="#3972fd" name="attach" size="16" />
						</up-copy>
					</view>
					<view
						class="reset"
						v-if="status == 1 || status == 3"
						@click="resetPsw(item.email)"
						>重置密码</view
					>
				</view>
				<view class="gray-card-info">
					<view class="label">公网IP</view>
					<view class="value flex-1">
						<view class="mr10">{{
							item.publicIp ? item.publicIp : "--"
						}}</view>
						<up-copy
							v-if="!!item.publicIp"
							:content="item.publicIp"
						>
							<up-icon color="#3972fd" name="attach" size="16" />
						</up-copy>
					</view>
				</view>
				<view class="gray-card-info">
					<view class="label">IP地区</view>
					<view class="value flex-1">
						<view class="mr10">{{
							item.ipAddress ? item.ipAddress : "--"
						}}</view>
						<up-copy
							v-if="!!item.ipAddress"
							:content="item.ipAddress"
						>
							<up-icon color="#3972fd" name="attach" size="16" />
						</up-copy>
					</view>
				</view>
			</view>
		</view>

		<view class="card order-info tip">
			<view class="card-title">使用提示</view>
			<view>
				<view>
					1、苹果，安卓手机，电脑端直接到应用市场搜索下载：飞连客户端（苹果手机请使用国内的苹果
					ID 下载）
				</view>
				<view class="flex-1">
					下载链接：
					<up-copy
						content="https://www.volcengine.com/product/feilian/download"
					>
						<text class="copy">点击复制</text>
					</up-copy>
				</view>
			</view>
			<view class="flex-1">
				<view> 2、打开客户端并输入企业识别码：agi-turbo </view>
				<up-copy content="agi-turbo">
					<up-icon color="#3972fd" name="attach" size="16" />
				</up-copy>
			</view>
			<view> 3、输入账号 </view>
			<view> 4、输入密码 </view>
			<view> 5、根据提示修改密码。 </view>
			<view> 6、点击VPN网络，拨号成功。 </view>
		</view>

		<!-- <view class="order-createTime mb20">
			<view class="time-item">
				创建时间：<view class="time">{{ detailInfo.createTime }} </view>
			</view>
			<view class="time-item">
				支付时间：<view class="time">{{ detailInfo.payTime }} </view>
			</view>
			<view class="time-item">
				开通时间：<view class="time"
					>{{ status != 0 ? detailInfo.actualAgiOpenTime : "--" }}
				</view>
			</view>
			<view class="time-item">
				到期时间：<view class="time"
					>{{
						status != 0
							? detailInfo.actualAgiExpireTime
								? detailInfo.actualAgiExpireTime
								: "--"
							: "--"
					}}
				</view>
			</view>
		</view> -->
		<!-- <view class="card order-container">
			<view class="order-number"> 产品信息 </view>

			<view class="saas-info">
				<image class="sass-img" :src="detail.productImg"></image>
				<view class="info">
					<view class="title overflow-1">{{
						detail.productName
					}}</view>
					<view class="label">实付价:</view>
					<view class="price">
						￥{{ detail.unitPrice }}/天
						<span class="line-price"
							>￥{{ detail.crossedPrice }}/天</span
						>
					</view>
					<view class="primary-price"
						>参考价：￥{{ detail.referPrice }}/天</view
					>
				</view>
			</view>
		</view> -->
	</view>
	<view class="footer" v-if="status != 0 && status != 2 && status != 4">
		<button
			class="btn blue"
			@click="btnHandler('renew')"
			:style="{ 'margin-left': status == 3 ? '30%' : '20%' }"
		>
			<!-- v-if="detailInfo.chargeType != 'POSTPAID_BY_HOUR'" -->
			续费
		</button>

		<button class="btn blue" @click="btnHandler('edit')">变更套餐</button>

		<button
			class="btn orange"
			@click="btnHandler('stop')"
			v-if="status == 1"
		>
			暂停
		</button>
		<button
			class="btn red"
			@click="btnHandler('cancel')"
			v-if="status == 1"
		>
			撤线
		</button>
		<button
			class="btn green"
			@click="btnHandler('restart')"
			v-if="status == 3"
		>
			重启
		</button>
	</view>
</template>

<script lang="ts" setup>
import {
	agiResetPasswordApi,
	cancelLineApi,
	getAgicOrderInfoApi,
	stopLineApi,
	upLineApi,
} from "@/api/model/networkProduct";
import { buildOrderInfoApi, createOrderApi } from "@/api/model/order";
import { weChatPay } from "@/common/weChatPay";
import { useAuth } from "@/store";
import { useProduction } from "@/store/modules/product";
import { useAgicProduct } from "@/store/modules/agicProduct";
import { myShowToast } from "@/utils";
import tool from "@/utils/tool";
const user = useAuth().USER_INFO;
const productDetail: any = useProduction();
const detail = productDetail.production;
// 获取订单信息
const detailInfo = ref<any>();
const id = ref();
onLoad((option) => {
	id.value = option?.id;
});
const initData = () => {
	getAgicOrderInfoApi(id.value).then((res: any) => {
		console.log("res", res);
		detailInfo.value = res.data;
	});
};
onShow(() => {
	initData();
});
const codeDialogVisible = ref(false);
const closeCodeDialog = () => {
	codeDialogVisible.value = false;
};
// codeDialogVisible为true时候禁止页面滚动
watch(
	() => codeDialogVisible.value,
	(val) => {
		if (val) {
			document.body.style.overflow = "hidden";
		} else {
			document.body.style.overflow = "";
		}
	}
);
const actualStatusEnum: any = [
	{
		type: "warning",
		text: "未开通",
	},
	{
		type: "success",
		text: "运行中",
	},
	{
		type: "warning",
		text: "已过期",
	},
	{
		type: "primary",
		text: "已暂停",
	},
	{
		type: "error",
		text: "已撤线",
	},
];
const status = computed<any>(() => {
	if (detailInfo.value.actualStatus == 1) {
		return 0;
	} else if (detailInfo.value.actualStatus == 2) {
		return 1;
	} else if (detailInfo.value.actualStatus == 3) {
		return 2;
	} else if (detailInfo.value.actualStatus == 4) {
		return 3;
	} else if (detailInfo.value.actualStatus == 5) {
		return 4;
	} else {
		return 0;
	}
});
// 构建订单信息
// const buildOrder = () => {
// 	let params: any = {
// 		orderType: 'PRODUCT',
// 		networkPayValueId: detail.id,
// 		networkProductId: detail.productId,
// 		bandwidth: detail.bandwidth
// 			? detail.bandwidth
// 			: 1,
// 		networkDay: detail.networkDay,
// 		networkCount: detail.networkCount,
// 		payFormValue: detail.payFormValue,
// 	}
// 	buildOrderInfoApi(params).then((res: any) => {
// 		total.value.payAmount = res.data.finalPayAmount
// 	})
// 	// todo:这里后期加获取优惠券
// }
// buildOrder()
// 支付控制台
const dialog = ref(false);
const toPay = () => {
	dialog.value = true;
};

const total = ref({
	discountAmount: 0,
	payAmount: 0,
});
const isDisCount = ref(false);
const selectDisCount = ref<any>({ id: "" });
// 实付总金额（账单金额-优惠券金额）
const billAmount = computed(() => {
	let totalPrice = total.value.payAmount;
	if (isDisCount.value && selectDisCount.value.id) {
		totalPrice -= selectDisCount.value.deductionAmount;
	}
	return Number(totalPrice).toFixed(2);
});
// 代金券支付费用(优先级更高)
const voucherPayAmount = computed(() => {
	if (selectPay.value.indexOf("voucherPay") != -1) {
		// 如果余额能够cover所有费用
		if (user.voucherBalance >= Number(billAmount.value)) {
			return billAmount.value;
		} else {
			return user.voucherBalance;
		}
	} else {
		return 0;
	}
});
console.log("user", user);
// 支付方式
const selectPay = computed<any>(() => {
	const result: string[] = [];

	payCardList.value.forEach((card) => {
		if (!card.isSelected) {
			return;
		}

		const flag = card.flag;

		// 如果已选中，则移除
		if (result.includes(flag)) {
			result.splice(result.indexOf(flag), 1);
		} else {
			// 授信额支付逻辑
			if (flag === "creditLinePay") {
				if (Number(billAmount.value) > Number(user.creditAmount)) {
					payCardList.value[2].isSelected = false;
					uni.showToast({
						title: "授信额度不足，请选择其他支付方式",
						icon: "none",
					});
					return; // 超过授信额度，不添加
				}
				result.length = 0; // 清空所有支付方式
				payCardList.value.forEach((card) => {
					if (card.flag !== "creditLinePay") {
						card.isSelected = false;
					}
				});
				result.push(flag); // 仅保留授信额
			}
			// 其他支付方式
			else {
				result.push(flag);
				// 移除授信额
				if (result.includes("creditLinePay")) {
					result.splice(result.indexOf("creditLinePay"), 1);
				}
			}
		}
	});
	console.log(result);
	return result;
});
// 余额支付费用
const balancePayAmount = computed(() => {
	if (selectPay.value.indexOf("balancePay") != -1) {
		// 查看是否有勾选代金券付款
		let bill = JSON.parse(JSON.stringify(billAmount.value));
		if (selectPay.value.indexOf("voucherPay") != -1) {
			bill -= Number(voucherPayAmount.value);
		}
		// 如果余额能够cover所有费用
		if (user.balance >= Number(bill)) {
			return bill;
		} else {
			return user.balance;
		}
	} else {
		return 0;
	}
});

// 在线支付费用
const onlinePayAmount = computed(() => {
	if (selectPay.value.indexOf("creditLinePay") > -1) {
		return 0;
	}
	let amount = Number(billAmount.value);
	amount -= Number(voucherPayAmount.value);
	amount -= balancePayAmount.value;
	return Number(amount.toFixed(2));
});
// 授信额支付
const creditPayAmount = computed(() => {
	if (selectPay.value.indexOf("creditLinePay") > -1) {
		return Number(billAmount.value).toFixed(2);
	} else {
		return 0;
	}
});
const choosePay = (flag: string) => {
	// 除去已选的支付方式
	if (selectPay.value.indexOf(flag) > -1) {
		selectPay.value.splice(selectPay.value.indexOf(flag), 1);
	} else {
		// 如果是授信额支付，则清除所有的支付方式，仅保留授信额
		if (flag === "creditLinePay") {
			if (Number(billAmount.value) > Number(user.creditAmount)) {
				return;
			}
			selectPay.value = [];
			selectPay.value.push(flag);
		} else if (flag === "ALI_PAY" || flag === "WECHAT_PAY") {
			const i = selectPay.value.findIndex(
				(item) => item === "ALI_PAY" || item === "WECHAT_PAY"
			);
			if (i !== -1) {
				selectPay.value[i] = flag;
			} else {
				selectPay.value.push(flag);
			}
			// 找到授信额，如果有就去除
			if (selectPay.value.indexOf("creditLinePay") > -1) {
				selectPay.value.splice(
					selectPay.value.indexOf("creditLinePay"),
					1
				);
			}
		} else {
			selectPay.value.push(flag);
			// 找到授信额，如果有就去除
			if (selectPay.value.indexOf("creditLinePay") > -1) {
				selectPay.value.splice(
					selectPay.value.indexOf("creditLinePay"),
					1
				);
			}
		}
	}
};
// 支付卡片
const payCardList = ref([
	{
		title: "代金券",
		count: user.voucherBalance,
		balance: user.voucherBalance,
		bgSrc: "voucher-pay",
		flag: "voucherPay",
		noShow: false,
		isSelected: false,
		// payAmount: voucherPayAmount.value
	},
	{
		title: "余额",
		count: user.balance,
		balance: user.balance,
		bgSrc: "balance-pay",
		flag: "balancePay",
		noShow: false,
		isSelected: false,
		// payAmount: balancePayAmount.value
	},
	{
		title: "授信额",
		count: user.creditAmount,
		balance: user.creditAmount,
		bgSrc: "credit-pay",
		flag: "creditLinePay",
		isSelected: false,
		// payAmount: creditPayAmount.value
	},
]);
const IsChecked = ref(false);
const payData = ref({ onlinePayParam: "", orderNo: "", onlinePay: "" });
// 创建订单
const createOrder = async () => {
	if (!IsChecked.value) {
		uni.showToast({
			title: "请先阅读并同意《用户服务协议》",
			icon: "none",
		});
		return;
	}
	const data: any = {
		orderType: "PRODUCT",
		// couponId: selectDisCount.value.id,
		voucherPay: selectPay.value.includes("voucherPay") ? true : false,
		balancePay: selectPay.value.includes("balancePay") ? true : false,
		creditLinePay: selectPay.value.includes("creditLinePay") ? true : false,
		onlinePayType: onlinePayAmount.value > 0 ? "WECHAT_PAY" : null,
		networkPayValueId: detail.id,
		networkProductId: detail.productId,
		networkDay: detail.networkDay,
		bandwidth: detail.bandwidth ? detail.bandwidth : 1,
		networkCount: detail.networkCount,
		payFormValue: detail.payFormValue,
		isApplet: true,
	};

	await new Promise((resolve, reject) => {
		uni.login({
			provider: "weixin",
			onlyAuthorize: true,
			success: function (event) {
				const { code } = event; // 客户端成功获取授权临时票据（code）
				data.loginCode = code;
				resolve(""); // 登录成功后resolve，继续执行后续代码
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

	await createOrderApi(data).then(async (res: any) => {
		if (res.code === 200) {
			if (!res.data.onlinePayParam) {
				uni.showToast({
					title: "已完成订单购买",
					icon: "success",
					duration: 2000,
				});
				uni.$u.sleep(2000).then(() => {
					return uni.navigateBack({ delta: 1 });
				});
			} else {
				const { code, msg } = await weChatPay(res.data.onlinePayParam);
				myShowToast(msg, code === 1 ? "success" : "error");
				uni.$u.sleep(2000).then(() => {
					if (code === 1) return uni.navigateBack({ delta: 1 });
				});
			}
		}
	});
};
// 详情处理
const agicProduct = useAgicProduct();
// 详情操作
const btnHandler = (type: string) => {
	switch (type) {
		case "edit":
			agicProduct.setAgicDetail(detailInfo, "count");
			agicProduct.setDisplayOrBind({
				isAccountIpBinding: detailInfo.value.isAccountIpBinding,
				isBandwidthDisplay: detailInfo.value.isBandwidthDisplay,
				isIpDisplay: detailInfo.value.isIpDisplay,
			});
			uni.navigateTo({
				url: `/packageAgic/buy/index?isEdit=true&id=${detailInfo.value.productId}`,
			});
			break;
		case "renew":
			agicProduct.setAgicDetail(detailInfo, "renew");
			agicProduct.setDisplayOrBind({
				isAccountIpBinding: detailInfo.value.isAccountIpBinding,
				isBandwidthDisplay: detailInfo.value.isBandwidthDisplay,
				isIpDisplay: detailInfo.value.isIpDisplay,
			});
			uni.navigateTo({
				url: `/packageAgic/buy/index?isEdit=true&id=${detailInfo.value.productId}`,
			});
			break;
		case "stop":
			uni.showModal({
				title: "暂停操作",
				content: "是否确认暂停当前产品？",

				success: function (res) {
					if (res.confirm) {
						stopLineApi(detailInfo.value.id).then(() => {
							uni.showToast({
								title: "已停止",
								icon: "success",
							});
							initData();
						});
					} else if (res.cancel) {
						console.log("用户点击取消");
					}
				},
			});

			break;
		case "cancel":
			uni.showModal({
				title: "撤线操作",
				content: "是否确认撤线当前产品？",

				success: function (res) {
					if (res.confirm) {
						cancelLineApi(detailInfo.value.id).then(() => {
							uni.showToast({
								title: "已撤线",
								icon: "success",
							});
							initData();
						});
					} else if (res.cancel) {
						console.log("用户点击取消");
					}
				},
			});

			break;
		case "restart":
			uni.showModal({
				title: "重启操作",
				content: "是否确认重启当前产品？",
				success: function (res) {
					if (res.confirm) {
						upLineApi(detailInfo.value.id).then(() => {
							uni.showToast({
								title: "已重启",
								icon: "success",
							});
							initData();
						});
					} else if (res.cancel) {
						console.log("用户点击取消");
					}
				},
			});

			break;
	}
};
// 充值密码
const resetPsw = (email: string) => {
	agiResetPasswordApi(email).then((res: any) => {
		if (res.code === 200) {
			uni.showToast({
				title: "重置密码成功",
				icon: "success",
			});
			initData();
		} else {
			uni.showToast({
				title: "重置密码失败",
				icon: "none",
			});
		}
	});
};
</script>

<style lang="scss" scoped>
.content {
	padding: 187rpx 24rpx 150rpx;
}

.card {
	width: 662rpx;
	background: #ffffff;
	border-radius: 12px 12px 12px 12px;
	padding: 24rpx;
	margin-bottom: 30rpx;
}

.order-info {
	display: flex;
	flex-direction: column;
	gap: 16rpx;

	.basic {
		display: flex;
		align-items: center;
		justify-content: space-between;

		.no {
			font-weight: 400;
			font-size: 26rpx;
			color: #3b3b3b;
		}
	}

	.info-card {
		background: #f8f8f8;
		border-radius: 8rpx 8rpx 8rpx 8rpx;
		display: flex;
		flex-direction: column;
		gap: 24rpx;
		padding: 24rpx 20rpx;

		.flx-2 {
			display: grid;
			grid-template-columns: 1fr 1fr;
		}

		.item {
			display: flex;
			align-items: center;
			gap: 30rpx;

			.label {
				font-weight: 400;
				font-size: 26rpx;
				color: #83889d;
				min-width: 100rpx;
			}

			.value {
				font-weight: 400;
				font-size: 26rpx;
				color: #000000;
			}
		}
	}

	.card-title {
		font-weight: bold;
		font-size: 30rpx;
		color: #000000;
	}

	.gray-card {
		background: #f8f8f8;
		border-radius: 8rpx 8rpx 8rpx 8rpx;
		display: flex;
		flex-direction: column;
		gap: 24rpx;
		padding: 24rpx 20rpx;
		margin-bottom: 16rpx;

		.email {
			font-weight: bold;
			font-size: 26rpx;
			color: #3972fd;
		}

		.gray-card-info {
			display: flex;
			align-items: center;
			gap: 30rpx;

			.label {
				min-width: 110rpx;
				width: 104rpx;
				font-weight: 400;
				font-size: 26rpx;
				color: #666666;
			}

			.value {
				font-weight: 400;
				font-size: 26rpx;
				color: #000000;
			}
			.reset {
				font-weight: 400;
				font-size: 26rpx;
				color: #3972fd;
			}
		}
	}
}

.order-container {
	width: 88vw;
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	display: flex;
	flex-direction: column;
	gap: 24rpx;

	/* 订单号样式 */
	.order-number {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-weight: bold;
		font-size: 30rpx;
	}

	/* 状态标签 */
	.status-tag {
		margin-left: auto;
		color: #ff4444;
		margin-bottom: 40rpx;
	}

	/* 套餐信息 */

	.saas-info {
		display: flex;
		gap: 24rpx;

		.sass-img {
			width: 274rpx;
			height: 144rpx;
			background: #e6eeff;
			border-radius: 16rpx 16rpx 16rpx 16rpx;
		}

		.info {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			margin-bottom: 24rpx;

			.title {
				font-weight: bold;
				font-size: 32rpx;
				max-width: 300rpx;
			}

			.label {
				font-weight: 400;
				font-size: 24rpx;
				color: #3972fd;
			}

			.price {
				font-weight: bold;
				font-size: 30rpx;
				color: #3972fd;

				.line-price {
					font-weight: 400;
					font-size: 24rpx;
					color: #999999;
					text-decoration-line: line-through;
				}
			}

			.primary-price {
				font-weight: 400;
				font-size: 24rpx;
				color: #ff4151;
			}
		}
	}

	/* 购买信息表格 */
	.purchase-info {
		border-top: #e5e5e5 1rpx solid;
		border-bottom: #e5e5e5 1rpx solid;
		padding: 24rpx 0rpx;

		.info-item {
			display: flex;
			justify-content: space-between;
			margin-bottom: 24rpx;
			line-height: 1.6;
			font-weight: 400;
			font-size: 26rpx;

			.label {
				color: #999;
			}

			.value {
				color: #333;
			}
		}

		:last-child {
			margin-bottom: 0;
		}
	}

	.buttons {
		margin-left: auto;
		display: flex;

		align-items: center;
		gap: 20rpx;

		.btn {
			width: 164rpx;
			height: 60rpx;
			border-radius: 34rpx 34rpx 34rpx 34rpx;
			font-weight: 400;
			font-size: 26rpx;
			display: flex;
			justify-content: center;
			align-items: center;
		}

		.service {
			border: 1rpx solid #fba201;
			color: #fba201;
		}

		.pay {
			border: 1rpx solid #3972fd;
			background: #3972fd;
			color: #fff;
		}
	}
}

.tip {
	// width: 104rpx;
	font-weight: 400;
	font-size: 26rpx;
	color: #666666;
	.copy {
		color: #3972fd;
	}
}

.order-createTime {
	width: 89vw;
	padding: 30rpx 20rpx;
	background: #ffffff;
	box-shadow: 0rpx 0rpx 12rpx 2rpx rgba(181, 181, 181, 0.1);
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	font-weight: 400;
	font-size: 26rpx;
	color: #83889d;
	display: flex;
	flex-direction: column;
	gap: 10rpx;

	.time-item {
		display: flex;
		align-items: center;
		justify-content: space-between;

		.time {
			font-weight: 400;
			font-size: 26rpx;
			color: #000000;
		}
	}
}

// 固定底部
.footer {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	width: 100%;
	height: 100rpx;
	padding-bottom: 40rpx;
	background: #ffffff;
	border-radius: 6rpx 0rpx 6rpx 0rpx;
	display: flex;
	justify-content: flex-end;
	align-items: center;

	.btn {
		width: 164rpx;
		height: 60rpx;
		border-radius: 34rpx 34rpx 34rpx 34rpx;
		font-weight: 400;
		font-size: 26rpx;
		display: flex;
		justify-content: center;
		align-items: center;

		margin-right: 10rpx;
		// 不换行
		white-space: nowrap;
	}
	.gray {
		border: 1rpx solid #999999;
		background: #999999;
		color: #ffffff;
	}
	.blue {
		border: 1rpx solid #3972fd;
		background: #3972fd;
		color: #fff;
	}

	.red {
		border: 1rpx solid #ff4151;
		background: #ff4151;
		color: #fff;
	}
	.orange {
		border: 1rpx solid #f8af10;
		background: #f8af10;
		color: #fff;
	}

	.green {
		border: 1rpx solid #08a030;
		background: #08a030;
		color: #fff;
	}
}

.dialog {
	padding: 36rpx 30rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	background: #f3f4f6;
	border-radius: 16rpx 16rpx 0rpx 0rpx;

	.dialog-header {
		font-weight: 400;
		font-size: 32rpx;
		color: #000000;
	}

	.price-info {
		margin-top: 30rpx;
		display: flex;
		flex-direction: column;
		gap: 6rpx;
		margin-bottom: 26rpx;

		.total {
			.yuan {
				font-weight: 800;
				font-size: 44rpx;
				color: #000000;
			}

			font-weight: 800;
			font-size: 80rpx;
			color: #000000;
		}

		.discount {
			font-weight: 400;
			font-size: 26rpx;
			color: #ff4151;
		}
	}

	.pay-way {
		width: 650rpx;
		background: #ffffff;
		border-radius: 8rpx 8rpx 8rpx 8rpx;
		display: flex;
		flex-direction: column;
		padding: 30rpx 24rpx;
		gap: 24rpx;

		.way-item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-weight: 400;
			font-size: 28rpx;

			.label {
			}

			.option {
				display: flex;
				align-items: center;
				gap: 20rpx;
			}
		}

		.split {
			width: 670rpx;
			height: 1rpx;
			background: #e5e5e5;
			border-radius: 0rpx 0rpx 0rpx 0rpx;
		}

		.final {
			margin-left: auto;
			font-weight: bold;
			font-size: 28rpx;
			color: #000000;
		}
	}

	.online-pay {
		width: 650rpx;
		background: #ffffff;
		border-radius: 8rpx 8rpx 8rpx 8rpx;
		padding: 30rpx 24rpx;
		display: flex;
		flex-direction: column;
		gap: 30rpx;
		margin-top: 24rpx;

		.online-item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-weight: 400;
			font-size: 28rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;

			.label {
				display: flex;
				align-items: center;
				font-weight: 400;
				font-size: 28rpx;
				color: #000000;
			}

			image {
				width: 40rpx;
				height: 40rpx;
			}
		}
	}

	.dialog-footer {
		display: flex;
		flex-direction: column;
		margin-top: 122rpx;

		.form-agreement {
			display: flex;
			align-items: center;
			gap: 6rpx;

			margin-right: auto;

			&-icon {
				width: 28rpx;
				height: 28rpx;
			}

			&-tip {
				color: #3972fd;
			}
		}

		.pay {
			margin-top: 10rpx;
			width: 690rpx;
			height: 80rpx;
			background: #3972fd;
			border-radius: 44rpx 44rpx 44rpx 44rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			color: #fff;
			font-weight: 400;
			font-size: 28rpx;
		}
	}
}
</style>
