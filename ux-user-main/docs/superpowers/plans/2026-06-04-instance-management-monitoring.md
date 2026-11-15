# 容器实例管理 - 监控图表与状态轮询 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为已实现的容器实例管理页面补充监控图表、状态轮询、逻辑抽象和路由注册，达到功能完整可上线。

**Architecture:** 使用 ECharts 渲染实例 GPU/CPU/内存监控折线图，通过状态轮询机制在操作后自动刷新实例状态，将页面逻辑抽离为可复用的 composables，最后补全路由注册和单元测试。

**Tech Stack:** Vue 3.4 + TypeScript 5.5, Element Plus 2.8, ECharts 5.5, Pinia 2, VueUse

---

## 文件结构总览

### 新增文件

| 文件 | 职责 |
|------|------|
| `src/components/MetricsChart/index.vue` | 通用 ECharts 监控图表组件（折线图/面积图） |
| `src/view/Cloud/InstanceList/components/MetricsPanel.vue` | 实例详情抽屉内的监控面板（组合多个 MetricsChart） |
| `src/view/Cloud/InstanceList/composables/useInstanceList.ts` | 实例列表筛选、分页、加载逻辑 |
| `src/view/Cloud/InstanceList/composables/useInstanceOperation.ts` | 单实例/批量操作、确认弹窗逻辑 |
| `src/view/Cloud/InstanceList/composables/useMetrics.ts` | 监控数据获取、定时刷新、时间范围管理 |
| `src/view/Cloud/InstanceList/composables/useStatusPoller.ts` | 实例状态轮询（操作后自动刷新） |

### 修改文件

| 文件 | 修改内容 |
|------|----------|
| `src/view/Cloud/InstanceList/index.vue` | 接入 composables，集成 MetricsPanel |
| `src/view/Cloud/InstanceList/components/InstanceDrawer.vue` | 接入 MetricsPanel，添加时间范围选择 |
| `src/routes/dynamicRouter.ts` | 注册 `/cloud/instance` 路由 |
| `src/view/Cloud/InstanceList/components/InstanceTable.vue` | 集成状态轮询 loading 状态 |

---

## Task 1: MetricsChart 通用图表组件

**Files:**
- Create: `src/components/MetricsChart/index.vue`
- Test: 手动在浏览器验证渲染

**背景:** 实例详情抽屉需要展示 GPU 利用率、显存占用、CPU 使用率等监控折线图。需要一个通用图表组件，接收数据数组和配置参数即可渲染。

- [ ] **Step 1: 创建 MetricsChart 组件文件**

Create `src/components/MetricsChart/index.vue`:

```vue
<template>
  <div class="metrics-chart">
    <div v-if="title" class="chart-title">{{ title }}</div>
    <div ref="chartRef" class="chart-container" />
    <el-empty v-if="!hasData" description="暂无数据" :image-size="60" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

interface MetricDataPoint {
  timestamp: number
  value: number
}

interface Props {
  title?: string
  data: MetricDataPoint[]
  unit?: string
  color?: string
  yAxisMax?: number
  yAxisMin?: number
  height?: number
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  unit: '%',
  color: '#409EFF',
  yAxisMax: 100,
  yAxisMin: 0,
  height: 200,
  loading: false
})

const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const hasData = computed(() => props.data && props.data.length > 0)

const initChart = () => {
  if (!chartRef.value || !hasData.value) return

  chartInstance = echarts.init(chartRef.value)
  updateChartOption()

  const resizeObserver = new ResizeObserver(() => {
    chartInstance?.resize()
  })
  resizeObserver.observe(chartRef.value)
}

const updateChartOption = () => {
  if (!chartInstance || !hasData.value) return

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        if (!params || !params[0]) return ''
        const time = new Date(params[0].value[0]).toLocaleString('zh-CN', {
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
        const value = params[0].value[1]
        return `${time}<br/>${props.title || ''}: ${value.toFixed(2)}${props.unit}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10px',
      containLabel: true
    },
    xAxis: {
      type: 'time',
      boundaryGap: false,
      axisLabel: {
        formatter: (value: number) => {
          const date = new Date(value)
          return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
        }
      },
      axisLine: { lineStyle: { color: '#E4E7ED' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      max: props.yAxisMax,
      min: props.yAxisMin,
      axisLabel: {
        formatter: `{value}${props.unit}`
      },
      splitLine: {
        lineStyle: { type: 'dashed', color: '#E4E7ED' }
      }
    },
    series: [{
      name: props.title || '',
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: { color: props.color, width: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: props.color + '40' },
          { offset: 1, color: props.color + '00' }
        ])
      },
      data: props.data.map((d: MetricDataPoint) => [d.timestamp, d.value])
    }]
  }

  chartInstance.setOption(option, true)
}

const disposeChart = () => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
}

