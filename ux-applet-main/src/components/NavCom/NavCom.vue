<!--
 * @Author: zhengyc sjku2al@lingyangtech.com
 * @Date: 2025-03-10 10:44:43
 * @LastEditors: zhengyc sjku2al@lingyangtech.com
 * @LastEditTime: 2025-03-10 14:25:20
 * @FilePath: \ai-intelligence-applet\src\components\NavCom\NavCom.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
	<u-navbar :fixed="fixed" :titleStyle="titleStyle" :title="title" :placeholder="placeholder" :bgColor="bgColor"
		@leftClick="back" :leftIconColor="leftIconColor" style="z-index:100">
		<template #left>
			<up-icon name="home" size="22" :color="leftIconColor" v-if="isFirst" />
			<up-icon name="arrow-left" size="20" :color="leftIconColor" v-else />
		</template>
	</u-navbar>
</template>

<script setup>
const props = defineProps({
	title: {
		type: String,
		default: "",
	},
	fixed: {
		type: Boolean,
		default: true,
	},
	placeholder: {
		type: Boolean,
		default: false,
	},
	leftIconColor: {
		type: String,
		default: "#303133",
	},
	titleColor: {
		type: String,
		default: "#000000",
	},
	bgColor: {
		type: String,
		default: "transparent",
	},
});
const titleStyle = computed(() => {
	return {
		fontWeight: 500,
		fontSize: "34rpx",
		color: props.titleColor,
	};
});
const isFirst = computed(() => uni.$u.pages().length === 1);
const back = () => {
	if (uni.$u.pages().length === 1) {
		uni.switchTab({ url: "/pages/index/index" });
		return;
	}
	uni.navigateBack();
};
</script>

<style lang="scss" scoped></style>
