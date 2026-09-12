<template>
  <div class="volcano-purchase" v-loading="loading">
    <main class="config-panel">
      <section class="config-section">
        <div class="section-title">基础配置</div>

        <div class="config-row">
          <div class="row-label">
            <strong>付费类型</strong>
            <span>官方目录价格</span>
          </div>
          <div class="row-control option-grid two">
            <button
              v-for="item in billingOptions"
              :key="item.code"
              class="option-card"
              :class="{ active: billingType === item.code }"
              @click="billingType = item.code"
            >
              <strong>{{ item.name }}</strong>
              <span>{{ item.description }}</span>
            </button>
          </div>
        </div>

        <div class="config-row">
          <div class="row-label">
            <strong>地域</strong>
            <span>选择地域</span>
          </div>
          <div class="row-control">
            <div class="option-grid region-grid">
              <button class="option-chip" :class="{ active: filters.region === '' }" @click="filters.region = ''">全部地域</button>
              <button
                v-for="region in regionOptions"
                :key="region"
                class="option-chip"
                :class="{ active: filters.region === region }"
                @click="filters.region = region"
              >
                {{ region }}
              </button>
            </div>
            <div class="hint-line"><el-icon><InfoFilled /></el-icon>库存与价格以实时结果为准。</div>
          </div>
        </div>
      </section>

      <section class="config-section">
        <div class="section-title">实例和镜像</div>
        <div class="config-row">
          <div class="row-label">
            <strong>实例</strong>
            <span>选择 GPU 实例规格</span>
          </div>
          <div class="row-control">
            <div class="filter-strip">
              <el-select v-model="filters.gpuCount" clearable placeholder="选择 GPU 数量">
                <el-option label="全部 GPU 数量" value="" />
                <el-option v-for="count in countOptions" :key="count" :label="`${count} 卡`" :value="String(count)" />
              </el-select>
              <el-select v-model="filters.model" clearable filterable placeholder="选择 GPU 型号">
                <el-option v-for="model in modelOptions" :key="model" :label="model" :value="model" />
              </el-select>
              <el-input v-model="filters.keyword" clearable placeholder="搜索实例规格、CPU、地域">
                <template #prefix><el-icon><Search /></el-icon></template>
              </el-input>
              <button class="ghost-action" @click="resetFilters"><el-icon><RefreshRight /></el-icon>重置</button>
              <el-button link type="primary" :loading="loading" @click="loadCatalog">刷新</el-button>
            </div>

            <div class="arch-tabs"><button class="arch-tab active">GPU</button></div>
            <div class="instance-table">
              <el-table :data="filteredInstances" height="460" highlight-current-row @row-click="selectInstance">
                <el-table-column width="44">
                  <template #default="{ row }"><el-radio :model-value="selectedKey" :value="row.key" @change="selectInstance(row)"><span /></el-radio></template>
                </el-table-column>
                <el-table-column label="实例规格" min-width="250">
                  <template #default="{ row }"><div class="spec-name">{{ row.instanceTypeId }}</div><div class="spec-sub">{{ row.region }}</div></template>
                </el-table-column>
                <el-table-column label="规格族" min-width="160"><template #default="{ row }"><div>{{ row.gpuModel }}</div><div class="spec-sub">{{ row.gpuMemory || '--' }}</div></template></el-table-column>
                <el-table-column label="vCPU" width="100"><template #default="{ row }">{{ valueText(row.cpuCores) }}</template></el-table-column>
                <el-table-column label="内存" width="125"><template #default="{ row }">{{ valueText(row.memGib) }} GiB</template></el-table-column>
                <el-table-column label="处理器" min-width="180"><template #default="{ row }"><span class="processor">{{ row.cpuModel || '--' }}</span></template></el-table-column>
                <el-table-column label="GPU" width="100"><template #default="{ row }">{{ row.gpuCount }} 卡</template></el-table-column>
                <el-table-column label="参考价格" width="150" align="right" fixed="right"><template #default="{ row }"><span class="row-price">{{ priceText(priceOf(row)) }}</span></template></el-table-column>
              </el-table>
              <div v-if="!loading && !filteredInstances.length" class="empty-state">暂无符合条件的 GPU 实例</div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <aside class="summary-panel">
      <div class="summary-card">
        <div class="summary-header"><h3>配置概要</h3></div>
        <div class="summary-row"><span>购买实例数量</span><div class="quantity-control"><button :disabled="quantity <= 1" @click="quantity--">-</button><el-input v-model="quantityInput" inputmode="numeric" @blur="commitQuantity" @keyup.enter="commitQuantity" /><button @click="quantity++">+</button></div></div>
        <div class="summary-row"><span>购买时长</span><div class="duration-control"><button :disabled="duration <= 1" @click="duration--">-</button><el-input v-model="durationInput" inputmode="numeric" @blur="commitDuration" @keyup.enter="commitDuration" /><span>{{ billingType === 'monthly' ? '月' : '小时' }}</span><button @click="duration++">+</button></div></div>
        <div class="summary-divider" />
        <div class="price-title">价格概要</div>
        <div class="price-line"><span>计费方式</span><strong>{{ billingName }}</strong></div>
        <div class="price-line"><span>单价</span><strong>{{ priceText(selectedPrice) }}</strong></div>
        <div class="price-line"><span>配置费用</span><strong>{{ totalPriceText }}</strong></div>
        <div class="selected-spec"><span>已选规格</span><strong>{{ selectedInstance ? `${selectedInstance.gpuModel} / ${selectedInstance.gpuMemory}` : '请选择 GPU 实例' }}</strong></div>
        <div class="summary-total"><span>合计金额</span><strong>{{ totalPriceText }}</strong></div>
        <div class="summary-actions"><el-button class="order-btn" disabled>暂不支持下单</el-button></div>
        <p class="summary-note">当前页面用于查看和选择资源，实际购买请在对应平台完成。</p>
      </div>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { InfoFilled, RefreshRight, Search } from '@element-plus/icons-vue'
