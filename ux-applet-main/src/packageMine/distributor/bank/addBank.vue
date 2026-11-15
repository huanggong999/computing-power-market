<template>
	<NavCom title="添加银行卡" bgColor="#fff" />
	<view class="content">
		<view class="fwb fz-40 lh-56 mb12"> 请输入您的银行卡信息 </view>
		<up-image
			width="702rpx"
			height="125rpx"
			:src="$ICON('/mine/bank_tip.png')"
			bgColor="transparent"
		/>

		<view class="content-bankInfo mt60">
			<view
				class="content-bankInfo-item between fz-28 lh-40"
				v-for="(item, index) in bankInfoList"
				:key="index"
			>
				<view class="label"> {{ item.label }} </view>
				<up-input
					:customStyle="customStyle"
					v-model="dataForm[item.value]"
					:placeholder="`请输入${item.label}`"
					border="none"
					inputAlign="right"
				/>
			</view>
		</view>
		<view class="content-changeBank flex mt60 fz-26 lh-36" @click="save">
			保存
		</view>
	</view>
</template>

<script setup lang="ts">
import { extendUpdateBankApi } from "@/api/model/mine";
import { myShowToast } from "@/utils";

const bankInfoList = ref([
	{ label: "持卡人", value: "bankUserName" },
	{ label: "卡号", value: "bankNo" },
	{ label: "所在支行", value: "bank" },
	{ label: "身份证号", value: "idCard" },
	{ label: "开户地址", value: "address" },
]);
const customStyle = reactive({
	background: "transparent",
	padding: "32rpx",
});
const dataForm = ref<TKeyValue>({});
// 保存
const save = async () => {
	await extendUpdateBankApi(dataForm.value);
	const encodedData = encodeURIComponent(JSON.stringify(dataForm.value));
	myShowToast("银行卡信息保存成功", "success");
	uni.$u.sleep(2000).then(() => {
		uni.redirectTo({
			url: `/packageMine/distributor/bank/cardManagement?bankData=${encodedData}`,
		});
	});
};

onLoad((options) => {
	dataForm.value = JSON.parse(decodeURIComponent(options?.bankData));
});
</script>

<style lang="scss" scoped>
.content {
	padding: 242rpx 24rpx 24rpx;
	&-bankInfo {
		padding: 8rpx 28rpx;
		background-color: #fff;
		border-radius: 24rpx;
		&-item {
			padding: 32rpx 0 0;
			border-bottom: 2rpx solid #e5e5e5;
			&:last-child {
				border-bottom: none;
			}
		}
	}
	&-changeBank {
		padding: 22rpx;
		color: #fff;
		background: #3972fd;
		border-radius: 44rpx;
	}
}
</style>
