<template>
  <MainLayout>
    <div class="console-page">
      <!-- 页面标题栏 -->
      <div class="page-header">
        <div class="header-title">
          <DashboardOutlined />
          <h1>控制台</h1>
        </div>
        <a-button type="primary" @click="$router.push('/market')">
          <PlusOutlined />
          新建实例
        </a-button>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon running">
            <PlayCircleOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ runningCount }}</span>
            <span class="stat-label">运行中实例</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stopped">
            <PauseCircleOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stoppedCount }}</span>
            <span class="stat-label">已停止实例</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon balance">
            <WalletOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">¥{{ userStore.balance.toFixed(2) }}</span>
            <span class="stat-label">账户余额</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon cost">
            <DollarOutlined />
          </div>
          <div class="stat-info">
            <span class="stat-value">¥{{ todayCost.toFixed(2) }}</span>
            <span class="stat-label">今日消费</span>
          </div>
        </div>
      </div>

      <!-- 实例列表 -->
      <div class="instances-section">
        <div class="section-header">
          <h2>我的实例</h2>
          <div class="section-actions">
            <a-input-search
              v-model:value="searchKeyword"
              placeholder="搜索实例名称/ID"
              style="width: 200px"
              @search="handleSearch"
            />
            <a-button @click="refreshInstances">
              <ReloadOutlined />
              刷新
            </a-button>
          </div>
        </div>

        <!-- 实例表格 -->
        <a-table
          :dataSource="filteredInstances"
          :columns="columns"
          :loading="loading"
          :pagination="false"
          row-key="id"
          class="instances-table"
        >
          <!-- GPU信息列 -->
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'gpu'">
              <div class="gpu-info-cell">
                <div class="gpu-icon-small" :style="getGPUIconStyle(record.model)">
                  {{ getGPUIcon(record.model) }}
                </div>
                <div class="gpu-detail">
                  <span class="gpu-model">{{ record.model }}</span>
                  <span class="gpu-spec">{{ record.vram }} · {{ record.cpuCores }} · {{ record.memory }}</span>
                </div>
              </div>
            </template>

            <template v-else-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ getStatusText(record.status) }}
              </a-tag>
            </template>

            <template v-else-if="column.key === 'price'">
              <span class="price-tag">¥{{ record.price }}/h</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <div class="action-buttons">
                <a-button
                  v-if="record.status === 'running'"
                  type="primary"
                  size="small"
                  @click="handleStart(record)"
                >
                  启动
                </a-button>

                <a-button
                  v-if="record.status === 'running'"
                  size="small"
                  danger
                  @click="handleStop(record)"
                >
                  停止
                </a-button>

                <a-button
                  v-if="record.status === 'running'"
                  size="small"
                  @click="handleSSH(record)"
                >
                  SSH
                </a-button>

                <a-button
                  v-if="record.status !== 'running'"
                  type="primary"
                  size="small"
                  @click="handleRent(record)"
                >
                  租用
                </a-button>

                <a-dropdown>
                  <a-button size="small">更多 <DownOutlined /></a-button>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item @click="handleRenew(record)">续费</a-menu-item>
                      <a-menu-item @click="handleRebuild(record)">重装系统</a-menu-item>
                      <a-menu-divider />
                      <a-menu-item danger @click="handleDestroy(record)">销毁实例</a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </div>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useGPUStore } from '@/stores/gpu'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/components/layout/MainLayout.vue'
import {
  DashboardOutlined,
  PlusOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  WalletOutlined,
  DollarOutlined,
  ReloadOutlined,
  DownOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const router = useRouter()
const gpuStore = useGPUStore()
const userStore = useUserStore()

const loading = ref(false)
const searchKeyword = ref('')

const runningCount = computed(() =>
  gpuStore.gpuList.filter(item => item.status === 'running').length
)

const stoppedCount = computed(() =>
  gpuStore.gpuList.filter(item => item.status !== 'running').length
)

const todayCost = computed(() => {
  const running = gpuStore.gpuList.filter(item => item.status === 'running')
  return running.reduce((sum, item) => sum + item.price * 24, 0)
})

const filteredInstances = computed(() => {
  let list = [...gpuStore.gpuList]
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(
      item =>
        item.model.toLowerCase().includes(kw) ||
        item.id.toLowerCase().includes(kw)
    )
  }
  return list
})

