<template>
	<view class="content mt20" @click="toPage">
		<view
			class="lh-44 between"
			v-if="
				type == 'cloudServer' ||
				type == 'containerCluster' ||
				type == 'imageRepository'
			"
		>
			<view
				class="fz-32 overflow-1"
				style="max-width: 400rpx; font-weight: bold"
			>
				{{ data.instanceName ? data.instanceName : data.clusterName }}
			</view>
			<MyStatus
				:type="statusEnum[data.status].type"
				:text="statusEnum[data.status].text"
			/>
		</view>
		<view
			class="mt10 dis-flex"
			v-if="type == 'cloudServer' || type == 'containerCluster'"
		>
			<view class="tip mr16"> ID: </view>
			<view>
				{{ data.orderSourceUid ? data.orderSourceUid : data.clusterId }}
			</view>
		</view>
		<view
			class="mt40 dis-flex"
			v-if="type == 'cloudServer' || type == 'containerCluster'"
		>
			<view class="tip mr16"> IP: </view>
			<view class="IP-value" v-if="type == 'cloudServer'">
				<text style="padding-right: 20rpx"
					>{{
						data.eipAddress ? data.eipAddress.ipAddress : "--"
					}}
					(公)</text
				>
				<text
					>{{
						data.networkList
							? data.networkList[0].primaryIpAddress
							: "--"
					}}
					(内)</text
				>
			</view>
			<view class="IP-value" v-if="type == 'containerCluster'">
				<text style="padding-right: 20rpx"
					>{{
						data.apiServerPublicAccess
							? data.apiServerPublicAccess
							: "--"
					}}
					(公)</text
				>
				<text
					>{{
						data.privateAccess ? data.privateAccess : "--"
					}}
					(内)</text
				>
			</view>
		</view>
		<view class="mt40 dis-flex" v-if="type == 'imageRepository'">
			<view class="tip mr16"> 计费类型: </view>
			<view class="IP-value between">
				<text>{{ chargeTypeEnum[data.chargeType] }}</text>
			</view>
		</view>
		<!-- 到期时间 -->
		<!-- <view class="mt10 dis-flex" v-if="type == 'cloudServer' || type == 'containerCluster'">
			<view class="tip mr16">到期时间：</view>
			<view1> {{ data.expireTime ? data.expireTime : '--' }}</view1>
		</view> -->
		<view class="mt10 dis-flex" v-if="type == 'imageRepository'">
			<view class="tip mr16">创建时间</view>
			<view1> {{ data.createTime }}</view1>
		</view>

		<!-- 网络产品 -->
		<view class="lh-44 between" v-if="type == 'networkProduct'">
			<view
				class="fz-32 overflow-1"
				style="max-width: 400rpx; font-weight: bold"
			>
				{{ data.orderSourceList[0].productName }}
			</view>
			<MyStatus
				:type="statusEnum[data.actualStatus].type"
				:text="statusEnum[data.actualStatus].text"
			/>
		</view>
		<view class="mt10 dis-flex" v-if="type == 'networkProduct'">
			<view class="tip mr16"> 客户名称: </view>
			<view> {{ data.agiCustomerName }} </view>
		</view>
		<view class="mt40 dis-flex" v-if="type == 'networkProduct'">
			<view class="tip mr16"> 到期时间: </view>
			<view class="IP-value">
				<text style="padding-right: 20rpx">{{
					data.actualAgiExpireTime ? data.actualAgiExpireTime : "--"
				}}</text>
			</view>
		</view>
		<view
			class="mt40 dis-flex"
			v-if="type == 'networkProduct' && data.bandwidth"
		>
			<view class="tip mr16"> 带宽(M)数量: </view>
			<view class="IP-value">
				<text style="padding-right: 20rpx">{{
					data.bandwidth ? data.bandwidth : "--"
				}}</text>
			</view>
			<!-- <view class="tip mr16"> 购买时长: </view>
			<view class="IP-value" v-if="data.networkDay">
				<text style="padding-right: 20rpx">{{
					data.networkDay ? data.networkDay : "--"
				}}</text>
			</view> -->
		</view>
		<!-- 续费按钮 -->

		<!-- {{ data.networkProductId }} -->
		<view class="buttons">
			<!-- <button class="btn service" @click="codeDialogVisible = true">添加客服</button> -->
			<button
				class="btn pay"
				v-if="
					type == 'networkProduct' &&
					data.actualStatus != 1 &&
					data.actualStatus != 3 &&
					data.actualStatus != 5 &&
					data.chargeType !== 'POSTPAID_BY_HOUR'
				"
				@click.stop="renew"
			>
				立即续费
			</button>
		</view>
	</view>
</template>

<script setup lang="ts">
import {
	getAgicOrderInfoApi,
	getAIGCFormListApi,
	submitNetWorkPayFormApi,
} from "@/api/model/networkProduct";
import { getOrderInfoApi } from "@/api/model/order";
import { useAgicProduct } from "@/store/modules/agicProduct";
import { useProduction } from "@/store/modules/product";

