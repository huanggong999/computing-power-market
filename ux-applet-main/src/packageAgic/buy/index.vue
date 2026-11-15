<template>
	<NavCom title="确认付款" bgColor="#fff" />
	<view class="content">
		<view class="mb24">
			<MySubsection :type="chargeType" @changeType="changeType" />
		</view>
		<view class="card">
			<view class="item" v-if="chargeType == 'POSTPAID_BY_MONTH'">
				<view class="label">购买时长</view>
				<picker
					@change="bindPickerChange"
					:value="timeIndex"
					:range="timeOptions"
					:disabled="agicProduct.agic.editType == 'count'"
				>
					<view class="uni-input">{{
						timeArr[timeIndex].label
					}}</view>
				</picker>
			</view>
			<view class="item">
				<view class="label">账户数</view>
				<view class="count">
					<view
						class="reduce flx-center"
						@click="reduce('networkCount')"
						v-if="agicProduct.agic.editType != 'renew'"
						>-</view
					>
					<view class="num">{{ agicForm.networkCount }}</view>
					<view
						class="plus flx-center"
						@click="plus('networkCount')"
						v-if="agicProduct.agic.editType != 'renew'"
						>+</view
					>
				</view>
			</view>
			<view class="item" v-if="displayOrBind.isIpDisplay">
				<view class="label">IP地址数</view>
				<view class="value">
					<view class="count">
						<view
							class="reduce flx-center"
							@click="reduce('ipCount')"
							v-if="agicProduct.agic.editType != 'renew'"
							>-</view
						>
						<view class="num">{{ agicForm.ipCount }}</view>
						<view
							class="plus flx-center"
							@click="plus('ipCount')"
							v-if="agicProduct.agic.editType != 'renew'"
							>+</view
						>
					</view>
				</view>
			</view>
			<view class="item" v-if="displayOrBind.isBandwidthDisplay">
				<view class="label">带宽(M)</view>
				<view class="value">
					<view class="count">
						<view
							class="reduce flx-center"
							@click="reduce('bandwidth')"
							v-if="agicProduct.agic.editType != 'renew'"
							>-</view
						>
						<view class="num">{{ agicForm.bandwidth }}</view>
						<view
							class="plus flx-center"
							@click="plus('bandwidth')"
							v-if="agicProduct.agic.editType != 'renew'"
							>+</view
						>
					</view>
				</view>
			</view>
			<view class="item">
				<view class="label">客户名称</view>
				<input
					v-model="agicForm.agiCustomerName"
					style="text-align: right"
					type="text"
					class="value"
					:disabled="agicProduct.agic.productId"
					placeholder="请输入客户名称"
				/>
			</view>
			<view class="item">
				<view class="label">联系电话</view>
				<input
					v-model="agicForm.mobile"
					style="text-align: right"
					type="text"
					class="value"
					:disabled="agicProduct.agic.productId"
					placeholder="请输入联系电话"
				/>
			</view>
			<view class="item">
				<view class="label">联系邮箱</view>
				<view class="email-list">
					<view
						class="email-item"
						v-for="(item, index) in agicEmailForm"
						:key="index"
					>
						<input
							:disabled="index < agicProduct.agic.networkCount"
							style="text-align: right"
							v-model="agicEmailForm[index]"
							type="text"
							class="value"
							placeholder="请输入联系邮箱"
						/>
					</view>
					<view class="tips">邮箱默认为账户名</view>
				</view>
			</view>
			<view class="item">
				<view class="label">备注</view>
				<input
					v-model="agicForm.remark"
					style="text-align: right"
					type="text"
					class="value"
					placeholder="请输入备注"
				/>
			</view>
		</view>
		<view class="card order-price">
			<div class="inside">
				订单金额：
				<span class="price">{{
					total.payAmount ? `￥${total.payAmount}` : "--"
				}}</span>
			</div>
		</view>
		<view class="card order-container" v-if="!isEdit">
			<!-- 订单号 -->
			<view class="order-number"> 产品信息 </view>
			<!-- 套餐信息 -->
			<view class="saas-info">
				<image class="sass-img" :src="detail.image"></image>
				<view class="info">
					<view class="title overflow-1">{{ detail.name }}</view>
					<view class="price-group">
						<view class="price-item"
							><view class="label">实付价:</view>
							<view class="price">
								￥{{ detail.payPrice }}元/月
								<!-- <span class="line-price"
							>￥{{ detail.crossedPrice }}/天</span
						> -->
							</view>
						</view>
						<view class="price-item" v-if="detail.isIpCountDisplay">
							<view class="label">IP单价：</view>
							<view class="price">
								￥{{ detail.crossedPrice }}元/个
								<!-- <span class="line-price">￥{{ info.crossedPrice }}/天</span> -->
							</view>
						</view>
					</view>

					<view class="primary-price"
						>参考价：￥{{ detail.referPrice }}元/月</view
					>
				</view>
			</view>
		</view>

		<view class="footer">
			<button class="btn pay" @click="toPay">立即支付</button>
		</view>
	</view>
	<!-- 支付台 -->
	<up-popup
		v-model:show="dialog"
		mode="bottom"
		style="border-radius: 16rpx 0rpx"
	>
		<view class="dialog">
			<view class="dialog-header flx-center">支付台</view>
			<view class="price-info">
				<view class="total"
					><span class="yuan">￥</span>{{ total.payAmount }}</view
				>
				<!-- <view class="discount">优惠抵扣230元</view> -->
			</view>
			<view class="pay-way">
				<view
					class="way-item"
					v-for="item in payCardList"
					:key="item.flag"
				>
					<view class="label"
						>{{ item.title
						}}<span style="color: gray"
							>(可用：￥{{ item.count }})</span
						></view
					>
					<view class="option"
						>{{
							item.flag == "voucherPay"
								? voucherPayAmount
								: item.flag == "balancePay"
								? balancePayAmount
								: creditPayAmount
						}}<up-switch v-model="item.isSelected"></up-switch
					></view>
				</view>

				<view class="split"></view>
				<view class="final">还需要支付：￥{{ onlinePayAmount }}</view>
			</view>
			<view class="online-pay">
				<view class="online-item">
					<view class="label">
						<image
							class="content-rechargeType-item-icon mr8"
							:src="$ICON(`/mine/weChat_icon.png`)"
						/>微信支付
					</view>
					<image
						class="form-agreement-icon mr16"
						:src="$ICON(`/image/IsCheck.png`)"
					/>
				</view>
			</view>
			<view class="dialog-footer">
				<view
					class="form-agreement flex-1 fz-26 lh-36 x"
					@click="IsChecked = !IsChecked"
				>
					<image
						class="form-agreement-icon mr16"
						:src="
							$ICON(
								`/image/${IsChecked ? 'IsCheck' : 'check'}.png`
							)
						"
					/>
					我已阅读并同意
					<text @click.stop="open" class="form-agreement-tip">
						《AI智算云平台推广协议》
					</text>
				</view>
				<button class="pay" @click="createOrder">立即支付</button>
			</view>
		</view>
	</up-popup>
