<template>
  <div class="purchase-page ignore-purchase-page">
    <div class="product-heading">
      <div class="product-name">算力服务器 GPU</div>
      <div class="product-mode">自定义购买</div>
    </div>

    <div class="purchase-shell">
      <main class="config-panel">
        <section class="config-section">
          <div class="section-title">基础配置</div>

          <div class="config-row">
            <div class="row-label">
              <strong>付费类型</strong>
              <span>选择资源计费方式</span>
            </div>
            <div class="row-control option-grid three">
              <button
                v-for="item in billingTypeOptions"
                :key="item.code"
                class="option-card"
                :class="{ active: filters.billingType === item.code }"
                @click="setBillingType(item.code)"
              >
                <strong>{{ item.name }}</strong>
                <span>{{ billingDescription(item.code) }}</span>
              </button>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label">
              <strong>地域</strong>
              <span>不同地域内网互不相通</span>
            </div>
            <div class="row-control">
              <div class="option-grid region-grid">
                <button
                  class="option-chip"
                  :class="{ active: filters.region === '' }"
                  @click="setRegion('')"
                >
                  全部地域
                </button>
                <button
                  v-for="item in regionOptions"
                  :key="item.regionCode"
                  class="option-chip"
                  :class="{ active: filters.region === item.regionCode }"
                  @click="setRegion(item.regionCode)"
                >
                  {{ item.regionName }}
                </button>
              </div>
              <div class="hint-line">
                <el-icon><InfoFilled /></el-icon>
                实例创建后地域无法变更，建议选择距离用户或业务系统更近的地域。
              </div>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label">
              <strong>网络及可用区</strong>
              <span>选择专区或可用区</span>
            </div>
            <div class="row-control inline-selects">
              <el-select v-model="filters.zone" placeholder="请选择专区" clearable @change="reloadFromFirstPage">
                <el-option label="全部专区" value="" />
                <el-option
                  v-for="item in filteredZoneOptions"
                  :key="item.zoneCode"
                  :label="item.zoneName"
                  :value="item.zoneCode"
                />
              </el-select>
              <el-select v-model="networkType" placeholder="专有网络" disabled>
                <el-option label="默认专有网络 / 交换机自动分配" value="vpc" />
              </el-select>
            </div>
          </div>
        </section>

        <section class="config-section">
          <div class="section-title">实例和镜像</div>

          <div class="config-row">
            <div class="row-label">
              <strong>实例</strong>
              <span>选择 GPU 规格</span>
            </div>
            <div class="row-control">
              <div class="filter-strip">
                <el-select v-model="filters.gpuCount" placeholder="选择 GPU 数量" clearable @change="reloadFromFirstPage">
                  <el-option label="全部 GPU 数量" value="" />
                  <el-option v-for="n in gpuCountOptions" :key="n" :label="`${n} 卡`" :value="n" />
                </el-select>

                <el-select
                  v-model="filters.gpuModel"
                  multiple
                  collapse-tags
                  collapse-tags-tooltip
                  placeholder="选择 GPU 型号"
                  clearable
                  @change="reloadFromFirstPage"
                >
                  <el-option
                    v-for="item in gpuModelOptions"
                    :key="item.model"
                    :label="`${item.model}（${item.availableCount}/${item.totalCount}）`"
                    :value="item.model"
                  />
                </el-select>

                <el-input
                  v-model="keyword"
                  clearable
                  placeholder="搜索规格、机器编号、CPU"
                  @input="reloadFromFirstPage"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>

                <button class="ghost-action" @click="handleFilterReset">
                  <el-icon><RefreshRight /></el-icon>
                  重置
                </button>
              </div>

              <div class="arch-tabs">
                <button class="arch-tab active">GPU</button>
              </div>

              <div class="instance-table" v-loading="loading">
                <el-table
                  :data="visibleGpuList"
                  :row-key="getGpuKey"
                  height="420"
                  highlight-current-row
                  @row-click="selectGpu"
                >
                  <el-table-column width="44">
                    <template #default="{ row }">
                      <el-radio
                        :model-value="selectedGpuKey"
                        :value="getGpuKey(row)"
                        @change="selectGpu(row)"
                      >
                        <span />
                      </el-radio>
                    </template>
                  </el-table-column>
                  <el-table-column label="实例规格" min-width="210">
                    <template #default="{ row }">
                      <div class="spec-name">{{ row.resourceNo || row.machineId || row.model }}</div>
                      <div class="spec-sub">{{ row.machineUuid || row.machineId || '--' }}</div>
                    </template>
                  </el-table-column>
                  <el-table-column label="规格族" min-width="160">
                    <template #default="{ row }">
                      <div>{{ row.model }}</div>
                      <div class="spec-sub">{{ row.vram || '--' }}</div>
                    </template>
                  </el-table-column>
                  <el-table-column label="vCPU" width="110">
                    <template #default="{ row }">{{ row.cpuCores || '--' }} vCPU</template>
                  </el-table-column>
                  <el-table-column label="内存" width="120" prop="memory" />
                  <el-table-column label="处理器" min-width="130">
                    <template #default="{ row }">
                      <span class="processor">{{ row.cpuModel || 'intel' }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="GPU" min-width="170">
                    <template #default="{ row }">
                      {{ row.totalCount || row.availableCount || 1 }} * {{ row.model }}
                    </template>
                  </el-table-column>
                  <el-table-column label="参考价格" width="150" align="right" fixed="right">
                    <template #default="{ row }">
                      <span v-if="row.source === 'volcano' && !getGpuResourceId(row)" class="row-price">{{ formatPriceValue(row.price, row.priceMonthly) }}</span>
                      <span v-else class="row-price">¥ {{ formatMoney(row.price) }}/{{ billingUnit }}</span>
                    </template>
                  </el-table-column>
                </el-table>

                <div v-if="!loading && visibleGpuList.length === 0" class="empty-state">
                  暂无符合条件的 GPU 实例
                  <el-button link type="primary" @click="handleFilterReset">清除筛选条件</el-button>
                </div>
              </div>

              <div v-if="total > 0" class="pagination-wrapper">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :total="total"
                  :page-sizes="[10, 20, 50]"
                  layout="total, prev, pager, next, sizes"
                  @change="handlePageChange"
                />
              </div>
            </div>
          </div>
        </section>
      </main>

      <aside class="summary-panel">
        <div class="summary-card">
          <div class="summary-header">
            <h3>配置概要</h3>
            <button class="link-action">保存为模板</button>
          </div>

          <div class="summary-row">
            <span>购买实例数量</span>
            <div class="quantity-control">
              <button :disabled="instanceCount <= 1" @click="changeInstanceCount(-1)">-</button>
              <el-input
                v-model="instanceCountInput"
                inputmode="numeric"
                placeholder="请输入数量"
                @blur="commitInstanceCount"
                @keyup.enter="commitInstanceCount"
              />
              <button :disabled="instanceCount >= instanceCountMax" @click="changeInstanceCount(1)">+</button>
            </div>
          </div>

          <div class="summary-row">
            <span>购买时长</span>
            <div class="duration-control">
              <button :disabled="durationCount <= 1" @click="changeDurationCount(-1)">-</button>
              <el-input
                v-model="durationCountInput"
                inputmode="numeric"
                placeholder="请输入时长"
                @blur="commitDurationCount"
                @keyup.enter="commitDurationCount"
              />
              <span>{{ billingUnit }}</span>
              <button @click="changeDurationCount(1)">+</button>
            </div>
          </div>

          <div class="summary-row">
            <span>续费方式</span>
            <strong>到期前手动续费</strong>
          </div>

          <div class="summary-divider" />

          <div class="price-title">价格概要</div>
          <div class="price-line">
            <span>计费方式</span>
            <strong>{{ selectedBillingTypeName }}</strong>
          </div>
          <div class="price-line">
            <span>单价</span>
            <strong>¥ {{ formatMoney(unitAmount) }}/{{ billingUnit }}</strong>
          </div>
          <div class="price-line">
            <span>配置费用</span>
            <strong>¥ {{ formatMoney(configAmount) }}</strong>
          </div>
          <div class="price-line discount">
            <span>优惠</span>
            <strong>- ¥ {{ formatMoney(discountAmount) }}</strong>
          </div>

          <div class="selected-spec">
            <span>已选规格</span>
            <strong>{{ selectedGpu ? `${selectedGpu.model} / ${selectedGpu.vram}` : '请选择 GPU 实例' }}</strong>
          </div>

          <div class="summary-total">
            <span>合计金额</span>
            <strong>¥ {{ formatMoney(totalAmount) }}</strong>
          </div>

          <div class="summary-actions">
            <el-button class="cart-btn" :disabled="!canOrder">加入清单</el-button>
            <el-button class="order-btn" :disabled="!canOrder" @click="handleConfirmOrder">
              下一步：镜像配置
            </el-button>
          </div>

          <p class="summary-note">
            下一步将选择运行镜像并确认完整配置，实际价格以最终确认页为准。
          </p>
        </div>
      </aside>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { InfoFilled, RefreshRight, Search } from '@element-plus/icons-vue'
import {
  getGpuMarketList,
  getGpuMarketMeta,
  GpuBillingType,
  GpuMarketItem,
  GpuModelStat,
  GpuRegionItem,
  GpuZoneItem,
} from '@/api/gpuMarket'
import { getVolcanoGpuCatalog, VolcanoGpuCatalog } from '@/api/volcanoGpu'

const router = useRouter()
const route = useRoute()

const gpuList = ref<GpuMarketItem[]>([])
const volcanoCatalog = ref<VolcanoGpuCatalog>({ regions: [] })
const loading = ref(false)
const selectedGpu = ref<GpuMarketItem | null>(null)

const baseRegionOptions = ref<GpuRegionItem[]>([])
const zoneOptions = ref<GpuZoneItem[]>([])
const baseGpuModelOptions = ref<GpuModelStat[]>([])
const baseGpuCountOptions = ref<number[]>([])
const billingTypeOptions = ref<GpuBillingType[]>([])

const filters = reactive({
  billingType: 'monthly',
  region: '',
  zone: '',
  gpuModel: [] as string[],
  gpuCount: '' as number | '',
})

const keyword = ref('')
const networkType = ref('vpc')
const instanceCount = ref(1)
const instanceCountInput = ref('1')
const durationCount = ref(1)
const durationCountInput = ref('1')
const sortBy = ref('price')
const sortOrder = ref('asc')
const currentPage = ref(1)
const pageSize = ref(10)

const regionOptions = computed<GpuRegionItem[]>(() => {
  const merged = new Map<string, GpuRegionItem>()
  for (const item of baseRegionOptions.value) merged.set(item.regionCode, item)
  for (const region of volcanoCatalog.value.regions || []) {
    if (region.region) merged.set(region.region, { regionCode: region.region, regionName: region.region })
  }
  return Array.from(merged.values())
})

const gpuModelOptions = computed<GpuModelStat[]>(() => {
  const merged = new Map<string, GpuModelStat>()
  for (const item of baseGpuModelOptions.value) merged.set(item.model, item)
  for (const item of volcanoGpuList.value) {
    const current = merged.get(item.model) || { model: item.model, availableCount: 0, totalCount: 0 }
    current.availableCount += 1
    current.totalCount += 1
    merged.set(item.model, current)
  }
  return Array.from(merged.values())
})

const gpuCountOptions = computed(() => Array.from(new Set([
  ...baseGpuCountOptions.value,
  ...volcanoGpuList.value.map((item) => Number((item as any).gpuCount)).filter(Number.isFinite),
])).sort((a, b) => a - b))

const billingUnit = computed(() => {
  const units: Record<string, string> = {
    hourly: '小时',
    on_demand: '小时',
    daily: '天',
    weekly: '周',
    monthly: '月',
  }
  return units[filters.billingType] || '小时'
})

const selectedBillingTypeName = computed(() => {
  return billingTypeOptions.value.find((item) => item.code === filters.billingType)?.name || filters.billingType
})

const filteredZoneOptions = computed(() => {
  if (!filters.region) return zoneOptions.value
  return zoneOptions.value.filter((item) => item.zoneCode.includes(filters.region) || item.zoneName.includes(selectedRegionName.value))
})

const selectedRegionName = computed(() => {
  return regionOptions.value.find((item) => item.regionCode === filters.region)?.regionName || ''
})

function hasCurrentExternalPrice(item: GpuMarketItem) {
  if (item.source !== 'volcano') return true
  const value = filters.billingType === 'monthly' ? item.priceMonthly : item.price
  const price = resolveExternalPrice(value)
  return price != null && price > 0
}

const filteredGpuList = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return [...gpuList.value, ...volcanoGpuList.value].filter((item) => {
    return hasCurrentExternalPrice(item) &&
      (!filters.region || item.regionCode === filters.region) &&
      (!filters.zone || item.zoneCode === filters.zone) &&
      (!filters.gpuModel.length || filters.gpuModel.includes(item.model)) &&
      (!filters.gpuCount || Number((item as any).gpuCount || item.totalCount) === Number(filters.gpuCount)) &&
      (!key || [item.resourceNo, item.model, item.cpuModel, item.machineId, item.machineUuid, item.instanceTypeId]
      .filter(Boolean)
      .some((value) => String(value).toLowerCase().includes(key)))
  })
})

