<template>
  <el-dialog
    v-model="visible"
    title="重置密码"
    width="400px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="实例">
        <span>{{ instance?.id }}</span>
      </el-form-item>
      <el-form-item label="新密码" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          show-password
          placeholder="请输入新密码"
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          show-password
          placeholder="请再次输入新密码"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        确认重置
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Instance } from '@/types/instance'
import { useInstanceStore } from '@/store/modules/useInstance'

interface Props {
  modelValue: boolean
  instance: Instance | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  success: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const instanceStore = useInstanceStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePassword = (_rule: any, value: string, callback: any) => {
  if (!value) return callback(new Error('请输入新密码'))
  if (value.length < 8 || value.length > 30) return callback(new Error('密码长度需为 8-30 位'))
  const categories = [/[A-Z]/, /[a-z]/, /\d/, /[^A-Za-z0-9]/].filter(pattern => pattern.test(value)).length
  if (categories < 2) return callback(new Error('需包含大写字母、小写字母、数字、特殊字符中的至少两类'))
  callback()
}

const rules: FormRules = {
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid && props.instance) {
      loading.value = true
      try {
        await instanceStore.resetPassword(props.instance.id, form.password)
        ElMessage.success('密码已重置，重启实例后生效')
        visible.value = false
        emit('success')
        // 重置表单
        form.password = ''
        form.confirmPassword = ''
      } catch (error) {
        ElMessage.error('密码重置失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>
