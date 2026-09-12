<template>
  <el-card class="detail-card metrics-panel-card">
    <template #header>
      <div class="metrics-header">
        <span>实时监控</span>
        <div class="metrics-actions">
          <el-radio-group v-model="selectedTimeRange" size="small" @change="handleTimeRangeChange">
            <el-radio-button label="1h">1小时</el-radio-button>
            <el-radio-button label="6h">6小时</el-radio-button>
            <el-radio-button label="24h">24小时</el-radio-button>
            <el-radio-button label="7d">7天</el-radio-button>
          </el-radio-group>
          <el-button
            :icon="Refresh"
            circle
            size="small"
            title="刷新"
            @click="handleRefresh"
          />
        </div>
      </div>
    </template>

    <el-skeleton v-if="loading && !metrics" :rows="6" animated />

    <template v-else-if="metrics">
      <div class="metrics-grid">
        <MetricsChart
          title="GPU 利用率"
          :data="metrics.gpuUtilization"
          unit="%"
          color="#67C23A"
          :y-axis-max="100"
          :y-axis-min="0"
          :height="200"
          :loading="loading"
        />
        <MetricsChart
          title="GPU 显存占用"
          :data="metrics.gpuMemoryUsed"
          :unit="gpuMemoryUnit"
          color="#409EFF"
          :y-axis-max="metrics.gpuMemoryTotal || undefined"
          :y-axis-min="0"
          :height="200"
          :loading="loading"
        />
        <MetricsChart
          title="CPU 使用率"
          :data="metrics.cpuUtilization"
          unit="%"
          color="#E6A23C"
          :y-axis-max="100"
          :y-axis-min="0"
          :height="200"
          :loading="loading"
        />
        <MetricsChart
          title="内存使用"
          :data="metrics.memoryUsed"
          :unit="memoryUnit"
          color="#F56C6C"
          :y-axis-max="metrics.memoryTotal || undefined"
          :y-axis-min="0"
          :height="200"
          :loading="loading"
        />
      </div>
    </template>

    <el-empty v-else description="暂无监控数据" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import MetricsChart from '@/components/MetricsChart/index.vue'
import type { InstanceMetrics } from '@/types/instance'

interface Props {
  metrics: InstanceMetrics | null
  loading: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:timeRange': [range: string]
  refresh: []
}>()

const selectedTimeRange = ref('1h')

const gpuMemoryUnit = 'GB'
const memoryUnit = 'GB'

const handleTimeRangeChange = (val: string | number | boolean) => {
  emit('update:timeRange', String(val))
}

const handleRefresh = () => {
  emit('refresh')
}
</script>

<style scoped lang="scss">
.metrics-panel-card {
  :deep(.el-card__header) {
    padding: 13px;
  }
}

.metrics-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.metrics-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}
</style>
