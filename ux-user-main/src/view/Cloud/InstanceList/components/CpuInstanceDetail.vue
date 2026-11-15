<template>
  <aside class="cpu-detail-panel" v-if="instance">
    <header>
      <div>
        <h3>{{ instance.instance_name || instance.name || instance.instance_id || instance.operation_id || instance.id }}</h3>
        <span>{{ instance.instance_id || instance.operation_id || instance.id || '-' }}</span>
      </div>
      <button type="button" @click="$emit('close')">×</button>
    </header>

    <el-alert
      v-if="!instanceId"
      class="creating-tip"
      :title="isAbnormal ? '创建任务未成功，请查看失败原因后清除异常记录。' : '实例正在创建中，拿到实例 ID 后才可执行开机、关机、SSH 等操作。'"
      :type="isAbnormal ? 'warning' : 'info'"
      :closable="false"
      show-icon
    />

    <el-descriptions :column="1" border>
      <el-descriptions-item v-if="!instanceId && instance.operation_id" label="创建任务">
        {{ instance.operation_id }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="cpuStatusInfo(instance.status).type as any">{{ cpuStatusInfo(instance.status).label }}</el-tag>
        <div v-if="instance.status_message || instance.message" class="message">{{ instance.status_message || instance.message }}</div>
      </el-descriptions-item>
      <el-descriptions-item label="地域 / 可用区">{{ instance.region || '-' }} / {{ instance.zone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="CPU / 内存">{{ instance.cpu_cores || '-' }} 核 / {{ instance.memory || '-' }}</el-descriptions-item>
      <el-descriptions-item label="系统盘 / 数据盘">{{ instance.system_disk || '-' }} / {{ instance.data_disk || '无' }}</el-descriptions-item>
      <el-descriptions-item label="网络">公网 {{ instance.public_ip || '-' }}；内网 {{ instance.private_ip || '-' }}</el-descriptions-item>
      <el-descriptions-item label="SSH">
        <template v-if="sshInfo.host">
          <div class="ssh-row">
            <span>{{ sshInfo.host }}:{{ sshInfo.port || 22 }}</span>
            <el-button link type="primary" size="small" @click="copySshCommand">复制指令</el-button>
          </div>
        </template>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="计费">{{ billing(instance.billing_mode) }}；到期 {{ instance.expire_time || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ instance.create_time || '-' }}</el-descriptions-item>
    </el-descriptions>

    <section class="detail-section">
      <div class="section-title">快捷操作</div>
      <div class="detail-actions">
        <el-tooltip
          v-for="item in quickActions"
          :key="item.key"
          :content="item.reason"
          :disabled="!item.reason"
          placement="top"
        >
          <span class="action-item">
            <el-button
              :type="item.type"
              :plain="item.plain"
              :disabled="!item.enabled"
              @click="$emit('action', item.key, instance)"
            >
              {{ item.label }}
            </el-button>
          </span>
        </el-tooltip>
        <el-tooltip :content="releaseState.reason" :disabled="releaseState.enabled" placement="top">
          <span class="action-item">
            <el-button type="danger" :disabled="!releaseState.enabled" @click="$emit('action', 'release', instance)">释放实例</el-button>
          </span>
        </el-tooltip>
      </div>
      <div class="actions-source">
        {{ instanceId ? '操作可用性以 CPU 实例详情接口返回的 actions 为准，状态变化后请刷新。' : (isAbnormal ? '创建任务未成功，云上没有可操作的实例，请清除异常记录。' : '创建任务尚未拿到云上实例 ID，请等待创建完成后再操作。') }}
      </div>
    </section>

    <section v-if="isAbnormal" class="detail-section">
      <div class="section-title">异常处理</div>
      <div class="detail-actions">
        <el-button type="danger" plain @click="$emit('action', 'clear', instance)">清除异常记录</el-button>
      </div>
    </section>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { CpuInstance } from '@/types/instance'
import { cpuActionState, cpuCloudInstanceId, cpuStatusInfo } from '@/utils/instanceSource'

const props = defineProps<{ instance: CpuInstance | null }>()
defineEmits<{ close: []; action: [string, CpuInstance] }>()

const billing = (value?: string) => ({ on_demand: '按量付费', monthly: '包月', yearly: '包年' }[value || ''] || value || '-')
const instanceId = computed(() => cpuCloudInstanceId(props.instance))
const isAbnormal = computed(() => ['failed', 'error'].includes(cpuStatusInfo(props.instance?.status).value))
const sshInfo = computed(() => props.instance?.ssh_info || {})
const sshCommand = computed(() => {
  const info = sshInfo.value
  if (info.command) return info.command
  if (!info.host) return ''
  return `ssh -p ${info.port || 22} ${info.username || 'root'}@${info.host}`
})

const quickActions = computed(() => {
  const instance = props.instance
  if (!instance) return []
  const definitions: Array<{
    key: string
    label: string
    type: 'success' | 'danger' | 'warning' | 'primary'
    plain?: boolean
  }> = [
    { key: 'start', label: '开机', type: 'success' },
    { key: 'stop', label: '关机', type: 'danger' },
    { key: 'restart', label: '重启', type: 'warning' },
    { key: 'ssh', label: 'SSH 连接', type: 'primary', plain: true },
    { key: 'resetPassword', label: '重置密码', type: 'primary', plain: true },
    { key: 'rename', label: '修改名称', type: 'primary', plain: true },
    { key: 'resize', label: '资源变更', type: 'primary', plain: true },
  ]
  return definitions.map(item => {
    const state = cpuActionState(instance, item.key)
    return { ...item, enabled: state.enabled, reason: state.enabled ? '' : (state.reason || '当前状态不支持此操作') }
  })
})

const releaseState = computed(() => {
  if (!props.instance) return { enabled: false, reason: '' }
  return cpuActionState(props.instance, 'release')
})

const copySshCommand = async () => {
  if (!sshCommand.value) return
  try {
    await navigator.clipboard.writeText(sshCommand.value)
    ElMessage.success('SSH 登录指令已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.cpu-detail-panel { width:min(560px, calc(100vw - 40px)); max-height:calc(100vh - 64px); overflow:auto; padding:22px; border-radius:8px; background:#fffdf8; box-shadow:0 18px 48px rgb(47 34 11 / 25%); }
.cpu-detail-panel header { display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:18px; }
.cpu-detail-panel h3 { margin:0 0 5px; color:#2f220b; }
.cpu-detail-panel header span,.message { color:#909399; font-size:12px; }
.cpu-detail-panel header button { border:0; background:none; font-size:24px; cursor:pointer; color:#8a6f3b; }
.creating-tip { margin-bottom:14px; }
.ssh-row { display:flex; align-items:center; gap:8px; flex-wrap:wrap; }
.detail-section { margin-top:20px; padding-top:16px; border-top:1px solid #f0e5cf; }
.section-title { margin-bottom:10px; color:#2f220b; font-size:14px; font-weight:650; }
.detail-actions { display:flex; align-items:center; flex-wrap:wrap; gap:8px; }
.action-item { display:inline-flex; }
.detail-actions :deep(.el-button) { margin-left:0; }
.actions-source { margin-top:10px; color:#909399; font-size:12px; line-height:1.5; }
</style>
