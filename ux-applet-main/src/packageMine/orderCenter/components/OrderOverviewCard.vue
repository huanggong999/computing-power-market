<template>
	<view class="order-container">
		<!-- 订单号 -->
		<view class="order-number"
			>订单时间：{{ info.createTime }}
			<MyStatus
				:type="actualStatusEnum[status].type"
				:text="actualStatusEnum[status].text"
			/>
		</view>
		<!-- 套餐信息 -->
		<view class="saas-info">
			<image class="sass-img" :src="info.productImg"></image>
			<view class="info">
				<view class="title overflow-1">{{ info.productName }}</view>
				<view class="price-group">
					<view class="price-item"
						><view class="label">实付价:</view>
						<view class="price">
							￥{{ info.unitPrice }}元/月
							<!-- <span class="line-price"
							>￥{{ detail.crossedPrice }}/天</span
						> -->
						</view>
					</view>
					<view class="price-item" v-if="info.isIpCountDisplay">
						<view class="label">IP单价：</view>
						<view class="price">
							￥{{ info.crossedPrice }}元/个
							<!-- <span class="line-price">￥{{ info.crossedPrice }}/天</span> -->
						</view>
					</view>
				</view>
				<view class="primary-price"
					>参考价：￥{{ info.referPrice }}元/月</view
				>
			</view>
		</view>

		<!-- 购买信息表格 -->
		<view class="purchase-info">
			<view class="info-item">
				<text class="label">购买个数</text>
				<text class="value">{{ info.networkCount }}</text>
			</view>
			<view class="info-item">
				<text class="label">ip个数</text>
				<text class="value">{{ info.ipCount }}</text>
			</view>
			<view class="info-item">
				<text class="label">带宽(M)</text>
				<text class="value">{{ info.bandwidth }}</text>
			</view>
			<view class="info-item">
				<text class="label">订单价格</text>
				<text class="value">{{
					info.payAmount ? info.payAmount : "--"
				}}</text>
			</view>
		</view>

		<!-- 添加客服 -->
		<view class="buttons">
			<!-- <button class="btn service" @click="codeDialogVisible = true">添加客服</button> -->
			<!-- <button class="btn pay" v-if="status == 1" @click="toPay">立即支付</button> -->
			<button class="btn pay" v-if="status != 1" @click="toDetail">
				查看详情
			</button>
		</view>
		<serviceDialog
			v-if="codeDialogVisible"
			@close="closeCodeDialog"
		></serviceDialog>
		<!-- <view class="dialog" v-if="dialogVisible">
			<image class="title" :src="$ICON('/image/cofirm_form.png')" />
			<view class="dialog-form">
				<formCreateUni ref="formCreateRef" :option="option"></formCreateUni>
			</view>

			<view class="form-agreement flex-1 fz-26 lh-36	x" @click="IsChecked = !IsChecked">
				<image class="form-agreement-icon mr16" :src="$ICON(`/image/${IsChecked ? 'IsCheck' : 'check'}.png`)
					" />
				我已阅读并同意
				<text @click.stop="open" class="form-agreement-tip">
					《AI智算云平台推广协议》
				</text>
			</view>
			<button @click="getFormData" class="dialog-btn mt20 mb20">确认</button>
			<view class="dialog-tip">表单提交后，会有工作人员与您联系</view>
			<view class="dialog-tip">如果您现在就需要帮助，可以与我们客服联系</view>
			<view class="dialog-close" @click="dialogVisible = false">X</view>
		</view>
		<view class="mask" v-if="dialogVisible"></view> -->
	</view>
</template>

<script lang="ts" setup>
import { useProduction } from "@/store/modules/product";
import serviceDialog from "./serviceDialog.vue";
import {
	getAIGCFormListApi,
	getNetWorkPayFormApi,
	submitNetWorkPayFormApi,
} from "@/api/model/networkProduct";
// 购买弹窗
// const dialogVisible = ref(false)
const codeDialogVisible = ref(false);
// const formCreateRef = ref()
// const option = ref<any>({ "form": { "labelPosition": "right", "size": "small", "labelWidth": "75px", } })
// const rule = ref<any>()
// const IsChecked = ref(false)
const closeCodeDialog = () => {
	codeDialogVisible.value = false;
};
// const getFormData = () => { // 在不点击内置的提交按钮的情况下，获取表单内输入的数据值，表单校验将不起作用
// 	if (!IsChecked.value) {
// 		uni.showToast({
// 			title: '请先阅读并同意《AI智算云平台推广协议》',
// 			icon: 'none'
// 		})
// 		return
// 	}
// 	const formData = JSON.stringify(formCreateRef.value.getFormData())
// 	// 给每个"前面加一个斜杠/

