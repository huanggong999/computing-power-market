import type { App } from "vue";
import { pages } from "@/pages.json";
import api from "@/api";
export default {
	install(app: App) {
		app.mixin({
			data() {
				return {
					isMicromessenger: true,
				};
			},
			onLoad(option: TKeyValue) {
				// #ifdef H5
				const ua = window.navigator.userAgent.toLowerCase();
				this.isMicromessenger = ua.indexOf("micromessenger") > -1;
				// #endif
			},
			onShow() {
				if (this.isMicromessenger) {
					this.postSystemLog({
						type: "page",
					});
				}
			},
			methods: {
				// 埋点
				async postSystemLog({ type }: TKeyValue) {
					if (type === "page") {
						let routes = getCurrentPages();
						let curRoute = routes[routes.length - 1]
							? routes[routes.length - 1].route
							: "";
						if (curRoute && pages) {
							// console.log(curRoute, pages);
							// const targetPage = pages.find(
							// 	(item) => item.path === curRoute
							// );
							// const noLog = [
							// 	...appConfig.NO_TOKEN_URL_ARRAY,
							// 	...appConfig.OTHER_URL_ARRAY,
							// ];
							// if (noLog.indexOf(curRoute) === -1) {
							// 	api.system.log.post({
							// 		remarks: targetPage.style.title,
							// 	});
							// }
						}
					}
				},
			},
		});
	},
};
