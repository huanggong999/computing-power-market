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
      row-key="poolName"
    >
      <template #gpu="row">
        <div class="resource-cell">
          <div>总量 {{ row.gpuTotal ?? 0 }}</div>
          <div class="resource-sub">
            已分配 {{ row.allocatedGpus ?? 0 }} / 可用 {{ row.availableGpus ?? 0 }}
          </div>
        </div>
      </template>
      <template #capacity="row">
        <div class="resource-cell">
          <div>CPU {{ row.cpuTotalCores ?? 0 }} 核</div>
          <div class="resource-sub">
            内存 {{ row.memoryTotalGi ?? 0 }} GiB / 磁盘 {{ row.diskTotalGi ?? 0 }} GiB
          </div>
        </div>
      </template>
      <template #status="row">
        <el-tag :type="row.status === 'Ready' ? 'success' : 'warning'">
          {{ row.status || "--" }}
        </el-tag>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="GpuNodePool">
import { gpuNodePoolListApi } from "@/api/gpuCluster";
import { useTable } from "@/hooks/useTable";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({
  api: gpuNodePoolListApi,
  title: "节点池管理",
});

const columns: ColumnProps[] = [
  { prop: "poolName", label: "节点池名称", width: 180 },
  {
    prop: "gpuModel",
    label: "GPU型号",
    width: 150,
    search: { el: "input", key: "gpuModel" },
  },
  { prop: "nodeCount", label: "节点数", width: 100 },
  { prop: "readyNodeCount", label: "Ready节点", width: 110 },
  { prop: "gpu", label: "GPU容量", slot: true, width: 170 },
  { prop: "capacity", label: "计算/存储容量", slot: true, width: 260 },
  {
    prop: "status",
    label: "状态",
    slot: true,
    width: 110,
    search: { el: "select" },
    enum: [
      { value: "Ready", label: "Ready" },
      { value: "异常", label: "异常" },
    ],
  },
];
</script>

<style lang="scss" scoped>
.resource-cell {
  line-height: 1.6;
  text-align: left;
}

.resource-sub {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  white-space: normal;
}
</style>
