<template>
  <ProTable
    type="none"
    :columns="columns"
    :tableData="tableData"
    :pageData="pageData"
    :searchParam="searchParam"
    :get-list="getList"
    :searchFn="searchFn"
    :resetFn="resetFn"
    :refreshFn="refreshFn"
    :maxHeight="500"
  />
</template>

<script setup lang="ts" name="BillOverview">
import { getBillOverviewPageApi } from "@/api/financialCenter";
import { useTable } from "@/hooks/useTable";

// 账单总览
const columns: ColumnProps[] = [
  { prop: "bill", label: "账期", width: 110 },
  {
    prop: "startDate",
    label: "账务日期",
    noShow: true,
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startDate", "endDate"],
      valueFormat: "YYYY-MM-DD",
    },
  },
  { prop: "originalPrice", label: "所有产品平台原价", width: 150 },
  { prop: "premiumPrice", label: "平台溢价金额", width: 130 },
  { prop: "userDiscountAmount", label: "用户折扣金额", width: 130 },
  { prop: "voucherAmount", label: "代金券抵扣金额", width: 130 },
  { prop: "creditLineAmount", label: "授信额抵扣金额", width: 130 },
  { prop: "payPrice", label: "应付金额", width: 130 },
  { prop: "arrearsAmount", label: "欠费金额", width: 130 },
  { prop: "balancePayAmount", label: "余额支付金额", width: 130 },
  { prop: "status", label: "状态", width: 90, fixed: "right" },
];
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
  api: getBillOverviewPageApi,
  initParams: { customerId: props.customerId },
});
</script>
<style lang="scss" scoped></style>
