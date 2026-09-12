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
      row-key="clusterId"
    >
      <template #clusterName="row">
        <div class="cluster-name-cell">
          <div class="cluster-name">{{ row.clusterName || "--" }}</div>
          <div class="cluster-id">{{ row.clusterId || "--" }}</div>
        </div>
      </template>
      <template #status="row">
        <el-tag :type="getStatusType(row.status)">
          {{ row.status || "--" }}
        </el-tag>
      </template>
      <template #labels="row">
        <span>{{ row.labels || "--" }}</span>
      </template>
      <template #usage="row">
        <div class="resource-cell">
          <div>GPU {{ row.usageGpu || "--" }}</div>
          <div class="resource-sub">
            内存 {{ row.usageMemoryGi || "--" }}
          </div>
        </div>
      </template>
      <template #operations="row">
        <el-button
          v-for="operation in getVisibleOperations(row.operations)"
          :key="operation"
          type="primary"
          link
          @click="handleOperation(operation, row)"
        >
          {{ operation }}
        </el-button>
      </template>
    </ProTable>

    <el-drawer
      v-model="detailVisible"
      :title="detailTitle"
      size="72%"
      destroy-on-close
    >
      <div v-loading="detailLoading" class="cluster-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="集群ID">
            {{ displayValue(detailData, "cluster_id", "clusterId", "id") }}
          </el-descriptions-item>
          <el-descriptions-item label="集群名称">
            {{ displayValue(detailData, "cluster_name", "clusterName", "name") }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            {{ clusterStatus }}
          </el-descriptions-item>
          <el-descriptions-item label="地域">
            {{ displayValue(detailData, "region") }}
          </el-descriptions-item>
          <el-descriptions-item label="版本">
            {{ displayValue(firstNode, "kubelet_version") }}
          </el-descriptions-item>
          <el-descriptions-item label="节点">
            {{ displayValue(summaryData, "ready_nodes") }} /
            {{ displayValue(summaryData, "total_nodes") }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section-title">资源概览</div>
        <el-table :data="resourceRows" border>
          <el-table-column prop="name" label="资源" min-width="120" />
          <el-table-column prop="total" label="总量" min-width="120" />
          <el-table-column prop="allocated" label="已分配" min-width="120" />
          <el-table-column prop="available" label="可用" min-width="120" />
          <el-table-column prop="remark" label="说明" min-width="140" />
        </el-table>

        <div class="detail-section-title">GPU 型号汇总</div>
        <el-table :data="gpuModelRows" border>
          <el-table-column prop="model" label="GPU型号" min-width="160" />
          <el-table-column prop="nodeCount" label="节点数" min-width="100" />
          <el-table-column prop="totalGpus" label="GPU总数" min-width="100" />
          <el-table-column prop="allocatedGpus" label="已分配" min-width="100" />
          <el-table-column prop="availableGpus" label="可用" min-width="100" />
        </el-table>

        <div class="detail-section-title">节点明细</div>
        <el-table :data="nodeRows" border>
          <el-table-column label="节点名称" min-width="180">
            <template #default="{ row }">
              {{ displayValue(row, "node_name", "nodeName", "name") }}
            </template>
          </el-table-column>
          <el-table-column label="状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ displayValue(row, "status") }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="GPU型号" min-width="120">
            <template #default="{ row }">
              {{ displayValue(row, "gpu_model") }}
            </template>
          </el-table-column>
          <el-table-column label="GPU" min-width="120">
            <template #default="{ row }">
              {{ displayValue(row, "allocated_gpus") }} /
              {{ displayValue(row, "gpu_count") }}
            </template>
          </el-table-column>
          <el-table-column label="CPU" min-width="120">
            <template #default="{ row }">
              {{ displayValue(row, "cpu.allocated_cores") }} /
              {{ displayValue(row, "cpu.total_cores") }} 核
            </template>
          </el-table-column>
          <el-table-column label="内存" min-width="120">
            <template #default="{ row }">
              {{ displayValue(row, "memory.allocated_gi") }} /
              {{ displayValue(row, "memory.total_gi") }} Gi
            </template>
          </el-table-column>
          <el-table-column label="磁盘" min-width="120">
            <template #default="{ row }">
              {{ displayValue(row, "disk.allocated_gi") }} /
              {{ displayValue(row, "disk.total_gi") }} Gi
            </template>
          </el-table-column>
          <el-table-column label="Pod" min-width="80">
            <template #default="{ row }">
              {{ displayValue(row, "pod_count") }}
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="180">
            <template #default="{ row }">
              {{ displayValue(row, "create_time") }}
            </template>
          </el-table-column>
        </el-table>

        <template v-if="nodeRows.length">
          <div class="detail-section-title">节点条件</div>
          <el-table :data="conditionRows" border>
            <el-table-column prop="nodeName" label="节点" min-width="180" />
            <el-table-column prop="type" label="类型" min-width="150" />
            <el-table-column prop="status" label="状态" min-width="90" />
            <el-table-column prop="reason" label="原因" min-width="180" />
            <el-table-column prop="message" label="说明" min-width="260" show-overflow-tooltip />
          </el-table>

          <div class="detail-section-title">节点标签</div>
          <el-table :data="labelRows" border>
            <el-table-column prop="nodeName" label="节点" min-width="180" />
            <el-table-column prop="key" label="标签" min-width="240" show-overflow-tooltip />
            <el-table-column prop="value" label="值" min-width="180" show-overflow-tooltip />
          </el-table>
        </template>

        <el-empty v-if="!nodeRows.length" description="暂无节点数据" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="GpuClusterList">
import { computed, ref } from "vue";
import { gpuClusterListApi, gpuClusterSummaryApi } from "@/api/gpuCluster";
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
  api: gpuClusterListApi,
  title: "集群列表",
});

