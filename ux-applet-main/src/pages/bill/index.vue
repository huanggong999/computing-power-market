<template>
	<view class="content">
		<MyNavTitle title="账单" />
		<view class="mb24"> <MySubsection @changeType="changeType" /> </view>
		<view class="between mb24">
			<view
				class="all fz-26 lh- 36"
				:class="{ activeAll: isAll }"
				@click="changeAll"
			>
				全部
			</view>

			<view class="fz-28 lh-40 flex" @click="changeDropdownType">
				{{ isAll ? "全部账单" : month }}
				<view class="ml10">
					<up-image
						width="40rpx"
						height="40rpx"
						:src="$ICON('/image/arrow.png')"
					/>
				</view>
			</view>
		</view>
		<BillOverview
			v-if="['billOverview'].includes(currentType)"
			:month="month"
			ref="billOverviewRef"
		/>
		<ResourceBill
			v-if="['resourceBill'].includes(currentType)"
			:month="month"
			ref="resourceBillRef"
		/>
	</view>

	<up-datetime-picker
		:show="showDatetimePicker"
		v-model="dataValue"
		mode="year-month"
		@close="cancelDatetimePicker"
		@cancel="cancelDatetimePicker"
		@confirm="confirm"
	/>
	<MyTabbar />
</template>

<script setup lang="ts">
import dayjs from "dayjs";
import BillOverview from "./components/BillOverview.vue";
import MySubsection from "./components/MySubsection .vue";
import ResourceBill from "./components/ResourceBill.vue";
import { useAuth } from "@/store";
import onShare from "@/hook/useShare";

type TListType = "billOverview" | "resourceBill";
const currentType = ref<TListType>("billOverview");
const changeType = (type: TListType) => {
	currentType.value = type;
};

const changeDropdownType = () => (showDatetimePicker.value = true);

const isAll = ref(true);

//#region  datetime picker
const showDatetimePicker = ref(false);
const dataValue = ref(Date.now());
const cancelDatetimePicker = () => (showDatetimePicker.value = false);
// 时间格式化
const time = computed(() => dayjs(dataValue.value).format("YYYY-MM"));

const month = ref("");
const confirm = () => {
	isAll.value = false;
	month.value = time.value;
	cancelDatetimePicker();
};
const changeAll = () => {
	isAll.value = true;
	month.value = "";
};

const { onShareAppMessage, onShareTimeline } = onShare("/pages/bill/index");

//#endregion
const Auth = useAuth();
const billOverviewRef = ref();
const resourceBillRef = ref();
onShow(() => {
	if (!!Auth.TOKEN && currentType.value === "billOverview")
		billOverviewRef.value?.searchFn();
	if (!!Auth.TOKEN && currentType.value === "resourceBill")
		resourceBillRef.value.searchFn();
});
</script>

<style lang="scss" scoped>
.content {
	background: linear-gradient(180deg, #e6eefe 0%, #f3f4f6 100%);
	padding: 0 24rpx;
	padding-top: 206rpx;
	.all {
		padding: 4rpx 16rpx;
		border-radius: 8rpx;
		background-color: #fff;
		color: #83889d;
	}
	.activeAll {
		background: #fff;
		color: #3972fd;
		background: #d7e2f6;
	}
}
</style>
