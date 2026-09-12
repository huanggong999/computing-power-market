<!-- 分栏布局 -->
<template>
  <el-container class="layout">
    <div class="aside-split">
      <div class="logo flx-center">
        <img class="logo-img" :src="logo" alt="logo" />
      </div>
      <el-scrollbar>
        <div class="split-list">
          <div
            class="split-item"
            :class="{
              'split-active':
                splitActive === item.path ||
                `/${splitActive.split('/')[1]}` === item.path,
            }"
            v-for="item in menuList"
            :key="item.path"
            @click="changeSubMenu(item)"
          >
            <el-icon>
              <component :is="item.meta.icon" />
            </el-icon>
            <span class="title">{{ item.meta.title }}</span>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <el-aside
      :class="{ 'not-aside': !subMenuList.length }"
      :style="{ width: isCollapse ? '65px' : '210px' }"
    >
      <div class="logo flx-center">
        <span class="logo-text" v-show="subMenuList.length">
          {{ isCollapse ? title[0] : title }}
        </span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :router="false"
          :collapse="isCollapse"
          :collapse-transition="false"
          :unique-opened="true"
        >
          <SubMenu :menuList="subMenuList" />
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container>
      <el-header>
        <ToolBarLeft />
        <ToolBarRight />
      </el-header>
      <Main />
    </el-container>
  </el-container>
</template>

<script setup lang="ts" name="LayoutColumns">
import SubMenu from "@/layout/components/Menu/SubMenu.vue";
import Main from "@/layout/components/Main/index.vue";
import ToolBarLeft from "@/layout/components/Header/ToolBarLeft.vue";
import ToolBarRight from "@/layout/components/Header/ToolBarRight.vue";
import { useAuth, useGlobalStore } from "@/store";

const route = useRoute();
const router = useRouter();
const Global = useGlobalStore();
const Auth = useAuth();

const title = import.meta.env.VITE_GLOB_APP_TITLE;
// 动态导入所有 logo 图片
const logoImg = import.meta.glob("/src/assets/logo/*.png");
const logo = ref("");
// 根据 env 配置 获取对应的 logo 图片
const handleLogo = async () => {
  if (!logoImg[`/src/assets/logo/${import.meta.env.VITE_GLOB_APP_LOGO}.png`])
    return;
  return await logoImg[
    `/src/assets/logo/${import.meta.env.VITE_GLOB_APP_LOGO}.png`
  ]().then((Module: any) => (logo.value = Module.default));
};
handleLogo();

const isCollapse = computed(() => Global.isCollapse);
const menuList = computed(() => Auth.showMenuListGet);
const subMenuList = ref<IMenuList[]>([]);
const activeMenu = computed(
  () => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string
);
const splitActive = ref("");

watch(
  () => [menuList, route],
  () => {
    // 当前菜单没有数据直接 return
    if (!menuList.value.length) return;
    splitActive.value = route.path;
    const menuItem = menuList.value.filter((item: IMenuList) => {
      return (
        route.path === item.path || `/${route.path.split("/")[1]}` === item.path
      );
    });
    if (menuItem[0].children?.length)
      return (subMenuList.value = menuItem[0].children);
    subMenuList.value = [];
  },
  {
    deep: true,
    immediate: true,
  }
);

// change SubMenu
const changeSubMenu = (item: IMenuList) => {
  splitActive.value = item.path;
  if (item.children?.length) return (subMenuList.value = item.children);
  subMenuList.value = [];
  router.push(item.path);
};
</script>
<style lang="scss" scoped>
@use "./index.scss";
</style>
