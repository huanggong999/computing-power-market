<template>
  <aside
    class="instance-detail-sidebar"
    role="dialog"
    :aria-label="instance?.name || '实例详情'"
  >
      <header class="drawer-hero">
        <div class="hero-main">
          <div class="hero-kicker">实例详情</div>
          <h3>{{ instance?.name || '未命名实例' }}</h3>
          <div class="hero-meta">
            <StatusTag v-if="instance" :status="instance.status" size="small" />
            <span>{{ instance?.region || instance?.regionCode || '-' }}</span>
            <span>{{ instance?.billingType === 'hourly' ? '按量计费' : '包年包月' }}</span>
          </div>
        </div>
        <button v-if="showClose" type="button" class="drawer-close-button" @click.stop="closePanel">关闭</button>
      </header>

      <div v-if="instance" class="drawer-body">
        <div class="summary-strip">
          <div class="summary-item">
            <span>GPU</span>
            <strong>{{ instance.gpuType || '-' }} × {{ instance.gpuCount || 0 }}卡</strong>
          </div>
          <div class="summary-item">
            <span>CPU</span>
            <strong>{{ instance.cpuCores || 0 }} vCPU</strong>
          </div>
          <div class="summary-item">
            <span>内存</span>
            <strong>{{ instance.memory || 0 }} GB</strong>
          </div>
          <div class="summary-item">
            <span>到期时间</span>
            <strong>{{ instance.expiredAt || '-' }}</strong>
          </div>
        </div>

        <section class="detail-section">
          <div class="section-title">基本信息</div>
          <div class="info-grid">
            <div class="info-item">
              <span>实例ID</span>
              <strong>{{ instance.id || '-' }}</strong>
            </div>
            <div class="info-item">
              <span>UUID</span>
              <strong>{{ instance.uuid || '-' }}</strong>
            </div>
            <div class="info-item">
              <span>创建时间</span>
              <strong>{{ instance.createdAt || '-' }}</strong>
            </div>
            <div class="info-item">
              <span>状态说明</span>
              <strong>{{ instance.statusMessage || '-' }}</strong>
            </div>
          </div>
        </section>

        <section class="detail-section">
          <div class="section-title">规格与存储</div>
          <div class="resource-grid">
            <div class="resource-tile accent-gpu">
              <span>GPU 显存</span>
              <strong>{{ instance.gpuMemory || 0 }} GB</strong>
              <small>{{ instance.gpuType || '-' }}</small>
            </div>
            <div class="resource-tile">
              <span>系统盘</span>
              <strong>{{ instance.systemDisk || instance.systemDiskSize || 0 }} GB</strong>
              <small>使用率 {{ diskUsage(instance.diskUsage?.systemDiskUsage) }}%</small>
            </div>
            <div class="resource-tile">
              <span>数据盘</span>
              <strong>{{ instance.dataDisk || instance.dataDiskSize || 0 }} GB</strong>
              <small>使用率 {{ diskUsage(instance.diskUsage?.dataDiskUsage) }}%</small>
            </div>
          </div>
        </section>

        <section class="detail-section">
          <div class="section-title">连接信息</div>
          <div class="connect-list" v-loading="sshLoading">
            <div class="connect-row">
              <span>内网IP</span>
              <strong>{{ instance.privateIp || '-' }}</strong>
            </div>
            <div class="connect-row">
              <span>公网IP</span>
              <strong>{{ instance.publicIp || sshInfo?.host || '-' }}</strong>
            </div>
            <div class="connect-row">
              <span>SSH端口</span>
              <strong>{{ instance.sshPort || sshInfo?.port || 22 }}</strong>
            </div>
            <div v-if="showConnectionCard" class="connect-row command-row">
              <span>登录指令</span>
              <strong>{{ sshCommand || '-' }}</strong>
              <el-button v-if="sshCopyText" link type="primary" size="small" @click="copyToClipboard(sshCopyText)">
                复制
              </el-button>
            </div>
            <div v-if="sshPassword" class="connect-row">
              <span>登录密码</span>
              <strong>{{ showPassword ? sshPassword : '********' }}</strong>
              <el-button link type="primary" size="small" @click="showPassword = !showPassword">
                {{ showPassword ? '隐藏' : '显示' }}
              </el-button>
            </div>
            <div v-if="instance.jupyterUrl" class="connect-row">
              <span>JupyterLab</span>
              <strong>{{ instance.jupyterUrl }}</strong>
              <el-button link type="primary" size="small" @click="openJupyter(instance.jupyterUrl)">
                打开
              </el-button>
            </div>
          </div>
        </section>

        <section class="detail-section action-section">
          <div class="section-title">快捷操作</div>
          <div class="quick-actions">
            <el-button v-if="instance.status === 'stopped'" type="success" @click="handleAction('start')">
              <el-icon><VideoPlay /></el-icon>
              开机
            </el-button>
            <el-button v-else-if="instance.status === 'running'" type="danger" @click="handleAction('stop')">
              <el-icon><VideoPause /></el-icon>
              关机
            </el-button>
            <el-button type="warning" @click="handleAction('restart')">
              <el-icon><Refresh /></el-icon>
              重启
            </el-button>
            <el-popconfirm
              title="确定要释放该实例吗？释放后数据不可恢复！"
              confirm-button-text="确定"
              cancel-button-text="取消"
              type="warning"
              @confirm="handleAction('release')"
            >
              <template #reference>
                <el-button type="info">
                  <el-icon><Delete /></el-icon>
                  释放
                </el-button>
              </template>
            </el-popconfirm>
          </div>
        </section>

        <MetricsPanel
          :metrics="metrics"
          :loading="metricsLoading"
          @update:timeRange="handleTimeRangeChange"
          @refresh="handleRefresh"
        />
      </div>
      <div v-else class="drawer-empty">
        请选择实例查看详情
      </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import {
  VideoPlay,
  VideoPause,
  Refresh,
  Delete
} from '@element-plus/icons-vue'
import StatusTag from './StatusTag.vue'
import MetricsPanel from './MetricsPanel.vue'
import { useInstanceStore } from '@/store/modules/useInstance'
import type { Instance, InstanceMetrics, SshInfo } from '@/types/instance'