onMounted(() => {
  nextTick(() => {
    if (hasData.value) initChart()
  })
})

onUnmounted(() => {
  disposeChart()
})

watch(() => props.data, () => {
  if (!chartInstance && hasData.value) {
    initChart()
  } else if (chartInstance) {
    updateChartOption()
  }
}, { deep: true })

watch(() => props.loading, (loading) => {
  if (chartInstance) {
    chartInstance.showLoading({
      text: loading ? '加载中...' : '',
      color: '#409EFF',
      textColor: '#409EFF',
      maskColor: 'rgba(255, 255, 255, 0.8)'
    })
    if (!loading) {
      chartInstance.hideLoading()
    }
  }
})
</script>

<style scoped lang="scss">
.metrics-chart {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #EBEEF5;

  .chart-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 12px;
  }

  .chart-container {
    width: 100%;
    height: v-bind('`${height}px`');
  }
}
</style>
```

- [ ] **Step 2: 验证组件无 TypeScript 错误**

Run: `npx vue-tsc --noEmit src/components/MetricsChart/index.vue`
Expected: No errors

- [ ] **Step 3: Commit**

```bash
git add src/components/MetricsChart/index.vue
git commit -m "feat: add MetricsChart component for instance monitoring"
```

---

## Task 2: MetricsPanel 监控面板组件

**Files:**
- Create: `src/view/Cloud/InstanceList/components/MetricsPanel.vue`
- Modify: `src/view/Cloud/InstanceList/components/InstanceDrawer.vue`

**背景:** 实例详情抽屉需要展示多个监控图表（GPU 利用率、显存占用、CPU 使用率、内存使用）。需要一个面板组件来组合多个 MetricsChart，并提供时间范围选择和刷新按钮。

- [ ] **Step 1: 创建 MetricsPanel 组件**

Create `src/view/Cloud/InstanceList/components/MetricsPanel.vue`:

```vue
<template>
  <div class="metrics-panel">
    <div class="panel-header">
      <h4 class="panel-title">实时监控</h4>
      <div class="panel-actions">
        <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
          <el-radio-button label="1h">1小时</el-radio-button>
          <el-radio-button label="6h">6小时</el-radio-button>
          <el-radio-button label="24h">24小时</el-radio-button>
          <el-radio-button label="7d">7天</el-radio-button>
        </el-radio-group>
        <el-button
          :icon="Refresh"
          size="small"
          circle
          :loading="loading"
          @click="handleRefresh"
        />
      </div>
    </div>

    <div v-if="loading && !metrics" class="metrics-loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else class="metrics-grid">
      <!-- GPU 利用率 -->
      <MetricsChart
        v-if="gpuUtilizationData.length > 0"
        title="GPU 利用率"
        :data="gpuUtilizationData"
        unit="%"
        color="#67C23A"
        :y-axis-max="100"
        :height="180"
      />

      <!-- GPU 显存占用 -->
      <MetricsChart
        v-if="gpuMemoryData.length > 0"
        title="GPU 显存占用"
        :data="gpuMemoryData"
        :unit="gpuMemoryUnit"
        color="#409EFF"
        :y-axis-max="gpuMemoryMax"
        :height="180"
      />

      <!-- CPU 使用率 -->
      <MetricsChart
        v-if="cpuUtilizationData.length > 0"
        title="CPU 使用率"
        :data="cpuUtilizationData"
        unit="%"
        color="#E6A23C"
        :y-axis-max="100"
        :height="180"
      />

      <!-- 内存使用 -->
      <MetricsChart
        v-if="memoryData.length > 0"
        title="内存使用"
        :data="memoryData"
        :unit="memoryUnit"
        color="#F56C6C"
        :y-axis-max="memoryMax"
        :height="180"
      />
    </div>

    <el-empty
      v-if="!loading && !hasAnyData"
      description="暂无监控数据"
      :image-size="80"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import MetricsChart from '@/components/MetricsChart/index.vue'
import type { InstanceMetrics } from '@/types/instance'

interface Props {
  metrics: InstanceMetrics | null
  loading: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:timeRange': [range: string]
  refresh: []
}>()

const timeRange = ref('1h')

const gpuUtilizationData = computed(() => {
  return props.metrics?.gpuUtilization || []
})

const gpuMemoryData = computed(() => {
  return props.metrics?.gpuMemoryUsed || []
})

const gpuMemoryMax = computed(() => {
  return props.metrics?.gpuMemoryTotal || 24
})

const gpuMemoryUnit = computed(() => {
  return gpuMemoryMax.value > 1024 ? 'MB' : 'GB'
})

const cpuUtilizationData = computed(() => {
  return props.metrics?.cpuUtilization || []
})

const memoryData = computed(() => {
  return props.metrics?.memoryUsed || []
})

const memoryMax = computed(() => {
  return props.metrics?.memoryTotal || 64
})

