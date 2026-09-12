<template>
  <ProForm
    ref="ruleFormRef"
    :formColumns="formColumns"
    v-model="model"
    labelPosition="top"
  />
</template>

<script setup lang="ts" name="Register">
import { Iphone, Lock, UserFilled } from '@element-plus/icons-vue'

const props = defineProps<{ modelValue: IRegisterParams }>()
const emit = defineEmits(['update:modelValue'])
const model = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})
const ruleFormRef = ref()

const placeholder = {
  customerName: '请设置用户名，5-20个字符',
  password: '请设置登录密码',
  code: '请输入验证码',
}
const formColumns = ref<IFormColumnsProps[]>([
  {
    placeholder: placeholder.customerName,
    prop: 'customerName',
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
    prop: 'phone',
    prefixIcon: Iphone,
    el: 'phone',
    style: { height: '40px', fontSize: '16px' },
  },
  {
    placeholder: placeholder.code,
    prop: 'code',
    prefixIcon: Lock,
    authUid: 'uid',
    codePhone: 'phone',
    el: 'code',
    codeType: 'SMS',
    tmsg: '2',
    style: { height: '40px', width: '68%', fontSize: '16px' },
  },
])

defineExpose({ ruleFormRef })
</script>
<style lang="scss" scoped></style>
