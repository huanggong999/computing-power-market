import { defineStore } from 'pinia'

interface AgicDetail {
  productId?: number
  editType?: string
  networkCount?: number
  ipCount?: number
  bandWidth?: number
  mobile?: string | number
  remark?: string
  [key: string]: any
}
interface iBind {
  isAccountIpBinding?: number
  isBandwidthDisplay?: number
  isIpDisplay?: number
}
export const useAgicProduct = defineStore('agicDetail', {
  state: (): {
    agic: AgicDetail
    displayOrBind: iBind
  } => ({
    agic: {
      editType: '',
    },
    displayOrBind: {},
  }),
  getters: {},
  actions: {
    // 设置菜单权限列表
    setAgicDetail(detail: any, type: string) {
      this.agic = detail
      this.agic.editType = type
    },
    // 设置显示或绑定
    setDisplayOrBind(detail: iBind) {
      this.displayOrBind = {
        isAccountIpBinding: detail.isAccountIpBinding || 0,
        isIpDisplay: detail.isIpDisplay || 0,
        isBandwidthDisplay: detail.isBandwidthDisplay || 0,
      }
    },
    // 重置
    resetAgicDetail() {
      this.agic = {
        editType: '',
      }
      this.displayOrBind = {}
    },
  },
})
