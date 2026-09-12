// ! 网络产品
import request from '@/utils/request'

// 获取新闻列表
export const getNewsListApi = (params: any) =>
  request.get('/pc/news/page', { params })

// 获取新闻详情
export const getNewsDetailApi = (id: any) =>
  request.get(`/pc/news/${id}`)