const props = withDefaults(
	defineProps<{
		data: any;
		type: string;
		// title: string;
		// status?: string;
		// type?: string;
		// idValue: string;
		// // 公网IP

		// ip: string;
		// // 内网IP
		// innerIp: string;
		// // 到期时间
		// expireTime: string;
	}>(),
	{}
);
// 运行状态计算
const statusEnum: any = {
	CREATING: {
		text: "创建中",
		type: "info",
	},
	RUNNING: {
		text: "运行中",
		type: "success",
	},
	STOPPED: {
		text: "已停止",
		type: "error",
	},
	DELETING: {
		text: "删除中",
		type: "error",
	},
	RESIZING: {
		text: "更配中",
		type: "warning",
	},
	HAVE_NOT_OPENED: {
		text: "未开通",
		type: "warning",
	},
	HAVE_OPENED: {
		text: "已开通",
		type: "success",
	},
	CALCULATING: {
		text: "计算中",
		type: "info",
	},
	SHUT_DOWN: {
		text: "已停止",
		type: "error",
	},
	REFUNDED: {
		text: "已退款",
		type: "error",
	},
	ERROR: {
		text: "错误",
		type: "error",
	},
	创建中: {
		text: "创建中",
		type: "info",
	},
	运行中: {
		text: "运行中",
		type: "success",
	},
	停止中: {
		text: "停止中",
		type: "error",
	},
	已停止: {
		text: "已停止",
		type: "error",
	},
	重启中: {
		text: "重启中",
		type: "warning",
	},
	启动中: {
		text: "启动中",
		type: "success",
	},
	重装中: {
		text: "重装中",
		type: "warning",
	},
	更配中: {
		text: "更配中",
		type: "warning",
	},
	错误: {
		text: "错误",
		type: "error",
	},
	删除中: {
		text: "删除中",
		type: "error",
	},
	"1": {
		text: "未开通",
		type: "warning",
	},
	"2": {
		text: "运行中",
		type: "success",
	},
	"3": {
		text: "已过期",
		type: "info",
	},
	"4": {
		text: "已暂停",
		type: "error",
	},
	"5": {
		text: "已撤线",
		type: "error",
	},
};

const productDetail = useProduction();
const toPage = () => {
	if (props.type == "imageRepository")
		uni.navigateTo({
			url: `/packageConsole/details/index?id=${props.data.instanceName}&type=${props.type}`,
		});
	else if (props.type === "networkProduct") {
		productDetail.setProductionDetail(props.data);
		uni.navigateTo({
			url: `/packageMine/orderCenter/detail/index?id=${props.data.networkValueId}`,
		});
	} else
		uni.navigateTo({
			url: `/packageConsole/details/index?id=${props.data.id}&type=${props.type}`,
		});
};
const chargeTypeEnum: any = {
	POSTPAID_BY_HOUR: "按量计费",
	POSTPAID_BY_MONTH: "包年包月",
	POSTPAID_BY_YEAR: "包年包月",
	ONE_PAY: "一次性付费",
};
const emits = defineEmits(["refresh"]);
const isReNew = ref(false);
const agicProduct = useAgicProduct();

// 申请续费
const renew = async () => {
	let detailInfo: any = {};
	await getAgicOrderInfoApi(props.data.networkValueId).then((res: any) => {
		detailInfo = res.data;
	});
	agicProduct.setAgicDetail(detailInfo, "renew");
	agicProduct.setDisplayOrBind({
		isAccountIpBinding: detailInfo.isAccountIpBinding,
		isBandwidthDisplay: detailInfo.isBandwidthDisplay,
		isIpDisplay: detailInfo.isIpDisplay,
	});
	uni.navigateTo({
		url: `/packageAgic/buy/index?isEdit=true&id=${detailInfo.productId}`,
	});
	// if (isReNew.value) {
	// 	return;
	// }
	// isReNew.value = true;
	// submitNetWorkPayFormApi({
	// 	productId: props.data.networkProductId,
	// 	json: props.data.networkProductJson,
	// 	networkCount: props.data.networkCount,
	// 	networkDay: props.data.networkDay,
	// 	bandwidth: props.data.bandwidth,
	// 	noOrNoXufei: true,
	// })
	// 	.then((res: any) => {
	// 		if (res.code == 200) {
	// 			getAIGCFormListApi().then((res1: any) => {
	// 				if (res1.code === 200) {
	// 					const list = res1.data.list.filter((item: any) => {
	// 						return item.id === res.data;
	// 					});
	// 					const productDetail = useProduction();
	// 					productDetail.setProductionDetail(list[0]);
	// 					uni.navigateTo({
	// 						url: `/packageMine/orderCenter/detail/index`,
	// 					});
	// 				}
	// 			});
	// 			console.log(res);
	// 		} else {
	// 			uni.showToast({
	// 				title: res.msg,
	// 				icon: "none",
	// 			});
	// 		}
	// 	})
	// 	.finally(() => {
	// 		// 2 秒后重置标志位
	// 		setTimeout(() => {
	// 			isReNew.value = false;
	// 		}, 2000);
	// 	});
};
</script>

<style lang="scss" scoped>
.content {
	padding: 30rpx 24rpx;
	font-size: 26rpx;
	background: #ffffff;
	border-radius: 16rpx;
	line-height: 36rpx;

	.dis-flex {
		display: flex;
	}

	.tip {
		color: #83889d;
	}

	.IP-value {
		flex: 1;
	}

	.buttons {
		margin-left: auto;
		display: flex;
		align-items: center;
		gap: 20rpx;

		.btn {
			width: 164rpx;
			height: 60rpx;
			border-radius: 34rpx 34rpx 34rpx 34rpx;
			font-weight: 400;
			font-size: 26rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			margin-left: 76%;
			margin-top: 16rpx;
		}

		.pay {
			border: 1rpx solid #3972fd;
			background: #3972fd;
			color: #fff;
			white-space: nowrap;
		}
	}
}
</style>
