import { IBuildOrderParams } from './types/order'
// ! 网络产品
import request from '@/utils/request'

export const getNetWorkProductListAPI = (params: any) =>
  request.get('/pc/network-product/page', { params })

export const getNetWorkProductDetailAPI = (id: any) =>
  request.get(`/pc/network-product/${id}`)

export const getNetWorkProductFormAPI = (id: any) =>
  request.get(`/pc/network-product/form/${id}`)

export const getNetWorkPayFormApi = (id: any) =>
  request.get(`/pc/network-product/pay-form/${id}`)
export const submitNetWorkFormApi = (data: any) =>
  request.post('/pc/network-product/form/add', data)

// 新增购买咨询记录
export const submitNetWorkPayFormApi = (data: any) =>
  request.post('/pc/network-product/form/addPay', data)

// 构建AGIC订单信息
export const buildAgicOrderInfoApi = (data: any): Res =>
  request.post('/pc/order/buildAGOrderInfo', data)
// 创建AGIC订单信息
export const createAgicOrderApi = (data: any): Res =>
  request.post('/pc/order/createAGOrder', data)

// 查询AGIc订单信息
export const getAgicOrderInfoApi = (id: any): Res =>
  request.get('/pc/order/getProductDetail', {
    params: { networkValueId: id },
  })

// 修改自动续费状态
export const updateAutoRenewStatusApi = (data: any): Res =>
  request.post('/pc/order/updateAgiIsAutoRenew', data)

// 暂停
export const stopLineApi = (id: any): Res =>
  request.get('/pc/network-product/stop', {
    params: { valueId: id },
  })
// 撤线
export const cancelLineApi = (id: any): Res =>
  request.get('/pc/network-product/cancel', {
    params: { valueId: id },
  })
// 上线
export const upLineApi = (id: any): Res =>
  request.get('/pc/network-product/start', {
    params: { valueId: id },
  })
// 编辑产品名称
export const editProductNameApi = (data: any): Res =>
  request.post('/pc/order/updateAgiCustomerName', data)

// 重置密码
export const agiResetPasswordApi = (email: string): Res =>
  request.get('/pc/network-product/reset/password', {
    params: { email },
  })
// 判断邮箱是否在飞连存在
export const checkEmailExistApi = (data: any): Res =>
  request.post('/pc/order/isEmailExist', data)

// 判断ip数是否足够
export const checkIpNumApi = (data: any): Res =>
  request.post('/pc/network-product/ip/enough', data)
