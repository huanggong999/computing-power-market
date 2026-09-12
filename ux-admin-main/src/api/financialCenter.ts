import request from "@/utils/request";

// ! 财务中心

// ! 费用账单

// | /system/bill/overviewPage 获取账单总览列表
export const getBillOverviewPageApi = (params: any) =>
  request.get("/system/bill/overviewPage", { params });

// | /system/bill/page 获取账单列表
export const getBillPageApi = (params: any) =>
  request.get("/system/bill/page", { params });

// !  订单合同

// | /system/contract/page { 分页 }
export const contractPageApi = (params: any): Res =>
  request.get("/system/contract/page", { params });

// !  对公打款
// | /system/remit/page { 分页 }
export const remitPageApi = (params: any): Res =>
  request.get("/system/remit/page", { params });

// | /system/remit/verify    { 审核打款 }
export const remitVerifyApi = (params: any): Res =>
  request.post("/system/remit/verify", params);

// | /system/contract/verify-contract { 审核纸质合同 }
export const verifyContractApi = (data: any): Res =>
  request.post("/system/contract/verify-contract", data);

// | /system/contract/upload-contract { 上传签署扫描件 }
export const uploadContractApi = (data: any): Res =>
  request.post("/system/contract/upload-contract", data);