// 	submitNetWorkPayFormApi({
// 		productId: id.value,
// 		json: formData
// 	}).then((res: any) => {
// 		if (res.code == 200) {
// 			uni.showToast({
// 				title: '提交成功',
// 				icon: 'none'
// 			})
// 			dialogVisible.value = false
// 			codeDialogVisible.value = true
// 		} else {
// 			uni.showToast({
// 				title: res.msg,
// 				icon: 'none'
// 			})
// 		}
// 	})
// 	// console.log(JSON.stringify(formData)); // [{"type":"input","field":"Ffyi631rgf2hn","title":"输入框","info":"","$required":true,"_fc_drag_tag":"input","hidden":false,"display":true,"values":"111"},{"type":"inputNumber","field":"F78g6323ffje1","title":"计数器","info":"","$required":false,"_fc_drag_tag":"inputNumber","hidden":false,"display":true},{"type":"radio","field":"Fo0p6323ffxyk","title":"单选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"radio","hidden":false,"display":true,"values":1},{"type":"checkbox","field":"Fjfh6323fgc5h","title":"多选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"checkbox","hidden":false,"display":true,"values":[1,2]},{"type":"select","field":"Fork1oukyaqp1g","title":"选择器","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"select","hidden":false,"display":true},{"type":"switch","field":"Fsuh1oukyatz0r","title":"开关","info":"","$required":false,"_fc_drag_tag":"switch","hidden":false,"display":true},{"type":"timePicker","field":"Fnnm1oukyaz4tq","title":"时间选择器","info":"","$required":false,"_fc_drag_tag":"timePicker","hidden":false,"display":true},{"type":"datePicker","field":"Fud81oukyb2p59","title":"日期选择器","info":"","$required":false,"_fc_drag_tag":"datePicker","hidden":false,"display":true},{"type":"rate","field":"Fnjf1oukyb7tps","title":"评分","info":"","$required":false,"_fc_drag_tag":"rate","hidden":false,"display":true,"value":0,"values":2},{"type":"colorPicker","field":"F5zc1oukybcmk3","title":"颜色选择器","info":"","$required":false,"_fc_drag_tag":"colorPicker","hidden":false,"display":true},{"type":"upload","field":"Futt1oukybhfh6","title":"上传","info":"","$required":false,"props":{"action":""},"_fc_drag_tag":"upload","hidden":false,"display":true}]
// }
// // 打开咨询表单
// const openBuyDialog = () => {
// 	getNetWorkPayFormApi(props.info.id).then((res: any) => {
// 		rule.value = JSON.parse(res.data.json)
// 		formCreateRef.value.showForm(rule.value)
// 	})
// 	dialogVisible.value = true;
// }
// codeDialogVisible为true时候禁止页面滚动
watch(
	() => codeDialogVisible.value,
	(val) => {
		if (val) {
			document.body.style.overflow = "hidden";
		} else {
			document.body.style.overflow = "";
		}
	}
);
const actualStatusEnum: any = [
	{
		type: "warning",
		text: "未开通",
	},
	{
		type: "info",
		text: "已取消",
	},
	{
		type: "success",
		text: "运行中",
	},
	{
		type: "error",
		text: "已过期",
	},
	{
		type: "error",
		text: "已暂停",
	},
	{
		type: "error",
		text: "已撤线",
	},
];
const status = computed<any>(() => {
	if (props.info.actualStatus == 1 && props.info.payStatus == 1) {
		return 0;
	} else if (props.info.actualStatus == 1 && props.info.payStatus == 0) {
		return 1;
	} else if (props.info.actualStatus == 2 && props.info.payStatus == 1) {
		return 2;
	} else if (props.info.actualStatus == 3) {
		return 3;
	} else if (props.info.actualStatus == 4) {
		return 4;
	} else if (props.info.actualStatus == 5) {
		return 5;
	} else {
		return 1;
	}
});
const props = defineProps({
	info: {
		type: Object,
		default: () => {},
	},
});
const emits = defineEmits(["refresh"]);
const isReNew = ref(false);
// 申请续费
const renew = () => {
	if (isReNew.value) {
		return;
	}
	isReNew.value = true;
	submitNetWorkPayFormApi({
		productId: props.info.productId,
		json: props.info.json,
		networkCount: props.info.networkCount,
		networkDay: props.info.networkDay,
		bandwidth: props.info.bandwidth,
		noOrNoXufei: true,
	})
		.then((res: any) => {
			if (res.code == 200) {
				getAIGCFormListApi().then((res1: any) => {
					if (res1.code === 200) {
						const list = res1.data.list.filter((item: any) => {
							return item.id === res.data;
						});
						productDetail.setProductionDetail(list[0]);
						uni.navigateTo({
							url: `/packageMine/orderCenter/detail/index`,
						});
					}
				});
			} else {
				uni.showToast({
					title: res.msg,
					icon: "none",
				});
			}
		})
		.finally(() => {
			// 2 秒后重置标志位
			setTimeout(() => {
				isReNew.value = false;
			}, 2000);
		});
};

