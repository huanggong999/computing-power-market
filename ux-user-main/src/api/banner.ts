// ! 应用中心
import request from '@/utils/request'

// |  /pc/carousel/list {  banner列表  }
export const getBannerListApi = (params: any): Res =>
  request.get('/pc/carousel/list', { params })
