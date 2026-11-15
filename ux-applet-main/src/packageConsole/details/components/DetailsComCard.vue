<template>
	<view class="content">
		<view class="content-title tip pd item fz-26"> {{ title }} </view>
		<view class="content-list">
			<view
				class="item"
				:class="{ noneBorder }"
				v-for="item in list"
				:key="item.key"
			>
				<view
					class="pd between"
					:class="{ start: noneBorder }"
					v-if="!item.color"
				>
					<view class="item-key" :class="{ tip: noneBorder }">
						{{ item.label }}
					</view>
					<view :class="{ tip: !noneBorder }">
						{{ item.value }}
					</view>
				</view>
				<view class="progress pt30 pb18" v-if="!!item.color">
					<view class="text mb24 between">
						<view class="item-key"> 内存（GiB） </view>
						<view :style="{ color: item.color }">
							{{ item.value }}%
						</view>
					</view>
					<up-line-progress
						:percentage="item.value"
						:activeColor="item.color"
					/>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
defineProps<{
	list: TKeyValue[];
	title: string;
	noneBorder?: boolean;
}>();
</script>

<style lang="scss" scoped>
.content {
	padding: 0 24rpx;
	border-radius: 16rpx;
	background-color: #fff;
	font-size: 30rpx;
	.tip {
		color: #83889d;
	}
	.pd {
		padding: 30rpx 0;
	}
	.item {
		border-bottom: 1px solid #f3f4f6;
		&:last-child {
			border-bottom: none;
		}
		.between {
			align-items: start;
		}
		.start {
			justify-content: start;
		}
		&-key {
			width: 50%;
			flex-shrink: 0;
		}
	}
	.noneBorder {
		border-bottom: none;
	}
}
</style>
