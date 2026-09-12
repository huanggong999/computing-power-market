/** * @description: 应用分类 */
import request from "@/utils/request";

// | /system/apply-type/page { 应用分类 ==> 分页 }
export const applyTypePageApi = (params: any): Res =>
  request.get("/system/apply-type/page", { params });

// | /system/apply-type/first-list { 应用分类 ==> 一级列表 }
export const applyTypeFirstListApi = (): Res<any[]> =>
  request.post("/system/apply-type/first-list");

// | /system/apply-type/save { 应用分类 ==> 新增 }
export const applyTypeSaveApi = (data: any): Res =>
  request.post("/system/apply-type/save", data);

// | /system/apply-type/update { 应用分类 ==> 修改 }
export const applyTypeUpdateApi = (data: any): Res =>
  request.post("/system/apply-type/update", data);

// | /system/apply-type/all  { 应用分类 ==> 所有 }
export const applyTypeAllApi = (): Res<any[]> =>
  request.post("/system/apply-type/all");

// | /system/apply-type/delete { 应用分类 ==> 删除 }
export const applyTypeDeleteApi = (id: string): Res<any> =>
  request.get(`/system/apply-type/delete?id=${id}`);
