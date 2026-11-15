<template>
  <div class="instance-list-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">{{ pageTitle }}</h2>
        <p class="page-desc">{{ pageDesc }}</p>
      </div>
      <div v-if="!isGpuInstanceRoute" class="header-right">
        <el-button type="primary" @click="handleCreateInstance">
          <el-icon><Plus /></el-icon>
          创建实例
        </el-button>
      </div>
    </div>

    <el-alert
      v-if="creationNoticeVisible"
      class="creation-notice"
      type="success"
      :closable="true"
      show-icon
      @close="clearCreationNotice"
    >
      <template #title>
        <div class="creation-notice-content">
          <span>
            实例已提交创建<span v-if="createdInstanceId">，实例 ID：{{ createdInstanceId }}</span><span v-if="createdOrderNo">，订单号：{{ createdOrderNo }}</span>
          </span>
          <el-button v-if="createdOrderNo" link type="primary" @click="goToCreatedOrder">查看订单</el-button>
        </div>
      </template>
    </el-alert>

    <el-tabs v-model="activeTab" class="instance-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="GPU实例" name="gpu" />
      <el-tab-pane label="CPU实例" name="cpu" />
    </el-tabs>

    <template v-if="activeTab === 'gpu'">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ instanceStats.total }}</div>
        <div class="stat-label">总实例数</div>
      </div>
      <div class="stat-card running">
        <div class="stat-value">{{ instanceStats.running }}</div>
        <div class="stat-label">运行中</div>
      </div>
      <div class="stat-card stopped">
        <div class="stat-value">{{ instanceStats.stopped }}</div>
        <div class="stat-label">已停止</div>
      </div>
      <div class="stat-card other">
        <div class="stat-value">{{ instanceStats.other }}</div>
        <div class="stat-label">其他状态</div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <FilterBar
      v-model="filters"
      :gpu-types="gpuTypes"
      :regions="regions"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 批量操作栏 -->
    <BatchActions
      v-if="selectedInstances.length > 0"
      v-model:selected="selectedInstances"
      :instances="instanceList"
      @batch-start="handleBatchStart"
      @batch-stop="handleBatchStop"
      @batch-restart="handleBatchRestart"
      @batch-release="handleBatchRelease"
      @batch-renew="handleBatchRenew"
    />

    <div class="instance-workbench">
      <!-- 实例表格 -->
      <InstanceTable
        v-model:selected="selectedInstances"
        :data="instanceList"
        :loading="loading"
        :pagination="paginationWithTotal"
        :polling-ids="Array.from(operatingIds)"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
        @sort-change="handleSortChange"
        @row-click="handleRowClick"
        @action="handleInstanceAction"
      />

      <!-- 实例详情弹框 -->
      <div
        ref="detailHostRef"
        :class="['instance-detail-host', { 'is-open': drawerVisible }]"
        @click.self="handleDrawerClose"
      >
        <div class="instance-detail-dialog" @click.stop>
          <button
            ref="detailCloseRef"
            type="button"
            class="parent-detail-close"
            @click.stop.prevent="handleDrawerClose"
          >
            ×
          </button>
          <InstanceDrawer
            :model-value="drawerVisible"
            :instance="currentInstance"
            :metrics="currentMetrics"
            :metrics-loading="metricsLoading"
            :show-close="false"
            @refresh="handleRefreshDetail"
            @closed="handleDrawerClose"
            @update:timeRange="setMetricsTimeRange"
            @action="handleInstanceAction"
          />
        </div>
      </div>
    </div>

    </template>

    <template v-else>
      <div class="stats-cards">
        <div v-for="stat in cpuStatCards" :key="stat.label" :class="['stat-card', stat.className]">
          <div class="stat-value">{{ stat.value }}</div><div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
      <el-form class="cpu-filter-bar" inline @submit.prevent="applyCpuFilters">
        <el-form-item label="实例"><el-input v-model="cpuFilterDraft.keyword" placeholder="实例名称 / ID" clearable /></el-form-item>
        <el-form-item label="地域"><el-select v-model="cpuFilterDraft.region" placeholder="全部地域" clearable><el-option v-for="region in cpuRegionOptions" :key="region" :label="region" :value="region" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="cpuFilterDraft.status" placeholder="全部状态" clearable><el-option v-for="status in cpuStatusOptions" :key="status.value" :label="status.label" :value="status.value" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" native-type="submit">搜索</el-button><el-button @click="resetCpuFilters">重置</el-button></el-form-item>
      </el-form>
      <div class="instance-workbench">
        <CpuInstanceTable :data="pagedCpuInstances" :loading="cpuLoading" :total="filteredCpuInstances.length" :abnormal-total="abnormalCpuInstances.length" :page="cpuPage" :page-size="cpuPageSize" @page-change="(page) => cpuPage = page" @size-change="(size) => { cpuPageSize = size; cpuPage = 1 }" @refresh="refreshCpuList" @action="handleCpuAction" @batch-clear="clearAllCpuAbnormal" @batch-release="releaseCreatingCpuInstances" />
        <div :class="['instance-detail-host', { 'is-open': cpuDetailVisible }]" @click.self="cpuDetailVisible = false">
          <div class="instance-detail-dialog"><CpuInstanceDetail :instance="cpuDetail" @close="cpuDetailVisible = false" @action="handleCpuAction" /></div>
        </div>
      </div>
    </template>

    <!-- 更多操作对话框 -->
    <MoreActionsDialog
      v-model="moreActionsVisible"
      :instance="currentInstance"
      @success="handleActionSuccess"
    />

    <ResetPasswordDialog
      v-model="resetPasswordVisible"
      :instance="currentInstance"
      @success="handleActionSuccess"
    />

    <SetNameDialog
      v-model="setNameVisible"
      :instance="currentInstance"
      @success="handleActionSuccess"
    />

    <GpuResizeDialog v-model="resizeVisible" :instance-id="currentInstance?.id || currentCpuInstance?.instance_id || currentCpuInstance?.id" :mode="resizeMode" @refresh="handleResizeSuccess" />

    <CpuInstanceActionDialog
      v-model="cpuActionVisible"
      :mode="cpuActionMode"
      :instance="cpuActionInstance"
      @success="handleCpuActionSuccess"
    />

    <SshInfoDialog
      v-model="sshVisible"
      :instance-id="sshInstanceId"
      :source="sshSource"
    />

    <RenewDialog
      v-model="renewVisible"
      :instance="currentInstance"
      @success="handleActionSuccess"
    />

    <BatchRenewDialog
      v-model="batchRenewVisible"
      :instances="batchRenewInstances"
      @success="handleActionSuccess"
    />

    <ShutdownScheduleDialog
      v-model="shutdownScheduleVisible"
      :instance="currentInstance"
      @success="handleActionSuccess"
    />

    <ToolsDialog
      v-model="toolsVisible"
      :instance="currentInstance"
    />

    <el-dialog
      v-model="vncVisible"
      title="VNC连接"
      width="560px"
      align-center
    >
      <div v-loading="vncLoading" class="vnc-dialog">
        <el-alert
          v-if="vncInfo?.message"
          :title="vncInfo.message"
          type="success"
          :closable="false"
          show-icon
        />
        <el-descriptions :column="1" border class="vnc-descriptions">
          <el-descriptions-item label="实例ID">{{ normalizedVncInfo.instanceId || currentInstance?.id || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ normalizedVncInfo.enabled ? '已启用' : '未启用' }}</el-descriptions-item>
          <el-descriptions-item label="节点地址">{{ normalizedVncInfo.nodeHost || '-' }}</el-descriptions-item>
          <el-descriptions-item label="节点端口">{{ normalizedVncInfo.nodePort || '-' }}</el-descriptions-item>
          <el-descriptions-item label="VNC密码">
            <span class="secret-value">{{ normalizedVncInfo.vncPassword || '-' }}</span>
            <el-button
              v-if="normalizedVncInfo.vncPassword"
              link
              type="primary"
              @click="copyText(normalizedVncInfo.vncPassword)"
            >
              复制
            </el-button>
          </el-descriptions-item>
          <el-descriptions-item label="WebSocket">
            <span class="url-value">{{ normalizedVncInfo.websocketUrl || '-' }}</span>
            <el-button
              v-if="normalizedVncInfo.websocketUrl"
              link
              type="primary"
              @click="copyText(normalizedVncInfo.websocketUrl)"
            >
              复制
            </el-button>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="vncVisible = false">关闭</el-button>
        <el-button
          type="primary"
          :disabled="!normalizedVncInfo.websocketUrl"
          @click="copyText(normalizedVncInfo.websocketUrl)"
        >
          复制连接
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Instance, VncInfo } from '@/types/instance'
import { getInstanceVncInfo } from '@/api/instance'
import { useInstanceList } from './composables/useInstanceList'
import { useInstanceOperation } from './composables/useInstanceOperation'
import { useMetrics } from './composables/useMetrics'

