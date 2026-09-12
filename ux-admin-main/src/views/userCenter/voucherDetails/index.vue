<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :refreshFn="refreshFn"
      :getList="getList"
      type="none"
    />
  </div>
</template>

<script setup lang="ts" name="VoucherDetails">
import { amountPageApi } from "@/api/userCenter";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";

const { tableData, pageData, refreshFn, getList } = useTable({
  api: amountPageApi,
  initParams: { amountType: "VOUCHER" },
});

const columns: ColumnProps[] = [
  { prop: "customerName", label: "客户名称" },
  { prop: "phone", label: "手机号" },
  {
    prop: "transactionType",
    label: "交易类型",
    value: (row: any) => enumType("couponTradeTypeEnum", row.transactionType),
  },
  { prop: "beforeAmount", label: "操作前金额" },
  { prop: "amount", label: "操作金额" },
  { prop: "afterAmount", label: "剩余金额" },
  { prop: "createTime", label: "操作时间" },
  { prop: "orderNo", label: "使用订单号" },
];
</script>
<style lang="scss" scoped></style>
