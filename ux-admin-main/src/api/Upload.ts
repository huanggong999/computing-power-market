/** * @description: 上传 */
import request from "@/utils/request";

// request.post("file/upload", data, {
export const UploadFn = (data: FormData): Res<TKeyValue> =>
  request.post("/file/upload", data, {
    headers: {
      ["Content-Type"]: "multipart/form-data",
    },
  });
export enum UploadMessage {
  Success = "上传成功！",
  Error = "上传失败，请您重新上传！",
  FileSize = "太大了，请您重新上传！",
  Type = "不符合所需的格式，请您重新上传！",
  Exceed = "当前选择太多了，请移除后上传！",
}

export type TUploadMessage = keyof typeof UploadMessage;

export interface IUploadData {
  name: string;
  url: string;
}

export type FileType = "voice" | "video" | "file";

export const goodsStockExport = (params: any) =>
  request.get("system/goodsStock/exportExcel", {
    params,
    responseType: "blob",
  });
