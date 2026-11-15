<template>
	<view class="content">
		<view class="content-title fz-28 fwb">
			<view>{{ currentItem.currentCard.title }}</view>
			<image
				v-if="props.currentItem.showIcon"
				class="content-title-icon"
				:src="$ICON('/mine/arrow_right.png')"
			/>
		</view>

		<view class="content-info mt16">
			<view class="content-info-number">
				<view class="content-info-number-num fz-68 fwb">
					{{ enumValue(currentItem.currentCard.count) }}
				</view>
				<view class="fz-28 mt8">
					{{ currentItem.currentCard.label }}
				</view>
			</view>
			<view
				class="content-info-status"
				:class="{
					'content-info-storage': currentItem.type === 'IsStorage',
				}"
			>
				<view
					class="content-info-status-item fz-26"
					v-for="(item, index) in currentItem.currentCard.status"
					:key="index"
				>
					<view class="content-info-status-item-tip">
						{{ item.tip }}
					</view>
					<view class="num ml24" :style="{ color: item.color }">
						{{ enumValue(item.num) }}
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import type { ConsoleCardItem } from "../type";

const props = defineProps<{ currentItem: ConsoleCardItem }>();

const enumValue = (value: string | number) => value ?? "--";
</script>

<style lang="scss" scoped>
.content {
	padding: 24rpx 14rpx 28rpx 24rpx;
	background-color: #fff;
	border-radius: 24rpx;
	position: relative;
	z-index: 2;

	&-title {
		display: flex;
		align-items: center;
		justify-content: space-between;

		&-icon {
			width: 40rpx;
			height: 40rpx;
		}
	}

	&-info {
		display: flex;
		padding: 0 52rpx 0 38rpx;

		&-number {
			flex: 1;

			&-num {
				height: 96rpx;
				line-height: 96rpx;
				color: #3972fd;
			}
		}

		&-status {
			flex: 2;
			display: flex;
			flex-wrap: wrap;

			&-item {
				width: 50%;
				display: flex;
				align-items: center;

				&-tip {
					color: #83889d;
				}
			}
		}

		&-storage {
			flex-direction: column;
			justify-content: space-evenly;

			.content-info-status-item {
				width: 100%;
			}
		}
	}
}
</style>