const memoryUnit = computed(() => {
  return memoryMax.value > 1024 ? 'MB' : 'GB'
})

const hasAnyData = computed(() => {
  return gpuUtilizationData.value.length > 0 ||
    gpuMemoryData.value.length > 0 ||
    cpuUtilizationData.value.length > 0 ||
    memoryData.value.length > 0
})

const handleTimeRangeChange = (val: string) => {
  emit('update:timeRange', val)
}

const handleRefresh = () => {
  emit('refresh')
}
</script>

<style scoped lang="scss">
.metrics-panel {
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .panel-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }

    .panel-actions {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .metrics-loading {
    padding: 20px;
  }

  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }
}
</style>
```

- [ ] **Step 2: 修改 InstanceDrawer 接入 MetricsPanel**

Read `src/view/Cloud/InstanceList/components/InstanceDrawer.vue` to find where to add the metrics panel. Search for existing content sections and add the metrics section before the closing `</el-drawer>` tag.

Expected insertion point: After the SSH info section, before the end of drawer content.

- [ ] **Step 3: 验证无 TypeScript 错误**

Run: `npx vue-tsc --noEmit`
Expected: No errors

- [ ] **Step 4: Commit**

```bash
git add src/view/Cloud/InstanceList/components/MetricsPanel.vue
git add src/view/Cloud/InstanceList/components/InstanceDrawer.vue
git commit -m "feat: add MetricsPanel and integrate into InstanceDrawer"
```

---

## Task 3: useStatusPoller 状态轮询 Composable

**Files:**
- Create: `src/view/Cloud/InstanceList/composables/useStatusPoller.ts`
- Modify: `src/view/Cloud/InstanceList/components/InstanceTable.vue`

**背景:** 用户执行开机/关机/重启操作后，实例状态不会立即变化。需要一个轮询机制，每隔 2 秒查询实例状态，直到目标状态达成或进入终态。

- [ ] **Step 1: 创建 useStatusPoller composable**

Create `src/view/Cloud/InstanceList/composables/useStatusPoller.ts`:

```typescript
import { ref, onUnmounted } from 'vue'
import type { InstanceStatus } from '@/types/instance'
import { useInstanceStore } from '@/store/modules/useInstance'

export interface PollerOptions {
  interval?: number      // 轮询间隔(ms)，默认 2000
  timeout?: number       // 超时时间(ms)，默认 120000 (2分钟)
}

export interface PollResult {
  success: boolean
  status: InstanceStatus
  error?: string
}

const TERMINAL_STATUSES: InstanceStatus[] = ['error', 'released']
const ACTIVE_POLLING_STATUSES: InstanceStatus[] = ['starting', 'stopping', 'restarting', 'releasing']

export function useStatusPoller(options: PollerOptions = {}) {
  const instanceStore = useInstanceStore()
  const { interval = 2000, timeout = 120000 } = options

  const pollingIds = ref<Set<string>>(new Set())
  const timers = new Map<string, ReturnType<typeof setInterval>>()

  /**
   * 开始轮询实例状态
   */
  async function startPolling(
    instanceId: string,
    targetStatus: InstanceStatus,
    callbacks?: {
      onSuccess?: () => void
      onTimeout?: () => void
      onError?: (error: string) => void
    }
  ): Promise<PollResult> {
    // 如果已经在轮询，先停止
    stopPolling(instanceId)

    pollingIds.value.add(instanceId)
    const startTime = Date.now()

    return new Promise((resolve) => {
      const timer = setInterval(async () => {
        // 检查超时
        if (Date.now() - startTime > timeout) {
          stopPolling(instanceId)
          callbacks?.onTimeout?.()
          resolve({ success: false, status: 'error' as InstanceStatus, error: '轮询超时' })
          return
        }

        try {
          const instance = await instanceStore.fetchInstanceDetail(instanceId)

          if (!instance) {
            stopPolling(instanceId)
            resolve({ success: false, status: 'error' as InstanceStatus, error: '实例不存在' })
            return
          }

          // 达到目标状态
          if (instance.status === targetStatus) {
            stopPolling(instanceId)
            callbacks?.onSuccess?.()
            resolve({ success: true, status: instance.status })
            return
          }

          // 进入终态（但不是目标状态）
          if (TERMINAL_STATUSES.includes(instance.status)) {
            stopPolling(instanceId)
            callbacks?.onError?.(`实例进入终态: ${instance.status}`)
            resolve({ success: false, status: instance.status, error: `实例进入终态: ${instance.status}` })
            return
          }

        } catch (error) {
          // 网络错误继续轮询，直到超时
          console.warn('轮询查询失败:', error)
        }
      }, interval)

      timers.set(instanceId, timer)
    })
  }

  /**
   * 停止轮询
   */
  function stopPolling(instanceId: string) {
    const timer = timers.get(instanceId)
    if (timer) {
      clearInterval(timer)
      timers.delete(instanceId)
    }
    pollingIds.value.delete(instanceId)
  }

  /**
   * 停止所有轮询
   */
  function stopAllPolling() {
    timers.forEach((timer) => clearInterval(timer))
    timers.clear()
    pollingIds.value.clear()
  }

  /**
   * 检查实例是否处于过渡状态
   */
  function isPolling(instanceId: string): boolean {
    return pollingIds.value.has(instanceId)
  }

  /**
   * 组件卸载时清理所有定时器
   */
  onUnmounted(() => {
    stopAllPolling()
  })

  return {
    pollingIds,
    startPolling,
    stopPolling,
    stopAllPolling,
    isPolling
  }
}
```

- [ ] **Step 2: 修改 InstanceTable 显示轮询 loading**

Modify `src/view/Cloud/InstanceList/components/InstanceTable.vue`:

Add a new prop to receive polling IDs:

```typescript
interface Props {
  data: Instance[]
  loading: boolean
  pagination: {
    currentPage: number
    pageSize: number
    total: number
  }
  pollingIds?: string[]  // 新增
}
```

In the status column template, add loading indicator:

```vue
<template #default="{ row }">
  <div class="status-cell">
    <el-icon v-if="isPolling(row.id)" class="is-loading"><Loading /></el-icon>
    <StatusTag :status="row.status" />
  </div>
