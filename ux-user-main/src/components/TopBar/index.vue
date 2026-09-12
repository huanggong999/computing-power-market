<template>
  <div class="top-bar">
    <div class="top-bar-box flx-align-center">
      <div class="bar-icon">
        <img
          style="width: 154px; height: 42px"
          src="../../assets/icon/ai.png"
          @click="router.push('/home')"
        />
      </div>
      <div class="bar-menu top-bar-li menu">
        <div
          v-for="(item, index) in menuList"
          :key="index"
          :class="['menu-li', 'flx-center', { active: isActive === index + 1 }]"
          @click="handleClick(item.link, index)"
        >
          {{ item.title }}
          <div class="bar-icon hot" v-if="item.tab == 'hot'">HOT</div>
          <div class="bar-icon new" v-if="item.tab == 'new'">NEW</div>
        </div>
      </div>

      <div class="bar-button top-bar-li flx-center">
        <div class="gift-btn">
          <div class="icon flx-center" @click="handleRecharge">
            <img src="../../assets/images/gift-icon.png" alt="" />
            <div class="btn flx-center">充值有礼</div>
          </div>
        </div>
        <el-button v-if="!Token" @click="handleRegister">开启AI路程</el-button>
        <el-button v-else @click="router.push('/console')">控制台</el-button>
        <div class="userInfo flx-align-center" v-if="Token">
          <el-dropdown trigger="click">
            <div class="flx-align-center">
              <img
                class="avatar"
                :src="
                  userInfo.$state.avatar
                    ? userInfo.$state.avatar
                    : '../../assets/images/sss.jpg'
                "
                alt=""
              />
              <div class="name">{{ userInfo.$state.customerName }}</div>
              <img src="../../assets/icon/down-icon.png" />
            </div>
            <template #dropdown>
              <div class="dropdown">
                <div class="info">
                  <div class="left">
                    <img
                      class="avatar"
                      :src="
                        userInfo.$state.avatar
                          ? userInfo.$state.avatar
                          : '../../assets/images/sss.jpg'
                      "
                      alt=""
                    />
                  </div>
                  <div class="right">
                    <div class="r-top">{{ userInfo.$state.customerName }}</div>
                    <div class="r-bottom">
                      <div
                        class="flag not-authentication"
                        v-if="userInfo.certStatus == 1"
                        @click="toAuth"
                      >
                        <img
                          src="../../assets/icon/not-authentication-icon.png"
                        />
                        未认证 >
                      </div>
                      <div
                        class="flag not-authentication"
                        v-if="userInfo.certStatus == 2"
                        @click="toAuth"
                      >
                        <img src="../../assets/icon/authenticating-icon.png" />
                        认证中 >
                      </div>
                      <div
                        class="flag not-authentication"
                        v-if="userInfo.certStatus == 4"
                        @click="toAuth"
                      >
                        <img
                          src="../../assets/icon/err-authentication-icon.png"
                        />
                        认证失败 >
                      </div>
                      <!-- 个人用户 -->
                      <div
                        class="flag authentication"
                        v-if="
                          userInfo.certStatus == 3 && userInfo.certType == 1
                        "
                        @click="toAuth"
                      >
                        <img src="../../assets/icon/authentication-icon.png" />
                        个人认证
                      </div>
                      <!-- 企业用户 -->
                      <div
                        class="flag authentication"
                        v-if="
                          userInfo.certStatus == 3 && userInfo.certType == 2
                        "
                        @click="toAuth"
                      >
                        <img src="../../assets/icon/authentication-icon.png" />
                        企业认证
                      </div>
                    </div>
                  </div>
                </div>
                <el-dropdown-menu>
                  <el-dropdown-item @click="changeInfo">
                    <el-icon>
                      <User />
                    </el-icon>
                    修改资料
                  </el-dropdown-item>
                  <el-dropdown-item @click="changePsw">
                    <el-icon>
                      <Files />
                    </el-icon>
                    修改密码
                  </el-dropdown-item>
                  <el-dropdown-item @click="logout">
                    <el-icon>
                      <SwitchButton />
                    </el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </div>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </div>
  <el-dialog
    v-model="changePasswordVisible"
    title="修改密码"
    center
    width="30%"
    :modal-append-to-body="true"
  >
    <ProForm
      ref="proFormRef"
      v-model="updatePasswordParams"
      :formColumns="updatePasswordColumns"
      :label-width="120"
      class="mt20"
    >
      <template #phone>
        <el-input disabled :value="updatePasswordParams.phone"></el-input>
      </template>
    </ProForm>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="changePasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePassWord"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    v-model="changeInfoVisible"
    title="修改个人资料·"
    center
    width="30%"
    :modal-append-to-body="true"
  >
    <ProForm
      ref="proFormRef"
      v-model="updateInfoParams"
      :formColumns="updateInfoColumns"
      :label-width="120"
      class="mt20"
    >
    </ProForm>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="changeInfoVisible = false">取消</el-button>
        <el-button type="primary" @click="updateInfo"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="TopBar">
