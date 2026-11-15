import http from "@/utils/request";

// | /pc/news/page { 获取新闻列表 }
export const getNewsListApi = (params: any): any =>
	http.get("/pc/news/page", params);

// | /pc/news/{id} { 获取新闻详情 }
export const getNewsDetailApi = (id: string): any => http.get(`/pc/news/${id}`);
