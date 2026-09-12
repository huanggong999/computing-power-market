<!-- 横向布局 -->
<template>
  <el-container class="layout">
    <el-header>
      <div class="logo flx-center">
        <img class="logo-img" :src="logo" alt="logo" />
        <span class="logo-text">{{ title }}</span>
      </div>
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        :router="false"
        :unique-opened="true"
      >
        <!-- 不能直接使用 SubMenu 组件，无法触发 el-menu 隐藏省略功能 -->
        <template v-for="subItem in menuList" :key="subItem.path">
          <el-sub-menu
            v-if="subItem.children?.length"
            :index="subItem.path + 'el-sub-menu'"
            :key="subItem.path"
          >
            <template #title>
              <el-icon>
                <component :is="subItem.meta.icon" />
              </el-icon>
              <span>{{ subItem.meta.title }}</span>
            </template>
            <SubMenu :menuList="subItem.children" />
          </el-sub-menu>
          <el-menu-item
            v-else
            :index="subItem.path"
            :key="subItem.path + 'el-menu-item'"
            @click="handleClickMenu(subItem)"
          >
            <el-icon>
              <component :is="subItem.meta.icon" />
            </el-icon>
            <template #title>
              <span>{{ subItem.meta.title }}</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
      <ToolBarRight />
    </el-header>
    <Main />
  </el-container>
</template>

<script setup lang="ts" name="LayoutTransverse">
import SubMenu from "@/layout/components/Menu/SubMenu.vue";
import Main from "@/layout/components/Main/index.vue";
import ToolBarRight from "@/layout/components/Header/ToolBarRight.vue";
import { useAuth } from "@/store";

const Auth = useAuth();

const router = useRouter();
const route = useRoute();
const title = import.meta.env.VITE_GLOB_APP_TITLE;
const menuList = computed(() => Auth.showMenuListGet);
const activeMenu = computed(
  () => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string
);
const handleClickMenu = (subItem: IMenuList) => {
  router.push(subItem.path);
};

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
