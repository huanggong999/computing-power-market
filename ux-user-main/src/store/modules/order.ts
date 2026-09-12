import { defineStore } from 'pinia'

export const useOrder = defineStore('orderDetail', {
  state: (): {
    order: any
  } => ({
    order: {},
  }),
  getters: {},
  actions: {
    // 设置菜单权限列表
    setOrderDetail(detail: any) {
      this.order = detail
    },
  },
})
