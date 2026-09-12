<template>
  <div class="monitor-page" v-loading="loading">
    <div class="monitor-toolbar card">
      <div>
        <div class="page-title">
          <span class="status-dot" />
          集群监控
        </div>
        <div class="page-subtitle">
          更新时间 {{ formatTime(overview.time) }}
        </div>
      </div>
      <div class="toolbar-actions">
        <el-tag :type="metricsSource === 'prometheus' ? 'success' : 'warning'">
          {{ metricsSourceLabel }}
        </el-tag>
        <el-button type="primary" :icon="Refresh" @click="fetchOverview">
          刷新
        </el-button>
      </div>
    </div>

    <el-alert
      v-if="errorMessage"
      class="monitor-alert"
      type="error"
      :title="errorMessage"
      show-icon
      :closable="false"
    />

    <el-alert
      v-if="isMetricsDegraded && !errorMessage"
      class="monitor-alert"
      type="warning"
      title="实时 GPU 卡级指标暂不可用，数量层指标仍按 K8s 实际占用展示。"
      show-icon
      :closable="false"
    />

    <div class="summary-panel card">
      <div
        v-for="(item, index) in summaryCards"
        :key="item.label"
        class="summary-item"
        :class="`summary-item-${index}`"
      >
        <div class="metric-accent" />
        <div class="metric-label">{{ item.label }}</div>
        <div class="metric-value">{{ item.value }}</div>
        <div class="metric-extra">{{ item.extra }}</div>
      </div>
    </div>

    <el-row :gutter="12" class="section-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <span>资源总览</span>
              <span class="section-count">quantity</span>
            </div>
          </template>
          <el-table :data="resourceRows" border>
            <el-table-column prop="name" label="资源" width="100" />
            <el-table-column prop="total" label="总量" min-width="120" />
            <el-table-column prop="allocated" label="已用" min-width="120" />
            <el-table-column prop="available" label="剩余" min-width="120" />
            <el-table-column prop="unit" label="单位" width="90" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <span>GPU 型号</span>
              <span class="section-count">{{ gpuModelRows.length }}</span>
            </div>
          </template>
          <el-table :data="gpuModelRows" border>
            <el-table-column prop="model" label="型号" min-width="160" show-overflow-tooltip />
            <el-table-column prop="total" label="总量" width="80" />
            <el-table-column prop="allocated" label="已用" width="80" />
            <el-table-column prop="available" label="剩余" width="80" />
          </el-table>
          <el-empty v-if="!gpuModelRows.length" description="暂无 GPU 型号数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="section-card cluster-section">
      <template #header>
        <div class="section-header">
          <span>集群列表</span>
          <span class="section-count">{{ clusters.length }}</span>
        </div>
      </template>

      <el-collapse v-if="clusters.length" v-model="activeClusters">
        <el-collapse-item
          v-for="cluster in clusters"
          :key="clusterKey(cluster)"
          :name="clusterKey(cluster)"
        >
          <template #title>
            <div class="cluster-title">
              <div class="cluster-name">
                <span>{{ field(cluster, "cluster_name", "clusterName", "name") || "--" }}</span>
                <el-tag size="small" :type="statusType(field(cluster, 'status'))">
                  {{ field(cluster, "status") || "--" }}
                </el-tag>
              </div>
              <div class="cluster-meta">
                {{ field(cluster, "region", "region_code", "regionCode") || "--" }} ·
                节点 {{ numberText(field(cluster, "node_count", "nodeCount")) }} ·
                GPU {{ usageText(objectField(cluster, "gpu")) }} ·
                均值 {{ percentText(field(cluster, "gpu_utilization_avg", "gpuUtilizationAvg")) }}
              </div>
            </div>
          </template>

          <div class="cluster-body">
            <div class="cluster-summary-line">
              <span>GPU 卡数 <strong>{{ numberText(field(cluster, "gpu_cards_count", "gpuCardsCount")) }}</strong></span>
              <span>总量 <strong>{{ numberText(field(objectField(cluster, "gpu"), "total")) }}</strong></span>
              <span>已用 <strong>{{ numberText(field(objectField(cluster, "gpu"), "allocated")) }}</strong></span>
              <span>剩余 <strong>{{ numberText(field(objectField(cluster, "gpu"), "available")) }}</strong></span>
              <span>平均利用率 <strong>{{ percentText(field(cluster, "gpu_utilization_avg", "gpuUtilizationAvg")) }}</strong></span>
            </div>

            <el-table :data="nodeRows(cluster)" border row-key="node_name">
              <el-table-column type="expand" width="44">
                <template #default="{ row }">
                  <div class="node-detail">
                    <div class="node-resource-grid">
                      <div class="inline-metric">
                        <span>CPU</span>
                        <strong>{{ cpuText(row) }}</strong>
                      </div>
                      <div class="inline-metric">
                        <span>内存</span>
                        <strong>{{ memoryText(row) }}</strong>
                      </div>
                      <div class="inline-metric">
                        <span>磁盘</span>
                        <strong>{{ diskText(row) }}</strong>
                      </div>
                    </div>

                    <div class="card-title">GPU 卡实时指标</div>
                    <el-table v-if="gpuCards(row).length" :data="gpuCards(row)" border>
                      <el-table-column prop="index" label="序号" width="70" />
                      <el-table-column prop="uuid" label="UUID" min-width="180" show-overflow-tooltip />
                      <el-table-column prop="model" label="型号" min-width="180" show-overflow-tooltip />
                      <el-table-column label="利用率" width="110">
                        <template #default="{ row: card }">
                          {{ percentText(field(card, "utilization")) }}
                        </template>
                      </el-table-column>
                      <el-table-column label="显存" min-width="170">
                        <template #default="{ row: card }">
                          {{ numberText(field(card, "memory_used_gi", "memoryUsedGi")) }} /
                          {{ numberText(field(card, "memory_total_gi", "memoryTotalGi")) }} Gi
                        </template>
                      </el-table-column>
                      <el-table-column label="温度" width="100">
                        <template #default="{ row: card }">
                          {{ numberText(field(card, "temperature")) }} ℃
                        </template>
                      </el-table-column>
                      <el-table-column label="功耗" width="100">
                        <template #default="{ row: card }">
                          {{ numberText(field(card, "power_watts", "powerWatts")) }} W
                        </template>
                      </el-table-column>
                    </el-table>
                    <el-empty v-else description="暂无实时 GPU 卡指标" />
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="节点" min-width="180" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ field(row, "node_name", "nodeName", "name") || "--" }}
                </template>
              </el-table-column>
              <el-table-column label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="statusType(field(row, 'status'))">
                    {{ field(row, "status") || "--" }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="GPU 型号" min-width="150" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ field(row, "gpu_model", "gpuModel") || "--" }}
                </template>
              </el-table-column>
              <el-table-column label="GPU" width="150">
                <template #default="{ row }">
                  {{ usageText(objectField(row, "gpu")) }}
                </template>
              </el-table-column>
              <el-table-column label="GPU 卡" width="100">
                <template #default="{ row }">
                  {{ numberText(field(row, "gpu_card_count", "gpuCardCount")) }}
                </template>
              </el-table-column>
              <el-table-column label="地域" min-width="120" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ field(row, "region", "region_code", "regionCode") || "--" }}
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-if="!nodeRows(cluster).length" description="暂无节点数据" />
          </div>
        </el-collapse-item>
      </el-collapse>
      <el-empty v-else description="暂无集群监控数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts" name="GpuClusterMonitor">
