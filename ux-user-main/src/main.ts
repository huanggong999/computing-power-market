import { createApp } from 'vue'
import App from './App.vue'
import Language from '@/assets/Language.vue'
// element css
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// element dark css
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/styles/reset.scss'
import '@/styles/common.scss' // custom element css
import '@/styles/element.scss'
// vue i18n
import I18n from './languages'

// 引入form-create组件库
import formCreate from '@form-create/element-ui'
import router from './routes/index'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
// 导入v-md-editor

import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'
// highlightjs
import hljs from 'highlight.js'
VMdPreview.use(githubTheme, {
  Hljs: hljs,
})
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// custom directives
import directives from '@/directives/index'

// | element icons
import * as Icons from '@element-plus/icons-vue'

const app = createApp(App)
// | 注册图标
Object.keys(Icons).forEach((key) => {
  app.component(key, Icons[key as keyof typeof Icons])
})
app.component('IconLanguage', Language)
app.use(router)
app.use(pinia)
app.use(I18n)
app.use(ElementPlus)
app.use(directives)
app.use(VMdPreview)
app.use(formCreate)
app.mount('#app')
