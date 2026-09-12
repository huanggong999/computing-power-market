import { ref, computed, onUnmounted } from 'vue'
import { useInstanceStore } from '@/store/modules/useInstance'
import type { InstanceMetrics } from '@/types/instance'

const TIME_RANGE_MAP: Record<string, number> = {
  '1h': 60 * 60 * 1000,
  '6h': 6 * 60 * 60 * 1000,
  '24h': 24 * 60 * 60 * 1000,
  '7d': 7 * 24 * 60 * 60 * 1000
}

const AUTO_REFRESH_INTERVAL = 30000 // 30 seconds

export function useMetrics() {
  const instanceStore = useInstanceStore()

  const metrics = ref<InstanceMetrics | null>(null)
  const loading = ref(false)
  const timeRange = ref('1h')
  const currentInstanceId = ref<string | null>(null)
  let autoRefreshTimer: ReturnType<typeof setInterval> | null = null

  const timeRangeMs = computed(() => TIME_RANGE_MAP[timeRange.value] || TIME_RANGE_MAP['1h'])

  async function fetchMetrics(instanceId?: string) {
    const id = instanceId || currentInstanceId.value
    if (!id) return

    currentInstanceId.value = id
    loading.value = true

    try {
      const endTime = Date.now()
      const startTime = endTime - timeRangeMs.value

      const res = await instanceStore.fetchMetrics(id, { startTime, endTime })
      metrics.value = res.data || null
    } catch (error) {
      console.error('获取监控数据失败:', error)
      metrics.value = null
    } finally {
      loading.value = false
    }
  }

  async function setTimeRange(range: string) {
    timeRange.value = range
    if (currentInstanceId.value) {
      await fetchMetrics(currentInstanceId.value)
    }
  }

  async function refreshMetrics() {
    if (currentInstanceId.value) {
      await fetchMetrics(currentInstanceId.value)
    }
  }

  function startAutoRefresh() {
    stopAutoRefresh()
    autoRefreshTimer = setInterval(() => {
      if (currentInstanceId.value) {
        fetchMetrics(currentInstanceId.value)
      }
    }, AUTO_REFRESH_INTERVAL)
  }

  function stopAutoRefresh() {
    if (autoRefreshTimer) {
      clearInterval(autoRefreshTimer)
      autoRefreshTimer = null
    }
  }

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
