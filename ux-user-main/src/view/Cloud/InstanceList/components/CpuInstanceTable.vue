<template>
  <div class="instance-table-wrapper cpu-table-wrapper">
    <div class="table-toolbar">
      <div><h3>CPU 实例列表</h3><span>独立 CPU 云主机，不加入 GPU/Kubernetes 集群</span></div>
      <div class="toolbar-actions"><span class="toolbar-count">共 {{ total }} 台</span><el-button v-if="selectedCreating.length" type="danger" plain @click="$emit('batch-release', selectedCreating)">批量释放创建中（{{ selectedCreating.length }}）</el-button><el-button v-if="selectedAbnormal.length" type="danger" plain @click="$emit('batch-clear', selectedAbnormal)">批量删除异常（{{ selectedAbnormal.length }}）</el-button><el-button v-else-if="!selected.length && abnormalTotal" type="danger" plain @click="$emit('batch-clear', [])">删除全部异常（{{ abnormalTotal }}）</el-button><el-button :icon="RefreshRight" @click="$emit('refresh')">刷新</el-button></div>
    </div>
    <el-table v-loading="loading" :data="data" class="aliyun-instance-table" :row-key="rowKey" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="46" fixed="left" :selectable="isBatchSelectable" />
      <el-table-column label="实例" min-width="250" fixed="left"><template #default="{ row }"><div class="instance-main-cell"><strong>{{ row.instance_name || row.name || row.instance_id || row.id || '-' }}</strong><button type="button" class="instance-id-button" @click.stop="$emit('action', 'detail', row)">{{ row.instance_id || row.id || '-' }}</button><div class="instance-tags"><el-tag size="small" effect="plain">{{ row.region || '-' }}</el-tag><span>{{ row.zone || '-' }}</span></div></div></template></el-table-column>
      <el-table-column label="状态" width="125"><template #default="{ row }"><div class="status-cell"><el-tag :type="statusType(row)" size="small">{{ statusLabel(row) }}</el-tag><span v-if="row.status_message || row.message" class="status-message">{{ row.status_message || row.message }}</span></div></template></el-table-column>
      <el-table-column label="实例规格" min-width="180"><template #default="{ row }"><div class="spec-card-cell"><div>{{ row.cpu_cores || '-' }} vCPU / {{ row.memory || '-' }}</div><div class="spec-line">{{ row.instance_type_id || '-' }}</div></div></template></el-table-column>
      <el-table-column label="网络 / SSH" min-width="190"><template #default="{ row }"><div class="network-cell"><div><span>内网</span>{{ row.private_ip || '-' }}</div><div><span>公网</span>{{ row.public_ip || '-' }}</div><div><span>SSH</span>{{ row.ssh_info?.host || row.public_ip || '-' }}:{{ row.ssh_info?.port || 22 }}</div></div></template></el-table-column>
      <el-table-column label="存储" min-width="180"><template #default="{ row }"><div class="disk-cell"><div>系统盘 {{ row.system_disk || '-' }}</div><div>数据盘 {{ row.data_disk || '无' }}</div></div></template></el-table-column>
      <el-table-column label="计费" width="120"><template #default="{ row }"><div class="billing-cell"><strong>{{ billing(row.billing_mode) }}</strong><span>{{ row.expire_time || '-' }}</span></div></template></el-table-column>
      <el-table-column label="操作" width="300" fixed="right"><template #default="{ row }"><div class="action-cell"><el-button type="primary" plain size="small" @click.stop="$emit('action', 'detail', row)">详情</el-button><el-button v-if="['failed', 'error'].includes(normalizedStatus(row))" type="danger" plain size="small" @click.stop="$emit('action', 'clear', row)">清除异常</el-button><el-button v-else-if="normalizedStatus(row) === 'stopped'" type="success" size="small" @click.stop="$emit('action', 'start', row)">开机</el-button><el-button v-else-if="normalizedStatus(row) === 'running'" type="warning" size="small" @click.stop="$emit('action', 'stop', row)">关机</el-button><el-dropdown @command="(command) => $emit('action', command, row)" @click.stop><span class="el-dropdown-link">更多<el-icon class="el-icon--right"><ArrowDown /></el-icon></span><template #dropdown><el-dropdown-menu><el-dropdown-item command="restart" :disabled="!canDo(row, 'restart')" :title="canDo(row, 'restart') ? '重启实例' : reasonFor(row, 'restart')">重启</el-dropdown-item><el-dropdown-item command="ssh" :disabled="!canDo(row, 'ssh')" :title="canDo(row, 'ssh') ? '获取 SSH 连接信息' : reasonFor(row, 'ssh')">SSH 连接</el-dropdown-item><el-dropdown-item command="resetPassword" :disabled="!canDo(row, 'resetPassword')" :title="canDo(row, 'resetPassword') ? '重置密码' : reasonFor(row, 'resetPassword')">重置密码</el-dropdown-item><el-dropdown-item command="rename" :disabled="!canDo(row, 'rename')" :title="canDo(row, 'rename') ? '修改名称' : reasonFor(row, 'rename')">修改名称</el-dropdown-item><el-dropdown-item command="resize" :disabled="!canDo(row, 'resize')" :title="canDo(row, 'resize') ? '资源变更' : reasonFor(row, 'resize')">资源变更</el-dropdown-item><el-dropdown-item v-if="['failed', 'error'].includes(normalizedStatus(row))" command="clear" type="danger">清除异常记录</el-dropdown-item><el-dropdown-item command="release" divided type="danger" :disabled="!canRelease(row)" :title="canRelease(row) ? '释放实例' : releaseDisabledReason(row)">释放实例</el-dropdown-item></el-dropdown-menu></template></el-dropdown></div></template></el-table-column>
    </el-table>
    <div v-if="!loading && data.length === 0" class="empty-state">暂无 CPU 实例</div>
    <div class="pagination-wrapper"><el-pagination :current-page="page" :page-size="pageSize" :total="total" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50]" @current-change="$emit('page-change', $event)" @size-change="$emit('size-change', $event)" /></div>
  </div>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { ArrowDown, RefreshRight } from '@element-plus/icons-vue'
