# 技术设计文档: AutoDL 实例管理功能

## 1. 概述

### 1.1 设计目标

实现一个完整的 GPU 实例管理功能，参考 AutoDL 控制台的设计，提供：
- 实例列表展示与筛选
- 实例生命周期操作（开机/关机/重启/释放）
- 批量操作能力
- 实例详情与监控

### 1.2 技术栈

- **框架**: Vue 3.4 + TypeScript 5.5
- **构建**: Vite 5.4
- **UI**: Element Plus 2.8
- **状态**: Pinia 2 + persistedstate
- **图表**: ECharts 5.5
- **HTTP**: Axios

### 1.3 与现有系统的集成

```
┌─────────────────────────────────────────────────────────────┐
│                      现有系统架构                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  门户首页     │  │  算力市场     │  │  购买流程     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              新增: 实例管理模块                            │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐            │ │
│  │  │ 实例列表  │  │ 实例详情  │  │ 监控图表  │            │ │
│  │  └──────────┘  └──────────┘  └──────────┘            │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  控制台布局   │  │  用户中心     │  │  订单管理     │      │
│  │  (layout)    │  │              │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 数据模型

### 2.1 实例状态枚举

```typescript
// src/types/instance.ts

export enum InstanceStatus {
  CREATING = 'creating',       // 创建中
  RUNNING = 'running',         // 运行中
  STOPPED = 'stopped',         // 已关机
  STARTING = 'starting',       // 开机中
  STOPPING = 'stopping',       // 关机中
  RESTARTING = 'restarting',   // 重启中
  RELEASING = 'releasing',     // 释放中
  RELEASED = 'released',       // 已释放
  EXPIRED = 'expired',         // 已过期
  ERROR = 'error'              // 错误
}

export const StatusMap: Record<InstanceStatus, { label: string; type: string; color: string }> = {
  [InstanceStatus.CREATING]: { label: '创建中', type: 'info', color: '#909399' },
  [InstanceStatus.RUNNING]: { label: '运行中', type: 'success', color: '#67C23A' },
  [InstanceStatus.STOPPED]: { label: '已关机', type: 'info', color: '#909399' },
  [InstanceStatus.STARTING]: { label: '开机中', type: 'warning', color: '#E6A23C' },
  [InstanceStatus.STOPPING]: { label: '关机中', type: 'warning', color: '#E6A23C' },
  [InstanceStatus.RESTARTING]: { label: '重启中', type: 'warning', color: '#E6A23C' },
  [InstanceStatus.RELEASING]: { label: '释放中', type: 'danger', color: '#F56C6C' },
  [InstanceStatus.RELEASED]: { label: '已释放', type: 'info', color: '#909399' },
  [InstanceStatus.EXPIRED]: { label: '已过期', type: 'danger', color: '#F56C6C' },
  [InstanceStatus.ERROR]: { label: '错误', type: 'danger', color: '#F56C6C' }
}
```

### 2.2 实例数据模型

```typescript
// src/types/instance.ts

export interface Instance {
  id: string                          // 实例ID
  uuid: string                        // 实例UUID
  name: string                        // 实例名称
  description?: string                // 实例描述
  
  // 状态信息
  status: InstanceStatus              // 实例状态
  statusMessage?: string              // 状态描述信息
  
  // GPU配置
  gpuType: string                     // GPU型号，如 RTX 4090
  gpuCount: number                    // GPU数量
  gpuMemory: number                   // 单卡显存(GB)
  
  // CPU配置
  cpuCores: number                    // CPU核心数
  cpuModel?: string                   // CPU型号
  
  // 内存配置
  memory: number                      // 内存大小(GB)
  
  // 磁盘配置
  systemDisk: number                  // 系统盘大小(GB)
  dataDisk: number                    // 数据盘大小(GB)
  
  // 网络配置
  region: string                      // 区域，如 北京A区
  regionCode: string                  // 区域代码
  privateIp?: string                  // 内网IP
  publicIp?: string                   // 公网IP
  sshPort?: number                    // SSH端口
  
  // 登录信息
  sshCommand?: string                 // SSH登录命令
  rootPassword?: string               // root密码（脱敏显示）
  jupyterUrl?: string                 // JupyterLab地址
  
