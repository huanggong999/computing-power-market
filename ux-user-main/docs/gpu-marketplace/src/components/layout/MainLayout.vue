<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <AppHeader />

    <!-- 主体内容 -->
    <div class="main-container">
      <slot />
    </div>

    <!-- 底部 -->
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-dark);
}

.main-container {
  flex: 1;
  padding-top: 64px; /* 顶部导航栏高度 */
  min-height: calc(100vh - 64px);
}
</style>
