<template>
  <el-dialog
    v-model="model"
    title="查看分销用户"
    center
    :destroy-on-close="true"
    :before-close="() => emit('update:modelValue', false)"
    @close="() => emit('update:modelValue', false)"
  >
    <el-radio-group class="mt10 mb10" v-model="searchParam.type">
      <el-radio-button
        v-for="item in radioGroup"
        :key="item.label"
        :value="item.value"
      >
        {{ item.label }}
      </el-radio-button>
    </el-radio-group>
    <ProTable
      type="none"
      :columns="columns"
      :tableData="tableData"
      :search-param="searchParam"
      :max-height="500"
      :pageData="pageData"
      :getList="getList"
      :IsRefresh="false"
    >
      <template #userId="row">
        <div>{{ row.userId }}</div>
        <div>{{ row.shareKey }}</div>
      </template>
      <template #userName="row">
        <div>{{ row.userName }}</div>
        <div>{{ row.userPhone }}</div>
      </template>
    </ProTable>
  </el-dialog>
</template>

<script setup lang="ts" name="Distributor">
import { promotionAmbassadorFirstPageApi } from "@/api/promotionManagement";
import { useTable } from "@/hooks/useTable";
import { useVModel } from "@/utils/useVModel";

const props = defineProps<{
  modelValue: boolean;
  type: 1 | 2;
  userID: string | number;
}>();
const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);

const radioGroup = ref([
  { label: "一级", value: 1 },
  { label: "二级", value: 2 },
]);
const { tableData, pageData, getList, searchParam } = useTable({
  api: promotionAmbassadorFirstPageApi,
  requestAuto: false,
});

watch(
  () => [props.type, props.userID],
  ([type, userId]) => Object.assign(searchParam.value, { type, userId }),
  { immediate: true }
);
watch(() => [searchParam.value.type, searchParam.value.userId], getList);
watch(
  () => props.modelValue,
  (val) => {
    if (!val) Object.assign(pageData.value, { pageNo: 1, pageSize: 10 });
  }
);

const columns: ColumnProps[] = [
  { prop: "userId", label: "用户ID", slot: true },
  { prop: "userName", label: "用户信息", slot: true },
  { prop: "createTime", label: "绑定时间" },
  { prop: "sgOrder", label: "贡献订单" },
  { prop: "sgAmount", label: "贡献金额(元)" },
];
</script>
<style lang="scss" scoped></style>