const visibleGpuList = computed(() => filteredGpuList.value.slice(
  (currentPage.value - 1) * pageSize.value,
  currentPage.value * pageSize.value,
))

const total = computed(() => filteredGpuList.value.length)

const volcanoGpuList = computed(() => {
  const rows: GpuMarketItem[] = []
  for (const region of volcanoCatalog.value.regions || []) {
    for (const spec of region.gpuSpecs || []) {
      for (const item of spec.instanceTypes || []) {
        const hourlyPrice = resolveExternalPrice(item.price, spec.price)
        const monthlyPrice = resolveExternalPrice(item.priceMonthly, spec.priceMonthly)
        rows.push({
          resourceId: 0,
          resourceNo: item.instanceTypeId || '',
          model: spec.gpuModel || '',
          vram: spec.gpuMemory || '',
          region: region.region || '',
          regionCode: region.region || '',
          machineId: item.instanceTypeId || '',
          machineUuid: item.instanceTypeId || '',
          rentableUntil: '',
          availableCount: 1,
          totalCount: 1,
          cacheOptimized: false,
          cpuCores: item.cpuCores || 0,
          cpuModel: item.cpuModel || '',
          memory: item.memGib == null ? '' : `${item.memGib} GiB`,
          systemDisk: '', dataDisk: '', expandable: '', gpuDriver: '', cudaVersion: '',
          price: hourlyPrice == null ? '' : String(hourlyPrice),
          priceMonthly: monthlyPrice == null ? '' : String(monthlyPrice),
          discountPrice: '', discountRate: '', rentableCount: 1,
          source: 'volcano',
          instanceTypeId: item.instanceTypeId || '',
          gpuCount: item.gpuCount || 0,
          volcanoPrice: filters.billingType === 'monthly' ? item.priceMonthly : item.price,
        } as GpuMarketItem)
      }
    }
  }
  return rows
})

