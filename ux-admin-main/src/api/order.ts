/** * @description: 订单列表 */
import request from "@/utils/request";

// ! 资源订单
// | /system/order/page { 获取订单列表 }
export const orderListApi = (params: any): Res =>
  request.get("/system/order/page", { params });

// | /system/order/product/open { 开通产品 }
export const openProductApi = (orderId: string): Res =>
  request.get(`/system/order/product/open?orderId=${orderId}`);

// ! 服务器工单
// | /system/ecs/work/page { 获取工单列表 }
export const workListApi = (params: any): Res =>
  request.get("/system/ecs/work/page", { params });

// | /system/ecs/work/detail/{id} { 获取工单详情 }
export const workDetailApi = (id: string): Res =>
  request.get(`/system/ecs/work/detail/${id}`);

// | /system/ecs/work/operation { 运维状态操作 }
export const workOperationApi = (data: any): Res =>
  request.post("/system/ecs/work/operation", data);

// | /system/ecs/work/openWork { 开通账号 }
export const openWorkApi = (data: any): Res =>
  request.post("/system/ecs/work/openWork", data);
