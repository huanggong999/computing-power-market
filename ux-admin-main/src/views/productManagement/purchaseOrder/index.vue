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
      <template #agiCustomer="row">
        <div>
          <div class="flex">
            <div class="label">名称 ：</div>
            {{ row.agiCustomerName ?? "--" }}
          </div>
          <div class="flex">
            <div class="label">手机号 ：</div>
            {{ row.mobile ?? "--" }}
          </div>
        </div>
      </template>
      <template #orderStatus="row">
        <el-tag :type="enumTag('orderStatusTag', row.orderStatus)">
          {{ enumType("orderStatusEnum", row.orderStatus) }}
        </el-tag>
      </template>
      <template #actualStatus="row">
        <el-tag :type="enumTag('productStatusTag', row.actualStatus)">
          {{ enumType("productStatusEnum", row.actualStatus) }}
        </el-tag>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="purchaseOrder">
import { orderListApi } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { productStatusSelectEnum } from "@/utils/selectEnum";

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
  initParams: { orderType: "PRODUCT", orderStatus: "PAID" },
});
const columns: ColumnProps[] = [
  {
    prop: "customerInfoQueryKey",
    label: "账户信息",
    width: 200,
    search: { el: "input" },
    slot: true,
  },
  { prop: "agiCustomer", label: "客户信息", width: 200, slot: true },

  {
    prop: "agiCustomerName",
    label: "客户名称",
    noShow: true,
    search: { el: "input" },
  },
  { prop: "mobile", label: "联系方式", noShow: true, search: { el: "input" } },
  { prop: "orderNo", label: "订单号", width: 230, search: { el: "input" } },

  {
    prop: "productName",
    label: "产品名称",
    width: 170,
    value: (row) =>
      row.orderSourceList.map((item: any) => item.productName).join("、") ||
      "--",
    search: { el: "input" },
  },
  {
    prop: "orderType",
    label: "订单类型",
    value: (row) => enumType("orderTypeEnum", row.orderType),
    width: 130,
  },
  { prop: "bandwidth", label: "宽带(M)", width: 90 },
  { prop: "ipCount", label: "IP数量", width: 90 },
  {
    prop: "chargeType",
    label: "计费类型",
    width: 90,
    value: (row) => enumType("billingTypeEnum", row.chargeType),
  },
  {
    prop: "duration",
    label: "计费时长",
    width: 90,
    value: (row) =>
      row.duration
        ? row.duration + enumType("durationUnitEnum", row.durationUnit)
        : "--",
  },
  { prop: "networkCount", label: "购买产品数量", width: 120 },
  {
    prop: "actualAgiOpenTime",
    label: "开通时间",
    width: 170,
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["actualAgiOpenStartTime", "actualAgiOpenEndTime"],
      valueFormat: "YYYY-MM-DD",
    },
  },
  { prop: "actualAgiExpireTime", label: "过期时间", width: 170 },
  { prop: "payTime", label: "支付时间", width: 170 },
  // {
  //   prop: "createTime",
  //   label: "日期",
  //   noShow: true,
  //   search: {
  //     el: "date-picker",
  //     dateType: "datetimerange",
  //     dateEnum: ["actualAgiOpenTime", "actualAgiExpireTime"],
  //     valueFormat: "YYYY-MM-DD HH:mm:ss",
  //   },
  // },
  {
    prop: "actualStatus",
    label: "产品状态",
    slot: true,
    search: { el: "select" },
    enum: productStatusSelectEnum,
    width: 90,
  },
  { prop: "originalPrice", label: "原价", width: 90 },
  { prop: "premiumPrice", label: "平台溢价金额", width: 130 },
  { prop: "userDiscountAmount", label: "用户折扣金额", width: 130 },
  { prop: "couponAmount", label: "优惠券满减金额", width: 130 },
  { prop: "voucherAmount", label: "代金券抵扣金额", width: 130 },
  { prop: "onlinePayAmount", label: "微信/支付宝支付金额", width: 180 },
  { prop: "balancePayAmount", label: "余额支付金额", width: 130 },
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
