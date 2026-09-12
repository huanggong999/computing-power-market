// ! 数据中心
import request from '@/utils/request'

export const getDataListAPI = (params: any) =>
  request.get('/pc/data/page', { params })

export const getDataDetailAPI = (id: any): Res =>
  request.get(`/pc/data/detail/${id}`)
