<template>
  <el-dialog v-model="visible" :title="title" width="440px" :close-on-click-modal="false">
    <el-alert
      v-if="mode === 'resetPassword'"
      class="action-tip"
      title="密码修改后需要重启实例才会生效，重启前请继续使用旧密码。"
      type="warning"
      :closable="false"
      show-icon
    />
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item label="实例">
        <span class="instance-value">{{ instanceId || '-' }}</span>
      </el-form-item>
      <template v-if="mode === 'rename'">
        <el-form-item label="当前名称">
          <span>{{ instance?.instance_name || instance?.name || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="新名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入实例名称" maxlength="64" show-word-limit />
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入 8-30 位新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <div class="password-rule">8-30 位，且至少包含大写字母、小写字母、数字、特殊字符中的两类。</div>
      </template>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">{{ mode === 'rename' ? '确认修改' : '确认重置' }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { CpuInstance } from '@/types/instance'
import { renameCpuInstance, resetCpuInstancePassword } from '@/api/instanceV2'

interface Props {
  modelValue: boolean
  mode: 'rename' | 'resetPassword'
  instance: CpuInstance | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  success: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value),
})
const title = computed(() => props.mode === 'rename' ? '修改实例名称' : '重置密码')
const instanceId = computed(() => props.instance?.instance_id || props.instance?.id || '')

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({ name: '', password: '', confirmPassword: '' })

const validatePassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) return callback(new Error('请输入新密码'))
  if (value.length < 8 || value.length > 30) return callback(new Error('密码长度需为 8-30 位'))
  const categories = [/[A-Z]/, /[a-z]/, /\d/, /[^A-Za-z0-9]/].filter(pattern => pattern.test(value)).length
  if (categories < 2) return callback(new Error('需包含大写字母、小写字母、数字、特殊字符中的至少两类'))
  callback()
}

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) return callback(new Error('请再次输入新密码'))
  if (value !== form.password) return callback(new Error('两次输入的密码不一致'))
  callback()
}

const rules = computed<FormRules>(() => props.mode === 'rename'
  ? { name: [
      { required: true, message: '请输入实例名称', trigger: 'blur' },
      { min: 1, max: 64, message: '名称长度需为 1-64 个字符', trigger: 'blur' },
    ] }
  : {
      password: [{ validator: validatePassword, trigger: 'blur' }],
      confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
    })

const resetForm = () => {
  form.name = props.instance?.instance_name || props.instance?.name || ''
  form.password = ''
  form.confirmPassword = ''
  formRef.value?.clearValidate()
}

watch(
  () => [props.modelValue, props.mode, props.instance] as const,
  ([open]) => { if (open) resetForm() },
  { immediate: true },
)

const handleSubmit = async () => {
  if (!formRef.value || !instanceId.value) return
  await formRef.value.validate(async valid => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.mode === 'rename') {
        const res = await renameCpuInstance(instanceId.value, form.name.trim())
        if (res.data?.success === false) throw new Error(res.data.message || '名称修改失败')
        ElMessage.success(res.data?.message || '名称修改成功')
      } else {
        const res = await resetCpuInstancePassword(instanceId.value, form.password)
        if (res.data?.success === false) throw new Error(res.data.message || '密码重置失败')
        ElMessage.success(res.data?.message || '密码已重置，重启实例后生效')
      }
      visible.value = false
      emit('success')
    } catch (error: any) {
      ElMessage.error(error?.response?.data?.message || error?.message || (props.mode === 'rename' ? '名称修改失败' : '密码重置失败'))
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.action-tip { margin-bottom: 16px; }
.instance-value { overflow-wrap: anywhere; font-family: Menlo, Monaco, Consolas, monospace; }
.password-rule { margin: -4px 0 8px 96px; color: #909399; font-size: 12px; line-height: 1.5; }
</style>
