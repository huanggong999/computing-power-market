<template>
	<NavCom title="订单管理" bgColor="#fff" />
	<view class="content">
		<view class="mt30 list">
			<scroll-view refresher-background="transparent" style="height: 100%" scroll-with-animation scroll-y
				@scrolltolower="reachBottom" refresher-enabled @refresherrefresh="refreshData"
				:refresher-triggered="triggered">
				<view class="mb20" v-for="(item, index) in dataList" :key="index">
					<OrderOverviewCard :info="item" @refresh="refreshData" />
				</view>
				<MyEmpty text="暂无内容" icon="/image/bill_empty.png" :loadText="loadText"
					:IsEmpty="dataList.length !== 0" />
				<view class="empty-btn fz-28 lh-40 flex" v-if="!isLogin" @click="toLogin">
					登录查看更多
					<image class="icon" :src="$ICON('/image/login_right.png')" />
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { usePageList } from "@/hook/pageList";
import { getAIGCFormListApi } from "@/api/model/networkProduct";
import { useAuth } from "@/store";
import OrderOverviewCard from "./components/OrderOverviewCard.vue";
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
} = usePageList({ api: getAIGCFormListApi, requestAuto: false });
const isLogin = computed(() => !!Auth.TOKEN);

const toLogin = () => {
	uni.navigateTo({ url: "/pages/login/index" });
};

// onMounted(() => {
// 	if (isLogin.value) {
// 		getList();
// 	}
// });
onShow(() => {
	if (isLogin.value) {
		refreshData();
	}
})
</script>

<style lang="scss" scoped>
.content {
	padding: 0rpx 24rpx;
	padding-top: 187rpx;
	position: relative;
}
</style>
