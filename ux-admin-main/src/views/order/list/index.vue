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
      <template #customerInfoQueryKey="row">
        <div>
          <div class="flex">
            <div class="label">名称 ：</div>
            {{ row.customerName ?? "--" }}
          </div>
          <div class="flex">
            <div class="label">手机号 ：</div>
            {{ row.customerPhone ?? "--" }}
          </div>
        </div>
      </template>
      <template #orderStatus="row">
        <el-tag :type="enumTag('orderStatusTag', row.orderStatus)">
          {{ enumType("orderStatusEnum", row.orderStatus) }}
        </el-tag>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="OrderList">
import { orderListApi } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { orderStatusSelectEnum, orderTypeSelectEnum } from "@/utils/selectEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({
  api: orderListApi,
  title: "订单",
  initParams: { productType: 1 },
});

const columns: ColumnProps[] = [
  {
    prop: "customerInfoQueryKey",
    label: "账户信息",
    width: 170,
    search: { el: "input" },
    slot: true,
  },
  { prop: "orderNo", label: "订单号", width: 230 },
  {
    prop: "orderType",
    label: "订单类型",
    value: (row) => enumType("orderTypeEnum", row.orderType),
    search: { el: "select" },
    enum: orderTypeSelectEnum,
    width: 130,
  },
  {
    prop: "productName",
    label: "产品名称",
    width: 170,
    value: (row) =>
      row.orderSourceList.map((item: any) => item.productName).join("、") ||
      "--",
  },
  { prop: "createTime", label: "创建时间", width: 170 },
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
  { prop: "payTime", label: "支付时间", width: 170 },
  {
    prop: "orderStatus",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: orderStatusSelectEnum,
    width: 100,
  },
  { prop: "originalPrice", label: "原价", width: 100 },
  { prop: "premiumPrice", label: "平台溢价金额", width: 130 },
  { prop: "userDiscountAmount", label: "用户折扣金额", width: 130 },
  { prop: "couponAmount", label: "优惠券满减金额", width: 130 },
  { prop: "voucherAmount", label: "代金券抵扣金额", width: 130 },
  { prop: "onlinePayAmount", label: "微信/支付宝支付金额", width: 180 },
  { prop: "finalPayAmount", label: "实付金额", width: 100 },
];
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
