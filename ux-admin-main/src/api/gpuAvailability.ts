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