</template>

<script setup lang="ts">
import MySubsection from "./components/MySubsection.vue";
import { useAuth } from "@/store";
import { weChatPay } from "@/common/weChatPay";
import { myShowToast } from "@/utils";
import {
	buildAgicOrderInfoApi,
	checkEmailExistApi,
	checkIpNumApi,
	createAgicOrderApi,
	getNetWorkProductDetailAPI,
} from "@/api/model/networkProduct";
import { useAgicProduct } from "@/store/modules/agicProduct";
type TListType = "POSTPAID_BY_MONTH" | "POSTPAID_BY_HOUR";
const user = useAuth().USER_INFO;
const agicProduct = useAgicProduct();
// 产品信息 & 产品id
const id = ref();
const detail = ref<any>({});
// 是否为编辑
const isEdit = ref();
const orderPrice = ref();
// 订单参数
const agicForm = ref<any>({
	networkCount: agicProduct.agic.networkCount
		? Number(agicProduct.agic.networkCount)
		: 1,
	ipCount: agicProduct.agic.ipCount ? Number(agicProduct.agic.ipCount) : 1,
	bandwidth: agicProduct.agic.bandwidth
		? Number(agicProduct.agic.bandwidth)
		: 1,
	agiCustomerName: agicProduct.agic.agiCustomerName
		? agicProduct.agic.agiCustomerName
		: "",
	mobile: agicProduct.agic.mobile ? agicProduct.agic.mobile : "",
	remark: agicProduct.agic.remark ? agicProduct.agic.remark : "",
	isAutoRenew: agicProduct.agic.isAutoRenew
		? agicProduct.agic.isAutoRenew
		: 1,
});
// 订单邮箱
const agicEmailForm = ref<any>(
	agicForm.value.networkCount
		? new Array(agicForm.value.networkCount).fill("")
		: [""]
);
if (agicProduct.agic.productId) {
	agicEmailForm.value = agicProduct.agic.emails;
}