import { computed, onMounted, ref } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import { gpuClusterMonitorOverviewApi } from "@/api/gpuCluster";

const loading = ref(false);
const errorMessage = ref("");
const overview = ref<Record<string, any>>({});
const activeClusters = ref<string[]>([]);

const summary = computed(() => objectField(overview.value, "summary"));
const clusters = computed(() => arrayField(overview.value, "clusters"));
const metricsSource = computed(() => field(overview.value, "metrics_source", "metricsSource") || "none");
const isMetricsDegraded = computed(() => metricsSource.value === "none");
const metricsSourceLabel = computed(() => {
  return metricsSource.value === "prometheus" ? "Prometheus 指标" : "实时指标不可用";
});

const summaryCards = computed(() => {
  const gpu = objectField(summary.value, "gpu");
  return [
    {
      label: "集群",
      value: numberText(field(summary.value, "cluster_count", "clusterCount")),
      extra: `节点 ${numberText(field(summary.value, "node_count", "nodeCount"))}`,
    },
    {
      label: "节点状态",
      value: `${numberText(field(summary.value, "online_nodes", "onlineNodes"))} 在线`,
      extra: `${numberText(field(summary.value, "offline_nodes", "offlineNodes"))} 离线`,
    },
    {
      label: "GPU",
      value: usageText(gpu),
      extra: "总量 / 已用 / 剩余",
    },
    {
      label: "GPU 卡",
      value: numberText(field(summary.value, "gpu_cards_count", "gpuCardsCount")),
      extra: `平均利用率 ${percentText(field(summary.value, "gpu_utilization_avg", "gpuUtilizationAvg"))}`,
    },
  ];
});

