<template>
  <el-switch
    v-model="Global.isDark"
    @change="switchDark"
    inline-prompt
    :active-icon="Sunny"
    :inactive-icon="Moon"
  />
</template>
<script setup lang="ts" name="SwitchDark">
import { useTheme } from "@/hooks/useTheme";
import { useGlobalStore } from "@/store";
import { Sunny, Moon } from "@element-plus/icons-vue";
const Global = useGlobalStore();
const { switchDark } = useTheme();
</script>
