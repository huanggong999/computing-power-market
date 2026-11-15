import type { CpuInstance, Instance } from '@/types/instance'
import { normalizeCpuStatus, type CpuPresentationStatus } from './cpuPurchase'

export function isCpuInstance(instance: any): instance is CpuInstance {
  return instance?.origin === 'cpu_instance'
}

export function isGpuInstance(instance: any): instance is Instance {
  return instance?.origin === 'gpu_pod' || instance?.origin === 'cluster'
}

/**
 * Returns the cloud-side instance ID only. A creation operation placeholder
 * carries its operation ID in `id`, which must never be sent to the
 * /instances/{instance_id} endpoints.
 */
export function cpuCloudInstanceId(instance?: CpuInstance | null): string {
  if (!instance) return ''
  if (instance.instance_id) return String(instance.instance_id)
  return instance.operation_id ? '' : String(instance.id || '')
}

/**
 * Selects the correct detail data source. Placeholder creation rows only
 * have an operation ID and must use /operations/{id}, never /instances/{id}.
 */
export function cpuDetailTarget(instance?: CpuInstance | null): { kind: 'instance' | 'operation' | 'local'; id: string } {
  const instanceId = cpuCloudInstanceId(instance)
  if (instanceId) return { kind: 'instance', id: instanceId }
  if (instance?.operation_id) return { kind: 'operation', id: String(instance.operation_id) }
  return { kind: 'local', id: '' }
}

/** Durable list key: cloud instance ID first, then creation operation ID. */
export function cpuRowKey(instance?: CpuInstance | null): string {
  if (!instance) return ''
  return cpuCloudInstanceId(instance) || String(instance.operation_id || instance.id || '')
}

export function cpuStatusInfo(status: unknown): { value: CpuPresentationStatus; label: string; type: 'success' | 'warning' | 'danger' | 'info' } {
  const value = normalizeCpuStatus(status)
  const map = {
    creating: { label: '创建中', type: 'info' }, running: { label: '运行中', type: 'success' }, stopped: { label: '已停止', type: 'warning' },
    failed: { label: '创建失败', type: 'danger' }, error: { label: '异常', type: 'danger' },
  } as const
  return { value, ...map[value] }
}

/** Actions exposed by the independent CPU detail API. */
export type CpuInstanceActionKey =
  | 'detail' | 'start' | 'stop' | 'restart' | 'ssh' | 'resetPassword' | 'rename'
  | 'resize' | 'release' | 'clear'

const CPU_ACTION_ALIASES: Record<string, CpuInstanceActionKey> = {
  setName: 'rename',
  reset_password: 'resetPassword',
}

const TRANSITIONAL_CPU_STATUSES = new Set([
  'creating', 'pending', 'provisioning', 'starting', 'stopping', 'restarting', 'releasing',
])
const TERMINAL_CPU_STATUSES = new Set(['released', 'expired', 'failed', 'error'])

function normalizeCpuAction(action: string): CpuInstanceActionKey {
  return CPU_ACTION_ALIASES[action] || action as CpuInstanceActionKey
}

function cpuRawStatus(instance: CpuInstance): string {
  return String(instance?.status || '').toLowerCase()
}

/**
 * Resolve whether an action is available. Detail responses carry an `actions`
 * map from the v2.1 API and take precedence; list rows fall back to a local
 * status check so the table and detail panel stay usable before detail loads.
 */
export function cpuActionState(instance: CpuInstance, action: string): { enabled: boolean; reason: string } {
  const key = normalizeCpuAction(action)
  if (key === 'detail') return { enabled: true, reason: '' }
  if (!isCpuInstance(instance)) return { enabled: false, reason: '实例来源不匹配，请刷新列表后重试' }

  if (key === 'clear') {
    const status = cpuStatusInfo(instance.status).value
    return { enabled: ['failed', 'error'].includes(status), reason: '仅异常实例可清除记录' }
  }

  const instanceId = cpuCloudInstanceId(instance)
  const rawStatus = cpuRawStatus(instance)
  const creating = ['creating', 'pending', 'provisioning', 'starting'].includes(rawStatus)
  // Operation-only rows have no cloud instance yet. v2.1 explicitly rejects
  // release/start/stop until the upstream instance ID exists, so keep every
  // lifecycle action disabled but still expose the local operation detail.
  if (!instanceId) {
    return {
      enabled: false,
      reason: key === 'release' ? '实例正在创建中，请等创建完成后再释放' : '实例正在创建中，请等创建完成后再操作',
    }
  }
  // A creating CPU host that already has a cloud instance ID must stay releasable,
  // even if a detail response omits or disables `actions.release` (v2.1 only
  // blocks release while the instance ID is still missing).
  if (key === 'release' && creating && instanceId) {
    return { enabled: true, reason: '' }
  }

  const remoteAction = instance.actions?.[key]
  if (remoteAction && typeof remoteAction.enabled === 'boolean') {
    return { enabled: remoteAction.enabled, reason: remoteAction.reason || '' }
  }

  const transitional = TRANSITIONAL_CPU_STATUSES.has(rawStatus)
  const manageAllowed = !transitional && !TERMINAL_CPU_STATUSES.has(rawStatus)

  switch (key) {
    case 'release':
      return {
        enabled: ['running', 'stopped'].includes(rawStatus),
        reason: creating ? '实例正在创建中，请等创建完成后再释放' : '当前状态不支持释放',
      }
    case 'start':
      return { enabled: ['stopped', 'shutdown'].includes(rawStatus), reason: '仅已停止实例可开机' }
    case 'stop':
      return { enabled: ['running', 'started'].includes(rawStatus), reason: '仅运行中实例可关机' }
    case 'restart':
      return { enabled: ['running', 'started'].includes(rawStatus), reason: '仅运行中实例可重启' }
    case 'ssh':
      return { enabled: ['running', 'started'].includes(rawStatus), reason: '仅运行中实例可获取 SSH 连接信息' }
    case 'resetPassword':
      return { enabled: ['running', 'started', 'stopped', 'shutdown'].includes(rawStatus), reason: '当前状态不支持重置密码' }
    case 'rename':
      return { enabled: manageAllowed, reason: transitional ? '实例正在处理中，请稍后再试' : '当前状态不支持修改名称' }
    case 'resize':
      return { enabled: manageAllowed, reason: transitional ? '实例正在处理中，请稍后再试' : '当前状态不支持资源变更' }
    default:
      return { enabled: false, reason: '当前状态不支持此操作' }
  }
}

export function cpuCanAction(instance: CpuInstance, action: string): boolean {
  return cpuActionState(instance, action).enabled
}

export function cpuActionDisabledReason(instance: CpuInstance, action: string): string {
  return cpuActionState(instance, action).reason
}