const gpuModelRows = computed(() => {
  return arrayField(summary.value, "gpu_models", "gpuModels").map((item) => ({
    model: field(item, "model") || "--",
    total: numberText(field(item, "total")),
    allocated: numberText(field(item, "allocated")),
    available: numberText(field(item, "available")),
  }));
});

const resourceRows = computed(() => [
  resourceRow("GPU", objectField(summary.value, "gpu"), "卡"),
  resourceRow("CPU", objectField(summary.value, "cpu_cores", "cpuCores"), "核"),
  resourceRow("内存", objectField(summary.value, "memory_gi", "memoryGi"), "Gi"),
  resourceRow("磁盘", objectField(summary.value, "disk_gi", "diskGi"), "Gi"),
]);

const fetchOverview = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    const res = await gpuClusterMonitorOverviewApi();
    overview.value = (res?.data || res || {}) as Record<string, any>;
    activeClusters.value = clusters.value.length ? [clusterKey(clusters.value[0])] : [];
  } catch (error: any) {
    overview.value = { metrics_source: "none", summary: {}, clusters: [] };
    errorMessage.value = error?.message || "集群监控数据加载失败";
  } finally {
    loading.value = false;
  }
};

const resourceRow = (name: string, value: Record<string, any>, unit: string) => ({
  name,
  total: numberText(field(value, "total")),
  allocated: numberText(field(value, "allocated")),
  available: numberText(field(value, "available")),
  unit,
});

const clusterKey = (cluster: Record<string, any>) => {
  return String(field(cluster, "cluster_id", "clusterId", "cluster_name", "clusterName", "name") || "default");
};

const nodeRows = (cluster: Record<string, any>) => arrayField(cluster, "nodes");
const gpuCards = (node: Record<string, any>) => arrayField(node, "gpu_cards", "gpuCards");

const cpuText = (node: Record<string, any>) => {
  const cpu = objectField(node, "cpu");
  return `${numberText(field(cpu, "allocated_cores", "allocatedCores"))} / ${numberText(field(cpu, "total_cores", "totalCores"))} 核`;
};

const memoryText = (node: Record<string, any>) => {
  const memory = objectField(node, "memory");
  return `${numberText(field(memory, "allocated_gi", "allocatedGi"))} / ${numberText(field(memory, "total_gi", "totalGi"))} Gi`;
};

const diskText = (node: Record<string, any>) => {
  const disk = objectField(node, "disk");
  return `${numberText(field(disk, "allocated_gi", "allocatedGi"))} / ${numberText(field(disk, "total_gi", "totalGi"))} Gi`;
};

function field(source: Record<string, any> | undefined, ...keys: string[]) {
  if (!source) return undefined;
  for (const key of keys) {
    const value = source[key];
    if (value !== undefined && value !== null && value !== "") return value;
  }
  return undefined;
}

function objectField(source: Record<string, any> | undefined, ...keys: string[]) {
  const value = field(source, ...keys);
  return value && typeof value === "object" && !Array.isArray(value) ? value : {};
}

function arrayField(source: Record<string, any> | undefined, ...keys: string[]) {
  const value = field(source, ...keys);
  return Array.isArray(value) ? value : [];
}

function numberText(value: any) {
  return value === undefined || value === null || value === "" ? "--" : String(value);
}

function percentText(value: any) {
  return value === undefined || value === null || value === "" ? "--" : `${value}%`;
}

function usageText(value: Record<string, any>) {
  return `${numberText(field(value, "total"))} / ${numberText(field(value, "allocated"))} / ${numberText(field(value, "available"))}`;
}

function statusType(status: any) {
  if (status === "运行中" || status === "Ready") return "success";
  if (status === "部分异常" || status === "Warning") return "warning";
  if (status === "异常" || status === "NotReady") return "danger";
  return "info";
}

function formatTime(value: any) {
  if (!value) return "--";
  const parsed = dayjs(String(value));
  return parsed.isValid() ? parsed.format("YYYY-MM-DD HH:mm:ss") : String(value);
}

onMounted(fetchOverview);
</script>

<style lang="scss" scoped>
.monitor-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 100%;
  padding: 2px;
}

.monitor-toolbar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  overflow: hidden;
  border: 1px solid rgba(36, 196, 180, 0.18);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(0, 150, 136, 0.12), rgba(64, 158, 255, 0.06) 44%, rgba(255, 255, 255, 0.96)),
    var(--el-bg-color);
  box-shadow: 0 8px 24px rgba(34, 62, 89, 0.08);
}

