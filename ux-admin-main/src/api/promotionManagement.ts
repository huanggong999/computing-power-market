/* 推广管理 */
import request from "@/utils/request";

// ! 推广大使

//#region

// | /system/extend/page { 分页查询 }
export const promotionAmbassadorPageApi = (params: any): Res =>
  request.get("/system/extend/page", { params });

// | /system/extend/verify { 审核 }
export const promotionAmbassadorVerifyApi = (data: any): Res =>
  request.post("/system/extend/verify", data);

// | /system/extend/updateScale { 修改佣金比例 }
export const promotionAmbassadorUpdateScaleApi = (params: any): Res =>
  request.get("/system/extend/updateScale", { params });

// | /system/extend/first-page { 查看分销用户 }
export const promotionAmbassadorFirstPageApi = (params: any): Res =>
  request.get("/system/extend/first-page", { params });

// | system/extend/relevance  { 推广大使设置上级 }
export const promotionAmbassadorRelevanceApi = (data: any): Res =>
  request.post("/system/extend/relevance", data);

//#endregion

// ! 返佣订单
//#region
// | /system/extend/orderPage { 分页查询 }
export const promotionOrderPageApi = (params: any): Res =>
  request.get("/system/extend/orderPage", { params });

//#endregion

// ! 提现审核
//#region

// | /system/extend/firstPage { 分页查询 }
export const promotionWithdrawalFirstPageApi = (params: any): Res =>
  request.get("/system/extend/firstPage", { params });

// | /system/extend/with-verify { 提现审核 }
export const promotionWithdrawalVerifyApi = (data: any): Res =>
  request.post("/system/extend/with-verify", data);

//#endregion

// ! 推广配置
//#region

// | /system/extend-config/detail { 查询配置 }
export const promotionConfigurationDetailApi = (): Res =>
  request.get("/system/extend-config/detail");

// | /system/extend-config/update { 修改配置 }
export const promotionConfigurationUpdateApi = (data: any): Res =>
  request.post("/system/extend-config/update", data);

//#endregion

// ! 推广活动配置
//#region

// /system/extend/detail { 查询配置 }
export const promotionActivityDetailApi = (): Res =>
  request.get("/system/extend/detail");

// | /system/extend/update { 修改配置 }
export const promotionActivityUpdateApi = (data: any): Res =>
  request.post("/system/extend/update", data);

//#endregion
