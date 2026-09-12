import request from '@/utils/request'
import type { ApiResponse } from './gpuMarket'

// ==================== 类型定义 ====================

/** 镜像信息 */
export interface MirrorItem {
  id: string
  name: string
  type: string
  icon: string
  description: string
  baseImage?: string
  imageAddress?: string
  supportedGpuModels: string[]
}

/** 镜像版本 */
export interface MirrorVersion {
  id: string
  mirrorId: string
  version: string
  cudaVersion: string
  pythonVersion: string
  description: string
  image: string
  frameworks?: {
    pytorch?: string
    tensorflow?: string
    [key: string]: string | undefined
  }
}

/** 代金券 */
export interface CouponItem {
  id: string
  name: string
  type: 'discount' | 'deduction'
  value: number
  minAmount?: number
  expireTime: string
}

/** 账户信息 */
export interface AccountInfo {
  userId: number
  username: string
  balance: number
  frozenAmount: number
  availableBalance: number
  totalBalance: number
  coupons: CouponItem[]
}

/** 费用计算参数 */
export interface CalculateFeeParams {
  resourceId: number
  billingType: 'on_demand' | 'hourly' | 'daily' | 'weekly' | 'monthly'
  quantity: number
  duration?: number
  expandSize?: number
  couponId?: string
}

/** 费用计算结果 */
export interface FeeResult {
  unitPrice: number
  quantity: number
  duration: number
  subtotal: number
  discount: number
  total: number
  diskFee?: number
  couponInfo?: {
    id: string
    name: string
    discount: number
  }
}

/** GPU Pod 创建参数 */
export interface PodCreateRequest {
  gpuSpec: {
    model: string
    count: number
    gpuMemory: string
  }
  image: string
  billing: {
    mode: string
    duration: number
  }
  resource: {
    cpu: string
    cpuModel: string
    memory: string
    systemDisk: string
    dataDisk: string
    dataDiskExpandable: string
  }
  pricing: {
    unitPrice: number
    discountUnitPrice: number
    totalCost: number
    discountTotalCost: number
    currency: string
    unit: string
    pricePerHour?: number
    discountPrice?: number
  }
  region: string
  zone: string
  machineId: string
  gpuDriver: string
  cudaVersion: string
}

/** Pod创建阶段状态 */
export interface PodStatusResult {
  status?: string
  display_status?: string
  displayStatus?: string
  phase?: string
  node?: string
  start_time?: string
  startTime?: string
  reason?: string
  message?: string
  pricing?: {
    unit_price?: number
    unitPrice?: number
    discount_unit_price?: number
    discountUnitPrice?: number
    total_cost?: number
    totalCost?: number
    discount_total_cost?: number
    discountTotalCost?: number
    currency?: string
    unit?: string
  }
  billing_info?: {
    unit_price?: number
    unitPrice?: number
    discount_unit_price?: number
    discountUnitPrice?: number
    total_cost?: number
    totalCost?: number
    discount_total_cost?: number
    discountTotalCost?: number
    currency?: string
    unit?: string
  }
  container_states?: Array<{
    name?: string
    ready?: boolean
    restart_count?: number
    state?: string
    reason?: string
    message?: string
  }>
  events?: Array<[string, string] | string[] | Record<string, any>>
}

/** 创建订单参数 */
export interface CreateOrderParams {
  resourceId: number
  mirrorId: string
  mirrorVersionId: string
  billingType: 'on_demand' | 'hourly' | 'daily' | 'weekly' | 'monthly'
  quantity: number
  duration?: number
  couponId?: string
  agreeProtocol: boolean
  podCreateRequest: PodCreateRequest
}

/** 订单结果 */
export interface OrderResult {
  orderId: string
  orderNo: string
  status: 'pending' | 'paid' | 'processing' | 'completed'
  totalAmount: number
  paidAmount: number
  pricing?: {
    unit_price?: number
    unitPrice?: number
    discount_unit_price?: number
    discountUnitPrice?: number
    total_cost?: number
    totalCost?: number
    discount_total_cost?: number
    discountTotalCost?: number
    currency?: string
    unit?: string
  }
  createTime: string
  instanceInfo?: {
    instanceId: string
    instanceName: string
    podName?: string
    tenantId?: string
    status: string
  }
}

// ==================== API 函数 ====================

/**
 * 获取 GPU 资源详情
 * @param resourceId 资源ID
 */
export function getGpuResourceDetail(resourceId: number): Promise<ApiResponse<any>> {
  return request({
    url: `/pc/gpu/market/detail/${resourceId}`,
    method: 'get'
  })
}

/**
 * 获取镜像列表
 * @param params 查询参数
 */
export function getMirrorList(params?: { gpuModel?: string }): Promise<ApiResponse<MirrorItem[]>> {
  return request({
    url: '/pc/gpu/mirror/list',
    method: 'get',
    params
  })
}

/**
 * 获取镜像版本
 * @param mirrorId 镜像ID
 */
export function getMirrorVersions(mirrorId: string): Promise<ApiResponse<MirrorVersion[]>> {
  return request({
    url: `/pc/gpu/mirror/${mirrorId}/versions`,
    method: 'get'
  })
}

/**
 * 获取用户账户信息
 */
export function getAccountInfo(): Promise<ApiResponse<AccountInfo>> {
  return request({
    url: '/pc/customer/info',
    method: 'get'
  }).then((res: ApiResponse<any>) => {
    if (res.code === 200 && res.data) {
      const balance = Number(res.data.balance || 0)
      const totalBalance = Number(res.data.totalBalance ?? res.data.balance ?? 0)
      res.data = {
        ...res.data,
        userId: Number(res.data.userId || 0),
        username: res.data.customerName || res.data.username || '',
        balance,
        frozenAmount: Math.max(balance - totalBalance, 0),
        availableBalance: balance,
        totalBalance,
        coupons: res.data.coupons || []
      }
    }
    return res
  })
}

/**
 * 计算租用费用
 * @param data 计算参数
 */
export function calculateRentFee(data: CalculateFeeParams): Promise<ApiResponse<FeeResult>> {
  return request({
    url: '/pc/gpu/rent/calculate',
    method: 'post',
    data
  })
}

/**
 * 创建租用订单
 * @param data 订单参数
 */
export function createRentOrder(data: CreateOrderParams): Promise<ApiResponse<OrderResult>> {
  return request({
    url: '/pc/gpu/rent/order',
    method: 'post',
    data,
    timeout: 5 * 60 * 1000,
    suppressErrorMessage: true
  })
}

/**
 * 获取 Pod 创建阶段状态
 */
export function getPodStatus(podName: string): Promise<ApiResponse<PodStatusResult>> {
  return request({
    url: `/pc/gpu/rent/pod/${encodeURIComponent(podName)}/status`,
    method: 'get',
    noLoading: true,
    suppressErrorMessage: true
  })
}
