<template>
  <div class="flx-justify-between">
    <TipText :content="title" class="mb10" />
    <SearchForm
      :columns="searchColumns"
      :searchParam="searchParam"
      :searchFn="searchFn"
      :resetFn="resetFn"
    />
  </div>
  <ProTable
    type="none"
    :columns="columns"
    :tableData="tableData"
    :pageData="pageData"
    :searchParam="searchParam"
    :IsRefresh="false"
    :get-list="getList"
    :searchFn="searchFn"
    :resetFn="resetFn"
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
import { getBillOverviewPageApi } from "@/api/bill";
import { useTable } from "@/hooks/useTable";
// 账单总览
const columns: ColumnProps[] = [
  { prop: "customerInfoQuery", label: "用户信息", width: 190, slot: true },
  { prop: "bill", label: "账期" },
  { prop: "premiumPrice", label: "所有产品平台原价" },
  // { prop: "premiumPrice", label: "平台溢价金额" },
  // { prop: "userDiscountAmount", label: "用户折扣金额" },
  { prop: "voucherAmount", label: "代金券抵扣金额" },
  //
  { prop: "payPrice", label: "应付金额" },
  { prop: "arrearsAmount", label: "欠费金额" },
  { prop: "balancePayAmount", label: "余额支付金额" },
  { prop: "status", label: "状态" },
];
const searchColumns: ColumnProps[] = [
  { prop: "customerInfoQuery", label: "用户信息", search: { el: "input" } },
  {
    prop: "startDate",
    label: "账务日期",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startDate", "endDate"],
      valueFormat: "YYYY-MM-DD",
    },
  },
];

defineProps<{ title: string }>();

const { tableData, pageData, getList, searchParam, searchFn, resetFn } =
  useTable({ requestApi: getBillOverviewPageApi });
</script>
<style lang="scss" scoped></style>
