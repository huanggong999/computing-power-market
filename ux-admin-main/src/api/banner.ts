/** * @description: 轮播图 */
import request from "@/utils/request";

// | /system/carousel/page { 轮播图列表 }
export const bannerPageApi = (params: any): Res =>
  request.get("/system/carousel/page", { params });

// | /system/carousel/save  { 新增轮播图 }
export const bannerSaveApi = (data: any): Res =>
  request.post("/system/carousel/save", data);

// | /system/carousel/{id} { 查询 ==> 详情 }
export const bannerDetailApi = (id: string): Res =>
  request.get(`/system/carousel/${id}`);

// | /system/carousel/update { 修改 }
export const bannerUpdateApi = (data: any): Res =>
  request.post("/system/carousel/update", data);

// | /system/carousel/delete { 删除 }
export const bannerDeleteApi = (id: string): Res =>
  request.get(`/system/carousel/delete?id=${id}`);