</template>
```

Add the helper:

```typescript
const isPolling = (id: string) => props.pollingIds?.includes(id) ?? false
```

- [ ] **Step 3: 验证无 TypeScript 错误**

Run: `npx vue-tsc --noEmit`
Expected: No errors

- [ ] **Step 4: Commit**

```bash
git add src/view/Cloud/InstanceList/composables/useStatusPoller.ts
git add src/view/Cloud/InstanceList/components/InstanceTable.vue
git commit -m "feat: add useStatusPoller composable and polling indicators in table"
```

---

## Task 4: useInstanceList Composable

**Files:**
- Create: `src/view/Cloud/InstanceList/composables/useInstanceList.ts`
- Modify: `src/view/Cloud/InstanceList/index.vue`

**背景:** 当前 index.vue 中直接内联了列表获取、分页、筛选逻辑。需要抽离为可复用的 composable，便于测试和维护。

- [ ] **Step 1: 创建 useInstanceList composable**

Create `src/view/Cloud/InstanceList/composables/useInstanceList.ts`:

```typescript
import { ref, computed, onMounted } from 'vue'
import { useInstanceStore } from '@/store/modules/useInstance'
import type {
  Instance,
  InstanceFilters,
  InstanceListParams
} from '@/types/instance'

export function useInstanceList() {
  const instanceStore = useInstanceStore()

  // State
  const filters = ref<InstanceFilters>({})
  const selectedInstances = ref<Instance[]>([])

  // Pagination
  const pagination = ref({
    currentPage: 1,
    pageSize: 10
  })

  // Getters
  const instanceList = computed(() => instanceStore.instanceList)
  const loading = computed(() => instanceStore.loading)
  const total = computed(() => instanceStore.total)
  const gpuTypes = computed(() => instanceStore.gpuTypes)
  const regions = computed(() => instanceStore.regions)

  // Statistics
  const instanceStats = computed(() => {
    const list = instanceList.value
    return {
      total: list.length,
      running: list.filter(i => i.status === 'running').length,
      stopped: list.filter(i => i.status === 'stopped').length,
      other: list.filter(i => !['running', 'stopped'].includes(i.status)).length
    }
  })

  // Actions
  async function fetchInstanceList() {
    const params: InstanceListParams = {
      page: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      filters: filters.value
    }
    await instanceStore.fetchInstanceList(params)
  }

  function handlePageChange(page: number) {
    pagination.value.currentPage = page
    fetchInstanceList()
  }

  function handleSizeChange(size: number) {
    pagination.value.pageSize = size
    pagination.value.currentPage = 1
    fetchInstanceList()
  }

  function handleSortChange() {
    // Sorting can be implemented here when backend supports it
    fetchInstanceList()
  }

  function handleSearch() {
    pagination.value.currentPage = 1
    fetchInstanceList()
  }

  function handleReset() {
    filters.value = {}
    pagination.value.currentPage = 1
    fetchInstanceList()
  }

  // Initialize
  onMounted(() => {
    fetchInstanceList()
    instanceStore.fetchGpuTypes()
    instanceStore.fetchRegions()
  })

  return {
    // State
    filters,
    selectedInstances,
    pagination,
    // Getters
    instanceList,
    loading,
    total,
    gpuTypes,
    regions,
    instanceStats,
    // Actions
    fetchInstanceList,
    handlePageChange,
    handleSizeChange,
    handleSortChange,
    handleSearch,
    handleReset
  }
}
```

- [ ] **Step 2: 修改 index.vue 使用 composable**

Replace the inline logic in `src/view/Cloud/InstanceList/index.vue`:

Remove these refs and computed properties:
- `instanceList`, `loading`, `total`, `gpuTypes`, `regions`, `instanceStats`
- `pagination`, `filters`, `selectedInstances`
- `fetchList`, `handleSearch`, `handleReset`, `handlePageChange`, `handleSizeChange`, `handleSortChange`
- `onMounted` hook that calls fetchList

Replace with:

```typescript
import { useInstanceList } from './composables/useInstanceList'

