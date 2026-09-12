<template>
  <div class="instance-list-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">{{ pageTitle }}</h2>
        <p class="page-desc">{{ pageDesc }}</p>
      </div>
      <div class="header-right">
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

    <!-- 更多操作对话框 -->
    <MoreActionsDialog
      v-model="moreActionsVisible"
      :instance="currentInstance"
      @success="handleActionSuccess"
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
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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
import RenewDialog from './components/RenewDialog.vue'
import BatchRenewDialog from './components/BatchRenewDialog.vue'
import ShutdownScheduleDialog from './components/ShutdownScheduleDialog.vue'
import ToolsDialog from './components/ToolsDialog.vue'

const router = useRouter()
const route = useRoute()
const isGpuInstanceRoute = computed(() => route.path === '/cloud/gpuInstance')
const pageTitle = computed(() => isGpuInstanceRoute.value ? 'GPU实例列表' : String(route.meta?.title || '容器实例'))
const pageDesc = computed(() =>
  isGpuInstanceRoute.value
    ? '统一管理GPU算力资源，支持开机、关机、重启、续费和释放等操作'
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
  refreshMetrics,
  startAutoRefresh,
  stopAutoRefresh
} = useMetrics()

// 创建实例
const handleCreateInstance = () => {
  router.push(isGpuInstanceRoute.value ? '/computeListNew' : '/cloud/createInstance')
}

// 抽屉
const drawerVisible = ref(false)
const currentInstance = ref<Instance | null>(null)
const detailHostRef = ref<HTMLElement | null>(null)
const detailCloseRef = ref<HTMLButtonElement | null>(null)

// 更多操作对话框
const moreActionsVisible = ref(false)
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
  stopAutoRefresh()
  currentInstance.value = row
  syncDetailHostVisible(true)
  nextTick(() => syncDetailHostVisible(true))
  startAutoRefresh()
  fetchMetrics(row.id)
}

// 实例操作
const handleInstanceAction = async (action: string, instance: Instance) => {
  if (action === 'detail') {
    handleRowClick(instance)
    return
  }

  if (['resetPassword', 'setName'].includes(action)) {
    currentInstance.value = instance
    moreActionsVisible.value = true
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
  if (route.query.createSuccess === '1') {
    ElMessage.success('实例已提交创建，当前状态可在列表中查看')
  }
})

onBeforeUnmount(() => {
  detailCloseRef.value?.removeEventListener('click', handleNativeDetailClose, true)
  stopAutoRefresh()
})

onBeforeRouteLeave(() => {
  syncDetailHostVisible(false)
  stopAutoRefresh()
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
  stopAutoRefresh()
}
</script>

<style scoped lang="scss">
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