const unitAmount = computed(() => {
  if (!selectedGpu.value) return 0
  if (selectedGpu.value.source === 'volcano') {
    const value = (selectedGpu.value as any).volcanoPrice
    return Number(resolveExternalPrice(value) || 0)
  }
  return Number(selectedGpu.value.price || 0)
})

const effectiveUnitAmount = computed(() => {
  const discount = Number(selectedGpu.value?.discountPrice || 0)
  if (discount > 0 && discount < unitAmount.value) return discount
  return unitAmount.value
})

const configAmount = computed(() => {
  if (!selectedGpu.value) return 0
  return unitAmount.value * instanceCount.value * durationCount.value
})

const discountAmount = computed(() => {
  return Math.max(configAmount.value - totalAmount.value, 0)
})

const totalAmount = computed(() => {
  if (!selectedGpu.value) return 0
  return effectiveUnitAmount.value * instanceCount.value * durationCount.value
})
const selectedGpuKey = computed(() => selectedGpu.value ? getGpuKey(selectedGpu.value) : '')
const canOrder = computed(() => {
  if (!selectedGpu.value || selectedGpu.value.rentableCount <= 0) return false
  if (selectedGpu.value.source === 'volcano') {
    return hasCurrentExternalPrice(selectedGpu.value)
  }
  return Boolean(getGpuResourceId(selectedGpu.value))
})
const instanceCountMax = computed(() => Math.max(selectedGpu.value?.rentableCount || 99, 1))

