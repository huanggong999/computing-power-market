<template>
	<view class="head">
		<view class="head-user flex-1">
			<up-image width="112rpx" height="112rpx" shape="circle" :src="Avatar" bgColor="transparent" />
			<view class="ml24 head-user-info between" @click="handlerLogin">
				<view>
					<view class="fz-34 lh-48 fwb flex-1">
						<view class="mr10">{{ UserName }}</view>
						<up-image v-if="isLogin" height="40rpx" :src="$ICON(
							`/mine/companyStatus_${pngStatus}.png`
						)
							" bgColor="transparent" mode="heightFix" />
					</view>
					<view class="head-user-info-id fz-26 lh-36">
						{{ UserID }}
					</view>
				</view>
				<up-image width="40rpx" height="40rpx" bgColor="transparent" :src="$ICON('/mine/arrow_right.png')" />
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { buildIcon } from "@/utils/icon";
import { useAuth } from "@/store";

const Auth = useAuth();
const isLogin = computed(() => !!Auth.TOKEN);
const UserInfo = computed(() => Auth.USER_INFO || {});

const default_config = {
	avatar: buildIcon("/mine/default_avatar.png"),
	unLoginTexts: { name: "未登录/注册", userID: "登录获取更多信息" },
};

const UserName = computed(() =>
	isLogin.value
		? UserInfo.value.customerName
		: default_config.unLoginTexts.name
);
const UserID = computed(() =>
	isLogin.value
		? `ID：${UserInfo.value.userId}`
		: default_config.unLoginTexts.userID
);
const pngStatus = computed(() => {
	const { certStatus, certType } = UserInfo.value;
	if (certStatus === 1) return "1";
	else if (certStatus === 3) {
		return certType === 1 ? "2" : "3";
	} else {
		return "4";
	}
});
const Avatar = computed(() =>
	isLogin.value ? UserInfo.value.avatar : default_config.avatar
);

const handlerLogin = () => {
	if (!Auth.checkLogin(false)) return;
	uni.navigateTo({ url: "/packageSetting/accountDetail/index" });
};
</script>

<style lang="scss" scoped>
.head {
	position: relative;
	z-index: 2;

	&-user {
		padding: 44rpx 20rpx 44rpx 28rpx;
		background: linear-gradient(180deg, #e6eeff 0%, #ffffff 100%);
		border-radius: 24rpx;

		&-info {
			width: calc(100% - 144rpx);

			&-id {
				color: #83889d;
			}
		}
	}
}
</style>
