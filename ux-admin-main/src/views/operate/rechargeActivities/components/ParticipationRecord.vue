<template>
  <el-dialog
    v-model="model"
    :title="'参与记录'"
    width="70%"
    center
    :destroy-on-close="true"
    :before-close="() => emit('update:modelValue', false)"
  >
    <ProTable
      type="none"
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :searchParam="searchParam"
      :searchFn="searchFn"
      :resetFn="resetFn"
      :refreshFn="refreshFn"
      :max-height="500"
      :getList="getList"
    >
      <template #customerInfoQueryKey="row">
        <div class="flex"><span>用户名：</span> {{ row.customerName }}</div>
        <div class="flex"><span>手机号：</span>{{ row.customerPhone }}</div>
      </template>
    </ProTable>
  </el-dialog>
</template>

<script setup lang="ts" name="ParticipationRecord">
import { orderListApi } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { useVModel } from "@/utils/useVModel";

const props = defineProps<{
  modelValue: boolean;
  rechargeId: string | number;
}>();
const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);

const {
  tableData,
  pageData,
  searchParam,
  searchFn,
  resetFn,
  getList,
  refreshFn,
  totalParam,
} = useTable({ api: orderListApi, requestAuto: false });

watch(model, (val) => {
  totalParam.value.rechargeId = props.rechargeId;
  if (val) getList();
});
const columns: ColumnProps[] = [
  {
    prop: "customerInfoQueryKey",
    label: "使用账户",
    search: { el: "input" },
    slot: true,
  },
  { prop: "orderNo", label: "单号" },
  {
    prop: "onlinePayType",
    label: "充值方式",
    value: (row: any) => enumType("payTypeEnum", row.onlinePayType),
  },
  { prop: "payTime", label: "充值时间" },
  { prop: "originalPrice", label: "充值金额" },

  {
    prop: "rewardsType",
    label: "奖励类型",
    value: (row: any) => enumType("rechargeRewardTypeEnum", row.rewardsType),
  },
  { prop: "rechargeName", label: "奖励活动名称" },
  { prop: "rewardsAmount", label: "奖励金额" },
  { prop: "rewardsTime", label: "奖励时间" },
];
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
}
</style>
