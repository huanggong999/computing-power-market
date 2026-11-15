/** * @description: 火山云 GPU 目录 */

import request from "@/utils/request";

export const gpuCatalogApi = (): Res<any> =>
  request.get("/system/gpu/volcano/catalog");

export const refreshGpuCatalogApi = (): Res<any> =>
  request.post("/system/gpu/volcano/catalog/refresh");

export interface VolcanoSalePriceQuery {
  regionCode?: string
  instanceTypeId?: string
  gpuModel?: string
  billingType?: string
  status?: number
}

export interface VolcanoSalePriceEditParams {
  id?: number
  regionCode: string
  instanceTypeId: string
  gpuModel: string
  gpuMemory?: string
  gpuCount?: number
  billingType: "on_demand" | "hourly" | "daily" | "weekly" | "monthly"
  upstreamPrice?: number | null
  salePrice: number
  status?: number
  remark?: string
}

export const volcanoPricePageApi = (params?: VolcanoSalePriceQuery): Res<any> =>
  request.get("/system/gpu/volcano/price/page", { params });

export const volcanoPriceSaveApi = (data: VolcanoSalePriceEditParams): Res<any> =>
  request.post("/system/gpu/volcano/price/save", data);

export const volcanoPriceStatusApi = (id: number, status: number): Res<any> =>
  request.get("/system/gpu/volcano/price/status", { params: { id, status } });

export const volcanoPriceDeleteApi = (id: number): Res<any> =>
  request.delete(`/system/gpu/volcano/price/${id}`);

export interface VolcanoCpuSalePriceEditParams {
  id?: number; regionCode: string; instanceTypeId: string; family?: string; cpuCores?: number; cpuModel?: string; memGib?: number;
  billingType: "on_demand" | "monthly"; upstreamPrice?: number | null; salePrice: number; status?: number; remark?: string; priceVersion?: string
}
export const volcanoCpuCatalogApi = (): Res<any> => request.get("/system/gpu/volcano/cpu/catalog")
export const refreshVolcanoCpuCatalogApi = (): Res<any> => request.post("/system/gpu/volcano/cpu/catalog/refresh", undefined, { timeout: 600000, noLoading: true } as any)
export const volcanoCpuPricePageApi = (params?: Record<string, unknown>): Res<any> => request.get("/system/gpu/volcano/cpu/price/page", { params })
export const volcanoCpuPriceSaveApi = (data: VolcanoCpuSalePriceEditParams): Res<any> => request.post("/system/gpu/volcano/cpu/price/save", data)
export const volcanoCpuPriceStatusApi = (id: number, status: number): Res<any> => request.get("/system/gpu/volcano/cpu/price/status", { params: { id, status } })
export const volcanoCpuPriceDeleteApi = (id: number): Res<any> => request.delete(`/system/gpu/volcano/cpu/price/${id}`)