import type { CpuInstance } from '@/types/instance'
import { cpuStatusInfo, cpuCanAction, cpuActionDisabledReason, cpuCloudInstanceId, cpuRowKey } from '@/utils/instanceSource'
defineProps<{ data: CpuInstance[]; loading: boolean; total: number; abnormalTotal: number; page: number; pageSize: number }>()
defineEmits<{ action: [string, CpuInstance]; 'page-change': [number]; 'size-change': [number]; refresh: []; 'batch-clear': [CpuInstance[]]; 'batch-release': [CpuInstance[]] }>()
const selected = ref<CpuInstance[]>([])
const rowKey = (row: CpuInstance) => cpuRowKey(row)
const normalizedStatus = (row: CpuInstance) => cpuStatusInfo(row.status).value
const isAbnormal = (row: CpuInstance) => ['failed', 'error'].includes(normalizedStatus(row))
const isCreating = (row: CpuInstance) => normalizedStatus(row) === 'creating' && Boolean(cpuCloudInstanceId(row))
const isBatchSelectable = (row: CpuInstance) => isAbnormal(row) || isCreating(row)
const selectedAbnormal = computed(() => selected.value.filter(isAbnormal))
const selectedCreating = computed(() => selected.value.filter(isCreating))
const statusLabel = (row: CpuInstance) => cpuStatusInfo(row.status).label
const statusType = (row: CpuInstance) => cpuStatusInfo(row.status).type
const canDo = (row: CpuInstance, action: string) => cpuCanAction(row, action)
const reasonFor = (row: CpuInstance, action: string) => cpuActionDisabledReason(row, action) || '当前状态不支持此操作'
const canRelease = (row: CpuInstance) => cpuCanAction(row, 'release') && !(normalizedStatus(row) === 'creating' && !cpuCloudInstanceId(row))
const releaseDisabledReason = (row: CpuInstance) => normalizedStatus(row) === 'creating' && !cpuCloudInstanceId(row) ? '实例正在创建中，请等创建完成后再释放' : cpuActionDisabledReason(row, 'release') || '当前状态不支持释放'
const billing = (value?: string) => ({ on_demand: '按量付费', monthly: '包月', yearly: '包年' }[value || ''] || value || '-')
const handleSelectionChange = (rows: CpuInstance[]) => { selected.value = rows.filter(isBatchSelectable) }
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

.toolbar-actions { display:flex; align-items:center; gap:12px; }.empty-state { padding:64px 16px; text-align:center; color:#909399; background:#fff; }.status-cell { display:flex; flex-direction:column; gap:4px; }.status-message { font-size:12px; color:#909399; max-width:110px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }.instance-id-button { display:block; padding:0; border:0; background:none; color:#909399; cursor:pointer; font-size:12px; }.instance-tags { display:flex; gap:8px; align-items:center; color:#909399; font-size:12px; margin-top:5px; }.network-cell span { display:inline-block; width:36px; color:#909399; }.billing-cell { display:flex; flex-direction:column; gap:4px; }.billing-cell span,.spec-line { color:#909399; font-size:12px; }
</style>
