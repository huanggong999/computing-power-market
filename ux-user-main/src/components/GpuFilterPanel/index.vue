<template>
  <div class="filter-panel-top">
    <!-- 计费方式 -->
    <div class="filter-row">
      <span class="filter-label">计费方式:</span>
      <el-radio-group v-model="localFilters.billingType" @change="handleChange">
        <el-radio-button
          v-for="item in billingTypeOptions"
          :key="item.code"
          :label="item.code"
        >
          {{ item.name }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 选择地区 -->
    <div class="filter-row">
      <span class="filter-label">选择地区:</span>
      <el-radio-group v-model="localFilters.region" @change="handleRegionChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button
          v-for="item in regionOptions"
          :key="item.regionCode"
          :label="item.regionCode"
        >
          {{ item.regionName }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 专区 -->
    <div class="filter-row" v-if="zoneOptions.length > 0">
      <span class="filter-label"></span>
      <el-radio-group v-model="localFilters.zone" @change="handleChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button
          v-for="item in zoneOptions"
          :key="item.zoneCode"
          :label="item.zoneCode"
        >
          {{ item.zoneName }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- GPU型号 -->
    <div class="filter-row gpu-model-row">
      <span class="filter-label">GPU型号:</span>
      <div class="gpu-model-options">
        <el-checkbox v-model="allGpuModel" @change="handleAllGpuChange">全部</el-checkbox>
        <el-checkbox-group v-model="localFilters.gpuModel" @change="handleChange">
          <el-checkbox v-for="opt in gpuModelOptions" :key="opt.model" :label="opt.model">
            {{ opt.model }} ({{ opt.availableCount }}/{{ opt.totalCount }})
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </div>

    <!-- GPU数量 -->
    <div class="filter-row">
      <span class="filter-label">GPU数量:</span>
      <el-radio-group v-model="localFilters.gpuCount" @change="handleChange">
        <el-radio-button :label="undefined">全部</el-radio-button>
        <el-radio-button v-for="n in gpuCountOptions" :key="n" :label="n">
          {{ n }}
        </el-radio-button>
      </el-radio-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { getGpuMarketMeta, GpuMarketMeta, GpuRegionItem, GpuZoneItem, GpuModelStat, GpuBillingType } from '@/api/gpuMarket'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  filters: Record<string, any>
}>()

const emit = defineEmits(['update:filters', 'change', 'reset'])

// 筛选项数据
const regionOptions = ref<GpuRegionItem[]>([])
const zoneOptions = ref<GpuZoneItem[]>([])
const gpuModelOptions = ref<GpuModelStat[]>([])
const gpuCountOptions = ref<number[]>([])
const billingTypeOptions = ref<GpuBillingType[]>([])

const allGpuModel = ref(false)

const localFilters = reactive({
  billingType: 'hourly',
  region: '',
  zone: '',
  gpuModel: [] as string[],
  gpuCount: undefined as number | undefined,
})

// 初始化筛选项数据
async function loadMetaData() {
  try {
    const res = await getGpuMarketMeta()
    if (res.code === 200 && res.data) {
      const meta = res.data
      regionOptions.value = meta.regions || []
      zoneOptions.value = meta.zones || []
      gpuModelOptions.value = meta.gpuModels || []
      gpuCountOptions.value = meta.gpuCounts || []
      billingTypeOptions.value = meta.billingTypes || []

      // 设置默认值
      if (billingTypeOptions.value.length > 0 && !localFilters.billingType) {
        localFilters.billingType = billingTypeOptions.value[0].code
      }
    }
  } catch (error) {
    console.error('加载筛选项数据失败:', error)
    ElMessage.error('加载筛选项数据失败')
  }
}

onMounted(() => {
  loadMetaData()
})

watch(() => props.filters, (newFilters) => {
  Object.assign(localFilters, newFilters)
}, { immediate: true, deep: true })

function handleChange() {
  emit('update:filters', { ...localFilters })
  emit('change', { ...localFilters })
}

function handleRegionChange() {
  localFilters.zone = ''
  handleChange()
}

function handleAllGpuChange(val: boolean) {
  if (val) {
    localFilters.gpuModel = gpuModelOptions.value.map(opt => opt.model)
  } else {
    localFilters.gpuModel = []
  }
  handleChange()
}
</script>

<style scoped lang="scss">
.filter-panel-top {
  background: #fff;
  border-radius: 6px;
  padding: 12px 16px;
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.filter-label {
  flex: 0 0 80px;
  padding-top: 7px;
  font-size: 16px;
  color: #606266;
  line-height: 22px;
  white-space: nowrap;
  text-align: right;
}

.gpu-model-options {
  display: flex;
  flex: 1;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 10px;
}

:deep(.el-radio-group) {
  display: flex;
  flex: 1;
  flex-wrap: wrap;
  gap: 8px 10px;
}

:deep(.el-radio-button) {
  margin: 0;
}

:deep(.el-radio-button__inner) {
  min-width: 58px;
  height: 38px;
  padding: 0 16px;
  border: 1px solid transparent;
  border-radius: 4px !important;
  background: #f5f7fa;
  color: #606266;
  font-size: 16px;
  line-height: 36px;
  box-shadow: none !important;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

:deep(.el-checkbox-group) {
  display: contents;
}

:deep(.el-checkbox) {
  height: 38px;
  margin-right: 0;
  padding: 0 16px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: #f5f7fa;
  color: #606266;
  font-size: 16px;
  line-height: 36px;
}

:deep(.el-checkbox__input) {
  display: none;
}

:deep(.el-checkbox__label) {
  padding-left: 0;
  color: inherit;
  font-size: 16px;
}

:deep(.el-checkbox.is-checked) {
  border-color: #409eff;
  background: #ecf5ff;
  color: #409eff;
}
</style>