import FilterBar from './components/FilterBar.vue'
import BatchActions from './components/BatchActions.vue'
import InstanceTable from './components/InstanceTable.vue'
import InstanceDrawer from './components/InstanceDrawer.vue'
import MoreActionsDialog from './components/MoreActionsDialog.vue'
import ResetPasswordDialog from './components/ResetPasswordDialog.vue'
import SetNameDialog from './components/SetNameDialog.vue'
import RenewDialog from './components/RenewDialog.vue'
import BatchRenewDialog from './components/BatchRenewDialog.vue'
import ShutdownScheduleDialog from './components/ShutdownScheduleDialog.vue'
import ToolsDialog from './components/ToolsDialog.vue'
import CpuInstanceTable from './components/CpuInstanceTable.vue'
import CpuInstanceDetail from './components/CpuInstanceDetail.vue'
import { isCpuInstance, isGpuInstance, cpuStatusInfo, cpuCanAction, cpuActionDisabledReason, cpuCloudInstanceId, cpuDetailTarget, cpuRowKey } from '@/utils/instanceSource'
import { clearCpuOperation, getCpuCreationOperation, getCpuInstances, startCpuInstance, stopCpuInstance, restartCpuInstance, releaseCpuInstance, getCpuInstanceDetail } from '@/api/instanceV2'
import type { CpuInstance } from '@/types/instance'
import GpuResizeDialog from './components/GpuResizeDialog.vue'
import CpuInstanceActionDialog from './components/CpuInstanceActionDialog.vue'
import SshInfoDialog from './components/SshInfoDialog.vue'

