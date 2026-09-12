/** * @description: 开票管理 */

import request from "@/utils/request";

// | /system/invoice/manage/list { 开票列表 }
export const getInvoicingManagementListApi = (data: TKeyValue): Res =>
  request.post("/system/invoice/manage/list", data);

// | /system/invoice/manage/billing/operation { 开票操作 }
export const getInvoicingManagementOperationApi = (data: TKeyValue): Res =>
  request.post("/system/invoice/manage/billing/operation", data);
