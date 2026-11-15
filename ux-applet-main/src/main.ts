import uviewPlus from "uview-plus";
import { createSSRApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import "./utils/time";
import i18n from "@/locale";

import AutoImport from "./auto-import";
export function createApp() {
	const app = createSSRApp(App);
	app.use(i18n);
	app.use(uviewPlus);
	const pinia = createPinia();
	pinia.use(piniaPluginPersistedstate);
	app.use(AutoImport);
	app.use(pinia);
	return {
		app,
	};
}
