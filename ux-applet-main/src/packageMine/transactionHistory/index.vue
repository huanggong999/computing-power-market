<template>
	<NavCom title="收支明细" bgColor="#fff" />
	<view class="content">
		<view class="content-dropdown">
			<MyDropdown
				:list="DROPDOWN_OPTIONS"
				:currentType="activeFilterType"
				@changeType="handleFilterChange"
			/>
		</view>
		<view class="content-price flex-1">
			<view class="pt40 pb40 text-center">
				<view class="fwb fz-40 lh-56">
					{{ dataForm.incomeTotal }} 元
				</view>
				<view class="fz-26 lh-36 mt8"> 收入 </view>
			</view>
			<view class="content-price-line" />
			<view class="pt40 pb40 text-center">
				<view class="fwb fz-40 lh-56">
					{{ dataForm.outTotal }} 元
				</view>
				<view class="fz-26 lh-36 mt8"> 支出 </view>
			</view>
		</view>
		<view class="content-history">
			<view class="fwb fz-30 lh-42 mb20"> 收支明细 </view>
			<view class="list">
				<view
					class="mb20"
					v-for="(item, index) in dataForm.billList"
					:key="index"
				>
					<HistoryList :row="item" />
				</view>
				<MyEmpty
					text="暂无明细"
					icon="/mine/transactionHistory_empty.png"
					loadText="没有更多了"
					:IsEmpty="dataForm.billList.length !== 0"
				/>
			</view>
		</view>

		<up-picker
			:show="showPicker"
			:columns="columns"
			keyName="label"
			:defaultIndex="defaultIndex"
			@close="cancelPicker"
			@cancel="cancelPicker"
			@confirm="confirmPicker"
		/>
		<up-datetime-picker
			:show="showDatetimePicker"
			v-model="dataValue"
			mode="year-month"
			@close="cancelDatetimePicker"
			@cancel="cancelDatetimePicker"
			@confirm="confirmDatetimePicker"
		/>
	</view>
</template>

<script setup lang="ts">
import dayjs from "dayjs";

import HistoryList from "./components/HistoryList.vue";
import { consoleIncomeList } from "@/api/model/mine";

const DROPDOWN_OPTIONS = ref([
	{ label: "本月", value: "month" }, // 选择月份
	{ label: "全部", value: "transactionType" }, // 收入 和 支出
]);

// 工具方法
const updateDropdownLabel = (index: number, value: string) => {
	DROPDOWN_OPTIONS.value[index].label =
		index === 0 && value === dayjs().format("YYYY-MM") ? "本月" : value;
};

const activeFilterType = ref("");
const dataForm = ref<TKeyValue>({ incomeTotal: 0, outTotal: 0, billList: [] });
const params = ref<TKeyValue>({});
const getData = async () => {
	const { data } = await consoleIncomeList(params.value);
	Object.assign(dataForm.value, {
		incomeTotal: data.incomeTotal || 0,
		outTotal: data.outTotal || 0,
		billList: data.billList || [],
	});
};

const handleFilterChange = (type: string) => {
	activeFilterType.value = type;
	type === "month" ? showDatePicker() : showTypePicker();
};

//#region  datetime picker
const showDatetimePicker = ref(false);
const dataValue = ref(Date.now());
const cancelDatetimePicker = () => (showDatetimePicker.value = false);
const showDatePicker = () => (showDatetimePicker.value = true);
// 时间格式化
const time = computed(() => dayjs(dataValue.value).format("YYYY-MM"));

const confirmDatetimePicker = () => {
	params.value.month = time.value;
	updateDropdownLabel(0, time.value);
	getData();
	cancelDatetimePicker();
};

//#endregion

//#region  picker
const defaultIndex = ref([0]);
const showPicker = ref(false);
const cancelPicker = () => (showPicker.value = false);
const showTypePicker = () => (showPicker.value = true);
const columns = reactive([
	[
		{ label: "全部", value: "all" },
		{ label: "充值", value: "RECHARGE" },
		{ label: "平台发放", value: "PLATFORM_GRANT" },
		{ label: "退款", value: "REFUND" },
		{ label: "支付抵扣", value: "PAY_DISCOUNT" },
	],
]);
const confirmPicker = ({ indexs, value }: TKeyValue) => {
	defaultIndex.value = [indexs[0]];
	updateDropdownLabel(1, value[0].label);
	showPicker.value = false;
	if (value[0].value === "all") {
		delete params.value.transactionType;
	} else {
		params.value.transactionType = value[0].value;
	}
	getData();
};

//#endregion

onLoad(() => getData());
</script>

<style lang="scss" scoped>
.content {
	padding-top: 172rpx;
	&-price {
		background-color: #fff;
		justify-content: space-evenly;
		&-line {
			width: 2rpx;
			height: 68rpx;
			background: #e5e5e5;
		}
	}
	&-history {
		padding: 30rpx 24rpx;
		.list {
			height: 1050rpx;
			overflow: auto;
		}
	}
}
</style>