watch(instanceCount, (count) => {
  instanceCountInput.value = String(count)
})

watch(durationCount, (count) => {
  durationCountInput.value = String(count)
})

watch(visibleGpuList, (list) => {
  if (!list.length) {
    selectedGpu.value = null
    return
  }
  if (!selectedGpu.value || !list.some((item) => getGpuKey(item) === selectedGpuKey.value)) {
    selectedGpu.value = getDefaultGpu(list)
  }
})

watch(() => selectedGpu.value?.rentableCount, (count) => {
  if (count && instanceCount.value > count) setInstanceCount(count)
})

watch(() => filters.billingType, () => {
  if (selectedGpu.value?.source === 'volcano') {
    const currentKey = getGpuKey(selectedGpu.value)
    selectedGpu.value = volcanoGpuList.value.find((item) => getGpuKey(item) === currentKey) || selectedGpu.value
  }
})

onMounted(async () => {
  restoreOrderDraft()
  const initialBillingType = filters.billingType
  const [billingTypeChanged] = await Promise.all([loadMetaData(), loadGpuList(), loadVolcanoCatalog()])
  if (billingTypeChanged && filters.billingType !== initialBillingType) {
    reloadFromFirstPage()
  }
})

async function loadMetaData() {
  try {
    const res = await getGpuMarketMeta()
    if (res.code === 200 && res.data) {
      baseRegionOptions.value = res.data.regions || []
      zoneOptions.value = res.data.zones || []
      baseGpuModelOptions.value = res.data.gpuModels || []
      baseGpuCountOptions.value = res.data.gpuCounts || []
      billingTypeOptions.value = normalizeBillingTypes(res.data.billingTypes || [])
      if (!billingTypeOptions.value.some((item) => item.code === filters.billingType)) {
        filters.billingType = billingTypeOptions.value[0]?.code || 'hourly'
        return true
      }
    }
  } catch (error) {
    console.error('加载筛选项数据失败:', error)
    billingTypeOptions.value = normalizeBillingTypes([])
  }
  return false
}

