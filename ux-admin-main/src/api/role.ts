/** * @description: 角色 */

import request from "@/utils/request";

// | /system/role/page  { 角色列表分页 }
export const getRoleListApi = (params: any): Res =>
  request.get("/system/role/page", { params });

// | /system/role/  { 角色详情 }
export const getRoleDetailApi = (id: string): Res =>
  request.get(`/system/role/?id=${id}`);

// | /system/role/save   { 新增角色 } 
export const addRoleApi = (data: any): Res =>
  request.post("/system/role/save", data);

// | /system/role/update  { 编辑角色 }
export const editRoleApi = (data: any): Res =>
  request.put("/system/role/update", data);

// | /system/role/ { 删除角色 }
export const deleteRoleApi = (id: string): Res =>
  request.delete(`/system/role/?id=${id}`);
