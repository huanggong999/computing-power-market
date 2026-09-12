<template>
  <el-dialog
    v-model="model"
    :title="'活动记录'"
    width="70%"
    center
    :destroy-on-close="true"
    :before-close="() => emit('update:modelValue', false)"
  >
    <ProTable
      type="none"
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :searchParam="searchParam"
      :searchFn="searchFn"
      :resetFn="resetFn"
      :refreshFn="refreshFn"
      :max-height="500"
      :getList="getList"
    >
      <template #shareUserInfo="row">
        <div>{{ row.shareUsername }}</div>
        <div>{{ row.shareUserPhone }}</div>
      </template>
      <template #registerUserInfo="row">
        <div>{{ row.registerUsername }}</div>
        <div>{{ row.registerUserPhone }}</div>
      </template>
    </ProTable>
  </el-dialog>
</template>

<script setup lang="ts" name="ActivityLog">
import { activeCenterGetActiveRecordApi } from "@/api/operationsManagement";
import { useTable } from "@/hooks/useTable";
import { useVModel } from "@/utils/useVModel";

const props = defineProps<{ modelValue: boolean; activeId: string | number }>();
const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);

const {
  tableData,
  pageData,
  searchParam,
  searchFn,
  resetFn,
  getList,
  refreshFn,
  totalParam,
} = useTable({ api: activeCenterGetActiveRecordApi, requestAuto: false });

watch(model, (val) => {
  totalParam.value.activeId = props.activeId;
  if (val) getList();
});
const columns: ColumnProps[] = [
  {
    prop: "nameOrPhone",
    label: "姓名/手机号",
    noShow: true,
    search: { el: "input" },
  },
  { prop: "shareUserId", label: "分享用户ID" },
  { prop: "shareUserInfo", label: "分享用户信息", slot: true },
  {
    prop: "invitationTime",
    label: "邀请时间",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startDay", "endDay"],
      valueFormat: "YYYY-MM-DD",
    },
  },
  { prop: "registerUserId", label: "注册用户ID" },
  { prop: "registerUserInfo", label: "注册用户信息", slot: true },
  { prop: "orderNo", label: "关联订单号" },
];
</script>
<style lang="scss" scoped></style>
