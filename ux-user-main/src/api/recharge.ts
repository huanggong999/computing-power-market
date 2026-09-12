// ! 充值相关
import request from '@/utils/request'

// 获取对公帐户信息
export const getRemitAccountInfoApi = (): Res<any> =>
  request.get('/pc/remit/account')

// 获取对公充值记录
export const getRemitAccountListApi = (params?: any): Res<any> =>
  request.get('/pc/remit/page', { params })

// 创建对公充值
export const createRemitAccountApi = (data: any): Res<any> =>
  request.post('/pc/remit/add', data)

// 获取充值活动列表
export const getRechargeActivityListApi = (): Res<any> =>
  request.get('/pc/active/getCouponList')
