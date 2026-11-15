import http from "@/utils/request";

export const uploadFileApi = (
	data: any,
	config: TKeyValue = { name: "file" }
): any => http.upload(`/file/upload`, data, config);

// 企业营业执照识别
export const uploadOcrApi = (
	data: any,
	config: TKeyValue = { name: "file" }
): any => http.upload(`/file/uploadocr`, data, config);
// 身份证识别
export const uploadIdCardApi = (
	data: any,
	config: TKeyValue = { name: "file" }
): any => http.upload(`/file/uploadIdCardOcr`, data, config);