  // 时间信息
  createdAt: string                   // 创建时间
  expiredAt?: string                  // 过期时间
  startedAt?: string                  // 开机时间
  stoppedAt?: string                  // 关机时间
  
  // 计费信息
  billingType: 'hourly' | 'monthly'   // 计费方式
  pricePerHour: number               // 每小时价格
}
```

### 2.3 筛选条件模型

```typescript
// src/types/instance.ts

export interface InstanceFilters {
  gpuTypes?: string[]           // GPU类型筛选
  regions?: string[]            // 区域筛选
  statuses?: InstanceStatus[]   // 状态筛选
  keyword?: string              // 关键词搜索
}

export interface InstanceListParams {
  page: number                  // 当前页
  pageSize: number             // 每页条数
  filters?: InstanceFilters    // 筛选条件
  sortBy?: string              // 排序字段
  sortOrder?: 'asc' | 'desc'   // 排序方式
}

export interface InstanceListResult {
  list: Instance[]             // 实例列表
  total: number                // 总数
  page: number                 // 当前页
  pageSize: number            // 每页条数
}
```

### 2.4 监控数据模型

```typescript
// src/types/metrics.ts

export interface MetricDataPoint {
  timestamp: number           // 时间戳
  value: number               // 数值
}

export interface InstanceMetrics {
  instanceId: string
  timeRange: [number, number] // 时间范围
  
  // GPU指标
  gpuUtilization: MetricDataPoint[]     // GPU使用率(%)
  gpuMemoryUsed: MetricDataPoint[]      // GPU显存使用(GB)
  gpuMemoryTotal: number                // GPU显存总量(GB)
  gpuTemperature?: MetricDataPoint[]     // GPU温度(°C)
  
  // CPU指标
  cpuUtilization: MetricDataPoint[]     // CPU使用率(%)
  cpuLoad?: MetricDataPoint[]            // CPU负载
  
  // 内存指标
  memoryUsed: MetricDataPoint[]          // 内存使用(GB)
  memoryTotal: number                    // 内存总量(GB)
  
  // 磁盘指标
  diskRead?: MetricDataPoint[]            // 磁盘读取(MB/s)
  diskWrite?: MetricDataPoint[]           // 磁盘写入(MB/s)
  
  // 网络指标
  networkIn?: MetricDataPoint[]           // 网络入流量(MB/s)
  networkOut?: MetricDataPoint[]          // 网络出流量(MB/s)
}
```

---

## 3. API 接口设计

### 3.1 实例管理接口

```typescript
// src/api/instance.ts

import request from '@/utils/request'
import type { 
  Instance, 
  InstanceListParams, 
  InstanceListResult,
  InstanceMetrics 
} from '@/types'

/**
 * 获取实例列表
 */
export function getInstanceList(params: InstanceListParams) {
  return request.post<InstanceListResult>('/api/instance/list', params)
}

/**
 * 获取实例详情
 */
export function getInstanceDetail(id: string) {
  return request.get<Instance>(`/api/instance/${id}`)
}

/**
 * 开机
 */
export function startInstance(id: string) {
  return request.post(`/api/instance/${id}/start`)
}

/**
 * 关机
 */
export function stopInstance(id: string) {
  return request.post(`/api/instance/${id}/stop`)
}

/**
 * 重启
 */
export function restartInstance(id: string) {
  return request.post(`/api/instance/${id}/restart`)
}

/**
 * 释放（删除）
 */
export function releaseInstance(id: string) {
  return request.post(`/api/instance/${id}/release`)
}

/**
 * 批量操作
 */
export function batchOperateInstances(ids: string[], action: 'start' | 'stop' | 'restart' | 'release') {
  return request.post('/api/instance/batch', { ids, action })
}

/**
 * 获取实例监控数据
 */
export function getInstanceMetrics(
  id: string, 
  params: { startTime: number; endTime: number; metrics?: string[] }
) {
  return request.get<InstanceMetrics>(`/api/instance/${id}/metrics`, { params })
}

/**
 * 获取GPU类型列表（用于筛选）
 */
