//引入配置文件
import { createI18n } from "vue-i18n"; //引入vue-1i8n
import en from "./en.json"; //引入英文配置文件
import zhHans from "./zh-Hans.json"; //引入中文简体配置文件

//创建配置
const i18n = createI18n({
	locale: uni.getLocale(), // 获取已设置的语言
	globalInjection: true,
	legacy: false,
	messages: {
		en,
		"zh-Hans": zhHans,
	},
});
//导出配置
export default i18n;
