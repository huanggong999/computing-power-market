<template>
	<NavCom title="账号信息" />
	<view class="content">
		<view class="content-bg"></view>
		<view class="content-list pd fz-28 lh-40">
			<view class="content-list-item between" v-for="(item, index) in accountList" :key="index"
				@click="handlerClick(item)">
				<view class="content-list-item-label"> {{ item.label }} </view>
				<view class="between">
					<view v-if="!!item.value" :class="{ phone: item.phone }">
						{{ item.value }}
					</view>
					<up-image v-if="!!item.certStatus" height="40rpx" :src="$ICON(
						`/mine/companyStatus_${pngStatus}.png`
					)
						" bgColor="transparent" mode="heightFix" />

					<up-image v-if="!item.icon" width="40rpx" height="40rpx" :src="$ICON('/mine/arrow_right.png')"
						bgColor="transparent" />
				</view>
			</view>
		</view>
		<view class="content-bottom" @click="logout">退出登录</view>
	</view>
</template>

<script setup lang="ts">
import { useAuth } from "@/store";

const Auth = useAuth();

const UserInfo = computed(() => Auth.USER_INFO);
const pngStatus = computed(() => {
	const { certStatus, certType } = UserInfo.value;
	if (certStatus === 1) return "1";
	else if (certStatus === 3) {
		return certType === 1 ? "2" : "3";
	} else {
		return "4";
	}
});
const logout = () => Auth.logout();
onShow(() => {
	Auth.GetUserInfo();
})

const accountList = computed(() => [
	{ label: "个人信息", url: "/packageSetting/mineDetail/userInfo" },
	{
		label: "联系手机",
		icon: !!UserInfo.value.phone,
		value: UserInfo.value.phone ?? "",
		phone: true,
	},
	{
		label: "实名状态",
		certStatus: UserInfo.value.certStatus,
		url: "/packageSetting/businessVerification/index",
	},
	{ label: "联系微信", value: UserInfo.value.openId ? "已绑定" : "未绑定" },
	{ label: "修改密码", url: "/packageSetting/mineDetail/changePassword" },
]);

const handlerClick = (row: TKeyValue) => {
	if (!row.url || row.certStatus === 3) return;
	let { url, label } = row;
	if (label === "实名状态" && row.certStatus !== 1)
		url = `/packageSetting/businessVerification/reviewStatus?status=${row.certStatus}&tip=${UserInfo.value.companyVerifyRemark}`;
	uni.navigateTo({ url });
};
</script>

<style lang="scss" scoped>
.content {
	padding-top: 182rpx;
	position: relative;

	&-bg {
		position: absolute;
		top: 0;
		width: 100%;
		height: 480rpx;
		height: 268rpx;
		display: block;
		z-index: 1;
		background: linear-gradient(180deg, #d7e2f6 0%, #f3f4f6 100%);
	}

	.pd {
		margin: 24rpx;
		position: relative;
		z-index: 2;
	}

	&-list {
		background-color: #fff;
		box-shadow: 0rpx 0rpx 12rpx 2rpx rgba(181, 181, 181, 0.16);
		border-radius: 16rpx;
		padding: 0 30rpx;

		&-item {
			border-bottom: 1px solid #f2f2f2;
			padding: 30rpx 0;

			&:last-child {
				border: none;
			}

			&-label {
				color: #333;
			}

			.phone {
				color: #83889d;
			}
		}
	}

	&-bottom {
		position: fixed;
		left: 24rpx;
		bottom: 96rpx;
		bottom: calc(96rpx + constant(safe-area-inset-bottom));
		bottom: calc(96rpx + env(safe-area-inset-bottom));
		width: calc(100% - 48rpx);
		padding: 18rpx 0;
		background: var(--themeColors);
		border-radius: 44rpx;
		color: #ffffff;
		font-family: PingFang SC, PingFang SC;
		font-weight: 500;
		font-size: 28rpx;
		line-height: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
}
</style>
