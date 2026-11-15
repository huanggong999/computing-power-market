<template>
	<view class="content">
		<view class="fwb mb16 fz-26 lh-36"> 充值金额 </view>
		<up-input
			type="digit"
			placeholder="请输入充值金额"
			v-model="amount"
			border="none"
			:customStyle="customStyle"
		>
			<template #prefix><view> ￥ </view> </template>
		</up-input>
		<view class="fwb mt40 mb20 fz-26 lh-36">充值方式</view>

		<view class="content-rechargeType between">
			<view
				class="content-rechargeType-item flex fz-24"
				:style="{
					width: `calc((100% - ${
						(rechargeType.length - 1) * 12
					}rpx) / ${rechargeType.length})`,
				}"
				:class="{ active: onlinePayType === item.type }"
				v-for="item in rechargeType"
				:key="item.name"
				@click="handleRechargeType(item.type)"
			>
				<image
					class="content-rechargeType-item-icon mr8"
					:src="$ICON(`/mine/${item.icon}.png`)"
				/>
				{{ item.name }}
			</view>
		</view>

		<view
			class="content-business mt24"
			v-if="onlinePayType === 'REMIT_PAY'"
		>
			<view
				class="content-business-item fz-28 lh-40 mb20"
				v-for="(item, index) in remitAccountList"
				:key="index"
				:class="{ active: index === currentBankType }"
				@click="handleRemitAccount(index)"
			>
				<view class="content-business-item-title mb24 flex">
					{{ item.swiftCode ? "境外" : "境内" }}
				</view>
				<view
					class="ml24"
					v-for="(el, i) in remitAccountItemList"
					:key="i"
				>
					<view class="flex-1 mt24" v-if="el.show(!!item.swiftCode)">
						<view class="content-business-item-tip">
							{{ el.label }}
						</view>
						<view :class="[item.swiftCode ? 'ml28' : 'ml44']">
							{{ el.value(item) }}
						</view>
					</view>
				</view>
			</view>
			<view class="content-business-remittanceInfo mt30">
				<image
					class="content-business-remittanceInfo-img"
					:src="$ICON('/mine/business_tip.png')"
				/>
				<view class="ml24">
					<view class="fz-28 lh-40">
						{{ remittanceInfoList[0].title }}
					</view>
					<view
						class="content-business-remittanceInfo-tip pl22 fz-24 lh-34 mt20"
						v-for="(item, index) in remittanceInfoList[0].tip"
						:key="index"
					>
						{{ item }}
					</view>
					<view class="mt34 mb20">
						<up-input
							placeholder="请输入汇款主体"
							v-model="remitName"
							border="none"
							:customStyle="customStyle"
						/>
					</view>
					<view class="fz-28 lh-40">
						{{ remittanceInfoList[1].title }}
					</view>
					<view
						class="content-business-remittanceInfo-tip pl22 fz-24 lh-34 mt20"
						v-for="(item, index) in remittanceInfoList[1].tip"
						:key="index"
					>
						{{ item }}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { getRemitAccountApi } from "@/api/model/mine";

const customStyle = {
	background: "#F7F8FB",
	borderRadius: "16rpx",
	padding: "30rpx",
};

const rechargeType = [
	{ name: "微信支付", icon: "weChat_icon", type: "WECHAT_PAY" },
	{ name: "对公转账", icon: "business_icon", type: "REMIT_PAY" },
];
const onlinePayType = ref("WECHAT_PAY");

const remitAccountList = ref<TKeyValue>([]);
const getList = async () => {
	const { data } = await getRemitAccountApi();
	remitAccountList.value = data;
};
// 充值金额
const amount = ref();
// 汇款户名
const remitName = ref("");
// 汇款账户信息
const remitAccountItemList = reactive([
	{
		label: "开户名称",
		value: (val: TKeyValue) => val.account,
		show: () => true,
	},
	{
		label: "开户银行",
		value: (val: TKeyValue) => val.bankName,
		show: () => true,
	},
	{
		label: "银行账户",
		value: (val: TKeyValue) => val.bankNo,
		show: () => true,
	},
	{
		label: "SWIFT CODE",
		value: (val: TKeyValue) => val.swiftCode,
		show: (type: boolean) => type,
	},
]);
// 汇款信息
const remittanceInfoList = reactive([
	{
		title: "确定汇款户名",
		tip: [
			"请选择以上【境内】或【境外】两种对公账户进行汇款",
			"若汇款户名为非本实名主体，为保障资金安全与合规，请先添加汇款户名再向以上账户进行汇款",
		],
	},
	{
		title: "汇款到账",
		tip: [
			"使用与本账号实名信息一致的账号进行汇款，汇款至对公账号，最快1小时左右到账",
			"向对公账号汇款到账后，无需操作，系统将自动认领至您的逸云数智账户",
			"向对公账号汇款，需要您至汇款认领页面发起人工认领，认领充值到您逸云数智后，系统将通过短信通知您充值结果",
		],
	},
]);
const currentBankType = ref("china");
// 汇款账户类型
const bankType = computed(() => (currentBankType.value === "china" ? 1 : 2));

const handleRechargeType = (val: string) => (onlinePayType.value = val);
const handleRemitAccount = (val: string) => (currentBankType.value = val);

const currentBankItem = computed(
	() => remitAccountList.value[currentBankType.value]
);

const remitItem = computed(() => ({
	account: currentBankItem.value.account,
	amount: amount.value,
	bankName: currentBankItem.value.bankName,
	bankType: bankType.value,
	bankNo: currentBankItem.value.bankNo,
	remitName: remitName.value,
	swiftCode:
		currentBankType.value === "china"
			? ""
			: currentBankItem.value.swiftCode,
}));

onMounted(() => getList());
defineExpose({ amount, onlinePayType, remitItem });
</script>

<style lang="scss" scoped>
.content {
	padding: 30rpx 24rpx;
	border-radius: 16rpx;
	background: #ffffff;
	&-rechargeType {
		&-item {
			padding: 34rpx 0;
			border-radius: 16rpx;
			border: 2rpx solid #e5e5e5;
			&-icon {
				width: 46rpx;
				height: 46rpx;
			}
		}
	}
	.active {
		border: 4rpx solid #3972fd;
	}
	&-business {
		&-item {
			padding: 24rpx 0 30rpx;
			border-radius: 16rpx;
			background: #f7f8fb;
			&-title {
				width: 96rpx;
				padding: 8rpx 0;
				background-color: #fff;
				position: relative;
				&::after {
					content: "";
					width: 0;
					position: absolute;
					border-bottom: 25rpx solid transparent;
					border-left: 25rpx solid #fff;
					border-right: 25rpx solid transparent;
					border-top: 25rpx solid transparent;
					right: -48rpx;
				}
			}
			&-tip {
				color: #83889d;
			}
		}
		&-remittanceInfo {
			display: flex;
			&-img {
				flex-shrink: 0;
				width: 52rpx;
				height: 426rpx;
			}
			&-tip {
				color: #83889d;
				position: relative;
				&::after {
					content: "";
					width: 10rpx;
					height: 10rpx;
					border-radius: 50%;
					position: absolute;
					background: #83889d;
					top: 12rpx;
					left: 0;
				}
			}
		}
	}
}
</style>
