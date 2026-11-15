<template>
	<view class="agic-card">
		<view class="info-card">
			<image class="img" :src="info.image" />

			<view class="info">
				<view class="title overflow-1">{{ info.name }}</view>
				<view class="price-group">
					<view class="price-item">
						<view class="label">售价：</view>
						<view class="price">
							￥{{ info.payPrice }}元/月
							<!-- <span class="line-price">￥{{ info.crossedPrice }}/天</span> -->
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
		<view class="btns">
			<up-button class="btn" type="primary" plain @click="consult"
				>立即咨询</up-button
			>
			<up-button class="btn" type="primary" @click="buy"
				>立即购买</up-button
			>
		</view>
	</view>
</template>

<script setup lang="ts">
const props = defineProps({
	info: {
		type: Object,
		default: () => {},
	},
});
const emits = defineEmits(["consult"]);
const consult = () => {
	emits("consult", props.info.id);
};
const buy = () => {
	uni.navigateTo({
		url: "/packageAgic/details/index?id=" + props.info.id,
	});
};
</script>

<style lang="scss" scoped>
.agic-card {
	height: 298rpx;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	margin-bottom: 30rpx;

	.info-card {
		display: flex;
		flex: 1;
		gap: 24rpx;

		.img {
			width: 274rpx;
			height: 184rpx;
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

	.btns {
		display: flex;
		justify-content: space-between;
		gap: 20rpx;

		.btn {
			width: 48%;
			height: 68rpx;
			border-radius: 8rpx;
			border: 1rpx solid #3972fd;
			display: flex;
			justify-content: center;
			align-items: center;
		}

		.contact {
			background-color: #ffffff;
			color: #3972fd;
		}

		.buy {
			color: #ffffff;
			background-color: #3972fd;
		}
	}
}
</style>