interface Props {
  modelValue: boolean
  instance: Instance | null
  metrics: InstanceMetrics | null
  metricsLoading: boolean
  showClose?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showClose: true
})
const instanceStore = useInstanceStore()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  refresh: []
  closed: []
  'update:timeRange': [range: string]
  action: [action: string, instance: Instance]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const showPassword = ref(false)
const sshInfo = ref<SshInfo | null>(null)
const sshLoading = ref(false)

const sshCommand = computed(() => {
  if (sshInfo.value?.command) return sshInfo.value.command
  const { host, port, username } = sshInfo.value || {}
  if (host && port && username) {
    return `ssh -p ${port} ${username}@${host}`
  }
  return ''
})

const sshPassword = computed(() => sshInfo.value?.password || '')
const showConnectionCard = computed(() => Boolean(sshLoading.value || sshCommand.value || sshPassword.value || props.instance?.jupyterUrl))
const sshCopyText = computed(() => {
  if (!sshCommand.value) return ''
  return sshPassword.value ? `${sshCommand.value}\n${sshPassword.value}` : sshCommand.value
})

const diskUsage = (usage?: number) => Math.max(0, Math.min(100, Math.round(Number(usage || 0))))

const fetchSshInfo = async (id: string) => {
  sshLoading.value = true
  sshInfo.value = null
  showPassword.value = false
  try {
    const res = await instanceStore.fetchInstanceSshInfo(id)
    sshInfo.value = res.data || null
  } catch (error) {
    sshInfo.value = null
  } finally {
    sshLoading.value = false
  }
}

watch(
  () => [props.modelValue, props.instance?.id] as const,
  ([visible, id]) => {
    if (visible && id) {
      fetchSshInfo(id)
    } else {
      sshInfo.value = null
      showPassword.value = false
    }
  },
  { immediate: true }
)

