/*** @description: 用户中心 */

import request from "@/utils/request";

//#region // ! 用户管理

// | /system/customer/page { 用户列表 ==> 分页 }
export const getUserListApi = (params: any): Res =>
  request.get("/system/customer/page", { params });

// | /system/customer/giveCoupon/{customerId}  { 赠送优惠卷 }
export const giveCouponApi = (customerId: string, data: any): Res =>
  request.put(`/system/customer/giveCoupon/${customerId}`, data);

// | /system/customer/updateBatchCustomerDiscount { 修改客户资源折扣比列 }
export const updateCustomerDiscountApi = (data: ISetUpPDParams): Res =>
  request.put("/system/customer/updateBatchCustomerDiscount", data);

// | /system/customer/addCustomerVoucher { 添加代金卷 }
export const addCustomerVoucherApi = (data: TKeyValue): Res =>
  request.post("/system/customer/addCustomerVoucher", data);

// | /system/customer/addCustomerCreditLine { 添加授信额 }
export const addCustomerCreditLineApi = (data: any): Res =>
  request.post("/system/customer/addCustomerCreditLine", data);

// | /system/customer/getInstancePageList  { 实例列表 ==> 分页 }
export const getInstancePageListApi = (params: any): Res =>
  request.get("/system/customer/getInstancePageList", { params });

// | /system/customer/batchStopInstances/{sourceRegIons} { 批量停止实例 }
export const stopInstancesApi = (sourceRegIons: string, data: TKeyValue): Res =>
  request.put(`/system/customer/batchStopInstances/${sourceRegIons}`, data);

// | /system/customer/batchDeletedInstances/{sourceRegIons} { 批量删除实例 }
export const deletedInstancesApi = (
  sourceRegIons: string,
  data: TKeyValue
): Res =>
  request.put(`/system/customer/batchDeletedInstances/${sourceRegIons}`, data);

// // | /system/customer/image/repository/list { 镜像列表 ==> 分页 }
// export const getImageListApi = (data: TKeyValue): Res =>
//   request.post("/system/customer/image/repository/list", data);

// // | /system/customer/image/repository/delete/image { 删除镜像 }
// export const deleteImageApi = (data: TKeyValue): Res =>
//   request.post("/system/customer/image/repository/delete/image", data);

// | /system/customer/container/list  { 容器列表 ==> 分页 }
export const getContainerListApi = (data: TKeyValue): Res =>
  request.post("/system/customer/container/list", data);

// | /system/customer/delete/container/{id} { 删除容器 }
export const deleteContainerApi = (id: string): Res =>
  request.delete(`/system/customer/delete/container/${id}`);

// | /system/customer/openExtend?customerId={用户id}  { 开通推广权限 }
export const openExtendApi = (customerId: string): Res =>
  request.get(`/system/customer/openExtend?customerId=${customerId}`);

// | /system/customer/updateParent { 修改用户上级 }
export const updateParentApi = (data: any): Res =>
  request.post("/system/customer/updateParent", data);
//#endregion

//#region   // ! 企业认证

// | /system/customer/company/page { 企业认证列表 ==> 分页 }
export const getEnterpriseCertificationListApi = (params: any): Res =>
  request.get("/system/customer/company/page", { params });

// |  /system/customer/verifyCompany { 审核企业认证 }
export const enterpriseCertificationVerifyApi = (data: any): Res =>
  request.post("/system/customer/verifyCompany", data);
//#endregion

//#region // !  代金券明细

// | /system/customer/amount/page { 分页 }
export const amountPageApi = (params: any): Res =>
  request.get("/system/customer/amount/page", { params });

//#endregion

//#region // ! 授信额合同

// | /system/creditContract/page { 分页 }
export const creditContractPageApi = (params: any): Res =>
  request.get("/system/creditContract/page", { params });

// | /system/creditContract/send-contract { 发送合同  }
export const sendContractApi = (data: any): Res =>
  request.post("/system/creditContract/send-contract", data);

// | system/creditContract/verify-contract { 审核线下合同 }
export const verifyContractApi = (data: any): Res =>
  request.post("/system/creditContract/verify-contract", data);

// | /system/creditContract/upload-contract  { 上传签署扫描件 }
export const uploadContractApi = (data: any): Res =>
  request.post("/system/creditContract/upload-contract", data);

// | /system/creditContract/revoke-contract { 撤销合同 }
export const revokeContractApi = (data: any): Res =>
  request.post("/system/creditContract/revoke-contract", data);
//#endregion
