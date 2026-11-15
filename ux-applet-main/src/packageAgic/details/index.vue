<template>
	<NavCom title="资源详情" bgColor="#fff" />
	<view class="content">
		<image class="product-img" :src="info?.image"></image>
		<view class="detail">
			<uaMarkdown :source="info?.detail" />
		</view>
		<view class="footer">
			<view class="info">
				<view class="price-group">
					<view class="price-item"
						><view class="label">实付价:</view>
						<view class="price">
							￥{{ info.payPrice }}元/月
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
				<!-- <view class="label">售价：</view>
				<view class="price">
					￥{{ info.payPrice }}元/月
					<span class="line-price">￥{{ info.crossedPrice }}/天</span>
				</view> -->
				<view class="primary-price"
					>参考价：￥{{ info.referPrice }}元/月</view
				>
			</view>
			<button class="buy-btn" @click="openBuyDialog">立即购买</button>
		</view>
	</view>
	<view class="dialog" v-if="dialogVisible">
		<image class="title" :src="$ICON('/image/cofirm_form.png')" />
		<view class="dialog-form">
			<formCreateUni ref="formCreateRef" :option="option"></formCreateUni>
		</view>

		<view
			class="form-agreement flex-1 fz-26 lh-36 x"
			@click="IsChecked = !IsChecked"
		>
			<image
				class="form-agreement-icon mr16"
				:src="$ICON(`/image/${IsChecked ? 'IsCheck' : 'check'}.png`)"
			/>
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
	<view class="mask" v-if="dialogVisible"></view>
	<serviceDialog
		v-if="codeDialogVisible"
		@close="closeCodeDialog"
	></serviceDialog>
</template>

<script setup lang="ts">
import formCreateUni from "@/uni_modules/form-create-uni_1.0.0/components/form-create-uni/form-create-uni.vue";
import {
	getNetWorkPayFormApi,
	getNetWorkProductDetailAPI,
	submitNetWorkPayFormApi,
} from "@/api/model/networkProduct";
import uaMarkdown from "@/components/ua-markdown/ua-markdown.vue";
import serviceDialog from "../../pages/agic/components/serviceDialog.vue";
const id = ref();
onLoad((options) => {
	id.value = options?.id;
	getNetWorkProductDetailAPI(id.value).then((res: any) => {
		info.value = res.data;
	});
});
const info = ref<any>();

