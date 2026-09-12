/** * @description: 应用列表 */
import request, { NoLoadingType } from "@/utils/request";

// | /system/apply/page { 列表 }
export const applicationListApi = (params: any): Res =>
  request.get("/system/apply/page", { params });

// | /system/apply/save { 新增 }
export const applicationSaveApi = (data: any): Res =>
  request.post("/system/apply/save", data);

// | /system/apply/{id}  { 查询 ==> 详情 }
export const applicationDetailApi = (id: string): Res =>
  request.get("/system/apply/" + id);

// | /system/apply/update { 修改 }
export const applicationUpdateApi = (data: any): Res =>
  request.post("/system/apply/update", data);

// | /pc/image/page  { 获取镜像列表 }
export const imageListApi = (params: any): Res =>
  request.get("/pc/image/page", {
    params,
    noLoading: true,
  } as NoLoadingType);

// | /system/apply/delete { 删除 }
export const applicationDeleteApi = (id: string): Res =>
  request.get(`/system/apply/delete?id=${id}`);
