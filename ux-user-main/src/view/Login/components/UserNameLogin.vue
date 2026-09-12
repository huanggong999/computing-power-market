<template>
  <el-tabs
    v-model="activeName"
    class="demo-tabs mb5 ft18"
    @tab-click="resetForm"
  >
    <el-tab-pane label="账号登录" name="userNameLogin" />
    <el-tab-pane label="短信登录" name="phoneLogin" />
  </el-tabs>
  <ProForm
    ref="ruleFormRef"
    labelPosition="top"
    v-model="model"
    :formColumns="formColumns"
  >
    <template #code>
      <el-input
        placeholder="请输入"
        v-model.trim="model.code"
        :show-word-limit="true"
        style="width: 68%; height: 40px; font-size: 16px"
      >
      </el-input>
      <AuthCode
        class="codeBtn"
        type="math"
        ref="authCodeRef"
        @authCodeId="(uid:any) => setUid(uid)"
        @checkCode="(code:any)=>model.code = code"
      />
    </template>
  </ProForm>
</template>

<script setup lang="ts" name="UserNameLogin">
import { UserFilled, Lock, Iphone } from '@element-plus/icons-vue'
import AuthCode from '@/view/Login/components/AuthCode.vue'
const props = defineProps<{ modelValue: ILoginParams }>()
const emit = defineEmits(['update:modelValue'])
const model = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const authCodeRef = ref()

const activeName = ref('userNameLogin')
const ruleFormRef = ref()
const resetForm = () => {
  model.value.uid = ''
  ruleFormRef.value.formRef.resetFields()
}

const formColumns = computed(() =>
  activeName.value === 'userNameLogin'
    ? userNameColumns.value
    : phoneColumns.value
)

const placeholder = {
  username: '请输入账号',
  password: '请输入密码',
  phone: '请输入手机号',
  code: '请输入验证码',
}
const userNameColumns = ref<IFormColumnsProps[]>([
  {
    placeholder: placeholder.phone,
    prop: 'username',
    prefixIcon: UserFilled,
    el: 'input',
    style: { height: '40px', fontSize: '16px' },
  },
  {
    placeholder: placeholder.password,
    prop: 'password',
    prefixIcon: Lock,
    el: 'password',
    style: { height: '40px', fontSize: '16px' },
  },
  {
    placeholder: placeholder.code,
    prop: 'code',
    el: 'slot',
    authUid: 'uid',
    tmsg: '1',
  },
])
const phoneColumns = ref<IFormColumnsProps[]>([
  {
    placeholder: placeholder.phone,
    prop: 'phone',
    prefixIcon: Iphone,
    el: 'input',
    style: { height: '40px', fontSize: '16px' },
  },
  {
    placeholder: placeholder.code,
    prop: 'code',
    prefixIcon: Lock,
    el: 'code',
    authUid: 'uid',
    codePhone: 'phone',
    codeType: 'SMS',
    tmsg: '1',
    style: { width: '68%', height: '40px', fontSize: '16px' },
  },
])
const setUid = (uid: any) => {
  model.value.uid = uid
}
defineExpose({ ruleFormRef, activeName, authCodeRef })
</script>
<style lang="scss" scoped>
:deep(.el-tabs__item) {
  font-size: 20px;
  font-weight: bold;
}
:deep(.el-tabs__nav-wrap:after) {
  height: 0;
}
.codeBtn {
  width: 28%;
  margin-left: 4%;
  height: 50px;
}
.ft18 {
  font-size: 18px;
}
</style>
