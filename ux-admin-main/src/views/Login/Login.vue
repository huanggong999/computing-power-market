<template>
  <div class="login-container flx-center">
    <div class="login-box">
      <SwitchDark class="dark" />
      <div class="login-left"></div>
      <div class="login-form">
        <div class="login-logo">
          <img class="login-icon" :src="logo" alt="logo" />
          <h2 class="logo-text">{{ title }}</h2>
        </div>
        <UserNameLogin
          v-model="loginParams"
          :codeUrl="codeUrl"
          @submit="submit"
          @getCode="getCode"
        />

        <div class="login-btn">
          <el-button
            :icon="UserFilled"
            type="primary"
            round
            :disabled="disabled"
            @click="submit"
            :loading="loading"
          >
            登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Login">
import { getCodeApi, LoginApi } from "@/api/Login";
import UserNameLogin from "./components/UserNameLogin.vue";
import { setToken } from "@/utils/auth";
import router from "@/router";
import { HOME_URL } from "@/config";
import { useUser } from "@/store";
import { getTimeState } from "@/utils";
import { initDynamicRouter } from "@/router/modules/dynamicRouter";

import { UserFilled } from "@element-plus/icons-vue";

const title = import.meta.env.VITE_GLOB_APP_TITLE;
// const logo = import.meta.env.VITE_GLOB_APP_LOGO;
// 动态导入所有 logo 图片
const logoImg = import.meta.glob("/src/assets/logo/*.png");
const logo = ref("");
// 根据 env 配置 获取对应的 logo 图片
const handleLogo = async () => {
  if (!logoImg[`/src/assets/logo/${import.meta.env.VITE_GLOB_APP_LOGO}.png`])
    return;
  return await logoImg[
    `/src/assets/logo/${import.meta.env.VITE_GLOB_APP_LOGO}.png`
  ]().then((Module: any) => (logo.value = Module.default));
};
handleLogo();

const user = useUser();
const disabled = computed(
  () =>
    !loginParams.value.username ||
    !loginParams.value.password ||
    !loginParams.value.code
);

//  admin admin123456
const loginParams = ref<ILoginParams>({
  username: "",
  password: "",
  uid: "",
  code: "",
});
const codeUrl = ref("");
const loading = ref(false);

const getCode = async () => {
  const { data } = await getCodeApi();
  codeUrl.value = "data:image/jpeg;base64," + data.img;
  loginParams.value.uid = data.uid;
};
getCode();

const submit = async () => {
  loading.value = true;

  LoginApi(loginParams.value)
    .then(async ({ data }) => {
      localStorage.clear();
      setToken(data.token);
      user.token = data.token;
      await initDynamicRouter();
      router.push(HOME_URL);
      ElNotification({
        title: getTimeState(),
        message: `欢迎登录 ${user.nickName}`,
        type: "success",
        duration: 3000,
      });
    })
    .finally(() => (loading.value = false))
    .catch(() => getCode());
};
</script>

<style lang="scss" scoped>
@use "./index.scss";
</style>
