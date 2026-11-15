<template>
  <section class="cpu-panel" v-loading="loading">
    <div class="toolbar">
      <div><h3>火山云 CPU 价格管理</h3><p>维护按量小时价和包年包月单价，平台售价不得低于火山云原价</p></div>
      <el-button type="primary" :icon="Refresh" :loading="refreshing" :disabled="refreshing" @click="refreshAll">刷新全部地域</el-button>
    </div>
    <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" />
    <el-alert v-if="summary" :title="summary.message" :type="summary.failed ? 'warning' : 'success'" show-icon :closable="false" />
    <el-table v-if="summary" :data="summary.regions" size="small" class="summary">
      <el-table-column prop="region" label="刷新地域" /><el-table-column label="结果"><template #default="{ row }">{{ row.success ? '成功' : '失败，保留旧数据' }}</template></el-table-column>
      <el-table-column prop="specCount" label="规格数" /><el-table-column prop="raisedPrices" label="自动抬价数" /><el-table-column prop="message" label="说明" />
    </el-table>
    <div class="filters">
      <el-select v-model="filters.region" clearable placeholder="全部地域"><el-option v-for="region in regions" :key="region.region" :label="region.label || region.region" :value="region.region" /></el-select>
      <el-select v-model="filters.billing" clearable placeholder="全部计费方式"><el-option label="按量（PostPaid / Hour）" value="on_demand" /><el-option label="包年包月（PrePaid / Month）" value="monthly" /></el-select>
      <el-input v-model="filters.instance" clearable placeholder="搜索 CPU 规格" />
      <el-button @click="resetFilters">重置</el-button>
    </div>
    <el-table :data="visibleRows" border stripe empty-text="暂无 CPU 价格，请刷新全部地域">
      <el-table-column prop="regionCode" label="地域" width="130" />
      <el-table-column prop="instanceTypeId" label="CPU 规格" min-width="180" show-overflow-tooltip />
      <el-table-column prop="family" label="规格族" width="130" /><el-table-column prop="cpuCores" label="核数" width="70" />
      <el-table-column prop="cpuModel" label="CPU 型号" min-width="150" show-overflow-tooltip /><el-table-column prop="memGib" label="内存 GiB" width="95" />
      <el-table-column label="计费方式" width="110"><template #default="{ row }">{{ row.billingType === 'monthly' ? '包年包月 / 月' : '按量 / 小时' }}</template></el-table-column>
      <el-table-column label="火山云原价" width="115"><template #default="{ row }">{{ money(row.upstreamPrice) }}</template></el-table-column>
      <el-table-column label="生效售价" width="115"><template #default="{ row }">{{ money(row.effectivePrice) }}</template></el-table-column>
      <el-table-column label="加价差额" width="110"><template #default="{ row }">{{ row.effectivePrice == null || row.upstreamPrice == null ? '--' : money(row.effectivePrice - row.upstreamPrice) }}</template></el-table-column>
      <el-table-column label="售价配置" width="100"><template #default="{ row }"><el-tag :type="!row.config ? 'info' : row.config.status === 1 ? 'success' : 'warning'">{{ !row.config ? '默认原价' : row.config.status === 1 ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="更新时间" width="170"><template #default="{ row }">{{ formatTime(row.config?.updateTime || row.updatedAt) }}</template></el-table-column>
      <el-table-column label="操作" fixed="right" width="230"><template #default="{ row }">
        <el-button link type="primary" :disabled="row.upstreamPrice == null" @click="edit(row)">改价</el-button>
        <el-button link type="primary" :disabled="!row.config || mutating" @click="toggle(row)">{{ row.config?.status === 0 ? '启用' : '停用' }}</el-button>
        <el-button link type="warning" :disabled="!row.config || mutating" @click="restore(row)">恢复默认</el-button>
      </template></el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="调整 CPU 平台售价" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="地域 / 规格">{{ form.regionCode }} / {{ form.instanceTypeId }}</el-form-item>
        <el-form-item label="计费方式">{{ form.billingType === 'monthly' ? '包年包月 / 月' : '按量 / 小时' }}</el-form-item>
        <el-form-item label="火山云原价">{{ money(form.upstreamPrice) }}</el-form-item>
        <el-form-item label="平台售价" prop="salePrice"><el-input-number v-model="form.salePrice" :min="form.upstreamPrice || 0.0001" :precision="4" :step="1" controls-position="right" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" maxlength="256" show-word-limit /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="mutating" @click="save">保存</el-button></template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { volcanoCpuCatalogApi, refreshVolcanoCpuCatalogApi, volcanoCpuPriceSaveApi, volcanoCpuPriceStatusApi, volcanoCpuPriceDeleteApi, type VolcanoCpuSalePriceEditParams } from '@/api/gpuAvailability'