export function getGpuTypes() {
  return request.get<string[]>('/api/gpu/types')
}

/**
 * 获取区域列表（用于筛选）
 */
export function getRegions() {
  return request.get<{ code: string; name: string }[]>('/api/regions')
}
```

---

## 4. 组件设计

### 4.1 组件架构

```
src/view/Cloud/InstanceList/
├── index.vue                 # 主页面
├── components/
│   ├── FilterBar.vue         # 顶部筛选栏
│   ├── InstanceTable.vue     # 实例表格
│   ├── BatchActions.vue      # 批量操作栏
│   ├── InstanceDrawer.vue    # 实例详情抽屉
│   ├── MetricsPanel.vue      # 监控面板
│   └── StatusTag.vue         # 状态标签
├── composables/
│   ├── useInstanceList.ts    # 列表逻辑
│   ├── useInstanceOperation.ts # 操作逻辑
│   └── useMetrics.ts         # 监控逻辑
└── types.ts                  # 本地类型定义
```

### 4.2 核心组件接口

#### InstanceList/index.vue

```vue
<template>
  <div class="instance-list-page">
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
      v-model:selected="selectedInstances"
      :instances="instanceList"
      @batch-start="handleBatchStart"
      @batch-stop="handleBatchStop"
      @batch-restart="handleBatchRestart"
      @batch-release="handleBatchRelease"
    />
    
    <!-- 实例表格 -->
    <InstanceTable
      v-model:selected="selectedInstances"
      :data="instanceList"
      :loading="loading"
      :pagination="pagination"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      @sort-change="handleSortChange"
      @row-click="handleRowClick"
      @action="handleInstanceAction"
    />
    
    <!-- 实例详情抽屉 -->
    <InstanceDrawer
      v-model="drawerVisible"
      :instance="currentInstance"
      :metrics="currentMetrics"
      :metrics-loading="metricsLoading"
      @refresh="handleRefreshDetail"
      @action="handleInstanceAction"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useInstanceList } from './composables/useInstanceList'
import { useInstanceOperation } from './composables/useInstanceOperation'
import { useMetrics } from './composables/useMetrics'

import FilterBar from './components/FilterBar.vue'
import BatchActions from './components/BatchActions.vue'
import InstanceTable from './components/InstanceTable.vue'
import InstanceDrawer from './components/InstanceDrawer.vue'

// 列表逻辑
const {
  instanceList,
  loading,
  pagination,
  filters,
  gpuTypes,
  regions,
  fetchInstanceList,
  handlePageChange,
  handleSizeChange,
  handleSortChange,
  handleSearch,
  handleReset
} = useInstanceList()

// 操作逻辑
const {
  selectedInstances,
  handleBatchStart,
  handleBatchStop,
  handleBatchRestart,
  handleBatchRelease,
  handleInstanceAction,
  refreshList
} = useInstanceOperation({ fetchInstanceList })

// 详情和监控
const drawerVisible = ref(false)
const currentInstance = ref<Instance | null>(null)

const {
  metrics: currentMetrics,
  loading: metricsLoading,
  fetchMetrics
} = useMetrics()

// 点击行打开详情
const handleRowClick = (row: Instance) => {
  currentInstance.value = row
  drawerVisible.value = true
  fetchMetrics(row.id)
}

// 刷新详情
const handleRefreshDetail = () => {
  if (currentInstance.value) {
    fetchMetrics(currentInstance.value.id)
  }
}

onMounted(() => {
  fetchInstanceList()
})
</script>
```

---

## 5. 状态管理 (Pinia Store)

### useInstanceStore.ts

```typescript
// src/store/modules/useInstance.ts

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { 
  Instance, 
  InstanceListParams, 
  InstanceListResult,
  InstanceFilters,
  InstanceMetrics 
} from '@/types'
import * as instanceApi from '@/api/instance'

