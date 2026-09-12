/** * @description: GPU地区管理 */

import request from "@/utils/request";

// | /system/gpu/region/page { 分页列表 }
export const gpuRegionPageApi = (params: any): Res =>
  request.get("/system/gpu/region/page", { params });

// | /system/gpu/region/list { 列表 }
export const gpuRegionListApi = (): Res =>
  request.get("/system/gpu/region/list");

// | /system/gpu/region/detail { 详情 }
export const gpuRegionDetailApi = (id: string): Res =>
  request.get(`/system/gpu/region/detail?id=${id}`);

// | /system/gpu/region/save { 新增 }
export const gpuRegionSaveApi = (data: any): Res =>
  request.post("/system/gpu/region/save", data);

// | /system/gpu/region/update { 编辑 }
export const gpuRegionUpdateApi = (data: any): Res =>
  request.post("/system/gpu/region/update", data);

// | /system/gpu/region/delete { 删除 }
export const gpuRegionDeleteApi = (id: string): Res =>
  request.get(`/system/gpu/region/delete?id=${id}`);

// | /system/gpu/region/status { 状态变更 }
export const gpuRegionStatusApi = (id: string, status: number): Res =>
  request.get(`/system/gpu/region/status?id=${id}&status=${status}`);
