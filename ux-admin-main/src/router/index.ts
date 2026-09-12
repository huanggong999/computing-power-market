import { LOGIN_URL, ROUTER_WHITE_LIST } from "@/config";
import NProgress from "@/config/nprogress";
import { useAuth } from "@/store";
import { getToken } from "@/utils/auth";
import { createRouter, createWebHashHistory } from "vue-router";
import { initDynamicRouter } from "./modules/dynamicRouter";

import { errorRouter, staticRouter } from "./modules/staticRouter";

/**
 * @description 📚 路由参数配置简介
 * @param path ==> 菜单路径
 * @param name ==> 菜单别名
 * @param redirect ==> 重定向地址
 * @param component ==> 视图文件路径
 * @param meta ==> 菜单信息
 * @param meta.hideMenu ==> 是否隐藏
 * @param meta.icon ==> 菜单图标
 * @param meta.title ==> 菜单标题
 * @param meta.activeMenu ==> 当前路由为详情页时，需要高亮的菜单
 * @param meta.isLink ==> 是否外链
 * @param meta.isFull ==> 是否全屏(示例：数据大屏页面)
 * @param meta.isAffix ==> 是否固定在 tabs nav
 * @param meta.isKeepAlive ==> 是否缓存
 * */

const router = createRouter({
  history: createWebHashHistory(),
  routes: [...errorRouter, ...staticRouter],
  scrollBehavior: () => ({ left: 0, top: 0 }),
});
router.beforeEach(async (to, from, next) => {
  const Token = getToken();
  const Auth = useAuth();
  NProgress.start();
  const title = import.meta.env.VITE_GLOB_APP_TITLE;
  document.title = to.meta.title ? `${to.meta.title} - ${title}` : title;
  // 3.判断是访问登陆页，有 Token 就在当前页面，没有 Token 重置路由到登陆页
  if (to.path.toLocaleLowerCase() === LOGIN_URL) {
    if (Token) return next(from.fullPath);
    return next();
  }
  if (ROUTER_WHITE_LIST.includes(to.path)) return next();
  if (!Token) return next(LOGIN_URL);
  if (Auth.authMenuListGet && Auth.authMenuListGet.length === 0) {
    await initDynamicRouter();
    return next({ ...to, replace: true });
  }
  return next();
});

/**
 * @description 重置路由
 * */
export const resetRouter = () => {
  const authStore = useAuth();
  authStore.flatMenuListGet.forEach((route: { name: string }) => {
    const { name } = route;
    if (name && router.hasRoute(name)) router.removeRoute(name);
  });
};
/**
 * @description 路由跳转错误
 * */
router.onError((error) => {
  NProgress.done();
  console.warn("路由错误", error.message);
});
/**
 * @description 路由跳转结束
 * */
router.afterEach(() => {
  NProgress.done();
});

export default router;