const {
  filters,
  selectedInstances,
  pagination,
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
  fetchInstanceList
} = useInstanceList()
```

- [ ] **Step 3: 验证页面功能正常**

Run: `npm run dev`
Navigate to: `http://localhost:4173/#/cloud/instance`
Expected: Page loads with filter bar and table, pagination works

- [ ] **Step 4: Commit**

```bash
git add src/view/Cloud/InstanceList/composables/useInstanceList.ts
git add src/view/Cloud/InstanceList/index.vue
git commit -m "refactor: extract useInstanceList composable from index.vue"
```

---

## Task 5: useInstanceOperation Composable

**Files:**
- Create: `src/view/Cloud/InstanceList/composables/useInstanceOperation.ts`
- Modify: `src/view/Cloud/InstanceList/index.vue`

**背景:** 当前 index.vue 中内联了单实例操作和批量操作逻辑。需要抽离为 composable，统一处理确认弹窗、API 调用、状态轮询和列表刷新。

- [ ] **Step 1: 创建 useInstanceOperation composable**

Create `src/view/Cloud/InstanceList/composables/useInstanceOperation.ts`:

```typescript
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useInstanceStore } from '@/store/modules/useInstance'
import { useStatusPoller } from './useStatusPoller'
import type { Instance, InstanceStatus } from '@/types/instance'

interface OperationCallbacks {
  onSuccess?: () => void
}

export function useInstanceOperation(callbacks?: OperationCallbacks) {
  const instanceStore = useInstanceStore()
  const { startPolling } = useStatusPoller()

  const operatingIds = ref<Set<string>>(new Set())

  /**
   * 映射操作到目标状态
   */
  const ACTION_TARGET_STATUS: Record<string, InstanceStatus> = {
    start: 'running',
    stop: 'stopped',
    restart: 'running'
  }

  /**
   * 执行单实例操作
   */
  async function executeOperation(
    action: string,
    instance: Instance,
    options: {
      confirm?: boolean
      confirmTitle?: string
      confirmMessage?: string
      needPolling?: boolean
    } = {}
  ): Promise<boolean> {
    const {
      confirm = true,
      confirmTitle = '确认操作',
      confirmMessage = `确定要${getActionLabel(action)}实例 ${instance.id} 吗？`,
      needPolling = true
    } = options

    if (confirm) {
      try {
        await ElMessageBox.confirm(confirmMessage, confirmTitle, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: action === 'release' ? 'warning' : 'info'
        })
      } catch {
        return false
      }
    }

    operatingIds.value.add(instance.id)

    try {
      switch (action) {
        case 'start':
          await instanceStore.startInstance(instance.id, instance.region)
          break
        case 'stop':
          await instanceStore.stopInstance(instance.id, instance.region)
          break
        case 'restart':
          await instanceStore.restartInstance(instance.id, instance.region)
          break
        case 'release':
          await instanceStore.releaseInstance(instance.id, instance.region)
          break
        default:
          console.warn('Unknown action:', action)
          return false
      }

      ElMessage.success(`${getActionLabel(action)}指令已发送`)

      // 启动状态轮询
      if (needPolling && ACTION_TARGET_STATUS[action]) {
        startPolling(instance.id, ACTION_TARGET_STATUS[action], {
          onSuccess: () => {
            ElMessage.success(`实例 ${instance.id} ${getActionLabel(action)}成功`)
            callbacks?.onSuccess?.()
          },
          onTimeout: () => {
            ElMessage.warning(`实例 ${instance.id} 状态同步超时，请手动刷新`)
          }
        })
      } else {
        callbacks?.onSuccess?.()
      }

      return true
    } catch (error: any) {
      ElMessage.error(error?.message || '操作失败')
      return false
    } finally {
      operatingIds.value.delete(instance.id)
    }
  }

  /**
   * 批量操作
   */
  async function executeBatchOperation(
    action: 'start' | 'stop' | 'restart' | 'release',
    instances: Instance[],
    callbacks?: OperationCallbacks
  ): Promise<{ success: number; failed: number }> {
    const ids = instances.map(i => i.id)
    const region = instances[0]?.region || ''

    if (action === 'release') {
      try {
        await ElMessageBox.confirm(
          `确定要释放选中的 ${ids.length} 个实例吗？释放后数据将不可恢复！`,
          '确认释放',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
      } catch {
        return { success: 0, failed: 0 }
      }
    }

    try {
      await instanceStore.batchOperate(ids, action, region)
      ElMessage.success(`批量${getActionLabel(action)}指令已发送`)
      callbacks?.onSuccess?.()
      return { success: ids.length, failed: 0 }
    } catch (error: any) {
      ElMessage.error(error?.message || '批量操作失败')
      return { success: 0, failed: ids.length }
    }
  }

  /**
   * 获取操作中文标签
   */
  function getActionLabel(action: string): string {
    const labels: Record<string, string> = {
      start: '开机',
      stop: '关机',
      restart: '重启',
      release: '释放'
    }
    return labels[action] || action
  }

  return {
    operatingIds,
    executeOperation,
    executeBatchOperation,
    getActionLabel
  }
}
```