export const useInstanceStore = defineStore('instance', () => {
  // State
  const instanceList = ref<Instance[]>([])
  const total = ref(0)
  const loading = ref(false)
  const currentInstance = ref<Instance | null>(null)
  const currentMetrics = ref<InstanceMetrics | null>(null)
  const gpuTypes = ref<string[]>([])
  const regions = ref<{ code: string; name: string }[]>([])

  // Getters
  const runningInstances = computed(() => 
    instanceList.value.filter(i => i.status === 'running')
  )
  
  const stoppedInstances = computed(() => 
    instanceList.value.filter(i => i.status === 'stopped')
  )

  // Actions
  async function fetchInstanceList(params: InstanceListParams) {
    loading.value = true
    try {
      const res = await instanceApi.getInstanceList(params)
      instanceList.value = res.list
      total.value = res.total
      return res
    } finally {
      loading.value = false
    }
  }

  async function fetchInstanceDetail(id: string) {
    const res = await instanceApi.getInstanceDetail(id)
    currentInstance.value = res
    return res
  }

  async function startInstance(id: string) {
    return instanceApi.startInstance(id)
  }

  async function stopInstance(id: string) {
    return instanceApi.stopInstance(id)
  }

  async function restartInstance(id: string) {
    return instanceApi.restartInstance(id)
  }

  async function releaseInstance(id: string) {
    return instanceApi.releaseInstance(id)
  }

  async function batchOperate(ids: string[], action: string) {
    return instanceApi.batchOperateInstances(ids, action as any)
  }

  async function fetchMetrics(id: string, params: { startTime: number; endTime: number }) {
    const res = await instanceApi.getInstanceMetrics(id, params)
    currentMetrics.value = res
    return res
  }

  async function fetchGpuTypes() {
    const res = await instanceApi.getGpuTypes()
    gpuTypes.value = res
    return res
  }

  async function fetchRegions() {
    const res = await instanceApi.getRegions()
    regions.value = res
    return res
  }

  function resetState() {
    instanceList.value = []
    total.value = 0
    currentInstance.value = null
    currentMetrics.value = null
  }

  return {
    // state
    instanceList,
    total,
    loading,
    currentInstance,
    currentMetrics,
    gpuTypes,
    regions,
    // getters
    runningInstances,
    stoppedInstances,
    // actions
    fetchInstanceList,
    fetchInstanceDetail,
    startInstance,
    stopInstance,
    restartInstance,
    releaseInstance,
    batchOperate,
    fetchMetrics,
    fetchGpuTypes,
    fetchRegions,
    resetState
  }
})
```

---

## 6. 路由配置

### 动态路由添加

```typescript
// src/routes/dynamicRouter.ts

// 在动态路由初始化时添加实例管理路由
const instanceRoutes = [
  {
    path: '/cloud/instance',
    name: 'InstanceList',
    component: () => import('@/view/Cloud/InstanceList/index.vue'),
    meta: {
      title: '实例管理',
      icon: 'Monitor',
      keepAlive: true
    }
  }
]

// 在 initDynamicRouter 中合并到菜单
```

---

## 7. 关键实现细节

### 7.1 状态轮询机制

```typescript
// 实例操作后的状态轮询
class InstanceStatusPoller {
  private timers: Map<string, number> = new Map()
  
  startPolling(instanceId: string, targetStatus: InstanceStatus, callback: () => void) {
    const timer = window.setInterval(async () => {
      const instance = await fetchInstanceDetail(instanceId)
      if (instance.status === targetStatus || this.isTerminalStatus(instance.status)) {
        this.stopPolling(instanceId)
        callback()
      }
    }, 2000)
    
    this.timers.set(instanceId, timer)
  }
  
  stopPolling(instanceId: string) {
    const timer = this.timers.get(instanceId)
    if (timer) {
      clearInterval(timer)
      this.timers.delete(instanceId)
    }
  }
  
  private isTerminalStatus(status: InstanceStatus): boolean {
    return [InstanceStatus.ERROR, InstanceStatus.RELEASED].includes(status)
  }
}
```

### 7.2 批量操作队列

```typescript
// 批量操作管理
class BatchOperationManager {
  async executeBatch(
    ids: string[], 
    action: string, 
    onProgress: (completed: number, total: number) => void
  ) {
    const results = []
    const batchSize = 3 // 每批3个并发
    
    for (let i = 0; i < ids.length; i += batchSize) {
      const batch = ids.slice(i, i + batchSize)
      const batchResults = await Promise.all(
        batch.map(id => this.executeSingle(id, action))
      )
      results.push(...batchResults)
      onProgress(Math.min(i + batchSize, ids.length), ids.length)
    }
    
    return results
  }
  
