/** * @description: 优惠券 */

import request from "@/utils/request";

// | /system/coupon/page  { 分页列表 }
export const couponPageApi = (params: any): Res =>
  request.get("/system/coupon/page", { params });

// | /system/coupon/ { 详情 }
export const couponDetailApi = (id: string): Res =>
  request.get(`/system/coupon/?id=${id}`);

// | /system/coupon/save  { 新增 }
export const couponSaveApi = (data: any): Res =>
  request.post("/system/coupon/save", data);

// | /system/coupon/update  { 编辑 }
export const couponUpdateApi = (data: any): Res =>
  request.put("/system/coupon/update", data);

// | /system/coupon/ { 删除 }
export const couponDeleteApi = (id: string): Res =>
  request.delete(`/system/coupon/?id=${id}`);
