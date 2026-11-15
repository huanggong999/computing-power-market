<template>
  <el-dialog v-model="visible" title="资源管理" width="720px" :close-on-click-modal="false" @open="loadOptions">
    <div v-loading="loading" class="resize-dialog">
      <el-alert
        v-if="options"
        :title="guidanceTitle"
        :description="guidanceDescription"
        :type="options.backing === 'volc' ? 'warning' : 'info'"
        show-icon
        :closable="false"
      />
      <el-form v-if="options" ref="formRef" :model="form" label-width="128px" class="resize-form">
        <template v-if="mode === 'gpu'">
          <el-divider content-position="left">多卡变更</el-divider>
          <el-form-item label="当前 GPU 卡数">
            <span>{{ options.current.gpu_count ?? '-' }} 卡</span>
          </el-form-item>
          <el-form-item label="目标 GPU 卡数">
            <el-select v-model="form.gpuCount" placeholder="请选择目标卡数" :disabled="gpuUnsupported">
              <el-option v-for="target in gpuTargets" :key="target.gpu_count" :label="`${target.gpu_count} 卡`" :value="target.gpu_count" :disabled="target.selectable === false || target.gpu_count <= Number(options.current.gpu_count || 0)" />
            </el-select>
          </el-form-item>
          <el-alert v-if="gpuUnsupported" title="当前实例暂不支持多卡变更" type="warning" show-icon :closable="false" />
        </template>
        <template v-else>
        <el-divider content-position="left">磁盘与规格</el-divider>
        <el-form-item v-if="options.capabilities.disk_expand" label="数据盘目标 (GiB)">
          <el-input-number v-model="form.diskGb" :min="(options.current.data_disk_gib || 0) + 1" :max="options.limits.data_disk_gib?.max" :step="10" />
          <span class="current-value">当前 {{ options.current.data_disk_gib || 0 }} GiB，只能增加，不能缩小</span>
        </el-form-item>
        <el-form-item v-if="options.capabilities.cpu_scale" label="目标 CPU（核）">
          <el-input v-model="form.cpu" placeholder="请输入目标核数，例如 8" />
          <span class="current-value resource-rule">
            当前 CPU：{{ options.current.cpu_cores ?? '-' }} 核<span v-if="options.capabilities.cpu_scale_up_only">；仅支持扩容，目标必须更大</span>
          </span>
        </el-form-item>
        <el-form-item v-if="options.capabilities.memory_scale" label="目标内存">
          <el-input v-model="form.memory" placeholder="请输入目标内存，例如 32Gi" />
          <span class="current-value resource-rule">
            当前内存：{{ options.current.memory || (options.current.memory_mib ? `${options.current.memory_mib}Mi` : '-') }}<span v-if="options.capabilities.cpu_scale_up_only">；仅支持扩容，目标必须更大</span>
          </span>
        </el-form-item>
        <el-form-item v-if="options.backing === 'volc' && (form.cpu || form.memory)" label="目标规格 ID" required>
          <el-input v-model="form.instanceTypeId" placeholder="例如 ecs.g3i.2xlarge" />
          <span class="field-help">调整 CPU 或内存时必须填写云主机规格 ID。</span>
        </el-form-item>
        <el-divider content-position="left">公网带宽</el-divider>
        <el-form-item label="目标带宽 (Mbps)">
          <el-input-number v-model="bandwidth" :disabled="!options.capabilities.bandwidth_change" :min="options.limits.bandwidth_mbps?.min" :max="options.limits.bandwidth_mbps?.max" />
          <span class="current-value" v-if="!options.capabilities.bandwidth_change">当前实例没有可单独调整带宽的公网 IP，暂时不能修改</span>
          <span class="current-value" v-else>当前 {{ options.current.bandwidth_mbps || 0 }} Mbps，可增加或减少</span>
        </el-form-item>
        </template>
        <el-divider content-position="left">最近任务</el-divider>
        <el-table :data="tasks" size="small" max-height="180">
          <el-table-column prop="task_id" label="任务 ID" min-width="170" />
          <el-table-column prop="status" label="状态" width="90" />
          <el-table-column label="参数" min-width="140"><template #default="{ row }">{{ JSON.stringify(row.params || {}) }}</template></el-table-column>
          <el-table-column label="步骤" min-width="180"><template #default="{ row }">{{ (row.steps || []).map((step: any) => step.message).join("；") || "-" }}</template></el-table-column>
          <el-table-column prop="message" label="说明" min-width="220" />
        </el-table>
        <el-alert v-if="taskMessage" :title="taskMessage" :type="taskStatus === 'failed' ? 'error' : taskStatus === 'success' ? 'success' : 'info'" show-icon :closable="false" />
      </el-form>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" :loading="submitting" :disabled="!canSubmitResize" @click="submitResizeForm">{{ mode === 'gpu' ? '提交多卡变更' : '提交磁盘/规格变更' }}</el-button>
      <el-button type="success" :loading="bandwidthSubmitting" :disabled="!canSubmitBandwidth" @click="submitBandwidth">提交带宽变更</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { changeBandwidth, getResizeOptions, getResizeTask, getResizeTasks, submitResize } from '@/api/instanceV2'
