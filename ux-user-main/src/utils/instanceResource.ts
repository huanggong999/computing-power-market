import type { ResizeOptions, ResizeRequest } from '@/types/instance'

export function validateResizeRequest(options: ResizeOptions, request: ResizeRequest): string | null {
  const current = options.current
  const limits = options.limits
  const hasDisk = request.diskGb != null
  const hasCpu = request.cpu != null && request.cpu.trim() !== ''
  const hasMemory = request.memory != null && request.memory.trim() !== ''
  if (options.capabilities.disk_expand && !hasDisk) return '请输入目标磁盘'
  if (options.capabilities.cpu_scale && !hasCpu) return '请输入目标CPU'
  if (options.capabilities.memory_scale && !hasMemory) return '请输入目标内存'
  if (!hasDisk && !hasCpu && !hasMemory && request.gpuCount == null) return '请输入目标CPU、目标内存或目标磁盘'
  if (hasDisk && request.diskGb! <= 0) return '目标磁盘必须大于0'
  if (hasDisk && request.diskGb! <= Number(current.data_disk_gib || 0)) return '数据盘只支持扩容，目标容量必须大于当前容量'
  if (request.diskGb != null && limits.data_disk_gib?.max != null && request.diskGb > limits.data_disk_gib.max) return `数据盘不能超过 ${limits.data_disk_gib.max} GiB`
  const currentCpu = Number(current.cpu_cores || 0)
  const currentMemory = Number(current.memory_mib || parseMemoryMib(current.memory))
  if (hasCpu && parseCpuCores(request.cpu!) === 0) return '目标CPU必须大于0'
  if (hasMemory && parseMemoryMib(request.memory!) === 0) return '目标内存必须大于0'
  if (options.capabilities.cpu_scale_up_only && hasCpu) {
    const targetCpu = parseCpuCores(request.cpu)
    if (!Number.isFinite(targetCpu) || targetCpu <= currentCpu) return `当前 CPU 为 ${currentCpu} 核，目标值必须大于 ${currentCpu} 核`
  }
  if (options.capabilities.cpu_scale_up_only && hasMemory) {
    const targetMemory = parseMemoryMib(request.memory)
    if (!Number.isFinite(targetMemory) || targetMemory <= currentMemory) return `当前内存为 ${formatMemory(currentMemory)}，目标值必须更大`
  }
  if ((hasCpu || hasMemory) && options.backing === 'volc' && !request.instanceTypeId) return '火山云 CPU/内存变更必须填写目标规格 ID'
  return null
}

function parseCpuCores(value: string): number {
  const normalized = value.trim().toLowerCase()
  if (normalized.endsWith('m')) return Number(normalized.slice(0, -1)) / 1000
  return Number(normalized)
}

function parseMemoryMib(value?: string): number {
  if (!value) return 0
  const normalized = value.trim().toLowerCase()
  const number = Number.parseFloat(normalized)
  if (!Number.isFinite(number)) return 0
  if (/(gib|gb|g)$/.test(normalized)) return number * 1024
  if (/(kib|kb|k)$/.test(normalized)) return number / 1024
  if (/(tib|tb|t)$/.test(normalized)) return number * 1024 * 1024
  return number
}

function formatMemory(value: number): string {
  if (value >= 1024 && value % 1024 === 0) return `${value / 1024}Gi`
  return `${value}Mi`
}

export function isAllowedCpuType(instanceTypeId: string, types: Array<{ instanceTypeId: string }>): boolean {
  return types.some(item => item.instanceTypeId === instanceTypeId)
}

export function isResizeTaskTerminal(status?: string): boolean {
  return status === 'success' || status === 'failed'
}