const router = useRouter()
const route = useRoute()
const activeTab = ref((route.query.type === 'cpu' ? 'cpu' : 'gpu'))
const cpuInstances = ref<CpuInstance[]>([])
const cpuLoading = ref(false)
const cpuDetail = ref<any>(null)
const cpuPage = ref(1)
const cpuPageSize = ref(10)
const cpuFilterDraft = reactive({ keyword: '', region: '', status: '' })
const cpuFilters = ref({ ...cpuFilterDraft })
const cpuStatusOptions = [
  { value: 'creating', label: '创建中' }, { value: 'running', label: '运行中' }, { value: 'stopped', label: '已停止' },
  { value: 'starting', label: '开机中' }, { value: 'stopping', label: '关机中' }, { value: 'releasing', label: '释放中' },
  { value: 'released', label: '已释放' }, { value: 'failed', label: '创建失败' }, { value: 'error', label: '异常' },
]
const cpuRegionOptions = computed(() => [...new Set(cpuInstances.value.map(row => row.region).filter(Boolean))] as string[])
const cpuStatCards = computed(() => {
  const running = cpuInstances.value.filter(row => cpuStatusInfo(row.status).value === 'running').length
  const stopped = cpuInstances.value.filter(row => cpuStatusInfo(row.status).value === 'stopped').length
  return [{ label: '总实例数', value: cpuInstances.value.length, className: '' }, { label: '运行中', value: running, className: 'running' }, { label: '已停止', value: stopped, className: 'stopped' }, { label: '其他状态', value: cpuInstances.value.length - running - stopped, className: 'other' }]
})
const filteredCpuInstances = computed(() => cpuInstances.value.filter(row => {
  const { keyword, region, status } = cpuFilters.value
  return (!keyword || [row.instance_name, row.name, row.instance_id, row.id].some(value => value?.toLowerCase().includes(keyword.toLowerCase().trim())))
    && (!region || row.region === region) && (!status || cpuStatusInfo(row.status).value === status)
}))
const pagedCpuInstances = computed(() => filteredCpuInstances.value.slice((cpuPage.value - 1) * cpuPageSize.value, cpuPage.value * cpuPageSize.value))
const abnormalCpuInstances = computed(() => cpuInstances.value.filter(row => ['failed', 'error'].includes(cpuStatusInfo(row.status).value)))
const applyCpuFilters = () => { cpuFilters.value = { ...cpuFilterDraft }; cpuPage.value = 1 }
const resetCpuFilters = () => { Object.assign(cpuFilterDraft, { keyword: '', region: '', status: '' }); applyCpuFilters() }
const refreshCpuList = () => { void fetchCpuList() }
const resizeVisible = ref(false)
const resizeMode = ref<'resize' | 'gpu'>('resize')
const currentCpuInstance = ref<CpuInstance | null>(null)
const cpuDetailVisible = ref(false)
const cpuActionVisible = ref(false)
const cpuActionMode = ref<'rename' | 'resetPassword'>('rename')
const cpuActionInstance = ref<CpuInstance | null>(null)
const sshVisible = ref(false)
const sshInstanceId = ref('')
const sshSource = ref<'gpu' | 'cpu'>('gpu')
const isGpuInstanceRoute = computed(() => route.path === '/cloud/gpuInstance')
const pageTitle = computed(() => isGpuInstanceRoute.value ? (activeTab.value === 'cpu' ? 'CPU实例列表' : 'GPU实例列表') : String(route.meta?.title || '容器实例'))
const pageDesc = computed(() =>
  isGpuInstanceRoute.value
    ? (activeTab.value === 'cpu' ? '管理独立 CPU 云主机，查看资源、网络、创建状态和生命周期' : '统一管理GPU算力资源，支持开机、关机、重启、续费和释放等操作')
    : '管理您的GPU云服务器实例，支持开机、关机、重启等操作'
)
const creationNoticeVisible = ref(route.query.createSuccess === '1')
const createdOrderNo = computed(() => String(route.query.orderNo || ''))
const createdInstanceId = computed(() => String(route.query.instanceId || ''))

