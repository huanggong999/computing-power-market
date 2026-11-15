<template>
	<view class="content">
		<image class="bg" :src="$ICON('/mine/bg.png')" />
		<MyNavTitle title="个人中心" color="#ffffff" />
		<view class="pd">
			<HeadCom />
		</view>
		<view class="content-account mt20 pd">
			<view class="pt30">
				<Account :info="UserInfo" />
			</view>
		</view>
		<view class="mt20 pd">
			<AssetDetails :info="UserInfo" />
		</view>
		<view class="content-service pd mt20">
			<view
				v-for="item in serviceList"
				:key="item.label"
				class="mb20"
				@click="toPage(item.label, item.url)"
			>
				<ServiceCom :label="item.label" :icon="item.icon" />
			</view>
		</view>
	</view>
	<MyTabbar :placeholder="false" />
</template>

<script lang="ts" setup>
import HeadCom from "./components/HeadCom.vue";
import Account from "./components/Account.vue";
import ServiceCom from "./components/ServiceCom.vue";
import { useAuth } from "@/store";
import AssetDetails from "./components/AssetDetails.vue";
import { getExtendDetailApi } from "@/api/model/mine";
import onShare from "@/hook/useShare";
const { onShareAppMessage, onShareTimeline } = onShare("/pages/mine/index");
const Auth = useAuth();
const UserInfo = computed(() => Auth.USER_INFO || {});

const serviceList = reactive([
	{
		label: "逸起赚",
		icon: "distributionCenter_icon",
		url: "/distributor/apply/openDistributor",
	},
	{
		label: "收支明细",
		icon: "transactionHistory_icon",
		url: "/transactionHistory/index",
	},
	{
		label: "订单管理",
		icon: "OrderCenter_icon",
		url: "/orderCenter/index",
	},
]);

const toPage = async (label: string, url: string) => {
	if (!Auth.checkLogin()) return;
	let pageUrl = url;
	if (label === "逸起赚") {
		const { data } = await getExtendDetailApi();
		const statusEnum: Record<number, string> = {
			1: "/distributor/apply/reviewStatus?status=1", // 申请中
			2: "/distributor/distributionCenter/index", // 申请成功
			3: `/distributor/apply/reviewStatus?status=3&tip=${data.verifyRemark}`, // 申请失败
		};
		if (data.status in statusEnum)
			pageUrl = statusEnum[data.status as keyof typeof statusEnum];
	}
	uni.navigateTo({ url: "/packageMine" + pageUrl });
};
</script>
<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding-bottom: 24rpx;

	.bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 480rpx;
		display: block;
		z-index: 1;
	}

	.pd {
		padding: 0 24rpx;
	}

	&-account {
		position: relative;
		z-index: 2;
		border-radius: 24rpx 24rpx 0rpx 0rpx;
		background-color: #f3f4f6;
	}
}
</style>
