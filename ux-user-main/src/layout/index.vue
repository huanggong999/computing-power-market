<template>
  <el-container class="layout">
    <el-header>
      <TopBar />
    </el-header>
    <img
      class="img"
      src="@/assets/images/header-bg.png"
      alt=""
      style="z-index: 0"
    />

    <el-container class="classic-content">
      <el-aside>
        <div class="aside-box">
          <el-scrollbar>
            <el-menu
              class="el-menu-demo"
              :default-active="activeMenu"
              :collapse-transition="false"
              :unique-opened="true"
            >
              <SubMenu :menuList="menuList" />
            </el-menu>
          </el-scrollbar>
        </div>
      </el-aside>
      <el-container class="classic-main">
        <Main />
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup lang="ts" name="Layout">
import { useAuth } from '@/store'
import SubMenu from './components/SubMenu.vue'
import Main from './components/Main.vue'
const Auth = useAuth()
const menuList = computed(() => Auth.showMenuListGet)

const route = useRoute()
const activeMenu = computed(
  () => (route.meta.activeMenu ? route.meta.activeMenu : route.path) as string
)
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
