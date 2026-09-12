<!-- 经典布局 -->
<template>
  <el-container class="layout">
    <el-header>
      <div class="header-lf">
        <div class="logo flx-center">
          <!-- 动态 src -->
          <img class="logo-img" :src="logo" alt="logo" />
          <span class="logo-text" v-show="!isCollapse">{{ title }}</span>
        </div>
        <ToolBarLeft />
      </div>
      <div class="header-ri">
        <ToolBarRight />
      </div>
    </el-header>

    <el-container class="classic-content">
      <el-aside>
        <div
          class="aside-box"
          :style="{ width: isCollapse ? '65px' : '210px' }"
        >
          <el-scrollbar>
            <el-menu
              :default-active="activeMenu"
              :router="false"
              :collapse="isCollapse"
              :collapse-transition="false"
              :unique-opened="true"
            >
              <SubMenu :menuList="menuList" />
            </el-menu>
          </el-scrollbar>
        </div>
      </el-aside>
      <el-container class="classic-main"> <Main /> </el-container>
    </el-container>
  </el-container>
</template>

<script setup lang="ts" name="LayoutClassic">
import ToolBarLeft from "@/layout/components/Header/ToolBarLeft.vue";
import ToolBarRight from "@/layout/components/Header/ToolBarRight.vue";
import SubMenu from "@/layout/components/Menu/SubMenu.vue";
import Main from "@/layout/components/Main/index.vue";
import { useAuth, useGlobalStore } from "@/store";

const Global = useGlobalStore();
const Auth = useAuth();
const route = useRoute();
const isCollapse = computed(() => Global.isCollapse);
const menuList = computed(() => Auth.showMenuListGet);
const activeMenu = computed(
  () => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string
);

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
</script>
<style lang="scss" scoped>
@use "./index.scss";
</style>
