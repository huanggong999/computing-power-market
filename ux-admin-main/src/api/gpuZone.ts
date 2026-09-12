/** * @description: GPU专区管理 */

import request from "@/utils/request";

// | /system/gpu/zone/page { 分页列表 }
export const gpuZonePageApi = (params: any): Res =>
  request.get("/system/gpu/zone/page", { params });

// | /system/gpu/zone/list { 列表 }
export const gpuZoneListApi = (): Res =>
  request.get("/system/gpu/zone/list");

// | /system/gpu/zone/detail { 详情 }
export const gpuZoneDetailApi = (id: string): Res =>
  request.get(`/system/gpu/zone/detail?id=${id}`);

// | /system/gpu/zone/save { 新增 }
export const gpuZoneSaveApi = (data: any): Res =>
  request.post("/system/gpu/zone/save", data);

// | /system/gpu/zone/save { 编辑 }
export const gpuZoneUpdateApi = (data: any): Res =>
  request.post("/system/gpu/zone/save", data);

// | /system/gpu/zone/delete { 删除 }
export const gpuZoneDeleteApi = (id: string): Res =>
  request.get(`/system/gpu/zone/delete?id=${id}`);

// | /system/gpu/zone/status { 状态变更 }
export const gpuZoneStatusApi = (id: string, status: number): Res =>
  request.get(`/system/gpu/zone/status?id=${id}&status=${status}`);
