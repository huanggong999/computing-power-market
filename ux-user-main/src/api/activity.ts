// ! 应用中心
import request from '@/utils/request'

// | 活动列表
export const getActivityListApi = (params?: any) =>
  request.get(`/pc/active/details/list`, { params })
// |  活动详情
export const getActivityDetailApi = (id: any) =>
  request.get(`/pc/active/details/${id}`)
// | 活动参与情况
export const getActivityRecordApi = (params: any) =>
  request.get(`/pc/active/record/page`, { params })
// | 获取邀请人信息
export const getInviteInfoApi = (userId: any) =>
  request.get(`/pc/active/getUserById/${userId}`)