// 前往支付页
const productDetail = useProduction();
const toPay = () => {
	// const data = {
	// 	networkPayValueId: props.info.id,
	// 	networkProductId: props.info.productId,
	// 	networkCount: props.info.networkCount,
	// 	networkDay: props.info.networkDay,
	// 	bandwidth: props.info.bandwidth,
	// 	payFormValue: {
	// 		json: props.info.json,
	// 	},
	// }
	productDetail.setProductionDetail(props.info);
	uni.navigateTo({
		url: `/packageMine/orderCenter/detail/index`,
	});
};
const toDetail = () => {
	// productDetail.setProductionDetail(props.info);
	uni.navigateTo({
		url: `/packageConsole/details/index?id=${props.info.id}&type=networkProduct`,
	});
};
// 再次续费
const againPay = () => {};
</script>

<style lang="scss" scoped>
.order-container {
	padding: 24rpx;
	width: 88vw;
	min-height: 596rpx;
	background: #ffffff;
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	display: flex;
	flex-direction: column;
	gap: 24rpx;

	/* 订单号样式 */
	.order-number {
		font-weight: 400;
		font-size: 28rpx;
		color: #83889d;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	/* 状态标签 */
	.status-tag {
		margin-left: auto;
		color: #ff4444;
		margin-bottom: 40rpx;
	}

	/* 套餐信息 */

	.saas-info {
		display: flex;
		gap: 24rpx;

		.sass-img {
			width: 274rpx;
			height: 144rpx;
			background: #e6eeff;
			border-radius: 16rpx 16rpx 16rpx 16rpx;
		}

		.info {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			margin-bottom: 24rpx;

			.title {
				font-weight: bold;
				font-size: 32rpx;
				max-width: 300rpx;
			}
			.price-group {
				display: flex;
				align-items: center;
				gap: 20rpx;
			}
			.label {
				font-weight: 400;
				font-size: 24rpx;
				color: #3972fd;
			}

			.price {
				font-weight: bold;
				font-size: 30rpx;
				color: #3972fd;

				.line-price {
					font-weight: 400;
					font-size: 24rpx;
					color: #999999;
					text-decoration-line: line-through;
				}
			}

			.primary-price {
				font-weight: 400;
				font-size: 24rpx;
				color: #ff4151;
			}
		}
	}

	/* 购买信息表格 */
	.purchase-info {
		border-top: #e5e5e5 1rpx solid;
		border-bottom: #e5e5e5 1rpx solid;
		padding: 24rpx 0rpx;

		.info-item {
			display: flex;
			justify-content: space-between;
			margin-bottom: 24rpx;
			line-height: 1.6;
			font-weight: 400;
			font-size: 26rpx;

			.label {
				color: #999;
			}

			.value {
				color: #333;
			}
		}

		:last-child {
			margin-bottom: 0;
		}
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
		}

		.service {
			border: 1rpx solid #fba201;
			color: #fba201;
		}

		.pay {
			border: 1rpx solid #3972fd;
			background: #3972fd;
			color: #fff;
		}
	}
}
</style>
