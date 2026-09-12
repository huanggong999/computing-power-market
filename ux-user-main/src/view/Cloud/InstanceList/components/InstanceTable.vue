<template>
  <div class="instance-table-wrapper">
    <div class="table-toolbar">
      <div>
        <h3>实例列表</h3>
        <span>按实例、规格、网络和实时状态统一查看</span>
      </div>
      <div class="toolbar-count">共 {{ total }} 台</div>
    </div>

    <el-table
      v-loading="loading"
      :data="data"
      class="aliyun-instance-table"
      row-key="id"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="46" fixed="left" />

      <el-table-column label="实例" min-width="280" fixed="left">
        <template #default="{ row }">
          <div class="instance-main-cell">
            <div class="instance-title-line">
              <el-button
                class="instance-name-button"
                link
                type="primary"
                @click.stop="handleSetName(row)"
              >
                {{ row.name || '设置名称' }}
              </el-button>
              <el-icon v-if="isPolling(row.id)" class="is-loading polling-icon"><Loading /></el-icon>
            </div>
            <button type="button" class="instance-id-button" @click.stop="handleViewDetail(row)">
              {{ row.id || '-' }}
            </button>
            <div class="instance-tags">
              <el-tag size="small" effect="plain">{{ row.region || row.regionCode || '-' }}</el-tag>
              <span v-if="row.uuid">UUID {{ row.uuid }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="118">
        <template #default="{ row }">
          <div class="status-cell">
            <StatusTag :status="row.status" size="small" />
            <span v-if="row.statusMessage" class="status-message">{{ row.statusMessage }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="实例规格" min-width="210">
        <template #default="{ row }">
          <div class="spec-card-cell">
            <div class="gpu-line">{{ row.gpuType || '-' }} × {{ row.gpuCount || 0 }} 卡</div>
            <div class="spec-line">{{ row.cpuCores || 0 }} vCPU / {{ row.memory || 0 }} GB</div>
            <div class="spec-line">显存 {{ row.gpuMemory || 0 }} GB</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="网络" min-width="160">
        <template #default="{ row }">
          <div class="network-cell">
            <div><span>内网</span>{{ row.privateIp || '-' }}</div>
            <div><span>公网</span>{{ row.publicIp || '-' }}</div>
            <div><span>SSH</span>{{ row.sshPort || 22 }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="存储" min-width="190">
        <template #default="{ row }">
          <div class="disk-cell">
            <div class="disk-item">
              <div class="disk-row">
                <span>系统盘 {{ row.systemDisk || row.systemDiskSize || 0 }} GB</span>
                <strong>{{ getDiskUsage(row.diskUsage?.systemDiskUsage) }}%</strong>
              </div>
              <el-progress :percentage="getDiskUsage(row.diskUsage?.systemDiskUsage)" :stroke-width="5" :show-text="false" color="#f5a524" />
            </div>
            <div class="disk-item">
              <div class="disk-row">
                <span>数据盘 {{ row.dataDisk || row.dataDiskSize || 0 }} GB</span>
                <strong>{{ getDiskUsage(row.diskUsage?.dataDiskUsage) }}%</strong>
              </div>
              <el-progress :percentage="getDiskUsage(row.diskUsage?.dataDiskUsage)" :stroke-width="5" :show-text="false" color="#409eff" />
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="监控" min-width="190">
        <template #default="{ row }">
          <div class="health-cell">
            <div class="health-status">
              <span :class="['status-dot', getHealthStatus(row)]"></span>
              <span>{{ getHealthText(row) }}</span>
            </div>
            <div class="metric-row">
              <span>CPU</span>
              <el-progress :percentage="getCpuUsage(row)" :stroke-width="5" :show-text="false" :color="getProgressColor(getCpuUsage(row))" />
              <strong>{{ getCpuUsage(row) }}%</strong>
            </div>
            <div class="metric-row">
              <span>内存</span>
              <el-progress :percentage="getMemoryUsage(row)" :stroke-width="5" :show-text="false" :color="getProgressColor(getMemoryUsage(row))" />
              <strong>{{ getMemoryUsage(row) }}%</strong>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="计费" width="126">
        <template #default="{ row }">
          <div class="billing-cell">
            <strong>{{ row.billingType === 'hourly' ? '按量计费' : '包年包月' }}</strong>
            <span v-if="row.billingType === 'hourly'">￥{{ row.pricePerHour || 0 }}/时</span>
            <span v-else>{{ row.expiredAt || '-' }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="236" fixed="right" align="left">
        <template #default="{ row }">
          <div class="action-cell">
            <el-button
              class="primary-action"
              type="primary"
              plain
              size="small"
              @click.stop="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              v-if="row.status === 'stopped'"
              type="success"
              size="small"
              :loading="isPolling(row.id)"
              :disabled="isActionDisabled(row)"
              @click.stop="$emit('action', 'start', row)"
            >
              开机
            </el-button>
            <el-button
              v-else-if="row.status === 'running'"
              type="danger"
              size="small"
              :loading="isPolling(row.id)"
              :disabled="isActionDisabled(row)"
              @click.stop="$emit('action', 'stop', row)"
            >
              关机
            </el-button>
            <el-button
              type="primary"
              plain
              size="small"
              :disabled="isActionDisabled(row)"
              @click.stop="$emit('action', 'vnc', row)"
            >
              VNC
            </el-button>
            <el-dropdown :disabled="isActionDisabled(row)" @command="(cmd) => handleCommand(cmd, row)" @click.stop>
              <span class="el-dropdown-link">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="restart">重启</el-dropdown-item>
                  <el-dropdown-item command="resetPassword">重置密码</el-dropdown-item>
                  <el-dropdown-item command="setName">设置名称</el-dropdown-item>
                  <el-dropdown-item command="renew">续费</el-dropdown-item>
                  <el-dropdown-item command="shutdownSchedule">定时关机</el-dropdown-item>
                  <el-dropdown-item command="tools">快捷工具</el-dropdown-item>
                  <el-dropdown-item command="release" divided type="danger">释放实例</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowDown, Loading } from '@element-plus/icons-vue'
import StatusTag from './StatusTag.vue'
import { InstanceStatus } from '@/types/instance'
import type { Instance } from '@/types/instance'

interface Props {
  data: Instance[]
  loading: boolean
  pagination: {
    currentPage: number
    pageSize: number
    total: number
  }
  pollingIds?: string[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:selected': [selected: Instance[]]
  'page-change': [page: number]
  'size-change': [size: number]
  'sort-change': [column: any]
  'row-click': [row: Instance]
  action: [action: string, row: Instance]
}>()

const currentPage = computed({
  get: () => props.pagination.currentPage,
  set: (val) => emit('page-change', val)
})

const pageSize = computed({
  get: () => props.pagination.pageSize,
  set: (val) => emit('size-change', val)
})

const total = computed(() => props.pagination.total)

const handleSelectionChange = (selection: Instance[]) => {
  emit('update:selected', selection)
}

const handleCurrentChange = (page: number) => {
  emit('page-change', page)
}

const handleSizeChange = (size: number) => {
  emit('size-change', size)
}

const handleCommand = (cmd: string, row: Instance) => {
  emit('action', cmd, row)
}

const handleSetName = (row: Instance) => {
  emit('action', 'setName', row)
}

const handleViewDetail = (row: Instance) => {
  emit('action', 'detail', row)
}

const getDiskUsage = (usage?: number) => Math.max(0, Math.min(100, Math.round(Number(usage || 0))))
const getCpuUsage = (row: Instance) => Math.max(0, Math.min(100, Math.round(Number(row.healthStatus?.cpuUsage || 0))))
const getMemoryUsage = (row: Instance) => Math.max(0, Math.min(100, Math.round(Number(row.healthStatus?.memoryUsage || 0))))

const getHealthStatus = (row: Instance) => {
  const cpuUsage = getCpuUsage(row)
  if (cpuUsage < 70) return 'healthy'
  if (cpuUsage < 90) return 'warning'
  return 'danger'
}

const getHealthText = (row: Instance) => {
  const status = getHealthStatus(row)
  const statusMap: Record<string, string> = {
    healthy: '正常',
    warning: '警告',
    danger: '危险'
  }
  return statusMap[status]
}

const getProgressColor = (percentage: number) => {
  if (percentage < 70) return '#52c41a'
  if (percentage < 90) return '#f5a524'
  return '#f56c6c'
}

const switchingStatuses = [
  InstanceStatus.STARTING,
  InstanceStatus.STOPPING,
  InstanceStatus.RESTARTING,
  InstanceStatus.RELEASING
]

const isPolling = (id: string) => props.pollingIds?.includes(id) ?? false
const isActionDisabled = (row: Instance) => isPolling(row.id) || switchingStatuses.includes(row.status)
</script>

<style scoped lang="scss">
.instance-table-wrapper {
  overflow: hidden;
  background: #fff;
  border: 1px solid #ead7af;
  border-radius: 6px;
  box-shadow: 0 10px 28px rgb(118 82 18 / 8%);
}

.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 16px;
  background: #fff8e8;
  border-bottom: 1px solid #ead7af;
}

.table-toolbar h3 {
  margin: 0 0 3px;
  color: #2f220b;
  font-size: 15px;
  font-weight: 650;
}

.table-toolbar span,
.toolbar-count {
  color: #8a6f3b;
  font-size: 12px;
}

:deep(.aliyun-instance-table) {
  color: #1f2937;
  font-size: 12px;

  &::before {
    display: none;
  }

  .el-table__border-left-patch,
  .el-table__inner-wrapper::before {
    display: none;
  }

  .el-table__header th.el-table__cell {
    height: 42px;
    padding: 0;
    background: #fff3d6;
    border-bottom: 1px solid #ead7af;
    color: #6f520f;
    font-size: 12px;
    font-weight: 650;
  }

  .el-table__cell {
    padding: 12px 0;
    border-bottom: 1px solid #f0e5cf;
    vertical-align: top;
  }

  .el-table__row:hover > td.el-table__cell,
  .el-table__row.current-row > td.el-table__cell {
    background: #fffaf0;
  }

  .el-table__fixed-right::before,
  .el-table__fixed::before {
    background: #ead7af;
  }

  .el-table-column--selection .cell {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 28px;
    cursor: pointer;
  }

  .el-checkbox {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 18px;
    height: 18px;
  }

  .el-checkbox__inner {
    width: 14px;
    height: 14px;
  }
}

.instance-main-cell {
  min-width: 0;
  line-height: 1.45;
}

.instance-title-line {
  display: flex;
  align-items: center;
  gap: 8px;
}

.instance-name-button {
  max-width: 230px;
  height: auto;
  min-height: 18px;
  padding: 0;
  color: #0b63ce;
  font-size: 13px;
  font-weight: 650;
  justify-content: flex-start;
  white-space: normal;
  text-align: left;
}

.polling-icon {
  color: #f5a524;
  font-size: 13px;
}

.instance-id-button {
  display: block;
  max-width: 250px;
  margin-top: 4px;
  padding: 0;
  border: 0;
  background: transparent;
  color: #1f2937;
  cursor: pointer;
  font-family: Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  overflow: hidden;
  text-align: left;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.instance-id-button:hover {
  color: #c47a00;
}

.instance-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 7px;
  color: #8a97a8;
  font-size: 12px;
}

.instance-tags span {
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
}

.status-message {
  max-width: 96px;
  color: #8a6f3b;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.spec-card-cell {
  min-width: 0;
}

.gpu-line {
  color: #111827;
  font-size: 13px;
  font-weight: 650;
}

.spec-line,
.network-cell div,
.billing-cell span {
  margin-top: 5px;
  color: #667085;
  font-size: 12px;
}

.network-cell span {
  display: inline-block;
  width: 34px;
  color: #9a7c41;
}

.disk-cell {
  display: grid;
  gap: 10px;
}

.disk-row,
.metric-row {
  display: grid;
  grid-template-columns: auto minmax(50px, 1fr) auto;
  align-items: center;
  gap: 8px;
}

.disk-row {
  grid-template-columns: minmax(0, 1fr) auto;
  margin-bottom: 4px;
}

.disk-row span,
.metric-row span {
  color: #667085;
  font-size: 12px;
}

.disk-row strong,
.metric-row strong {
  color: #344054;
  font-size: 12px;
  font-weight: 600;
}

.health-status {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  color: #344054;
  font-size: 12px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.healthy {
  background: #52c41a;
}

.status-dot.warning {
  background: #f5a524;
}

.status-dot.danger {
  background: #f56c6c;
}

.metric-row + .metric-row {
  margin-top: 7px;
}

.metric-row span {
  width: 30px;
}

.metric-row strong {
  width: 34px;
  text-align: right;
}

.billing-cell {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.billing-cell strong {
  color: #111827;
  font-size: 12px;
}

.action-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.action-cell .el-button {
  min-width: 0;
  padding: 5px 8px;
  font-size: 12px;
}

.action-cell .primary-action {
  border-color: #f0bf62;
  color: #a16000;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #0b63ce;
  cursor: pointer;
  font-size: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 12px 16px;
  border-top: 1px solid #f0e5cf;
  background: #fffdf8;
}

:deep(.el-pagination) {
  font-size: 12px;
}

:deep(.el-pager li.is-active) {
  color: #c47a00;
}
</style>
