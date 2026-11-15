import http from "@/utils/request";

// | /pc/carousel/appletList  { 获取小程序轮播图列表 }
export const getCarouselListApi = (): any =>
	http.get("/pc/carousel/appletList");
