import http from "@/utils/request";

// | /pc/active/details/list { 获取活动列表 }
export const getActiveDetailsListApi = (): any =>
	http.get(`/pc/active/details/list`);

// | pc/active/details/ { 获取活动详情 }
export const getActiveDetailsApi = (id: string): any =>
	http.get(`/pc/active/details/${id}`);
