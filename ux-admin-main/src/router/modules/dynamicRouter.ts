import { useAuth, useUser } from "@/store";
import router from "@/router";
import { HOME_URL, LOGIN_URL } from "@/config";
import { RouteRecordRaw } from "vue-router";

const modules = import.meta.glob("@/views/**/*.vue");

/**
 * @description 初始化动态路由
 */
export const initDynamicRouter = async () => {
  const user = useUser();
  const Auth = useAuth();
  try {
    await user.UserInfo();
    await Auth.getMenuList();
    // return;
    // | 判断权限
    if (!Auth.authMenuList.length) {
      ElNotification({
        title: "无权限访问",
        message: "当前账号无任何菜单权限，请联系系统管理员！",
        type: "warning",
        duration: 3000,
      });
      user.LogOut();
      router.replace(LOGIN_URL);
      return Promise.reject("No permission");
    }
    // | 动态路由
    Auth.flatMenuListGet.forEach((item: IMenuList) => {
      item.children && delete item.children;
      if (item.component && typeof item.component == "string") {
        // 兼容历史菜单数据：火山云 GPU 菜单曾误指向云服务器页面，
        // 即使数据库迁移尚未执行，也必须打开新的目录页面。
        if (item.path === "volcanoGpuResource") {
          item.component = "gpu/volcano/index";
        }
        item.component = modules[
          "/src/views/" + item.component + ".vue"
        ] as unknown as string;
      }
      if (item.meta.isFull) {
        router.addRoute(item as unknown as RouteRecordRaw);
      } else {
        router.addRoute("layout", item as unknown as RouteRecordRaw);
      }
    });
  } catch (err) {
    user.LogOut();
    router.replace(HOME_URL);
    return Promise.reject(err);
  }
};
