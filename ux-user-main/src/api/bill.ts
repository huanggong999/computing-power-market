// ! 账单
import request from "@/utils/request";

// | /pc/bill/overviewPage  获取账单总览列表
export const getBillOverviewPageApi = (params: any): Res =>
  request.get("/pc/bill/overviewPage", { params });

// | /pc/bill/page  获取账单列表
export const getBillPageApi = (params: any): Res =>
  request.get("/pc/bill/page", { params });
