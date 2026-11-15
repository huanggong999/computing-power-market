<template>
	<view class="content">
		<view class="fwb title fz-34 lh-48"> 账单总览 </view>

		<view class="mt30 list">
			<scroll-view
				refresher-background="transparent"
				style="height: 100%"
				scroll-with-animation
				scroll-y
				@scrolltolower="reachBottom"
				refresher-enabled
				@refresherrefresh="refreshData"
				:refresher-triggered="triggered"
			>
				<view
					class="mb20"
					v-for="(item, index) in dataList"
					:key="index"
				>
					<BillOverviewCard :current-item="item" />
				</view>
				<MyEmpty
					text="暂无内容"
					icon="/image/bill_empty.png"
					:loadText="loadText"
					:IsEmpty="dataList.length !== 0"
				/>
				<view
					class="empty-btn fz-28 lh-40 flex"
					v-if="!isLogin"
					@click="toLogin"
				>
					登录查看更多
					<image
						class="icon"
						:src="$ICON('/image/login_right.png')"
					/>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { usePageList } from "@/hook/pageList";
import BillOverviewCard from "./BillOverviewCard.vue";
import { billOverviewPage } from "@/api/model/bill";
import { useAuth } from "@/store";

const props = defineProps<{ month: string }>();

const Auth = useAuth();

const {
	dataList,
	getList,
	refreshData,
	reachBottom,
	loadText,
	triggered,
	searchParam,
	searchFn,
} = usePageList({ api: billOverviewPage, requestAuto: false });

watch(
	() => props.month,
	(val) => {
		searchParam.value.month = val;
		searchFn();
	}
);

defineExpose({ searchFn });
const isLogin = computed(() => !!Auth.TOKEN);

const toLogin = () => {
	uni.navigateTo({ url: "/pages/login/index" });
};

onMounted(() => {
	if (isLogin.value) {
		getList();
	}
});
</script>

<style lang="scss" scoped>
.content {
	height: 1052rpx;
	border-radius: 16rpx;
	background-color: #fff;
	padding: 30rpx 28rpx;
	box-sizing: border-box;
	.list {
		height: calc(100% - 50rpx);
	}
	.empty-btn {
		color: #fff;
		background: #3972fd;
		width: 298rpx;
		height: 80rpx;
		border-radius: 44rpx;
		margin: 30rpx auto;
		.icon {
			width: 40rpx;
			height: 40rpx;
		}
	}
}
</style>
