// ! 区域相关
import request from "@/utils/request";
import {
  IEcsParams,
  IEcsImgParams,
  ISourceRegion,
  IBuildOrderParams,
} from "./types/order";

// 获取区域列表
export const getRegionListApi = (): Res<ISourceRegion[]> =>
  request.get("/pc/region/getRegions");
// 获取区域可用区列表
export const getUsefulRegionUsefulListApi = (regionId: any): Res =>
  request.get(`/pc/region/getZones/${regionId}`);

// ! 云服务器相关

// 获取ecs列表
export const getEcsListApi = (params: IEcsParams, signal: any): Res =>
  request.get("/pc/ecs/getEcsList", { params, signal });
// 获取镜像列表
export const getEcsImgListApi = (params: IEcsImgParams): Res =>
  request.get("/pc/ecs/getEcsImgList", { params });
// 创建服务器
export const createEcsApi = (data: any): Res =>
  request.post("/pc/ecs/create", data);
// 构建订单信息
export const buildOrderInfoApi = (data: IBuildOrderParams): Res =>
  request.post("/pc/order/buildOrderInfo", data);
// 创建订单信息
export const createOrderApi = (data: any): Res =>
  request.post("/pc/order/createOrder", data);

// ! 订单

// | /pc/order/page  { 订单列表 }
export const getOrderListAPI = (params: any): Res =>
  request.get("/pc/order/page", { params });
// | /pc/order/agic/page {获取AGIC订单}
export const getAgicOrderListAPI = (params: any): Res =>
  request.get("/pc/order/agic/page", { params });
// | /pc/order/orderSourceList/{orderId}  { 资源订单 }
export const getOrderSourceListAPI = (orderId: string, params: any): Res =>
  request.get(`/pc/order/orderSourceList/${orderId}`, { params });

// ! 微信支付回调
export const weChatPayCallbackApi = (orderNo: any): Res =>
  request.get(`/pc/order/detail?orderNo=${orderNo}`);

export const weChatPay = (orderNo: string) =>
  request.get(`/pc/order/getWechatPayQrCode?orderNo=${orderNo}`);

// ! AIGC 咨询表单
export const getAIGCFormListApi = (params: any): Res =>
  request.get("/pc/network-product/payValue/page", {
    params,
  });

// ! 授信额
// 获取授信额合同分页
export const getCreditOrderListAPI = (params: any): Res =>
  request.get("/pc/credit-contract/page", { params });
// 审核合同
export const auditCreditOrderAPI = (data: any): Res =>
  request.post("/pc/credit-contract/apply-contract", data);
// 上传纸质合同(授信额)
export const uploadCreditOrderAPI = (data: any): Res =>
  request.post("/pc/credit-contract/upload-contract", data);
// 上传纸质合同(订单)
export const uploadNormalOrderAPI = (data: any): Res =>
  request.post("/pc/contract/upload-contract", data);
