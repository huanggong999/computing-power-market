<template>
	<NavCom title="资源详情" bgColor="#fff" />
	<!-- 服务器实例 -->
	<view class="content" v-if="type === 'cloudServer'">
		<view class="between">
			<view class="fz-32 dis-flex">
				<view class="tip mr16"> ID: </view>
				<view class="value tip overflow-1" style="max-width: 500rpx">
					{{ pageData.orderSourceUid ? pageData.orderSourceUid : "" }}
				</view>
			</view>
			<MyStatus
				:type="statusEnum[pageData.status].type"
				:text="statusEnum[pageData.status].text"
			/>
		</view>
		<view class="fz-32 fwb mt24 overflow-1" style="max-width: 600rpx">
			{{ pageData.instanceName }}
		</view>
		<view class="dis-flex mt24">
			<view class="tip mr16"> 实例规格： </view>
			<view class="tip">
				{{ pageData.ecsScale }}
			</view>
		</view>
		<!-- <view class="dis-flex mt24">
			<view class="tip mr16"> 秘钥： </view>
			<view class="tip">
				{{ "17b1e0f7-5ed7-4e2d-b7d6-c3deafae8e5d" }}
			</view>
		</view> -->
		<view class="mt30">
			<DetailsComCard title="网络信息" :list="networkInfo" />
		</view>

		<view class="mt20">
			<DetailsComCard title="配置信息" :list="configInfo" />
		</view>
		<view class="mt20">
			<DetailsComCard title="镜像信息" :list="imageInfo" />
		</view>
		<view class="mt20">
			<!-- <DetailsComCard title="云服务器ECS资源使用量" :list="ECSInfo" :noneBorder="true" /> -->
		</view>
		<view class="mt20">
			<DetailsComCard title="计费信息" :list="chargeInfo" />
		</view>
		<view
			class="mt20"
			v-for="(item, index) of addressInfoList"
			:key="index"
		>
			<DetailsComCard title="访问地址信息" :list="item" />
		</view>
	</view>
	<!-- 镜像实例 -->
	<view class="content" v-if="type === 'imageRepository'">
		<!-- <view class="between">
			<view class="fz-32 dis-flex">
				<view class="tip mr16"> 名称: </view>
				<view class="value tip overflow-1" style="max-width: 500rpx;"> {{ pageData.instanceName }} </view>
			</view>
			<MyStatus :type="statusEnum[pageData.status].type" :text="statusEnum[pageData.status].text" />
		</view>

		<view class="dis-flex mt24">
			<view class="tip mr16"> 实例规格： </view>
			<view class="tip">
				{{ pageData.ecsScale }}
			</view>
		</view> -->
		<view>
			<DetailsComCard title="镜像仓库概览" :list="warehouseInfo" />
		</view>
		<view class="mt30">
			<DetailsComCard title="OCI概览信息" :list="OciInfo" />
		</view>
	</view>
	<!-- 容器实例 -->
	<view class="content" v-if="type === 'containerCluster'">
		<view class="between">
			<view class="fz-32 dis-flex">
				<view class="tip mr16"> ID: </view>
				<view class="value tip overflow-1" style="max-width: 500rpx">
					{{ pageData.clusterId }}
				</view>
			</view>
			<MyStatus
				:type="statusEnum[pageData.status].type"
				:text="statusEnum[pageData.status].text"
			/>
		</view>
		<view class="fz-32 fwb mt24 overflow-1" style="max-width: 600rpx">
			{{ pageData.clusterName }}
		</view>
		<view class="dis-flex mt24">
			<view class="tip mr16"> kubernetesVersion版本: </view>
			<view class="tip">
				{{ pageData.kubernetesVersion }}
			</view>
		</view>
		<!-- <view class="dis-flex mt24">
			<view class="tip mr16"> 秘钥： </view>
			<view class="tip">
				{{ "17b1e0f7-5ed7-4e2d-b7d6-c3deafae8e5d" }}
			</view>
		</view> -->
		<view class="mt30">
			<DetailsComCard title="集群网络信息" :list="clusterNetworkInfo" />
		</view>
		<view class="mt30">
			<DetailsComCard title="集群API Server配置" :list="clusterAPIInfo" />
		</view>
		<view class="mt30">
			<DetailsComCard
				title="云服务器ECS资源使用量"
				:list="containerEcsInfo"
			/>
		</view>
	</view>
	<!-- 网络产品详情 -->
	<view class="content" v-if="type === 'networkProduct'">
		<view class="between">
			<view class="fz-32 dis-flex">
				<view class="tip mr16"> 订单号: </view>
				<view class="value tip overflow-1" style="max-width: 500rpx">
					{{ pageData.orderNo }}
				</view>
			</view>
			<MyStatus
				:type="statusEnum[pageData.actualStatus].type"
				:text="statusEnum[pageData.actualStatus].text"
			/>
		</view>
		<view class="fz-32 fwb mt24 overflow-1" style="max-width: 600rpx"
			>{{ pageData.agiCustomerName }} [ {{ pageData.productName }} ]
		</view>

		<view class="mt30">
			<DetailsComCard title="网络信息" :list="productInfo" />
		</view>
		<view class="mt30">
			<DetailsComCard title="计费信息" :list="amountInfo" />
		</view>
		<view class="mt30" v-for="(item, index) in pwdArray" :key="index">
			<DetailsComCard title="账户信息" :list="item" />
		</view>
	</view>