import { getVolcanoGpuCatalog, VolcanoGpuCatalog, VolcanoGpuSpec, VolcanoInstanceType, VolcanoRegion } from '@/api/volcanoGpu'

type BillingType = 'hourly' | 'monthly'
type InstanceRow = VolcanoInstanceType & { key: string; region: string; gpuModel: string; gpuMemory?: string }
const loading = ref(false)
const catalog = ref<VolcanoGpuCatalog>({ regions: [] })
const billingType = ref<BillingType>('hourly')
const filters = reactive({ region: '', model: '', gpuCount: '', keyword: '' })
const selectedInstance = ref<InstanceRow | null>(null)
const quantity = ref(1)
const quantityInput = ref('1')
const duration = ref(1)
const durationInput = ref('1')
const billingOptions = [
  { code: 'hourly' as BillingType, name: '按小时', description: '按实际使用时长计费' },
  { code: 'monthly' as BillingType, name: '按月', description: '包月预付，价格更优惠' },
]
const regions = computed(() => Array.isArray(catalog.value.regions) ? catalog.value.regions : [])
const regionOptions = computed(() => regions.value.map(item => item.region || '').filter(Boolean))
const modelOptions = computed(() => Array.from(new Set(regions.value.flatMap(region => specsOf(region).map(spec => spec.gpuModel || '')).filter(Boolean))).sort())
const countOptions = computed(() => {
  const counts = regions.value.flatMap(region =>
    specsOf(region).flatMap(spec => instancesOf(spec).map(item => Number(item.gpuCount)))
  )
  return Array.from(new Set(counts.filter(Number.isFinite))).sort((a, b) => a - b)
})
const allInstances = computed<InstanceRow[]>(() => regions.value.flatMap(region =>
  specsOf(region).flatMap(spec => instancesOf(spec).map((item, index) => ({
    ...item,
    key: `${region.region}-${spec.gpuModel}-${item.instanceTypeId}-${index}`,
    region: region.region || '--',
    gpuModel: spec.gpuModel || '--',
    gpuMemory: spec.gpuMemory,
  })))
))
const filteredInstances = computed(() => allInstances.value.filter(item => (!filters.region || item.region === filters.region) && (!filters.model || item.gpuModel === filters.model) && (!filters.gpuCount || Number(item.gpuCount) === Number(filters.gpuCount)) && (!filters.keyword || `${item.instanceTypeId} ${item.cpuModel} ${item.region} ${item.gpuModel}`.toLowerCase().includes(filters.keyword.trim().toLowerCase()))))
const billingName = computed(() => billingType.value === 'monthly' ? '按月' : '按小时')
const selectedKey = computed(() => selectedInstance.value?.key || '')
const selectedPrice = computed(() => selectedInstance.value ? priceOf(selectedInstance.value) : null)
const totalPriceText = computed(() => { const price = numericPrice(selectedPrice.value); return price === null ? '--' : `¥${(price * quantity.value * duration.value).toFixed(2)}` })

