<template>
  <el-dialog v-model="visible" title="定时关机" width="460px" :close-on-click-modal="false" @open="loadSchedule">
    <el-form label-width="100px" v-loading="loading">
      <el-form-item label="实例">
        <span>{{ instance?.name || instance?.id }}</span>
      </el-form-item>
      <el-form-item label="关机时间">
        <el-date-picker
          v-model="shutdownTime"
          type="datetime"
          value-format="YYYY-MM-DDTHH:mm:ss"
          placeholder="请选择关机时间"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button :loading="submitting" @click="cancelSchedule">取消定时关机</el-button>
      <el-button type="primary" :loading="submitting" :disabled="!shutdownTime" @click="saveSchedule">
        保存
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { Instance } from '@/types/instance'
import { useInstanceStore } from '@/store/modules/useInstance'

const props = defineProps<{
  modelValue: boolean
  instance: Instance | null
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
const loading = ref(false)
const submitting = ref(false)
const shutdownTime = ref<string | null>(null)

const loadSchedule = async () => {
  if (!props.instance) return
  loading.value = true
  try {
    const res = await instanceStore.getShutdownSchedule(props.instance.id)
    shutdownTime.value = res.data?.shutdownTime || null
  } catch (error) {
    shutdownTime.value = null
  } finally {
    loading.value = false
  }
}

const saveSchedule = async () => {
  if (!props.instance || !shutdownTime.value) return
  submitting.value = true
  try {
    await instanceStore.setShutdownSchedule(props.instance.id, { shutdownTime: shutdownTime.value })
    ElMessage.success('定时关机设置成功')
    visible.value = false
    emit('success')
  } catch (error) {
    ElMessage.error('定时关机设置失败')
  } finally {
    submitting.value = false
  }
}

const cancelSchedule = async () => {
  if (!props.instance) return
  submitting.value = true
  try {
    await instanceStore.setShutdownSchedule(props.instance.id, { shutdownTime: null })
    shutdownTime.value = null
    ElMessage.success('已取消定时关机')
    visible.value = false
    emit('success')
  } catch (error) {
    ElMessage.error('取消定时关机失败')
  } finally {
    submitting.value = false
  }
}
</script>
