<template>
  <Tabs v-if="tabs" />
  <el-main>
    <router-view v-slot="{ Component, route }">
      <transition appear name="fade-transform" mode="out-in">
        <keep-alive :include="keepAliveName" :max="8">
          <component
            :is="Component"
            :key="route.fullPath"
            v-if="isRouterShow"
          />
        </keep-alive>
      </transition>
    </router-view>
  </el-main>
  <el-footer v-if="footer">
    <Footer />
  </el-footer>
</template>

<script setup lang="ts" name="Main">
import Tabs from "../Tabs/index.vue";
import Footer from "../Footer/index.vue";
import { storeToRefs, useGlobalStore, useKeepAlive } from "@/store";
const keepAliveStore = useKeepAlive();
const { keepAliveName } = storeToRefs(keepAliveStore);
const Global = useGlobalStore();
const { tabs, footer, layout } = storeToRefs(Global);

watch(
  () => layout.value,
  () => {
    const body = document.body as HTMLElement;
    body.setAttribute("class", layout.value);
  },
  { immediate: true }
);

// 注入刷新方法
const isRouterShow = ref(true);
const refreshCurrentPage = (val: boolean) => (isRouterShow.value = val);
provide("refresh", refreshCurrentPage);
</script>
<style lang="scss" scoped>
@use "./index.scss";
</style>
