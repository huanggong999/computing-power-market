/** * @description: 账号 */

import request from "@/utils/request";

// /system/menu/getRouter { 当前菜单 }
export const getUserAuthApi = (): Res => request.get("/system/menu/getRouter");

// /system/user/info { 当前登陆用户信息 }
export const getInfoApi = (): Res<IUseInfoData> =>
  request.get("/system/user/info");

// | /system/user/page { 账号列表 }
export const getAccountListApi = (params: any): Res<IAccountListData> =>
  request.get("/system/user/page", { params });

// // 账号状态 /sys/user/disable/{id}
// export const getAccountStatusApi = (id: string, disable: boolean): Res<any> =>
//   request.put(`/sys/user/disable/${id}`, { disable });

// | /system/user/ { 删除账号 }
export const delAccountApi = (id: string): Res =>
  request.delete(`/system/user/?id=${id}`);

// | /system/user/ { 账号详情 }
export const getAccountDetailApi = (id: string): Res =>
  request.get(`/system/user/?id=${id}`);

// | /system/user/save { 新增账号 }
export const addAccountApi = (data: any): Res =>
  request.post("/system/user/save", data);

// | /system/user/update { 修改账号 }
export const editAccountApi = (data: any): Res =>
  request.put("/system/user/update", data);