const columns = [
  { title: 'GPU实例', key: 'gpu', dataIndex: 'model' },
  { title: '状态', key: 'status', dataIndex: 'status' },
  { title: '单价', key: 'price', dataIndex: 'price' },
  { title: '区域', key: 'region', dataIndex: 'region' },
  { title: '操作', key: 'action' },
]

function getGPUIcon(model) {
  if (!model) return 'GPU'
  if (model.includes('4090') || model.includes('3090') || model.includes('4080') || model.includes('3080')) return 'RTX'
  if (model.includes('A100') || model.includes('A10')) return 'A'
  if (model.includes('H100')) return 'H'
  if (model.includes('V100')) return 'V'
  return 'GPU'
}

function getGPUIconStyle(model) {
  if (!model) return {}
  let gradient = 'linear-gradient(135deg, #1677ff, #00d4ff)'
  if (model.includes('4090') || model.includes('3090')) {
    gradient = 'linear-gradient(135deg, #ff6b6b, #ff8e8e)'
  } else if (model.includes('A100') || model.includes('H100')) {
    gradient = 'linear-gradient(135deg, #ffd93d, #ff6b6b)'
  } else if (model.includes('V100')) {
    gradient = 'linear-gradient(135deg, #6bcb77, #4d96ff)'
  }
  return { background: gradient }
}

function getStatusColor(status) {
  const map = {
    available: 'green',
    running: 'blue',
    outofstock: 'default',
  }
  return map[status] || 'default'
}

function getStatusText(status) {
  const map = {
    available: '可用',
    running: '运行中',
    outofstock: '已停止',
  }
  return map[status] || status
}

function handleStart(record) {
  message.info(`启动实例 ${record.id}`)
}

function handleStop(record) {
  gpuStore.stopGPU(record.id)
}

function handleSSH(record) {
  message.info(`SSH连接: ssh root@${record.id}.gpumarket.com`)
}

function handleRent(record) {
  router.push({ path: '/market', query: { rent: record.id } })
}

function handleRenew(record) {
  message.info(`续费实例 ${record.id}`)
}

function handleRebuild(record) {
  message.info(`重装系统 ${record.id}`)
}

function handleDestroy(record) {
  gpuStore.destroyGPU(record.id)
}

function handleSearch() {
  // 搜索逻辑由 filteredInstances 计算属性处理
}

function refreshInstances() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('刷新成功')
  }, 500)
}
</script>

<style scoped>
.console-page {
  padding: 40px 24px;
  max-width: 1440px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.running {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
}

.stat-icon.stopped {
  background: rgba(150, 159, 175, 0.15);
  color: #969faf;
}

.stat-icon.balance {
  background: rgba(22, 119, 255, 0.15);
  color: #1677ff;
}

.stat-icon.cost {
  background: rgba(255, 107, 107, 0.15);
  color: #ff6b6b;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.instances-section {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.section-actions {
  display: flex;
  gap: 12px;
}

.gpu-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.gpu-icon-small {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 11px;
  color: white;
  flex-shrink: 0;
}

.gpu-detail {
  display: flex;
  flex-direction: column;
}

.gpu-model {
  font-weight: 500;
  color: var(--text-primary);
}

.gpu-spec {
  font-size: 12px;
  color: var(--text-secondary);
}

.price-tag {
  color: var(--price-color);
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .console-page {
    padding: 20px 16px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .action-buttons {
    flex-wrap: wrap;
  }
}
</style>
