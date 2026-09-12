<!-- 纵向布局 -->
<template>
  <el-container class="layout">
    <el-aside>
      <div class="aside-box" :style="{ width: isCollapse ? '65px' : '210px' }">
        <div class="logo flx-center">
          <img class="logo-img" :src="logo" alt="logo" />
          <span class="logo-text" v-show="!isCollapse">{{ title }}</span>
        </div>
        <el-scrollbar>
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapse"
            :router="false"
            :unique-opened="true"
            :collapse-transition="false"
          >
            <SubMenu :menuList="menuList" />
          </el-menu>
        </el-scrollbar>
      </div>
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

<script setup lang="ts" name="LayoutVertical">
import SubMenu from "@/layout/components/Menu/SubMenu.vue";
import Main from "@/layout/components/Main/index.vue";
import { useAuth, useGlobalStore } from "@/store";
import ToolBarLeft from "@/layout/components/Header/ToolBarLeft.vue";
import ToolBarRight from "@/layout/components/Header/ToolBarRight.vue";

const route = useRoute();
const title = import.meta.env.VITE_GLOB_APP_TITLE;

const Auth = useAuth();
const Global = useGlobalStore();
const isCollapse = computed(() => Global.isCollapse);
const menuList = computed(() => Auth.showMenuListGet);
const activeMenu = computed(
  () => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string
);

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
</script>
<style lang="scss" scoped>
@use "./index.scss";
</style>
