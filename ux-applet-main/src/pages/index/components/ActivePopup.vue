<template>
	<u-popup
		:show="visible"
		:closeOnClickOverlay="false"
		mode="center"
		bgColor="transparent"
	>
		<view class="content">
			<view class="popup flex">
				<up-image
					width="546rpx"
					height="190rpx"
					:src="$ICON('/image/active_bg.png')"
					bgColor="transparent"
				/>
			</view>
			<view class="swiper">
				<view
					class="swiper-left"
					@click="changeCurrentNum('reduce')"
					v-if="length > 1"
				>
					<up-image
						width="36rpx"
						height="68rpx"
						:src="$ICON('/image/active_left.png')"
						bgColor="transparent"
					/>
				</view>
				<view>
					<up-swiper
						:list="props.activeList"
						keyName="picture"
						circular
						radius="16rpx"
						height="300rpx"
						width="100rpx"
						:current="currentNum"
						:autoplay="false"
					/>
				</view>
				<view
					class="swiper-right"
					@click="changeCurrentNum('add')"
					v-if="length > 1"
				>
					<up-image
						width="36rpx"
						height="68rpx"
						:src="$ICON('/image/active_right.png')"
						bgColor="transparent"
					/>
				</view>
			</view>

			<view class="close" @click="close">
				<up-image
					width="58rpx"
					height="58rpx"
					:src="$ICON('/image/active_close.png')"
					bgColor="transparent"
				/>
			</view>

			<view class="submit mt40" @click="learnMore"> 点击了解 </view>
		</view>
	</u-popup>
</template>

<script setup lang="ts">
const props = defineProps<{ visible: boolean; activeList: TKeyValue[] }>();
const length = computed(() => props.activeList.length);
// 加 | 减
const changeCurrentNum = (type: "add" | "reduce") => {
	const offset = type === "add" ? 1 : -1;
	currentNum.value =
		(currentNum.value + offset + length.value) % length.value;
};

const currentNum = ref(0);

const learnMore = () => {
	uni.navigateTo({
		url: `/packageActive/activeCenter/details?id=${
			props.activeList[currentNum.value].id
		}`,
	});
	close();
};

const emit = defineEmits(["close"]);
const close = () => emit("close");
</script>

<style lang="scss" scoped>
.content {
	position: relative;
	.popup {
		width: 594rpx;
	}
	.swiper {
		position: relative;
		&-left,
		&-right {
			position: absolute;
			top: 50%;
			transform: translateY(-50%);
		}
		&-left {
			left: -52rpx;
		}
		&-right {
			right: -52rpx;
		}
	}
	.submit {
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
		width: 320rpx;
		margin: auto;
	}
	.close {
		position: absolute;
		top: 0;
		right: -50rpx;
	}
}
</style>
