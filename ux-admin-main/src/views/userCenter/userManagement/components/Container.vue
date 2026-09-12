<template>
  <ProTable
    :columns="columns"
    :tableData="tableData"
    :pageData="pageData"
    :search-param="searchParam"
    :refreshFn="refreshFn"
    :getList="getList"
    :searchFn="searchFn"
    :resetFn="resetFn"
    :maxHeight="500"
    type="none"
    :isPage="false"
  >
    <template #clusterName="row">
      <div class="instance-name">
        <div class="instance-id">{{ row.clusterId ?? "--" }}</div>
        <div class="instance-name-text">{{ row.clusterName ?? "--" }}</div>
      </div>
    </template>
    <template #operation="row">
      <el-button
        type="primary"
        link
        @click="removeFn(deleteContainerApi, row.id, row.clusterName)"
      >
        删除
      </el-button>
    </template>
  </ProTable>
</template>

<script setup lang="ts" name="Container">
import { deleteContainerApi, getContainerListApi } from "@/api/userCenter";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";

const props = defineProps<{ customerId: string | number | undefined }>();
const {
  tableData,
  pageData,
  getList,
  searchParam,
  searchFn,
  resetFn,
  refreshFn,
  removeFn,
} = useTable({
  api: getContainerListApi,
  isPage: false,
  initParams: { customerId: props.customerId },
});
const columns: ColumnProps[] = [
  { prop: "clusterName", label: "ID/名称", slot: true },
  {
    prop: "status",
    label: "状态",
    value: (row) => enumType("containerStatusEnum", row.status),
  },
  { prop: "vciNumber", label: "VCI实例数" },
  { prop: "vci", label: "VCI用量", slot: true },
  { prop: "kubernetesVersion", label: "Kubernetes 版本" },
  { prop: "operation", label: "操作", slot: true },
];
</script>
<style lang="scss" scoped></style>
