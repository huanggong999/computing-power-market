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
        <div class="flex"><span>用户名：</span> {{ row.customerName }}</div>
        <div class="flex"><span>手机号：</span>{{ row.customerPhone }}</div>
      </template>
      <template #orderStatus="row">
        <el-tag :type="enumTag('orderStatusTag', row.orderStatus)">
          {{ enumType("orderStatusEnum", row.orderStatus) }}
        </el-tag>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="RechargeOrder">
import { orderListApi } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { orderStatusSelectEnum } from "@/utils/selectEnum";

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
  title: "充值订单",
  initParams: { orderType: "BALANCE" },
});

const columns: ColumnProps[] = [
  {
    prop: "customerInfoQueryKey",
    label: "使用账户",
    search: { el: "input" },
    slot: true,
    width: 200,
  },
  { prop: "orderNo", label: "单号", width: 230 },
  {
    prop: "onlinePayType",
    label: "充值方式",
    value: (row: any) => enumType("payTypeEnum", row.onlinePayType),
    width: 110,
  },
  { prop: "payTime", label: "充值时间", width: 180 },
  { prop: "originalPrice", label: "充值金额", width: 110 },
  {
    prop: "orderStatus",
    label: "充值状态",
    search: { el: "select" },
    enum: orderStatusSelectEnum,
    slot: true,
    width: 130,
  },
  {
    prop: "rewardsType",
    label: "奖励类型",
    width: 160,
    value: (row: any) => enumType("rechargeRewardTypeEnum", row.rewardsType),
  },
  { prop: "rechargeName", label: "奖励活动名称", width: 180 },
  { prop: "rewardsAmount", label: "奖励金额", width: 113 },
  { prop: "rewardsTime", label: "奖励时间", width: 230 },
];
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
}
</style>
