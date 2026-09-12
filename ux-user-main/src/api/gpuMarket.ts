import request from '@/utils/request'

// GPU市场筛选项
export interface GpuMarketMeta {
  regions: GpuRegionItem[]
  zones: GpuZoneItem[]
  gpuModels: GpuModelStat[]
  gpuCounts: number[]
  billingTypes: GpuBillingType[]
}

export interface GpuRegionItem {
  regionCode: string
  regionName: string
}

export interface GpuZoneItem {
  zoneCode: string
  zoneName: string
}

export interface GpuModelStat {
  model: string
  availableCount: number
  totalCount: number
}

export interface GpuBillingType {
  code: string
  name: string
}

// GPU市场列表项
export interface GpuMarketItem {
  resourceId: number
  resourceNo: string
  model: string
  vram: string
  region: string
  regionCode: string
  zone?: string
  zoneCode?: string
  machineId: string
  machineUuid: string
  rentableUntil: string
  availableCount: number
  totalCount: number
  cacheOptimized: boolean
  cpuCores: number
  cpuModel: string
  memory: string
  systemDisk: string
  dataDisk: string
  expandable: string
  gpuDriver: string
  cudaVersion: string
  price: string
  priceMonthly?: string
  discountPrice: string
  discountRate: string
  rentableCount: number
  source?: 'platform' | 'volcano'
  instanceTypeId?: string
  gpuCount?: number
  volcanoPrice?: unknown
}

// 分页结果
export interface PageResult<T> {
  pageNo: number
  pageSize: number
  pageTotal: number
  dataTotal: number
  list: T[]
}

// API响应
export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

/**
 * 获取GPU市场筛选项元数据
 */
export function getGpuMarketMeta(): Promise<ApiResponse<GpuMarketMeta>> {
  return request({
    url: '/pc/gpu/market/meta',
    method: 'get',
    headers: { isToken: false },
  })
}

/**
 * 获取GPU资源列表
 * @param data 查询参数
 */
export function getGpuMarketList(data: {
  billingType?: string
  regionCode?: string
  zoneCode?: string
  gpuModels?: string[]
  gpuCount?: number
  sortBy?: string
  sortOrder?: string
  pageNo?: number
  pageSize?: number
}): Promise<ApiResponse<PageResult<GpuMarketItem>>> {
  return request({
    url: '/pc/gpu/market/list',
    method: 'post',
    data,
    headers: { isToken: false },
  })
}

/**
 * 获取GPU资源详情
 * @param resourceId 资源ID
 */
export function getGpuMarketDetail(resourceId: number): Promise<ApiResponse<GpuMarketItem>> {
  return request({
    url: `/pc/gpu/market/detail/${resourceId}`,
    method: 'get',
    headers: { isToken: false },
  })
}
