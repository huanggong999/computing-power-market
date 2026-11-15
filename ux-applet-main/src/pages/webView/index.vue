<template>
	<view>
		<web-view :src="url"></web-view>
	</view>
</template>

<script setup lang="ts">
const url = ref('')
onLoad((options) => {
	url.value = options?.url
})
</script>

<style lang="scss" scoped></style>
