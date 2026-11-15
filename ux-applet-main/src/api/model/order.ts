import http from "@/utils/request";

// 构建订单信息
export const buildOrderInfoApi = (data: any) =>
	http.post("/pc/order/buildOrderInfo", data);
// 创建订单信息
export const createOrderApi = (data: any) =>
	http.post("/pc/order/createOrder", data);
// 获取订单信息
export const getOrderInfoApi = (data: any) =>
	http.post("/pc/order/getOrderInfo", data);
