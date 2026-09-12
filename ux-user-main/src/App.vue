<script setup lang="ts">
import en from 'element-plus/es/locale/lang/en'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { useGlobalStore } from './store'
import { getBrowserLang } from './utils'
import { LanguageType } from './store/modules/interface'
import { useI18n } from 'vue-i18n'

// 修改 element-plus 主题色
document.documentElement.style.setProperty('--el-color-primary', '#3B75FE')

const globalStore = useGlobalStore()

const i18n = useI18n()

onMounted(() => {
  const language = globalStore.language ?? getBrowserLang()
  i18n.locale.value = language
  globalStore.setGlobalLanguage(language as LanguageType)
})

const locale = computed(() => {
  if (globalStore.language == 'zh') return zhCn
  if (globalStore.language == 'en') return en
  return getBrowserLang() == 'zh' ? zhCn : en
})
</script>

<template>
  <el-config-provider :locale="locale">
    <router-view :key="$route.fullPath" />
  </el-config-provider>
</template>

<style>
body {
  background: #fff;
}

/* 避免浏览器翻译插件注入的悬浮层遮挡业务弹框 */
.imt-fb-container,
.imt-fb-btn,
.imt-fb-wrapper,
[class^="imt-"],
[class*=" imt-"] {
  display: none !important;
  pointer-events: none !important;
}
.el-message {
  z-index: 3000 !important;
}
.el-dialog{
  z-index: 2000 !important;
}
/* .el-overlay {
  z-index: 4000 !important;
}

.el-dialog {
  z-index: 4100 !important;
} */
.el-table__inner-wrapper {
  z-index: 1 !important;
}

.el-table--border {
  z-index: 1 !important;
}
</style>