function specsOf(region: VolcanoRegion): VolcanoGpuSpec[] { return Array.isArray(region.gpuSpecs) ? region.gpuSpecs : [] }
function instancesOf(spec: VolcanoGpuSpec): VolcanoInstanceType[] { return Array.isArray(spec.instanceTypes) ? spec.instanceTypes : [] }
function priceOf(row: InstanceRow | null) { return billingType.value === 'monthly' ? row?.priceMonthly : row?.price }
function numericPrice(value: unknown): number | null { if (typeof value === 'number' && Number.isFinite(value)) return value; if (value && typeof value === 'object') { const raw = value as any; const price = raw.unitPrice ?? raw.unit_price; return typeof price === 'number' && Number.isFinite(price) ? price : null } return null }
function priceText(value: unknown) { const price = numericPrice(value); return price === null ? '--' : `¥${price.toFixed(2)}/${billingType.value === 'monthly' ? '月' : '小时'}` }
function valueText(value: unknown) { return value === null || value === undefined || value === '' ? '--' : String(value) }
function selectInstance(row: InstanceRow) { selectedInstance.value = row }
function resetFilters() { filters.region = ''; filters.model = ''; filters.gpuCount = ''; filters.keyword = '' }
function commitQuantity() { const next = Number(quantityInput.value); quantity.value = Number.isFinite(next) && next > 0 ? Math.floor(next) : 1; quantityInput.value = String(quantity.value) }
function commitDuration() { const next = Number(durationInput.value); duration.value = Number.isFinite(next) && next > 0 ? Math.floor(next) : 1; durationInput.value = String(duration.value) }
async function loadCatalog() {
  loading.value = true
  try {
    const response = await getVolcanoGpuCatalog() as any
    const data = response?.success !== undefined ? response : (response?.data || {})
    catalog.value = { ...data, regions: Array.isArray(data.regions) ? data.regions : [] }
    if (!selectedInstance.value) selectedInstance.value = filteredInstances.value[0] || null
  } finally {
    loading.value = false
  }
}
watch(quantity, value => { quantityInput.value = String(value) })
watch(duration, value => { durationInput.value = String(value) })
watch(filteredInstances, list => { if (!selectedInstance.value || !list.some(item => item.key === selectedInstance.value?.key)) selectedInstance.value = list[0] || null })
onMounted(loadCatalog)
</script>