const copyToClipboard = (text: string) => {
  if (!text) return
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const closePanel = () => {
  visible.value = false
  emit('closed')
}

const openJupyter = (url: string) => {
  if (url) {
    window.open(url, '_blank')
  }
}

const handleAction = (action: string) => {
  const instance = props.instance
  if (instance) {
    closePanel()
    emit('action', action, instance)
  }
}

const handleTimeRangeChange = (range: string) => {
  emit('update:timeRange', range)
}

const handleRefresh = () => {
  emit('refresh')
}
</script>

<style scoped lang="scss">
.instance-detail-sidebar {
  width: min(1120px, calc(100vw - 64px));
  max-height: min(780px, calc(100vh - 64px));
  background: #fffaf0;
  border: 1px solid #f0c36d;
  border-radius: 8px;
  box-shadow: 0 24px 64px rgb(45 31 6 / 24%);
  overflow: hidden;
}

.drawer-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  min-height: 116px;
  padding: 24px 32px 20px;
  background: linear-gradient(135deg, #fff2cc 0%, #ffe1a6 45%, #f7b955 100%);
  border-bottom: 1px solid #efc46c;
  color: #3d2a0a;
}

.hero-main {
  min-width: 0;
}

.hero-kicker {
  margin-bottom: 8px;
  color: #8a5b00;
  font-size: 12px;
  font-weight: 600;
}

.drawer-hero h3 {
  margin: 0;
  color: #221700;
  font-size: 22px;
  font-weight: 650;
  line-height: 1.3;
  word-break: break-word;
}

.hero-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
  color: #6b4a12;
  font-size: 12px;
}

.drawer-close-button {
  flex: 0 0 auto;
  height: 30px;
  padding: 0 14px;
  border: 1px solid rgb(128 82 0 / 34%);
  border-radius: 4px;
  background: rgb(255 255 255 / 74%);
  color: #4d3409;
  cursor: pointer;
  font-size: 12px;
  line-height: 28px;
}

.drawer-close-button:hover {
  background: #fff;
  border-color: #c47a00;
  color: #a16000;
}

.drawer-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(260px, 340px);
  align-items: start;
  gap: 12px;
  max-height: calc(min(780px, 100vh - 64px) - 116px);
  padding: 14px 18px 18px;
  overflow-y: auto;
}

.drawer-empty {
  padding: 24px;
  color: #8a6f3b;
  font-size: 13px;
}

.summary-strip {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
}

.summary-item,
.detail-section {
  background: #fff;
  border: 1px solid #f0dfbd;
  border-radius: 6px;
  box-shadow: 0 6px 18px rgb(116 76 13 / 6%);
}

.summary-item {
  min-width: 0;
  padding: 9px 10px;
}

.summary-item span,
.info-item span,
.resource-tile span,
.connect-row span {
  display: block;
  color: #8a6f3b;
  font-size: 12px;
}

.summary-item strong,
.info-item strong,
.resource-tile strong,
.connect-row strong {
  display: block;
  margin-top: 3px;
  color: #1f2937;
  font-size: 12px;
  font-weight: 600;
  overflow-wrap: anywhere;
}

.detail-section {
  padding: 12px;
}

.detail-section.action-section,
:deep(.metrics-panel-card) {
  grid-column: 1 / -1;
}

.section-title {
  margin-bottom: 8px;
  color: #3d2a0a;
  font-size: 14px;
  font-weight: 650;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px 12px;
}

.info-item {
  min-width: 0;
  padding: 8px 10px;
  background: #fffdf8;
  border: 1px solid #f3ead8;
  border-radius: 4px;
}

.resource-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.resource-tile {
  min-width: 0;
  padding: 9px 10px;
  border: 1px solid #f2e3c2;
  border-radius: 6px;
  background: #fffdf8;
}

.resource-tile.accent-gpu {
  background: #fff4d8;
  border-color: #e8bd58;
}

.resource-tile small {
  display: block;
  margin-top: 4px;
  color: #8a6f3b;
  font-size: 12px;
  overflow-wrap: anywhere;
}

.connect-list {
  display: grid;
  gap: 8px;
}

.connect-row {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  min-height: 30px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f3ead8;
}

.connect-row:last-child {
  padding-bottom: 0;
  border-bottom: 0;
}

.connect-row strong {
  margin-top: 0;
  font-family: Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
}

.quick-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  min-width: 82px;
}

:deep(.metrics-panel-card) {
  margin-bottom: 0;
  border: 1px solid #f0dfbd;
  border-radius: 6px;
  box-shadow: 0 6px 18px rgb(116 76 13 / 6%);
}

@media (max-width: 980px) {
  .instance-detail-sidebar {
    width: calc(100vw - 28px);
    max-height: calc(100vh - 28px);
  }

  .drawer-body {
    max-height: calc(100vh - 158px);
  }

  .info-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .drawer-body {
    grid-template-columns: 1fr;
  }

  .summary-strip,
  .info-grid {
    grid-template-columns: 1fr;
  }

  .connect-row {
    grid-template-columns: 1fr;
  }
}
</style>
