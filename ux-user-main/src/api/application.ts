// ! 应用中心
import request from "@/utils/request";

// |  /pc/coupon/page { 应用列表  }
export const getApplyListAPI = (params: any) =>
  request.get("/pc/apply/page", { params });
// |  /pc/coupon/page { 应用列表  }
export const getApplyTypeListAPI = () =>
  request.get("/pc/apply-type/getAll");


// |  /pc/coupon/receive/{couponId} { 应用详情 }
export const detailApplyAPI = (id: number) =>
request.get(`/pc/apply/${id}`);

