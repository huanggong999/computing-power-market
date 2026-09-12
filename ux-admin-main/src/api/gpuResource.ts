/** * @description: GPU资源管理 */

import request from "@/utils/request";

// | /system/gpu/resource/page { 分页列表 }
export const gpuResourcePageApi = (params: any): Res =>
  request.get("/system/gpu/resource/page", { params });

// | /system/gpu/resource/detail { 详情 }
export const gpuResourceDetailApi = (id: string): Res =>
  request.get(`/system/gpu/resource/${id}`);

// | /system/gpu/resource/save { 新增 }
export const gpuResourceSaveApi = (data: any): Res =>
  request.post("/system/gpu/resource/save", data);

// | /system/gpu/resource/update { 编辑 }
export const gpuResourceUpdateApi = (data: any): Res =>
  request.post("/system/gpu/resource/save", data);

// | /system/gpu/resource/delete { 删除 }
export const gpuResourceDeleteApi = (id: string): Res =>
  request.delete(`/system/gpu/resource/${id}`);

// | /system/gpu/resource/status { 状态变更 }
export const gpuResourceStatusApi = (id: string, status: number): Res =>
  request.post("/system/gpu/resource/status", null, { params: { id, status } });
