<template>
  <el-dialog
    v-model="model"
    :title="'选择云服务器'"
    width="65%"
    center
    :destroy-on-close="true"
    :before-close="() => emit('update:modelValue', false)"
  >
    <ProTable
      type="radio"
      ref="ComProTableRef"
      :columns="columns"
      :tableData="tableData"
      :search-param="searchParam"
      :max-height="500"
      :pageData="pageData"
      :searchFn="searchFn"
      :resetFn="resetFn"
      :getList="getList"
    >
    </ProTable>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emit('update:modelValue', false)">取消</el-button>
        <el-button type="primary" @click="confirm"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="SelectCloudServer">
import { ecsPageApi } from "@/api/cloudServer";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import {
  ecsAvailableZoneSelectEnum,
  serverTypeSelectEnum,
} from "@/utils/selectEnum";
import { useVModel } from "@/utils/useVModel";
const props = defineProps<{ modelValue: boolean; productType: 1 | 2 }>();

const { tableData, pageData, searchParam, searchFn, resetFn, getList } =
  useTable({
    api: ecsPageApi,
    initParams: { productType: props.productType },
    requestAuto: false,
  });

const emit = defineEmits(["update:modelValue", "submit"]);
const model = useVModel(props, "modelValue", emit);
const columns: ColumnProps[] = [
  { prop: "hoursPrice", label: "按量计费价格" },
  { prop: "monthPrice", label: "包年包月价格" },
  { prop: "oneYearPrice", label: "1年价格" },
  { prop: "twoYearPrice", label: "2年价格" },
  { prop: "threeYearPrice", label: "3年价格" },
  {
    prop: "ecsTypeEnum",
    label: "ecs类型",
    value: (row) => enumType("ecsTypeEnum", row.ecsType),
    search: { el: "select" },
    enum: serverTypeSelectEnum,
  },
  { prop: "ecsScale", label: "服务器规格" },
  { prop: "cpuNumber", label: "cpu数量", width: 90 },
  { prop: "memorySize", label: "内存大小", width: 90 },
  { prop: "cpuModel", label: "cpu型号" },
  { prop: "gpuModel", label: "gpu型号", search: { el: "input" } },
  { prop: "gpuMemory", label: "gpu内存", search: { el: "input" } },
  {
    prop: "sourceRegions",
    label: "可用区",
    value: (row) => enumType("regionEnum", row.regionsZones),
    search: { el: "select" },
    enum: ecsAvailableZoneSelectEnum,
  },
];

watch(
  () => model.value,
  (val) => {
    if (val) return getList();
    tableData.value = [];
  }
);
const ComProTableRef = ref();
const confirm = () => {
  const { radio } = ComProTableRef.value;
  if (!radio) return ElMessage.error("请选择云服务器");
  const { id, ecsScale, ecsType } = tableData.value.find(
    (el) => el.id === radio
  );
  emit("submit", id, ecsScale, ecsType);
  emit("update:modelValue", false);
};
</script>
<style lang="scss" scoped></style>