const clearCreationNotice = () => {
  creationNoticeVisible.value = false
  const query = { ...route.query }
  delete query.createSuccess
  delete query.orderNo
  delete query.instanceId
  router.replace({ path: route.path, query })
}

const goToCreatedOrder = () => {
  if (!createdOrderNo.value) return
  router.push({ path: '/order', query: { orderNo: createdOrderNo.value } })
}

const {
  filters,
  selectedInstances,
  pagination,
  paginationWithTotal,
  instanceList,
  loading,
  total,
  gpuTypes,
  regions,
  instanceStats,
  handlePageChange,
  handleSizeChange,
  handleSortChange,
  handleSearch,
  handleReset,
  fetchList
} = useInstanceList()

const { executeOperation, executeBatchOperation, operatingIds } = useInstanceOperation({
  onSuccess: fetchList
})

const {
  metrics: currentMetrics,
  loading: metricsLoading,
  timeRange: metricsTimeRange,
  fetchMetrics,
  setTimeRange: setMetricsTimeRange,
  refreshMetrics
} = useMetrics()

const handleTabChange = async (tab: string) => {
  await router.replace({ path: route.path, query: { ...route.query, type: tab === 'cpu' ? 'cpu' : undefined } })
  cpuDetailVisible.value = false
  handleDrawerClose()
  if (tab === 'cpu') await fetchCpuList()
}
const fetchCpuList = async () => {
  cpuLoading.value = true
  try {
    const res = await getCpuInstances()
    if (res.data?.success === false) throw new Error(res.data.message || 'CPU实例加载失败')
    cpuInstances.value = (res.data?.instances || []).filter(isCpuInstance)
    cpuPage.value = Math.max(1, Math.min(cpuPage.value, Math.ceil(filteredCpuInstances.value.length / cpuPageSize.value)))
    if (cpuDetail.value) {
      const updated = cpuInstances.value.find(row => cpuRowKey(row) === cpuRowKey(cpuDetail.value))
      if (updated) cpuDetail.value = { ...cpuDetail.value, ...updated }
    }
  } catch (error: any) { ElMessage.error(error?.message || 'CPU实例加载失败') }
  finally {
    cpuLoading.value = false
  }
}
const refreshCpuDetail = async () => {
  // Operation-only rows have no cloud instance ID; refreshing their instance
  // detail would query /instances/{operation_id} and fail upstream.
  const id = cpuCloudInstanceId(cpuDetail.value)
  if (!id) return
  try {
    const res = await getCpuInstanceDetail(id)
    if (res.data?.success === false) return
    const detail = res.data?.instance || res.data
    if (isCpuInstance(detail)) cpuDetail.value = { ...cpuDetail.value, ...detail }
  } catch {
    // Keep the last known detail when a refresh races with a state transition.
  }
}

