<template>
	<NavCom title="个人信息" bgColor="#fff" />
	<view class="content fz-29 lh-40">
		<view class="content-info between">
			<view> 昵称 </view>
			<view class="between">
				<up-input
					v-model="dataForm.customerName"
					border="none"
					inputAlign="right"
					type="nickname"
				/>
				<up-image
					width="40rpx"
					height="40rpx"
					:src="$ICON('/mine/arrow_right.png')"
					bgColor="transparent"
				/>
			</view>
			<button
				open-type="chooseAvatar"
				class="content-info-avatar"
				@chooseavatar="changeAvatar"
			>
				<up-image
					width="154rpx"
					height="154rpx"
					shape="circle"
					:src="dataForm.avatar"
				/>
				<view class="content-info-avatar-text">更换头像</view>
			</button>
		</view>
		<view class="content-submit mt72" @click="submit"> 保存 </view>
	</view>
</template>

<script setup lang="ts">
import { uploadFileApi } from "@/api/model/file";
import { updateCustomerInfoApi } from "@/api/model/mine";
import { useAuth } from "@/store";
import { myShowToast } from "@/utils";

const Auth = useAuth();
const userInfo = Auth.USER_INFO;
const dataForm = ref({
	avatar: userInfo.avatar,
	customerName: userInfo.customerName,
});

const changeAvatar = async (e: any) => {
	const { avatarUrl } = e.detail;
	const { data } = await uploadFileApi(avatarUrl);
	dataForm.value.avatar = data.url;
};
const submit = async () => {
	if (!dataForm.value.customerName && !dataForm.value.avatar)
		return myShowToast("请填写昵称或上传头像");
	await updateCustomerInfoApi(dataForm.value);
	myShowToast("已修改成功!");
	Auth.GetUserInfo();
	uni.$u.sleep(2000).then(() => uni.navigateBack());
};
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	padding: 176rpx 24rpx;
	&-info {
		padding: 138rpx 16rpx 40rpx 30rpx;
		margin-top: 116rpx;
		border-radius: 16rpx;
		background-color: #fff;
		position: relative;
		&-avatar {
			width: 154rpx;
			height: 154rpx;
			border-radius: 50%;
			overflow: hidden;
			position: absolute;
			top: -76rpx;
			left: 50%;
			transform: translate(-50%);
			padding: 0;
			&-text {
				position: absolute;
				width: 100%;
				height: 48rpx;
				font-size: 20rpx;
				background: rgba(0, 0, 0, 0.5);
				bottom: 0;
				color: #fff;
			}
		}
	}
	&-submit {
		padding: 20rpx 0;
		background: var(--themeColors);
		border-radius: 44rpx;
		color: #ffffff;
		font-weight: 500;
		font-size: 28rpx;
		line-height: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
}
</style>
