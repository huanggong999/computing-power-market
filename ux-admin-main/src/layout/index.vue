<template>
  <suspense>
    <template #default>
      <component :is="LayoutComponents[layout]" />
    </template>
    <template #fallback>
      <Loading />
    </template>
  </suspense>
  <ThemeDrawer />
</template>

<script setup lang="ts" name="layoutAsync">
import { useGlobalStore } from "@/store";
import ThemeDrawer from "./components/ThemeDrawer/index.vue";
const LayoutComponents: Record<LayoutType, Component> = {
  vertical: defineAsyncComponent(() => import("./LayoutVertical/index.vue")),
  classic: defineAsyncComponent(() => import("./LayoutClassic/index.vue")),
  transverse: defineAsyncComponent(
    () => import("./LayoutTransverse/index.vue")
  ),
  columns: defineAsyncComponent(() => import("./LayoutColumns/index.vue")),
};
const Global = useGlobalStore();
const layout = computed(() => Global.layout);
</script>

<style lang="scss" scoped></style>