import type { ResizeOptions, ResizeTask } from '@/types/instance'
import { validateResizeRequest } from '@/utils/instanceResource'

const props = withDefaults(defineProps<{ modelValue: boolean; instanceId?: string; mode?: 'resize' | 'gpu' }>(), { mode: 'resize' })
const emit = defineEmits<{ 'update:modelValue': [boolean]; refresh: [] }>()
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) })
watch(() => props.modelValue, open => { if (!open && pollTimer) clearTimeout(pollTimer) })
onBeforeUnmount(() => { if (pollTimer) clearTimeout(pollTimer) })
const loading = ref(false)
const submitting = ref(false)
const bandwidthSubmitting = ref(false)
const options = ref<ResizeOptions | null>(null)
const tasks = ref<ResizeTask[]>([])
const form = reactive<{ diskGb?: number; cpu: string; memory: string; instanceTypeId: string; gpuCount?: number }>({ cpu: '', memory: '', instanceTypeId: '' })
const mode = computed(() => props.mode)
const gpuTargets = computed(() => options.value?.gpu_targets || [])
const gpuUnsupported = computed(() => mode.value === 'gpu' && (options.value?.capabilities.gpu_scale !== true || gpuTargets.value.length === 0))
const bandwidth = ref<number>()
const taskMessage = ref('')
const taskStatus = ref('')
let pollTimer: ReturnType<typeof setTimeout> | undefined
let pollCount = 0

const guidanceTitle = computed(() => {
  return ''
})
const guidanceDescription = computed(() => {
  if (!options.value) return ''
  const messages: string[] = []
  if (options.value.backing === 'volc') {
    messages.push('修改 CPU 或内存时实例会临时关机，变更完成后自动开机。')
  } else if (options.value.backing === 'cluster') {
    messages.push('CPU 和内存调整的是实例可用资源，实际可用上限以当前节点资源为准。')
  }
  if (options.value.capabilities.disk_expand) {
    messages.push('数据盘只能扩容；扩容完成后，系统可能还需要执行文件系统扩容命令才能看到新增空间。')
  }
  if (!messages.length) messages.push('可修改项和取值范围由服务端返回。')
  return messages.join(' ')
})

const canSubmitResize = computed(() => Boolean(options.value && !submitting.value && (mode.value === 'gpu' ? form.gpuCount != null && !gpuUnsupported.value : (form.diskGb != null || form.cpu || form.memory))))
const canSubmitBandwidth = computed(() => Boolean(options.value?.capabilities.bandwidth_change && bandwidth.value != null && !bandwidthSubmitting.value))

const loadOptions = async () => {
  if (!props.instanceId) return
  loading.value = true
  try {
    const [optionRes, taskRes] = await Promise.all([getResizeOptions(props.instanceId), getResizeTasks(props.instanceId)])
    options.value = optionRes.data
    tasks.value = taskRes.data?.tasks || []
    bandwidth.value = options.value?.current.bandwidth_mbps
    form.diskGb = undefined; form.cpu = ''; form.memory = ''; form.instanceTypeId = ''; form.gpuCount = undefined
    if (mode.value === 'gpu' && gpuUnsupported.value) ElMessage.warning('当前实例暂不支持多卡变更')
  } catch (error: any) {
    ElMessage.error(error?.message || '加载资源变更选项失败')
  } finally { loading.value = false }
}

