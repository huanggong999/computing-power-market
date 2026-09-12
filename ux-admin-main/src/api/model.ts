/** * @description: 模型 */

import request from "@/utils/request";
// | /system/model/page { 分页 }
export const modelPageApi = (params: any): Res =>
  request.get("/system/model/page", { params });

// | /system/model/save { 新增 }
export const modelSaveApi = (data: any): Res =>
  request.post("/system/model/save", data);

// | /system/model/{id} { 详情 }
export const modelDetailApi = (id: string): Res =>
  request.get(`/system/model/${id}`);

// | /system/model/update { 编辑 }
export const modelUpdateApi = (data: any): Res =>
  request.post("/system/model/update", data);

// | /system/model/delete/{id} { 删除 }
export const modelDeleteApi = (id: string): Res =>
  request.get(`/system/model/delete?id=${id}`);
