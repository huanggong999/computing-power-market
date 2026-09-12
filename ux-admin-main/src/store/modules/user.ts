import { getInfoApi } from "@/api/account";
import { getToken, removeToken } from "@/utils/auth";
import { defineStore } from "pinia";

export const useUser = defineStore("user", {
  state: (): IUserStore => ({
    token: getToken() || "",
    userId: 0,
    username: "",
    superAdmin: false,
    roleList: [],
    menuIdList: null,
    permissions: null,
    nickName: "",
    phone: "",
    sex: "MAN",
    avatar: "",
    remark: "",
  }),
  // | 数据持久化  persist: true,

  actions: {
    async UserInfo() {
      const { data } = await getInfoApi();
      this.$patch(data);
    },
    LogOut() {
      return new Promise<void>(async (resolve) => {
        this.token = "";
        this.$reset();
        removeToken();
        resolve();
      });
    },
  },
});
