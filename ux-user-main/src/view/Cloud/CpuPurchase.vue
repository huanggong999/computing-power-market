<template>
  <div class="cpu-purchase-page">
    <div class="page-header"><div><h2>购买 CPU 实例</h2><p>独立 CPU 云主机，不加入 GPU/Kubernetes 集群</p></div><el-button @click="router.push('/cloud/gpuInstance?type=cpu')">返回 CPU 实例</el-button></div>
    <el-card v-loading="loading">
      <el-form :model="form" label-width="120px" @submit.prevent>
        <el-form-item label="地域" :error="errors.region"><el-select v-model="form.region" placeholder="请选择地域"><el-option v-for="item in regions" :key="item.region" :label="item.label || item.region" :value="item.region" /></el-select></el-form-item>
        <el-form-item label="可用区" :error="errors.zone"><el-select v-model="form.zone" placeholder="请选择可用区"><el-option v-for="zone in selectedZones" :key="zone" :label="zoneLabel(zone)" :value="zone" /></el-select></el-form-item>
        <el-form-item label="CPU规格" :error="errors.instanceTypeId || errors.catalog"><el-select v-model="form.instanceTypeId" :loading="catalogLoading" placeholder="请选择纯 CPU 规格"><el-option v-for="item in pricedInstanceTypes" :key="item.instanceTypeId" :label="`${item.instanceTypeId}（${item.cpuCores}核/${item.memGib}GiB）`" :value="item.instanceTypeId" /></el-select></el-form-item>
        <el-form-item label="计费方式"><el-radio-group v-model="form.billingMode"><el-radio-button label="on_demand">按量付费</el-radio-button><el-radio-button label="monthly">包月</el-radio-button><el-radio-button label="yearly">包年</el-radio-button></el-radio-group></el-form-item>
        <el-form-item v-if="form.billingMode !== 'on_demand'" label="时长" :error="errors.duration"><el-input-number v-model="form.duration" :min="1" :max="36" /></el-form-item>
        <el-form-item label="系统盘 (GiB)" :error="errors.systemDiskGb"><el-input-number v-model="form.systemDiskGb" :min="20" :max="2048" /></el-form-item>
        <el-form-item label="数据盘 (GiB)" :error="errors.dataDiskGb"><el-input-number v-model="form.dataDiskGb" :min="0" :max="8192" :step="20" /><span class="field-hint">0 表示不购买，购买时最低 20G</span></el-form-item>
        <el-form-item v-if="diskCapacityTooSmall" label="磁盘容量"><span class="disk-capacity-error">磁盘资源最低需要 100G（当前合计 {{ totalDiskCapacityGb }}G）</span></el-form-item>
        <el-form-item label="购买数量" :error="errors.count"><el-input-number v-model="form.count" :min="1" :max="10" /></el-form-item><el-form-item label="实例单价"><strong data-testid="cpu-unit-price">{{ displayPrice === null ? '--' : `¥${displayPrice.toFixed(4)} / ${form.billingMode === 'on_demand' ? '小时' : '月'}` }}</strong></el-form-item>
        <el-alert v-if="catalogError" :title="catalogError" type="warning" show-icon :closable="false" />
        <el-empty v-else-if="!catalogLoading && form.region && !pricedInstanceTypes.length" description="当前计费方式暂无可购买 CPU 规格" />
        <el-alert title="CPU 实例独立于 GPU/Kubernetes 集群。服务端默认分配弹性公网 IP，按实际出公网流量后付费；当前接口不支持在购买页切换公网计费方式。" type="info" show-icon :closable="false" />
        <el-button type="primary" :loading="submitting" :disabled="loading || catalogLoading || displayPrice === null" @click="submit">立即购买</el-button>
      </el-form>
      <el-alert v-if="result" class="result" :title="result.statusMessage || result.message || (result.success ? '已受理，创建中' : '购买失败')" :type="result.success ? 'success' : 'error'" show-icon :closable="false"><template #default><span v-if="result.operation_id">创建任务：{{ result.operation_id }}，请到 CPU 实例列表查看进度。</span></template></el-alert>
      <el-descriptions v-if="result?.instances?.length" title="已创建实例" :column="1" border class="result"><el-descriptions-item v-for="(item, index) in result.instances" :key="index" :label="String(item.instance_id || index)">状态 {{ item.status || '-' }}，公网 IP {{ item.public_ip || '-' }}</el-descriptions-item></el-descriptions>
      <el-button v-if="result?.success" class="result" type="primary" @click="router.push('/cloud/gpuInstance?type=cpu')">查看 CPU 实例</el-button>
      <el-descriptions v-if="result?.checks?.length" title="校验结果" :column="1" border class="result"><el-descriptions-item v-for="item in result.checks" :key="item.name" :label="item.name || '-'">{{ item.message }}</el-descriptions-item></el-descriptions>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getCpuInstanceTypes, getCpuPrices, getCpuRegions, getCpuZones, purchaseCpu } from '@/api/instanceV2'
