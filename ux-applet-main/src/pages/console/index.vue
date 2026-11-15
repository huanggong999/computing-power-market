<template>
	<view class="content">
		<MyNavTitle title="控制台" />
		<image class="bg" :src="$ICON('/image/console_bg.png')" />
		<view class="pd">
			<up-swiper
				:list="activeList"
				keyName="picture"
				circular
				radius="24rpx"
				height="180rpx"
				@click="handleSwiperClick"
			/>
			<!-- imgMode="heightFix" -->
		</view>
		<view
			class="pd"
			v-for="item in filterCards"
			:key="item.type"
			@click="handleCardClick(item.type)"
		>
			<ConsoleCard :currentItem="item" />
		</view>

		<image
			class="no-data"
			v-if="!filterCards.length"
			:src="$ICON('/image/noconsole.png')"
		/>
	</view>

	<MyTabbar />
</template>

<script lang="ts" setup>
import ConsoleCard from "./components/ConsoleCard.vue";
import api from "@/api";
import { useAuth } from "@/store";
import { getActiveDetailsListApi } from "@/api/model/activity";
import type { ConsoleCardItem } from "./type";
import onShare from "@/hook/useShare";
const { onShareAppMessage, onShareTimeline } = onShare("/pages/console/index");

const Auth = useAuth();
const handleCardClick = (type: string) => {
	if (type != "IsStorage")
		uni.navigateTo({ url: `/packageConsole/list/index?type=${type}` });
};

const dataForm = ref<TKeyValue>({
	instanceCount: "--", //服务器总数
	ecsRunningNumber: "--", // 服务器运行中数量
	ecsStoppedNumber: "--", // 服务器已停止数量
	expiringNumber: "--", // 服务器即将到期数量
	expireNumber: "--", // 服务器已到期数量
	//
	containerTotal: "--", // 集群总数
	containerNormalNumber: "--", // 集群正常数量
	containerAbnormalNumber: "--", // 集群异常数量
	containerOtherNumber: "--", // 集群其他数量
	//
	imageCount: "--", // 镜像仓库总数
	imageRunningNumber: "--", // 镜像仓库运行中数量
	imageStoppedNumber: "--", // 镜像仓库已停止数量
	imageErrorNumber: "--", // 镜像仓库异常数量
	//
	bucketCount: "--", // 对象存储桶数量
	bucketObjectCount: "--", // 桶对象数量
	bucketObjectSize: "--", // 桶对象总容量
	//
	networkProductCount: "--", // 网络产品数量
	networkProductRunningNumber: "--", // 网络产品已开通数量
	networkProductExpireNumber: "--", // 网络产品已过期数量
});
const cardList = computed<ConsoleCardItem[]>(() => [
	{
		type: "cloudServer",
		showIcon: true,
		currentCard: {
			title: "云服务器",
			label: "云服务器",
			countKey: "instanceCount",
			count: dataForm.value.instanceCount,

			status: [
				{
					tip: "运行中",
					num: dataForm.value.ecsRunningNumber,
					color: "#3BA46F",
				},
				{
					tip: "已停止",
					num: dataForm.value.ecsStoppedNumber,
					color: "#3972FD",
				},
				{
					tip: "已到期",
					num: dataForm.value.expireNumber,
					color: "#FBA201",
				},
				{
					tip: "将到期",
					num: dataForm.value.expiringNumber,
					color: "#FF4151",
				},
			],
		},
	},
	{
		type: "containerCluster",
		showIcon: true,
		currentCard: {
			title: "容器集群",
			label: "集群总数",
			countKey: "containerTotal",
			count: dataForm.value.containerTotal,

			status: [
				{
					tip: "正常",
					num: dataForm.value.containerNormalNumber,
					color: "#3BA46F",
				},
				{
					tip: "异常",
					num: dataForm.value.containerAbnormalNumber,
					color: "#FF4151",
				},
				{
					tip: "其他",
					num: dataForm.value.containerOtherNumber,
					color: "#FBA201",
				},
			],
		},
	},
	{
		type: "imageRepository",
		showIcon: true,
		currentCard: {
			title: "镜像仓库",
			label: "仓库总数",
			countKey: "imageCount",
			count: dataForm.value.imageCount,

			status: [
				{
					tip: "运行中",
					num: dataForm.value.imageRunningNumber,
					color: "#3BA46F",
				},
				{
					tip: "异常",
					num: dataForm.value.imageErrorNumber,
					color: "#FF4151",
				},
				{
					tip: "欠费关停",
					num: dataForm.value.imageStoppedNumber,
					color: "#FBA201",
				},
			],
		},
	},
	{
		type: "IsStorage",
		showIcon: false,
		currentCard: {
			title: "对象存储",
			label: "桶总数",
			countKey: "bucketCount",
			count: dataForm.value.bucketCount,

			status: [
				{
					tip: "桶对象总数",
					num: dataForm.value.bucketObjectCount,
					color: "#3BA46F",
				},
				{
					tip: "桶对象总容量",
					num: dataForm.value.bucketObjectSize,
					color: "#FBA201",
				},
			],
		},
	},
	{
		type: "networkProduct",
		showIcon: true,
		currentCard: {
			title: "AGI-C",
			label: "AGI-C",
			countKey: "networkProductCount",
			count: dataForm.value.networkProductCount,

			status: [
				{
					tip: "运行中产品",
					num: dataForm.value.networkProductRunningNumber,
					color: "#3BA46F",
				},
				{
					tip: "已过期产品",
					num: dataForm.value.networkProductExpireNumber,
					color: "#FF4151",
				},
			],
		},
	},
]);

const init = async () => {
	const { data } = await api.console.appletsConsoleList.get();
	dataForm.value = data;
};

const filterCards = computed<any>(() => {
	return cardList.value.filter((card) => {
		const count = dataForm.value[card.currentCard.countKey];
		// 判断 count 是否有效：非 0 且不是 "--"
		return count && count !== "--";
	});
});

//#region 轮播图

const activeList = ref([]);
const getActive = async () => {
	const { data } = await getActiveDetailsListApi();
	activeList.value = data;
};
const handleSwiperClick = () => {
	uni.navigateTo({ url: "/packageActive/activeCenter/index" });
};
//#endregion
onShow(() => {
	getActive();
	if (Auth.GET_TOKEN()) init();
});
</script>
<style lang="scss" scoped>
.content {
	padding-top: 176rpx;
	position: relative;

	.pd {
		padding: 24rpx 24rpx 0 24rpx;
		position: relative;
		z-index: 2;
	}

	.bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 458rpx;
		display: block;
		z-index: 1;
	}
}

.no-data {
	position: absolute;
	top: 800rpx;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 436rpx;
	height: 340rpx;
}
</style>
