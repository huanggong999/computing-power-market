<template>
  <section class="volcano-catalog" v-loading="loading">
    <div class="catalog-header">
      <div>
        <div class="catalog-title">GPU 资源</div>
        <div class="catalog-subtitle">实时目录展示，仅供查看，不参与当前官网下单</div>
      </div>
      <div class="catalog-header-actions">
        <span v-if="catalog.updatedAt" class="updated-at">更新于 {{ formatTime(catalog.updatedAt) }}</span>
        <el-button link type="primary" @click="loadCatalog">刷新</el-button>
      </div>
    </div>

    <el-alert
      v-if="errorMessage"
      class="catalog-alert"
      type="error"
      :title="errorMessage"
      show-icon
      :closable="false"
    />
    <el-alert
      v-else-if="catalog.pricing?.status === 'unavailable'"
      class="catalog-alert"
      type="warning"
      :title="catalog.pricing?.error || '价格暂不可用，GPU 规格仍可查看'"
      show-icon
      :closable="false"
    />

    <div class="catalog-meta">
      <span>数据来源：{{ sourceLabel }}</span>
      <span>地域：{{ regions.length }}</span>
      <span>实例规格：{{ instanceTotal }}</span>
      <span v-if="catalog.date">快照日期：{{ catalog.date }}</span>
    </div>

    <div class="catalog-filters">
      <el-select v-model="filters.region" clearable placeholder="全部地域">
        <el-option v-for="item in regionOptions" :key="item" :label="item" :value="item" />
      </el-select>
      <el-select v-model="filters.model" clearable filterable placeholder="全部 GPU 型号">
        <el-option v-for="item in modelOptions" :key="item" :label="item" :value="item" />
      </el-select>
      <el-select v-model="filters.count" clearable placeholder="全部 GPU 卡数">
        <el-option v-for="item in countOptions" :key="item" :label="`${item} 卡`" :value="String(item)" />
      </el-select>
      <el-input v-model="filters.keyword" clearable placeholder="搜索实例规格" />
    </div>

    <el-collapse v-if="filteredRegions.length" v-model="activeRegions">
      <el-collapse-item v-for="region in filteredRegions" :key="region.region" :name="region.region">
        <template #title>
          <div class="region-title">
            <span>{{ region.region || '未知地域' }}</span>
            <el-tag v-if="region.error" size="small" type="danger">异常</el-tag>
            <el-tag v-else size="small" type="success">{{ region.gpuSpecs?.length || 0 }} 个 GPU 规格</el-tag>
          </div>
        </template>

        <el-alert v-if="region.error" type="warning" :title="region.error" :closable="false" show-icon />
        <div v-else class="spec-list">
          <el-card v-for="spec in region.gpuSpecs || []" :key="`${spec.gpuModel}-${spec.gpuMemory}`" shadow="never" class="spec-card">
            <template #header>
              <div class="spec-title">
                <strong>{{ spec.gpuModel || '未知型号' }}</strong>
                <el-tag size="small">{{ spec.gpuMemory || '显存未知' }}</el-tag>
                <span>卡数：{{ countsText(spec.gpuCounts) }}</span>
              </div>
            </template>
            <el-table :data="spec.instanceTypes || []" border size="small">
              <el-table-column prop="instanceTypeId" label="实例规格" min-width="210" show-overflow-tooltip />
              <el-table-column prop="gpuCount" label="GPU卡数" width="90" />
              <el-table-column prop="cpuCores" label="CPU核数" width="90" />
              <el-table-column label="内存(GiB)" width="110">
                <template #default="{ row }">{{ valueText(row.memGib) }}</template>
              </el-table-column>
              <el-table-column label="按量/小时" width="120">
                <template #default="{ row }">{{ priceText(row.price) }}</template>
              </el-table-column>
              <el-table-column label="包月" width="120">
                <template #default="{ row }">{{ priceText(row.priceMonthly) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-collapse-item>
    </el-collapse>
    <el-empty v-else description="暂无符合条件的 GPU 资源" />
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { getVolcanoGpuCatalog, VolcanoGpuCatalog, VolcanoGpuSpec, VolcanoInstanceType, VolcanoPrice, VolcanoRegion } from '@/api/volcanoGpu'

const loading = ref(false)
const errorMessage = ref('')
const catalog = ref<VolcanoGpuCatalog>({ regions: [] })
const activeRegions = ref<string[]>([])
const filters = reactive({ region: '', model: '', count: '', keyword: '' })