let requestSeq = 0
async function loadGpuList() {
  const seq = ++requestSeq
  loading.value = true
  try {
    const params = {
      billingType: filters.billingType,
      regionCode: filters.region || undefined,
      zoneCode: filters.zone || undefined,
      gpuModels: filters.gpuModel.length > 0 ? filters.gpuModel : undefined,
      gpuCount: typeof filters.gpuCount === 'number' ? filters.gpuCount : undefined,
      sortBy: sortBy.value,
      sortOrder: sortOrder.value,
      pageNo: currentPage.value,
      pageSize: 1000,
    }
    const res = await getGpuMarketList(params)
    if (seq !== requestSeq) return
    if (res.code === 200 && res.data) {
      gpuList.value = res.data.list || []
      if (gpuList.value.length && !gpuList.value.some((item) => getGpuKey(item) === selectedGpuKey.value)) {
        selectedGpu.value = getDefaultGpu(gpuList.value)
      }
      if (!gpuList.value.length) {
        selectedGpu.value = null
      }
    } else {
      ElMessage.error(res.msg || '获取数据失败')
    }
  } catch (error) {
    if (seq !== requestSeq) return
    console.error('加载GPU列表失败:', error)
    ElMessage.error('加载GPU列表失败')
  } finally {
    if (seq === requestSeq) loading.value = false
  }
}

async function loadVolcanoCatalog() {
  try {
    const res = await getVolcanoGpuCatalog(filters.billingType)
    if (res.code === 200 && res.data) {
      volcanoCatalog.value = res.data
    }
  } catch (error) {
    console.warn('加载 GPU 目录失败:', error)
  }
}

function normalizeBillingTypes(types: GpuBillingType[]) {
  const canonicalTypes = [
    { code: 'on_demand', name: '按量计费' },
    { code: 'hourly', name: '按小时' },
    { code: 'monthly', name: '按月' },
    { code: 'daily', name: '按天' },
    { code: 'weekly', name: '按周' },
  ]
  if (!types.length) return canonicalTypes
  const typeMap = new Map(types.map((item) => [item.code, item]))
  return canonicalTypes.map((item) => typeMap.get(item.code) || item)
}

function billingDescription(code: string) {
  const descriptions: Record<string, string> = {
    on_demand: '后付费模式，按实际使用计费',
    monthly: '先付费后使用，价格优惠',
    hourly: '使用后付费，按需开通',
    daily: '短期稳定使用',
    weekly: '适合周级任务',
  }
  return descriptions[code] || '灵活选择计费周期'
}

function setBillingType(code: string) {
  filters.billingType = code
  setDurationCount(1)
  void loadVolcanoCatalog()
  reloadFromFirstPage()
}

function setRegion(regionCode: string) {
  filters.region = regionCode
  filters.zone = ''
  reloadFromFirstPage()
}

function reloadFromFirstPage() {
  currentPage.value = 1
  loadGpuList()
}

function handlePageChange() {
  loadGpuList()
}

function handleFilterReset() {
  filters.billingType = billingTypeOptions.value[0]?.code || 'monthly'
  filters.region = ''
  filters.zone = ''
  filters.gpuModel = []
  filters.gpuCount = ''
  keyword.value = ''
  currentPage.value = 1
  loadGpuList()
}

function selectGpu(row: GpuMarketItem) {
  selectedGpu.value = row
  if (row.rentableCount > 0 && instanceCount.value > row.rentableCount) {
    setInstanceCount(row.rentableCount)
  }
}

function setInstanceCount(value: number) {
  const next = Math.min(Math.max(Math.floor(value || 1), 1), instanceCountMax.value)
  instanceCount.value = next
  instanceCountInput.value = String(next)
}

function changeInstanceCount(step: number) {
  setInstanceCount(instanceCount.value + step)
}

function commitInstanceCount() {
  const next = Number(instanceCountInput.value)
  if (!Number.isFinite(next)) {
    setInstanceCount(instanceCount.value)
    return
  }
  setInstanceCount(next)
}

function setDurationCount(value: number) {
  const next = Math.max(Math.floor(value || 1), 1)
  durationCount.value = next
  durationCountInput.value = String(next)
}

function changeDurationCount(step: number) {
  setDurationCount(durationCount.value + step)
}

function commitDurationCount() {
  const next = Number(durationCountInput.value)
  if (!Number.isFinite(next)) {
    setDurationCount(durationCount.value)
    return
  }
  setDurationCount(next)
}

function handleConfirmOrder() {
  if (!selectedGpu.value) {
    ElMessage.warning('请先选择 GPU 实例规格')
    return
  }
  if (selectedGpu.value.rentableCount <= 0) {
    ElMessage.warning('当前规格暂无可租 GPU，请选择其他规格')
    return
  }
  const resourceId = selectedGpu.value.source === 'volcano' ? 0 : getGpuResourceId(selectedGpu.value)
  if (resourceId == null || resourceId === undefined) {
    ElMessage.warning('当前规格缺少资源ID，无法下单')
    return
  }
  cacheOrderDraft(resourceId)
  router.push({
    path: '/computeRent',
    query: {
      resourceId: resourceId.toString(),
      billingType: filters.billingType,
      duration: durationCount.value.toString(),
      quantity: instanceCount.value.toString(),
      source: 'computeListNew',
    },
  })
}

