<template>
	<NavCom title="邮箱管理" bgColor="#fff" />
	<view class="content">
		<view class="email-option" v-for="item in emailList" :key="item.id">
			<view class="card-header">邮箱账号</view>
			<view class="email">{{ item.email }}</view>
			<view class="split"> </view>
			<view class="actions">
				<!-- 复选框 -->
				<view class="default-checkbox" @click="toggleDefault(item)">
					<image :src="$ICON(`/image/${item.isDefault ? 'IsCheck' : 'check'}.png`)
						" />
					默认邮箱
				</view>

				<!-- 操作按钮 -->
				<view class="buttons">
					<view class="btn">
						<image :src="$ICON(`/image/edit_icon.png`)
							" />
						<text @click="editEmail(item)">编辑</text>
					</view>
					<view class="btn">
						<image :src="$ICON(`/image/delete_icon.png`)
							" />
						<text @click="deleteEmail(item)">删除</text>
					</view>

				</view>
			</view>
		</view>
	</view>
	<view class="footer" v-if="!isEmpty">
		<button class="footer-btn flx-center mt24" @click="createEmail">新增邮箱</button>
	</view>
	<view class="mt50" v-if="isEmpty">
		<MyEmpty text="暂无内容" icon="/image/noemail.png" loadText="" :IsEmpty="!isEmpty" />
		<button class="empty-btn flx-center mt24" @click="createEmail">新增邮箱</button>
	</view>
	<up-popup v-model:show="dialog" mode="center" style="border-radius: 16rpx;">
		<view class="dialog">
			<view class="dialog-header flx-center">{{ dialogTitle }}</view>
			<view class="dialog-body">
				<up-input shape="circle" v-model="curEmail.email" placeholder="请输入邮箱" class="log-input">
					<template #prefix>
						<text style="color:#999999">电子邮箱:</text>
					</template>
				</up-input>
			</view>
			<view class="dialog-footer">
				<button class="cancel" @click="dialog = false">取消</button>
				<button class="save" @click="confirmHandle">保存</button>
			</view>
		</view>
	</up-popup>
</template>

<script setup lang="ts">
import { createInvoiceEmailApi, deleteInvoiceEmailApi, getInvoiceEmailListApi, setDefaultEmailApi, updateInvoiceEmailApi } from '@/api/model/invoice'
const dialog = ref(false)
const emailList = ref<any>([])
const isEmpty = computed(() =>
	emailList.value.length === 0
)
const initPage = () => {
	getInvoiceEmailListApi().then((res: any) => {
		emailList.value = res.data
	})
}
onMounted(() => {
	initPage()
})

// 逻辑处理
// 处理默认邮箱选中状态
const toggleDefault = (item: any) => {
	if (item.isDefault) {
		uni.showToast({
			title: '须有一个默认邮箱',
			icon: 'none'
		})
		return
	}
	const data = {
		id: item.id,
		email: item.email
	}
	setDefaultEmailApi(data).then((res: any) => {
		if (res.code === 200) {
			uni.showToast({
				title: '设置成功',
				icon: 'none'
			})
			initPage()
		}
	})
};

// 触发编辑事件
const dialogType = ref('create')
const curEmail = ref<any>({ email: '' })
const dialogTitle = computed(() => {
	if (dialogType.value == 'edit') {
		return '修改发票邮箱'
	} else {
		return '新增发票邮箱'
	}
})
const editEmail = (item: any) => {
	dialogType.value = 'edit'
	curEmail.value = JSON.parse(JSON.stringify(item))
	dialog.value = true
};
const confirmHandle = () => {
	console.log(curEmail.value);
	if (curEmail.value.email.trim() == '') {
		uni.showToast({
			title: '邮箱不能为空',
			icon: 'none'
		})
		return;
	}
	// 编辑
	if (dialogType.value == 'edit') {
		const params = {
			id: curEmail.value.id,
			email: curEmail.value.email
		}
		updateInvoiceEmailApi(params).then((res: any) => {
			if (res.code === 200) {
				uni.showToast({
					title: '修改成功',
					icon: 'none'
				})
				initPage()
				dialog.value = false
			}
		})
	} else {
		// 新增
		createInvoiceEmailApi({ email: curEmail.value.email }).then((res: any) => {
			if (res.code === 200) {
				uni.showToast({
					title: '增加成功',
					icon: 'none'
				})
				initPage()
				dialog.value = false
			}
		})
	}
}

// 触发新增事件
const createEmail = () => {
	dialogType.value = 'create'
	curEmail.value = {
		email: ''
	}
	dialog.value = true
}

// 触发删除事件
const deleteEmail = (item: any) => {
	deleteInvoiceEmailApi(item.id).then((res: any) => {
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

.email-option {
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

	.email {

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
	border-radius: 16rpx;
	display: flex;
	flex-direction: column;
	padding: 40rpx 32rpx;
	justify-content: space-evenly;

	.dialog-header {
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: 400;
		font-size: 32rpx;
		padding-top: 40rpx;
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
			border-radius: 36rpx;
			text-align: center;
			font-weight: 400;
			font-size: 26rpx;
		}

		.cancel {

			border: 1rpx solid #999;
		}

		.save {
			background: #3972FD;

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