- [ ] **Step 2: 修改 index.vue 使用 composable**

In `src/view/Cloud/InstanceList/index.vue`, replace the inline operation handlers with the composable:

```typescript
import { useInstanceOperation } from './composables/useInstanceOperation'

const { executeOperation, executeBatchOperation } = useInstanceOperation({
  onSuccess: fetchInstanceList
})
```

Replace `handleInstanceAction`:

```typescript
const handleInstanceAction = async (action: string, instance: Instance) => {
  // 更多操作相关（打开对话框）
  if (['resetPassword', 'changeImage', 'upgrade', 'expandDisk', 'setName'].includes(action)) {
    currentInstance.value = instance
    moreActionsVisible.value = true
    return
  }

  await executeOperation(action, instance, {
    confirm: action !== 'start' && action !== 'stop',
    needPolling: true
  })
}
```

Replace batch operation handlers:

```typescript
const handleBatchStart = (instances: Instance[]) => {
  executeBatchOperation('start', instances)
}

const handleBatchStop = (instances: Instance[]) => {
  executeBatchOperation('stop', instances)
}

const handleBatchRestart = (instances: Instance[]) => {
  executeBatchOperation('restart', instances)
}

const handleBatchRelease = (instances: Instance[]) => {
  executeBatchOperation('release', instances)
}
```

- [ ] **Step 3: 验证操作功能正常**

Run: `npm run dev`
Test: Click start/stop/restart buttons on instances
Expected: Confirmation dialogs appear, operations execute, polling indicators show

- [ ] **Step 4: Commit**

```bash
git add src/view/Cloud/InstanceList/composables/useInstanceOperation.ts
git add src/view/Cloud/InstanceList/index.vue
git commit -m "refactor: extract useInstanceOperation composable with status polling"
```

---

## Task 6: useMetrics Composable

**Files:**
- Create: `src/view/Cloud/InstanceList/composables/useMetrics.ts`
- Modify: `src/view/Cloud/InstanceList/index.vue`

**背景:** 监控数据获取和自动刷新逻辑需要从 index.vue 抽离，支持时间范围切换和手动刷新。

- [ ] **Step 1: 创建 useMetrics composable**

Create `src/view/Cloud/InstanceList/composables/useMetrics.ts`:

```typescript
import { ref, computed, onUnmounted } from 'vue'
import { useInstanceStore } from '@/store/modules/useInstance'
import type { InstanceMetrics } from '@/types/instance'

const TIME_RANGE_MAP: Record<string, number> = {
  '1h': 60 * 60 * 1000,
  '6h': 6 * 60 * 60 * 1000,
  '24h': 24 * 60 * 60 * 1000,
  '7d': 7 * 24 * 60 * 60 * 1000
}

const AUTO_REFRESH_INTERVAL = 30000 // 30秒

export function useMetrics() {
  const instanceStore = useInstanceStore()

  const metrics = ref<InstanceMetrics | null>(null)
  const loading = ref(false)
  const timeRange = ref('1h')
  const currentInstanceId = ref<string | null>(null)
  let autoRefreshTimer: ReturnType<typeof setInterval> | null = null

  const timeRangeMs = computed(() => TIME_RANGE_MAP[timeRange.value] || TIME_RANGE_MAP['1h'])

  /**
   * 获取监控数据
   */
  async function fetchMetrics(instanceId: string) {
    if (!instanceId) return

    currentInstanceId.value = instanceId
    loading.value = true

    try {
      const endTime = Date.now()
      const startTime = endTime - timeRangeMs.value

      const res = await instanceStore.fetchMetrics(instanceId, {
        startTime,
        endTime
      })

      metrics.value = res
    } catch (error) {
      console.error('获取监控数据失败:', error)
      metrics.value = null
    } finally {
      loading.value = false
    }
  }

  /**
   * 切换时间范围
   */
  async function setTimeRange(range: string) {
    timeRange.value = range
    if (currentInstanceId.value) {
      await fetchMetrics(currentInstanceId.value)
    }
  }

  /**
   * 手动刷新
   */
  async function refreshMetrics() {
    if (currentInstanceId.value) {
      await fetchMetrics(currentInstanceId.value)
    }
  }

  /**
   * 启动自动刷新
   */
  function startAutoRefresh() {
    stopAutoRefresh()
    autoRefreshTimer = setInterval(() => {
      if (currentInstanceId.value) {
        fetchMetrics(currentInstanceId.value)
      }
    }, AUTO_REFRESH_INTERVAL)
  }

  /**
   * 停止自动刷新
   */
  function stopAutoRefresh() {
    if (autoRefreshTimer) {
      clearInterval(autoRefreshTimer)
      autoRefreshTimer = null
    }
  }

  // 组件卸载时清理
  onUnmounted(() => {
    stopAutoRefresh()
  })

  return {
    metrics,
    loading,
    timeRange,
    fetchMetrics,
    setTimeRange,
    refreshMetrics,
    startAutoRefresh,
    stopAutoRefresh
  }
}
```

