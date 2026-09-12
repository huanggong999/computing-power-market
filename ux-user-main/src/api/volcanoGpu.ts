import request from '@/utils/request'

export interface VolcanoPrice {
  available?: boolean
  unitPrice?: number | null
  unit_price?: number | null
  discountPrice?: number | null
  discount_unit_price?: number | null
  currency?: string
  error?: string
}

export interface VolcanoInstanceType {
  instanceTypeId?: string
  gpuCount?: number
  cpuCores?: number
  cpuModel?: string
  memGib?: number
  price?: VolcanoPrice | number | null
  priceMonthly?: VolcanoPrice | number | null
}

export interface VolcanoGpuSpec {
  gpuModel?: string
  gpuMemory?: string
  gpuCounts?: number[]
  price?: VolcanoPrice | number | null
  priceMonthly?: VolcanoPrice | number | null
  instanceTypes?: VolcanoInstanceType[]
}

export interface VolcanoRegion {
  region?: string
  gpuSpecs?: VolcanoGpuSpec[]
  error?: string
}

export interface VolcanoGpuCatalog {
  success?: boolean
  source?: string
  date?: string
  generatedAt?: string
  updatedAt?: string
  availabilitySource?: string
  availabilityUpdatedAt?: string
  mergeMode?: string
  mergeMessage?: string
  message?: string
  pricing?: {
    status?: string
    error?: string
    total?: number
    available?: number
  }
  regions?: VolcanoRegion[]
}

export function getVolcanoGpuCatalog(billingType?: string): Promise<ApiResponse<VolcanoGpuCatalog>> {
  return request({
    url: '/pc/gpu/market/volcano/catalog',
    method: 'get',
    params: billingType ? { billingType } : undefined,
    headers: { isToken: false },
    skipAuthRedirect: true,
  })
}
