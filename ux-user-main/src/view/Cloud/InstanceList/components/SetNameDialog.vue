<template>
  <el-dialog
    v-model="visible"
    title="设置名称"
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
      <el-form-item label="当前名称">
        <span>{{ instance?.name || '未设置' }}</span>
      </el-form-item>
      <el-form-item label="新名称" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入实例名称"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        确认设置
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch } from 'vue'
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
  name: ''
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入实例名称', trigger: 'blur' },
    { min: 1, max: 50, message: '名称长度1-50个字符', trigger: 'blur' }
  ]
}

// 监听实例变化，初始化表单
watch(() => props.instance, (newInstance) => {
  if (newInstance) {
    form.name = newInstance.name || ''
  }
}, { immediate: true })

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid && props.instance) {
      loading.value = true
      try {
        await instanceStore.setInstanceName(props.instance.id, form.name)
        ElMessage.success('名称设置成功')
        visible.value = false
        emit('success')
      } catch (error) {
        ElMessage.error('名称设置失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>