function getGpuKey(gpu: GpuMarketItem) {
  const raw = gpu as any
  if (raw.source === 'volcano') {
    return `volcano:${raw.regionCode || raw.region}:${raw.instanceTypeId || raw.resourceNo || raw.machineId}`
  }
  return String(raw.resourceId ?? raw.id ?? raw.resourceNo ?? raw.machineId ?? raw.machineUuid ?? '')
}

function getGpuResourceId(gpu: GpuMarketItem) {
  const raw = gpu as any
  return raw.resourceId ?? raw.id
}

function formatPriceValue(value: unknown, monthlyValue: unknown) {
  const selected = filters.billingType === 'monthly' ? monthlyValue : value
  const number = resolveExternalPrice(selected)
  return number == null ? '--' : `¥ ${Number(number).toFixed(2)}/${billingUnit.value}`
}

function resolveExternalPrice(value: unknown, fallback?: unknown): number | null {
  const source = value ?? fallback
  if (source === null || source === undefined || source === '') return null
  if (typeof source === 'number') return Number.isFinite(source) ? source : null
  if (typeof source === 'string') {
    const number = Number(source)
    return Number.isFinite(number) ? number : null
  }
  if (typeof source === 'object') {
    const raw = source as any
    const number = raw.unitPrice ?? raw.unit_price ?? raw.discountPrice ?? raw.discount_unit_price
    return number == null ? null : resolveExternalPrice(number)
  }
  return null
}

function getDefaultGpu(list: GpuMarketItem[]) {
  return (
    list.find((item) => item.rentableCount > 0 && Number(item.price || 0) > 0) ||
    list.find((item) => item.rentableCount > 0) ||
    list.find((item) => Number(item.price || 0) > 0) ||
    list[0]
  )
}

function cacheOrderDraft(resourceId: number) {
  if (!selectedGpu.value) return
  const draft = {
    source: 'computeListNew',
    resourceId,
    billingType: filters.billingType,
    billingTypeName: selectedBillingTypeName.value,
    duration: durationCount.value,
    quantity: instanceCount.value,
    unitAmount: unitAmount.value,
    configAmount: configAmount.value,
    discountAmount: discountAmount.value,
    totalAmount: totalAmount.value,
    prices: selectedGpu.value.source === 'volcano' ? [
      { billingType: 'on_demand', billingTypeName: '按量计费', unitPrice: selectedGpu.value.price },
      { billingType: 'hourly', billingTypeName: '按小时', unitPrice: selectedGpu.value.price },
      { billingType: 'monthly', billingTypeName: '包月', unitPrice: selectedGpu.value.priceMonthly },
    ].filter((item) => resolveExternalPrice(item.unitPrice) != null) : undefined,
    regionCode: filters.region || selectedGpu.value.regionCode || '',
    regionName: selectedRegionName.value || selectedGpu.value.region || '',
    zoneCode: filters.zone || selectedGpu.value.zoneCode || '',
    zoneName: filteredZoneOptions.value.find((item) => item.zoneCode === filters.zone)?.zoneName || selectedGpu.value.zone || '',
    resource: selectedGpu.value,
    createdAt: Date.now(),
  }
  try {
    sessionStorage.setItem('computeRentOrderDraft', JSON.stringify(draft))
  } catch (error) {
    console.warn('保存下单配置失败:', error)
  }
}

function restoreOrderDraft() {
  if (route.query.restoreDraft !== '1') return
  try {
    const raw = sessionStorage.getItem('computeRentOrderDraft')
    if (!raw) return
    const draft = JSON.parse(raw)
    if (draft.source !== 'computeListNew' || !draft.resource) return

    filters.billingType = draft.billingType || filters.billingType
    filters.region = draft.regionCode || draft.resource.regionCode || draft.resource.region || ''
    filters.zone = draft.zoneCode || ''
    selectedGpu.value = draft.resource
    setInstanceCount(Number(draft.quantity) || 1)
    setDurationCount(Number(draft.duration) || 1)
  } catch (error) {
    console.warn('恢复下单配置失败:', error)
  }
}

function formatMoney(value: string | number | undefined) {
  const number = Number(value || 0)
  if (!Number.isFinite(number)) return '0.00'
  return number.toFixed(2)
}
</script>

<style scoped lang="scss">
.purchase-page {
  --aliyun-primary: #ff6a00;
  --aliyun-primary-hover: #ff7a1a;
  --aliyun-page-bg: #fff3dc;
  --aliyun-surface: #fffdf8;
  --aliyun-tint: #fff7e8;
  --aliyun-tint-strong: #fff0d4;
  --aliyun-border: #f1d3a1;
  --aliyun-soft-border: #f5dfba;

  min-height: 100vh;
  padding: 102px 0 36px;
  background: var(--aliyun-page-bg);
  color: #1f2937;
}

.product-heading {
  display: flex;
  align-items: center;
  gap: 14px;
  width: min(1540px, calc(100vw - 48px));
  margin: 0 auto 12px;
}

