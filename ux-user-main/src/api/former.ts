// ! 模型中心
import request from '@/utils/request'
import { IModelItem } from './types/home'

// |  /pc/coupon/page { 模型列表  }
export const getModelListAPI = (params: any): Res =>
  request.get('/pc/model/page', { params })

// |  /pc/model/detail { 模型详情  }
export const getModelDetailAPI = (id: any): Res =>
  request.get(`/pc/model/detail/${id}`)
