/** * @description: GPU规格管理 */

import request from "@/utils/request";

// | /system/gpu/spec/page { 分页列表 }
export const gpuSpecPageApi = (params: any): Res =>
  request.get("/system/gpu/spec/page", { params });

// | /system/gpu/spec/list { 列表 }
export const gpuSpecListApi = (): Res =>
  request.get("/system/gpu/spec/list");

// | /system/gpu/spec/detail { 详情 }
export const gpuSpecDetailApi = (id: string): Res =>
  request.get(`/system/gpu/spec/detail?id=${id}`);

// | /system/gpu/spec/save { 新增 }
export const gpuSpecSaveApi = (data: any): Res =>
  request.post("/system/gpu/spec/save", data);

// | /system/gpu/spec/update { 编辑 }
export const gpuSpecUpdateApi = (data: any): Res =>
  request.post("/system/gpu/spec/update", data);

// | /system/gpu/spec/delete { 删除 }
export const gpuSpecDeleteApi = (id: string): Res =>
  request.get(`/system/gpu/spec/delete?id=${id}`);

// | /system/gpu/spec/status { 状态变更 }
export const gpuSpecStatusApi = (id: string, status: number): Res =>
  request.get(`/system/gpu/spec/status?id=${id}&status=${status}`);
