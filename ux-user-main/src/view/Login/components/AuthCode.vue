<template>
  <el-button
    v-if="props.type === 'SMS'"
    type="primary"
    @click="getAuthCode"
    :disabled="!!timer || !props.phone"
    :style="props.style ? props.style : {}"
  >
    {{ buttonText }}
  </el-button>
  <div class="login-code" v-else>
    <img :src="codeUrl" @click="getAuthCode" class="login-code-img" />
  </div>
</template>

<script setup lang="ts" name="AuthCode">
import { getCodeAPI } from '@/api/user'
import { useIntervalFn } from '@vueuse/core'
const props = defineProps<{
  phone?: string
  tmsg: string
  type: TCodeType
  style?: any
}>()
const codeUrl = ref('')
const emit = defineEmits(['authCodeId', 'checkCode'])
const buttonText = computed(() =>
  timer.value ? `${timer.value}s 后重新发送` : '发送验证码'
)
const timer = ref(0)

const { pause, resume } = useIntervalFn(() => timer.value--, 1000)
watch(timer, (val) => {
  if (val <= 0) {
    pause()
    timer.value = 0
  }
})

const getAuthCode = async () => {
  if (props.type === 'SMS') timer.value = 60
  getCodeAPI(props.type, props.tmsg, props.phone)
    .then(({ data }) => {
      if (props.type === 'SMS') ElMessage.success('发送成功')
      codeUrl.value = 'data:image/jpeg;base64,' + data.img
      emit('authCodeId', data.uid)
      // emit('checkCode', data.code)
      if (props.type) resume()
    })
    .catch(() => {
      timer.value = 0
    })
}
if (props.type !== 'SMS') getAuthCode()

defineExpose({ getAuthCode })
</script>
<style lang="scss" scoped>
.login-code {
  text-align: right;
  height: 40px;
  img {
    width: 90%;
    height: 100%;
    cursor: pointer;
    vertical-align: top;
  }
}
</style>
