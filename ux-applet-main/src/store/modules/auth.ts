import { tool } from "@/utils/tool";
import { defineStore } from "pinia";
import api from "@/api";
export const useAuth = defineStore("auth", {
	state: () => ({
		TOKEN: tool.get("TOKEN") || "",
		OPENID: tool.get("OPENID") || "",
		UNIONID: tool.get("UNIONID") || "",
		USER_INFO: tool.get("USER_INFO") || null,
		SCENE: tool.get("SCENE") || null,
	}),
	actions: {
		SET_TOKEN(token: string) {
			this.TOKEN = token;
			tool.set("TOKEN", token);
		},

		CLEAR_TOKEN() {
			this.TOKEN = "";
			tool.remove("TOKEN");
		},
		GET_TOKEN() {
			return this.TOKEN ? this.TOKEN : null;
		},
		SET_OPENID(openid: string) {
			this.OPENID = openid;
			tool.set("OPENID", openid);
		},
		CLEAR_OPENID() {
			this.OPENID = "";
			tool.remove("openid");
		},
		GET_OPENID() {
			return this.OPENID ? this.OPENID : null;
		},
		SET_UNIONID(unionid: string) {
			this.UNIONID = unionid;
			tool.set("UNIONID", unionid);
		},
		CLEAR_UNIONID() {
			this.UNIONID = "";
			tool.remove("UNIONID");
		},
		GET_UNIONID() {
			return this.UNIONID ? this.UNIONID : null;
		},
		SET_USER_INFO(data: any) {
			this.USER_INFO = data;
			tool.set("USER_INFO", data);
		},
		CLEAR_USER_INFO() {
			this.USER_INFO = null;
			tool.remove("USER_INFO");
		},
		SET_SCENE(data: any) {
			this.SCENE = data;
			tool.set("SCENE", data);
		},
		GET_SCENE() {
			return this.SCENE ? this.SCENE : null;
		},
		CLEAR_SCENE() {
			this.SCENE = null;
			tool.remove("SCENE");
		},
		async GetUserInfo() {
			const res: any = await api.user.user.info.get({});
			if (res?.code === 200) {
				this.SET_USER_INFO(res.data);
			}
		},
		logout() {
			this.CLEAR_TOKEN();
			uni.showToast({ title: "退出登录成功", icon: "none" });
			this.CLEAR_USER_INFO();
			uni.$u.sleep(1000).then(() => uni.navigateBack());
		},
		checkLogin(isShow: boolean = true) {
			if (!!this.TOKEN) return true;
			const navigateToLogin = () => {
				uni.navigateTo({ url: "/pages/login/index" });
			};
			if (isShow) {
				uni.showToast({ title: "请先登录", icon: "none", mask: true });
				uni.$u.sleep(2000).then(navigateToLogin);
			}
			navigateToLogin();
			return false;
		},
		bindParentUser(res: any) {
			// if (!res.pid && tool.get("INVITE_USER")) {
			// 	bindParent({ pid: tool.get("INVITE_USER") });
			// }
			// if (tool.get("INVITE_DEVICE")) {
			// 	bindParent({ device_code: tool.get("INVITE_DEVICE") });
			// }
			// tool.remove("INVITE_DEVICE");
			// tool.remove("INVITE_USER");
		},
	},
});
