<template>
  <ProTable
    type="none"
    :columns="columns"
    :table-data="tableData"
    :is-page="false"
    :is-refresh="false"
  >
    <template #operation="row">
      <el-button type="primary" text @click="() => emit('delCoupon', row)">
        移除
      </el-button>
    </template>
  </ProTable>
</template>

<script setup lang="ts" name="SelectCoupon">
import { enumType } from "@/utils/Enum";

const columns: ColumnProps[] = [
  { prop: "id", label: "ID" },
  { prop: "name", label: "优惠券名称" },
  {
    prop: "type",
    label: "优惠券类型",
    value: (row: any) => enumType("couponTypeEnum", row.type),
  },
  { prop: "deductionAmount", label: "抵扣金额" },
  {
    prop: "rangeType",
    label: "使用范围",
    value: (row: any) => enumType("couponRangeEnum", row.rangeType),
  },
  { prop: "count", label: "优惠卷数量" },
  {
    prop: "useTimeStart",
    label: "有效期",
    value: (row: any) => row.useTimeStart + "--" + row.useTimeEnd,
    width: 320,
  },
  { prop: "operation", label: "操作", slot: true },
];

defineProps<{ tableData: TKeyValue[] }>();

const emit = defineEmits(["delCoupon"]);
</script>
<style lang="scss" scoped></style>
