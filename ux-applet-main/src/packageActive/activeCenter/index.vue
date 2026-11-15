<template>
	<NavCom title="活动中心" />
	<view class="content">
		<view class="bg" />

		<view class="content-list">
			<view
				class="content-list-item mb20"
				v-for="(item, index) in activeList"
				:key="index"
				@click.stop="handlerClick(item.id)"
			>
				<up-image
					:src="item.picture"
					width="702rpx"
					height="356rpx"
					bgColor="transparent"
				/>
				<view class="content-list-item-info between">
				  	<view class="fwb fz-30 mle lh-42"> {{ item.name }} </view>
					<button
						class="content-list-item-info-share ml20 fz-24 lh-34"
						@click.stop="share(item)"
						open-type="share"
					>
						分享
					</button>
				</view>
			</view>

			<MyEmpty
				icon="/mine/order_empty.png"
				text="暂无活动"
				:IsEmpty="activeList.length !== 0"
				loadText="没有更多了"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
import { getActiveDetailsListApi } from "@/api/model/activity";
import onShare from "@/hook/useShare";
import { myShowToast } from "@/utils";
const {
	onShareAppMessage,
	onShareTimeline,
	currentImage,
	currentId,
	currentPath,
	currentTitle,
} = onShare();
const activeList = ref<TKeyValue[]>([]);

const getActive = async () => {
	uni.showLoading({ title: "加载中", mask: true });
	const { data } = await getActiveDetailsListApi();
	activeList.value = data;
	uni.hideLoading();
};
const handlerClick = (id: string) => {
	uni.navigateTo({ url: `/packageActive/activeCenter/details?id=${id}` });
};
const share = (row: TKeyValue) => {
	currentImage.value = row.picture;
	currentId.value = row.id;
	currentTitle.value = row.name;
	currentPath.value = `/packageActive/activeCenter/details`;
};
onHide(() => {
	currentImage.value = "";
	currentId.value = "";
	currentPath.value = "/packageActive/activeCenter/index";
	currentTitle.value = "";
});
onShow(() => getActive());
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
		height: 192rpx;
		display: block;
		z-index: 1;
		background: linear-gradient(180deg, #d7e2f6 0%, #f3f4f6 100%);
	}
	&-list {
		padding: 24rpx;
		position: relative;
		z-index: 2;

		&-item {
			border-radius: 16rpx;
			background-color: #fff;
			overflow: hidden;
			&-info {
				align-items: baseline;
				padding: 16rpx 20rpx;
				&-share {
					color: #3972fd;
					border-radius: 6rpx;
					background: #d7e2f6;
					flex-shrink: 0;
					padding: 0 8rpx;
					margin: 0;
				}
			}
		}
	}
}
</style>
