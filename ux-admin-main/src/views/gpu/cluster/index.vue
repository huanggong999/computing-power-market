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
      row-key="node_name"
    >
      <template #gpu="row">
        <div class="resource-cell">
          <div>{{ row.gpu_model || "--" }}</div>
          <div class="resource-sub">
            总量 {{ row.gpu_count ?? 0 }} / 已分配 {{ row.allocated_gpus ?? 0 }} / 可用 {{ row.available_gpus ?? 0 }}
          </div>
        </div>
      </template>

      <template #cpu="row">
        <div class="resource-cell">
          <div>{{ formatCore(row.cpu?.total_cores) }} 核</div>
          <div class="resource-sub">
            已分配 {{ formatCore(row.cpu?.allocated_cores) }} / 可用 {{ formatCore(row.cpu?.available_cores) }}
          </div>
        </div>
      </template>

      <template #memory="row">
        <div class="resource-cell">
          <div>{{ formatGi(row.memory?.total_gi) }}</div>
          <div class="resource-sub">
            已分配 {{ formatGi(row.memory?.allocated_gi) }} / 可用 {{ formatGi(row.memory?.available_gi) }}
          </div>
        </div>
      </template>

      <template #disk="row">
        <div class="resource-cell">
          <div>{{ formatGi(row.disk?.total_gi) }}</div>
          <div class="resource-sub">
            已分配 {{ formatGi(row.disk?.allocated_gi) }} / 可用 {{ formatGi(row.disk?.available_gi) }}
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

<script setup lang="ts" name="GpuCluster">
import { gpuClusterNodesApi } from "@/api/gpuCluster";
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
  api: gpuClusterNodesApi,
  title: "GPU集群",
});

const formatNumber = (value: any, digits = 2) => {
  const number = Number(value);
  if (!Number.isFinite(number)) return "0";
  return Number.isInteger(number) ? String(number) : number.toFixed(digits);
};

const formatCore = (value: any) => formatNumber(value);
const formatGi = (value: any) => `${formatNumber(value)} GiB`;

const columns: ColumnProps[] = [
  {
    prop: "node_name",
    label: "节点名称",
    width: 190,
    search: { el: "input", key: "nodeName" },
  },
  {
    prop: "gpu",
    label: "GPU型号/数量",
    slot: true,
    width: 220,
    search: { el: "input", key: "gpuModel" },
  },
  { prop: "cpu", label: "CPU", slot: true, width: 180 },
  { prop: "memory", label: "内存", slot: true, width: 210 },
  { prop: "disk", label: "磁盘", slot: true, width: 210 },
  { prop: "pod_count", label: "Pod数量", width: 100 },
  { prop: "kubelet_version", label: "Kubelet版本", width: 130 },
  {
    prop: "status",
    label: "状态",
    slot: true,
    width: 110,
    search: { el: "select" },
    enum: [
      { value: "Ready", label: "Ready" },
      { value: "NotReady", label: "NotReady" },
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
