<template>
  <el-dialog
    v-model="model"
    :title="'优惠券'"
    width="55%"
    center
    :destroy-on-close="true"
    :before-close="() => emit('update:modelValue', false)"
  >
    <ProTable
      type="selection"
      ref="ComProTableRef"
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :searchParam="searchParam"
      :searchFn="searchFn"
      :resetFn="resetFn"
      :refreshFn="refreshFn"
      :max-height="500"
      :getList="getList"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emit('update:modelValue', false)">取消</el-button>
        <el-button type="primary" @click="confirm"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="ComCoupon">
import { couponPageApi } from "@/api/coupon";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { useVModel } from "@/utils/useVModel";
import dayjs from "dayjs";

interface IParamsCoupon {
  modelValue: boolean;
}
const props = defineProps<IParamsCoupon>();
const emit = defineEmits(["update:modelValue", "submit"]);
const model = useVModel(props, "modelValue", emit);
const {
  tableData,
  pageData,
  searchParam,
  searchFn,
  resetFn,
  getList,
  refreshFn,
} = useTable({
  api: couponPageApi,
  requestAuto: false,
  initParams: {
    status: "OK",
    useEndTime: dayjs().format("YYYY-MM-DD HH:mm:ss"),
  },
});
watch(model, (val) => {
  if (val) getList();
});
const columns: ColumnProps[] = [
  { prop: "name", label: "优惠券名称", search: { el: "input" } },
  {
    prop: "type",
    label: "优惠券类型",
    value: (row: any) => enumType("couponTypeEnum", row.type),
  },

  { prop: "thresholdAmount", label: "满足金额" },
  { prop: "deductionAmount", label: "抵扣金额" },
  {
    prop: "rangeType",
    label: "使用范围",
    value: (row: any) => enumType("couponRangeEnum", row.rangeType),
  },
  { prop: "count", label: "优惠券数量" },
  {
    prop: "useTimeStart",
    label: "有效期",
    value: (row: any) => row.useTimeStart + "--" + row.useTimeEnd,
    width: 320,
  },
];
const ComProTableRef = ref();
const confirm = () => {
  const { selectedList } = ComProTableRef.value;
  if (!selectedList.length) return ElMessage.error("请选择优惠券");
  emit("submit", selectedList);
  emit("update:modelValue", false);
};
</script>
<style lang="scss" scoped></style>