</template>

<script setup lang="ts">
import {
	getContainerDetailApi,
	getEcsInstanceDetailApi,
	getNetWorkProductListAPI,
	getOCIOverviewApi,
	getRepositoryDetailApi,
} from "@/api/model/console";
import DetailsComCard from "./components/DetailsComCard.vue";
import { usePageList } from "@/hook/pageList";
import { statusEnum } from "@/components/MyStatus/enum";
import { getAgicOrderInfoApi } from "@/api/model/networkProduct";
const id = ref();
const type = ref();
onLoad((options?) => {
	// 如果是镜像则id其实是instanceName
	id.value = options?.id;
	type.value = options?.type;
});
const requestApi = computed(() => {
	switch (type.value) {
		case "networkProduct":
			return getAgicOrderInfoApi;
		case "cloudServer":
			return getEcsInstanceDetailApi;
		case "imageRepository":
			return getRepositoryDetailApi;
		case "containerCluster":
			return getContainerDetailApi;
	}
});
const pageData = ref<any>();
// 附加oci概览信息(镜像仓库)
const ociData = ref<any>();
// 获取数据
onMounted(() => {
	// if (type.value === 'networkProduct') {
	// 	const params = {
	// 		type: 'PRODUCT',
	// 		orderId: id.value
	// 	}
	// 	getNetWorkProductListAPI(params).then((res: any) => {
	// 		pageData.value = res.data.list[0]
	// 	})

	// }
	if (requestApi.value) {
		requestApi.value(id.value).then((res: any) => {
			pageData.value = res.data;
			console.log("sjkuu", pageData.value);
		});
		return;
	}
	if (type.value === "imageRepository") {
		getOCIOverviewApi(id.value).then((res: any) => {
			ociData.value = res.data;
		});
		return;
	}
});

// 网络信息
const networkInfo = computed(() => {
	if (!pageData.value.networkList) {
		return [
			{ label: "所属私有网络", value: "--" },
			{ label: "所属子网", value: "--" },
			{ label: "所属子网ID", value: "--" },
			{ label: "主IPv4内网IP", value: "--" },
		];
	}
	return [
		{
			label: "所属私有网络",
			value: pageData.value.networkList[0].vpcId ?? "--",
		},
		{
			label: "所属子网",
			value: pageData.value.networkList[0].macAddress ?? "--",
		},
		{
			label: "所属子网ID",
			value: pageData.value.networkList[0].subnetId ?? "--",
		},
		// { label: "API Server 公网访问", value: pageData.value.networkList[0] },
		{
			label: "主IPv4内网IP",
			value: pageData.value.networkList[0].primaryIpAddress ?? "--",
		},
	];
});
// 配置信息
const configInfo = computed(() => [
	{ label: "操作系统", value: pageData.value.osName ?? "--" },
	{ label: "内存", value: pageData.value.memorySize + "GB" ?? "--" },
	{ label: "CPU", value: pageData.value.cpuModel ?? "--" },
	// { label: "公网宽带", value: pageData.value },
	{ label: "系统盘Id", value: pageData.value.sysVolume.volumeId ?? "--" },
]);
// 镜像信息
const imageInfo = computed(() => [
	// { label: "镜像名称", value: pageData.value },
	// { label: "镜像类型", value: pageData.value },
	{ label: "镜像ID", value: pageData.value.imageId ?? "--" },
]);
// 计费信息
const chargeInfo = computed(() => [
	{
		label: "实例计费模式",
		value:
			pageData.value.chargeType === "POSTPAID_BY_HOUR"
				? "按量付费"
				: "包年包月",
	},
	{ label: "网络计费模式", value: "按量计费" },
	{ label: "创建时间", value: pageData.value.createTime },
]);
// 访问地址信息
const addressInfoList = computed(() => {
	if (!pageData.value.publicInfos) {
		return [];
	}
	return pageData.value.publicInfos.map((item: any) => {
		return [
			{
				label: "公网IP",
				value: item.publicIp,
			},
			{
				label: "私网IP",
				value: item.privateIp,
			},
			{
				label: "访问方式",
				value: item.accessMethod,
			},
			{
				label: "端口",
				value: item.port,
			},
		];
	});
});
// 镜像详情
// 仓库概览
const warehouseInfo = computed(() => [
	{ label: "实例名称", value: pageData.value.instanceName ?? "--" },
	{ label: "username", value: pageData.value.username ?? "--" },
	{ label: "付费类型", value: pageData.value.chargeType ? "按量付费" : "--" },
	{ label: "实例规格", value: pageData.value.version ?? "--" },
	{ label: "地域", value: pageData.value.region ?? "--" },
	{ label: "项目", value: pageData.value.project ?? "--" },
	{ label: "访问域名", value: pageData.value.domain ?? "--" },
]);
// oci概览信息
const OciInfo = computed(() => [
	{ label: "命名空间", value: ociData.value.namespace ?? "--" },
	{ label: "类型", value: ociData.value.type ?? "--" },
	{ label: "OCI制品仓库名称", value: ociData.value.ociName ?? "--" },
]);

