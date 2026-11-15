<template>
	<view class="content">
		<MyTitle title="新闻动态" @handleMore="handleMore" />
		<view class="card mt26">
			<view
				class="card-item between mb20"
				v-for="(item, index) in list"
				:key="index"
				@click="handlerClick(item.id)"
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
		</view>
	</view>
</template>

<script setup lang="ts">
import MyTitle from "./MyTitle.vue";

defineProps<{ list: TKeyValue[] }>();

const handleMore = () =>
	uni.navigateTo({ url: "/packageHome/newsUpdates/index" });
const handlerClick = (id: string) =>
	uni.navigateTo({ url: `/packageHome/newsUpdates/details?id=${id}` });
</script>

<style lang="scss" scoped>
.content {
	.card {
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
