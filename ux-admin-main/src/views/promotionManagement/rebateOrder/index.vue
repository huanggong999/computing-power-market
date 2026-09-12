<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :search-param="searchParam"
      :refreshFn="refreshFn"
      :getList="getList"
      :searchFn="searchFn"
      :resetFn="resetFn"
      type="none"
    >
      <template #userInfo="row">
        <div>{{ row.userName }}</div>
        <div>{{ row.userPhone }}</div>
      </template>
      <template #firstUserName="row">
        <div>{{ row.firstUserName ?? "--" }}</div>
        <div>{{ row.firstUserPhone ?? "--" }}</div>
      </template>
      <template #twoUserName="row">
        <div>{{ row.twoUserName ?? "--" }}</div>
        <div>{{ row.twoUserPhone ?? "--" }}</div>
      </template>
    </ProTable>
  </div>
</template>
<script setup lang="ts" name="RebateOrder">
import { promotionOrderPageApi } from "@/api/promotionManagement";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { rebateOrderLevelSelectEnum } from "@/utils/selectEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({ api: promotionOrderPageApi, title: "返佣订单" });

const columns: ColumnProps[] = [
  {
    prop: "userInfo",
    label: "用户信息",
    slot: true,
    fixed: "left",
    width: 150,
  },
  {
    prop: "level",
    label: "用户类型",
    search: { el: "select" },
    enum: rebateOrderLevelSelectEnum,
    value: (row) => enumType("rebateOrderLevelEnum", row.level),
    width: 100,
  },
  { prop: "orderNo", label: "订单号", width: 240 },
  { prop: "finalPayAmount", label: "订单金额", width: 120 },
  {
    prop: "productName",
    label: "产品名称",
    value: (row) => row.orders.map((item: any) => item.productName).join("、"),
    width: 150,
  },
  {
    prop: "orderType",
    label: "订单类型",
    value: (row) => enumType("orderTypeEnum", row.orderType),
    width: 140,
  },
  { prop: "firstUserName", label: "一级分销商", slot: true, width: 150 },
  {
    prop: "firstRate",
    label: "一级返佣比例",
    value: (row) => (row.firstRate ? row.firstRate + "%" : "--"),
    width: 120,
  },
  { prop: "firstUserCommission", label: "一级推广佣金", width: 120 },
  { prop: "twoUserName", label: "二级分销商", slot: true, width: 150 },
  {
    prop: "twoRate",
    label: "二级返佣比例",
    value: (row) => (row.twoRate ? row.twoRate + "%" : "--"),
    width: 120,
  },
  { prop: "twoUserCommission", label: "二级推广佣金", width: 120 },
  {
    prop: "payTime",
    label: "支付时间",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD",
    },
    width: 180,
  },
];
</script>
