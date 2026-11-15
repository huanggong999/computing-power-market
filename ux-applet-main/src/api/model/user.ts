import http from "@/utils/request";
export default {
	user: {
		info: {
			url: `/pc/customer/info`,
			name: "获取用户信息",
			async get(data = {}) {
				return await http.get(this.url, data);
			},
		},
	},
	login: {
		url: `/login`,
		name: "登录",
		async post(data = {}, type = "USERNAME_PASSWORD_LOGIN") {
			return await http.post(this.url, data, {
				header: { source: type },
			});
		},
	},
	unionIdLogin: {
		url: `/login`,
		name: "登录",
		async post(data = {}, type = "WECHAT_LOGIN") {
			return await http.post(this.url, data, {
				header: { source: type },
			});
		},
	},
	getAuthCode: {
		url: `/code`,
		name: "获取验证码",
		async get(params: any = {}) {
			const query = new URLSearchParams();
			if (params.type !== undefined) query.append("type", params.type);
			if (params.tmsg !== undefined) query.append("tmsg", params.tmsg);
			if (params.phone !== undefined) query.append("phone", params.phone);
			const qs = query.toString();
			return await http.get(this.url + (qs ? "?" + qs : ""));
		},
	},
	bindPhone: {
		url: `/applets/login/bindPhone`,
		name: "openid绑定手机",
		async post(data = {}) {
			return await http.get(this.url, data);
		},
	},
};
// | /pc/customer/register  { 注册 }
export const registerAPI = (data: any) =>
	http.put("/pc/customer/register", data);

// | /pc/customer/updatePassword  { 重置密码 }  resetPasswordAPI
export const resetPasswordAPI = (data: any) =>
	http.put("/pc/customer/resetPasswordByPhone", data);

// | /pc/customer/register  { 获取unionID }
export const getUnionId = (data: any) =>
	http.get("/applets/login/getUnionId", data);
// | /pc/extend/relevance { 绑定推广大使 }
export const bindExtendRelevanceApi = (data: any) =>
	http.get(`/pc/extend/relevance?code=${data}`);
