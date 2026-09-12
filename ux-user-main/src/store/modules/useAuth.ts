import { getFlatMenuList, getShowMenuList } from '@/utils'
import { defineStore } from 'pinia'

import menuList from '@/assets/json/authMenuList.json'
import { getExtendInfoApi } from '@/api/partner'

export const useAuth = defineStore('auth', {
  state: (): {
    authMenuList: any[]
  } => ({
    authMenuList: [],
  }),
  getters: {
    // 左侧菜单栏渲染，需要剔除 meta.hideMenu == true
    showMenuListGet: (state) => getShowMenuList(state.authMenuList),
    // 菜单权限列表 ==> 扁平化之后的一维数组菜单，主要用来添加动态路由
    flatMenuListGet: (state) => getFlatMenuList(state.authMenuList),
  },
  actions: {
    // 设置菜单权限列表
    async setAuthMenuList() {
      console.log('setAuthMenuList')
      let copyMenuList = JSON.parse(JSON.stringify(menuList))
      // 如果不是推广大使要删去推广管理
      await getExtendInfoApi().then((res) => {
        if (res.data.status && res.data.status == 2) {
          console.log('是')
        } else {
          copyMenuList.pop()
          console.log('不是')
        }
      })

      this.authMenuList = copyMenuList
    },
  },
})
