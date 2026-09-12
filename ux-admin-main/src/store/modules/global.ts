import { DEFAULT_PRIMARY } from "@/config";
import { defineStore } from "pinia";

export const useGlobalStore = defineStore("Global", {
  state: (): IGlobalState => {
    return {
      drawerVisible: false,
      // 布局模式 (纵向：vertical | 经典：classic | 横向：transverse | 分栏：columns)
      layout: "vertical",
      // element 组件大小
      assemblySize: "default",
      // 当前系统语言
      language: null,
      // 当前页面是否全屏
      maximize: false,
      // 主题颜色
      primary: DEFAULT_PRIMARY,
      // 深色模式
      isDark: false,
      // 灰色模式
      isGrey: false,
      // 色弱模式9
      isWeak: false,
      // 侧边栏反转 (目前仅支持 'vertical' 模式)
      asideInverted: false,
      // 折叠菜单
      isCollapse: false,
      // 面包屑导航
      breadcrumb: true,
      // 面包屑导航图标
      breadcrumbIcon: true,
      // 标签页
      tabs: true,
      // 标签页图标
      tabsIcon: true,
      // 页脚
      footer: true,
    };
  },
  actions: {
    // Set GlobalState
    setGlobalState(...args: ObjToKeyValArray<IGlobalState>) {
      this.$patch({ [args[0]]: args[1] });
    },
  },
  persist: true,
});
