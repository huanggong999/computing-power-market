//** 菜单管理 */

import request from "@/utils/request";

// | /system/menu/tree { 菜单树 }
export const getMenuTreeApi = (): Res => request.get("/system/menu/tree");

// | /system/menu/ { 详情 }
export const getMenuDetailApi = (id: string): Res =>
  request.get(`/system/menu/?id=${id}`);

// | /system/menu/save  { 新增 }
export const saveMenuApi = (data: any): Res =>
  request.post("/system/menu/save", data);

// | /system/menu/update { 编辑 }
export const updateMenuApi = (data: any): Res =>
  request.put("/system/menu/update", data);

//  | /system/menu/  { 删除 }
export const deleteMenuApi = (id: string): Res =>
  request.delete(`/system/menu/?id=${id}`);
