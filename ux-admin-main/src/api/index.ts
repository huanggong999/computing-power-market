import request, { NoLoadingType } from "@/utils/request";

// | /system/config/getConfig/{config} { 获取系统配置 }
export const getConfigApi = (config: TConfigKey): Res<IConfig> =>
  request.get(`/system/config/getConfig/${config}`);

// | /system/config/update/{config} { 修改系统配置 }
export const updateConfigApi = (config: TConfigKey, data: any): Res<IConfig> =>
  request.put(`/system/config/update/${config}`, data);

// | /system/message/page { 分页查询消息通知列表 } // status 1:未读 2:已读
export const getMessagePageApi = (params: TKeyValue): Res =>
  request.get("/system/message/page", {
    params,
    noLoading: true,
  } as NoLoadingType);

// | /system/message/read?id=xxx { 已读一条消息 }
export const readMessageApi = (id: string): Res =>
  request.get(`/system/message/read?id=${id}`);

// | /system/message/readAll { 全部已读 }
export const readAllMessageApi = (): Res =>
  request.get(`/system/message/readAll`);
