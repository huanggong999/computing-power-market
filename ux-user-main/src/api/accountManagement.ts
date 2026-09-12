// ! 领券中心
import request from "@/utils/request";

// |  /pc/coupon/page { 优惠券列表 ==> 领券中心 }
export const getCouponListAPI = (params: any) =>
  request.get("/pc/coupon/page", { params });

// |  /pc/coupon/receive/{couponId} { 领取优惠券 }
export const receiveCouponAPI = (couponId: string) =>
  request.post(`/pc/coupon/receive/${couponId}`);
