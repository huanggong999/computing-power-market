/** * @description: 算力服务器 */

import request from "@/utils/request";

// | /system/home-ecs/page { 分页列表 }
export const homeEcsPageApi = (params: any): Res =>
  request.get("/system/home-ecs/page", { params });

// | /system/home-ecs/save { 新增 }
export const homeEcsSaveApi = (data: any): Res =>
  request.post("/system/home-ecs/save", data);

// | /system/home-ecs/update { 编辑 }
export const homeEcsUpdateApi = (data: any): Res =>
  request.post("/system/home-ecs/update", data);

// | /system/home-ecs/delete { 删除 }
export const homeEcsDeleteApi = (id: string): Res =>
  request.get(`/system/home-ecs/delete?id=${id}`);