// import {
//   // BellFilled,
//   // Checked,
//   Coin,
//   Position,
//   FolderChecked,
// } from "@element-plus/icons-vue";
import { LOGIN_URL } from "@/config";
import { useRouter } from "vue-router";
import { useUserInfo } from "@/store";
import { getToken } from "@/utils/auth";
const router = useRouter();
let Token = ref(getToken());
// const showTag = ref(false);

import { computed, ref, watch } from "vue";
import { toPage } from "@/utils";
import { updatePasswordAPI, updateUserInfoAPI } from "@/api/user";
import { UserFilled, Lock, Message } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const userInfo = useUserInfo();
// let isActive = ref(1)
let isActive = computed(() => {
  console.log(router.currentRoute.value);
  switch (router.currentRoute.value.path) {
    case "/home":
      return 1;
    case "/application":
    case "/application/detail":
      return 2;
    case "/model":
    case "/model/detail":
      return 3;
    case "/data":
    case "/data/detail":
      return 4;
    case "/computeList":
      return 5;
    case "/diycomputeList":
      return 6;
    case "/computeListNew":
      return 7;
    case "/aiModel":
      return 8;
    case "/modelZoo":
      return 9;
    // case '/networkProduct':
    // case '/networkProductDetail':
    //   return 6
    // case '/aiModel':
    //   return 7
    // case '/partner':
    //   return 8
    // case '/activityCenter':
    //   return 9
  }
});
const menuList = [
  {
    title: "首页",
    link: "/home",
  },
  {
    title: "应用",
    link: "/application",
  },
  {
    title: "模型",
    link: "/model",
  },
  {
    title: "数据",
    link: "/data",
  },
  {
    title: "算力市场",
    link: "/computeList",
    tab: "hot",
  },
  {
    title: "算力智选",
    link: "/diycomputeList",
    tab: "new",
  },
  {
    title: "算力市场-new",
    link: "/computeListNew",
    tab: "new",
  },
  // {
  //   title: 'AGI-C',
  //   link: '/networkProduct',
  // },
  {
    title: "AI智算大模型",
    link: "/aiModel",
  },
  {
    title: "摩搭",
    link: "/modelZoo",
  },
  // {
  //   title: '合作伙伴',
  //   link: '/partner',
  // },
  // {
  //   title: '活动中心',
  //   link: '/activityCenter',
  // },
  // {
  //   title: '新闻资讯',
  //   link: '/news',
  // },
];
const handleRecharge = () => {
  if (Token.value) toPage("/rechargeCenter");
  else router.push(LOGIN_URL);
};
const handleRegister = () => {
  router.push(LOGIN_URL);
};

const logout = () => {
  userInfo.LogOut();
  Token.value = getToken();
};

const handleClick = (link: string, index: number) => {
  if (!link) {
    ElMessage.warning("页面开发中...");
    return;
  }
  router.push(link);
};
// 修改密码
const changePasswordVisible = ref(false);
const changePsw = () => {
  changePasswordVisible.value = true;
};
const updatePasswordParams = ref({ phone: userInfo.$state.phone, uid: "" });

