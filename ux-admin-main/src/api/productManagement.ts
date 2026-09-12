/* 产品管理 */
import request from "@/utils/request";

//#region 咨询表单
// | /system/network-form/page { 分页列表 }
export const inquiryFormPageApi = (params: any): Res<any> =>
  request.get("/system/network-form/page", { params });

// | /system/network-form/save { 新增 }
export const inquiryFormSaveApi = (data: any): Res<any> =>
  request.post("/system/network-form/save", data);

// | /system/network-form/{id} { 详情 }
export const inquiryFormDetailApi = (id: string): Res<any> =>
  request.get(`/system/network-form/${id}`);

// | /system/network-form/update { 编辑 }
export const inquiryFormUpdateApi = (data: any): Res<any> =>
  request.post("/system/network-form/update", data);

// | /system/network-form/delete { 删除 }
export const inquiryFormDeleteApi = (id: string): Res<any> =>
  request.get(`/system/network-form/delete?id=${id}`);

//#endregion

//#region  产品列表
// | /system/network-product/page { 分页列表 }
export const productPageApi = (params: any): Res =>
  request.get("/system/network-product/page", { params });

// | /system/network-product/save { 新增 }
export const productSaveApi = (data: any): Res =>
  request.post("/system/network-product/save", data);

// | /system/network-product/{id} { 详情 }
export const productDetailApi = (id: string): Res =>
  request.get(`/system/network-product/${id}`);

// | /system/network-product/update { 编辑 }
export const productUpdateApi = (data: any): Res =>
  request.post("/system/network-product/update", data);

// | /system/network-product/delete { 删除 }
export const productDeleteApi = (id: string): Res =>
  request.get(`/system/network-product/delete?id=${id}`);

// | /system/network-value/product/detail { 购买工单详情 }
export const networkProductDetailApi = (id: string): Res =>
  request.get(`/system/network-value/product/detail?id=${id}`);

//#endregion

//#region 表单数据回收
// | /system/network-value/page { 分页列表 }
export const formDataPageApi = (params: any): Res<any> =>
  request.get("/system/network-value/page", { params });

// | /system/network-value/delete   { 删除 }
export const formDataDeleteApi = (id: string): Res =>
  request.get(`/system/network-value/delete?id=${id}`);

//#endregion

// #region 咨询购买
// | /system/network-value/page { 分页 }
export const networkValueApi = (params: any): Res =>
  request.get("/system/network-value/page", { params });

// | /system/network-value/product/open  { 开通产品 }
export const networkValueOpenApi = (data: TKeyValue): Res =>
  request.post("/system/network-value/product/open", data);

// | /system/network-value/product/open/delete?id= { 删除 }
export const networkValueDeleteApi = (id: string): Res =>
  request.get(`/system/network-value/delete?id=${id}`);

//#endregion

//#region 产品IP列表

// | /system/network-product/ip/page { 分页列表 }
export const productIpPageApi = (params: TKeyValue): Res =>
  request.get("/system/network-product/ip/page", { params });

// | /system/network-product/ip/save { 新增 }
export const productIpSaveApi = (params: TKeyValue): Res =>
  request.post("/system/network-product/ip/save", params);

// | /system/network-product/ip/update { 修改 }
export const productIpUpdateApi = (params: TKeyValue): Res =>
  request.post("/system/network-product/ip/update", params);

// | /system/network-product/ip/delete { 删除  }
export const productIpDeleteApi = (data: TKeyValue): Res =>
  request.delete(`/system/network-product/ip/delete`, { data });

// | /system/network-product/ip/downloadTemplate { AGI-C产品IP模板下载  }
export const productIpDownloadTemplateApi = (): Res =>
  request.get(`/system/network-product/ip/downloadTemplate`, {
    responseType: "blob",
  });

// | /system/network-product/ip/importIp/{productId} { 导入AGI-C产品IP }
export const productIpImportIpApi = (productId: string, data: FormData): Res =>
  request.post(`/system/network-product/ip/importIp/${productId}`, data, {
    headers: { ["Content-Type"]: "multipart/form-data" },
  });

// | /system/network-product/ip/export { 导出AGI-C产品IP }
export const productIpExportApi = (data: TKeyValue): Res =>
  request.post("/system/network-product/ip/export", data, {
    responseType: "blob",
  });
//#endregion
