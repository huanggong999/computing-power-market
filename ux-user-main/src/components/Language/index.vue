<template>
  <el-dropdown trigger="click" @command="changeLanguage">
    <el-icon size="22"><IconLanguage /></el-icon>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in languageList"
          :key="item.value"
          :command="item.value"
          :disabled="language === item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts" name="Language">
import { useI18n } from "vue-i18n";
import { useGlobalStore } from "@/store";
import { LanguageType } from "@/store/modules/interface";

const i18n = useI18n();
const globalStore = useGlobalStore();
const language = computed(() => globalStore.language);

const languageList = [
  { label: "简体中文", value: "zh" },
  { label: "English", value: "en" },
];

const changeLanguage = (lang: string) => {
  nextTick(() => {
    i18n.locale.value = lang;
    globalStore.setGlobalLanguage(lang as LanguageType);
  });
};
</script>
<style lang="scss" scoped></style>