// 购买弹窗
const dialogVisible = ref(false);
const codeDialogVisible = ref(false);
const formCreateRef = ref();
const option = ref<any>({
	form: { labelPosition: "right", size: "small", labelWidth: "75px" },
});
const rule = ref<any>();
const IsChecked = ref(false);
const closeCodeDialog = () => {
	codeDialogVisible.value = false;
};
const getFormData = () => {
	// 在不点击内置的提交按钮的情况下，获取表单内输入的数据值，表单校验将不起作用
	if (!IsChecked.value) {
		uni.showToast({
			title: "请先阅读并同意《AI智算云平台推广协议》",
			icon: "none",
		});
		return;
	}
	const formData = JSON.stringify(formCreateRef.value.getFormData());
	// 给每个"前面加一个斜杠/

	submitNetWorkPayFormApi({
		productId: id.value,
		json: formData,
	}).then((res: any) => {
		if (res.code == 200) {
			uni.showToast({
				title: "提交成功",
				icon: "none",
			});
			dialogVisible.value = false;
			codeDialogVisible.value = true;
		} else {
			uni.showToast({
				title: res.msg,
				icon: "none",
			});
		}
	});
	// console.log(JSON.stringify(formData)); // [{"type":"input","field":"Ffyi631rgf2hn","title":"输入框","info":"","$required":true,"_fc_drag_tag":"input","hidden":false,"display":true,"values":"111"},{"type":"inputNumber","field":"F78g6323ffje1","title":"计数器","info":"","$required":false,"_fc_drag_tag":"inputNumber","hidden":false,"display":true},{"type":"radio","field":"Fo0p6323ffxyk","title":"单选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"radio","hidden":false,"display":true,"values":1},{"type":"checkbox","field":"Fjfh6323fgc5h","title":"多选框","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"checkbox","hidden":false,"display":true,"values":[1,2]},{"type":"select","field":"Fork1oukyaqp1g","title":"选择器","info":"","effect":{"fetch":""},"$required":false,"options":[{"label":"选项1","value":1},{"label":"选项2","value":2}],"_fc_drag_tag":"select","hidden":false,"display":true},{"type":"switch","field":"Fsuh1oukyatz0r","title":"开关","info":"","$required":false,"_fc_drag_tag":"switch","hidden":false,"display":true},{"type":"timePicker","field":"Fnnm1oukyaz4tq","title":"时间选择器","info":"","$required":false,"_fc_drag_tag":"timePicker","hidden":false,"display":true},{"type":"datePicker","field":"Fud81oukyb2p59","title":"日期选择器","info":"","$required":false,"_fc_drag_tag":"datePicker","hidden":false,"display":true},{"type":"rate","field":"Fnjf1oukyb7tps","title":"评分","info":"","$required":false,"_fc_drag_tag":"rate","hidden":false,"display":true,"value":0,"values":2},{"type":"colorPicker","field":"F5zc1oukybcmk3","title":"颜色选择器","info":"","$required":false,"_fc_drag_tag":"colorPicker","hidden":false,"display":true},{"type":"upload","field":"Futt1oukybhfh6","title":"上传","info":"","$required":false,"props":{"action":""},"_fc_drag_tag":"upload","hidden":false,"display":true}]
};
// 打开咨询表单
const openBuyDialog = () => {
	// getNetWorkPayFormApi(id.value).then((res: any) => {
	// 	rule.value = JSON.parse(res.data.json);
	// 	formCreateRef.value.showForm(rule.value);
	// });
	// dialogVisible.value = true;
	// 携带产品id跳转购买页面
	uni.navigateTo({
		url: `/packageAgic/buy/index?id=${id.value}`,
	});
};
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	display: flex;
	flex-direction: column;
	gap: 10rpx;
	min-height: 88vh;

	.product-img {
		width: 100vw;
		height: 394rpx;
		border-radius: 0rpx 0rpx 0rpx 0rpx;
	}

	.detail {
		margin: 20rpx;
		margin-bottom: 230rpx;
		padding: 10rpx;
		width: 702rpx;
		background: #ffffff;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
		flex: 1;
	}

	// 至于底部
	.footer {
		position: fixed;
		bottom: 0;
		margin-top: 10rpx;
		width: 100%;
		height: 172rpx;
		border-top: #d7e2f6 6rpx solid;
		background-color: #fff;
		border-radius: 24rpx 24rpx 0rpx 0rpx;
		display: flex;
		justify-content: space-between;
		padding: 24rpx;

		.info {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			margin-bottom: 24rpx;
			min-width: 40%;
			gap: 10rpx;

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

		// 单独置底
		.buy-btn {
			width: 302rpx;
			height: 80rpx;
			background: #3972fd;
			border-radius: 44rpx 44rpx 44rpx 44rpx;
			margin-top: auto;
			margin-bottom: 28rpx;
			margin-left: auto;
			display: flex;
			justify-content: center;
			align-items: center;
			font-size: 28rpx;
			color: #fff;
		}
	}
}

/* 居中弹窗 */
.dialog {
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	z-index: 11;
	width: 590rpx;
	height: 1058rpx;
	background: #ffffff;
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx 40rpx;

	.title {
		width: 592rpx;
		height: 100rpx;
		background: #ffffff;
		border-radius: 16rpx 16rpx 16rpx 16rpx;
	}

	.dialog-form {
		flex: 1;
		overflow: auto;
	}

	.form-agreement {
		margin-top: 40rpx;
		display: flex;
		align-items: center;
		gap: 20rpx;

		&-icon {
			width: 28rpx;
			height: 28rpx;
		}

		&-tip {
			color: #3972fd;
		}
	}

	&-btn {
		width: 610rpx;
		height: 64rpx;
		background: #3972fd;
		border-radius: 32rpx 32rpx 32rpx 32rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: 400;
		font-size: 26rpx;
		color: #ffffff;
	}

	&-tip {
		font-weight: 400;
		font-size: 28rpx;
	}

	&-close {
		position: absolute;
		top: 20rpx;
		right: 20rpx;
		font-size: 32rpx;
		color: #999999;
	}
}

/* 遮罩层 */
.mask {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 10;
	overflow: hidden;
}
</style>