const regions = computed(() => catalog.value.regions || [])
const regionOptions = computed(() => regions.value.map((item) => item.region || '').filter(Boolean))
const modelOptions = computed(() => Array.from(new Set(regions.value.flatMap((region) => specsOf(region).map((spec) => spec.gpuModel || '')).filter(Boolean))).sort())
const countOptions = computed(() => Array.from(new Set(regions.value.flatMap((region) => specsOf(region).flatMap((spec) => [
  ...(Array.isArray(spec.gpuCounts) ? spec.gpuCounts : []),
  ...instancesOf(spec).map((item) => item.gpuCount),
])))).filter((item) => Number.isFinite(Number(item))).sort((a, b) => Number(a) - Number(b)))
const instanceTotal = computed(() => regions.value.reduce((total, region) => total + specsOf(region).reduce((sum, spec) => sum + instancesOf(spec).length, 0), 0))
const filteredRegions = computed(() => regions.value.map((region) => {
  const gpuSpecs = specsOf(region).map((spec) => ({
    ...spec,
    instanceTypes: instancesOf(spec).filter((item) =>
      (!filters.count || Number(item.gpuCount) === Number(filters.count)) &&
      (!filters.keyword || String(item.instanceTypeId || '').toLowerCase().includes(filters.keyword.trim().toLowerCase())),
    ),
  })).filter((spec) =>
    (!filters.model || spec.gpuModel === filters.model) &&
    (!filters.count || instancesOf(spec).length > 0) &&
    (!filters.keyword || instancesOf(spec).length > 0),
  )
  return { ...region, gpuSpecs }
}).filter((region) => (!filters.region || region.region === filters.region) && (Boolean(region.error) || (region.gpuSpecs || []).length > 0)))

const sourceLabel = computed(() => catalog.value.source === 'snapshot' ? '每日快照' : catalog.value.source === 'live' ? '实时目录' : catalog.value.source || '--')

async function loadCatalog() {
  loading.value = true
  errorMessage.value = ''
  try {
    const response = await getVolcanoGpuCatalog()
    const data = response?.data || {}
    catalog.value = { ...data, regions: Array.isArray(data.regions) ? data.regions : [] }
    const first = regions.value.find((item) => !item.error)?.region
    activeRegions.value = first ? [first] : []
    if (data.success === false) errorMessage.value = data.message || 'GPU 资源获取失败'
  } catch (error: any) {
    errorMessage.value = error?.message || 'GPU 资源获取失败'
    catalog.value = { regions: [] }
  } finally {
    loading.value = false
  }
}

const specsOf = (region: VolcanoRegion) => Array.isArray(region.gpuSpecs) ? region.gpuSpecs : []
const instancesOf = (spec: VolcanoGpuSpec) => Array.isArray(spec.instanceTypes) ? spec.instanceTypes : []
const countsText = (counts?: number[]) => Array.isArray(counts) && counts.length ? counts.join(' / ') : '--'
const valueText = (value: unknown) => value === null || value === undefined || value === '' ? '--' : String(value)
const priceText = (value: VolcanoPrice | number | null | undefined) => {
  if (value === null || value === undefined) return '--'
  if (typeof value === 'number') return `¥${value}`
  const unitPrice = value.unitPrice ?? value.unit_price
  if (value.available === false || unitPrice === null || unitPrice === undefined) return '未开通'
  return `¥${unitPrice}`
}
const formatTime = (value?: string) => value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '--'

onMounted(loadCatalog)
</script>

<style scoped lang="scss">
.volcano-catalog { margin: 16px auto 0; width: min(1540px, calc(100vw - 48px)); border: 1px solid #f1d3a1; background: #fffdf8; }
.catalog-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 20px 24px; border-bottom: 1px solid #f5dfba; }
.catalog-title { color: #111827; font-size: 18px; font-weight: 700; }
.catalog-subtitle, .updated-at, .catalog-meta { color: #7b8494; font-size: 13px; }
.catalog-subtitle { margin-top: 6px; }
.catalog-header-actions, .catalog-meta, .catalog-filters, .region-title, .spec-title { display: flex; align-items: center; gap: 12px; }
.catalog-alert { margin: 12px 24px 0; }
.catalog-meta { flex-wrap: wrap; padding: 14px 24px 0; }
.catalog-filters { padding: 14px 24px; }
.catalog-filters :deep(.el-select) { width: 180px; }
.catalog-filters :deep(.el-input) { width: 240px; }
.volcano-catalog > :deep(.el-collapse) { padding: 0 24px 12px; border-top: 0; }
.region-title { width: 100%; justify-content: space-between; padding-right: 12px; }
.spec-list { display: flex; flex-direction: column; gap: 12px; padding-bottom: 8px; }
.spec-card { border-color: #f5dfba; }
.spec-title span:last-child { margin-left: auto; color: #7b8494; font-size: 13px; }
@media (max-width: 768px) {
  .volcano-catalog { width: calc(100vw - 24px); }
  .catalog-header { align-items: flex-start; flex-direction: column; }
  .catalog-filters { align-items: stretch; flex-wrap: wrap; }
  .catalog-filters :deep(.el-select), .catalog-filters :deep(.el-input) { width: 100%; }
}
</style>