.monitor-toolbar::after {
  position: absolute;
  right: 150px;
  bottom: -38px;
  width: 180px;
  height: 90px;
  border: 1px solid rgba(0, 150, 136, 0.18);
  border-radius: 50%;
  content: "";
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-primary);
  font-size: 20px;
  font-weight: 600;
  line-height: 1.4;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #00a896;
  box-shadow: 0 0 0 5px rgba(0, 168, 150, 0.12), 0 0 18px rgba(0, 168, 150, 0.55);
}

.page-subtitle {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.toolbar-actions {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.monitor-alert {
  margin: 0;
}

.summary-panel {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  padding: 12px;
  border: 1px solid rgba(36, 196, 180, 0.16);
  border-radius: 8px;
  background: linear-gradient(180deg, #f8fbff, #f3f7fb);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9), 0 8px 22px rgba(34, 62, 89, 0.06);
}

.summary-item {
  position: relative;
  min-width: 0;
  min-height: 96px;
  padding: 14px 14px 12px 16px;
  overflow: hidden;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.04);
}

.summary-item::after {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: rgba(0, 168, 150, 0.06);
  content: "";
}

.metric-accent {
  width: 28px;
  height: 3px;
  margin-bottom: 10px;
  border-radius: 999px;
  background: #00a896;
}

.summary-item-2 .metric-accent,
.summary-item-3 .metric-accent {
  background: #409eff;
}

.summary-item-4 .metric-accent,
.summary-item-5 .metric-accent,
.summary-item-6 .metric-accent {
  background: #7c3aed;
}

.metric-label {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.metric-value {
  margin-top: 6px;
  color: var(--el-text-color-primary);
  font-size: 22px;
  font-weight: 700;
  line-height: 1.2;
  word-break: break-word;
}

.metric-extra {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.section-row {
  row-gap: 12px;
}

.section-card {
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 8px;
  box-shadow: 0 8px 20px rgba(34, 62, 89, 0.05);
}

.section-card :deep(.el-card__header) {
  padding: 14px 16px;
  border-bottom-color: rgba(148, 163, 184, 0.18);
  background: linear-gradient(90deg, rgba(0, 168, 150, 0.08), rgba(64, 158, 255, 0.04));
}

.section-card :deep(.el-card__body) {
  padding: 16px;
}

.section-card :deep(.el-table .cell) {
  line-height: 22px;
}

.section-card :deep(.el-table th.el-table__cell) {
  background: #f7fafc;
  color: #5f6b7a;
  font-weight: 600;
}

.section-card :deep(.el-table__row:hover > td.el-table__cell) {
  background: rgba(0, 168, 150, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--el-text-color-primary);
  font-weight: 600;
}

.section-count {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
}

.cluster-section {
  margin-bottom: 12px;
}

.cluster-section :deep(.el-collapse) {
  border-top: 0;
  border-bottom: 0;
}

.cluster-section :deep(.el-collapse-item__header) {
  min-height: 52px;
  padding: 0;
  border-bottom-color: var(--el-border-color-lighter);
}

.cluster-section :deep(.el-collapse-item__content) {
  padding-bottom: 0;
}

.cluster-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-width: 0;
  padding-right: 18px;
  gap: 16px;
}

.cluster-name {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 8px;
  color: var(--el-text-color-primary);
  font-weight: 600;
}

.cluster-name span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cluster-meta {
  flex-shrink: 0;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.cluster-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px 0 16px;
}

.node-resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
}

.cluster-summary-line {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 10px 12px;
  border: 1px solid rgba(0, 168, 150, 0.16);
  border-radius: 8px;
  background: linear-gradient(90deg, rgba(0, 168, 150, 0.08), rgba(64, 158, 255, 0.04));
}

.cluster-summary-line span {
  padding-right: 10px;
  border-right: 1px solid rgba(148, 163, 184, 0.26);
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.cluster-summary-line span:last-child {
  padding-right: 0;
  border-right: 0;
}

.cluster-summary-line strong {
  margin-left: 4px;
  color: var(--el-text-color-primary);
  font-size: 13px;
}

.inline-metric {
  min-width: 0;
  padding: 10px 12px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.72);
}

.inline-metric span {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.inline-metric strong {
  display: block;
  margin-top: 6px;
  color: var(--el-text-color-primary);
  font-size: 16px;
  word-break: break-word;
}

.node-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 8px 12px 12px;
}

.card-title {
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .monitor-toolbar,
  .cluster-title {
    align-items: flex-start;
    flex-direction: column;
  }

  .toolbar-actions {
    width: 100%;
    justify-content: space-between;
  }

  .cluster-meta {
    flex-shrink: 1;
    white-space: normal;
  }

  .summary-panel {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .summary-item {
    padding: 10px 0;
    border-right: 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }
}

@media (min-width: 769px) and (max-width: 1280px) {
  .summary-panel {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}
</style>