type Price = { unitPrice?: number; available?: boolean; pricePending?: boolean; currency?: string }
type Config = VolcanoCpuSalePriceEditParams & { id: number; updateTime?: string }
type Spec = { instanceTypeId: string; family?: string; cpuCores?: number; cpuModel?: string; memGib?: number; price?: Price; priceMonthly?: Price; salePrice?: number | null; salePriceMonthly?: number | null; hourlyPriceConfig?: Config; monthlyPriceConfig?: Config }
type Region = { region: string; label?: string; updatedAt?: string; specs: Spec[] }
type Row = Spec & { regionCode: string; updatedAt?: string; billingType: 'on_demand' | 'monthly'; upstreamPrice: number | null; effectivePrice: number | null; config?: Config }
const regions = ref<Region[]>([]), loading = ref(false), refreshing = ref(false), mutating = ref(false), error = ref('')
const summary = ref<{ message: string; failed: number; regions: Array<{region: string; success: boolean; specCount?: number; raisedPrices?: number; message?: string}> }>()
const filters = reactive({ region: '', billing: '', instance: '' })
const dialogVisible = ref(false), formRef = ref<FormInstance>()
const form = reactive<VolcanoCpuSalePriceEditParams>({ regionCode: '', instanceTypeId: '', billingType: 'monthly', salePrice: 0, status: 1, remark: '' })
const rules: FormRules = { salePrice: [{ validator: (_rule, value, done) => {
  if (!Number.isFinite(value) || value <= 0 || value < Number(form.upstreamPrice)) done(new Error('平台售价不能低于火山云原价'))
  else done()
}, trigger: 'blur' }] }
const rawPrice = (price?: Price): number | null => price?.available === true && !price.pricePending && price.currency === 'CNY' && Number(price.unitPrice) > 0 ? Number(price.unitPrice) : null
const rows = computed<Row[]>(() => regions.value.flatMap(region => (region.specs || []).flatMap(spec => (['on_demand', 'monthly'] as const).map(billingType => ({
  ...spec, regionCode: region.region, updatedAt: region.updatedAt, billingType,
  upstreamPrice: rawPrice(billingType === 'monthly' ? spec.priceMonthly : spec.price),
  effectivePrice: (billingType === 'monthly' ? spec.salePriceMonthly : spec.salePrice) ?? null,
  config: billingType === 'monthly' ? spec.monthlyPriceConfig : spec.hourlyPriceConfig
})))))
const visibleRows = computed(() => rows.value.filter(row => (!filters.region || row.regionCode === filters.region) && (!filters.billing || row.billingType === filters.billing) && row.instanceTypeId.toLowerCase().includes(filters.instance.toLowerCase())))
const money = (value?: number | null) => value == null ? '--' : `¥${Number(value).toFixed(4)}`
const formatTime = (value?: string) => value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '--'
const resetFilters = () => Object.assign(filters, { region: '', billing: '', instance: '' })
async function load() {
  loading.value = true; error.value = ''
  try { const res = await volcanoCpuCatalogApi(); regions.value = res.data?.regions || [] }
  catch (e: any) { error.value = e?.message || 'CPU 价格加载失败' }
  finally { loading.value = false }
}
async function refreshAll() {
  if (refreshing.value) return
  refreshing.value = true
  try { const res = await refreshVolcanoCpuCatalogApi(); summary.value = res.data; await load() }
  catch (e: any) { error.value = e?.message || 'CPU 价格刷新失败，已保留原数据' }
  finally { refreshing.value = false }
}
function edit(row: Row) {
  Object.assign(form, { id: row.config?.id, regionCode: row.regionCode, instanceTypeId: row.instanceTypeId, billingType: row.billingType, upstreamPrice: row.upstreamPrice, salePrice: row.config?.salePrice ?? row.effectivePrice ?? row.upstreamPrice ?? 0, status: row.config?.status ?? 1, remark: row.config?.remark || '' })
  dialogVisible.value = true
}
async function save() {
  if (!await formRef.value?.validate().catch(() => false)) return
  mutating.value = true
  try { await volcanoCpuPriceSaveApi({ ...form }); dialogVisible.value = false; ElMessage.success('CPU 售价已保存'); await load() }
  finally { mutating.value = false }
}
async function toggle(row: Row) {
  if (!row.config) return
  mutating.value = true
  try { await volcanoCpuPriceStatusApi(row.config.id, row.config.status === 1 ? 0 : 1); await load() }
  finally { mutating.value = false }
}
async function restore(row: Row) {
  if (!row.config) return
  try { await ElMessageBox.confirm('恢复后将使用火山云原价，是否继续？', '恢复默认价', { type: 'warning' }) }
  catch { return }
  mutating.value = true
  try { await volcanoCpuPriceDeleteApi(row.config.id); await load() }
  finally { mutating.value = false }
}
onMounted(load)
</script>
<style scoped>
.cpu-panel { padding:12px; }.toolbar { display:flex;align-items:center;justify-content:space-between;gap:16px;margin-bottom:16px; }.toolbar h3 { margin:0 0 8px; }.toolbar p { margin:0;color:var(--el-text-color-secondary); }.filters { display:flex;gap:12px;margin:16px 0; }.filters .el-input,.filters .el-select { width:220px; }.summary { margin-top:12px; }
@media(max-width:760px) { .toolbar,.filters { flex-wrap:wrap; }.filters .el-input,.filters .el-select { width:100%; } }
</style>
