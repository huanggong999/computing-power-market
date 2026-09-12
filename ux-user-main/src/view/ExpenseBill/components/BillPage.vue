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
    :IsRefresh="false"
    :get-list="getList"
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
    <template #instanceId="row">
      <div>{{ row.instanceId }}</div>
      <div>{{ row.instanceName }}</div>
    </template>
  </ProTable>
</template>

<script setup lang="ts" name="BillPage">
import { getBillPageApi } from "@/api/bill";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
// 账单明细
const columns: ColumnProps[] = [
  { prop: "customerInfoQuery", label: "用户信息", width: 190, slot: true },
  { prop: "bill", label: "账期" },

  { prop: "billDate", label: "账期时间", width: 170 },
  {
    prop: "sourceType",
    label: "产品",
    value: (row) => enumType("resourceDiscountEnum", row.sourceType),
    width: 110,
  },
  {
    prop: "chargeType",
    label: "计费模式",
    width: 90,
    value: (row) => enumType("billingTypeEnum", row.chargeType),
  },
  {
    prop: "duration",
    label: "使用时长",
    width: 100,
    value: (row) =>
      row.duration ? ` ${row.duration}(${row.chargeUnit})` : "--",
  },
  { prop: "instanceId", label: "关联实例ID/名称", width: 160, slot: true },
  { prop: "unitId", label: "计费单元", width: 90 },
  { prop: "price", label: "产品平台单价（GiB/时）", width: 190 },
  { prop: "usage", label: "用量", width: 120 },
  { prop: "usageUnit", label: "用量单位", width: 100 },
  { prop: "originalPrice", label: "产品平台原价", width: 120 },
  { prop: "userDiscountAmount", label: "用户折扣金额", width: 120 },
  { prop: "voucherAmount", label: "代金券抵扣金额", width: 130 },
  { prop: "payPrice", label: "应付金额", width: 100 },
  { prop: "arrearsAmount", label: "欠费金额" },
  { prop: "balancePayAmount", label: "余额支付金额" },
  {
    prop: "payStatus",
    label: "支付状态",
    width: 90,
    value: (row) => enumType("orderStatusEnum", row.payStatus),
  },
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
  useTable({ requestApi: getBillPageApi });
</script>
<style lang="scss" scoped></style>