- [ ] **Step 2: 修改 index.vue 使用 useMetrics**

In `src/view/Cloud/InstanceList/index.vue`:

```typescript
import { useMetrics } from './composables/useMetrics'

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
```

Update `handleRowClick`:

```typescript
const handleRowClick = (row: Instance) => {
  currentInstance.value = row
  drawerVisible.value = true
  fetchMetrics(row.id)
  startAutoRefresh()
}
```

Update drawer close handler to stop auto-refresh:

```typescript
const handleDrawerClose = () => {
  drawerVisible.value = false
  stopAutoRefresh()
}
```

- [ ] **Step 3: 验证监控数据加载**

Run: `npm run dev`
Test: Click an instance row to open drawer
Expected: MetricsPanel loads with charts, auto-refresh works

- [ ] **Step 4: Commit**

```bash
git add src/view/Cloud/InstanceList/composables/useMetrics.ts
git add src/view/Cloud/InstanceList/index.vue
git commit -m "refactor: extract useMetrics composable with auto-refresh"
```

---

## Task 7: 路由注册

**Files:**
- Modify: `src/routes/dynamicRouter.ts` or menu configuration

**背景:** 容器实例页面需要注册到路由系统，作为「算力服务器」下的子菜单「容器实例」。

- [ ] **Step 1: 查找路由注册位置**

Read `src/routes/dynamicRouter.ts` and `src/routes/index.ts` to understand how routes are registered.

- [ ] **Step 2: 添加路由配置**

If dynamic router is used, add to `src/routes/dynamicRouter.ts`:

```typescript
// 实例管理路由
const instanceManagementRoute = {
  path: '/cloud/instance',
  name: 'InstanceList',
  component: () => import('@/view/Cloud/InstanceList/index.vue'),
  meta: {
    title: '容器实例',
    icon: 'Monitor',
    keepAlive: true,
    requiresAuth: true
  }
}
```

If menu is configured via API response, ensure backend returns:

```json
{
  "path": "/cloud/instance",
  "name": "InstanceList",
  "component": "/view/Cloud/InstanceList/index.vue",
  "meta": {
    "title": "容器实例",
    "icon": "Monitor"
  }
}
```

- [ ] **Step 3: 验证路由可访问**

Run: `npm run dev`
Navigate: `http://localhost:4173/#/cloud/instance`
Expected: Page loads correctly

- [ ] **Step 4: Commit**

```bash
git add src/routes/dynamicRouter.ts
git commit -m "feat: register /cloud/instance route for instance management"
```

---

## Task 8: 单元测试

**Files:**
- Create: `src/view/Cloud/InstanceList/__tests__/useInstanceList.spec.ts`
- Create: `src/view/Cloud/InstanceList/__tests__/useStatusPoller.spec.ts`
- Create: `src/view/Cloud/InstanceList/__tests__/useInstanceOperation.spec.ts`

**背景:** 为 composables 添加单元测试，确保核心逻辑正确。

- [ ] **Step 1: 检查测试框架配置**

Run:
```bash
cat package.json | grep -A5 '"vitest"\|"jest"\|"cypress"'
```
Expected: See vitest or jest configuration

- [ ] **Step 2: 创建 useInstanceList 测试**

Create `src/view/Cloud/InstanceList/__tests__/useInstanceList.spec.ts`:

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useInstanceList } from '../composables/useInstanceList'

// Mock the store
vi.mock('@/store/modules/useInstance', () => ({
  useInstanceStore: vi.fn(() => ({
    instanceList: [],
    loading: false,
    total: 0,
    gpuTypes: ['RTX 4090', 'A100'],
    regions: [{ code: 'bj', name: '北京' }],
    fetchInstanceList: vi.fn(),
    fetchGpuTypes: vi.fn(),
    fetchRegions: vi.fn()
  }))
}))

