<template>
  <div class="login">
    <div class="login-box">
      <!-- 切换登录模式 -->
      <img
        src="https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/code-login.png"
        @click="openQrCode"
        class="right-corner"
      />
      <!-- 账密登录 -->
      <div class="login-win">
        <h2 class="h2 mb50">{{ IsLogin ? '欢迎登录' : '注册' }}</h2>
        <UserNameLogin
          v-if="IsLogin"
          v-model="loginParams"
          ref="UserNameLoginRef"
        />
        <Register v-model="registerParams" ref="RegisterRef" v-if="!IsLogin" />
        <div class="tip tac mt10">
          {{ IsLogin ? '登录' : '注册' }}视为您已阅读并同意
          <el-button
            type="primary"
            link
            class="ml2 mr2 ft18"
            @click="openDocs('serve')"
            >服务条款</el-button
          >
          和
          <el-button
            class="ml2 ft18"
            type="primary"
            link
            @click="openDocs('privacy')"
            >隐私政策</el-button
          >
        </div>
        <el-button
          class="btn mt20"
          type="primary"
          @click="submit"
          v-loading="submitLoading"
          :disabled="submitLoading"
        >
          {{ IsLogin ? '立即登录' : '立即注册' }}
        </el-button>
        <div
          class="mt20"
          :class="IsLogin ? 'flx-justify-between' : 'flx-center'"
        >
          <el-button
            v-if="IsLogin"
            class="ft18"
            link
            @click="changePasswordVisible = true"
          >
            重置密码
          </el-button>
          <el-text v-if="!IsLogin" type="info" class="ft18">
            已有帐号？
          </el-text>
          <el-button
            type="primary"
            link
            @click="toggleLoginStatus"
            class="ft18"
          >
            {{ IsLogin ? '注册' : '立即登录' }}
          </el-button>
        </div>
      </div>
      <!-- 扫码登录 -->
      <!-- <div class="code-login" v-else>
        <h2 class="h2 mb30">微信扫码登录</h2>
        <img id="login_container" :src="codeImg" class="code-img">
        <div class="tip tac mt10">
          登录视为您已阅读并同意
          <el-button type="primary" link class="ml2 mr2" @click="openDocs('serve')">服务条款</el-button>
          和
          <el-button class="ml2" type="primary" link @click="openDocs('privacy')">隐私政策</el-button>
        </div>
      </div> -->
      <el-dialog
        v-model="changePasswordVisible"
        title="重置密码"
        center
        width="30%"
      >
        <ProForm
          ref="proFormRef"
          v-model="updatePasswordParams"
          :formColumns="updatePasswordColumns"
          :label-width="80"
          class="mt20"
        />
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="changePasswordVisible = false">取消</el-button>
            <el-button type="primary" @click="updatePassWord"> 确定 </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
    <!-- <iframe v-if="wxUrl" class="login-iframe" id="login_container" :src="wxUrl">123</iframe> -->
  </div>
</template>

<script setup lang="ts" name="Login">
import UserNameLogin from './components/UserNameLogin.vue'
import Register from './components/Register.vue'

