<template>
	<view class="content">
		<view class="fwb fz-26 lh-36">活动赠送代金券</view>
		<view
			class="content-card mt20"
			:style="backgroundImage"
			v-for="(item, index) in list"
			:key="index"
		>
			<view class="content-card-info between">
				<view class="flex ml34 content-card-info-amount">
					<view class="fwb fz-60 lh-84"> {{ "10" }} </view>
					<view> 元 </view>
				</view>
				<view class="mr12 content-card-info-tip">
					<view class="fwb fz-32 lh-44">
						{{ "充值赠送券" }}
					</view>
					<view class="content-card-info-valid fz-24 lh-34 mt12">
						有效期至{{ item.couponEndTime }}
					</view>
				</view>
			</view>
			<view class="content-card-tip mt68 fz-24 lh-34">
				充值额度满 {{ item.rechargeAmount }} 即自动赠送
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { buildIcon } from "@/utils/icon";

const backgroundImage = computed(() => ({
	backgroundImage: `url(${buildIcon("/mine/voucher_bg.png")})`,
}));

defineProps<{ list: TKeyValue[] }>();
</script>

<style lang="scss" scoped>
.content {
	padding-bottom: 170rpx;
	&-card {
		height: 264rpx;
		padding: 48rpx 16rpx 24rpx 30rpx;
		background-repeat: no-repeat;
		background-size: 100% 100%;
		box-sizing: border-box;

		&-info {
			&-amount {
				align-items: baseline;
				color: #fba201;
			}
			&-tip {
				text-align: right;
			}
			&-valid {
				color: #999999;
			}
		}
		&-tip {
			color: #666666;
		}
	}
}
</style>
