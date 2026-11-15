import type { CpuInstance, CpuInstanceType, CpuRegion, CpuPurchaseRequest, CpuPurchaseResult } from '@/types/instance'
import { MIN_DISK_CAPACITY_GB, MIN_DISK_CAPACITY_MESSAGE, totalDiskCapacityGb } from '@/utils/diskCapacity'

export type CpuPurchaseField = keyof CpuPurchaseRequest | 'catalog' | 'zone' | 'diskCapacity'
export type CpuPurchaseErrors = Partial<Record<CpuPurchaseField, string>>

export interface CpuPurchaseValidationContext {
  regions: CpuRegion[]
  instanceTypes: CpuInstanceType[]
  catalogLoading?: boolean
}

export function validateCpuPurchase(
  form: Partial<CpuPurchaseRequest>,
  context: CpuPurchaseValidationContext,
): CpuPurchaseErrors {
  const errors: CpuPurchaseErrors = {}
  const region = String(form.region || '')
  const zone = String(form.zone || '')
  const instanceTypeId = String(form.instanceTypeId || '')
  const billingMode = form.billingMode

  if (!region) errors.region = '请选择地域'
  const regionConfig = context.regions.find(item => item.region === region)
  if (!zone) errors.zone = '请选择可用区'
  else if (!regionConfig || !regionConfig.zones.includes(zone)) errors.zone = '可用区不属于当前地域，请重新选择'
  if (!instanceTypeId) errors.instanceTypeId = '请选择纯 CPU 规格'
  else if (context.catalogLoading) errors.catalog = 'CPU 规格仍在加载，请稍后再试'
  else if (!context.instanceTypes.some(item => item.instanceTypeId === instanceTypeId)) errors.instanceTypeId = '请选择当前地域返回的纯 CPU 规格'
  if (!['on_demand', 'monthly', 'yearly'].includes(String(billingMode))) errors.billingMode = '请选择有效的计费方式'

  validateIntegerRange(form.systemDiskGb, 20, 2048, '系统盘', errors, 'systemDiskGb')
  validateDataDiskCapacity(form.dataDiskGb, errors)
  if (!errors.systemDiskGb && !errors.dataDiskGb && totalDiskCapacityGb(form.systemDiskGb, form.dataDiskGb) < MIN_DISK_CAPACITY_GB) {
    errors.diskCapacity = MIN_DISK_CAPACITY_MESSAGE
  }
  validateIntegerRange(form.count, 1, 10, '购买数量', errors, 'count')
  if (billingMode !== 'on_demand') validateIntegerRange(form.duration, 1, 36, '购买时长', errors, 'duration')

  return errors
}

function validateIntegerRange(
  value: unknown,
  min: number,
  max: number,
  label: string,
  errors: CpuPurchaseErrors,
  field: CpuPurchaseField,
  allowZero = false,
) {
  if (value === '' || value === null || value === undefined || typeof value === 'boolean') {
    errors[field] = `请输入${label}`
    return
  }
  const number = Number(value)
  if (!Number.isFinite(number) || !Number.isInteger(number)) {
    errors[field] = `${label}必须是整数`
  } else if (number < min || number > max || (!allowZero && number === 0)) {
    errors[field] = `${label}范围为 ${min}–${max}`
  }
}

export const MIN_DATA_DISK_CAPACITY_GB = 20
export const MAX_DATA_DISK_CAPACITY_GB = 8192
export const DATA_DISK_CAPACITY_MESSAGE = `数据盘容量需为 0（不购买）或至少 ${MIN_DATA_DISK_CAPACITY_GB}G`

function validateDataDiskCapacity(value: unknown, errors: CpuPurchaseErrors) {
  if (value === '' || value === null || value === undefined || typeof value === 'boolean') {
    errors.dataDiskGb = '请输入数据盘'
    return
  }
  const number = Number(value)
  if (!Number.isFinite(number) || !Number.isInteger(number)) {
    errors.dataDiskGb = '数据盘必须是整数'
  } else if (number < 0 || (number > 0 && number < MIN_DATA_DISK_CAPACITY_GB)) {
    errors.dataDiskGb = DATA_DISK_CAPACITY_MESSAGE
  } else if (number > MAX_DATA_DISK_CAPACITY_GB) {
    errors.dataDiskGb = `数据盘不能超过 ${MAX_DATA_DISK_CAPACITY_GB}G`
  }
}

export function firstCpuPurchaseError(errors: CpuPurchaseErrors): string {
  return Object.values(errors).find(Boolean) || ''
}

export function hasCpuPurchaseErrors(errors: CpuPurchaseErrors): boolean {
  return Object.keys(errors).length > 0
}

export type CpuPresentationStatus = 'creating' | 'running' | 'stopped' | 'failed' | 'error'

const terminalStatuses = new Set(['running', 'stopped', 'failed', 'error', 'released', 'expired'])

export function normalizeCpuStatus(status: unknown, message?: unknown): CpuPresentationStatus {
  const value = String(status || '').toLowerCase()
  if (value === 'running' || value === 'started') return 'running'
  if (value === 'stopped' || value === 'shutdown') return 'stopped'
  if (value === 'failed' || value === 'failure') return 'failed'
  if (value === 'error' || value === 'exception') return 'error'
  if (value === 'creating' || value === 'pending' || value === 'provisioning' || value === 'starting') return 'creating'
  if (message && /失败|异常|error|fail/i.test(String(message))) return 'error'
  return 'creating'
}

export function normalizeCpuInstance<T extends Partial<CpuInstance>>(instance: T): T & { presentationStatus: CpuPresentationStatus; statusMessage?: string } {
  const statusMessage = String((instance as any).status_message || (instance as any).message || (instance as any).error_message || '') || undefined
  return { ...instance, presentationStatus: normalizeCpuStatus(instance.status, statusMessage), statusMessage } as T & { presentationStatus: CpuPresentationStatus; statusMessage?: string }
}

export function normalizeCpuPurchaseResult(result: CpuPurchaseResult): CpuPurchaseResult & { accepted: boolean; statusMessage: string } {
  const instances = (result.instances || []).map(item => ({
    ...item,
    status: normalizeCpuStatus(item.status, item.message || item.error_message),
  }))
  const statusMessage = result.message || (result.success ? 'CPU 实例已受理，正在创建中，预计需要几分钟' : 'CPU 实例创建失败')
  return { ...result, instances, accepted: Boolean(result.success), statusMessage }
}

export function isCpuTerminalStatus(status: unknown): boolean {
  return terminalStatuses.has(String(status || '').toLowerCase())
}
