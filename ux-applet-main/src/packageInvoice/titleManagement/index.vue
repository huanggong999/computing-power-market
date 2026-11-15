<template>
	<NavCom title="邮箱管理" bgColor="#fff" />
	<view class="content">
		<view class="title-option" v-for="item in titleList" :key="item.id">

			<view class="title overflow-1">{{ item.invoiceTitle }}</view>
			<view class="card-header">{{ item.invoiceType === 1 ? '增值税普通发票' : '增值税专用发票' }}</view>
			<view class="split"> </view>
			<view class="actions">
				<!-- 操作按钮 -->
				<view class="buttons">
					<view class="btn">
						<image :src="$ICON(`/image/edit_icon.png`)
							" />
						<text @click="editTitle(item)">编辑</text>
					</view>
					<view class="btn">
						<image :src="$ICON(`/image/delete_icon.png`)
							" />
						<text @click="deleteTitle(item)">删除</text>
					</view>

				</view>
			</view>
		</view>
	</view>
	<view class="footer" v-if="!isEmpty">
		<button class="footer-btn flx-center mt24" @click="createTitle">新增发票抬头</button>
	</view>
	<view class="mt50" v-if="isEmpty">
		<MyEmpty text="暂无内容" icon="/image/notitle.png" loadText="" :IsEmpty="!isEmpty" />
		<button class="empty-btn flx-center mt24" @click="createTitle">新增发票抬头</button>
	</view>

</template>

<script setup lang="ts">
import { deleteInvoiceTitleApi, getInvoiceTitleListApi } from '@/api/model/invoice'
import { useInvoice } from '@/store/modules/invoice'

const titleList = ref<any>([])
const INVOICE = useInvoice()
const isEmpty = computed(() =>
	titleList.value.length === 0
)
const initPage = () => {
	getInvoiceTitleListApi().then((res: any) => {
		titleList.value = res.data
	})
}
onShow(() => {
	initPage()
})



const editTitle = (item: any) => {
	INVOICE.SET_INVOICE_TITLE(item)
	uni.navigateTo({
		url: '/packageInvoice/titleManagement/titleEdit?type=edit'
	})
};


// 触发新增事件
const createTitle = () => {
	uni.navigateTo({
		url: '/packageInvoice/titleManagement/titleEdit'
	})
}

// 触发删除事件
const deleteTitle = (item: any) => {
	deleteInvoiceTitleApi(item.id).then((res: any) => {
		if (res.code === 200) {
			uni.showToast({
				title: '删除成功',
				icon: 'none'
			})
			initPage()
		}
	})
};
</script>

<style lang="scss" scoped>
.content {
	background: linear-gradient(180deg, #e6eefe 0%, #f3f4f6 100%);
	padding: 0 24rpx;
	padding-top: 206rpx;
}

.title-option {
	height: 180rpx;
	background: #FFFFFF;
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	padding: 24rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	margin-bottom: 20rpx;

	.card-header {

		font-weight: 400;
		font-size: 24rpx;
		color: #999999;

	}

	.title {

		font-weight: 400;
		font-size: 28rpx;
		color: #000000;

	}

	.split {
		width: 654rpx;
		height: 1rpx;
		background: #E5E5E5;
	}

	.actions {
		display: flex;
		justify-content: space-between;
		align-items: center;

		.default-checkbox {
			display: flex;
			align-items: center;

			font-weight: 400;
			font-size: 26rpx;


			image {
				width: 32rpx;
				height: 32rpx;
				margin-right: 10rpx;
			}
		}

		.buttons {
			display: flex;
			gap: 20rpx;
			margin-left: auto;

			.btn {
				display: flex;
				align-items: center;
				gap: 10rpx;

				image {
					width: 36rpx;
					height: 36rpx
				}

				font-weight: 400;
				font-size: 26rpx;
				color: #666666;

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

.dialog {
	width: 594rpx;
	background: #FFFFFF;
	box-shadow: 0rpx 0rpx 12rpx 2rpx rgba(181, 181, 181, 0.1);
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	display: flex;
	flex-direction: column;
	padding: 40rpx 32rpx;
	justify-content: space-evenly;
	padding-bottom: -20rpx;

	.dialog-header {
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: 400;
		font-size: 32rpx;
	}

	.dialog-body {
		margin: 60rpx 0;
	}

	.dialog-footer {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 18rpx;

		button {
			display: flex;
			justify-content: center;
			align-items: center;
			width: 256rpx;
			height: 72rpx;
			border-radius: 36rpx 36rpx 36rpx 36rpx;
			text-align: center;
			font-weight: 400;
			font-size: 26rpx;
		}

		.cancel {
			border-radius: 36rpx
		}

		.save {
			background: #3972FD;
			border-radius: 36rpx 36rpx 36rpx 36rpx;
			font-weight: 400;
			font-size: 26rpx;
			color: #FFFFFF;
		}

	}
}

.u-safe-area-inset-bottom {
	padding: 0 !important;
}

.empty-btn {
	width: 378rpx;
	height: 80rpx;
	background: #3972FD;
	border-radius: 44rpx;
	font-family: PingFang SC, PingFang SC;
	font-weight: 400;
	font-size: 28rpx;
	color: #FFFFFF;
	text-align: center;

}
</style>