const validate = () => {
  if (mode.value === 'gpu') {
    if (gpuUnsupported.value) return '当前实例暂不支持多卡变更'
    const current = Number(options.value?.current.gpu_count || 0)
    if (form.gpuCount == null) return '请选择目标 GPU 卡数'
    if (form.gpuCount <= current) return `目标 GPU 卡数必须大于当前 ${current} 卡`
    const target = gpuTargets.value.find(item => item.gpu_count === form.gpuCount)
    if (target?.selectable === false) return '当前目标 GPU 卡数暂不可用'
    return null
  }
  return validateResizeRequest(options.value!, { diskGb: form.diskGb, cpu: form.cpu || undefined, memory: form.memory || undefined, instanceTypeId: form.instanceTypeId || undefined })
}

const submitResizeForm = async () => {
  const error = validate(); if (error) return ElMessage.warning(error)
  if (!props.instanceId) return
  submitting.value = true
  try {
    const request = mode.value === 'gpu'
      ? { gpuCount: form.gpuCount }
      : { diskGb: form.diskGb, cpu: form.cpu || undefined, memory: form.memory || undefined, instanceTypeId: form.instanceTypeId || undefined }
    const res = await submitResize(props.instanceId, request)
    const payload = res.data
    if (!payload?.success) return ElMessage.error(payload?.message || '资源变更未受理')
    taskMessage.value = `任务 ${payload.task_id || '-'} 已受理，正在等待执行`
    taskStatus.value = payload.status || 'pending'
    if (payload.task_id) pollTask(payload.task_id)
    await loadOptions()
  } catch (error: any) { ElMessage.error(error?.message || '提交资源变更失败') }
  finally { submitting.value = false }
}

const pollTask = (taskId: string) => {
  if (pollTimer) clearTimeout(pollTimer)
  pollCount = 0
  const poll = async () => {
    if (++pollCount > 100) { taskMessage.value = '任务状态查询超时，请稍后从历史任务查看'; return }
    if (!props.instanceId) return
    try {
      const res = await getResizeTask(props.instanceId, taskId)
      const task = res.data
      taskStatus.value = task?.status || ''
      taskMessage.value = task?.message || taskMessage.value
      if (task?.status === 'success') { ElMessage.success('资源变更完成'); emit('refresh'); await loadOptions(); return }
      if (task?.status === 'failed') { ElMessage.error(task.message || '资源变更失败'); return }
      pollTimer = setTimeout(poll, 3000)
    } catch (error: any) { taskMessage.value = error?.message || '任务状态查询失败' }
  }
  poll()
}

const submitBandwidth = async () => {
  if (!props.instanceId || bandwidth.value == null || !options.value) return
  const limit = options.value.limits.bandwidth_mbps
  if ((limit?.min != null && bandwidth.value < limit.min) || (limit?.max != null && bandwidth.value > limit.max)) return ElMessage.warning('带宽超出服务端允许范围')
  bandwidthSubmitting.value = true
  try {
    const res = await changeBandwidth(props.instanceId, { bandwidthMbps: bandwidth.value })
    const payload = res.data
    if (!payload?.success) return ElMessage.error(payload?.message || '带宽调整失败')
    ElMessage.success(payload.message || '公网带宽已调整')
    emit('refresh'); await loadOptions()
  } catch (error: any) { ElMessage.error(error?.message || '带宽调整失败') }
  finally { bandwidthSubmitting.value = false }
}
</script>

<style scoped>
.resize-dialog { min-height: 260px; }
.guide { margin-top: 10px; }
.current-value { margin-left: 12px; color: #909399; font-size: 12px; }
.resource-rule { display: block; margin: 6px 0 0; line-height: 1.5; }
.field-help { display: block; margin-top: 5px; color: #909399; font-size: 12px; line-height: 1.5; }
.resize-form { margin-top: 12px; }
</style>