const updatePasswordColumns: IFormColumnsProps[] = [
  {
    prop: "phone",
    label: "手机号",
    prefixIcon: UserFilled,
    el: "slot",
  },
  {
    prop: "password",
    label: "密码",
    prefixIcon: Lock,
    el: "password",
  },
  {
    prop: "code",
    prefixIcon: Message,
    el: "code",
    authUid: "uid",
    codePhone: "phone",
    codeType: "SMS",
    tmsg: "3",
  },
];
// 修改个人资料
const changeInfoVisible = ref(false);
const changeInfo = () => {
  changeInfoVisible.value = true;
};
const updateInfoParams = ref({
  phone: userInfo.$state.phone,
  uid: "",
  customerName: userInfo.$state.customerName,
  avatar: userInfo.$state.avatar,
});
const updateInfoColumns: IFormColumnsProps[] = [
  {
    prop: "customerName",
    label: "客户名称",
    el: "input",
  },
  {
    prop: "avatar",
    label: "用户头像",
    el: "img",
  },
];
const toAuth = () => {
  toPage("/authenticationCenter");
};
const updatePassWord = async () => {
  updatePasswordParams.value.uid = updatePasswordParams.value.phone;
  await updatePasswordAPI(updatePasswordParams.value);
  // UserNameLoginRef.value.authCodeRef.getAuthCode()
  changePasswordVisible.value = false;
  ElMessage.success("修改密码成功,正在退出登录..");
  userInfo.LogOut();
  Token.value = getToken();
  toPage("/login");
};
const updateInfo = async () => {
  await updateUserInfoAPI(updateInfoParams.value).then((res) => {
    if (res.code == 200) {
      userInfo.getUserInfo();
      ElMessage.success("修改信息成功!");
      changeInfoVisible.value = false;
    }
  });
};
</script>
<style lang="scss" scoped>
.top-bar {
  z-index: 2;
  position: absolute;
  top: 0;
  left: 0;
  height: 100px;
  width: 100%;
  background-color: #ffffff;

  .top-bar-box {
    height: 100%;
    width: 100vw;
    position: relative;
    display: flex;
    align-items: center;
    gap: 30px;

    /* 菜单栏 */
    .bar-menu {
      display: flex;
      justify-content: center;
      gap: 0px;

      .menu-li {
        position: relative;
        white-space: nowrap;
        font-weight: 400;
        font-size: 20px;
        cursor: pointer;
        height: 100px;
        box-sizing: border-box;
        padding: 0 36px;

        &:hover {
          color: #3972fd;
        }

        /* logo */
        .bar-icon {
          position: absolute;
          top: 10%;
          left: 64%;
          width: 52px;
          height: 24px;

          border-radius: 8px 8px 8px 0px;
          display: flex;
          align-items: center;
          justify-content: center;

          font-weight: 400;
          font-size: 18px;
          color: #ffffff;
        }

        .hot {
          background: #ff4151;
        }

        .new {
          background: #fba201;
        }
      }

      .active {
        color: #3972fd;

        &::after {
          content: "";
          position: absolute;
          bottom: 0;
          left: 50%;
          /* 使其居中 */
          width: 28px;
          /* 设置边框长度 */
          height: 4px;
          /* 设置伪元素的高度作为边框 */
          background-color: #3972fd;
          /* 边框颜色 */
          transform: translateX(-50%);
          /* 调整使其居中 */
        }
      }
    }

    .bar-button {
      gap: 20px;
      margin-left: auto;
      margin-right: 20px;

      .gift-btn {
        gap: 8px;
        cursor: pointer;

        .icon {
          gap: 8px;
          width: 174px;
          height: 60px;
          background: #d7e2f6;
          border-radius: 12px 12px 12px 12px;
          color: #3972fd;
          display: flex;

          &:hover {
            background: #3972fd;
            color: #ffffff;
          }

          img {
            width: 70px;
            height: 55px;
            border-radius: 0px 0px 0px 0px;
          }

          .btn {
            font-weight: 400;
            font-size: 18px;
          }
        }
      }

      .el-button {
        width: 174px;
        height: 60px;
        background: linear-gradient(133deg, #3b75fe 0%, #3871f4 100%);
        border-radius: 12px 12px 12px 12px;

        font-family: Microsoft YaHei, Microsoft YaHei;
        font-weight: 400;
        font-size: 18px;
        color: #ffffff;

        &:hover {
          background: #3f97fe;
        }
      }
    }

    .userInfo {
      .avatar {
        width: 40px;
        height: 40px;
        border-radius: 100%;
        margin-right: 10px;
      }
    }
  }
}

.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}

.dropdown {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 216px;
  // width: 250px;
  height: 180px;
  padding: 18px;

  .info {
    padding-top: 10px;
    display: flex;
    gap: 16px;

    .left {
      height: 54px;
      width: 54px;
      border-radius: 100%;

      img {
        height: 100%;
        width: 100%;
        border-radius: 100%;
      }
    }

    .right {
      display: flex;
      flex-direction: column;
      gap: 5px;

      .r-top {
        font-weight: bold;
        font-size: 18px;
        color: #333333;
      }

      .r-bottom {
        .flag {
          width: 102px;
          height: 26px;
          border-radius: 4px 4px 4px 4px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 5px 7px;

          img {
            height: 16px;
            width: 16px;
          }

          &:hover {
            cursor: pointer;
          }
        }

        .authentication {
          background: #e6eeff;
          font-weight: 400;
          font-size: 14px;
          color: #3972fd;
        }

        .not-authentication {
          background: #fff3dc;
          font-weight: 400;
          font-size: 14px;
          color: #f0a50f;
        }
      }
    }
  }
}
</style>
