import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useInstanceStore } from '@/store/modules/useInstance'
import { useStatusPoller } from './useStatusPoller'
import { InstanceStatus } from '@/types/instance'
import type { Instance } from '@/types/instance'

interface OperationCallbacks {
  onSuccess?: () => void
}

export function useInstanceOperation(callbacks?: OperationCallbacks) {
  const instanceStore = useInstanceStore()
  const { startPolling } = useStatusPoller()
  const operatingIds = ref<Set<string>>(new Set())

  const addOperating = (id: string) => {
    operatingIds.value = new Set([...operatingIds.value, id])
  }

  const removeOperating = (id: string) => {
    const nextIds = new Set(operatingIds.value)
    nextIds.delete(id)
    operatingIds.value = nextIds
  }

  const ACTION_TARGET_STATUS: Record<string, InstanceStatus> = {
    start: InstanceStatus.RUNNING,
    stop: InstanceStatus.STOPPED,
    restart: InstanceStatus.RUNNING
  }

  const ACTION_LABELS: Record<string, string> = {
    start: '开机',
    stop: '关机',
    restart: '重启',
    release: '释放'
  }

  /**
   * 执行单实例操作
   */
  async function executeOperation(
    action: string,
    instance: Instance,
    options: {
      confirm?: boolean
      needPolling?: boolean
    } = {}
  ): Promise<boolean> {
    const { confirm = true, needPolling = true } = options

    // 释放操作需要确认弹窗
    if (confirm || action === 'release') {
      const message = action === 'release'
        ? `确定要释放实例 ${instance.id} 吗？释放后数据不可恢复！`
        : `确定要${ACTION_LABELS[action] || action}实例 ${instance.id} 吗？`
      try {
        await ElMessageBox.confirm(message, '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: action === 'release' ? 'warning' : 'info'
        })
      } catch {
        return false
      }
    }

    if (operatingIds.value.has(instance.id)) {
      return false
    }

    addOperating(instance.id)

    try {
      switch (action) {
        case 'start':
          await instanceStore.startInstance(instance.id)
          break
        case 'stop':
          await instanceStore.stopInstance(instance.id)
          break
        case 'restart':
          await instanceStore.restartInstance(instance.id)
          break
        case 'release':
          await instanceStore.releaseInstance(instance.id)
          break
        default:
          removeOperating(instance.id)
          console.warn('Unknown action:', action)
          return false
      }

      ElMessage.success(`${ACTION_LABELS[action] || action}指令已发送`)

      if (needPolling && ACTION_TARGET_STATUS[action]) {
        startPolling(instance.id, ACTION_TARGET_STATUS[action], {
          onSuccess: () => {
            removeOperating(instance.id)
            ElMessage.success(`实例 ${instance.id} ${ACTION_LABELS[action]}成功`)
            callbacks?.onSuccess?.()
          },
          onError: () => {
            removeOperating(instance.id)
            callbacks?.onSuccess?.()
          },
          onTimeout: () => {
            removeOperating(instance.id)
            ElMessage.warning(`实例 ${instance.id} 状态同步超时，请手动刷新`)
          }
        })
      } else {
        removeOperating(instance.id)
        callbacks?.onSuccess?.()
      }

      return true
    } catch (error: any) {
      removeOperating(instance.id)
      if (error?.response) {
        ElMessage.error(error.response.data?.detail || error.response.data?.msg || error.response.data?.message || '操作失败')
      }
      return false
    }
  }

  /**
   * 批量操作
   */
  async function executeBatchOperation(
    action: 'start' | 'stop' | 'restart' | 'release',
    ids: string[]
  ): Promise<{ success: number; failed: number }> {
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
      await instanceStore.batchOperate(ids, action)
      ElMessage.success(`批量${ACTION_LABELS[action]}指令已发送`)
      callbacks?.onSuccess?.()
      return { success: ids.length, failed: 0 }
    } catch (error: any) {
      if (error?.response) {
        ElMessage.error(error.response.data?.detail || error.response.data?.msg || error.response.data?.message || '批量操作失败')
      }
      return { success: 0, failed: ids.length }
    }
  }

  return {
    operatingIds,
    executeOperation,
    executeBatchOperation
  }
}
