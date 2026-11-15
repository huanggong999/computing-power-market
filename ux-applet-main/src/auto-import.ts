import type { App } from "vue";
import { buildIcon } from "@/utils/icon";
import api from "@/api";
import appMixin from "@/mixins/appMixin";
export default {
	install(app: App) {
		// 统一注册配置文件
		app.config.globalProperties.$ICON = buildIcon;
		app.config.globalProperties.$API = api;
		app.use(appMixin);
	},
};