<style scoped lang="scss">
.volcano-purchase {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 22px;
  width: min(1540px, calc(100vw - 48px));
  margin: 0 auto;
  align-items: start;
}
.volcano-purchase .config-panel { min-width: 0; }
.volcano-purchase .config-section {
  padding: 28px 30px;
  border: 1px solid #eadfcf;
  border-radius: 14px;
  background: linear-gradient(180deg, #fffdfa 0%, #fffaf2 100%);
  box-shadow: 0 8px 24px rgba(111, 74, 24, .05);
}
.volcano-purchase .config-section + .config-section { margin-top: 16px; }
.volcano-purchase .section-title { margin-bottom: 26px; color: #182230; font-size: 18px; font-weight: 750; letter-spacing: .01em; }
.volcano-purchase .config-row { display: grid; grid-template-columns: 142px minmax(0, 1fr); gap: 24px; margin-bottom: 24px; }
.volcano-purchase .config-row:last-child { margin-bottom: 0; }
.volcano-purchase .row-label { display: flex; flex-direction: column; gap: 7px; padding-top: 5px; }
.volcano-purchase .row-label strong { color: #253044; font-size: 14px; }
.volcano-purchase .row-label span, .volcano-purchase .hint-line, .volcano-purchase .spec-sub, .volcano-purchase .summary-note { color: #8b94a3; font-size: 12px; line-height: 1.5; }
.volcano-purchase .option-grid { display: grid; gap: 10px; }
.volcano-purchase .option-grid.two { grid-template-columns: repeat(2, minmax(145px, 220px)); }
.volcano-purchase .option-card, .volcano-purchase .option-chip { border: 1px solid #ead8bc; border-radius: 9px; background: rgba(255, 255, 255, .72); color: #4b5563; cursor: pointer; transition: .2s ease; }
.volcano-purchase .option-card:hover, .volcano-purchase .option-chip:hover { border-color: #ff8a2a; transform: translateY(-1px); }
.volcano-purchase .option-card { min-height: 68px; padding: 12px 15px; text-align: left; }
.volcano-purchase .option-card strong, .volcano-purchase .option-card span { display: block; }
.volcano-purchase .option-card strong { font-size: 14px; }
.volcano-purchase .option-card span { margin-top: 6px; color: #8a93a3; font-size: 12px; }
.volcano-purchase .option-card.active, .volcano-purchase .option-chip.active { border-color: #ff7418; background: linear-gradient(135deg, #fff2df, #fffaf3); color: #ef5d0a; box-shadow: 0 4px 12px rgba(255, 116, 24, .12); }
.volcano-purchase .region-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.volcano-purchase .option-chip { padding: 9px 15px; font-size: 13px; }
.volcano-purchase .hint-line { display: flex; align-items: center; gap: 7px; margin-top: 13px; padding: 10px 12px; border-radius: 8px; background: #fff5e7; }
.volcano-purchase .hint-line .el-icon { color: #f27a1a; }
.volcano-purchase .filter-strip { display: flex; gap: 10px; align-items: center; }
.volcano-purchase .filter-strip :deep(.el-select) { width: 175px; }
.volcano-purchase .filter-strip :deep(.el-input) { flex: 1; min-width: 160px; }
.volcano-purchase .filter-strip :deep(.el-input__wrapper), .volcano-purchase .filter-strip :deep(.el-select__wrapper) { border-radius: 8px; box-shadow: 0 0 0 1px #eadfcf inset; }
.volcano-purchase .arch-tabs { margin: 18px 0 10px; }
.volcano-purchase .arch-tab { padding: 8px 30px; border: 1px solid #ff7418; border-radius: 8px; background: #fff7ea; color: #ef5d0a; font-size: 17px; font-weight: 700; }
.volcano-purchase .instance-table { overflow: hidden; border: 1px solid #eadfcf; border-radius: 10px; background: #fff; }
.volcano-purchase .instance-table :deep(.el-table) { --el-table-header-bg-color: #fff6e9; --el-table-row-hover-bg-color: #fff8ef; --el-table-border-color: #f0e4d2; }
.volcano-purchase .instance-table :deep(.el-table th.el-table__cell) { color: #667085; font-size: 12px; font-weight: 700; }
.volcano-purchase .instance-table :deep(.el-table td.el-table__cell) { padding: 12px 0; }
.volcano-purchase .spec-name { color: #273244; font-weight: 650; }
.volcano-purchase .processor { color: #657184; font-size: 12px; }
.volcano-purchase .row-price { color: #f06418; font-weight: 750; }
.volcano-purchase .empty-state { padding: 42px; color: #8a93a3; text-align: center; }
.volcano-purchase .summary-panel { position: sticky; top: 124px; min-width: 0; }
.volcano-purchase .summary-card { overflow: hidden; border: 1px solid #ead8bc; border-radius: 16px; background: linear-gradient(180deg, #fffdf9 0%, #fff7e9 100%); box-shadow: 0 12px 30px rgba(109, 67, 18, .1); }
.volcano-purchase .summary-header { display: flex; align-items: center; justify-content: space-between; padding: 22px 22px 18px; border-bottom: 1px solid #f0e2cd; }
.volcano-purchase .summary-header h3 { margin: 0; color: #182230; font-size: 18px; }
.volcano-purchase .summary-row, .volcano-purchase .price-line, .volcano-purchase .selected-spec { display: flex; align-items: center; justify-content: space-between; gap: 14px; }
.volcano-purchase .summary-row { padding: 15px 22px 0; color: #4b5563; font-size: 13px; }
.volcano-purchase .summary-row > span, .volcano-purchase .price-line > span, .volcano-purchase .selected-spec > span { color: #7b8494; }
.volcano-purchase .quantity-control, .volcano-purchase .duration-control { display: grid; grid-template-columns: 30px minmax(60px, 1fr) 30px; width: 142px; height: 34px; }
.volcano-purchase .quantity-control button, .volcano-purchase .duration-control button { border: 1px solid #ead8bc; background: #fff; color: #ef6b1b; cursor: pointer; }
.volcano-purchase .quantity-control button:first-child, .volcano-purchase .duration-control button:first-child { border-radius: 7px 0 0 7px; }
.volcano-purchase .quantity-control button:last-child, .volcano-purchase .duration-control button:last-child { border-radius: 0 7px 7px 0; }
.volcano-purchase .quantity-control :deep(.el-input__wrapper), .volcano-purchase .duration-control :deep(.el-input__wrapper) { border-radius: 0; box-shadow: 0 0 0 1px #ead8bc inset; }
.volcano-purchase .duration-control { grid-template-columns: 30px minmax(50px, 1fr) 36px 30px; }
.volcano-purchase .duration-control > span { display: flex; align-items: center; justify-content: center; border-top: 1px solid #ead8bc; border-bottom: 1px solid #ead8bc; background: #fffaf3; color: #7b8494; font-size: 12px; }
.volcano-purchase .summary-divider { height: 1px; margin: 22px 22px 18px; background: #f0e2cd; }
.volcano-purchase .price-title { padding: 0 22px 10px; color: #253044; font-size: 14px; font-weight: 700; }
.volcano-purchase .price-line { padding: 7px 22px; font-size: 13px; }
.volcano-purchase .price-line strong { color: #253044; }
.volcano-purchase .selected-spec { margin: 15px 22px 0; padding: 13px 12px; border-radius: 9px; background: #fff1dc; font-size: 12px; }
.volcano-purchase .selected-spec strong { max-width: 170px; color: #344054; text-align: right; }
.volcano-purchase .summary-total { display: flex; align-items: baseline; justify-content: space-between; margin: 18px 22px 0; padding-top: 16px; border-top: 1px solid #f0e2cd; }
.volcano-purchase .summary-total span { color: #4b5563; font-size: 13px; }
.volcano-purchase .summary-total strong { color: #ef5d0a; font-size: 25px; }
.volcano-purchase .summary-actions { padding: 20px 22px 0; }
.volcano-purchase .summary-actions :deep(.order-btn) { width: 100%; height: 42px; border: 0; border-radius: 9px; background: linear-gradient(135deg, #ff8a2a, #ef5d0a); color: #fff; font-weight: 700; }
.volcano-purchase .summary-note { margin: 12px 22px 20px; text-align: center; }
@media (max-width: 760px) { .volcano-purchase { grid-template-columns: 1fr; width: calc(100vw - 24px); } .volcano-purchase .summary-panel { position: static; } .volcano-purchase .config-row { grid-template-columns: 1fr; gap: 12px; } .volcano-purchase .filter-strip { flex-wrap: wrap; } .volcano-purchase .filter-strip :deep(.el-input) { min-width: 100%; } }
</style>