const handleCpuActionSuccess = async () => {
  await fetchCpuList()
  await refreshCpuDetail()
}

const handleCpuAction = async (action: string, row: CpuInstance) => {
  if (!isCpuInstance(row)) return void ElMessage.error('实例来源不匹配，请刷新列表后重试')
  if (action !== 'detail' && !cpuCanAction(row, action))
    return void ElMessage.warning(cpuActionDisabledReason(row, action) || '当前状态不支持此操作，请刷新后重试')
  // 创建任务尚未拿到上游实例 ID 时没有可释放的目标，按接口约定提示等待创建完成。
  if (action === 'release' && cpuStatusInfo(row.status).value === 'creating' && !cpuCloudInstanceId(row))
    return void ElMessage.warning('实例正在创建中，请等创建完成后再释放')
  const instanceId = cpuCloudInstanceId(row)
  const detailTarget = cpuDetailTarget(row)
  try {
    // 创建任务占位记录只有 operation_id，详情必须走 /operations/{id}。
    // 先展示列表中的本地数据，接口刷新失败时也不会阻断详情。
    if (action === 'detail') {
      cpuDetail.value = { ...row }
      cpuDetailVisible.value = true
      try {
        if (detailTarget.kind === 'instance') {
          const res = await getCpuInstanceDetail(detailTarget.id)
          if (res.data?.success !== false) {
            const detail = res.data?.instance || res.data
            if (isCpuInstance(detail)) cpuDetail.value = { ...cpuDetail.value, ...detail }
          }
        } else if (detailTarget.kind === 'operation') {
          const res = await getCpuCreationOperation(detailTarget.id)
          const operation = res.data || {}
          const created = operation.instances?.[0] as CpuInstance | undefined
          cpuDetail.value = {
            ...cpuDetail.value,
            ...(created || {}),
            operation_id: detailTarget.id,
            operation_status: operation.operation_status,
            status_message: operation.message || row.status_message || row.message,
            status: created?.status || row.status,
            message: operation.message || row.message,
          }
        }
      } catch {
        // 列表数据已足够展示创建任务；不为详情刷新再弹一次错误。
      }
      return
    }
    if (action === 'clear') {
      const target = row.operation_id || instanceId
      if (!target) return void ElMessage.error('异常记录缺少可删除的标识')
      await ElMessageBox.confirm(`确定清除 ${target} 的异常记录吗？清除后列表中将不再显示该记录。`, '确认清除', { type: 'warning', confirmButtonText: '清除', cancelButtonText: '取消' })
      if (row.operation_id) {
        const res = await clearCpuOperation(row.operation_id)
        if (res.data?.success === false) throw new Error(res.data.message || '清除异常记录失败')
      } else {
        if (!instanceId) return void ElMessage.error('异常记录缺少可删除的标识')
        const res = await releaseCpuInstance(instanceId)
        if (res.data?.success === false) throw new Error(res.data.message || '清除异常记录失败')
      }
      ElMessage.success('异常记录已清除')
      cpuDetailVisible.value = false
      await fetchCpuList()
      return
    }
    if (!instanceId) return void ElMessage.warning('实例正在创建中，请等创建完成后再操作')
    if (action === 'resize') {
      currentCpuInstance.value = row
      currentInstance.value = null
      resizeMode.value = 'resize'
      resizeVisible.value = true
      return
    }
    if (action === 'ssh') {
      sshInstanceId.value = instanceId
      sshSource.value = 'cpu'
      sshVisible.value = true
      return
    }
    if (['resetPassword', 'rename', 'setName'].includes(action)) {
      cpuActionInstance.value = row
      cpuActionMode.value = action === 'resetPassword' ? 'resetPassword' : 'rename'
      cpuActionVisible.value = true
      return
    }
    if (action === 'release') await ElMessageBox.confirm(`确定释放 ${instanceId} 吗？云主机和数据将不可恢复`, '确认释放', { type: 'warning' })
    if (action === 'restart') await ElMessageBox.confirm(`确定重启 ${instanceId} 吗？重启期间实例将短暂不可用。`, '确认重启', { type: 'warning' })
    const res = action === 'release' ? await releaseCpuInstance(instanceId)
      : action === 'start' ? await startCpuInstance(instanceId)
      : action === 'stop' ? await stopCpuInstance(instanceId)
      : action === 'restart' ? await restartCpuInstance(instanceId) : null
    if (!res) return
    if (res.data?.success === false) throw new Error(res.data.message || 'CPU实例操作失败')
    ElMessage.success(res.data?.message || '操作已完成')
    if (action === 'release') cpuDetailVisible.value = false
    await fetchCpuList()
    await refreshCpuDetail()
  } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || 'CPU实例操作失败') }
}
const clearAllCpuAbnormal = async (selectedRows: CpuInstance[] = []) => {
  const rows = selectedRows.length ? selectedRows : abnormalCpuInstances.value
  if (!rows.length) return
  try {
    await ElMessageBox.confirm(`确定删除当前 ${rows.length} 条异常记录吗？删除后不可恢复。`, '批量删除异常', { type: 'warning', confirmButtonText: '全部删除', cancelButtonText: '取消' })
    const results: PromiseSettledResult<any>[] = []
    for (let index = 0; index < rows.length; index += 20) {
      const batch = rows.slice(index, index + 20)
      results.push(...await Promise.allSettled(batch.map(row => {
        if (row.operation_id) return clearCpuOperation(row.operation_id)
        const instanceId = cpuCloudInstanceId(row)
        return instanceId ? releaseCpuInstance(instanceId) : Promise.reject(new Error('异常记录缺少实例 ID'))
      })))
    }
    const failed = results.filter(result => result.status === 'rejected' || (result.status === 'fulfilled' && result.value.data?.success === false)).length
    if (failed) ElMessage.warning(`已删除 ${rows.length - failed} 条，${failed} 条删除失败`)
    else ElMessage.success(`已删除 ${rows.length} 条异常记录`)
    cpuDetailVisible.value = false
    await fetchCpuList()
  } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || '批量删除异常失败') }
}

