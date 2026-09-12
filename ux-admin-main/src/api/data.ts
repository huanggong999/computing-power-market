/** * @description: 数据 */

import request from "@/utils/request";

// | /system/data/page { 分页 }
export const dataPageApi = (params: any): Res =>
  request.get("/system/data/page", { params });

// | /system/data/save { 新增 }
export const dataSaveApi = (data: any): Res =>
  request.post("/system/data/save", data);

// | /system/data/{id} { 详情 }
export const dataDetailApi = (id: string): Res =>
  request.get(`/system/data/${id}`);

// | /system/data/update { 编辑 }
export const dataUpdateApi = (data: any): Res =>
  request.post("/system/data/update", data);

// | /system/data/delete { 删除 }
export const dataDeleteApi = (id: string): Res =>
  request.get(`/system/data/delete?id=${id}`);
