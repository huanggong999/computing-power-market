import { HOME_URL, LOGIN_URL, TEST_URL } from "@/config";
import { _RouteRecordBase } from "vue-router";

/**
 * staticRouter (静态路由)
 */
export const staticRouter: any[] = [
  { path: "/", redirect: HOME_URL },
  {
    path: LOGIN_URL,
    name: "Login",
    component: () => import("@/views/Login/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: TEST_URL,
    name: "Test",
    component: () => import("@/views/Test/Test.vue"),
    meta: { title: "测试页面" },
  },
  {
    path: "/layout",
    name: "layout",
    component: () => import("@/layout/index.vue"),
    redirect: HOME_URL,
    children: [],
  },
];

// defaultRouter （默认路由） =
export const defaultRouter = [
  {
    path: "home",
    name: "Home",
    component: "home/index",
    meta: {
      title: "首页",
      icon: "HomeFilled",
      isAffix: true,
      com: "home/index",
    },
  },
];

/**
 * errorRouter (错误页面路由)
 */
export const errorRouter = [
  {
    path: "/403",
    name: "403",
    component: () => import("@/components/ErrorMessage/403.vue"),
    meta: {
      title: "403页面",
    },
  },
  {
    path: "/404",
    name: "404",
    component: () => import("@/components/ErrorMessage/404.vue"),
    meta: {
      title: "404页面",
    },
  },
  {
    path: "/500",
    name: "500",
    component: () => import("@/components/ErrorMessage/500.vue"),
    meta: {
      title: "500页面",
    },
  },
  // Resolve refresh page, route warnings
  {
    path: "/:pathMatch(.*)*",
    component: () => import("@/components/ErrorMessage/404.vue"),
  },
];
