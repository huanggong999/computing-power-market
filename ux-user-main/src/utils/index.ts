import router from '@/routes'
import ClipboardJS from 'clipboard'
/**
 * @description 判断数据类型
 * @param {*} val 需要判断类型的数据
 * @returns {String}
 */
export const isType = (val: any) => {
  if (val === null) return 'null'
  if (typeof val !== 'object') return typeof val
  else
    return Object.prototype.toString.call(val).slice(8, -1).toLocaleLowerCase()
}

/**
 * @description 浏览器自带API 生成UUID
 * @returns {String}
 */
// export const generateUUID = () => window.crypto.randomUUID();
export const generateUUID = () => {
  let uuid = ''
  for (let i = 0; i < 32; i++) {
    let random = (Math.random() * 16) | 0
    if (i === 8 || i === 12 || i === 16 || i === 20) uuid += '-'
    uuid += (i === 12 ? 4 : i === 16 ? (random & 3) | 8 : random).toString(16)
  }
  return uuid
}
// 随机从生成实例名
export const generateRandomText = (length = 30) => {
  // 定义可用的字符集
  const charset =
    'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-.'

  // 确保文本长度不超过最大限制
  const maxLength = Math.min(length, 30)

  // 第一个字符只能是字母
  const firstChar = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'

  // 生成文本的第一个字符（字母）
  let result = firstChar.charAt(Math.floor(Math.random() * firstChar.length))

  // 随后字符可以是字母、数字、下划线、中划线、点
  for (let i = 1; i < maxLength; i++) {
    result += charset.charAt(Math.floor(Math.random() * charset.length))
  }

  return result
}

/**
 * @description 获取浏览器默认语言
 * @returns {String}
 */
export function getBrowserLang() {
  let browserLang = navigator.language
  // let browserLang = navigator.language
  //   ? navigator.language
  //   : navigator?.browserLanguage;
  let defaultBrowserLang = ''
  if (['cn', 'zh', 'zh-cn'].includes(browserLang.toLowerCase())) {
    defaultBrowserLang = 'zh'
  } else {
    defaultBrowserLang = 'en'
  }
  return defaultBrowserLang
}

/**
 * @description 使用递归扁平化菜单，方便添加动态路由
 * @param {Array} menuList 菜单列表
 * @returns {Array}
 */
export const getFlatMenuList = (menuList: IMenuList[]): IMenuList[] => {
  let newMenuList: IMenuList[] = JSON.parse(JSON.stringify(menuList))
  return newMenuList.flatMap((item) => [
    item,
    ...(item.children ? getFlatMenuList(item.children) : []),
  ])
}
/**
 * @description 使用递归过滤出需要渲染在左侧菜单的列表 (需剔除 meta.hideMenu == true 的菜单)
 *   */
export const getShowMenuList = (menuList: IMenuList[]) => {
  let newMenuList: IMenuList[] = JSON.parse(JSON.stringify(menuList))
  return newMenuList.filter((item: IMenuList) => {
    item.children?.length && (item.children = getShowMenuList(item.children))
    return !item.meta.hideMenu
  })
}
/**
 * @description 路由跳转
 * @param url 路由地址
 */
export const toPage = (url: string) => router.push(url)
export const newPage = (url: string) => {
  window.open(url, '_blank')
}
/**
 * @description 文本复制至剪贴板
 * @param text 文本
 */

export const copyText = (text: string) => {
  // 创建一个临时的按钮
  const btn = document.createElement('button')
  btn.setAttribute('data-clipboard-text', text)
  document.body.appendChild(btn)

  // 初始化 ClipboardJS
  const clipboard = new ClipboardJS(btn)

  clipboard.on('success', function (e) {
    ElMessage.success('内容已复制！')
    document.body.removeChild(btn) // 删除临时按钮
  })

  clipboard.on('error', function (e) {
    ElMessage.error('内容复制失败！')
    document.body.removeChild(btn) // 删除临时按钮
  })

  // 触发复制
  btn.click()
}
