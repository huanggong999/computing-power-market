// ! 应用中心
import request from '@/utils/request'

// | /pc/customer/getEidTokenApi {获取EidToken}
export const getEidTokenApi = (data: any) =>
  request.post(`/pc/customer/getEidToken`, data)
// | 申请实名认证 600s有效期
export const applyRealNameVerifyApi = (data: any) =>
  request.post(`/pc/customer/realNameVerify`, data)
// | 轮询实名认证情况
export const getEidResultApi = () => request.get(`/pc/customer/getEidResult`)
// | /pc/customer/getEidTokenApi {获取EidToken}
export const getVerifyLogApi = () =>
  request.get(`/pc/customer/realNameVerify/log`)
