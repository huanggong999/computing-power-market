<template>
  <div class="purchase-page ignore-purchase-page">
    <div class="product-heading">
      <div class="product-name">算力服务器 {{ purchaseType === 'gpu' ? 'GPU' : 'CPU' }}</div>
      <div class="product-mode">自定义购买</div>
      <div class="purchase-type-switch" role="group" aria-label="实例类型">
        <button type="button" class="purchase-type" :class="{ active: purchaseType === 'gpu' }" :aria-current="purchaseType === 'gpu' ? 'page' : undefined" @click="purchaseType = 'gpu'">GPU 实例</button>
        <button type="button" class="purchase-type" :class="{ active: purchaseType === 'cpu' }" :aria-current="purchaseType === 'cpu' ? 'page' : undefined" @click="purchaseType = 'cpu'">CPU 实例</button>
      </div>
    </div>

    <div v-if="purchaseType === 'gpu'" class="purchase-shell">
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
                  {{ resolveRegionDisplayName(item.regionCode, item.regionName) }}
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

          <div class="config-row">
            <div class="row-label">
              <strong>公网 IP</strong>
              <span>实例是否需要公网访问</span>
            </div>
            <div class="row-control">
              <el-switch v-model="publicIpEnabled" active-text="购买公网 IP" inactive-text="不购买" />
              <div v-if="publicIpEnabled" class="public-network-options">
                <div class="option-grid three">
                  <button type="button" class="option-card" :class="{ active: bandwidthChargeType === 'PayByTraffic' }" @click="bandwidthChargeType = 'PayByTraffic'">
                    <strong>按流量计费</strong><span>按实际出公网流量后付费</span>
                  </button>
                  <button type="button" class="option-card" :class="{ active: bandwidthChargeType === 'PayByBandwidth' }" @click="bandwidthChargeType = 'PayByBandwidth'">
                    <strong>按带宽计费</strong><span>按所选带宽上限计费</span>
                  </button>
                </div>
                <div class="public-bandwidth-control">
                  <span>带宽上限</span>
                  <el-input-number v-model="bandwidthMbps" :min="1" :max="200" :step="1" />
                  <span>Mbps</span>
                </div>
                <div class="hint-line">公网费用将在下一步单独展示；后端费用试算和创建接口接入前，开启公网 IP 的订单暂不可提交。</div>
              </div>
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
          <div v-if="publicIpEnabled" class="price-line">
            <span>公网计费</span>
            <strong>{{ bandwidthChargeType === 'PayByTraffic' ? '按流量' : '按带宽' }} · {{ bandwidthMbps }} Mbps</strong>
          </div>
          <div v-if="publicIpEnabled" class="price-line">
            <span>公网费用</span>
            <strong>待后端试算</strong>
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

    <div v-else class="purchase-shell cpu-purchase-shell">
      <main class="config-panel">
        <section class="config-section">
          <div class="section-title">基础配置</div>
          <div class="config-row">
            <div class="row-label">
              <strong>地域</strong>
              <span>选择 CPU 云主机所在地域</span>
            </div>
            <div class="row-control inline-selects">
              <el-select v-model="cpuForm.region" placeholder="请选择地域">
                <el-option v-for="item in cpuRegions" :key="item.region" :label="item.label || item.region" :value="item.region" />
              </el-select>
              <el-select v-model="cpuForm.zone" placeholder="请选择可用区">
                <el-option v-for="zone in selectedCpuZones" :key="zone" :label="cpuZoneLabel(zone)" :value="zone" />
              </el-select>
              <div v-if="cpuErrors.region || cpuErrors.zone" class="cpu-field-error">{{ cpuErrors.region || cpuErrors.zone }}</div>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label">
              <strong>计费方式</strong>
              <span>按量、包月或包年购买</span>
            </div>
            <div class="row-control option-grid three">
              <button v-for="item in cpuBillingOptions" :key="item.code" type="button" class="option-card" :class="{ active: cpuForm.billingMode === item.code }" @click="cpuForm.billingMode = item.code">
                <strong>{{ item.name }}</strong>
                <span>{{ item.description }}</span>
              </button>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label">
              <strong>CPU 规格</strong>
              <span>仅展示当前计费方式下有价格的纯 CPU 规格</span>
            </div>
            <div class="row-control">
              <el-select v-model="cpuForm.instanceTypeId" class="cpu-wide-select" placeholder="请选择纯 CPU 规格" :loading="cpuLoading || cpuPriceLoading">
                <el-option v-for="item in pricedCpuInstanceTypes" :key="item.instanceTypeId" :label="`${item.instanceTypeId}（${item.cpuCores}核 / ${item.memGib}GiB）`" :value="item.instanceTypeId" />
              </el-select>
              <div v-if="cpuErrors.instanceTypeId || cpuErrors.catalog" class="cpu-field-error">{{ cpuErrors.instanceTypeId || cpuErrors.catalog }}</div>
              <div v-if="cpuCatalogError" class="cpu-field-error">{{ cpuCatalogError }}</div>
              <div v-else-if="!cpuLoading && !cpuPriceLoading && cpuForm.region && !pricedCpuInstanceTypes.length" class="cpu-field-error">当前地域与计费方式暂无可购买 CPU 规格</div>
            </div>
          </div>
        </section>

        <section class="config-section">
          <div class="section-title">磁盘和数量</div>
          <div class="config-row">
            <div class="row-label"><strong>系统盘</strong><span>系统盘容量</span></div>
            <div class="row-control cpu-number-control"><el-input-number v-model="cpuForm.systemDiskGb" :min="20" :max="2048" /> <span>GiB</span><div v-if="cpuErrors.systemDiskGb" class="cpu-field-error">{{ cpuErrors.systemDiskGb }}</div></div>
          </div>
          <div class="config-row">
            <div class="row-label"><strong>数据盘</strong><span>0 表示不购买，购买时最低 20G；合计最低 100G</span></div>
            <div class="row-control cpu-number-control">
              <el-input-number v-model="cpuForm.dataDiskGb" :min="0" :max="8192" :step="20" /> <span>GiB</span>
              <div v-if="cpuErrors.dataDiskGb" class="cpu-field-error">{{ cpuErrors.dataDiskGb }}</div>
              <div v-if="cpuDiskCapacityTooSmall" class="cpu-field-error">磁盘资源最低需要 100G（当前合计 {{ cpuTotalDiskCapacityGb }}G）</div>
            </div>
          </div>
          <div class="config-row">
            <div class="row-label"><strong>购买数量</strong><span>一次购买多台 CPU 实例</span></div>
            <div class="row-control cpu-number-control"><el-input-number v-model="cpuForm.count" :min="1" :max="10" /> <span>台</span><div v-if="cpuErrors.count" class="cpu-field-error">{{ cpuErrors.count }}</div></div>
          </div>
          <div v-if="cpuForm.billingMode !== 'on_demand'" class="config-row">
            <div class="row-label"><strong>购买时长</strong><span>包月或包年时长</span></div>
            <div class="row-control cpu-number-control"><el-input-number v-model="cpuForm.duration" :min="1" :max="36" /> <span>{{ cpuForm.billingMode === 'monthly' ? '月' : '年' }}</span><div v-if="cpuErrors.duration" class="cpu-field-error">{{ cpuErrors.duration }}</div></div>
          </div>
          <el-alert title="CPU 实例独立于 GPU/Kubernetes 集群。服务端默认分配弹性公网 IP，按实际出公网流量后付费；当前接口不支持在购买页切换公网计费方式。" type="info" show-icon :closable="false" />
        </section>
      </main>

      <aside class="summary-panel">
        <div class="summary-card">
          <div class="summary-header"><h3>配置概要</h3></div>
          <div class="price-title">CPU 购买配置</div>
          <div class="price-line"><span>规格</span><strong>{{ selectedCpuType ? `${selectedCpuType.cpuCores}核 / ${selectedCpuType.memGib}GiB` : '请选择 CPU 规格' }}</strong></div>
          <div class="price-line"><span>计费方式</span><strong>{{ selectedCpuBillingName }}</strong></div>
          <div class="price-line"><span>公网 IP</span><strong>默认分配 · 按流量后付费</strong></div>
          <div class="price-line"><span>购买数量</span><strong>{{ cpuForm.count }} 台</strong></div>
          <div class="price-line cpu-unit-price-line"><span>实例单价</span><strong data-testid="cpu-unit-price">{{ cpuLoading || cpuPriceLoading ? '价格加载中…' : selectedCpuUnitPrice === null ? '--' : `¥${selectedCpuUnitPrice.toFixed(4)} / ${selectedCpuPriceUnit}` }}</strong></div>
          <div class="summary-divider" />
          <p class="summary-note">实例费用和公网实际流量费用以服务端账单为准。</p>
          <el-button class="order-btn cpu-order-btn" type="primary" :loading="cpuSubmitting" :disabled="cpuSubmitting || cpuLoading || cpuPriceLoading || selectedCpuUnitPrice === null" @click="submitCpuPurchase">立即购买 CPU 实例</el-button>
          <el-alert v-if="cpuResult" class="cpu-result" :title="cpuResult.statusMessage || cpuResult.message || (cpuResult.success ? '已受理，创建中' : '购买失败')" :type="cpuResult.success ? 'success' : 'error'" show-icon :closable="false"><template #default><span v-if="cpuResult.operation_id">创建任务：{{ cpuResult.operation_id }}，请到 CPU 实例列表查看进度。</span><span v-else>请根据返回的校验结果处理后重试。</span></template></el-alert>
          <el-descriptions v-if="cpuResult?.instances?.length" title="已创建实例" :column="1" border class="cpu-result">
            <el-descriptions-item v-for="(item, index) in cpuResult.instances" :key="index" :label="String(item.instance_id || index)">状态 {{ item.status || '-' }}，公网 IP {{ item.public_ip || '-' }}</el-descriptions-item>
          </el-descriptions>
          <el-button v-if="cpuResult?.success" class="cpu-result" type="primary" @click="router.push('/cloud/gpuInstance?type=cpu')">查看 CPU 实例</el-button>
        </div>
      </aside>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { InfoFilled, RefreshRight, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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
import { gpuBillingLabelMap } from '@/utils/gpuBilling'
import { getCpuInstanceTypes, getCpuPrices, getCpuRegions, getCpuZones, purchaseCpu } from '@/api/instanceV2'
import type { CpuInstanceType, CpuPriceSpec, CpuPurchaseResult, CpuRegion, CpuZoneDetail } from '@/types/instance'
import { firstCpuPurchaseError, hasCpuPurchaseErrors, normalizeCpuPurchaseResult, validateCpuPurchase } from '@/utils/cpuPurchase'
import { cpuDisplayPrice, cpuPricingSnapshot } from '@/utils/cpuPricing'
import { MIN_DISK_CAPACITY_GB, totalDiskCapacityGb } from '@/utils/diskCapacity'
import { resolveRegionDisplayName } from '@/utils/regionDisplay'
import { generateUUID } from '@/utils/uuid'

const router = useRouter()
const route = useRoute()
const purchaseType = ref<'gpu' | 'cpu'>(route.query.type === 'cpu' ? 'cpu' : 'gpu')
const publicIpEnabled = ref(true)
const bandwidthChargeType = ref<'PayByTraffic' | 'PayByBandwidth'>('PayByTraffic')
const bandwidthMbps = ref(5)

const cpuLoading = ref(false)
const cpuSubmitting = ref(false)
const cpuRegions = ref<CpuRegion[]>([])
const cpuZoneDetails = ref<CpuZoneDetail[]>([])
const cpuInstanceTypes = ref<CpuInstanceType[]>([])
const cpuPriceCatalog = ref<CpuPriceSpec[]>([])
const cpuPriceLoading = ref(false)
const cpuCatalogError = ref('')
let cpuPriceRequestId = 0
const cpuResult = ref<CpuPurchaseResult | null>(null)
const cpuRequestKey = ref('')
const cpuForm = reactive({ region: '', zone: '', instanceTypeId: '', billingMode: 'on_demand' as 'on_demand' | 'monthly' | 'yearly', duration: 1, systemDiskGb: 100, dataDiskGb: 0, count: 1 })
const cpuErrors = reactive<Record<string, string>>({})
const cpuBillingOptions = [
  { code: 'on_demand' as const, name: '按量付费', description: '按实际使用时长计费' },
  { code: 'monthly' as const, name: '包月', description: '先付费后使用，价格优惠' },
  { code: 'yearly' as const, name: '包年', description: '适合长期稳定使用' },
]
const selectedCpuZones = computed(() => cpuRegions.value.find(item => item.region === cpuForm.region)?.zones || [])
function fallbackZoneLabel(zone: string) {
  const suffix = zone.match(/([a-zA-Z])$/)?.[1]
  return suffix ? `可用区${suffix.toUpperCase()}` : zone
}
function cpuZoneLabel(zone: string) {
  return cpuZoneDetails.value.find(item => item.zone === zone && (!item.region || item.region === cpuForm.region))?.name || fallbackZoneLabel(zone)
}
const pricedCpuInstanceTypes = computed(() => cpuInstanceTypes.value.filter(item =>
  cpuForm.billingMode !== 'yearly' && cpuPriceCatalog.value.some(price => price.instanceTypeId === item.instanceTypeId && cpuDisplayPrice(price, cpuForm.billingMode) !== null),
))
const selectedCpuType = computed(() => pricedCpuInstanceTypes.value.find(item => item.instanceTypeId === cpuForm.instanceTypeId))
const selectedCpuPriceSpec = computed(() => cpuPriceCatalog.value.find(item => item.instanceTypeId === cpuForm.instanceTypeId))
const selectedCpuUnitPrice = computed(() => cpuDisplayPrice(selectedCpuPriceSpec.value, cpuForm.billingMode))
const selectedCpuPriceUnit = computed(() => cpuForm.billingMode === 'on_demand' ? '小时' : '月')
const selectedCpuBillingName = computed(() => cpuBillingOptions.find(item => item.code === cpuForm.billingMode)?.name || '')
const cpuTotalDiskCapacityGb = computed(() => totalDiskCapacityGb(cpuForm.systemDiskGb, cpuForm.dataDiskGb))
const cpuDiskCapacityTooSmall = computed(() => cpuTotalDiskCapacityGb.value < MIN_DISK_CAPACITY_GB)
const cpuValidationErrors = computed(() => validateCpuPurchase(cpuForm, { regions: cpuRegions.value, instanceTypes: pricedCpuInstanceTypes.value, catalogLoading: cpuLoading.value || cpuPriceLoading.value }))
const canPurchaseCpu = computed(() => !hasCpuPurchaseErrors(cpuValidationErrors.value))

watch(purchaseType, (type) => {
  if (type === 'cpu') void loadCpuPurchaseOptions()
})

watch(() => [cpuForm.region, cpuForm.billingMode], ([region], [previousRegion]) => {
  void loadCpuTypes(region !== previousRegion)
})

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

const volcanoSupportedBillingTypes = new Set(['on_demand', 'hourly', 'monthly'])

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
  if (!volcanoSupportedBillingTypes.has(filters.billingType)) return false
  const value = filters.billingType === 'monthly' ? item.salePriceMonthly : item.salePrice
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
  if (!volcanoSupportedBillingTypes.has(filters.billingType)) return []
  const rows: GpuMarketItem[] = []
  for (const region of volcanoCatalog.value.regions || []) {
    for (const spec of region.gpuSpecs || []) {
      for (const item of spec.instanceTypes || []) {
        const hourlyPrice = resolveExternalPrice(item.salePrice, item.price ?? spec.price)
        const monthlyPrice = resolveExternalPrice(item.salePriceMonthly, item.priceMonthly ?? spec.priceMonthly)
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
          salePrice: hourlyPrice,
          salePriceMonthly: monthlyPrice,
          discountPrice: '', discountRate: '', rentableCount: 1,
          source: 'volcano',
          instanceTypeId: item.instanceTypeId || '',
          gpuCount: item.gpuCount || 0,
          volcanoPrice: filters.billingType === 'monthly' ? item.salePriceMonthly : item.salePrice,
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
    if (!volcanoSupportedBillingTypes.has(filters.billingType)) {
      selectedGpu.value = null
      return
    }
    const currentKey = getGpuKey(selectedGpu.value)
    selectedGpu.value = volcanoGpuList.value.find((item) => getGpuKey(item) === currentKey) || selectedGpu.value
  }
})

onMounted(async () => {
  restoreOrderDraft()
  if (purchaseType.value === 'cpu') {
    await loadCpuPurchaseOptions()
    return
  }
  const initialBillingType = filters.billingType
  const [billingTypeChanged] = await Promise.all([loadMetaData(), loadGpuList(), loadVolcanoCatalog()])
  if (billingTypeChanged && filters.billingType !== initialBillingType) {
    reloadFromFirstPage()
  }
})

async function loadCpuPurchaseOptions() {
  if (cpuRegions.value.length) return
  cpuLoading.value = true
  try {
    const res = await getCpuRegions()
    if (res.data?.success === false) throw new Error('CPU 地域目录暂不可用，请稍后重试')
    cpuRegions.value = res.data?.regions || []
    cpuForm.region = cpuRegions.value[0]?.region || ''
    void loadCpuZoneDetails()
  } catch (error: any) {
    ElMessage.error(error?.message || '加载 CPU 购买选项失败')
  } finally {
    cpuLoading.value = false
  }
}

async function loadCpuZoneDetails() {
  try {
    const res = await getCpuZones()
    if (res.data?.success === false) return
    cpuZoneDetails.value = res.data?.zones || []
  } catch (error) {
    console.warn('加载 CPU 可用区名称失败，将使用可用区编码:', error)
  }
}

async function loadCpuTypes(resetZone: boolean) {
  const requestId = ++cpuPriceRequestId
  const region = cpuForm.region
  const billingMode = cpuForm.billingMode
  const previousType = resetZone ? '' : cpuForm.instanceTypeId
  if (resetZone) cpuForm.zone = selectedCpuZones.value[0] || ''
  cpuForm.instanceTypeId = ''
  cpuInstanceTypes.value = []
  cpuPriceCatalog.value = []
  cpuCatalogError.value = ''
  if (!region || billingMode === 'yearly') {
    cpuPriceLoading.value = false
    return
  }
  cpuPriceLoading.value = true
  try {
    const [typesRes, pricesRes] = await Promise.all([
      getCpuInstanceTypes(region),
      getCpuPrices(region, undefined, billingMode),
    ])
    if (requestId !== cpuPriceRequestId) return
    if (typesRes.data?.success === false || pricesRes.data?.success === false) {
      throw new Error('CPU 规格或价格暂不可用，请稍后重试')
    }
    cpuInstanceTypes.value = typesRes.data?.instance_types || []
    cpuPriceCatalog.value = pricesRes.data?.specs || []
    cpuForm.instanceTypeId = pricedCpuInstanceTypes.value.some(item => item.instanceTypeId === previousType)
      ? previousType : pricedCpuInstanceTypes.value[0]?.instanceTypeId || ''
  } catch (error: any) {
    if (requestId === cpuPriceRequestId) cpuCatalogError.value = error?.message || '加载 CPU 规格或价格失败，请稍后重试'
  } finally {
    if (requestId === cpuPriceRequestId) cpuPriceLoading.value = false
  }
}

async function submitCpuPurchase() {
  if (cpuSubmitting.value) return
  Object.keys(cpuErrors).forEach(key => delete cpuErrors[key])
  Object.assign(cpuErrors, cpuValidationErrors.value)
  if (!canPurchaseCpu.value) {
    ElMessage.warning(firstCpuPurchaseError(cpuValidationErrors.value))
    return
  }
  if (selectedCpuUnitPrice.value === null || !selectedCpuPriceSpec.value) {
    ElMessage.warning('当前 CPU 价格暂不可用，请刷新后重试')
    return
  }
  cpuSubmitting.value = true
  cpuResult.value = null
  if (!cpuRequestKey.value) cpuRequestKey.value = generateUUID()
  try {
    const res = await purchaseCpu({
      ...cpuForm,
      requestKey: cpuRequestKey.value,
      pricing: cpuPricingSnapshot(selectedCpuPriceSpec.value, cpuForm),
    })
    cpuResult.value = normalizeCpuPurchaseResult(res.data)
    if (res.data?.success) {
      try {
        sessionStorage.setItem('cpuPurchaseResult', JSON.stringify({
          form: { ...cpuForm },
          spec: selectedCpuType.value ? { cpuCores: selectedCpuType.value.cpuCores, memGib: selectedCpuType.value.memGib } : undefined,
          result: cpuResult.value,
        }))
      } catch (error) { console.warn('保存 CPU 创建结果失败:', error) }
      ElMessage.success('CPU 实例已受理，正在创建中')
      router.push('/cpuPurchaseResult')
    }
    else ElMessage.error(res.data?.message || 'CPU 实例购买失败')
  } catch (error: any) {
    const message = error?.code === 'ECONNABORTED' || error?.message?.toLowerCase?.().includes('timeout')
      ? '请求状态未知，请到 CPU 实例列表确认当前状态'
      : error?.message || 'CPU 实例购买失败'
    cpuResult.value = { success: false, message, statusMessage: message, accepted: false } as CpuPurchaseResult
    ElMessage.warning(message)
  } finally {
    cpuSubmitting.value = false
  }
}

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
  if (!volcanoSupportedBillingTypes.has(filters.billingType)) {
    volcanoCatalog.value = { regions: [] }
    return
  }
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
    { code: 'on_demand', name: gpuBillingLabelMap.on_demand },
    { code: 'hourly', name: gpuBillingLabelMap.hourly },
    { code: 'monthly', name: gpuBillingLabelMap.monthly },
    { code: 'daily', name: gpuBillingLabelMap.daily },
    { code: 'weekly', name: gpuBillingLabelMap.weekly },
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
    draftVersion: 2,
    resourceId,
    billingType: filters.billingType,
    billingTypeName: selectedBillingTypeName.value,
    duration: durationCount.value,
    quantity: instanceCount.value,
    unitAmount: unitAmount.value,
    configAmount: configAmount.value,
    discountAmount: discountAmount.value,
    totalAmount: totalAmount.value,
    publicIpEnabled: publicIpEnabled.value,
    bandwidthChargeType: bandwidthChargeType.value,
    bandwidthMbps: bandwidthMbps.value,
    prices: selectedGpu.value.source === 'volcano' ? [
      { billingType: 'on_demand', billingTypeName: '按量计费', unitPrice: selectedGpu.value.salePrice ?? selectedGpu.value.price },
      { billingType: 'hourly', billingTypeName: '按小时', unitPrice: selectedGpu.value.salePrice ?? selectedGpu.value.price },
      { billingType: 'monthly', billingTypeName: '包月', unitPrice: selectedGpu.value.salePriceMonthly ?? selectedGpu.value.priceMonthly },
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
    // 公网 IP 默认随实例开通；仅当新版本订单草稿明确关闭时保持关闭
    const draftVersion = Number(draft.draftVersion) || 0
    publicIpEnabled.value = draftVersion >= 2 ? draft.publicIpEnabled !== false : true
    bandwidthChargeType.value = draft.bandwidthChargeType === 'PayByBandwidth' ? 'PayByBandwidth' : 'PayByTraffic'
    bandwidthMbps.value = Number(draft.bandwidthMbps) || 5
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

.purchase-type-switch {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  margin-left: auto;
  border: 1px solid #f1d3a1;
  border-radius: 8px;
  background: #fffdf8;
}
.purchase-type {
  border: 0;
  border-radius: 6px;
  padding: 8px 16px;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
  font-weight: 600;
}
.purchase-type.active {
  color: #fff;
  background: #ff6a00;
}
.purchase-type:not(.active):hover { color: #ff6a00; background: #fff0d4; }

.public-network-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 14px;
  max-width: 720px;
}

.public-bandwidth-control {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4b5563;
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

.cpu-field-error {
  margin-top: 8px;
  color: #d14343;
  font-size: 13px;
  line-height: 1.4;
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
