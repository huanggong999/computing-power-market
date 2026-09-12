import request from '@/utils/request'

// 获取容器概览
export const getContainerOverviewApi = (): Res =>
  request.get('/pc/container/get/container/overview')
// 获取容器列表
export const getContainerListApi = (data?: any): Res =>
  request.post('/pc/container/list', data)
// 获取容器详情
export const getContainerDetailApi = (id: any): Res =>
  request.get(`/pc/container/details/${id}`)
// 删除容器
export const deleteContainerApi = (id: any): Res =>
  request.delete(`/pc/container/delete/container/${id}`)
// 查询可用镜像版本
export const getAvailableImageVersionApi = (data: any): Res =>
  request.post('/pc/container/get/image/version', data)
