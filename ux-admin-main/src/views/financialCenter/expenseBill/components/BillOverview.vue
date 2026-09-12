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
  >
    <template #customerInfoQuery="row">
      <div>
        <div class="flex">
          <div class="label">名称 ：</div>
          {{ row.customerName ?? "--" }}
        </div>
        <div class="flex">
          <div class="label">手机号 ：</div>
          {{ row.phone ?? "--" }}
        </div>
      </div>
    </template>
  </ProTable>
</template>

<script setup lang="ts" name="BillOverview">
import { getBillOverviewPageApi } from "@/api/financialCenter";
import { useTable } from "@/hooks/useTable";
// 账单总览
const columns: ColumnProps[] = [
  {
    prop: "customerInfoQuery",
    label: "用户信息",
    width: 190,
    search: { el: "input" },
    slot: true,
  },
  { prop: "bill", label: "账期", width: 100 },
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
  { prop: "originalPrice", label: "所有产品平台原价", width: 180 },
  { prop: "premiumPrice", label: "平台溢价金额", width: 180 },
  { prop: "userDiscountAmount", label: "用户折扣金额", width: 180 },
  { prop: "voucherAmount", label: "代金券抵扣金额", width: 180 },
  { prop: "creditLineAmount", label: "授信额抵扣金额", width: 180 },
  { prop: "payPrice", label: "应付金额", width: 180 },
  { prop: "arrearsAmount", label: "欠费金额", width: 120 },
  { prop: "balancePayAmount", label: "余额支付金额", width: 180 },
  { prop: "status", label: "状态", width: 110 },
];

const {
  tableData,
  pageData,
  getList,
  searchParam,
  searchFn,
  resetFn,
  refreshFn,
} = useTable({ api: getBillOverviewPageApi });
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
  .label {
    width: 65px;
    flex-shrink: 0;
    text-align: right;
  }
}
</style>
