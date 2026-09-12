<template>
  <ProTable
    :columns="columns"
    :tableData="tableData"
    :pageData="pageData"
    :search-param="searchParam"
    :refreshFn="refreshFn"
    :getList="getList"
    :searchFn="searchFn"
    :resetFn="resetFn"
    :maxHeight="500"
    type="none"
  />
</template>

<script setup lang="ts" name="Balance">
import { orderListApi } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { orderStatusSelectEnum } from "@/utils/selectEnum";

const props = defineProps<{ customerId: string | number | undefined }>();
const {
  tableData,
  pageData,
  getList,
  searchParam,
  searchFn,
  resetFn,
  refreshFn,
} = useTable({
  api: orderListApi,
  initParams: { customerId: props.customerId, orderType: "BALANCE" },
});
const columns: ColumnProps[] = [
  { prop: "orderNo", label: "单号", width: 230 },
  {
    prop: "onlinePayType",
    label: "充值方式",
    value: (row: any) => enumType("payTypeEnum", row.onlinePayType),
  },
  { prop: "createTime", label: "创建时间" },
  {
    prop: "createTime",
    label: "日期",
    noShow: true,
    search: {
      el: "date-picker",
      dateType: "datetimerange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD HH:mm:ss",
    },
  },
  { prop: "payTime", label: "支付时间" },
  {
    prop: "orderStatus",
    label: "状态",
    value: (row) => enumType("orderStatusEnum", row.orderStatus),
    search: { el: "select" },
    enum: orderStatusSelectEnum,
  },
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
<style lang="scss" scoped></style>