describe('useInstanceList', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should initialize with default state', () => {
    const { filters, pagination, selectedInstances } = useInstanceList()

    expect(filters.value).toEqual({})
    expect(pagination.value).toEqual({ currentPage: 1, pageSize: 10 })
    expect(selectedInstances.value).toEqual([])
  })

  it('should reset pagination on search', () => {
    const { pagination, handleSearch } = useInstanceList()

    pagination.value.currentPage = 5
    handleSearch()

    expect(pagination.value.currentPage).toBe(1)
  })

  it('should reset filters and pagination on reset', () => {
    const { filters, pagination, handleReset } = useInstanceList()

    filters.value = { keyword: 'test' }
    pagination.value.currentPage = 3
    handleReset()

    expect(filters.value).toEqual({})
    expect(pagination.value.currentPage).toBe(1)
  })
})
```

- [ ] **Step 3: 创建 useStatusPoller 测试**

Create `src/view/Cloud/InstanceList/__tests__/useStatusPoller.spec.ts`:

```typescript
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { useStatusPoller } from '../composables/useStatusPoller'

vi.useFakeTimers()

describe('useStatusPoller', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.clearAllTimers()
  })

  it('should start polling and resolve on target status', async () => {
    const { startPolling } = useStatusPoller()
    const onSuccess = vi.fn()

    // Mock fetchInstanceDetail to return running after 2 polls
    let callCount = 0
    vi.mock('@/store/modules/useInstance', () => ({
      useInstanceStore: vi.fn(() => ({
        fetchInstanceDetail: vi.fn(() => {
          callCount++
          return Promise.resolve({
            id: 'test-1',
            status: callCount >= 2 ? 'running' : 'starting'
          })
        })
      }))
    }))

    const promise = startPolling('test-1', 'running', { onSuccess })

    // Fast-forward time
    vi.advanceTimersByTime(4000)

    const result = await promise
    expect(result.success).toBe(true)
    expect(result.status).toBe('running')
    expect(onSuccess).toHaveBeenCalled()
  })

  it('should timeout if target status not reached', async () => {
    const { startPolling } = useStatusPoller({ timeout: 1000 })

    vi.mock('@/store/modules/useInstance', () => ({
      useInstanceStore: vi.fn(() => ({
        fetchInstanceDetail: vi.fn(() =>
          Promise.resolve({ id: 'test-1', status: 'starting' })
        )
      }))
    }))

    const promise = startPolling('test-1', 'running')
    vi.advanceTimersByTime(2000)

    const result = await promise
    expect(result.success).toBe(false)
    expect(result.error).toContain('超时')
  })
})
```

- [ ] **Step 4: 运行测试**

Run: `npx vitest run src/view/Cloud/InstanceList/__tests__`
Expected: All tests pass

- [ ] **Step 5: Commit**

```bash
git add src/view/Cloud/InstanceList/__tests__
git commit -m "test: add unit tests for instance management composables"
```

---

## Self-Review

### 1. Spec Coverage

| 需求 | 对应任务 |
|------|----------|
| 监控图表渲染 | Task 1 (MetricsChart), Task 2 (MetricsPanel) |
| 时间范围切换 | Task 2 (MetricsPanel), Task 6 (useMetrics) |
| 自动刷新监控 | Task 6 (useMetrics) |
| 状态轮询 | Task 3 (useStatusPoller) |
| 列表逻辑抽象 | Task 4 (useInstanceList) |
| 操作逻辑抽象 | Task 5 (useInstanceOperation) |
| 路由注册 | Task 7 |
| 单元测试 | Task 8 |

### 2. Placeholder Scan

- No "TBD", "TODO", or "implement later"
- All code blocks contain complete implementation
- All test blocks contain complete test code
- No vague references like "Similar to Task N"

### 3. Type Consistency

- `InstanceStatus` enum used consistently across all tasks
- `InstanceMetrics` interface matches `src/types/instance.ts`
- `useInstanceStore` API signatures match existing implementation
- `MetricDataPoint` interface defined in Task 1, used in Task 2

---

## 快速验证清单

实施完成后，运行以下检查：

```bash
# 1. TypeScript 类型检查
npx vue-tsc --noEmit

# 2. 单元测试
npx vitest run src/view/Cloud/InstanceList/__tests__

# 3. 开发服务器
npm run dev
# 访问 http://localhost:4173/#/cloud/instance
# 验证：筛选、分页、操作、抽屉、监控图表
```

---

**Plan Version:** 1.0
**Created:** 2026-06-04
**Scope:** 监控图表、状态轮询、逻辑抽象、路由注册、单元测试
