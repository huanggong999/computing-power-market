import http from "@/utils/request";

// | /pc/bill/overviewPage  { 账单总览列表 }
export const billOverviewPage = (params: TKeyValue = {}) =>
	http.get(`/pc/bill/overviewPage`, params);

// | /pc/bill/page  { 资源账单列表 }
export const billPage = (params: TKeyValue = {}) =>
	http.get(`/pc/bill/page`, params);
