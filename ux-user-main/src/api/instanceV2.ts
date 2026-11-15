import request from '@/utils/request'
import { generateUUID } from '@/utils/uuid'
import type {
  BandwidthRequest, BandwidthResult, CpuListResult, CpuPurchaseRequest, CpuPurchaseResult,
  CpuRegion, CpuZoneCatalog, CpuInstanceType, CpuPriceCatalog, CpuSshResponse, ResizeOptions, ResizeRequest,
  ResizeSubmitResult, ResizeTask, ResizeTaskListResult
} from '@/types/instance'

const resourceBase = '/pc/instance-resource'
const cpuBase = '/pc/cpu-instance'

export function getResizeOptions(id: string) { return request.get<ResizeOptions>(`${resourceBase}/instances/${id}/resize/options`) }
export function submitResize(id: string, data: ResizeRequest) { return request.post<ResizeSubmitResult>(`${resourceBase}/instances/${id}/resize`, data) }
export function getResizeTasks(id: string) { return request.get<ResizeTaskListResult>(`${resourceBase}/instances/${id}/resize/tasks`) }
export function getResizeTask(id: string, taskId: string) { return request.get<ResizeTask>(`${resourceBase}/instances/${id}/resize/tasks/${taskId}`) }
export function changeBandwidth(id: string, data: BandwidthRequest) { return request.post<BandwidthResult>(`${resourceBase}/instances/${id}/bandwidth`, data) }

export function getCpuRegions() { return request.get<{ success: boolean; regions: CpuRegion[] }>(`${cpuBase}/regions`) }
export function getCpuZones(region?: string) { return request.get<CpuZoneCatalog>(`${cpuBase}/zones`, { params: { region, scene: 'cpu' }, suppressErrorMessage: true } as any) }
export function getCpuInstanceTypes(region: string) { return request.get<{ success: boolean; instance_types: CpuInstanceType[] }>(`${cpuBase}/instance-types`, { params: { region } }) }
export function getCpuPrices(region: string, instanceTypeId?: string, billingMode?: string) { return request.get<CpuPriceCatalog>(`${cpuBase}/prices`, { params: { region, instanceTypeId, billingMode } }) }
export function getCpuInstances(includeReleased = false) { return request.get<CpuListResult>(`${cpuBase}/instances`, { params: { includeReleased }, suppressErrorMessage: true } as any) }
export function getCpuInstanceDetail(id: string) { return request.get<{ success: boolean; message?: string; instance: any }>(`${cpuBase}/instances/${id}`, { suppressErrorMessage: true } as any) }
export function getCpuCreationOperation(id: string) { return request.get<CpuPurchaseResult & { operation_status?: string }>(`${cpuBase}/operations/${id}`, { suppressErrorMessage: true } as any) }
export function clearCpuOperation(id: string) { return request.delete(`${cpuBase}/operations/${encodeURIComponent(id)}`, { suppressErrorMessage: true } as any) }
export function purchaseCpu(data: CpuPurchaseRequest) { return request.post<CpuPurchaseResult>(`${cpuBase}/instances`, { ...data, requestKey: (data as any).requestKey || generateUUID() }, { timeout: 30000, suppressErrorMessage: true } as any) }
export function startCpuInstance(id: string) { return request.post(`${cpuBase}/instances/${id}/start`, null, { suppressErrorMessage: true } as any) }
export function stopCpuInstance(id: string, force = false) { return request.post(`${cpuBase}/instances/${id}/stop`, null, { params: { force }, suppressErrorMessage: true } as any) }
export function restartCpuInstance(id: string, force = false) { return request.post(`${cpuBase}/instances/${id}/restart`, null, { params: { force }, suppressErrorMessage: true } as any) }
export function getCpuInstanceSsh(id: string) { return request.get<CpuSshResponse>(`${cpuBase}/instances/${id}/ssh`, { suppressErrorMessage: true } as any) }
export function resetCpuInstancePassword(id: string, newPassword: string) { return request.post(`${cpuBase}/instances/${id}/reset-password`, { newPassword }, { suppressErrorMessage: true } as any) }
export function renameCpuInstance(id: string, name: string) { return request.put(`${cpuBase}/instances/${id}/name`, { name }, { suppressErrorMessage: true } as any) }
export function releaseCpuInstance(id: string) { return request.delete(`${cpuBase}/instances/${id}`, { suppressErrorMessage: true } as any) }
