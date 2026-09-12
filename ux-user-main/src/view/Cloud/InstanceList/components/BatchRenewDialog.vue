<template>
  <el-dialog v-model="visible" title="批量续费" width="420px" :close-on-click-modal="false">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="已选实例">
        <span>{{ instances.length }} 个</span>
      </el-form-item>
      <el-form-item label="计费方式" prop="billingMode">
        <el-select v-model="form.billingMode" placeholder="请选择计费方式" style="width: 100%">
          <el-option label="包年包月" value="monthly" />
          <el-option label="按量计费" value="hourly" />
        </el-select>
      </el-form-item>
      <el-form-item label="续费时长" prop="duration">
        <el-input-number v-model="form.duration" :min="1" :max="120" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" :disabled="instances.length === 0" @click="handleSubmit">
        确认续费
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Instance } from '@/types/instance'
import { useInstanceStore } from '@/store/modules/useInstance'

const props = defineProps<{
  modelValue: boolean
  instances: Instance[]
}>()

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
  billingMode: 'monthly',
  duration: 1
})

const rules: FormRules = {
  billingMode: [{ required: true, message: '请选择计费方式', trigger: 'change' }],
  duration: [{ required: true, message: '请输入续费时长', trigger: 'blur' }]
}

const handleSubmit = async () => {
  if (!formRef.value || props.instances.length === 0) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await instanceStore.batchRenew({
        instanceIds: props.instances.map(item => item.id),
        billingMode: form.billingMode,
        duration: form.duration
      })
      ElMessage.success('批量续费成功')
      visible.value = false
      emit('success')
    } catch (error) {
      ElMessage.error('批量续费失败')
    } finally {
      loading.value = false
    }
  })
}
</script>
