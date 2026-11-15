<template>
	<view class="content">
		<view
			class="content-item fz-26 lh-36"
			v-for="(item, index) in list"
			:key="index"
		>
			<view class="content-item-orderNo pb16 between">
				<view> 订单号：{{ item.orderNo }} </view>
				<view
					:style="{color: statusEnum[item.settlementStatus as IStatusEnum].color}"
				>
					{{ statusEnum[item.settlementStatus as IStatusEnum].label }}
				</view>
			</view>
			<view class="content-item-info mt24">
				<view class="content-item-info-user flex-1">
					<image
						class="content-item-info-user-avatar"
						:src="item.firstAvatar || item.twoAvatar"
					/>
					<view class="ml16 content-item-info-user-right">
						<view class="between">
							<view> {{ item.userName }} </view>
							<view class="tip">
								订单金额：<text class="num fwb"
									>￥{{ item.finalPayAmount }}</text
								>
							</view>
						</view>
						<view class="between mt4">
							<view class="tip"> {{ item.payTime }} </view>
							<view class="tip">
								佣金：<text class="num commission fwb"
									>￥{{ item.firstUserCommission }}</text
								>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{ list: TKeyValue[] }>();

const statusEnum = reactive({
	1: { label: "待结算", color: "#FF4151" },
	2: { label: "已完成", color: "#83889D" },
});
type IStatusEnum = keyof typeof statusEnum;
</script>

<style lang="scss" scoped>
.content {
	&-item {
		padding: 20rpx 24rpx 24rpx;
		background: #ffffff;
		border-radius: 16rpx;
		&-orderNo {
			border-bottom: 2rpx solid #e5e5e5;
			&-status {
				color: #83889d;
			}
		}
		&-info {
			&-user {
				&-avatar {
					width: 76rpx;
					height: 76rpx;
					border-radius: 50%;
				}
				&-right {
					flex: 1;
					.num {
						color: #000000;
					}
					.commission {
						color: #3972fd;
					}
				}
			}
		}
		.tip {
			color: #7f7f7f;
		}
	}
}
</style>
