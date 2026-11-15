<template>
	<view class="content">
		<view class="between lh-44">
			<view class="fz-28">
				{{ enumType("tradeTypeEnum", row.transactionType) }}
			</view>
			<view
				class="fz-32 fwb"
				:class="{ expenses: !isInCome(row.transactionType) }"
			>
				{{ isInCome(row.transactionType) ? "+" : "-"
				}}{{ row.changeAmount }}
			</view>
		</view>
		<view class="between tip fz-24 mt12 lh-34">
			<view>{{ row.createTime }}</view>
			<view>可用余额：{{ row.availableBalance }}元</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { enumType } from "@/utils/Enum";

defineProps<{ row: TKeyValue }>();

const incomeTypes = ["RECHARGE", "PLATFORM_GRANT", "REFUND"];

const isInCome = (transactionType: string) =>
	incomeTypes.includes(transactionType);
</script>

<style lang="scss" scoped>
.content {
	padding: 32rpx 24rpx 30rpx;
	border-radius: 16rpx;
	background-color: #fff;
	.tip {
		color: #83889d;
	}
	.expenses {
		color: #ff4151;
	}
}
</style>
