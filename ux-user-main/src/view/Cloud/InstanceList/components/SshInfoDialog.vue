<template>
  <el-dialog v-model="visible" title="SSH 连接" width="560px" :close-on-click-modal="false" @open="loadSshInfo">
    <div v-loading="loading" class="ssh-dialog">
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="warning"
        :closable="false"
        show-icon
      />
      <template v-else-if="sshInfo">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="实例">{{ instanceId }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ sshStatusLabel }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ host || '-' }}</el-descriptions-item>
          <el-descriptions-item label="端口">{{ port || 22 }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ username || 'root' }}</el-descriptions-item>
          <el-descriptions-item label="公网 IP">{{ publicIp || '-' }}</el-descriptions-item>
          <el-descriptions-item label="内网 IP">{{ privateIp || '-' }}</el-descriptions-item>
          <el-descriptions-item label="密码">{{ maskedPassword || '-' }}</el-descriptions-item>
          <el-descriptions-item label="登录指令">
            <div class="command-row">
              <span class="command-value">{{ command || '-' }}</span>
              <el-button v-if="command" link type="primary" @click="copyText(command)">复制</el-button>
            </div>
          </el-descriptions-item>
        </el-descriptions>
        <el-alert
          v-if="host && !publicIp"
          class="ssh-tip"
          title="当前实例未绑定公网 IP，SSH 地址仅在云内网可达。"
          type="info"
          :closable="false"
          show-icon
        />
      </template>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" :disabled="!command" @click="copyText(command)">复制登录指令</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCpuInstanceSsh } from '@/api/instanceV2'
import { getInstanceSshInfo } from '@/api/instance'

interface Props {
  modelValue: boolean
  instanceId: string
  source: 'gpu' | 'cpu'
}

const props = defineProps<Props>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value),
})

const loading = ref(false)
const errorMessage = ref('')
const info = ref<Record<string, any> | null>(null)

const sshInfo = computed<Record<string, any> | null>(() => {
  if (!info.value) return null
  return info.value.ssh_info || info.value
})

const host = computed(() => String(sshInfo.value?.host || info.value?.public_ip || ''))
const port = computed(() => Number(sshInfo.value?.port || 22))
const username = computed(() => String(sshInfo.value?.username || 'root'))
const publicIp = computed(() => String(info.value?.public_ip || info.value?.ssh_info?.host || ''))
const privateIp = computed(() => String(info.value?.private_ip || ''))
const maskedPassword = computed(() => String(sshInfo.value?.password || sshInfo.value?.password_masked || ''))
const command = computed(() => {
  if (sshInfo.value?.command) return String(sshInfo.value.command)
  if (!host.value) return ''
  return `ssh -p ${port.value} ${username.value}@${host.value}`
})
const sshStatusLabel = computed(() => {
  const status = String(info.value?.status || '')
  return { running: '运行中', creating: '创建中', stopped: '已关机', releasing: '释放中', released: '已释放' }[status] || status || '-'
})

const loadSshInfo = async () => {
  if (!props.instanceId) {
    errorMessage.value = '实例缺少 ID，暂时无法获取 SSH 连接信息'
    return
  }
  loading.value = true
  errorMessage.value = ''
  info.value = null
  try {
    const res = props.source === 'cpu'
      ? await getCpuInstanceSsh(props.instanceId)
      : await getInstanceSshInfo(props.instanceId)
    const payload = res.data as Record<string, any>
    if (payload?.success === false) {
      errorMessage.value = payload.message || '当前状态无法获取 SSH 连接信息'
      return
    }
    info.value = payload || null
    if (!payload?.ssh_info?.host && !payload?.host) {
      errorMessage.value = payload?.message || '实例地址尚未下发，请稍后重试'
    }
  } catch (error: any) {
    errorMessage.value = error?.response?.data?.message || error?.message || '获取 SSH 连接信息失败'
  } finally {
    loading.value = false
  }
}

const copyText = async (text: string) => {
  if (!text) return
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.ssh-dialog { min-height: 120px; }
.command-row { display: flex; align-items: center; gap: 8px; width: 100%; }
.command-value { min-width: 0; flex: 1; overflow-wrap: anywhere; font-family: Menlo, Monaco, Consolas, monospace; }
.ssh-tip { margin-top: 12px; }
</style>
