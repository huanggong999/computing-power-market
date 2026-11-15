<template>
	<NavCom title="发票抬头" bgColor="#fff" />
	<view class="content">
		<view class="title-card">
			<view class="form-item">
				<view class="label">发票类型</view>
				<view class="radio-group">
					<view class="radio-item mr10" @click="formData.invoiceType = 1">
						<image :src="$ICON(`/image/${formData.invoiceType == 1 ? 'IsCheck' : 'check'}.png`)
							" />
						普通发票
					</view>
					<view class="radio-item" @click="formData.invoiceType = 2">
						<image :src="$ICON(`/image/${formData.invoiceType == 2 ? 'IsCheck' : 'check'}.png`)
							" />
						专用发票
					</view>
				</view>
			</view>
			<view class="form-item">
				<view class="label">开票类型</view>
				<view class="radio-group">
					<view class="radio-item mr10" @click="formData.billingType = 1">
						<image :src="$ICON(`/image/${formData.billingType == 1 ? 'IsCheck' : 'check'}.png`)
							" />
						个人
					</view>
					<view class="radio-item" @click="formData.billingType = 2">
						<image :src="$ICON(`/image/${formData.billingType == 2 ? 'IsCheck' : 'check'}.png`)
							" />
						企业
					</view>
				</view>
			</view>
			<view class="form-item">
				<view class="label">发票抬头</view>
				<view class="value">
					<up-input v-model="formData.invoiceTitle" type="text" border="none"
						placeholderStyle="text-align:right" placeholder="请输入发票抬头" />
				</view>
			</view>
			<view class="form-item" v-if="formData.billingType === 2">
				<view class="label">税号</view>
				<view class="value">
					<up-input v-model="formData.number" type="text" border="none" placeholderStyle="text-align:right"
						placeholder="请输入发票抬头" />
				</view>
			</view>
			<view class="form-item" v-if="formData.billingType === 2">
				<view class="label">开户银行</view>
				<view class="value">
					<up-input v-model="formData.deposit" type="text" border="none" placeholderStyle="text-align:right"
						placeholder="选填" />
				</view>
			</view>
			<view class="form-item" v-if="formData.billingType === 2">
				<view class="label">银行账号</view>
				<view class="value">
					<up-input v-model="formData.account" type="text" border="none" placeholderStyle="text-align:right"
						placeholder="选填" />
				</view>
			</view>

			<view class="form-item" v-if="formData.billingType === 2">
				<view class="label">企业地址</view>
				<view class="value">
					<up-input v-model="formData.address" type="text" border="none" placeholderStyle="text-align:right"
						placeholder="选填" />
				</view>
			</view>
			<view class="form-item" v-if="formData.billingType === 2">
				<view class="label">企业电话</view>
				<view class="value">
					<up-input v-model="formData.phoneNumber" type="text" border="none"
						placeholderStyle="text-align:right" placeholder="选填" />
				</view>
			</view>
		</view>
	</view>
	<view class="footer">
		<button class="footer-btn flx-center mt24" @click="submit">确认</button>
	</view>


</template>

<script setup lang="ts">
import { createInvoiceTitleApi, updateInvoiceTitleApi } from '@/api/model/invoice'
import { useInvoice } from '@/store/modules/invoice'

const type = ref('create')
const INVOICE = useInvoice()
onLoad((options) => {
	type.value = options?.type ? options?.type : 'create'
	if (type.value === 'edit') {
		formData.value = INVOICE.GET_INVOICE_TITLE()
	}
})
const formData = ref<any>({
	billingType: 1,
	invoiceType: 1,
})
const submit = () => {
	// 新增
	let data;
	if (formData.value.billingType == 1) {
		data = {
			invoiceTitle: formData.value.invoiceTitle,
			invoiceType: formData.value.invoiceType,
			billingType: formData.value.billingType
		}
	} else {
		data = formData.value
	}
	if (type.value == 'create') {
		createInvoiceTitleApi(data).then((res: any) => {
			if (res.code == 200) {
				uni.showToast({
					title: '新增成功',
					icon: 'none'
				})
				uni.navigateBack()
			}
		})
	}
	// 编辑
	else {
		updateInvoiceTitleApi(data).then((res: any) => {
			if (res.code == 200) {
				uni.showToast({
					title: '新增成功',
					icon: 'none'
				})
				uni.navigateBack()
			}
		})
	}
}
</script>

<style lang="scss" scoped>
.content {
	background: linear-gradient(180deg, #e6eefe 0%, #f3f4f6 100%);
	padding: 0 24rpx;
	padding-top: 206rpx;
}

.title-card {
	background: #FFFFFF;
	border-radius: 16rpx;
	padding: 34rpx 28rpx;
	display: flex;
	flex-direction: column;
	gap: 34rpx;

	.form-item {
		display: flex;
		justify-content: space-between;
		align-items: center;

		.label {
			font-weight: 400;
			font-size: 28rpx;
		}

		.radio-group {
			display: flex;
			justify-content: space-evenly;

			.radio-item {
				display: flex;
				align-items: center;
				gap: 10rpx;

				image {
					width: 36rpx;
					height: 36rpx;
				}

				font-weight: 400;
				font-size: 28rpx;
				color: #000000;

			}
		}
	}
}


.footer {
	padding: 0 24rpx;
	position: fixed;
	top: 90%;
	width: 93%;

	.footer-btn {
		width: 100%;
		height: 80rpx;
		background: #3972FD;
		border-radius: 44rpx;
		font-family: PingFang SC, PingFang SC;
		font-weight: 400;
		font-size: 28rpx;
		color: #FFFFFF;
		text-align: center;
	}
}
</style>