const releaseCreatingCpuInstances = async (rows: CpuInstance[]) => {
  const releaseable = rows.filter(row => cpuStatusInfo(row.status).value === 'creating' && cpuCloudInstanceId(row))
  if (!releaseable.length) return
  try {
    await ElMessageBox.confirm(`确定释放当前 ${releaseable.length} 个创建中的 CPU 实例吗？释放后不可恢复。`, '批量释放创建中', { type: 'warning', confirmButtonText: '批量释放', cancelButtonText: '取消' })
    const results: PromiseSettledResult<any>[] = []
    for (let index = 0; index < releaseable.length; index += 20) {
      const batch = releaseable.slice(index, index + 20)
      results.push(...await Promise.allSettled(batch.map(row => releaseCpuInstance(cpuCloudInstanceId(row)))))
    }
    const failed = results.filter(result => result.status === 'rejected' || (result.status === 'fulfilled' && result.value.data?.success === false)).length
    if (failed) ElMessage.warning(`已释放 ${releaseable.length - failed} 个，${failed} 个释放失败`)
    else ElMessage.success(`已提交释放 ${releaseable.length} 个创建中实例`)
    await fetchCpuList()
  } catch (error: any) { if (error !== 'cancel') ElMessage.error(error?.message || '批量释放创建中实例失败') }
}

// 创建实例
const handleCreateInstance = () => {
  router.push(activeTab.value === 'cpu' ? '/computeListNew?type=cpu' : isGpuInstanceRoute.value ? '/computeListNew' : '/cloud/createInstance')
}

// 抽屉
const drawerVisible = ref(false)
const currentInstance = ref<Instance | null>(null)
const detailHostRef = ref<HTMLElement | null>(null)
const detailCloseRef = ref<HTMLButtonElement | null>(null)

// 更多操作对话框
const moreActionsVisible = ref(false)
const resetPasswordVisible = ref(false)
const setNameVisible = ref(false)
const renewVisible = ref(false)
const batchRenewVisible = ref(false)
const shutdownScheduleVisible = ref(false)
const toolsVisible = ref(false)
const vncVisible = ref(false)
const vncLoading = ref(false)
const vncInfo = ref<VncInfo | null>(null)
const batchRenewInstances = ref<Instance[]>([])

