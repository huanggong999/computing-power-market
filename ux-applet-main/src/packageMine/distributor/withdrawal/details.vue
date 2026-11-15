<template>
	<NavCom title="提现明细" bgColor="#fff" />
	<view class="content">
		<view
			class="content-item mb20"
			v-for="(item, index) in dataList"
			:key="index"
		>
			<view class="between">
				<view class="fz-30 lh-42 fwb">提现</view>
				<view class="content-item-price fz-30 lh-42">
					￥{{ item.withdrawalAmount }}
				</view>
			</view>
			<view class="between mt8">
				<view class="content-item-time fz-24 lh-34">
					{{ item.createTime }}
				</view>
				<view
					class="fz-26 lh-36"
					:style="{ color: statusEnum[item.status as TStatus].color }"
				>
					{{ statusEnum[item.status as TStatus].text }}
				</view>
			</view>
		</view>
		<MyEmpty
			text="暂无明细"
			icon="/mine/withdrawal_empty.png"
			:loadText="loadText"
			:IsEmpty="dataList.length !== 0"
		/>
	</view>
</template>

<script setup lang="ts">
import { extendWithPageApi } from "@/api/model/mine";
import { usePageList } from "@/hook/pageList";
type TStatus = 1 | 2 | 3 | 4;
const statusEnum = {
	1: { text: "审核中", color: "#999999" },
	2: { text: "通过审核", color: "#f9ae3e" },
	3: { text: "提现失败", color: "#FF4151" },
	4: { text: "已打款", color: "#3BA46F" },
};

const { dataList, loadText } = usePageList({ api: extendWithPageApi });
</script>

<style lang="scss" scoped>
.content {
	padding: 206rpx 24rpx;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	&-item {
		padding: 30rpx 24rpx;
		background-color: #fff;
		border-radius: 16rpx;
		&-price {
			color: #3972fd;
		}
		&-time {
			color: #999999;
		}
	}
}
</style>
