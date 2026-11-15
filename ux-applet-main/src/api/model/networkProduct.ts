// ! 网络产品
import http from "@/utils/request";

export const getNetWorkProductListAPI = (params: any) =>
	http.get("/pc/network-product/page", params);
export const getNetWorkProductDetailAPI = (id: any) =>
	http.get(`/pc/network-product/${id}`);

export const getNetWorkProductFormAPI = (id: any) =>
	http.get(`/pc/network-product/form/${id}`);

export const getNetWorkPayFormApi = (id: any) =>
	http.get(`/pc/network-product/pay-form/${id}`);
export const submitNetWorkFormApi = (data: any) =>
	http.post("/pc/network-product/form/add", data);
export const submitNetWorkPayFormApi = (data: any) =>
	http.post("/pc/network-product/form/addPay", data);

// 订单管理
// 获取咨询订单列表
export const getAIGCFormListApi = (params?: any) =>
	http.get("/pc/network-product/payValue/page", params);
// 获取订单
export const getOrderListAPI = (params: any) =>
	http.get("/pc/order/page", params);

// 构建AGIC订单信息
export const buildAgicOrderInfoApi = (data: any) =>
	http.post("/pc/order/buildAGOrderInfo", data);
// 创建AGIC订单信息
export const createAgicOrderApi = (data: any) =>
	http.post("/pc/order/createAGOrder", data);

// 查询AGIC订单信息
export const getAgicOrderInfoApi = (id: any) =>
	http.get("/pc/order/getProductDetail", { networkValueId: id });
// 修改自动续费状态
export const updateAutoRenewStatusApi = (data: any) =>
	http.post("/pc/order/updateAgiIsAutoRenew", data);

// 停线
export const stopLineApi = (id: any) =>
	http.get("/pc/network-product/stop", { valueId: id });
// 停线
export const cancelLineApi = (id: any) =>
	http.get("/pc/network-product/cancel", { valueId: id });
// 上线
export const upLineApi = (id: any) =>
	http.get("/pc/network-product/start", { valueId: id });
// 编辑产品名称
export const editProductNameApi = (data: any) =>
	http.post("/pc/order/updateAgiCustomerName", data);

// 重置密码
export const agiResetPasswordApi = (email: string) =>
	http.get("/pc/network-product/reset/password", {
		email,
	});
// 判断邮箱是否在飞连存在
export const checkEmailExistApi = (data: any) =>
	http.post("/pc/order/isEmailExist", data);

// 判断ip数是否足够
export const checkIpNumApi = (data: any) =>
	http.post("/pc/network-product/ip/enough", data);
