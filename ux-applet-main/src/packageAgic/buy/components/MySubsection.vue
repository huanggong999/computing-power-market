<template>
	<view class="content flex">
		<view
			class="content-item flex"
			@click="handelCurrent(item)"
			v-for="item in list"
			:key="item.label"
			:class="{ active: currentType === item.value }"
		>
			{{ item.label }}
		</view>
		<view
			class="slider"
			:style="{
				transform: `translateX(${activeIndex * 100}%)`,
				width: sliderWidth,
			}"
		>
		</view>
	</view>
</template>

<script setup lang="ts">
import { useAuth } from "@/store";
const props = defineProps<{ type?: string }>();

const Auth = useAuth();
const list = [
	{ label: "包年包月", value: "POSTPAID_BY_MONTH" },
	{ label: "按天计费", value: "POSTPAID_BY_HOUR" },
];

const currentType = ref(props.type || "POSTPAID_BY_MONTH");

const emit = defineEmits(["changeType"]);
const handelCurrent = (row: TKeyValue) => {
	if (!Auth.checkLogin()) return;
	currentType.value = row.value;
	emit("changeType", row.value);
};
// 计算当前激活项的索引
const activeIndex = computed(() =>
	list.findIndex((item) => item.value === currentType.value)
);
// 计算滑块宽度（根据实际需求调整）
const sliderWidth = computed(() => 100 / list.length + "%");
</script>

<style lang="scss" scoped>
.content {
	font-size: 28rpx;
	background-color: #fff;
	border-radius: 16rpx;
	position: relative;

	&-item {
		padding: 16rpx 0;
		flex-shrink: 0;
		width: 50%;
		border-radius: 16rpx;
		position: relative;
		z-index: 1;
	}

	.active {
		color: #fff;
	}

	.slider {
		position: absolute;
		bottom: 0;
		left: 0;
		height: 100%;
		background: #3972fd;
		border-radius: 16rpx;
		transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
		z-index: 0;
	}
}
</style>