import {
  getOpenId,
  getUnionId,
  removeOpenId,
  removeUnionId,
  setOpenId,
  setToken,
  setUnionId,
} from '@/utils/auth'
import router from '@/routes'
import { initDynamicRouter } from '@/routes/dynamicRouter'
import { useUserInfo } from '@/store'
import {
  getLoginPcCodeUrlAPI,
  getUnionIdAPI,
  loginAPI,
  registerAPI,
  resetPasswordAPI,
} from '@/api/user'
import { Lock, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { bindExtendRelevanceApi } from '@/api/partner'
import { useRoute } from 'vue-router'

const route = useRoute()
const wxUrl = ref('')
// 微信登录
const openQrCode = () => {
  getLoginPcCodeUrlAPI().then((res: any) => {
    if (res.code === 200) {
      wxUrl.value = res.data
      // 计算屏幕居中的位置
      const width = 400
      const height = 400
      const left = (window.innerWidth - width) / 2
      const top = (window.innerHeight - height) / 2

      // 打开新窗口（微信扫码页）
      window.open(
        res.data,
        'wechatLogin',
        `width=${width},height=${height},top=${top},left=${left},resizable=no,scrollbars=no`
      )
    }
  })
}
onMounted(() => {
  if (window.opener) {
    window!.opener.location.href = window.location.href
    window.opener.location.reload()
    window.close()
  } else {
    wxLogin()
  }
})

const wxCode = ref<any>(route.query.code)
const wxLogin = () => {
  if (wxCode.value) {
    // 获取用户unionId以及绑定状态
    getUnionIdAPI(wxCode.value).then((res: any) => {
      if (res.code === 200) {
        setUnionId(res.data.unionId)
        setOpenId(res.data.openId)
        // 已绑定（直接WECHAT_LOGIN）
        if (res.data.isBind) {
          loginAPI(
            { unionId: res.data.unionId, openId: res.data.openId },
            'WECHAT_LOGIN'
          ).then(async ({ data }: any) => {
            ElMessage.success('登录成功')
            setToken(data.token)
            userInfo.token = data.token
            await initDynamicRouter()
            if (localStorage.getItem('code')) {
              await bindExtendRelevanceApi({ code: code.value })
              localStorage.removeItem('code')
              localStorage.removeItem('IsLogin')
            }
            if (localStorage.getItem('inviterId')) {
              localStorage.removeItem('inviterId')
            }
            if (localStorage.getItem('activityId')) {
              localStorage.removeItem('activityId')
            }
            if (unionId.value && openId.value) {
              removeUnionId()
              removeOpenId()
            }
            router.push('/console')
          })
        } else {
          // 未绑定
          ElMessage.info(
            '当前微信用户暂未绑定平台账号，请用户登录或注册平台账户，系统将在此次自动绑定'
          )
        }
      }
    })
  }
}
// onMounted(() => {
//   wxLogin()
// })
const loginParams = ref<ILoginParams>({
  username: '',
  password: '',
  code: '',
  uid: '',
})
// 获取微信登录

const registerParams = ref<IRegisterParams>({})
const IsLogin = ref(true)
const code = ref()
const inviterId = ref()
const activityId = ref()
const unionId = ref()
const openId = ref()
const initPage = () => {
  // 如果是推广邀请
  if (localStorage.getItem('code')) {
    code.value = localStorage.getItem('code')
  }
  if (
    localStorage.getItem('IsLogin') &&
    localStorage.getItem('IsLogin') == 'false'
  ) {
    IsLogin.value = false
  } else {
    IsLogin.value = true
  }
  // 如果是活动邀请
  if (localStorage.getItem('`inviterId`')) {
    inviterId.value = localStorage.getItem('inviterId')
  }
  if (localStorage.getItem('activityId')) {
    activityId.value = localStorage.getItem('activityId')
    IsLogin.value = false
  }
  // 如果是绑定微信
  if (getUnionId() && getOpenId()) {
    unionId.value = getUnionId()
    openId.value = getOpenId()
  }
}
initPage()
const toggleLoginStatus = () => {
  loginParams.value = {}
  registerParams.value = {}
  IsLogin.value = !IsLogin.value
}
//
const submitLoading = ref(false)

const changeLoading = () => (submitLoading.value = !submitLoading.value)

const submit = () => {
  const refData = IsLogin.value ? UserNameLoginRef.value : RegisterRef.value
  const { ruleFormRef, activeName } = refData
  if (!ruleFormRef.formRef) return
  const apiMap = IsLogin.value ? loginFn : registerFn
  ruleFormRef.formRef.validate((valid: boolean) => {
    if (valid) return apiMap(activeName)
  })
}

const RegisterRef = ref()
// 注册
const registerFn = async () => {
  registerParams.value.uid = registerParams.value.phone
  // 如果有邀请码
  if (code.value) {
    registerParams.value.vcode = code.value
  }
  // 如果有活动邀请
  if (activityId.value) {
    registerParams.value.activityId = activityId.value
  }
  if (inviterId.value) {
    registerParams.value.inviterId = inviterId.value
  }
  // 如果有微信unionId和openId
  if (unionId.value && openId.value) {
    registerParams.value.unionId = unionId.value
    registerParams.value.openId = openId.value
  }
  await registerAPI(registerParams.value)
  // 注册完毕后清除微信信息
  if (unionId.value && openId.value) {
    removeUnionId()
    removeOpenId()
  }
  ElMessage.success('注册成功,请登录')
  toggleLoginStatus()
}

const UserNameLoginRef = ref()
const loginEnum: Record<string, TLoginType> = {
  userNameLogin: 'USERNAME_PASSWORD_LOGIN',
  phoneLogin: 'SMS_LOGIN',
}
const userInfo = useUserInfo()
// 登录
const loginFn = async (type: keyof typeof loginEnum) => {
  changeLoading()
  if (loginParams.value.phone) loginParams.value.uid = loginParams.value.phone
  if (unionId.value && openId.value) {
    loginParams.value.unionId = unionId.value
    loginParams.value.openId = openId.value
  }
  loginAPI(loginParams.value, loginEnum[type])
    .then(async ({ data }) => {
      ElMessage.success('登录成功')
      setToken(data.token)
      userInfo.token = data.token
      await initDynamicRouter()
      if (localStorage.getItem('code')) {
        await bindExtendRelevanceApi({ code: code.value })
        localStorage.removeItem('code')
        localStorage.removeItem('IsLogin')
      }
      if (localStorage.getItem('inviterId')) {
        localStorage.removeItem('inviterId')
      }
      if (localStorage.getItem('activityId')) {
        localStorage.removeItem('activityId')
      }
      if (unionId.value && openId.value) {
        removeUnionId()
        removeOpenId()
      }
      router.push('/console')
    })
    .finally(() => {
      changeLoading()
      UserNameLoginRef.value.authCodeRef.getAuthCode()
    })
}

// 修改密码
const changePasswordVisible = ref(false)

const updatePasswordParams = ref({ phone: '', uid: '' })
const placeholder = {
  username: '请输入账号',
  password: '请输入密码',
  phone: '请输入手机号',
  code: '请输入验证码',
}
const updatePasswordColumns: IFormColumnsProps[] = [
  {
    // placeholder: placeholder.username,
    prop: 'phone',
    label: '手机号',
    prefixIcon: UserFilled,
    el: 'input',
  },
  {
    // placeholder: placeholder.username,
    prop: 'password',
    label: '密码',
    prefixIcon: UserFilled,
    el: 'password',
  },
  {
    placeholder: placeholder.code,
    prop: 'code',
    prefixIcon: Lock,
    el: 'code',
    authUid: 'uid',
    codePhone: 'phone',
    codeType: 'SMS',
    tmsg: '3',
  },
]

watch(
  () => changePasswordVisible.value,
  (val) => {
    if (!val) updatePasswordParams.value = { phone: '', uid: '' }
  }
)

const updatePassWord = async () => {
  updatePasswordParams.value.uid = updatePasswordParams.value.phone
  await resetPasswordAPI(updatePasswordParams.value)
  UserNameLoginRef.value.authCodeRef.getAuthCode()
  changePasswordVisible.value = false
  ElMessage.success('重置密码成功')
}

// 查看文档
const openDocs = (type: string) => {
  let a = window.location.origin + '/#/'
  if (type === 'serve') {
    a += 'docsView/TermsOfService'
  }

  if (type === 'privacy') {
    a += 'docsView/PrivacyPolicy'
  }
  window.open(a, '_blank')
}
</script>
<style lang="scss" scoped>
.login {
  width: 100vw;
  height: 100vh;
  background: url('@/assets/images/loginBg.png') no-repeat;
  background-size: 100% 115%;

  &-box {
    max-height: 94%;
    height: 900px;

    width: 600px;
    padding: 100px 50px;
    background-color: var(--el-bg-color);
    border-radius: 15px;
    box-shadow: rgb(0 0 0 / 10%) 0 2px 10px 2px;
    top: 3%;
    right: 5%;
    position: absolute;

    .right-corner {
      position: absolute;
      width: 81px;
      height: 81px;
      top: 0;
      right: 0;

      &:hover {
        cursor: pointer;
      }
    }

    .h2 {
      font-size: 44px;
      color: #000000;
      font-weight: 800;
      position: relative;
    }

    .btn {
      width: 100%;
      height: 50px;
      font-size: 20px;
    }

    .tip {
      color: #999;
      font-size: 20px;
    }
    .ft18 {
      font-size: 20px;
    }
  }
  &-win {
    display: flex;
    flex-direction: column;
  }
  .code-login {
    .code-img {
      margin-left: 30px;
      margin-bottom: 15px;
      width: 262px;
      height: 262px;
      border-radius: 8px 8px 8px 8px;
      border: 1px solid #d9d9d9;
    }
  }

  .login-iframe {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 500px;
    height: 600px;
    background-color: white;
    border: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    padding-top: 40px;
  }
}
</style>
