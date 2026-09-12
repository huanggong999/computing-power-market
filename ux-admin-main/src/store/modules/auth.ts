import {
  addPath,
  getAllBreadcrumbList,
  getFlatMenuList,
  getKeepAliveRouterName,
  getShowMenuList,
} from "@/utils";
import { defineStore } from "pinia";

import { defaultRouter } from "@/router/modules/staticRouter";
import { getUserAuthApi } from "@/api/account";

export const useAuth = defineStore("auth", {
  state: (): { authMenuList: IMenuList[] } => ({ authMenuList: [] }),
  // | 数据持久化  persist: true,
  getters: {
    // 未处理
    authMenuListGet: (state) => state.authMenuList,
    // 左侧菜单栏渲染，需要剔除 meta.hideMenu == true
    showMenuListGet: (state) => getShowMenuList(state.authMenuList),
    // 菜单权限列表 ==> 扁平化之后的一维数组菜单，主要用来添加动态路由
    flatMenuListGet: (state) => getFlatMenuList(state.authMenuList),
    // 递归处理后的所有面包屑导航列表
    breadcrumbListGet: (state) => getAllBreadcrumbList(state.authMenuList),
    // keepAlive 路由列表
    keepAliveNameGet: (state) => getKeepAliveRouterName(state.authMenuList),
  },
  actions: {
    async getMenuList() {
      let { data } = await getUserAuthApi();
      this.authMenuList = addPath([...defaultRouter, ...data]);
    },
  },
});
