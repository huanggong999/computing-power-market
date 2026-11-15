<template>
	<view class="content between" :style="backgroundColor">
		<view
			class="content-item flex fz-28"
			:class="currentType === item.value ? 'active' : ''"
			:style="{ width: 100 / list.length + '%' }"
			v-for="(item, index) in list"
			:key="index"
			@click="handelCurrent(item.value)"
		>
			{{ item.label }}
			<view
				class="transition-container flex ml10"
				:class="currentType === item.value ? 'reverse' : ''"
			>
				<up-image
					width="40rpx"
					height="40rpx"
					:src="$ICON('/image/arrow.png')"
				/>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
interface IList {
	label: string;
	value: string;
}
const emit = defineEmits(["changeType"]);
const handelCurrent = (value: string) => emit("changeType", value);

const props = withDefaults(
	defineProps<{
		list: IList[];
		currentType: string;
		bgColor?: string;
	}>(),
	{
		bgColor: "#fff",
	}
);
const backgroundColor = computed(() => ({ backgroundColor: props.bgColor }));
</script>

<style lang="scss" scoped>
.content {
	padding: 24rpx;
	&-item {
		.transition-container {
			width: 30rpx;
			height: 30rpx;
			overflow: hidden;
			transition: all 0.5s ease;
		}
		.reverse {
			transform: rotate(180deg);
		}
	}
	.active {
		color: #3972fd;
	}
}
</style>
