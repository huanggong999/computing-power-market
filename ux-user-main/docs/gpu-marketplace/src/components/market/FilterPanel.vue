<template>
  <div class="filter-panel">
    <div class="filter-header">
      <FilterOutlined />
      <span>筛选条件</span>
      <a-button type="link" size="small" @click="handleReset">
        重置
      </a-button>
    </div>

    <a-collapse v-model:activeKey="activeKeys" :bordered="false">
      <!-- GPU型号 -->
      <a-collapse-panel key="gpuModel" header="GPU型号">
        <a-checkbox-group
          v-model:value="localFilters.gpuModel"
          :options="gpuModelOptions"
          @change="handleChange"
        />
      </a-collapse-panel>

      <!-- 显存大小 -->
      <a-collapse-panel key="vram" header="显存大小">
        <a-checkbox-group
          v-model:value="localFilters.vram"
          :options="vramOptions"
          @change="handleChange"
        />
      </a-collapse-panel>

      <!-- CPU核心数 -->
      <a-collapse-panel key="cpuCores" header="CPU核心数">
        <a-checkbox-group
          v-model:value="localFilters.cpuCores"
          :options="cpuOptions"
          @change="handleChange"
        />
      </a-collapse-panel>

      <!-- 内存大小 -->
      <a-collapse-panel key="memory" header="内存大小">
        <a-checkbox-group
          v-model:value="localFilters.memory"
          :options="memoryOptions"
          @change="handleChange"
        />
      </a-collapse-panel>

      <!-- 区域 -->
      <a-collapse-panel key="region" header="区域">
        <a-checkbox-group
          v-model:value="localFilters.region"
          :options="regionOptions"
          @change="handleChange"
        />
      </a-collapse-panel>

      <!-- 价格区间 -->
      <a-collapse-panel key="priceRange" header="价格区间">
        <div class="price-slider">
          <a-slider
            v-model:value="localFilters.priceRange"
            range
            :min="0"
            :max="20"
            :step="0.5"
            @change="handleChange"
          />
          <div class="price-range-display">
            <span>¥{{ localFilters.priceRange[0] }}</span>
            <span>-</span>
            <span>¥{{ localFilters.priceRange[1] }}</span>
          </div>
        </div>
      </a-collapse-panel>

      <!-- 可用状态 -->
      <a-collapse-panel key="status" header="可用状态">
        <a-radio-group
          v-model:value="localFilters.status"
          @change="handleChange"
        >
          <a-radio value="all">全部</a-radio>
          <a-radio value="available">可租用</a-radio>
          <a-radio value="running">运行中</a-radio>
        </a-radio-group>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { FilterOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  filters: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['update:filters', 'change', 'reset'])

// 筛选选项
const gpuModelOptions = [
  { label: 'RTX 4090', value: '4090' },
  { label: 'RTX 3090', value: '3090' },
  { label: 'RTX 4080', value: '4080' },
  { label: 'RTX 3080', value: '3080' },
  { label: 'A100', value: 'A100' },
  { label: 'A10', value: 'A10' },
  { label: 'H100', value: 'H100' },
  { label: 'V100', value: 'V100' },
]

const vramOptions = [
  { label: '10GB', value: '10GB' },
  { label: '16GB', value: '16GB' },
  { label: '24GB', value: '24GB' },
  { label: '40GB', value: '40GB' },
  { label: '80GB', value: '80GB' },
]

const cpuOptions = [
  { label: '≤ 32核', value: '32' },
  { label: '33-64核', value: '64' },
  { label: '> 64核', value: '96' },
]

const memoryOptions = [
  { label: '≤ 64GB', value: '64' },
  { label: '65-128GB', value: '128' },
  { label: '129-256GB', value: '256' },
  { label: '> 256GB', value: '512' },
]

const regionOptions = [
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '深圳', value: '深圳' },
  { label: '广州', value: '广州' },
]

// 展开的折叠面板
const activeKeys = ref(['gpuModel', 'vram', 'cpuCores', 'region', 'status'])

// 本地筛选状态
const localFilters = reactive({
  gpuModel: [],
  vram: [],
  cpuCores: [],
  memory: [],
  disk: [],
  region: [],
  priceRange: [0, 20],
  status: 'all',
})

// 同步外部filters到本地
watch(() => props.filters, (newFilters) => {
  Object.assign(localFilters, newFilters)
}, { immediate: true, deep: true })

function handleChange() {
  emit('update:filters', { ...localFilters })
  emit('change', { ...localFilters })
}

function handleReset() {
  localFilters.gpuModel = []
  localFilters.vram = []
  localFilters.cpuCores = []
  localFilters.memory = []
  localFilters.disk = []
  localFilters.region = []
  localFilters.priceRange = [0, 20]
  localFilters.status = 'all'

  emit('update:filters', { ...localFilters })
  emit('reset')
}
</script>

<style scoped>
.filter-panel {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.filter-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--text-primary);
}

.filter-header :deep(.ant-btn-link) {
  margin-left: auto;
  color: var(--primary-color);
}

.filter-panel :deep(.ant-collapse) {
  background: transparent;
}

.filter-panel :deep(.ant-collapse-header) {
  color: var(--text-primary) !important;
  font-weight: 500;
  padding: 12px 20px !important;
}

.filter-panel :deep(.ant-collapse-content) {
  background: transparent;
  border: none;
}

.filter-panel :deep(.ant-collapse-content-box) {
  padding: 0 20px 16px;
}

.filter-panel :deep(.ant-checkbox-group) {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.filter-panel :deep(.ant-checkbox-wrapper) {
  color: var(--text-secondary);
  margin-left: 0;
}

.filter-panel :deep(.ant-checkbox-wrapper:hover) {
  color: var(--text-primary);
}

.filter-panel :deep(.ant-checkbox-checked + span) {
  color: var(--primary-color);
}

.filter-panel :deep(.ant-radio-wrapper) {
  color: var(--text-secondary);
  display: block;
  margin-bottom: 10px;
}

.filter-panel :deep(.ant-radio-wrapper:hover) {
  color: var(--text-primary);
}

.filter-panel :deep(.ant-radio-checked + span) {
  color: var(--primary-color);
}

.price-slider {
  padding: 10px 0;
}

.price-range-display {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: var(--text-secondary);
  font-size: 13px;
}

.filter-panel :deep(.ant-slider) {
  margin: 10px 0;
}

.filter-panel :deep(.ant-slider-track) {
  background: var(--primary-color);
}

.filter-panel :deep(.ant-slider-handle) {
  border-color: var(--primary-color);
  background: var(--bg-card);
}

.filter-panel :deep(.ant-slider-rail) {
  background: var(--border-color);
}
</style>