.product-name {
  color: #111827;
  font-size: 20px;
  font-weight: 700;
}

.product-mode {
  position: relative;
  padding: 6px 0 8px;
  color: var(--aliyun-primary);
  font-size: 15px;
  font-weight: 600;

  &::after {
    position: absolute;
    right: 0;
    bottom: 0;
    left: 0;
    height: 2px;
    content: "";
    background: var(--aliyun-primary);
  }
}

.product-tabs {
  display: flex;
  align-items: center;
  gap: 24px;
  height: 42px;
}

.product-tab {
  position: relative;
  height: 42px;
  padding: 0 2px;
  border: 0;
  background: transparent;
  color: #7b8494;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}

.product-tab:hover,
.product-tab.active {
  color: var(--aliyun-primary);
}

.product-tab.active::after {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  height: 2px;
  content: '';
  background: var(--aliyun-primary);
}

.purchase-shell {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 16px;
  width: min(1540px, calc(100vw - 48px));
  margin: 0 auto;
  align-items: start;
}

.ignore-purchase-page .purchase-shell {
  grid-template-columns: minmax(0, 1fr) 360px;
}

.config-panel,
.summary-card {
  border: 1px solid var(--aliyun-soft-border);
  background: var(--aliyun-surface);
}

.config-section {
  padding: 26px 28px;

  & + .config-section {
    border-top: 8px solid var(--aliyun-page-bg);
  }
}

.section-title {
  margin-bottom: 24px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.config-row {
  display: grid;
  grid-template-columns: 132px minmax(0, 1fr);
  gap: 18px;
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.row-label {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 6px;

  strong {
    color: #111827;
    font-size: 16px;
  }

  span {
    color: #7b8494;
    font-size: 13px;
    line-height: 1.4;
  }
}

.row-control {
  min-width: 0;
}

.option-grid {
  display: grid;
  gap: 10px;

  &.three {
    grid-template-columns: repeat(3, minmax(150px, 1fr));
    max-width: 720px;
  }
}

.region-grid {
  grid-template-columns: repeat(auto-fill, minmax(138px, 1fr));
}

.option-card,
.option-chip,
.arch-tab,
.ghost-action,
.link-action {
  border: 1px solid var(--aliyun-border);
  background: #fff;
  color: #111827;
  cursor: pointer;
  transition: border-color 0.16s ease, color 0.16s ease, background 0.16s ease;

  &:hover {
    border-color: var(--aliyun-primary);
    color: var(--aliyun-primary);
  }
}

.option-card {
  min-height: 58px;
  padding: 10px 16px;
  text-align: left;

  strong,
  span {
    display: block;
  }

  strong {
    margin-bottom: 4px;
    font-size: 15px;
  }

  span {
    color: #7b8494;
    font-size: 13px;
  }

  &.active {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    box-shadow: inset 3px 0 0 var(--aliyun-primary);
  }
}

.option-chip {
  height: 38px;
  padding: 0 14px;
  font-size: 14px;

  &.active {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
  }
}

.hint-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 10px 14px;
  border: 1px solid var(--aliyun-soft-border);
  background: var(--aliyun-tint);
  color: #6b7280;
  font-size: 13px;

  .el-icon {
    color: var(--aliyun-primary);
  }
}

.inline-selects,
.filter-strip {
  display: grid;
  grid-template-columns: 240px minmax(280px, 1fr);
  gap: 12px;
}

.filter-strip {
  grid-template-columns: 170px 220px minmax(260px, 1fr) auto;
  margin-bottom: 14px;
}

.ghost-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 32px;
  padding: 0 12px;
  color: var(--aliyun-primary);
}

.arch-tabs {
  display: grid;
  grid-template-columns: 150px;
  gap: 8px;
  max-width: 150px;
  margin-bottom: 14px;
}

.arch-tab {
  height: 44px;
  color: #4b5563;

  &.active {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
  }
}

.instance-table {
  position: relative;
  border: 1px solid var(--aliyun-soft-border);
  background: #fff;
}

.spec-name {
  color: #1f2937;
  font-weight: 600;
}

.spec-sub {
  margin-top: 4px;
  color: #8a94a6;
  font-size: 12px;
}

.processor {
  color: #0076ce;
  font-weight: 700;
}

.row-price {
  color: var(--aliyun-primary);
  font-weight: 600;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 120px;
  color: #7b8494;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: 18px;
}

.summary-panel {
  position: sticky;
  top: 144px;
}

.summary-card {
  min-height: 620px;
}

.summary-header,
.summary-row,
.price-line,
.selected-spec {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.summary-header {
  padding: 22px 24px 12px;

  h3 {
    margin: 0;
    color: #111827;
    font-size: 18px;
  }
}

.link-action {
  border: 0;
  color: var(--aliyun-primary);
  font-size: 13px;
}

.summary-row {
  padding: 10px 24px;
  color: #111827;
  font-size: 14px;

  > span {
    flex: 0 0 92px;
    font-weight: 600;
  }

  :deep(.el-select),
  .quantity-control,
  .duration-control {
    width: 190px;
  }
}

.quantity-control,
.duration-control {
  display: grid;
  height: 32px;

  button {
    border: 1px solid var(--aliyun-border);
    background: var(--aliyun-tint);
    color: #4b5563;
    cursor: pointer;

    &:disabled {
      color: #c8cdd6;
      cursor: not-allowed;
    }
  }

  button:first-child {
    border-right: 0;
  }

  button:last-child {
    border-left: 0;
  }

  :deep(.el-input__wrapper) {
    border-radius: 0;
    box-shadow: 0 0 0 1px var(--aliyun-border) inset;
  }

  :deep(.el-input__inner) {
    text-align: center;
  }
}

.quantity-control {
  grid-template-columns: 34px minmax(0, 1fr) 34px;
}

.duration-control {
  grid-template-columns: 34px minmax(0, 1fr) 40px 34px;

  span {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-top: 1px solid var(--aliyun-border);
    border-bottom: 1px solid var(--aliyun-border);
    background: #fff;
    color: #4b5563;
    font-size: 13px;
  }
}

.summary-divider {
  height: 1px;
  margin: 14px 0;
  background: var(--aliyun-soft-border);
}

.price-title {
  padding: 8px 24px 14px;
  color: #111827;
  font-size: 17px;
  font-weight: 700;
}

.price-line {
  padding: 7px 24px;
  color: #4b5563;
  font-size: 14px;

  strong {
    color: #111827;
    font-weight: 500;
  }

  &.discount strong {
    color: var(--aliyun-primary);
  }
}

.selected-spec {
  margin: 20px 24px 0;
  padding: 14px;
  border: 1px solid var(--aliyun-soft-border);
  background: var(--aliyun-tint);
  color: #6b7280;
  font-size: 13px;

  strong {
    max-width: 180px;
    color: #111827;
    text-align: right;
  }
}

.summary-total {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 12px;
  margin: 260px 24px 18px;

  span {
    color: #4b5563;
    font-size: 16px;
  }

  strong {
    color: var(--aliyun-primary);
    font-size: 30px;
    font-weight: 700;
  }
}

.summary-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  padding: 0 24px;
}

.cart-btn,
.order-btn {
  height: 42px;
  border-radius: 0;
  font-size: 15px;
}

.cart-btn {
  border-color: var(--aliyun-primary);
  background: #fff;
  color: var(--aliyun-primary);

  &:hover {
    border-color: var(--aliyun-primary-hover);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary-hover);
  }
}

