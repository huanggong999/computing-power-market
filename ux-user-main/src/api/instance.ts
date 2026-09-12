import request from '@/utils/request'
import type {
  Instance,
  InstanceListParams,
  InstanceListResult,
  InstanceMetrics,
  SshInfo,
  VncInfo
} from '@/types/instance'

// ========== GPU Pod 实例管理 API（对接 GpuPodController）==========

// 获取实例列表
export function getInstanceList(params: any) {
  return request.get<any>('/pc/gpu-pod/instances', { params })
}

// 获取实例详情
export function getInstanceDetail(id: string) {
  return request.get<Instance>(`/pc/gpu-pod/instances/${id}`)
}

// 获取实例SSH连接信息
export function getInstanceSshInfo(id: string) {
  return request.get<SshInfo>(`/pc/gpu-pod/instances/${id}/ssh`, { suppressErrorMessage: true })
}

// 获取实例VNC连接信息
export function getInstanceVncInfo(id: string) {
  return request.get<VncInfo>(`/pc/gpu-pod/instances/${id}/vnc`)
}

// 开机
export function startInstance(id: string) {
  return request.post(`/pc/gpu-pod/instances/${id}/start`)
}

// 关机
export function stopInstance(id: string) {
  return request.post(`/pc/gpu-pod/instances/${id}/stop`)
}

// 重启
export function restartInstance(id: string) {
  return request.post(`/pc/gpu-pod/instances/${id}/restart`)
}

// 释放
export function releaseInstance(id: string) {
  return request.post(`/pc/gpu-pod/instances/${id}/release`)
}

// 批量开机（前端循环调用单实例接口）
export function batchStartInstances(ids: string[]) {
  return Promise.all(ids.map(id => startInstance(id)))
}

// 批量关机
export function batchStopInstances(ids: string[]) {
  return Promise.all(ids.map(id => stopInstance(id)))
}

// 批量重启
export function batchRestartInstances(ids: string[]) {
  return Promise.all(ids.map(id => restartInstance(id)))
}

// 批量释放
export function batchReleaseInstances(ids: string[]) {
  return Promise.all(ids.map(id => releaseInstance(id)))
}

// 重置密码
export function resetPassword(id: string, _newPassword: string) {
  return request.post(`/pc/gpu-pod/instances/${id}/reset-password`, { newPassword: _newPassword })
}

// 设置实例名称
export function setInstanceName(id: string, name: string) {
  return request.put(`/pc/gpu-pod/instances/${id}/name`, { name })
}

// 续费
export function renewInstance(id: string, data: { billingMode: string; duration: number }) {
  return request.post(`/pc/gpu-pod/instances/${id}/renew`, data)
}

// 批量续费
export function batchRenewInstances(data: { instanceIds: string[]; billingMode: string; duration: number }) {
  return request.post('/pc/gpu-pod/instances/batch-renew', data)
}

// 获取实例快捷工具
export function getInstanceTools(id: string) {
  return request.get(`/pc/gpu-pod/instances/${id}/tools`)
}

// 获取定时关机状态
export function getShutdownSchedule(id: string) {
  return request.get(`/pc/gpu-pod/instances/${id}/shutdown-schedule`)
}

// 设置定时关机
export function setShutdownSchedule(id: string, data: { shutdownTime?: string | null }) {
  return request.post(`/pc/gpu-pod/instances/${id}/shutdown-schedule`, data)
}

// 获取实例监控数据
export function getInstanceMetrics(
  id: string,
  params: { startTime: number; endTime: number; metrics?: string[] }
) {
  return request.get<InstanceMetrics>(`/pc/gpu-pod/instances/${id}/monitor`, { params })
}

// 获取GPU类型列表
export function getGpuTypes() {
  return request.get('/pc/gpu/market/meta')
}

// 获取区域列表
export function getRegions() {
  return request.get('/pc/region/getRegions')
}

// ========== 兼容旧版 API（其他页面仍在使用）==========

// 获取镜像列表
export const getImageListApi = (params: any): any =>
  request.get(`/pc/image/page`, { params })

// 获取实例详情（旧版）
export const getUserInstanceDetailApi = (id?: any): any =>
  request.get(`/pc/console/getInstanceDetail/${id}`)

// 批量启动实例
export const startInstanceApi = (data?: any): any =>
  request.put(`/pc/console/batchStartInstances/${data.region}`, data.idList)

// 批量停止实例
export const stopInstanceApi = (data?: any): any =>
  request.put(`/pc/console/batchStopInstances/${data.region}`, data)

// 批量重启实例
export const reStartInstanceApi = (data?: any): any =>
  request.put(`/pc/console/batchRestartInstances/${data.region}`, data.idList)

// 批量删除实例
export const deleteInstanceApi = (data?: any): any =>
  request.put(`/pc/console/batchDeletedInstances/${data.region}`, data.idList)

// 获取算力列表
export const getComputedListApi = (params: any): any =>
  request.get(`/pc/home-ecs/page`, { params })

// 自建服务器实例处理方法
export const buildInstanceHandlerApi = (data?: any): any =>
  request.post(`/pc/console/server/apply`, data)
