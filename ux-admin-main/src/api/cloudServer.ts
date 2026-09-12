/** * @description: 云服务器 */
import request from "@/utils/request";
// | /system/ecs/getPage { 列表 }
export const ecsPageApi = (params: any): Res =>
  request.get("/system/ecs/getPage", { params });

// | /system/ecs/syncEcs/{ecsType} { 同步ecs服务器 }
export const syncEcsApi = (ecsType: TEcsType, data: FormData): Res =>
  request.post("/system/ecs/syncEcs/" + ecsType, data, {
    headers: { ["Content-Type"]: "multipart/form-data" },
  });

// | /system/ecs/addEcs { 新增 }
export const addEcsApi = (data: any): Res =>
  request.post("/system/ecs/addEcs", data);
// | /system/ecs/updateEcs { 编辑 }
export const updateEcsApi = (data: any): Res =>
  request.post("/system/ecs/updateEcs", data);

// | /system/ecs/delete { 删除 }
export const deleteEcsApi = (id: string): Res =>
  request.get(`/system/ecs/delete?id=${id}`);

// GENERAL_COMPUTE = 通用型计算  COMPUTE 计算型  GENERAL 通用型  GPU GPU
export type TEcsType = "GENERAL_COMPUTE" | "COMPUTE" | "GENERAL" | "GPU";

// | /system/ecs/downloadTemplate { 自建服务器模板下载 }
export const downloadTemplateApi = (): Res =>
  request.get(`/system/ecs/downloadTemplate`, { responseType: "blob" });

// | /system/ecs/importPersonalEcs/{ecsType} { 导入自建服务器 }
export const importPersonalEcsApi = (ecsType: TEcsType, data: FormData): Res =>
  request.post("/system/ecs/importPersonalEcs/" + ecsType, data, {
    headers: { ["Content-Type"]: "multipart/form-data" },
  });
