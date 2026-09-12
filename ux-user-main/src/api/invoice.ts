// ! 发票中心
import request from "@/utils/request";

// 获取发票抬头列表
export const getInvoiceTitleListApi = (params?: any): Res =>
  request.get("/pc/invoice/title/list", { params });

// 新增发票抬头
export const createInvoiceTitleApi = (data?: any): Res =>
  request.post("/pc/invoice/title/save", data);

// 修改发票抬头
export const updateInvoiceTitleApi = (data?: any): Res =>
  request.post("/pc/invoice/title/update", data);

// 删除发票抬头
export const deleteInvoiceTitleApi = (id: any): Res =>
  request.delete(`/pc/invoice/title/delete/${id}`);

// 获取发票邮箱列表
export const getInvoiceEmailListApi = (params?: any): Res =>
  request.get("/pc/invoice/email/list", { params });

// 新增发票邮箱
export const createInvoiceEmailApi = (data?: any): Res =>
  request.post("/pc/invoice/email/save", data);

// 修改发票邮箱
export const updateInvoiceEmailApi = (data?: any): Res =>
  request.post("/pc/invoice/email/update", data);

// 删除发票邮箱
export const deleteInvoiceEmailApi = (id: any): Res =>
  request.delete(`/pc/invoice/email/delete/${id}`);

// 设置默认邮箱
export const setDefaultEmailApi = (data?: any): Res =>
  request.post(`/pc/invoice/email/setDefault`, data);

// ! 开票流程
// 获取发票相关金额
export const getInvoiceAmountListApi = (): Res =>
  request.get(`/pc/invoice/manage/amount/list`);
// 获取可申请开票数据
export const getEnableInvoiceListApi = (data?: any): Res =>
  request.post(`/pc/invoice/manage/list`, data);
// 申请开票
export const applyInvoiceApi = (data?: any): Res =>
  request.post(`/pc/invoice/manage/apply`, data);
// 取消开票
export const cancelInvoiceApi = (id?: any): Res =>
  request.get(`/pc/invoice/manage/cancel/invoice/${id}`);
// 导出明细
export const exportInvoiceDetailApi = (data?: any): Res =>
  request.post(`/pc/invoice/manage/export/details`, data, {
    responseType: "blob",
  });
// 已申请开票列表
export const getInvoiceListApi = (data?: any): Res =>
  request.post(`/pc/invoice/manage/apply/info`, data);

// 开票信息是否以完善
export const getInvoiceInfoApi = (): Res =>
  request.get(`/pc/invoice/manage/has/title/email`);
