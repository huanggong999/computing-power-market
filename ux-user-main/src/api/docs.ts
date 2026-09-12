// ! 文档中心
import request from '@/utils/request'

// 获取文档分类列表
export const getDocsTypeListApi = (params?: any): Res<any> =>
  request.get(`/pc/document/type-list`, { params })

// 获取文档详情
export const getDocsDetailByIdApi = (id: any): Res<any> =>
  request.get(`/pc/document/type/${id}`)

// 获取文档详情
// export const getDocsDetailApi = (id: any): Res<any> =>
//   request.get(`/pc/document/${id}`)
