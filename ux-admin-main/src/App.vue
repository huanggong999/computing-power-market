<template>
  <el-config-provider :locale="locale" :button="buttonConfig">
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">
import zhCn from "element-plus/es/locale/lang/zh-cn";
import { useTheme } from "./hooks/useTheme";
// import { useGlobalStore } from "./store";
import { autoRefresh } from "./utils/autoUpdate";
// const Global = useGlobalStore();
// element assemblySize
// const assemblySize = computed(() => Global.assemblySize);
// element button config
const buttonConfig = reactive({ autoInsertSpace: false });

const locale = computed(() => zhCn);

const { initTheme } = useTheme();
initTheme();

onMounted(() =>
  nextTick(() => {
    if (import.meta.env.VITE_APP_ENV !== "development") {
      autoRefresh();
    }
  })
);
</script>

<style lang="scss" scoped></style>
