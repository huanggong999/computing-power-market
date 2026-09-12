// ! 合同相关
import request from '@/utils/request'

// 获取订单合同列表
export const getOrderContractListApi = (params: any): Res<any> =>
  request.get(`/pc/contract/page`, { params })

// 获取乙方信息
export const getCompanyContractInfoApi = (): Res<any> =>
  request.get(`/pc/contract/company-info`)
// 生成订单合同
export const createOrderContractApi = (data: any): Res<any> =>
  request.post(`/pc/contract/generate-order-contract`, data)
