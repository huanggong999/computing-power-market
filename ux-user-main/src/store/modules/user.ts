import { getUserInfoAPI } from '@/api/user'
import { HOME_URL } from '@/config'
import router from '@/routes'
import { getOpenId, getToken, getUnionId, removeToken } from '@/utils/auth'
import { defineStore } from 'pinia'

export const useUserInfo = defineStore('userInfo', {
  state: (): TUserInfoStore => {
    return {
      userId: null,
      username: '',
      superAdmin: false,
      customerName: '',
      phone: '',
      balance: 0,
      avatar: '',
      gender: 'MAN',
      inviterId: null,
      voucherBalance: 0,
      remark: '',
      token: getToken() || '',
      arrearsAmount: 0,
      couponNum: 0,
      totalBalance: 0,
      amount: 0,
      type: 1,
      realName
: null,
      idCard: null,
      companyName: null,
      companyCode: null,
      companyAddress: null,
      companyImg: null,
      companyContactName: null,
      companyContactPhone: null,
      certStatus: 1,
      certType: 2,
      companyVerifyRemark: null,
      companyVerifyTime: null,
      registerTime: '',
      creditAmount: 0,
      unionId: getUnionId() || '',
      openId: getOpenId() || '',
    }
  },
  getters: {
    IsUserLogin(): boolean {
      return !!this.token
    },
  },
  actions: {
    async getUserInfo() {
      const { data } = await getUserInfoAPI()
      this.$state = data
    },

    LogOut() {
      return new Promise<void>(async (resolve) => {
        // await getLogoutAPI()
        // this.token = "";
        removeToken()
        router.replace(HOME_URL)
        ElMessage.success('退出成功')
        this.$reset()
        resolve()
      })
    },
  },

  persist: true,
})