  private async executeSingle(id: string, action: string) {
    try {
      await batchOperateInstances([id], action)
      return { id, success: true }
    } catch (error) {
      return { id, success: false, error }
    }
  }
}
```

### 7.3 ECharts 监控图表封装

```vue
<!-- components/MetricsChart/index.vue -->
<template>
  <div class="metrics-chart">
    <div ref="chartRef" class="chart-container" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import type { MetricDataPoint } from '@/types'

interface Props {
  title: string
  data: MetricDataPoint[]
  unit?: string
  color?: string
  yAxisMax?: number
}

const props = withDefaults(defineProps<Props>(), {
  unit: '%',
  color: '#409EFF',
  yAxisMax: 100
})

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value)
  updateChart()
  
  window.addEventListener('resize', handleResize)
}

const updateChart = () => {
  if (!chart) return
  
  const option: echarts.EChartsOption = {
    title: {
      text: props.title,
      textStyle: { fontSize: 14, fontWeight: 'normal' },
      left: 0
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const time = new Date(params[0].value[0]).toLocaleString()
        const value = params[0].value[1]
        return `${time}<br/>${params[0].seriesName}: ${value}${props.unit}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '50px',
      containLabel: true
    },
    xAxis: {
      type: 'time',
      boundaryGap: false,
      axisLabel: {
        formatter: (value: number) => {
          const date = new Date(value)
          return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
        }
      }
    },
    yAxis: {
      type: 'value',
      max: props.yAxisMax,
      axisLabel: {
        formatter: `{value}${props.unit}`
      },
      splitLine: {
        lineStyle: { type: 'dashed' }
      }
    },
    series: [{
      name: props.title,
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
      data: props.data.map(d => [d.timestamp, d.value])
    }]
  }
  
  chart.setOption(option)
}

const handleResize = () => {
  chart?.resize()
}

onMounted(initChart)

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})

watch(() => props.data, updateChart, { deep: true })
</script>

<style scoped>
.metrics-chart {
  width: 100%;
}

.chart-container {
  width: 100%;
  height: 200px;
}
</style>
```

---

## 8. 样式规范

### 8.1 颜色体系

```scss
// 状态颜色
$status-running: #67C23A;      // 运行中 - 绿色
$status-stopped: #909399;      // 已关机 - 灰色
$status-starting: #E6A23C;     // 开机中 - 橙色
$status-stopping: #E6A23C;     // 关机中 - 橙色
$status-restarting: #E6A23C;   // 重启中 - 橙色
$status-creating: #409EFF;     // 创建中 - 蓝色
$status-releasing: #F56C6C;    // 释放中 - 红色
$status-error: #F56C6C;        // 错误 - 红色

// 操作按钮颜色
$action-start: #67C23A;        // 开机
$action-stop: #F56C6C;         // 关机
$action-restart: #E6A23C;      // 重启
$action-release: #909399;     // 释放
```

### 8.2 布局尺寸

```scss
// 页面布局
$page-padding: 20px;
$card-gap: 16px;

// 筛选栏
$filter-bar-height: 56px;
$filter-item-width: 160px;

// 表格
$table-row-height: 56px;
$table-header-height: 48px;

// 批量操作栏
$batch-actions-height: 48px;

// 抽屉
$drawer-width: 480px;
```

---

## 9. 性能优化

### 9.1 列表优化

```typescript
// 1. 虚拟滚动（当数据量>100时启用）
import { useVirtualList } from '@vueuse/core'

const { list: virtualList, containerProps, wrapperProps } = useVirtualList(
  instanceList,
  { itemHeight: 56, overscan: 5 }
)

// 2. 防抖搜索
import { debounce } from 'lodash-es'

const debouncedSearch = debounce((keyword: string) => {
  filters.keyword = keyword
  fetchInstanceList()
}, 300)

// 3. 缓存实例列表
import { useSessionStorage } from '@vueuse/core'

const cachedList = useSessionStorage<Instance[]>('instance-list', [])
```

### 9.2 监控图表优化

```typescript
// 1. 数据采样（当点数>500时）
function downsample(data: MetricDataPoint[], threshold: number): MetricDataPoint[] {
  if (data.length <= threshold) return data
  
  const bucketSize = Math.ceil(data.length / threshold)
  const result: MetricDataPoint[] = []
  
  for (let i = 0; i < data.length; i += bucketSize) {
    const bucket = data.slice(i, i + bucketSize)
    const avg = bucket.reduce((sum, d) => sum + d.value, 0) / bucket.length
    result.push({
      timestamp: bucket[Math.floor(bucket.length / 2)].timestamp,
      value: avg
    })
  }
  
  return result
}

// 2. 图表销毁
onUnmounted(() => {
  chart?.dispose()
})
```

---

## 10. 测试策略

### 10.1 单元测试

```typescript
// __tests__/useInstanceList.spec.ts
import { describe, it, expect, vi } from 'vitest'
import { useInstanceList } from '../composables/useInstanceList'
import * as instanceApi from '@/api/instance'

vi.mock('@/api/instance')

describe('useInstanceList', () => {
  it('should fetch instance list on mount', async () => {
    const mockData = {
      list: [{ id: '1', name: 'test-instance', status: 'running' }],
      total: 1
    }
    vi.mocked(instanceApi.getInstanceList).mockResolvedValue(mockData)
    
    const { instanceList, fetchInstanceList } = useInstanceList()
    await fetchInstanceList()
    
    expect(instanceList.value).toHaveLength(1)
    expect(instanceList.value[0].name).toBe('test-instance')
  })
})
```

### 10.2 E2E 测试

```typescript
// cypress/e2e/instance-management.cy.ts
describe('实例管理', () => {
  beforeEach(() => {
    cy.login()
    cy.visit('/cloud/instance')
  })

  it('应显示实例列表', () => {
    cy.get('[data-testid="instance-table"]').should('be.visible')
    cy.get('[data-testid="instance-row"]').should('have.length.at.least', 1)
  })

  it('应能筛选实例', () => {
    cy.get('[data-testid="gpu-type-filter"]').click()
    cy.get('.el-select-dropdown__item').first().click()
    cy.get('[data-testid="search-btn"]').click()
    cy.get('[data-testid="instance-row"]').should('have.length.at.least', 0)
  })

  it('应能打开实例详情', () => {
    cy.get('[data-testid="instance-name"]').first().click()
    cy.get('[data-testid="instance-drawer"]').should('be.visible')
    cy.get('[data-testid="metrics-chart"]').should('be.visible')
  })
})
```

---

## 11. 部署与配置

### 11.1 环境变量

```bash
# .env.development

# 实例管理相关配置
VITE_INSTANCE_LIST_PAGE_SIZE=10
VITE_INSTANCE_STATUS_POLL_INTERVAL=2000
VITE_INSTANCE_METRICS_TIME_RANGE=3600
```

### 11.2 功能开关

```typescript
// src/config/features.ts

export const featureFlags = {
  // 监控图表
  instanceMetrics: true,
  
  // 批量操作
  batchOperations: true,
  
  // 无卡开机
  cpuOnlyStart: false, // 暂不支持
  
  // 实例迁移
  instanceMigration: false // 暂不支持
}
```

---

## 12. 附录

### 12.1 术语表

| 术语 | 说明 |
|------|------|
| 实例 | 用户购买的 GPU 云服务器 |
| 开机 | 启动已关机的实例 |
| 关机 | 停止运行中的实例（保留数据） |
| 重启 | 先关机再开机 |
| 释放 | 彻底删除实例，数据不可恢复 |
| GPU 利用率 | GPU 计算单元的使用百分比 |
| 显存占用 | GPU 显存的使用量 |

### 12.2 相关文档

- [Element Plus Table 文档](https://element-plus.org/zh-CN/component/table.html)
- [ECharts 文档](https://echarts.apache.org/zh/option.html)
- [VueUse 文档](https://vueuse.org/)

---

**文档版本**: 1.0  
**最后更新**: 2026-05-24