import type { CpuInstanceType, CpuPriceSpec, CpuPurchaseRequest, CpuPurchaseResult, CpuRegion, CpuZoneDetail } from '@/types/instance'
import { firstCpuPurchaseError, normalizeCpuPurchaseResult, validateCpuPurchase } from '@/utils/cpuPurchase'
import { cpuDisplayPrice, cpuPricingSnapshot } from '@/utils/cpuPricing'
import { MIN_DISK_CAPACITY_GB, totalDiskCapacityGb as calculateTotalDiskCapacityGb } from '@/utils/diskCapacity'
import { generateUUID } from '@/utils/uuid'

const router = useRouter()
const loading = ref(false), catalogLoading = ref(false), submitting = ref(false), catalogError = ref('')
const regions = ref<CpuRegion[]>([]), zoneDetails = ref<CpuZoneDetail[]>([]), instanceTypes = ref<CpuInstanceType[]>([]), priceCatalog = ref<CpuPriceSpec[]>([])
const result = ref<CpuPurchaseResult | null>(null), requestKey = ref('')
const form = reactive({ region: '', zone: '', instanceTypeId: '', billingMode: 'on_demand' as CpuPurchaseRequest['billingMode'], duration: 1, systemDiskGb: 100, dataDiskGb: 0, count: 1 })
const errors = reactive<Record<string, string>>({})
const selectedZones = computed(() => regions.value.find(item => item.region === form.region)?.zones || [])
function fallbackZoneLabel(zone: string) {
  const suffix = zone.match(/([a-zA-Z])$/)?.[1]
  return suffix ? `可用区${suffix.toUpperCase()}` : zone
}
function zoneLabel(zone: string) {
  return zoneDetails.value.find(item => item.zone === zone && (!item.region || item.region === form.region))?.name || fallbackZoneLabel(zone)
}
const pricedInstanceTypes = computed(() => instanceTypes.value.filter(item => form.billingMode !== 'yearly' && priceCatalog.value.some(price => price.instanceTypeId === item.instanceTypeId && cpuDisplayPrice(price, form.billingMode) !== null)))
const selectedPrice = computed(() => priceCatalog.value.find(item => item.instanceTypeId === form.instanceTypeId))
const displayPrice = computed(() => cpuDisplayPrice(selectedPrice.value, form.billingMode))
const totalDiskCapacityGb = computed(() => calculateTotalDiskCapacityGb(form.systemDiskGb, form.dataDiskGb))
const diskCapacityTooSmall = computed(() => totalDiskCapacityGb.value < MIN_DISK_CAPACITY_GB)
let catalogEpoch = 0
async function loadCatalog(resetZone: boolean) {
  const epoch = ++catalogEpoch
  instanceTypes.value = []; priceCatalog.value = []; form.instanceTypeId = ''; catalogError.value = ''
  if (resetZone) form.zone = selectedZones.value[0] || ''
  if (!form.region || form.billingMode === 'yearly') { catalogLoading.value = false; return }
  catalogLoading.value = true
  try {
    const [types, prices] = await Promise.all([getCpuInstanceTypes(form.region), getCpuPrices(form.region, undefined, form.billingMode)])
    if (epoch !== catalogEpoch) return
    if (types.data?.success === false || prices.data?.success === false) throw new Error('CPU 规格或价格暂不可用，请稍后重试')
    instanceTypes.value = types.data?.instance_types || []
    priceCatalog.value = prices.data?.specs || []
  } catch (error: any) {
    if (epoch === catalogEpoch) catalogError.value = error?.message || 'CPU 价格加载失败，请稍后重试'
  } finally { if (epoch === catalogEpoch) catalogLoading.value = false }
}
watch(() => [form.region, form.billingMode], ([region], [previousRegion]) => { void loadCatalog(region !== previousRegion) })
async function load() {
  loading.value = true
  try {
    const res = await getCpuRegions()
    regions.value = res.data?.regions || []
    form.region = regions.value[0]?.region || ''
    void loadZoneDetails()
  } catch (error: any) { catalogError.value = error?.message || '加载 CPU 购买选项失败' }
  finally { loading.value = false }
}
async function loadZoneDetails() {
  try {
    const res = await getCpuZones()
    if (res.data?.success === false) return
    zoneDetails.value = res.data?.zones || []
  } catch (error) { console.warn('加载 CPU 可用区名称失败，将使用可用区编码:', error) }
}
async function submit() {
  if (submitting.value) return
  Object.keys(errors).forEach(key => delete errors[key])
  const validation = validateCpuPurchase(form, { regions: regions.value, instanceTypes: pricedInstanceTypes.value, catalogLoading: loading.value || catalogLoading.value })
  Object.assign(errors, validation)
  if (Object.keys(validation).length) { ElMessage.warning(firstCpuPurchaseError(validation)); return }
  if (displayPrice.value === null) { ElMessage.warning('CPU 价格暂不可用，请刷新后重试'); return }
  submitting.value = true; result.value = null
  if (!requestKey.value) requestKey.value = generateUUID()
  try {
    const res = await purchaseCpu({ ...form, requestKey: requestKey.value, pricing: cpuPricingSnapshot(selectedPrice.value!, form) })
    result.value = normalizeCpuPurchaseResult(res.data)
    if (res.data?.success) {
      try {
        sessionStorage.setItem('cpuPurchaseResult', JSON.stringify({
          form: { ...form },
          spec: selectedPrice.value ? { cpuCores: selectedPrice.value.cpuCores, memGib: selectedPrice.value.memGib } : undefined,
          result: result.value,
        }))
      } catch (error) { console.warn('保存 CPU 创建结果失败:', error) }
      ElMessage.success('CPU 实例已受理，正在创建中')
      router.push('/cpuPurchaseResult')
    }
  } catch (error: any) {
    const message = error?.code === 'ECONNABORTED' || error?.message?.toLowerCase?.().includes('timeout') ? '请求状态未知，请到 CPU 实例列表确认当前状态' : error?.message || 'CPU 实例购买失败'
    result.value = { success: false, message, statusMessage: message, accepted: false }; ElMessage.warning(message)
  } finally { submitting.value = false }
}
onMounted(load)
</script>
<style scoped>.cpu-purchase-page { padding: 24px; background: #fff7e6; min-height: 100%; }.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }.page-header h2 { margin:0 0 6px; }.page-header p { margin:0; color:#8a6f3b; }.el-form { max-width:720px; }.result { margin-top:20px; }.price-hint { margin-left:12px;color:#8a6f3b; }.disk-capacity-error { color:#d14343; }.field-hint { margin-left:8px;color:#8a6f3b;font-size:12px; }</style>
