// ! 推广相关
import request from '@/utils/request'
import { IPersonalExtendInfo } from './types/partner'

// 官网申请推广
export const applyExtendApi = (data: any): Res<any> =>
  request.post('/pc/extend/apply', data)

// 获取个人推广信息
export const getExtendInfoApi = (): Res<IPersonalExtendInfo> =>
  request.get('/pc/extend/detail')

// 获取官网推广配置
export const getExtendConfigApi = (): Res<any> =>
  request.get('/pc/extend/config-detail')

// 关联推广大使
export const bindExtendRelevanceApi = (data: any): Res<any> =>
  request.get('/pc/extend/relevance', { params: data })

// 根据邀请码查询推广大使
export const getExtendByCodeApi = (data: any): Res<any> =>
  request.get('/pc/extend/getByCode', { params: data })

// 推广大使提现
export const extendWithdrawApi = (amount: any): Res<any> =>
  request.get('/pc/extend/with', { params: { amount } })

// 推广大使修改收款信息
export const extendUpdateBankApi = (data: any): Res<any> =>
  request.post('/pc/extend/updateBank', data)

// 获取推广客户列表
export const getExtendCustomerListApi = (data: any): Res<any> =>
  request.get(`/pc/extend/first-page`, { params: data })

// 获取分佣订单列表
export const getExtendOrderListApi = (data: any): Res<any> =>
  request.get(`/pc/extend/orderPage`, { params: data })
// 获取提现列表
export const getExtendWithdrawListApi = (data: any): Res<any> =>
  request.get(`/pc/extend/withPage`, { params: data })