//  容器详情
// 集群网络信息
const clusterNetworkInfo = computed(() => [
	{ label: "所属私有网络", value: pageData.value.vpcType ?? "--" },
	{ label: "Pod子网", value: pageData.value.podSubnet ?? "--" },
	{ label: "Proxy模式", value: pageData.value.proxyMode ?? "--" },
	{ label: "Pod默认安全组", value: pageData.value.podSecurityGroup ?? "--" },
	{ label: "网络模型", value: pageData.value.networkModel ?? "--" },
	{ label: "Service CIDRv4", value: pageData.value.serviceCidrV4 ?? "--" },
	{
		label: "节点默认安全组",
		value: pageData.value.nodeSecurityGroup ?? "--",
	},
	{ label: "NAT网关", value: pageData.value.natName ?? "--" },
]);
// 集群API Server配置
const clusterAPIInfo = computed(() => [
	{ label: "控制面子网", value: pageData.value.controlPlaneSubnet ?? "--" },
	{ label: "负载均衡", value: pageData.value.loadBalancer ?? "--" },
	{ label: "私网访问", value: pageData.value.privateAccess ?? "--" },
	{
		label: "API Server 公网访问",
		value: pageData.value.apiServerPublicAccess ?? "--",
	},
]);
// 云服务器ECS资源使用量
const containerEcsInfo = computed(() => [
	{ label: "CPU(Core)", value: pageData.value.cpuCore ?? "--" },
	{ label: "内存(GiB)", value: pageData.value.memoryGiB ?? "--" },
	{ label: "实例(个)", value: pageData.value.instanceNumber ?? "--" },
]);

// 网络产品
// 产品信息
const productInfo = computed(() => {
	let durationText = "";
	switch (pageData.value.durationUnit) {
		case "MONTH":
			durationText = "月";
			break;
		case "YEAR":
			durationText = "年";
			break;
		default:
			durationText = "按天计费";
			break;
	}
	let duration = durationText == "按天计费" ? "" : pageData.value.duration;
	return [
		{ label: "购买产品数量", value: pageData.value.networkCount ?? "--" },
		// { label: "购买产品时长", value: duration + durationText },
		{ label: "带宽(M)数量", value: pageData.value.bandwidth ?? "--" },
	];
});
// 计费信息
const amountInfo = computed(() => [
	{ label: "支付时间", value: pageData.value.payTime ?? "--" },
	{ label: "创建时间", value: pageData.value.createTime ?? "--" },
	{
		label: "开通时间",
		value:
			pageData.value.actualStatus != 1
				? pageData.value.actualAgiOpenTime
				: "--",
	},
	{ label: "到期时间", value: pageData.value.actualAgiExpireTime ?? "--" },
	{
		label: "状态",
		value: statusEnum[pageData.value.actualStatus].text ?? "--",
	},
]);
// 账号密码信息
const pwdArray = computed(() => {
	// 遍历pageData的userPwdList,并将每个子项放在一个新数组里,再将所有的新数组组成一个数组
	return (
		pageData.value.userPwdList?.map((item: any) => {
			return [
				{ label: "ip", value: item.ip ?? "--" },
				{ label: "邮箱", value: item.email ?? "--" },
				{ label: "密码", value: item.pwd ?? "--" },
			];
		}) ?? []
	);
});
</script>

<style lang="scss" scoped>
.content {
	padding: 28rpx 24rpx;
	padding-top: 216rpx;
	position: relative;
	font-size: 26rpx;
	line-height: 36rpx;

	.dis-flex {
		display: flex;
	}

	.tip {
		color: #83889d;
	}
}
</style>
