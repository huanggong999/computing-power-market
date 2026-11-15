<template>
	<NavCom title="银行卡管理" bgColor="#fff" />
	<view class="content">
		<BankInfo v-if="!!bankData.bankUserName" :bankData="bankData" />
		<ComAddBank :bankData="bankData" v-else />
	</view>
</template>

<script setup lang="ts">
import BankInfo from "./components/BankInfo.vue";
import ComAddBank from "./components/ComAddBank.vue";

const bankData = ref<TKeyValue>({});

onLoad((options) => {
	bankData.value = JSON.parse(decodeURIComponent(options?.bankData));
});
</script>

<style lang="scss" scoped>
.content {
	padding: 206rpx 24rpx 24rpx;
}
</style>
