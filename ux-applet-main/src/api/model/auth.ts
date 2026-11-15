import http from "@/utils/request";
export default {
	info: {
		url: `/app/game/bindDevice`,
		name: "绑定设备",
		async get(data = {}) {
			return await http.get(this.url, data);
		},
	},
	getAuthIds: {
		url: `/applets/login/test/bindOpenId`,
		name: "获取用户鉴权ID",
		async get(data = {}) {
			return await http.get(this.url, data);
		},
	},
};
// | /pc/customer/getEidTokenApi {获取EidToken}
export const getEidTokenApi = (data: any) =>
	http.post(`/pc/customer/getEidToken`, data);
// | 申请实名认证 600s有效期
export const applyRealNameVerifyApi = (data: any) =>
	http.post(`/pc/customer/realNameVerify`, data);
// | 轮询实名认证情况
export const getEidResultApi = () => http.get(`/pc/customer/getEidResult`);
// | /pc/customer/getEidTokenApi {获取EidToken}
export const getVerifyLogApi = () =>
	http.get(`/pc/customer/realNameVerify/log`);