const normalizedVncInfo = computed(() => {
  const info = vncInfo.value || {}
  return {
    instanceId: info.instanceId || info.instance_id || '',
    enabled: Boolean(info.enabled),
    websocketUrl: info.websocketUrl || info.websocket_url || '',
    vncPassword: info.vncPassword || info.vnc_password || '',
    nodeHost: info.nodeHost || info.node_host || '',
    nodePort: info.nodePort || info.node_port || '',
    message: info.message || ''
  }
})

const syncDetailHostVisible = (visible: boolean) => {
  drawerVisible.value = visible
  detailHostRef.value?.classList.toggle('is-open', visible)
}

// 行点击
const handleRowClick = (row: Instance) => {
  if (!isGpuInstance(row)) return void ElMessage.error('实例来源不匹配，请刷新列表后重试')
  currentInstance.value = row
  syncDetailHostVisible(true)
  nextTick(() => syncDetailHostVisible(true))
  fetchMetrics(row.id)
}

// 实例操作
const handleInstanceAction = async (action: string, instance: Instance) => {
  if (!isGpuInstance(instance)) return void ElMessage.error('实例来源不匹配，请刷新列表后重试')
  if (action === 'detail') {
    handleRowClick(instance)
    return
  }

  if (action === 'ssh') {
    currentInstance.value = instance
    sshInstanceId.value = instance.id
    sshSource.value = 'gpu'
    sshVisible.value = true
    return
  }

  if (action === 'resetPassword') {
    currentInstance.value = instance
    resetPasswordVisible.value = true
    return
  }

  if (['setName', 'rename'].includes(action)) {
    currentInstance.value = instance
    setNameVisible.value = true
    return
  }

  if (action === 'renew') {
    currentInstance.value = instance
    renewVisible.value = true
    return
  }

  if (action === 'shutdownSchedule') {
    currentInstance.value = instance
    shutdownScheduleVisible.value = true
    return
  }

  if (action === 'tools') {
    currentInstance.value = instance
    toolsVisible.value = true
    return
  }

  if (action === 'resize') {
    currentInstance.value = instance
    currentCpuInstance.value = null
    resizeMode.value = 'resize'
    resizeVisible.value = true
    return
  }

  if (action === 'gpuResize') {
    currentInstance.value = instance
    currentCpuInstance.value = null
    resizeMode.value = 'gpu'
    resizeVisible.value = true
    return
  }

  if (action === 'vnc') {
    currentInstance.value = instance
    await handleVncConnect(instance)
    return
  }

  await executeOperation(action, instance, {
    confirm: action !== 'start' && action !== 'stop',
    needPolling: true
  })
}

// 操作成功回调
const handleActionSuccess = () => {
  fetchList()
}

const handleResizeSuccess = () => {
  if (resizeMode.value === 'resize' && currentCpuInstance.value) {
    refreshCpuList()
    return
  }
  handleActionSuccess()
}

const handleVncConnect = async (instance: Instance) => {
  vncVisible.value = true
  vncLoading.value = true
  vncInfo.value = null
  try {
    const res = await getInstanceVncInfo(instance.id)
    if (res.code === 200 && res.data) {
      vncInfo.value = res.data
      return
    }
    ElMessage.error(res.msg || '获取VNC连接信息失败')
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.msg || error?.message || '获取VNC连接信息失败')
  } finally {
    vncLoading.value = false
  }
}

const copyText = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

const handleNativeDetailClose = (event: Event) => {
  event.preventDefault()
  event.stopPropagation()
  handleDrawerClose()
}

onMounted(() => {
  detailCloseRef.value?.addEventListener('click', handleNativeDetailClose, true)
  if (activeTab.value === 'cpu') fetchCpuList()
  if (route.query.createSuccess === '1') {
    ElMessage.success('实例已提交创建，当前状态可在列表中查看')
  }
})

onBeforeUnmount(() => {
  detailCloseRef.value?.removeEventListener('click', handleNativeDetailClose, true)
})

onBeforeRouteLeave(() => {
  syncDetailHostVisible(false)
})

