/* 文档 */
import request from "@/utils/request";

// ! 文档分类

//#region

// | /system/document-type/page { 分页列表 }
export const documentTypePageApi = (params: any): Res<any> =>
  request.get("/system/document-type/page", { params });

// | /system/document-type/type-list { 文档类型列表 }
export const documentTypeTypeListApi = (): Res<any> =>
  request.get("/system/document-type/type-list");

// | /system/document-type/save { 新增 }
export const documentTypeSaveApi = (data: any): Res<any> =>
  request.post("/system/document-type/save", data);

// | /system/document-type/update { 编辑 }
export const documentTypeUpdateApi = (data: any): Res<any> =>
  request.post("/system/document-type/update", data);

// | /system/document-type/delete { 删除 }
export const documentTypeDeleteApi = (id: string): Res<any> =>
  request.delete(`/system/document-type/delete/${id}`);

//#endregion

// ! 文档
//#region

// | /system/document/page { 分页列表 }
export const documentPageApi = (params: any): Res<any> =>
  request.get("/system/document/page", { params });

// | /system/document/{id} { 详情 }
export const documentDetailApi = (id: string): Res<any> =>
  request.get(`/system/document/${id}`);

// | /system/document/save { 新增 }
export const documentSaveApi = (data: any): Res<any> =>
  request.post("/system/document/save", data);

// | /system/document/update { 编辑 }
export const documentUpdateApi = (data: any): Res<any> =>
  request.post("/system/document/update", data);
// | /system/document/delete { 删除 }
export const documentDeleteApi = (id: string): Res<any> =>
  request.get(`/system/document/delete?id=${id}`);

//#endregion