onLoad(async (options) => {
	id.value = options?.id;

	await getNetWorkProductDetailAPI(id.value).then((res: any) => {
		detail.value = res.data;
		agicProduct.setDisplayOrBind({
			isAccountIpBinding: detail.value.isAccountIpBinding,
			isIpDisplay: detail.value.isIpDisplay,
			isBandwidthDisplay: detail.value.isBandwidthDisplay,
		});
	});
	isEdit.value = options?.isEdit || agicProduct.agic.productId ? true : false;
});
const chargeType = ref<TListType>(
	agicProduct.agic.chargeType
		? agicProduct.agic.chargeType
		: "POSTPAID_BY_MONTH"
);
const changeType = (type: TListType) => {
	chargeType.value = type;
};
// 时间列表
const timeOptions = ref<string[]>([
	"1个月",
	"2个月",
	"3个月",
	"4个月",
	"5个月",
	"6个月",
	"7个月",
	"8个月",
	"9个月",
	"10个月",
	"11个月",
	"1年",
	"2年",
	"3年",
]);
const timeArr = ref<Array<{ label: string; value: any }>>([
	...Array.from({ length: 11 }, (_, i) => ({
		label: `${i + 1}个月`,
		value: { num: i + 1, unit: "MONTH" },
	})),
	{ label: "1年", value: { num: 1, unit: "YEAR" } },
	{ label: "2年", value: { num: 2, unit: "YEAR" } },
	{ label: "3年", value: { num: 3, unit: "YEAR" } },
]);
const timeIndex = ref(0);
const bindPickerChange = (e: any) => {
	timeIndex.value = e.detail.value;
	console.log("aaa", timeArr.value[timeIndex.value]);
};
// 获取三要素字段关系
const displayOrBind = computed(() => {
	return agicProduct.displayOrBind;
});
onMounted(() => {
	buildOrder();
});
watch(
	() => chargeType.value,
	(val) => {
		buildOrder();
	}
);
watch(
	() => [
		agicForm.value.networkCount,
		agicForm.value.ipCount,
		agicForm.value.bandwidth,
		agicForm.value.chargeType,
		timeIndex.value,
	],
	() => {
		if (agicProduct.agic.editType == "count") {
			// 如果差值都是0就不构建订单
			if (
				agicForm.value.networkCount - agicProduct.agic.networkCount! ===
					0 &&
				agicForm.value.ipCount - agicProduct.agic.ipCount! === 0 &&
				agicForm.value.bandwidth - agicProduct.agic.bandwidth === 0
			) {
				return;
			}
		}
		buildOrder();
	},
	{ immediate: true, deep: true }
);
watch(
	() => agicForm.value.networkCount,
	(newCount, oldCount) => {
		// 如果是续费订单则不允许修改
		const minCount = agicProduct.agic.productId
			? Number(agicProduct.agic.networkCount)
			: 1;
		const maxCount = 10;

		// 边界判断并修正 networkCount
		if (newCount < minCount) {
			agicForm.value.networkCount = minCount;
			return;
		}
		if (newCount > maxCount) {
			agicForm.value.networkCount = maxCount;
			return;
		}
		if (oldCount < minCount) {
			return;
		}
		const currentEmails = agicEmailForm.value;
		// 根据 newCount 增减 agicEmailForm.value 的长度
		if (newCount > oldCount) {
			agicEmailForm.value = [
				...currentEmails,
				...Array.from({ length: newCount - oldCount }, () => ""),
			];
		} else if (newCount < oldCount) {
			agicEmailForm.value = currentEmails.slice(0, newCount);
		}
		if (displayOrBind.value.isAccountIpBinding) {
			// 如果是账户IP绑定，则需要将IP数也同步到网络数
			agicForm.value.ipCount = newCount;
		}
	},
	{ immediate: true, deep: true }
);
// ip地址数必须大于1
watch(
	() => agicForm.value.ipCount,
	(newCount) => {
		const minCount = agicProduct.agic.productId
			? Number(agicProduct.agic.ipCount)
			: 1;
		if (newCount < minCount) {
			agicForm.value.ipCount = minCount;
		}
		if (displayOrBind.value.isAccountIpBinding) {
			// 如果是账户IP绑定，则需要将IP数也同步到网络数
			agicForm.value.networkCount = newCount;
		}
	}
);
// 带宽必须大于1
watch(
	() => agicForm.value.bandwidth,
	(newCount) => {
		const minCount = agicProduct.agic.productId
			? Number(agicProduct.agic.bandwidth)
			: 1;
		if (newCount < minCount) {
			agicForm.value.bandwidth = minCount;
		}
	}
);
// 操作数量
const plus = (key: string) => {
	agicForm.value[key]++;
};
const reduce = (key: string) => {
	// 小于1则不允许操作
	if (agicForm.value[key] <= 1) return;
	agicForm.value[key]--;
};
// 构建订单
const buildOrder = async () => {
	// 对表单信息进行有效性检查

	const data: any = {
		orderType: "PRODUCT",
		orderSource: [
			{
				sourceType: "CLOUD_NETWORK",
				chargeType: chargeType.value,
				duration:
					chargeType.value != "POSTPAID_BY_HOUR"
						? timeArr.value[timeIndex.value].value.num
						: null, // 如果不是包年包月，默认为null
				durationUnit:
					chargeType.value != "POSTPAID_BY_HOUR"
						? timeArr.value[timeIndex.value].value.unit
						: null, // 如果没有选择，默认为null
			},
		],
		networkProductId: id.value,
		networkCount: agicForm.value.networkCount,
		ipCount: agicForm.value.ipCount,
		bandwidth: agicForm.value.bandwidth,
		isAutoRenew: agicForm.value.isAutoRenew,
		agiCustomerName: agicForm.value.agiCustomerName,
		mobile: agicForm.value.mobile,
		email: agicEmailForm.value.join(","),
		remark: agicForm.value.remark,
	};
	if (data.orderSource[0].durationUnit === "YEAR") {
		data.orderSource[0].chargeType = "POSTPAID_BY_YEAR";
	}
	// 根据agicProduct.agic.type分辨订单类型(没有：PRODUCT,renew:RENEW_PRODUCT,count:UPGRADE_PRODUCT)
	if (agicProduct.agic.editType === "renew") {
		data.orderType = "RENEW_PRODUCT";
		data.orderNo = agicProduct.agic.orderNo;
		data.networkPayValueId = agicProduct.agic.id;
	} else if (agicProduct.agic.editType === "count") {
		data.orderType = "UPGRADE_PRODUCT";
		data.networkCount =
			agicForm.value.networkCount - agicProduct.agic.networkCount!;
		data.bandwidth = agicForm.value.bandwidth - agicProduct.agic.bandwidth;
		data.ipCount = agicForm.value.ipCount - agicProduct.agic.ipCount!;
		// 邮箱也取networkCount插值后面部分
		data.email = agicEmailForm.value
			.slice(agicProduct.agic.networkCount)
			.join(",");
		data.orderNo = agicProduct.agic.orderNo;
		data.networkPayValueId = agicProduct.agic.id;
	}
	await buildAgicOrderInfoApi(data).then((res: any) => {
		total.value.payAmount = res.data.finalPayAmount;
	});
};
// 主页显示订单金额
// 支付控制台
const dialog = ref(false);
const toPay = async () => {
	// 检查联系电话、联系邮箱是否均已填写
	// 正则检查手机号有效
	if (agicForm.value.mobile.trim() === "") {
		myShowToast("联系电话不能为空");
		return;
	}
	const reg = /^1[3456789]\d{9}$/;
	if (!reg.test(agicForm.value.mobile)) {
		myShowToast("联系电话格式不正确");
		return;
	}
	let valid = true;
	// 邮箱校验正则
	const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	agicEmailForm.value.forEach((item: string) => {
		if (!item || !emailRegex.test(item)) {
			valid = false;
			return;
		}
	});
	if (!valid) {
		uni.showToast({
			title: "请检查邮箱是否正确填写",
			icon: "none",
		});

		return;
	}
	// 检测重复邮箱
	const emailSet = new Set(agicEmailForm.value);
	if (emailSet.size !== agicEmailForm.value.length) {
		uni.showToast({
			title: "联系邮箱不能重复",
			icon: "none",
		});
		return;
	}
	// const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	// for (const email of agicEmailForm.value) {
	// 	if (email.trim() === "") {
	// 		myShowToast("联系邮箱不能为空");
	// 		return;
	// 	}
	// 	if (!emailReg.test(email)) {
	// 		myShowToast("邮箱格式不正确");
	// 		return;
	// 	}
	// }
	// 检测邮箱是否在飞连注册过
	let emails = agicEmailForm.value;
	// 如果agicProduct.agic.editType为count则只检查新增的邮箱
	if (agicProduct.agic.editType === "count") {
		emails = agicEmailForm.value.slice(agicProduct.agic.networkCount);
	}
	let isHave = false;
	if (!agicProduct.agic.productId || agicProduct.agic.editType != "renew") {
		await checkEmailExistApi({
			emails,
		}).then(async (res: any) => {
			if (res.data.length > 0) {
				myShowToast(`${res.data}邮箱已存在`);
				isHave = true;
			}
		});
	}
	if (isHave) return;
	await buildOrder();
	dialog.value = true;
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
onUnload(() => {
	agicProduct.resetAgicDetail();
	console.log("1111", agicProduct.agic);
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
		orderSource: [
			{
				sourceType: "CLOUD_NETWORK",
				chargeType: chargeType.value,
				duration:
					chargeType.value != "POSTPAID_BY_HOUR"
						? timeArr.value[timeIndex.value].value.num
						: null, // 如果不是包年包月，默认为null
				durationUnit:
					chargeType.value != "POSTPAID_BY_HOUR"
						? timeArr.value[timeIndex.value].value.unit
						: null, // 如果没有选择，默认为nulls
			},
		],
		networkProductId: id.value,
		networkCount: agicForm.value.networkCount,
		ipCount: agicForm.value.ipCount,
		bandwidth: agicForm.value.bandwidth,
		isAutoRenew: agicForm.value.isAutoRenew,
		agiCustomerName: agicForm.value.agiCustomerName,
		mobile: agicForm.value.mobile,
		email: agicEmailForm.value.join(","),
		remark: agicForm.value.remark,
		isApplet: true,
	};
	if (onlinePayAmount.value && !data.onlinePayType) {
		uni.showToast({
			title: "请选择支付方式",
			icon: "none",
		});

		return;
	}
	// 检查手机号是否填写
	if (!agicForm.value.mobile) {
		uni.showToast({
			title: "请填写联系电话",
			icon: "none",
		});

		return;
	}
	// 邮箱合法校验（不能重复，不能为空，需要符合邮箱规则）
	let valid = true;
	// 邮箱校验正则
	const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	agicEmailForm.value.forEach((item: string) => {
		if (!item || !emailRegex.test(item)) {
			valid = false;
			return;
		}
	});
	if (!valid) {
		uni.showToast({
			title: "请检查邮箱是否正确填写",
			icon: "none",
		});

		return;
	}
	// 检测重复邮箱
	const emailSet = new Set(agicEmailForm.value);
	if (emailSet.size !== agicEmailForm.value.length) {
		uni.showToast({
			title: "联系邮箱不能重复",
			icon: "none",
		});
		return;
	}
	// 根据agicProduct.agic.type分辨订单类型(没有：PRODUCT,renew:RENEW_PRODUCT,count:UPGRADE_PRODUCT)
	if (agicProduct.agic.editType === "renew") {
		data.orderType = "RENEW_PRODUCT";
		data.orderNo = agicProduct.agic.orderNo;
		data.networkPayValueId = agicProduct.agic.id;
	} else if (agicProduct.agic.editType === "count") {
		data.orderType = "UPGRADE_PRODUCT";
		data.networkCount =
			agicForm.value.networkCount - agicProduct.agic.networkCount!;
		data.bandwidth = agicForm.value.bandwidth - agicProduct.agic.bandwidth;
		data.ipCount = agicForm.value.ipCount - agicProduct.agic.ipCount!;
		// 邮箱也取networkCount插值后面部分
		data.email = agicEmailForm.value
			.slice(agicProduct.agic.networkCount)
			.join(",");
		data.orderNo = agicProduct.agic.orderNo;
		data.networkPayValueId = agicProduct.agic.id;
	}
	// 检测邮箱是否在飞连注册过
	const emails = data.email.split(",");
	let isHave = false;
	if (!agicProduct.agic.editType || agicProduct.agic.editType != "renew") {
		await checkEmailExistApi({
			emails,
		}).then(async (res: any) => {
			if (res.data.length > 0) {
				myShowToast(`${res.data}邮箱已存在`);
				isHave = true;
			}
		});
	}
	if (isHave) return;

	// 检测ip节点数目是否足够
	const ipData = {
		productId: id.value,
		ipNumber: agicForm.value.ipCount,
	};
	if (agicProduct.agic.productId) {
		ipData.ipNumber -= agicProduct.agic.ipCount!;
	}
	await checkIpNumApi(ipData).then((res: any) => {
		if (res.data == false) {
			myShowToast("资源已用完，请耐心等待，后台尽快为您补充资源。");
			isHave = true;
			return;
		}
	});
	if (isHave) {
		return;
	}
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

	await createAgicOrderApi(data).then(async (res: any) => {
		if (res.code === 200) {
			if (!res.data.onlinePayParam) {
				uni.showToast({
					title: "已完成订单购买",
					icon: "success",
					duration: 2000,
				});
				uni.$u.sleep(2000).then(() => {
					agicProduct.resetAgicDetail();
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
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding-left: 24rpx;
	padding-right: 24rpx;
	display: flex;
	flex-direction: column;
	gap: 10rpx;
	min-height: 88vh;
	padding-bottom: 180rpx;
	// 卡片
	.card {
		width: 100%;
		background: #ffffff;
		border-radius: 12px 12px 12px 12px;

		.item {
			width: 646rpx;
			min-height: 104rpx;
			margin: 0 28rpx;
			border-bottom: 1rpx solid #e5e5e5;
			display: flex;
			align-items: center;
			justify-content: space-between;

			.label {
				font-weight: 400;
				font-size: 28rpx;
				color: #0f0f2b;
			}

			.value {
			}

			.count {
				display: flex;
				align-items: center;
				gap: 24rpx;

				.reduce {
					width: 40rpx;
					height: 40rpx;
					background: #f3f4f6;
					border-radius: 100%;
					font-size: 40rpx;
					color: #83889d;
					display: flex;
					justify-content: center;
					align-items: center;
				}

				.num {
					font-weight: 400;
					font-size: 28rpx;
					color: #000000;
				}

				.plus {
					width: 40rpx;
					height: 40rpx;
					font-size: 40rpx;
					background: #3972fd;
					border-radius: 100%;
					color: #ffffff;
					display: flex;
					justify-content: center;
					align-items: center;
				}
			}

			.email-list {
				display: flex;
				flex-direction: column;
				gap: 30rpx;
				padding: 20rpx 0;

				.tips {
					font-weight: 400;
					font-size: 24rpx;
					color: #fba201;
					text-align: right;
				}
			}
		}

		// 最后一个item没有border
		.item:last-child {
			border: none;
		}
	}
	.order-price {
		padding: 24rpx;
		width: 88vw;
		display: flex;
		align-items: center;
		.inside {
			margin-left: auto;
			.price {
				color: #ff4151;
				font-weight: bold;
			}
		}
	}
	.order-container {
		padding: 24rpx;
		width: 88vw;
		background: #ffffff;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
		display: flex;
		flex-direction: column;
		gap: 24rpx;
		margin-bottom: 160rpx;

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
				.price-group {
					display: flex;
					align-items: center;
					gap: 20rpx;
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
}

/* 居中弹窗 */
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

// 固定底部
.footer {
	position: fixed;
	margin-left: -20rpx;
	bottom: 0;
	width: 750rpx;
	height: 100rpx;
	padding-bottom: 40rpx;
	background: #ffffff;
	border-radius: 6rpx 0rpx 6rpx 0rpx;
	display: flex;
	justify-content: flex-end;
	align-items: center;
	z-index: 200;
	.btn {
		width: 164rpx;
		height: 60rpx;
		border-radius: 34rpx 34rpx 34rpx 34rpx;
		font-weight: 400;
		font-size: 26rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-left: 580rpx;
	}

	.pay {
		border: 1rpx solid #3972fd;
		background: #3972fd;
		color: #fff;
	}
}
</style>
