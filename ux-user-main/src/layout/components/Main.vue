<template>
  <h2 class="mt20 ml20" style="z-index: 3">{{ title }}</h2>
  <el-main>
    <Transition appear name="fade-transform" mode="out-in">
      <router-view :key="route.fullPath" />
    </Transition>
  </el-main>
</template>

<script setup lang="ts" name="Main">
const body = document.body as HTMLElement;
body.setAttribute("class", "classic");
const route = useRoute();
const title = computed(() => route.meta.title);
</script>
<style lang="scss" scoped></style>
