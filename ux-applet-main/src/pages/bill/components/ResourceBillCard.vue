<template>
	<view class="content">
		<view class="content-title pb24 between">
			<view class="fwb fz-30 lh-42">
				{{ enumType("sourceType", currentItem.sourceType) }}
			</view>
			<MyStatus
				:text="enumType('orderStatusEnum', currentItem.payStatus)"
				:type="enumTag(currentItem.payStatus)"
				shape="circle"
				:plainFill="true"
				borderColor="transparent"
			/>
		</view>
		<view class="mt24 mb20 between">
			<view class="fz-26 lh-36">
				{{ enumType("billingTypeEnum", currentItem.chargeType) }}
			</view>
			<view class="fwb fz-26 lh-36">
				总费用：￥{{ currentItem.payPrice }}
			</view>
		</view>
		<view class="fz-24 tip lh-34">
			扣费时间：{{ currentItem.billDate }}
		</view>
	</view>
</template>

<script setup lang="ts">
import { enumType } from "@/utils/Enum";
defineProps<{ currentItem: TKeyValue }>();
type TagType = "info" | "success" | "warning" | "error";
const orderStatusTagEnum = {
	UNPAID: "warning",
	PAID: "success",
	CANCELED: "info",
	REFUNDED: "error",
};
const enumTag = (status: keyof typeof orderStatusTagEnum): TagType =>
	orderStatusTagEnum[status] as TagType;
</script>

<style lang="scss" scoped>
.content {
	padding: 30rpx 28rpx;
	background-color: #fff;
	border-radius: 16rpx;
	&-title {
		border-bottom: 2rpx solid #e5e5e5;
	}
	.tip {
		color: #83889d;
	}
}
</style>
