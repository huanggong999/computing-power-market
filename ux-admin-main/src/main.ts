import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

import App from "./App.vue";

import "@/styles/reset.scss";
// CSS common style sheet
import "@/styles/common.scss";
// iconfont css
import "@/assets/iconfont/iconfont.scss";
// element css
import "element-plus/dist/index.css";
// element dark css
import "element-plus/theme-chalk/dark/css-vars.css";
// custom element dark css
import "@/styles/theme/element-dark.scss";
// custom element css
import "@/styles/element.scss";

// | element icons
import * as Icons from "@element-plus/icons-vue";

// | form-create
import FcDesigner from "@form-create/designer";
import FormCreate from "@form-create/element-ui";

import ElementPlus from "element-plus";

import router from "./router";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App);

// | 注册图标
Object.keys(Icons).forEach((key) => {
  app.component(key, Icons[key as keyof typeof Icons]);
});

app.use(router);
app.use(pinia);
app.use(ElementPlus);
app.use(FcDesigner);
app.use(FcDesigner.formCreate);
app.use(FormCreate);

app.mount("#app");
