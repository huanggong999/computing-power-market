import { defineStore } from 'pinia'

export const useProduction = defineStore('networkProduct', {
  state: (): {
    production: any
  } => ({
    production: {},
  }),
  getters: {},
  actions: {
    // 设置菜单权限列表
    setProductionDetail(detail: any) {
      this.production = detail
    },
    // 清除产品详情
    clearProductionDetail() {
      this.production = {}
    },
    // 设置名字
    setProductionName(name: string) {
      this.production.name = name
    },
  },
})
