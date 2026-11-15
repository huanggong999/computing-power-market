<template>
	<NavCom title="新闻动态" />
	<view class="content">
		<view class="bg" />
		<view class="content-list">
			<view
				class="content-list-item activeList between mb20"
				v-for="(item, index) in dataList"
				:key="index"
				@click.stop="handlerClick(item.id)"
			>
				<view>
					<view class="lh-42 fz-30"> {{ item.name }} </view>
					<view class="tip mt64 fz-24 lh-34">
						{{ item.createTime }}</view
					>
				</view>
				<view>
					<up-image
						width="188rpx"
						height="140rpx"
						:src="item.imageTwo"
						bgColor="transparent"
						radius="16rpx"
					/>
				</view>
			</view>

			<MyEmpty
				icon="/mine/order_empty.png"
				text="暂无活动"
				:IsEmpty="dataList.length !== 0"
				:loadText="loadText"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
import { getNewsListApi } from "@/api/model/news";
import { usePageList } from "@/hook/pageList";
import onShare from "@/hook/useShare";
const { onShareAppMessage, onShareTimeline } = onShare(
	"/packageHome/newsUpdates/index"
);
const { dataList, loadText, pullToRefresh } = usePageList({
	api: getNewsListApi,
});
pullToRefresh();

const handlerClick = (id: string) => {
	uni.navigateTo({ url: `/packageHome/newsUpdates/details?id=${id}` });
};
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
		height: 268rpx;
		display: block;
		z-index: 1;
		background: linear-gradient(180deg, #d7e2f6 0%, #f3f4f6 100%);
	}
	&-list {
		position: relative;
		z-index: 2;
		padding: 24rpx;
		&-item {
			padding: 28rpx;
			background-color: #fff;
			border-radius: 16rpx;
			.tip {
				color: #83889d;
			}
		}
	}
}
</style>