const getStatusType = (status: string) => {
  if (status === "运行中" || status === "Ready") return "success";
  if (status === "部分异常") return "warning";
  if (status === "异常") return "danger";
  return "info";
};

const getVisibleOperations = (operations?: string[]) => {
  return (operations?.length ? operations : ["详情"]).filter((operation) => operation !== "应用管理");
};

const detailVisible = ref(false);
const detailLoading = ref(false);
const detailData = ref<Record<string, any>>({});

const detailTitle = computed(() => {
  const name = getField(detailData.value, "cluster_name");
  return name ? `集群详情 - ${name}` : "集群详情";
});

const summaryData = computed(() => detailData.value.summary || {});
const nodeRows = computed(() => {
  const nodes = detailData.value.nodes;
  return Array.isArray(nodes) ? nodes : [];
});
const firstNode = computed(() => nodeRows.value[0] || {});
const clusterStatus = computed(() => {
  if (!nodeRows.value.length) return "--";
  const ready = summaryData.value.ready_nodes || 0;
  const total = summaryData.value.total_nodes || nodeRows.value.length;
  if (ready === total) return "Ready";
  if (ready > 0) return "部分异常";
  return "异常";
});

const resourceRows = computed(() => [
  buildSummaryRow("节点", "total_nodes", "ready_nodes", "not_ready_nodes", "就绪 / 未就绪"),
  buildSummaryRow("GPU", "total_gpus", "allocated_gpus", "available_gpus", "已分配 / 可用"),
  buildSummaryRow("CPU(核)", "total_cpu_cores", "allocated_cpu_cores", "available_cpu_cores", "已分配 / 可用"),
  buildSummaryRow("内存(Gi)", "total_memory_gi", "allocated_memory_gi", "available_memory_gi", "已分配 / 可用"),
  buildSummaryRow("磁盘(Gi)", "total_disk_gi", "allocated_disk_gi", "available_disk_gi", "已分配 / 可用"),
]);

const gpuModelRows = computed(() => Object.entries(summaryData.value.gpu_model_summary || {}).map(([model, value]: [string, any]) => ({
  model,
  nodeCount: formatValue(value?.node_count),
  totalGpus: formatValue(value?.total_gpus),
  allocatedGpus: formatValue(value?.allocated_gpus),
  availableGpus: formatValue(value?.available_gpus),
})));

const conditionRows = computed(() => nodeRows.value.flatMap((node: any) => {
  const conditions = Array.isArray(node.conditions) ? node.conditions : [];
  return conditions.map((condition: any) => ({
    nodeName: node.node_name || "--",
    type: condition.type || "--",
    status: condition.status || "--",
    reason: condition.reason || "--",
    message: condition.message || "--",
  }));
}));

const labelRows = computed(() => nodeRows.value.flatMap((node: any) => Object.entries(node.labels || {}).map(([key, value]) => ({
  nodeName: node.node_name || "--",
  key,
  value,
}))));

const handleOperation = async (operation: string, row: any) => {
  if (operation !== "详情") return;
  detailVisible.value = true;
  detailLoading.value = true;
  detailData.value = {};
  try {
    const { data } = await gpuClusterSummaryApi({ clusterId: row.clusterId });
    detailData.value = data || {};
  } finally {
    detailLoading.value = false;
  }
};

function buildSummaryRow(name: string, totalKey: string, allocatedKey: string, availableKey: string, remark: string) {
  return {
    name,
    total: formatValue(summaryData.value[totalKey]),
    allocated: formatValue(summaryData.value[allocatedKey]),
    available: formatValue(summaryData.value[availableKey]),
    remark,
  };
}

function displayValue(source: any, ...keys: string[]) {
  return formatValue(getField(source, ...keys));
}

function formatValue(value: any) {
  if (value === undefined || value === null || value === "") return "--";
  return value;
}

function getField(source: any, ...keys: string[]): any {
  if (!source) return undefined;
  for (const key of keys) {
    const value: any = key.split(".").reduce((current, path) => current?.[path], source);
    if (value !== undefined && value !== null && value !== "") return value;
  }
  for (const wrapper of ["cluster", "summary"]) {
    if (source[wrapper]) {
      const value: any = getField(source[wrapper], ...keys);
      if (value !== undefined && value !== null && value !== "") return value;
    }
  }
  return undefined;
}

const columns: ColumnProps[] = [
  {
    prop: "clusterName",
    label: "集群名称",
    slot: true,
    width: 160,
    search: { el: "input", key: "clusterName" },
  },
  {
    prop: "status",
    label: "状态",
    slot: true,
    width: 110,
    search: { el: "select" },
    enum: [
      { value: "运行中", label: "运行中" },
      { value: "部分异常", label: "部分异常" },
      { value: "异常", label: "异常" },
    ],
  },
  { prop: "version", label: "版本", width: 120 },
  { prop: "labels", label: "标签", slot: true, width: 180 },
  { prop: "nodeCount", label: "节点数", width: 90 },
  { prop: "usage", label: "使用量", slot: true, width: 180 },
  {
    prop: "createTime",
    label: "创建时间",
    width: 190,
  },
  {
    prop: "region",
    label: "地域",
    width: 120,
    search: { el: "input", key: "region" },
  },
  { prop: "operations", label: "操作", slot: true, fixed: "right", width: 180 },
];
</script>

<style lang="scss" scoped>
.cluster-name-cell {
  line-height: 1.6;
  text-align: left;
}

.cluster-name {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.cluster-id {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.resource-cell {
  line-height: 1.6;
  text-align: left;
}

.resource-sub {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  white-space: normal;
}

.cluster-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-section-title {
  color: var(--el-text-color-primary);
  font-size: 15px;
  font-weight: 600;
  line-height: 24px;
}
</style>