// 批量操作
const handleBatchStart = (ids: string[]) => {
  executeBatchOperation('start', ids)
}

const handleBatchStop = (ids: string[]) => {
  executeBatchOperation('stop', ids)
}

const handleBatchRestart = (ids: string[]) => {
  executeBatchOperation('restart', ids)
}

const handleBatchRelease = (ids: string[]) => {
  executeBatchOperation('release', ids)
}

const handleBatchRenew = (instances: Instance[]) => {
  batchRenewInstances.value = instances
  batchRenewVisible.value = true
}

// 刷新详情
const handleRefreshDetail = () => {
  refreshMetrics()
}

// 抽屉关闭
const handleDrawerClose = () => {
  syncDetailHostVisible(false)
}
</script>

<style scoped lang="scss">
.cpu-filter-bar { padding: 16px 16px 0; background: #fffdf8; border: 1px solid #ead7af; border-radius: 6px; margin-bottom: 14px; }
.cpu-filter-bar .el-select { width: 160px; }

.instance-list-page {
  padding: 16px 20px 24px;
  background: #fff7e6;
  min-height: 100%;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;

    .header-left {
      .page-title {
        font-size: 20px;
        font-weight: 600;
        color: #2f220b;
        margin: 0 0 6px;
      }

      .page-desc {
        font-size: 12px;
        color: #8a6f3b;
        margin: 0;
      }
    }

    .header-right {
      .el-button {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .creation-notice {
    margin-bottom: 20px;
  }

  .creation-notice-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    width: 100%;

    span {
      min-width: 0;
      overflow-wrap: anywhere;
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
    margin-bottom: 14px;

    .stat-card {
      background: #fffdf8;
      border: 1px solid #ead7af;
      border-radius: 6px;
      padding: 12px 14px;
      text-align: left;
      border-left: 3px solid #d6ad60;
      box-shadow: 0 8px 22px rgb(118 82 18 / 6%);

      &.running {
        border-left-color: #67c23a;

        .stat-value {
          color: #67c23a;
        }
      }

      &.stopped {
        border-left-color: #e6a23c;

        .stat-value {
          color: #e6a23c;
        }
      }

      &.other {
        border-left-color: #409eff;

        .stat-value {
          color: #409eff;
        }
      }

      .stat-value {
        font-size: 22px;
        font-weight: 600;
        color: #2f220b;
        margin-bottom: 3px;
      }

      .stat-label {
        font-size: 12px;
        color: #8a6f3b;
      }
    }
  }

  .instance-workbench {
    display: flex;
    align-items: flex-start;
    gap: 14px;

    :deep(.instance-table-wrapper) {
      flex: 1 1 auto;
      min-width: 0;
    }
  }

  .instance-detail-host {
    position: fixed;
    inset: 0;
    z-index: 3000;
    display: none;
    align-items: center;
    justify-content: center;
    padding: 32px;
    background: rgb(47 34 11 / 42%);
    backdrop-filter: blur(3px);
  }

  .instance-detail-host.is-open {
    display: flex;
  }

  .instance-detail-dialog {
    position: relative;
  }

  .parent-detail-close {
    position: absolute;
    top: 18px;
    right: 18px;
    z-index: 3002;
    width: 32px;
    height: 32px;
    padding: 0;
    border: 1px solid rgb(128 82 0 / 22%);
    border-radius: 50%;
    background: rgb(255 255 255 / 88%);
    color: #5a3b08;
    cursor: pointer;
    font-size: 22px;
    line-height: 29px;
  }

  .parent-detail-close:hover {
    background: #fff;
    border-color: #c47a00;
    color: #a16000;
  }

  @media (max-width: 1180px) {
    .instance-workbench {
      flex-direction: column;

      :deep(.instance-table-wrapper) {
        width: 100%;
      }
    }

    .instance-detail-host {
      width: 100%;
      padding: 14px;
    }

    .parent-detail-close {
      top: 14px;
      right: 14px;
    }
  }
}

.vnc-dialog {
  min-height: 180px;

  .vnc-descriptions {
    margin-top: 14px;
  }

  .secret-value,
  .url-value {
    display: inline-block;
    max-width: 330px;
    margin-right: 10px;
    word-break: break-all;
    vertical-align: middle;
  }
}

</style>
