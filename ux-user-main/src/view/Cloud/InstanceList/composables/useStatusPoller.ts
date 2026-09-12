import { ref, onUnmounted } from 'vue'
import { useInstanceStore } from '@/store/modules/useInstance'
import { InstanceStatus } from '@/types/instance'

export interface PollerOptions {
  interval?: number
  timeout?: number
}

export interface PollCallbacks {
  onSuccess?: () => void
  onError?: () => void
  onTimeout?: () => void
}

export interface PollResult {
  success: boolean
  status: InstanceStatus
  error?: string
}

const TERMINAL_STATUSES: InstanceStatus[] = [
  InstanceStatus.ERROR,
  InstanceStatus.RELEASED
]

export function useStatusPoller(options: PollerOptions = {}) {
  const instanceStore = useInstanceStore()
  const timers = new Map<string, number>()
  const pollingIds = ref<Set<string>>(new Set())

  const interval = options.interval ?? 2000
  const timeout = options.timeout ?? 120000

  function isPolling(instanceId: string): boolean {
    return pollingIds.value.has(instanceId)
  }

  function stopPolling(instanceId: string): void {
    const timer = timers.get(instanceId)
    if (timer !== undefined) {
      clearInterval(timer)
      timers.delete(instanceId)
    }
    pollingIds.value.delete(instanceId)
  }

  function stopAllPolling(): void {
    timers.forEach((timer) => {
      clearInterval(timer)
    })
    timers.clear()
    pollingIds.value.clear()
  }

  async function startPolling(
    instanceId: string,
    targetStatus: InstanceStatus,
    callbacks?: PollCallbacks
  ): Promise<PollResult> {
    if (isPolling(instanceId)) {
      stopPolling(instanceId)
    }

    pollingIds.value.add(instanceId)

    const startTime = Date.now()
    let consecutiveErrors = 0
    const MAX_CONSECUTIVE_ERRORS = 3

    return new Promise((resolve) => {
      const timer = window.setInterval(async () => {
        if (Date.now() - startTime > timeout) {
          stopPolling(instanceId)
          callbacks?.onTimeout?.()
          resolve({
            success: false,
            status: InstanceStatus.ERROR,
            error: '轮询超时'
          })
          return
        }

        try {
          const res = await instanceStore.fetchInstanceDetail(instanceId)
          const instance = res.data

          consecutiveErrors = 0

          if (!instance) {
            return
          }

          const currentStatus = instance.status

          if (currentStatus === targetStatus) {
            stopPolling(instanceId)
            callbacks?.onSuccess?.()
            resolve({ success: true, status: currentStatus })
            return
          }

          if (TERMINAL_STATUSES.includes(currentStatus)) {
            stopPolling(instanceId)
            callbacks?.onError?.()
            resolve({
              success: false,
              status: currentStatus,
              error: `实例进入终态: ${currentStatus}`
            })
            return
          }
        } catch (error) {
          consecutiveErrors++
          console.warn(
            `轮询查询失败 (${consecutiveErrors}/${MAX_CONSECUTIVE_ERRORS}):`,
            error
          )
          if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
            stopPolling(instanceId)
            callbacks?.onError?.()
            resolve({
              success: false,
              status: InstanceStatus.ERROR,
              error: '连续查询失败，停止轮询'
            })
          }
        }
      }, interval)

      timers.set(instanceId, timer)
    })
  }

  onUnmounted(() => {
    stopAllPolling()
  })

  return {
    startPolling,
    stopPolling,
    stopAllPolling,
    isPolling
  }
}
