/** * @description: 运营管理 */

import request from "@/utils/request";

// !  分享活动

// | /system/active/center/page  { 分页列表 }
export const activeCenterPageApi = (params: any): Res =>
  request.get("/system/active/center/page", { params });

// | /system/active/center/save { 新增活动 }
export const activeCenterSaveApi = (data: any): Res =>
  request.post("/system/active/center/save", data);

// | /system/active/center/getActiveDetails/{id} { 获取活动详情 }
export const activeCenterGetActiveDetailsApi = (id: string): Res =>
  request.get(`/system/active/center/getActiveDetails/${id}`);

// | /system/active/center/update { 编辑活动 }
export const activeCenterUpdateApi = (data: any): Res =>
  request.put("/system/active/center/update", data);

// | /system/active/center/getActiveRecord { 活动记录 }
export const activeCenterGetActiveRecordApi = (params: any): Res =>
  request.get("/system/active/center/getActiveRecord", { params });

// | /system/active/center/delete { 删除活动  }
export const activeCenterDeleteApi = (id: string): Res =>
  request.get(`/system/active/center/delete?id=${id}`);

// ! 充值活动

// | /system/recharge/page  { 分页列表 }
export const rechargePageApi = (params: any): Res =>
  request.get("/system/recharge/page", { params });

// | /system/recharge/save { 新增充值活动 }
export const rechargeSaveApi = (data: any): Res =>
  request.post("/system/recharge/save", data);

// | /system/recharge/update { 编辑充值活动 }
export const rechargeUpdateApi = (data: any): Res =>
  request.post("/system/recharge/update", data);

// | /system/recharge/{id} { 获取充值活动详情 }
export const rechargeGetDetailsApi = (id: string): Res =>
  request.get(`/system/recharge/${id}`);

// | /system/recharge/delete { 删除充值活动 }
export const rechargeDeleteApi = (id: string): Res =>
  request.get(`/system/recharge/delete?id=${id}`);

// ! 新闻动态
// | /system/news/page  { 分页列表 }
export const newsPageApi = (params: any): Res =>
  request.get("/system/news/page", { params });
// | /system/news/save { 新增新闻动态 }
export const newsSaveApi = (data: any): Res =>
  request.post("/system/news/save", data);

// | /system/news/update { 编辑新闻动态 }
export const newsUpdateApi = (data: any): Res =>
  request.post("/system/news/update", data);

// | /system/news/delete {id} { 删除新闻动态 }
export const newsDeleteApi = (id: string): Res =>
  request.get(`/system/news/delete?id=${id}`);

// | /system/news/{id} { 获取新闻动态详情 }
export const newsGetDetailsApi = (id: string): Res =>
  request.get(`/system/news/${id}`);
