<template>
	<view class="content flex fz-26 lh-36">
		<image class="content-avatar" :src="item.avatar" />
		<view class="ml16 content-info">
			<view class="between">
				<view> {{ item.userName }} </view>
				<MyStatus
					:text="currentList[item.type - 1].label"
					plainFill
					borderColor="transparent"
					:type="currentList[item.type - 1].tagType"
				/>
			</view>
			<view class="between mt4">
				<view class="tip"> {{ item.createTime }} </view>
				<view class="tip">
					贡献收益：<text class="num fwb"
						>￥{{ item.totalCommission }}</text
					>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{ item: TKeyValue }>();
type TagType = "info" | "success" | "warning" | "error" | "primary";
interface ICurrentList {
	label: string;
	value: number;
	tagType: TagType;
}

const currentList = reactive<ICurrentList[]>([
	{ label: "一级", value: 1, tagType: "primary" },
	{ label: "二级", value: 2, tagType: "warning" },
]);
</script>

<style lang="scss" scoped>
.content {
	padding: 28rpx 24rpx;
	background: #ffffff;
	border-radius: 16rpx;
	&-avatar {
		width: 76rpx;
		height: 76rpx;
		border-radius: 50%;
	}
	&-info {
		flex: 1;
		.tip {
			color: #7f7f7f;
		}
		.num {
			color: #3972fd;
		}
	}
}
</style>
