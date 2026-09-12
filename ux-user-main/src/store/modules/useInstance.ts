import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type {
  Instance,
  InstanceListParams,
  InstanceListResult,
  InstanceMetrics,
  InstanceFilters
} from '@/types/instance'
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
      const backendParams: any = {
        pageNo: params.page ?? 1,
        pageSize: params.pageSize ?? 10,
      }
      if (params.filters?.keyword) {
        backendParams.name = params.filters.keyword
      }
      if (params.filters?.regions && params.filters.regions.length > 0) {
        backendParams.regionCode = params.filters.regions[0]
      }
      if (params.filters?.gpuTypes && params.filters.gpuTypes.length > 0) {
        backendParams.gpuModel = params.filters.gpuTypes[0]
      }
      if (params.filters?.statuses && params.filters.statuses.length > 0) {
        backendParams.status = params.filters.statuses[0]
      }
      const res = await instanceApi.getInstanceList(backendParams)
      instanceList.value = res.data?.list || []
      total.value = Number(res.data?.dataTotal || 0)
      return res
    } finally {
      loading.value = false
    }
  }

  async function fetchInstanceDetail(id: string) {
    const res = await instanceApi.getInstanceDetail(id)
    currentInstance.value = res.data || null
    return res
  }

  async function fetchInstanceSshInfo(id: string) {
    return instanceApi.getInstanceSshInfo(id)
  }

  async function fetchInstanceVncInfo(id: string) {
    return instanceApi.getInstanceVncInfo(id)
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

  async function batchOperate(
    ids: string[],
    action: 'start' | 'stop' | 'restart' | 'release'
  ) {
    switch (action) {
      case 'start':
        return instanceApi.batchStartInstances(ids)
      case 'stop':
        return instanceApi.batchStopInstances(ids)
      case 'restart':
        return instanceApi.batchRestartInstances(ids)
      case 'release':
        return instanceApi.batchReleaseInstances(ids)
    }
  }

  async function fetchMetrics(
    id: string,
    params: { startTime: number; endTime: number }
  ) {
    const res = await instanceApi.getInstanceMetrics(id, params)
    currentMetrics.value = res
    return res
  }

  async function fetchGpuTypes() {
    const res = await instanceApi.getGpuTypes()
    gpuTypes.value = res.data?.gpuModels?.map((item: any) => item.model) || []
    return gpuTypes.value
  }

  async function fetchRegions() {
    const res = await instanceApi.getRegions()
    regions.value = res.data?.map((item: any) => ({ code: item.id, name: item.name })) || []
    return regions.value
  }

  // ========== 更多操作 ==========

  async function resetPassword(id: string, newPassword: string) {
    return instanceApi.resetPassword(id, newPassword)
  }

  async function setInstanceName(id: string, name: string) {
    return instanceApi.setInstanceName(id, name)
  }

  async function renewInstance(id: string, data: { billingMode: string; duration: number }) {
    return instanceApi.renewInstance(id, data)
  }

  async function batchRenew(data: { instanceIds: string[]; billingMode: string; duration: number }) {
    return instanceApi.batchRenewInstances(data)
  }

  async function getInstanceTools(id: string) {
    return instanceApi.getInstanceTools(id)
  }

  async function getShutdownSchedule(id: string) {
    return instanceApi.getShutdownSchedule(id)
  }

  async function setShutdownSchedule(id: string, data: { shutdownTime?: string | null }) {
    return instanceApi.setShutdownSchedule(id, data)
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
    fetchInstanceSshInfo,
    fetchInstanceVncInfo,
    startInstance,
    stopInstance,
    restartInstance,
    releaseInstance,
    batchOperate,
    fetchMetrics,
    fetchGpuTypes,
    fetchRegions,
    // 更多操作
    resetPassword,
    setInstanceName,
    renewInstance,
    batchRenew,
    getInstanceTools,
    getShutdownSchedule,
    setShutdownSchedule,
    resetState
  }
})
