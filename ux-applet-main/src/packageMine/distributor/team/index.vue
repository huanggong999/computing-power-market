<template>
	<NavCom title="我的团队" bgColor="#ffffff" />
	<view class="content">
		<view class="content-top fz-28 lh-40 between">
			<view> 团队总人数：{{ total }}人 </view>
			<view class="flex">
				<view
					@click="handelCurrent(item.value)"
					class="tag ml20"
					:class="{ activeTag: currentType === item.value }"
					v-for="(item, index) in currentList"
					:key="index"
				>
					{{ item.label }}
				</view>
			</view>
		</view>

		<view class="content-list">
			<view v-for="(item, index) in dataList" :key="index" class="mt20">
				<TeamCard :item="item" />
			</view>
			<MyEmpty
				text="暂无团队"
				icon="/mine/team_empty.png"
				:loadText="loadText"
				:IsEmpty="dataList.length !== 0"
			/>
		</view>
	</view>
</template>

<script setup lang="ts">
import { getExtendFirstPageApi } from "@/api/model/mine";
import { usePageList } from "@/hook/pageList";
import TeamCard from "./components/TeamCard.vue";

const currentType = ref(0);
const currentList = reactive([
	{ label: "一级", value: 1, tagType: "primary" },
	{ label: "二级", value: 2, tagType: "warning" },
]);
const { dataList, total, getList, loadText, searchParam, searchFn } =
	usePageList({
		api: getExtendFirstPageApi,
		requestAuto: false,
	});

const handelCurrent = (type: number) => {
	currentType.value = type;
	searchParam.value.type = type;
	searchFn();
};

onLoad(() => getList());
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding: 212rpx 24rpx;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	&-top {
		.tag {
			padding: 4rpx 16rpx;
			border-radius: 8rpx;
			background-color: #fff;
			color: #83889d;
		}

		.activeTag {
			background: #fff;
			color: #3972fd;
			background: #d7e2f6;
		}
	}
	&-list {
		&-item {
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
	}
}
</style>
