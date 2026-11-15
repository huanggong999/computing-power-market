<template>
	<view class="select-card" v-if="props.type == 'date'">
		<up-checkbox :disabled="disabled" shape="circle" :value="data.value" @change="handleChange" />
		<view class="info">
			<view class="name">{{ data.bill }}</view>
			<view class="amount-list">
				<view class="amount-item">
					<view class="label">可开票金额</view>
					<view class="value">{{ data.amount }}</view>
				</view>
				<view class="amount-item">
					<view class="label">可开票总额</view>
					<view class="value">{{ data.totalAmount }}</view>
				</view>
			</view>

		</view>
	</view>
	<view class="time-select-card" v-if="props.type == 'time'">
		<view class="select-card2">
			<up-checkbox :disabled="disabled" shape="circle" :value="data.value" @change="handleChange" />
			<view class="info">
				<view class="name">{{ data.bill }}</view>
				<view class="amount-list">
					<view class="amount-item">
						<view class="label">可开票金额</view>
						<view class="value">{{ data.amount }}</view>
					</view>
					<view class="amount-item">
						<view class="label">可开票总额</view>
						<view class="value">{{ data.totalAmount }}</view>
					</view>
				</view>
			</view>
			<view class="time">{{ data.bill }}</view>
		</view>
		<view class="info-card">
			<view class="info-item">
				<view class="label">产品</view>
				<view class="value">{{ data.productName }}</view>
			</view>
			<view class="info-item">
				<view class="label">开通时间</view>
				<view class="value">{{ data.startDate ? data.startDate + '-' + data.endDate : '--' }}</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { defineProps } from "vue";
const emit = defineEmits(["change"]);
const props = defineProps({
	data: {
		type: Object,
		default: () => { },
	},
	type: {
		type: String,
		default: () => { },
	},
});
const disabled = computed(() => props.data.tag == 1)
const handleChange = (e: any) => {

	emit("change", {
		value: props.data.billIds,
		amount: props.data.amount,
		selected: e,
	});
};
</script>

<style lang="scss" scoped>
/* 账期 */
.select-card {
	padding: 30rpx 24rpx;
	display: grid;
	grid-template-columns: 50rpx 1fr 100rpx;
	gap: 24rpx;
	height: 160rpx;
	width: 87vw;
	background: #FFFFFF;
	border-radius: 16rpx 16rpx 16rpx 16rpx;
	margin-bottom: 20rpx;

	.info {
		display: flex;
		flex-direction: column;
		gap: 30rpx;

		.name {

			font-weight: bold;
			font-size: 30rpx;

		}

		.amount-list {
			display: flex;
			align-items: center;
			justify-content: space-between;

			.amount-item {
				display: flex;
				flex-direction: column;
				gap: 8rpx;

				.label {

					font-weight: 400;
					font-size: 28rpx;
					color: #83889D;

				}

				.value {

					font-weight: 400;
					font-size: 28rpx;

				}
			}
		}

	}

	.time {
		font-weight: 400;
		font-size: 24rpx;
		color: #83889D;
	}
}

/* 消费时间 */
.time-select-card {
	display: grid;
	grid-template-rows: 1fr 122rpx;

	margin-bottom: 20rpx;

	.select-card2 {
		padding: 30rpx 24rpx;
		display: grid;
		grid-template-columns: 50rpx 1fr 100rpx;
		gap: 24rpx;
		height: 160rpx;
		width: 87vw;
		background: #FFFFFF;
		border-radius: 16rpx 16rpx 0 0;

		.info {
			display: flex;
			flex-direction: column;
			gap: 30rpx;

			.name {

				font-weight: bold;
				font-size: 30rpx;

			}

			.amount-list {
				display: flex;
				align-items: center;
				justify-content: space-between;

				.amount-item {
					display: flex;
					flex-direction: column;
					gap: 8rpx;

					.label {

						font-weight: 400;
						font-size: 28rpx;
						color: #83889D;

					}

					.value {

						font-weight: 400;
						font-size: 28rpx;

					}
				}
			}

		}

		.time {
			font-weight: 400;
			font-size: 24rpx;
			color: #83889D;
		}
	}

	.info-card {
		display: flex;
		flex-direction: column;
		padding: 20rpx 24rpx;
		gap: 14rpx;
		background: #F7F8FB;
		border-radius: 0 0 16rpx 16rpx;

		.info-item {
			display: flex;
			align-items: center;
			gap: 24rpx;

			.label {
				min-width: 100rpx;

				font-weight: 400;
				font-size: 24rpx;
				color: #83889D;

			}

			.value {

				font-weight: 400;
				font-size: 24rpx;
				color: #000000;

			}
		}
	}
}
</style>