.order-btn {
  border-color: var(--aliyun-primary);
  background: var(--aliyun-primary);
  color: #fff;

  &:hover {
    border-color: var(--aliyun-primary-hover);
    background: var(--aliyun-primary-hover);
    color: #fff;
  }
}

.summary-note {
  padding: 12px 24px 22px;
  color: #8a94a6;
  font-size: 12px;
  line-height: 1.7;
}

:deep(.el-table th.el-table__cell) {
  background: var(--aliyun-tint);
  color: #4b5563;
  font-weight: 600;
}

:deep(.el-table__row.current-row > td.el-table__cell) {
  background: var(--aliyun-tint-strong);
}

:deep(.el-table__cell.is-fixed-right) {
  background: #fff;
  box-shadow: -10px 0 18px rgb(17 24 39 / 8%);
}

:deep(.el-table__row.current-row > td.el-table__cell.is-fixed-right) {
  background: var(--aliyun-tint-strong);
}

:deep(.el-radio__label) {
  padding-left: 0;
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: var(--aliyun-primary);
  background: var(--aliyun-primary);
}

:deep(.el-pagination.is-background .el-pager li.is-active),
:deep(.el-pagination .el-pager li.is-active) {
  color: var(--aliyun-primary);
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 0;
  background-color: #fffdf8;
  box-shadow: 0 0 0 1px var(--aliyun-border) inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px var(--aliyun-primary) inset;
}

@media (max-width: 768px) {
  .purchase-page {
    padding-top: 88px;
  }

  .product-heading {
    width: calc(100vw - 24px);
  }

  .purchase-shell {
    width: min(1540px, calc(100vw - 24px));
  }

  .config-section {
    padding: 20px 16px;
  }

  .config-row,
  .inline-selects,
  .filter-strip {
    grid-template-columns: 1fr;
  }

  .option-grid.three,
  .region-grid,
  .arch-tabs {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1180px) {
  .purchase-shell,
  .ignore-purchase-page .purchase-shell {
    grid-template-columns: 1fr;
  }

  .summary-panel {
    position: static;
  }

  .summary-total {
    margin-top: 48px;
  }
}
</style>
