<template>
	<NavCom :title="navTitle" bgColor="#fff" />
	<view class="content">
		<view class="content-dropdown">
			<up-dropdown border-bottom v-if="showDropdown">
				<up-dropdown-item
					v-for="(item, index) in filteredDropdown"
					:key="index"
					v-model="pageRequest.searchParam[item.value]"
					:title="item.title"
					:options="item.options"
					@change="pageRequest.refreshData"
				/>
			</up-dropdown>
		</view>
		<scroll-view
			refresher-backgroundscroll-view="transparent"
			style="min-height: 100%"
			scroll-with-animation
			scroll-y
			@scrolltolower="pageRequest.reachBottom"
			refresher-enabled
			@refresherrefresh="pageRequest.refreshData"
			:refresher-triggered="pageRequest.triggered"
		>
			<view
				class="content-list"
				v-for="(item, index) in pageRequest.dataList"
				:key="index"
			>
				<view class="mb24">
					<!-- <ListComCard :title="item.instanceName" :idValue="item.idValue" :ip="item.ip" :innerIp="item.innerIp"
					:expireTime="item.expireTime" /> -->
					<ListComCard
						:data="item"
						:key="item.id"
						:type="type"
					></ListComCard>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script lang="ts" setup>
import { usePageList } from "@/hook/pageList";
import ListComCard from "./components/ListComCard.vue";
import {
	getContainerListApi,
	getEcsInstanceListApi,
	getImageListApi,
	getNetWorkProductListAPI,
} from "@/api/model/console";

const titleEnum = {
	cloudServer: "资源列表",
	containerCluster: "容器集群  ",
	imageRepository: "镜像仓库",
	networkProduct: "网络产品",
	IsStorage: "",
};
type TTitle = keyof typeof titleEnum;
const type = ref<TTitle>("cloudServer");
// 获取路由参数
onLoad((options) => {
	type.value = options?.type;
});
const navTitle = computed(() => titleEnum[type.value]);

const dropdown = [
	{
		title: "全部地区",
		value: "type",
		options: [
			{ label: "全部地区", value: "" },
			{ label: "华北2（北京）", value: 1 },
			{ label: "华东2（上海）", value: 2 },
		],
	},
	{
		title: "计费模式",
		value: "chargeType",
		options: [
			{ label: "全部计费模式", value: "" },
			{ label: "按量计费", value: "POSTPAID_BY_HOUR" },
			{ label: "包年包月", value: "POSTPAID_BY_MONTH" },
		],
	},
];
// 根据 type 动态过滤下拉筛选项
const filteredDropdown = computed(() => {
	if (type.value === "cloudServer") {
		return dropdown; // 显示所有筛选项
	} else if (type.value === "imageRepository") {
		return [dropdown[0]]; // 仅显示地区筛选项
	} else {
		return []; // 隐藏所有筛选项
	}
});
// 控制是否显示筛选区域
const showDropdown = computed(() => filteredDropdown.value.length > 0);
// 筛选项变更
const changeType = (value: any) => {
	console.log("🚀 ~ changeType ~ e:", value);
};

// 获取列表数据
const requestApi = computed(() => {
	switch (type.value) {
		case "cloudServer":
			return getEcsInstanceListApi;
		case "imageRepository":
			return getImageListApi;
		case "containerCluster":
			return getContainerListApi;
		case "networkProduct":
			return getNetWorkProductListAPI;
	}
});
const pageRequest = ref<any>(
	usePageList({ api: requestApi.value, requestAuto: false })
);

// 更新页面请求api
onMounted(() => {
	pageRequest.value = usePageList({
		api: requestApi.value,
		requestAuto: false,
		initParams:
			type.value === "networkProduct"
				? { orderType: "PRODUCT", orderStatus: "PAID" }
				: {},
	});
	pageRequest.value.getList();
});
onShow(() => {
	// 重新获取列表数据
	pageRequest.value.refreshData();
});
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	position: relative;

	&-list {
		padding: 12rpx 24rpx 0;
	}
}
</style>
