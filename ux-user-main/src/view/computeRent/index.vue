<template>
  <div class="purchase-confirm-page">
    <header class="product-heading">
      <div class="heading-copy">
        <button class="back-button" type="button" :title="createdOrder ? '返回算力市场' : '返回修改配置'" @click="createdOrder ? goToMarket() : goBack()">
          <el-icon><Back /></el-icon>
        </button>
        <div>
          <div class="product-name">算力服务器 GPU</div>
          <div class="product-mode">自定义购买</div>
        </div>
      </div>
      <div class="purchase-steps" aria-label="购买进度">
        <div class="step complete"><span>1</span>基础配置</div>
        <div class="step-line complete" />
        <div class="step" :class="createdOrder ? 'complete' : 'active'"><span>2</span>镜像与确认</div>
        <div class="step-line" :class="{ complete: createdOrder }" />
        <div class="step" :class="{ active: createdOrder }"><span>3</span>创建完成</div>
      </div>
    </header>

    <el-alert
      v-if="error"
      :title="error"
      type="error"
      closable
      class="error-alert"
      @close="error = ''"
    />

    <div v-loading="loading" class="purchase-shell">
      <main v-if="createdOrder" class="creation-result">
        <div class="result-status">
          <div class="result-icon"><el-icon><Check /></el-icon></div>
          <div>
            <h1>{{ isVolcanoResourceCreating ? '资源创建中' : '实例已提交创建' }}</h1>
            <p v-if="isVolcanoResourceCreating">后台已确认资源创建，预计需要 15-30 分钟，请耐心等待，期间无需重复提交。</p>
            <p v-else>订单已支付，GPU 实例正在创建并开机。资源就绪前无需重复提交。</p>
          </div>
        </div>

        <div class="result-details">
          <div class="result-detail-item">
            <span>订单号</span>
            <strong>{{ createdOrder.orderNo || '-' }}</strong>
          </div>
          <div class="result-detail-item">
            <span>实例 ID</span>
            <strong>{{ createdOrder.instanceInfo?.instanceId || '正在分配' }}</strong>
          </div>
          <div class="result-detail-item">
            <span>实例名称</span>
            <strong>{{ createdOrder.instanceInfo?.instanceName || '正在分配' }}</strong>
          </div>
          <div class="result-detail-item">
            <span>创建状态</span>
            <strong class="status-processing">{{ creationStatusDisplay }}</strong>
          </div>
          <div v-if="isVolcanoResourceCreating || podStatusMessage" class="result-detail-item wide">
            <span>当前动作</span>
            <strong>{{ isVolcanoResourceCreating ? resourceCreationMessage : podStatusMessage }}</strong>
          </div>
          <div class="result-detail-item">
            <span>实付金额</span>
            <strong class="result-price">¥ {{ formatMoney(createdOrder.paidAmount) }}</strong>
          </div>
          <div v-if="createdOrder.pricing?.unit_price != null" class="result-detail-item">
            <span>实际单价</span>
            <strong>¥ {{ formatMoney(createdOrder.pricing.unit_price) }}<span v-if="createdOrder.pricing.unit">/{{ createdOrder.pricing.unit }}</span></strong>
          </div>
          <div class="result-detail-item">
            <span>创建时间</span>
            <strong>{{ createdOrder.createTime || '-' }}</strong>
          </div>
        </div>

        <el-alert
          :title="creationNotice"
          type="info"
          :closable="false"
          show-icon
        />

        <div class="result-actions">
          <el-button class="secondary-result-action" @click="goToOrder">查看订单</el-button>
          <el-button class="primary-result-action" @click="goToInstance">管理实例</el-button>
        </div>
      </main>

      <main v-else-if="gpuResource" class="config-panel">
        <section class="config-section">
          <div class="section-heading">
            <div>
              <div class="section-title">已选基础配置</div>
              <div class="section-subtitle">以下配置来自上一步，创建前请再次核对</div>
            </div>
            <el-button class="edit-config-button" @click="goBack">
              <el-icon><EditPen /></el-icon>
              修改配置
            </el-button>
          </div>

          <div class="resource-overview">
            <div class="resource-identity">
              <div class="resource-badge">GPU</div>
              <div>
                <strong>{{ gpuResource.resourceNo || gpuResource.machineId || 'GPU 算力实例' }}</strong>
                <span>{{ gpuResource.machineId || gpuResource.machineUuid || '-' }}</span>
              </div>
            </div>
            <div class="overview-price">
              <span>单卡价格</span>
              <strong>¥ {{ currentPrice }}/{{ billingUnit }}</strong>
            </div>
          </div>

          <div class="configuration-grid">
            <div class="config-item">
              <span>地域 / 可用区</span>
              <strong>{{ selectedRegionText }}</strong>
            </div>
            <div class="config-item">
              <span>付费类型</span>
              <strong>{{ billingTypeNameMap[rentConfig.billingType] || rentConfig.billingType }}</strong>
            </div>
            <div class="config-item">
              <span>GPU 规格</span>
              <strong>{{ gpuResource.model || '-' }} / {{ gpuResource.vram || '-' }}</strong>
            </div>
            <div class="config-item">
              <span>购买数量</span>
              <strong>{{ rentConfig.quantity }} 卡</strong>
            </div>
            <div class="config-item">
              <span>vCPU / 内存</span>
              <strong>{{ instanceCpu }} vCPU / {{ instanceMemory }}</strong>
            </div>
            <div class="config-item">
              <span>购买时长</span>
              <strong>{{ rentConfig.duration }} {{ billingUnitText }}</strong>
            </div>
            <div class="config-item">
              <span>驱动 / CUDA</span>
              <strong>{{ gpuResource.gpuDriver || '-' }} / {{ gpuResource.cudaVersion || '-' }}</strong>
            </div>
            <div class="config-item">
              <span>续费方式</span>
              <strong>到期前手动续费</strong>
            </div>
          </div>
        </section>

        <section class="config-section">
          <div class="section-title">镜像与存储</div>

          <div class="config-row">
            <div class="row-label">
              <strong>镜像来源</strong>
              <span>选择实例的运行环境</span>
            </div>
            <div class="row-control">
              <el-radio-group v-model="mirrorSource" class="source-tabs">
                <el-radio-button label="base">基础镜像</el-radio-button>
                <el-radio-button label="community">社区镜像</el-radio-button>
                <el-radio-button label="my">我的镜像</el-radio-button>
              </el-radio-group>
              <div class="hint-line">
                基础镜像已预装常用深度学习框架和运行环境，实例创建后仍可安装其他软件。
              </div>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label required-label">
              <strong>运行镜像</strong>
              <span>依次选择框架及版本</span>
            </div>
            <div class="row-control">
              <el-popover
                v-if="mirrorSource === 'base' && baseMirrorTree.length > 0"
                v-model:visible="baseMirrorPanelVisible"
                trigger="click"
                placement="bottom-start"
                :width="840"
                :show-arrow="true"
                popper-class="base-mirror-popover"
              >
                <template #reference>
                  <div class="base-mirror-selector" :class="{ selected: selectedBaseMirrorLabel }">
                    <span>{{ selectedBaseMirrorLabel || '请选择框架、Python 与 CUDA 版本' }}</span>
                    <el-icon><ArrowDown /></el-icon>
                  </div>
                </template>
                <div class="base-mirror-cascader">
                  <div class="cascader-column">
                    <div class="cascader-title">框架名称</div>
                    <button
                      v-for="framework in baseMirrorTree"
                      :key="framework.name"
                      type="button"
                      class="cascader-option"
                      :class="{ active: selectedBaseMirrorType === framework.name }"
                      @click="selectBaseMirrorType(framework.name)"
                    >
                      <span>{{ framework.name }}</span>
                      <el-icon><ArrowRight /></el-icon>
                    </button>
                  </div>
                  <div class="cascader-column">
                    <div class="cascader-title">框架版本</div>
                    <button
                      v-for="frameworkVersion in selectedFrameworkVersions"
                      :key="frameworkVersion.version"
                      type="button"
                      class="cascader-option"
                      :class="{ active: selectedBaseFrameworkVersion === frameworkVersion.version }"
                      @click="selectBaseFrameworkVersion(frameworkVersion.version)"
                    >
                      <span>{{ frameworkVersion.version }}</span>
                      <el-icon><ArrowRight /></el-icon>
                    </button>
                  </div>
                  <div class="cascader-column">
                    <div class="cascader-title">Python 版本</div>
                    <button
                      v-for="pythonVersion in selectedPythonVersions"
                      :key="pythonVersion.version"
                      type="button"
                      class="cascader-option"
                      :class="{ active: selectedBasePythonVersion === pythonVersion.version }"
                      @click="selectBasePythonVersion(pythonVersion.version)"
                    >
                      <span>{{ pythonVersion.version }}</span>
                      <el-icon><ArrowRight /></el-icon>
                    </button>
                  </div>
                  <div class="cascader-column">
                    <div class="cascader-title">CUDA 版本</div>
                    <button
                      v-for="cudaOption in selectedCudaOptions"
                      :key="cudaOption.value"
                      type="button"
                      class="cascader-option final"
                      :class="{ active: rentConfig.mirrorId === cudaOption.mirror.id }"
                      @click="selectBaseMirror(cudaOption.mirror)"
                    >
                      <span>{{ cudaOption.label }}</span>
                    </button>
                  </div>
                </div>
              </el-popover>

              <el-select
                v-if="mirrorSource !== 'base' && filteredMirrors.length > 0"
                v-model="rentConfig.mirrorId"
                class="mirror-select"
                placeholder="请选择镜像"
                @change="onMirrorChange"
              >
                <el-option
                  v-for="mirror in filteredMirrors"
                  :key="mirror.id"
                  :label="formatMirrorLabel(mirror)"
                  :value="mirror.id"
                />
              </el-select>
              <el-select
                v-if="mirrorSource !== 'base' && rentConfig.mirrorId && versionList.length > 0"
                v-model="rentConfig.versionId"
                class="mirror-select version-select"
                placeholder="请选择版本"
              >
                <el-option
                  v-for="version in versionList"
                  :key="version.id"
                  :label="version.version"
                  :value="version.id"
                />
              </el-select>
              <div v-if="mirrorSource !== 'base' && filteredMirrors.length === 0" class="empty-inline">
                当前暂无可用{{ mirrorSource === 'community' ? '社区镜像' : '自定义镜像' }}
              </div>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label">
              <strong>系统盘</strong>
              <span>随实例创建</span>
            </div>
            <div class="row-control storage-line">
              <div class="storage-type">系统盘</div>
              <strong>{{ systemDiskText }}</strong>
            </div>
          </div>

          <div class="config-row">
            <div class="row-label">
              <strong>数据盘</strong>
              <span>保存模型和训练数据</span>
            </div>
            <div class="row-control storage-control">
              <div class="storage-line">
                <div class="storage-type">数据盘</div>
                <strong>{{ dataDiskText }}</strong>
              </div>
              <el-checkbox v-if="maxExpandSize > 0" v-model="needExpand">扩容数据盘</el-checkbox>
              <div v-if="needExpand" class="expand-input">
                <el-input-number v-model="expandSize" :min="1" :max="maxExpandSize" />
                <span>GB，可扩容上限 {{ expandableText }}</span>
              </div>
            </div>
          </div>
        </section>

        <section
          ref="agreementSectionRef"
          class="config-section confirmation-section"
          :class="{ 'needs-agreement': !agreed, 'agreement-prompt-active': agreementPromptActive }"
        >
          <div class="section-title">服务协议</div>
          <div class="agreement-confirm-box">
            <el-checkbox v-model="agreed">
              <span class="agreement-copy">
                <strong>我已阅读并同意《逸云数智产品和服务协议》</strong>
                <span>并确认所选地域、规格、镜像及计费配置无误。</span>
              </span>
            </el-checkbox>
            <div v-if="!agreed" class="agreement-warning">
              <el-icon><WarningFilled /></el-icon>
              <strong>请勾选以上协议后再确认创建</strong>
            </div>
            <div v-else class="agreement-confirmed">
              <el-icon><CircleCheckFilled /></el-icon>
              已确认服务协议
            </div>
          </div>
        </section>
      </main>

      <aside v-if="!createdOrder && gpuResource" class="summary-panel">
        <div class="summary-card">
          <div class="summary-header">
            <h3>配置概要</h3>
            <span class="confirm-badge">待确认</span>
          </div>

          <div class="summary-block">
            <div class="summary-block-title">算力配置</div>
            <div class="summary-line"><span>地域</span><strong>{{ selectedRegionText }}</strong></div>
            <div class="summary-line"><span>实例规格</span><strong>{{ gpuResource.model }} / {{ gpuResource.vram }}</strong></div>
            <div class="summary-line"><span>GPU 数量</span><strong>{{ rentConfig.quantity }} 卡</strong></div>
            <div class="summary-line"><span>购买时长</span><strong>{{ rentConfig.duration }} {{ billingUnitText }}</strong></div>
            <div class="summary-line"><span>计费方式</span><strong>{{ billingTypeNameMap[rentConfig.billingType] }}</strong></div>
            <div class="summary-line"><span>续费方式</span><strong>手动续费</strong></div>
          </div>

          <div class="summary-block">
            <div class="summary-block-title">系统配置</div>
            <div class="summary-line image-line"><span>镜像</span><strong>{{ selectedImageSummary }}</strong></div>
            <div class="summary-line"><span>系统盘</span><strong>{{ systemDiskText }}</strong></div>
            <div class="summary-line"><span>数据盘</span><strong>{{ instanceDataDisk }}</strong></div>
          </div>

          <div class="summary-block price-block">
            <div class="summary-block-title">价格概要</div>
            <div class="summary-line"><span>配置费用</span><strong>¥ {{ formatMoney(feeResult?.subtotal) }}</strong></div>
            <div class="summary-line discount"><span>优惠</span><strong>- ¥ {{ formatMoney(feeResult?.discount) }}</strong></div>
            <button type="button" class="fee-detail-link" @click="showFeeDetail = true">查看费用明细</button>
          </div>

          <div class="account-line">
            <span>可用余额</span>
            <strong>¥ {{ formatMoney(accountInfo?.availableBalance) }}</strong>
          </div>
          <el-alert v-if="isBalanceInsufficient" type="warning" :closable="false" class="balance-warning">
            <template #title>
              <span>可用余额不足，请充值后再创建</span>
              <button type="button" class="recharge-inline-link" @click="goToRecharge">去充值</button>
            </template>
          </el-alert>

          <div class="summary-total">
            <span>应付金额</span>
            <strong><small>¥</small>{{ configFee }}</strong>
          </div>
          <div class="daily-price">折合日常费用 ¥{{ dailyFee }}/日</div>

          <div class="summary-actions">
            <el-button class="back-config-action" @click="goBack">上一步</el-button>
            <el-button v-if="!agreed" class="submit-action agreement-required-action" @click="promptAgreement">
              请先勾选协议
            </el-button>
            <el-tooltip v-else-if="!canSubmit && submitDisabledReason" :content="submitDisabledReason" placement="top">
              <el-button class="submit-action" :loading="submitting" disabled>确认并创建</el-button>
            </el-tooltip>
            <el-button v-else class="submit-action" :loading="submitting" @click="handleSubmit">确认并创建</el-button>
          </div>
          <p class="summary-note">确认创建后将按当前配置计费，成功后可查看订单或进入实例列表。</p>
        </div>
      </aside>

      <div v-if="!loading && !gpuResource && error" class="error-state">
        <el-empty description="加载失败">
          <template #image><el-icon :size="64" color="#ff6a00"><CircleClose /></el-icon></template>
          <p>{{ error }}</p>
          <el-button type="primary" @click="goBack">返回算力市场</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 费用明细弹窗 -->
    <el-dialog v-model="showFeeDetail" title="费用明细" width="520px" align-center>
      <div class="fee-detail-content">
        <div class="fee-formula">配置费用 = 单价 × GPU数量 × 时长</div>
        <div class="fee-detail-table">
          <div class="fee-detail-header">
            <span>计费项</span>
            <span>原价</span>
            <span>折扣金额</span>
            <span>最终价格</span>
          </div>
          <div class="fee-detail-row">
            <span>实例费用</span>
            <span>¥{{ formatMoney(feeResult?.subtotal) }}</span>
            <span class="discount">-¥{{ formatMoney(feeResult?.discount) }}</span>
            <span class="final">¥{{ formatMoney(feeResult?.total) }}</span>
          </div>
          <div class="fee-detail-row">
            <span>数据盘费用</span>
            <span>¥{{ formatMoney(feeResult?.diskFee ?? 0) }}</span>
            <span class="discount">-¥0.00</span>
            <span class="final">¥{{ formatMoney(feeResult?.diskFee ?? 0) }}</span>
          </div>
        </div>
        <el-divider />
        <div class="fee-detail-summary">
          <div class="summary-row">
            <span>会员折扣</span>
            <span>{{ feeResult?.discount > 0 ? '有' : '无' }}</span>
          </div>
          <div class="summary-row total">
            <span>合计</span>
            <span class="final-price">¥{{ formatMoney(feeResult?.total) }}</span>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="createProgressVisible"
      title="正在创建并开机"
      width="460px"
      align-center
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="true"
    >
      <div class="create-progress-dialog">
        <el-progress :percentage="createProgressPercent" :stroke-width="10" :show-text="false" />
        <div class="create-progress-title">{{ createProgressTitle }}</div>
        <div class="create-progress-desc">
          已等待 {{ createElapsedText }}，系统正在{{ createProgressAction }}，期间请勿重复提交。
        </div>
        <div v-if="podStatusDetail" class="create-progress-status">
          <div v-if="podStatusDetail.phase"><span>阶段</span><strong>{{ podStatusDetail.phase }}</strong></div>
          <div v-if="podStatusDetail.node"><span>节点</span><strong>{{ podStatusDetail.node }}</strong></div>
          <div v-if="podStatusDetail.reason"><span>原因</span><strong>{{ podStatusDetail.reason }}</strong></div>
        </div>
        <div v-if="podStatusMessage" class="create-progress-message">{{ podStatusMessage }}</div>
        <div v-if="podStatusEvents.length" class="create-progress-events">
          <div v-for="(event, index) in podStatusEvents" :key="index">{{ event }}</div>
        </div>
        <div class="create-progress-tip">
          {{ createProgressTip }}
        </div>
      </div>
      <template #footer>
        <el-button @click="closeCreateProgressDialog">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, ArrowRight, Back, Check, CircleCheckFilled, CircleClose, EditPen, WarningFilled } from '@element-plus/icons-vue'
import {
  getGpuResourceDetail,
  getMirrorList,
  getMirrorVersions,
  getAccountInfo,
  calculateRentFee,
  createRentOrder,
  getPodStatus,
  type MirrorItem,
  type MirrorVersion,
  type AccountInfo,
  type FeeResult,
  type OrderResult,
  type PodStatusResult,
  type PodCreateRequest
} from '@/api/computeRent'

// ==================== 路由和导航 ====================

const route = useRoute()
const router = useRouter()

function goBack() {
  router.push({
    path: '/computeListNew',
    query: { restoreDraft: '1' }
  })
}

function goToMarket() {
  router.push('/computeListNew')
}

function goToRecharge() {
  router.push('/rechargeCenter')
}

function goToOrder() {
  if (!createdOrder.value?.orderNo) return
  router.push({ path: '/order', query: { orderNo: createdOrder.value.orderNo } })
}

function goToInstance() {
  router.push({
    path: '/cloud/gpuInstance',
    query: {
      createSuccess: '1',
      orderNo: createdOrder.value?.orderNo || '',
      instanceId: createdOrder.value?.instanceInfo?.instanceId || ''
    }
  })
}

// ==================== 状态管理 ====================

const loading = ref(false)
const submitting = ref(false)
const createErrorConfirming = ref(false)
const error = ref('')

const gpuResource = ref<any>(null)
const mirrorList = ref<MirrorItem[]>([])
const versionList = ref<MirrorVersion[]>([])
const accountInfo = ref<AccountInfo | null>(null)
const feeResult = ref<FeeResult | null>(null)
const createdOrder = ref<OrderResult | null>(null)
type BillingType = 'on_demand' | 'hourly' | 'daily' | 'weekly' | 'monthly'

const rentConfig = reactive({
  mirrorId: '',
  versionId: '',
  billingType: 'hourly' as BillingType,
  quantity: 1,
  duration: 1
})

const mirrorSource = ref<'base' | 'community' | 'my'>('base')
const selectedBaseMirrorType = ref('')
const selectedBaseFrameworkVersion = ref('')
const selectedBasePythonVersion = ref('')
const baseMirrorPanelVisible = ref(false)
const needExpand = ref(false)
const expandSize = ref(1)
const showFeeDetail = ref(false)
const agreed = ref(false)
const agreementSectionRef = ref<HTMLElement | null>(null)
const agreementPromptActive = ref(false)
const createProgressVisible = ref(false)
const createElapsedSeconds = ref(0)
const creatingPodName = ref('')
const podPollingStartedAt = ref(0)
const podStatus = ref<PodStatusResult | null>(null)
const podStatusError = ref('')
let createProgressTimer: ReturnType<typeof setInterval> | null = null
let createProgressDelayTimer: ReturnType<typeof setTimeout> | null = null
let podStatusTimer: ReturnType<typeof setInterval> | null = null
let feeCalculateSeq = 0

interface OrderDraft {
  source?: string
  resourceId?: number
  billingType?: BillingType
  billingTypeName?: string
  duration?: number
  quantity?: number
  unitAmount?: number
  configAmount?: number
  discountAmount?: number
  totalAmount?: number
  prices?: Array<Record<string, any>>
  regionCode?: string
  regionName?: string
  zoneCode?: string
  zoneName?: string
  resource?: Record<string, any>
}

const orderDraft = ref<OrderDraft | null>(null)

interface BaseMirrorPythonGroup {
  version: string
  mirrors: MirrorItem[]
}

interface BaseMirrorFrameworkGroup {
  version: string
  pythonVersions: BaseMirrorPythonGroup[]
}

interface BaseMirrorFramework {
  name: string
  versions: BaseMirrorFrameworkGroup[]
}

// ==================== 计算属性 ====================

const billingTypeNameMap: Record<string, string> = {
  on_demand: '按量计费',
  hourly: '按小时',
  daily: '包日',
  weekly: '包周',
  monthly: '包月'
}

const billingUnitMap: Record<string, string> = {
  on_demand: '时',
  hourly: '时',
  daily: '日',
  weekly: '周',
  monthly: '月'
}

const billingModeMap: Record<string, string> = {
  on_demand: 'on_demand',
  hourly: 'on_demand',
  daily: 'daily',
  weekly: 'weekly',
  monthly: 'monthly'
}

const billingUnitTextMap: Record<string, string> = {
  on_demand: '小时',
  hourly: '小时',
  daily: '天',
  weekly: '周',
  monthly: '月'
}

const billingHourFactorMap: Record<string, number> = {
  on_demand: 1,
  hourly: 1,
  daily: 24,
  weekly: 24 * 7,
  monthly: 24 * 30
}

const billingUnit = computed(() => {
  return billingUnitMap[rentConfig.billingType] || '时'
})

const billingUnitText = computed(() => {
  return billingUnitTextMap[rentConfig.billingType] || '小时'
})

const currentPriceItem = computed(() => {
  if (!gpuResource.value?.prices) return null
  return gpuResource.value.prices.find((p: any) => p.billingType === rentConfig.billingType)
})

const currentPrice = computed(() => {
  const price = currentPriceItem.value
  if (!price) return '--'
  return formatMoney(getEffectivePrice(price))
})

const perGpuCpu = computed(() => {
  if (!gpuResource.value) return 0
  const gpuCount = gpuResource.value.gpuCount || gpuResource.value.totalCount || 1
  return Math.floor((gpuResource.value.cpuCores || 0) / gpuCount)
})

const perGpuMemoryNum = computed(() => {
  if (!gpuResource.value?.memory) return 0
  const match = String(gpuResource.value.memory).match(/(\d+)/)
  return match ? parseInt(match[1]) : 0
})

const instanceCpu = computed(() => {
  return perGpuCpu.value * rentConfig.quantity
})

const instanceMemory = computed(() => {
  const num = perGpuMemoryNum.value
  const gpuCount = gpuResource.value?.gpuCount || gpuResource.value?.totalCount || 1
  return `${Math.floor((num / gpuCount) * rentConfig.quantity)}GB`
})

const systemDiskText = computed(() => formatResourceCapacity(gpuResource.value?.systemDisk))

const dataDiskText = computed(() => formatResourceCapacity(gpuResource.value?.dataDisk))

const expandableText = computed(() => formatResourceCapacity(gpuResource.value?.expandable))

const instanceDataDisk = computed(() => {
  const base = dataDiskText.value
  if (!needExpand.value || expandSize.value <= 0) {
    return base
  }
  return `${base} + 扩容 ${expandSize.value} GB`
})

const maxExpandSize = computed(() => {
  return capacityToGb(gpuResource.value?.expandable)
})

const filteredMirrors = computed(() => {
  if (mirrorSource.value === 'base') {
    // 基础镜像包含所有官方预置镜像
    return mirrorList.value
  } else if (mirrorSource.value === 'community') {
    // 社区镜像需要后端支持，目前返回空
    return mirrorList.value.filter(m => m.type === 'community')
  }
  return []
})

const baseMirrorTree = computed<BaseMirrorFramework[]>(() => {
  const frameworkMap = new Map<string, Map<string, Map<string, MirrorItem[]>>>()
  mirrorList.value.forEach((mirror) => {
    if (!mirror.name) return
    const frameworkName = mirror.name
    const frameworkVersion = getFrameworkVersion(mirror)
    const pythonVersion = getPythonVersion(mirror)
    if (!frameworkMap.has(frameworkName)) {
      frameworkMap.set(frameworkName, new Map())
    }
    const versionMap = frameworkMap.get(frameworkName)!
    if (!versionMap.has(frameworkVersion)) {
      versionMap.set(frameworkVersion, new Map())
    }
    const pythonMap = versionMap.get(frameworkVersion)!
    if (!pythonMap.has(pythonVersion)) {
      pythonMap.set(pythonVersion, [])
    }
    pythonMap.get(pythonVersion)!.push(mirror)
  })
  return Array.from(frameworkMap.entries()).map(([name, versionMap]) => ({
    name,
    versions: Array.from(versionMap.entries()).map(([version, pythonMap]) => ({
      version,
      pythonVersions: Array.from(pythonMap.entries()).map(([pythonVersion, mirrors]) => ({
        version: pythonVersion,
        mirrors
      }))
    }))
  }))
})

const selectedMirrorVersion = computed(() => {
  if (!rentConfig.versionId) return null
  return versionList.value.find(version => version.id === rentConfig.versionId) || null
})

const selectedFrameworkVersions = computed(() => {
  return baseMirrorTree.value.find(item => item.name === selectedBaseMirrorType.value)?.versions || []
})

const selectedPythonVersions = computed(() => {
  return selectedFrameworkVersions.value.find(item => item.version === selectedBaseFrameworkVersion.value)?.pythonVersions || []
})

const selectedCudaOptions = computed(() => {
  const pythonGroup = selectedPythonVersions.value.find(item => item.version === selectedBasePythonVersion.value)
  return (pythonGroup?.mirrors || []).map((mirror) => ({
    label: getCudaVersion(mirror),
    value: `${mirror.id}-${getCudaVersion(mirror)}`,
    mirror
  }))
})

const selectedBaseMirrorLabel = computed(() => {
  const mirror = mirrorList.value.find(item => item.id === rentConfig.mirrorId)
  if (!mirror || mirrorSource.value !== 'base') return ''
  return [
    mirror.name,
    getFrameworkVersion(mirror),
    getPythonVersion(mirror),
    getCudaVersion(mirror)
  ].filter(Boolean).join(' / ')
})

const selectedRegionText = computed(() => {
  const region = orderDraft.value?.regionName || gpuResource.value?.region || gpuResource.value?.regionName
  const zone = orderDraft.value?.zoneName || gpuResource.value?.zone || gpuResource.value?.zoneName
  return [region, zone].filter(Boolean).join(' / ') || '默认地域'
})

const selectedImageSummary = computed(() => {
  if (!rentConfig.mirrorId) return '待选择'
  if (selectedBaseMirrorLabel.value) return selectedBaseMirrorLabel.value
  const mirror = mirrorList.value.find(item => item.id === rentConfig.mirrorId)
  const version = selectedMirrorVersion.value?.version
  return [mirror?.name, version].filter(Boolean).join(' / ') || '已选择'
})

const dailyFee = computed(() => {
  const unitPrice = feeResult.value?.unitPrice
  if (unitPrice === undefined || unitPrice === null) return '0.00'
  const type = rentConfig.billingType
  let daily = 0
  if (type === 'on_demand' || type === 'hourly') {
    daily = unitPrice * rentConfig.quantity * 24
  } else if (type === 'daily') {
    daily = unitPrice * rentConfig.quantity
  } else if (type === 'weekly') {
    daily = unitPrice * rentConfig.quantity / 7
  } else if (type === 'monthly') {
    daily = unitPrice * rentConfig.quantity / 30
  }
  return formatMoney(daily)
})

const configFee = computed(() => {
  const total = feeResult.value?.total
  if (total === undefined || total === null) return '0.00'
  return formatMoney(total)
})

const isBalanceInsufficient = computed(() => {
  if (!accountInfo.value) return false
  const balance = accountInfo.value.availableBalance || 0
  const total = feeResult.value?.total || 0
  return balance < total
})

const canSubmit = computed(() => {
  if (submitting.value) return false
  if (createErrorConfirming.value) return false
  if (!feeResult.value) return false
  if (!agreed.value) return false
  if (!rentConfig.mirrorId || !rentConfig.versionId) return false
  if (!currentPriceItem.value) return false
  if (!rentConfig.quantity || rentConfig.quantity <= 0) return false
  if (!rentConfig.duration || rentConfig.duration <= 0) return false
  if (isBalanceInsufficient.value) return false
  return true
})

const submitDisabledReason = computed(() => {
  if (createErrorConfirming.value) return '请先确认创建失败提示'
  if (!feeResult.value) return '费用计算失败，请稍后重试'
  if (!rentConfig.mirrorId || !rentConfig.versionId) return '请先选择镜像和版本'
  if (!agreed.value) return '请先阅读并同意产品和服务协议'
  if (!currentPriceItem.value) return '当前计费方式未维护有效价格'
  if (!rentConfig.quantity || rentConfig.quantity <= 0) return 'GPU数量无效'
  if (!rentConfig.duration || rentConfig.duration <= 0) return '购买时长无效'
  if (isBalanceInsufficient.value) return '可用余额不足，请充值'
  return ''
})

const createElapsedText = computed(() => {
  const minutes = Math.floor(createElapsedSeconds.value / 60)
  const seconds = createElapsedSeconds.value % 60
  if (minutes <= 0) return `${seconds} 秒`
  return `${minutes} 分 ${seconds.toString().padStart(2, '0')} 秒`
})

const createProgressPercent = computed(() => {
  const status = normalizedPodStatus.value
  if (isPodRunningStatus(status)) return 100
  if (status === 'pulling_image') return Math.min(88, Math.max(35, Math.round((createElapsedSeconds.value / 180) * 88)))
  if (status === 'creating') return Math.min(70, Math.max(12, Math.round((createElapsedSeconds.value / 180) * 70)))
  if (isPodTerminalErrorStatus(status)) return 100
  return Math.min(95, Math.max(8, Math.round((createElapsedSeconds.value / 180) * 95)))
})

const normalizedPodStatus = computed(() => String(podStatus.value?.status || '').trim().toLowerCase())

// 创建接口成功返回即表示后台已受理资源创建，后续状态由实例列表继续跟踪。
const isVolcanoOrder = computed(() => createdOrder.value != null && gpuResource.value?.source === 'volcano')

const isVolcanoResourceCreating = computed(() => {
  if (!isVolcanoOrder.value) return false
  if (isPodRunningStatus(normalizedPodStatus.value)) return false
  return !['error', 'failed', 'stopped'].includes(normalizedPodStatus.value)
})

const creationStatusDisplay = computed(() => {
  if (isVolcanoResourceCreating.value) return '资源创建中'
  return podDisplayStatus.value
})

const resourceCreationMessage = '正在等待资源分配、实例创建和镜像初始化。'

const creationNotice = computed(() => {
  if (isVolcanoResourceCreating.value) {
    return '资源通常需要 15-30 分钟创建完成，请耐心等待，可前往实例列表查看实时状态。订单中心会长期保留本次配置、费用与支付记录。'
  }
  return '实例创建通常需要 1-3 分钟，可前往实例列表查看实时状态。订单中心会长期保留本次配置、费用与支付记录。'
})

const podDisplayStatus = computed(() => {
  if (isEarlyPodNotFound(normalizedPodStatus.value)) return '等待 Pod 创建'
  return podStatus.value?.display_status || podStatus.value?.displayStatus || statusDisplayMap[normalizedPodStatus.value] || '创建中'
})

const createProgressTitle = computed(() => `实例${podDisplayStatus.value}`)

const createProgressAction = computed(() => actionDisplayMap[normalizedPodStatus.value] || podDisplayStatus.value)

const createProgressTip = computed(() => {
  if (podStatusError.value) return `状态查询暂时不可用：${podStatusError.value}`
  if (isEarlyPodNotFound(normalizedPodStatus.value)) return 'Pod 资源还在初始化，系统会继续获取创建阶段。'
  if (isPodTerminalErrorStatus(normalizedPodStatus.value)) return '创建过程出现异常，请根据返回信息排查或稍后重试。'
  if (isPodRunningStatus(normalizedPodStatus.value)) return '实例已就绪，正在整理订单结果。'
  return '正在持续获取 Pod 当前阶段，创建完成后将展示订单结果，可继续查看订单或管理实例。'
})

const podStatusDetail = computed(() => podStatus.value)

const podStatusMessage = computed(() => {
  if (isEarlyPodNotFound(normalizedPodStatus.value)) {
    return '正在等待后端创建 Pod 资源，稍后会显示调度、镜像下载或运行状态。'
  }
  return podStatus.value?.message || ''
})

const podStatusEvents = computed(() => {
  const events = podStatus.value?.events || []
  return events.slice(-3).map((event) => {
    if (Array.isArray(event)) return event.filter(Boolean).join('：')
    if (typeof event === 'object' && event) return [event.reason, event.message].filter(Boolean).join('：') || JSON.stringify(event)
    return String(event || '')
  }).filter(Boolean)
})

const statusDisplayMap: Record<string, string> = {
  creating: '创建中',
  pending: '创建中',
  scheduling: '创建中',
  pulling_image: '镜像下载中',
  running: 'Running',
  error: '异常',
  failed: '异常',
  stopped: '已停止',
  not_found: '未找到'
}

const actionDisplayMap: Record<string, string> = {
  creating: '调度 Pod 和创建容器',
  pending: '等待 Pod 调度',
  scheduling: '调度 Pod',
  pulling_image: '拉取容器镜像',
  running: '启动实例',
  error: '处理异常状态',
  failed: '处理异常状态',
  stopped: '检查停止状态',
  not_found: '查找 Pod 资源'
}

// ==================== 方法 ====================

function formatMoney(value: number | string | undefined): string {
  if (value === undefined || value === null) return '0.00'
  const num = typeof value === 'string' ? parseFloat(value) : value
  if (isNaN(num)) return '0.00'
  return num.toFixed(2)
}

function getEffectivePrice(price: any): number {
  const unitPrice = Number(price?.unitPrice || 0)
  const discountPrice = Number(price?.discountPrice || 0)
  if (discountPrice > 0 && discountPrice < unitPrice) return discountPrice
  return unitPrice
}

function formatMirrorLabel(mirror: MirrorItem): string {
  return mirror.baseImage ? `${mirror.name} / ${mirror.baseImage}` : mirror.name
}

function getMirrorText(mirror: MirrorItem): string {
  return `${mirror.baseImage || ''} ${mirror.description || ''} ${mirror.imageAddress || ''}`.trim()
}

function getFrameworkVersion(mirror: MirrorItem): string {
  const text = getMirrorText(mirror)
  const escapedName = mirror.name.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const withoutName = text.replace(new RegExp(escapedName, 'ig'), ' ')
  const versionMatch = withoutName.match(/\b\d+(?:\.\d+){1,3}\b/)
  return versionMatch?.[0] || mirror.baseImage || mirror.name
}

function getPythonVersion(mirror: MirrorItem): string {
  const text = getMirrorText(mirror)
  const pythonVersion = text.match(/python\s*[:/_-]?\s*v?(\d+(?:\.\d+){1,2})/i)?.[1] || '无'
  const ubuntuVersion = text.match(/ubuntu\s*[:/_-]?\s*(\d+(?:\.\d+)?)/i)?.[1]
  return ubuntuVersion ? `${pythonVersion}(ubuntu${ubuntuVersion})` : pythonVersion
}

function getCudaVersion(mirror: MirrorItem): string {
  const text = getMirrorText(mirror)
  return text.match(/cuda\s*[:/_-]?\s*v?(\d+(?:\.\d+){0,2})/i)?.[1] || '无'
}

function isPodRunningStatus(status: string) {
  return status.toLowerCase() === 'running'
}

function isPodTerminalErrorStatus(status: string) {
  const normalizedStatus = status.toLowerCase()
  if (normalizedStatus === 'not_found') {
    return !isEarlyPodNotFound(normalizedStatus)
  }
  return ['error', 'failed', 'stopped'].includes(normalizedStatus)
}

function isEarlyPodNotFound(status: string) {
  if (status.toLowerCase() !== 'not_found') return false
  if (!podPollingStartedAt.value) return true
  const waitTime = isVolcanoOrder.value ? 30 * 60 * 1000 : 60 * 1000
  return Date.now() - podPollingStartedAt.value < waitTime
}

function startCreateProgress() {
  stopCreateProgress()
  createElapsedSeconds.value = 0
  podStatus.value = null
  podStatusError.value = ''
  createProgressDelayTimer = setTimeout(() => {
    createProgressVisible.value = true
    createProgressTimer = setInterval(() => {
      createElapsedSeconds.value += 1
    }, 1000)
  }, 800)
}

function stopCreateProgress() {
  stopCreateProgressDisplay()
  stopPodStatusPolling()
}

function stopCreateProgressDisplay() {
  if (createProgressDelayTimer) {
    clearTimeout(createProgressDelayTimer)
    createProgressDelayTimer = null
  }
  if (createProgressTimer) {
    clearInterval(createProgressTimer)
    createProgressTimer = null
  }
  createProgressVisible.value = false
}

/**
 * 关闭创建进度弹框，但保留实例创建和状态轮询在后台继续执行。
 */
function closeCreateProgressDialog() {
  createProgressVisible.value = false
}

function startPodStatusPolling(podName: string) {
  stopPodStatusPolling()
  creatingPodName.value = podName
  podPollingStartedAt.value = Date.now()
  fetchPodStatusOnce(podName)
  podStatusTimer = setInterval(() => fetchPodStatusOnce(podName), 3000)
}

function stopPodStatusPolling() {
  if (podStatusTimer) {
    clearInterval(podStatusTimer)
    podStatusTimer = null
  }
}

async function fetchPodStatusOnce(podName: string) {
  if (!podName) return
  try {
    const res = await getPodStatus(podName)
    if (res.code === 200 && res.data) {
      podStatus.value = res.data
      syncReturnedPricing(res.data)
      podStatusError.value = ''
      const status = String(res.data.status || '').toLowerCase()
      if (isPodRunningStatus(status) || isPodTerminalErrorStatus(status)) {
        stopPodStatusPolling()
        stopCreateProgressDisplay()
      }
    } else {
      podStatusError.value = res.msg || '状态查询失败'
    }
  } catch (err: any) {
    podStatusError.value = err?.message || '状态查询失败'
  }
}

function syncReturnedPricing(status: any) {
  const raw = status?.pricing || status?.billing_info || status?.billingInfo
  if (!raw || !createdOrder.value) return
  const unitPrice = Number(raw.unit_price ?? raw.unitPrice)
  if (!Number.isFinite(unitPrice)) return
  createdOrder.value = {
    ...createdOrder.value,
    paidAmount: Number(raw.discount_total_cost ?? raw.discountTotalCost ?? createdOrder.value.paidAmount),
    pricing: {
      ...createdOrder.value.pricing,
      ...raw,
      unit_price: unitPrice,
      unitPrice,
      discount_unit_price: Number(raw.discount_unit_price ?? raw.discountUnitPrice ?? unitPrice),
      total_cost: Number(raw.total_cost ?? raw.totalCost ?? createdOrder.value.totalAmount),
      discount_total_cost: Number(raw.discount_total_cost ?? raw.discountTotalCost ?? createdOrder.value.paidAmount),
    }
  }
}

function formatResourceCapacity(value: unknown) {
  const text = String(value ?? '').trim()
  return text || '接口未提供'
}

function capacityToGb(value: unknown) {
  const text = String(value ?? '').trim()
  const match = text.match(/([\d.]+)\s*(TiB|TB|GiB|GB)?/i)
  if (!match) return 0

  const amount = Number(match[1])
  if (!Number.isFinite(amount) || amount <= 0) return 0

  const unit = (match[2] || 'GB').toUpperCase()
  return Math.floor(amount * (unit === 'TB' || unit === 'TIB' ? 1024 : 1))
}

async function loadGpuResource() {
  const resourceId = Number(route.query.resourceId)
  const draft = readOrderDraft(resourceId)
  const isExternalResource = resourceId === 0 && draft?.resource?.source === 'volcano'
  if (!resourceId && !isExternalResource) {
    error.value = '缺少资源ID'
    return
  }

  loading.value = true
  error.value = ''
  orderDraft.value = draft

  try {
    const gpuRes = isExternalResource
      ? { code: 200, data: draft?.resource }
      : await getGpuResourceDetail(resourceId)

    if (gpuRes.code === 200 && gpuRes.data) {
      const draftResource = orderDraft.value?.resource || {}
      gpuResource.value = {
        ...draftResource,
        ...gpuRes.data,
        resourceId: gpuRes.data.resourceId || draftResource.resourceId || gpuRes.data.id || draftResource.id,
        id: gpuRes.data.id || draftResource.id,
        prices: normalizePriceOptions(
          gpuRes.data.prices || orderDraft.value?.prices || draftResource.prices || [],
          orderDraft.value
        )
      }
      applyRouteConfig()
      const availablePrices = (gpuResource.value.prices || []).filter((price: any) => getEffectivePrice(price) > 0)
      if (availablePrices.length > 0 && !availablePrices.some((price: any) => price.billingType === rentConfig.billingType)) {
        rentConfig.billingType = availablePrices[0].billingType
      }
      rentConfig.quantity = Math.min(rentConfig.quantity || 1, gpuResource.value.availableCount || 1)
    } else {
      error.value = '获取GPU资源失败'
      loading.value = false
      return
    }

    const [mirrorsRes, accountRes] = await Promise.all([
      getMirrorList({ gpuModel: gpuResource.value.model }),
      getAccountInfo()
    ])

    if (mirrorsRes.code === 200 && mirrorsRes.data) {
      mirrorList.value = mirrorsRes.data
      initBaseMirrorSelection()
    }

    if (accountRes.code === 200 && accountRes.data) {
      accountInfo.value = accountRes.data
    }

    // 加载成功后立即计算费用
    await calculateFee()
  } catch (err: any) {
    error.value = err?.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function normalizePriceOptions(prices: any[], draft?: OrderDraft | null) {
  const normalized = [...prices]
  if (draft?.billingType && Number(draft.unitAmount || 0) > 0) {
    const billingType = draft.billingType
    const unitPrice = Number(draft.unitAmount || 0)
    const perUnitTotal = Number(draft.totalAmount || 0) / Math.max(Number(draft.quantity || 1) * Number(draft.duration || 1), 1)
    const discountPrice = perUnitTotal > 0 && perUnitTotal < unitPrice ? perUnitTotal : 0
    const draftPrice = {
      billingType,
      billingTypeName: draft.billingTypeName || billingTypeNameMap[billingType] || billingType,
      unitPrice: String(unitPrice),
      discountPrice: discountPrice > 0 ? String(discountPrice) : undefined,
      discountRate: undefined
    }
    const index = normalized.findIndex((price) => price.billingType === billingType)
    if (index >= 0) {
      normalized[index] = {
        ...normalized[index],
        ...draftPrice
      }
    } else {
      normalized.unshift(draftPrice)
    }
  }
  if (!normalized.some((price) => price.billingType === 'on_demand')) {
    const hourly = normalized.find((price) => price.billingType === 'hourly')
    if (hourly) {
      normalized.unshift({
        ...hourly,
        billingType: 'on_demand',
        billingTypeName: '按量计费'
      })
    }
  }
  return normalized
}

function applyRouteConfig() {
  const draft = orderDraft.value
  const billingType = String(draft?.billingType || route.query.billingType || '')
  if (draft?.billingType) {
    rentConfig.billingType = draft.billingType
  } else if (['on_demand', 'hourly', 'daily', 'weekly', 'monthly'].includes(billingType)) {
    rentConfig.billingType = billingType as BillingType
  }
  const quantity = Number(draft?.quantity || route.query.quantity || 1)
  const duration = Number(draft?.duration || route.query.duration || 1)
  if (Number.isFinite(quantity) && quantity > 0) rentConfig.quantity = Math.floor(quantity)
  if (Number.isFinite(duration) && duration > 0) rentConfig.duration = Math.floor(duration)
}

function readOrderDraft(resourceId: number): OrderDraft | null {
  try {
    const raw = sessionStorage.getItem('computeRentOrderDraft')
    if (!raw) return null
    const draft = JSON.parse(raw) as OrderDraft
    if (Number(draft.resourceId) !== resourceId) return null
    return draft
  } catch (error) {
    console.warn('读取下单配置失败:', error)
    return null
  }
}

async function onMirrorChange(mirrorId: string) {
  rentConfig.versionId = ''
  versionList.value = []

  if (!mirrorId) return

  try {
    const res = await getMirrorVersions(mirrorId)
    if (res.code === 200 && res.data) {
      versionList.value = res.data
      if (mirrorSource.value === 'base' && versionList.value.length > 0) {
        rentConfig.versionId = versionList.value[0].id
      }
    }
  } catch (err) {
    console.error('获取镜像版本失败:', err)
  }
}

function resetBaseMirrorValue() {
  rentConfig.mirrorId = ''
  rentConfig.versionId = ''
  versionList.value = []
}

function selectBaseMirrorType(type: string) {
  selectedBaseMirrorType.value = type
  selectedBaseFrameworkVersion.value = ''
  selectedBasePythonVersion.value = ''
  resetBaseMirrorValue()
}

function selectBaseFrameworkVersion(version: string) {
  selectedBaseFrameworkVersion.value = version
  selectedBasePythonVersion.value = ''
  resetBaseMirrorValue()
}

function selectBasePythonVersion(version: string) {
  selectedBasePythonVersion.value = version
  resetBaseMirrorValue()
}

async function selectBaseMirror(mirror: MirrorItem) {
  rentConfig.mirrorId = mirror.id
  baseMirrorPanelVisible.value = false
  await onMirrorChange(mirror.id)
}

function initBaseMirrorSelection() {
  if (mirrorSource.value !== 'base' || selectedBaseMirrorType.value || baseMirrorTree.value.length === 0) {
    return
  }
  selectedBaseMirrorType.value = baseMirrorTree.value[0].name
}

async function calculateFee() {
  const price = currentPriceItem.value
  if (!gpuResource.value || rentConfig.quantity <= 0 || !price) {
    feeResult.value = null
    return
  }

  const seq = ++feeCalculateSeq
  try {
    if (gpuResource.value?.source === 'volcano') {
      const unitPrice = getEffectivePrice(price)
      const subtotal = unitPrice * rentConfig.quantity * rentConfig.duration
      feeResult.value = {
        unitPrice,
        quantity: rentConfig.quantity,
        duration: rentConfig.duration,
        subtotal,
        discount: 0,
        total: subtotal,
        diskFee: 0
      }
      return
    }
    const res = await calculateRentFee({
      resourceId: gpuResource.value.source === 'volcano'
        ? 0
        : (gpuResource.value.resourceId || gpuResource.value.id),
      billingType: rentConfig.billingType,
      quantity: rentConfig.quantity,
      duration: rentConfig.duration,
      expandSize: needExpand.value ? expandSize.value : undefined
    })
    if (seq !== feeCalculateSeq) return

    if (res.code === 200 && res.data) {
      feeResult.value = {
        ...res.data,
        unitPrice: Number(res.data.unitPrice || 0),
        quantity: Number(res.data.quantity || rentConfig.quantity),
        duration: Number(res.data.duration || rentConfig.duration),
        subtotal: Number(res.data.subtotal || 0),
        discount: Number(res.data.discount || 0),
        total: Number(res.data.total || 0),
        diskFee: Number(res.data.diskFee || 0)
      }
      if (error.value === '费用计算失败') error.value = ''
    } else {
      feeResult.value = null
      error.value = res.msg || '费用计算失败'
    }
  } catch (err: any) {
    if (seq !== feeCalculateSeq) return
    feeResult.value = null
    error.value = err?.message || '费用计算失败'
  }
}

function buildPodCreateRequest(): PodCreateRequest | null {
  const resource = gpuResource.value
  const price = currentPriceItem.value
  const version = selectedMirrorVersion.value
  const fee = feeResult.value
  if (!resource || !price || !version || !fee) return null

  const unitPrice = Number(price.unitPrice || 0)
  const discountUnitPrice = getEffectivePrice(price)
  const hourFactor = billingHourFactorMap[rentConfig.billingType] || 1
  const pricePerHour = unitPrice / hourFactor
  const discountPrice = discountUnitPrice / hourFactor
  const dataDisk = needExpand.value && expandSize.value > 0
    ? `${resource.dataDisk || ''} + ${expandSize.value} GB`
    : resource.dataDisk || ''

  return {
    gpuSpec: {
      model: resource.model || '',
      count: rentConfig.quantity,
      gpuMemory: resource.vram || ''
    },
    image: version.image,
    billing: {
      mode: billingModeMap[rentConfig.billingType] || rentConfig.billingType,
      duration: rentConfig.duration
    },
    resource: {
      cpu: String(instanceCpu.value || resource.cpuCores || ''),
      cpuModel: resource.cpuModel || '',
      memory: instanceMemory.value || resource.memory || '',
      systemDisk: resource.systemDisk || '',
      dataDisk,
      dataDiskExpandable: resource.expandable || ''
    },
    pricing: {
      unitPrice,
      discountUnitPrice,
      totalCost: fee.subtotal,
      discountTotalCost: fee.total,
      currency: 'CNY',
      unit: billingUnitTextMap[rentConfig.billingType] || rentConfig.billingType,
      pricePerHour,
      discountPrice
    },
    region: resource.regionCode || '',
    zone: resource.zoneCode || resource.zone || resource.regionCode || resource.region || '',
    machineId: resource.machineId || '',
    gpuDriver: resource.gpuDriver || '',
    cudaVersion: resource.cudaVersion || ''
  }
}

async function showCreateError(message: string) {
  createErrorConfirming.value = true
  try {
    await ElMessageBox.alert(formatCreateErrorMessage(message), '创建失败', {
      type: 'error',
      confirmButtonText: '确定',
      closeOnClickModal: false,
      closeOnPressEscape: false,
      showClose: false,
      customClass: 'create-error-message-box'
    })
  } finally {
    createErrorConfirming.value = false
  }
}

function formatCreateErrorMessage(message?: string) {
  const text = String(message || '').trim()
  if (!text) return '订单创建失败，请稍后重试或联系管理员处理。'
  if (/余额不足|库存不足|价格不存在|参数|不能为空/.test(text)) {
    return text
  }
  if (isTechnicalCreateError(text)) {
    return '实例创建服务暂时不可用，订单未创建成功。请稍后重试，如多次失败请联系管理员处理。'
  }
  // Scheduler business errors contain the actionable reason the user needs
  // (for example which GPU models are currently available).
  return text
}

function isTechnicalCreateError(message: string) {
  return /(https?:\/\/|authKey=|I\/O error|POST request|GET request|RestTemplate|Exception|Connection|timeout|timed out|ECONNABORTED|connect|refused|Socket|Read timed out|500 Internal Server Error|400 Bad Request)/i.test(message)
}

async function handleSubmit() {
  if (!canSubmit.value) return

  if (!gpuResource.value) {
    ElMessage.error('GPU资源信息缺失')
    return
  }

  submitting.value = true

  try {
    const podCreateRequest = buildPodCreateRequest()
    if (!podCreateRequest) {
      stopCreateProgress()
      await showCreateError('创建参数不完整，请重新选择配置')
      return
    }
    startCreateProgress()

    const params = {
      resourceId: gpuResource.value.source === 'volcano'
        ? 0
        : (gpuResource.value.resourceId || gpuResource.value.id),
      mirrorId: rentConfig.mirrorId,
      mirrorVersionId: rentConfig.versionId,
      billingType: rentConfig.billingType,
      quantity: rentConfig.quantity,
      duration: rentConfig.duration,
      agreeProtocol: true,
      podCreateRequest
    }

    const res = await createRentOrder(params)
    if (res.code === 200 && res.data) {
      createdOrder.value = res.data
      const returnedPodName = res.data.instanceInfo?.podName || res.data.instanceInfo?.instanceName
      if (returnedPodName) {
        startPodStatusPolling(returnedPodName)
      }
      sessionStorage.removeItem('computeRentOrderDraft')
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } else {
      stopCreateProgress()
      await showCreateError(res.msg || '创建订单失败')
    }
  } catch (err: any) {
    stopCreateProgress()
    if (err?.response) {
      await showCreateError(err.response.data?.msg || err.response.data?.message || '创建订单失败')
    } else if (err?.code === 'ECONNABORTED') {
      await showCreateError('创建请求超时，请稍后到 GPU 实例列表查看是否创建成功')
    } else {
      await showCreateError(err?.message || '创建订单失败')
    }
  } finally {
    if (createdOrder.value) {
      if (isPodRunningStatus(normalizedPodStatus.value) || isPodTerminalErrorStatus(normalizedPodStatus.value)) {
        stopCreateProgress()
      }
    } else {
      stopCreateProgress()
    }
    submitting.value = false
  }
}

async function promptAgreement() {
  agreementPromptActive.value = true
  await nextTick()
  agreementSectionRef.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  window.setTimeout(() => {
    agreementPromptActive.value = false
  }, 1800)
}

// ==================== 监听器 ====================

watch(mirrorSource, () => {
  selectedBaseMirrorType.value = ''
  selectedBaseFrameworkVersion.value = ''
  selectedBasePythonVersion.value = ''
  baseMirrorPanelVisible.value = false
  rentConfig.mirrorId = ''
  rentConfig.versionId = ''
  versionList.value = []
  initBaseMirrorSelection()
})

watch(
  () => [rentConfig.billingType, rentConfig.quantity, rentConfig.duration],
  () => {
    calculateFee()
  },
  { immediate: false }
)

watch([needExpand, expandSize], () => {
  calculateFee()
})

// ==================== 生命周期 ====================

onMounted(() => {
  loadGpuResource()
})

onBeforeUnmount(() => {
  stopCreateProgress()
})
</script>

<style scoped lang="scss">
.create-instance-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 120px;
  padding-top: 1px;
}

.breadcrumb-bar {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #e4e7ed;
  margin-top: 120px;

  :deep(.el-breadcrumb) {
    max-width: 1400px;
    margin: 0 auto;
    font-size: 16px;
  }
}

.error-alert {
  max-width: 1400px;
  margin: 16px auto;
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 24px;
}

.section {
  background: #fff;
  border-radius: 8px;
  padding: 20px 24px;
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  gap: 16px;

  .section-label {
    width: 100px;
    text-align: right;
    color: #303133;
    font-size: 16px;
    font-weight: 500;
    flex-shrink: 0;
    padding-top: 6px;
  }

  .section-content {
    flex: 1;
    min-width: 0;
  }
}

:deep(.el-radio-button__inner) {
  font-size: 16px;
}

:deep(.el-select) {
  font-size: 16px;
}

:deep(.el-table) {
  font-size: 15px;
}

.billing-section {
  .billing-rules-link {
    margin-left: 16px;
    font-size: 16px;
  }

  .billing-tip {
    margin-top: 12px;
    color: #909399;
    font-size: 15px;
  }
}

.host-section {
  .host-id {
    color: #409eff;
    font-weight: 500;
  }

  .host-sub {
    margin-top: 4px;
    color: #909399;
    font-size: 14px;
  }

  .gpu-model {
    font-weight: 500;
    color: #303133;
  }

  .gpu-vram {
    color: #909399;
    font-size: 16px;
  }

  .gpu-count {
    color: #303133;
    font-weight: 500;
  }

  .text-secondary {
    color: #909399;
    font-size: 16px;
  }

  .price-current {
    color: #f56c6c;
    font-weight: 600;
    font-size: 16px;
  }

  .host-radio {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    border: 1px solid #dcdfe6;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #409eff;
    }

    &.disabled {
      cursor: not-allowed;
    }

    &.checked {
      border-color: #409eff;

      .radio-inner {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #409eff;
      }
    }
  }
}

.host-popover-content {
  .popover-row {
    display: flex;
    justify-content: space-between;
    padding: 6px 0;
    font-size: 15px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .popover-label {
      color: #909399;
    }

    .popover-value {
      color: #303133;
      font-weight: 500;
      max-width: 140px;
      text-align: right;
      word-break: break-all;
    }
  }
}

.gpu-quantity-section {
  :deep(.el-radio-button__inner) {
    min-width: 48px;
    font-size: 16px;
  }
}

.duration-section {
  .section-content {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .duration-unit {
    color: #606266;
    font-size: 16px;
  }
}

.disk-section {
  .free-disk {
    margin-right: 16px;
    color: #303133;
    font-size: 16px;
  }

  .expand-input {
    margin-top: 12px;

    .hint {
      margin-left: 12px;
      color: #909399;
      font-size: 15px;
    }
  }
}

.spec-section {
  .spec-content {
    display: flex;
    gap: 48px;
    flex-wrap: wrap;
  }

  .spec-item {
    display: flex;
    flex-direction: column;
    gap: 6px;

    .spec-label {
      color: #909399;
      font-size: 15px;
    }

    .spec-value {
      color: #303133;
      font-size: 17px;
      font-weight: 500;
    }
  }
}

.mirror-section {
  .mirror-tip {
    margin-top: 12px;
    padding: 10px 14px;
    background: #f5f7fa;
    border-radius: 4px;
    color: #606266;
    font-size: 15px;
  }

  .mirror-empty {
    margin-top: 12px;
    color: #909399;
    font-size: 16px;
  }

  .base-mirror-selector {
    width: 520px;
    max-width: 100%;
    height: 44px;
    margin-top: 12px;
    padding: 0 14px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #909399;
    background: #fff;
    cursor: pointer;
    transition: border-color 0.2s, box-shadow 0.2s;

    span {
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 15px;
    }

    &.selected {
      color: #303133;
    }

    &:hover {
      border-color: #3b6cff;
    }
  }

  .mirror-option {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    width: 100%;

    .mirror-option-meta {
      color: #909399;
      font-size: 14px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

:global(.base-mirror-popover) {
  padding: 0 !important;
  border-radius: 4px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.base-mirror-cascader {
  display: grid;
  grid-template-columns: repeat(4, 210px);
  min-height: 320px;
  max-height: 420px;
  background: #fff;
  overflow: hidden;
}

.cascader-column {
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;

  &:last-child {
    border-right: none;
  }
}

.cascader-title {
  height: 44px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 15px;
  border-bottom: 1px solid #ebeef5;
}

.cascader-option {
  width: 100%;
  min-height: 42px;
  padding: 0 20px;
  border: none;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #606266;
  font-size: 15px;
  line-height: 1.3;
  text-align: left;
  cursor: pointer;

  span {
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &:hover,
  &.active {
    color: #3b6cff;
    background: #f5f8ff;
  }

  &.final {
    justify-content: flex-start;
  }
}

.version-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  .version-meta {
    color: #909399;
    font-size: 16px;
  }
}

.bottom-spacer {
  height: 80px;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 14px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.06);

  .bottom-left {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .fee-line {
    display: flex;
    align-items: center;
    gap: 24px;
  }

  .fee-item {
    font-size: 16px;
    color: #606266;

    .fee-num {
      font-size: 22px;
      font-weight: 600;
      color: #f56c6c;
      margin-left: 4px;
    }

    .fee-unit {
      font-size: 15px;
      color: #606266;
    }

    .price {
      color: #f56c6c;
    }
  }

  .balance-line {
    font-size: 15px;
    color: #606266;

    .recharge-link {
      margin-left: 8px;
      font-size: 15px;
    }
  }

  .bottom-right {
    display: flex;
    gap: 12px;

    .el-button {
      min-width: 100px;
    }
  }
}

.fee-detail-content {
  .fee-formula {
    color: #909399;
    font-size: 15px;
    margin-bottom: 16px;
    padding: 8px 12px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .fee-detail-table {
    .fee-detail-header,
    .fee-detail-row {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 1fr;
      gap: 12px;
      padding: 10px 0;
      font-size: 16px;
      border-bottom: 1px solid #ebeef5;
    }

    .fee-detail-header {
      color: #909399;
      font-weight: 500;
    }

    .fee-detail-row {
      color: #606266;

      .discount {
        color: #67c23a;
      }

      .final {
        color: #303133;
        font-weight: 500;
      }
    }
  }

  .fee-detail-summary {
    .summary-row {
      display: flex;
      justify-content: space-between;
      padding: 8px 0;
      font-size: 16px;
      color: #606266;

      &.total {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-top: 8px;
      }
    }

    .final-price {
      color: #f56c6c;
      font-size: 20px;
    }
  }
}

.error-state {
  padding: 80px 20px;
}

.create-progress-dialog {
  padding: 8px 0 4px;

  .create-progress-title {
    margin-top: 20px;
    color: #303133;
    font-size: 18px;
    font-weight: 600;
    text-align: center;
  }

  .create-progress-desc {
    margin-top: 10px;
    color: #606266;
    font-size: 15px;
    line-height: 1.7;
    text-align: center;
  }

  .create-progress-tip {
    margin-top: 14px;
    padding: 10px 12px;
    border-radius: 4px;
    background: #f5f8ff;
    color: #3b6cff;
    font-size: 14px;
    line-height: 1.6;
    text-align: center;
  }
}

// 响应式
@media (max-width: 768px) {
  .section {
    flex-direction: column;

    .section-label {
      text-align: left;
      width: auto;
      padding-top: 0;
    }
  }

  .spec-section .spec-content {
    gap: 24px;
  }

  .bottom-bar {
    flex-direction: column;
    gap: 12px;
    padding: 12px 16px;

    .fee-line {
      flex-wrap: wrap;
      gap: 12px;
    }
  }
}
</style>

<style scoped lang="scss">
.purchase-confirm-page {
  --aliyun-primary: #ff6a00;
  --aliyun-primary-hover: #ff7a1a;
  --aliyun-page-bg: #fff3dc;
  --aliyun-surface: #fffdf8;
  --aliyun-tint: #fff7e8;
  --aliyun-tint-strong: #fff0d4;
  --aliyun-border: #f1d3a1;
  --aliyun-soft-border: #f5dfba;

  min-height: 100vh;
  padding: 102px 0 48px;
  overflow-x: clip;
  background: var(--aliyun-page-bg);
  color: #1f2937;

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
  :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-primary);
  }

  :deep(.el-checkbox__input.is-focus .el-checkbox__inner),
  :deep(.el-checkbox:hover .el-checkbox__inner) {
    border-color: var(--aliyun-primary);
  }

  :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
    color: #4b5563;
  }
}

.product-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  width: min(1540px, calc(100vw - 48px));
  min-height: 58px;
  margin: 0 auto 12px;
  padding: 0 2px;
}

.heading-copy {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 0 0 auto;
}

.back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  padding: 0;
  border: 1px solid var(--aliyun-border);
  border-radius: 0;
  background: #fff;
  color: #4b5563;
  cursor: pointer;

  &:hover {
    border-color: var(--aliyun-primary);
    color: var(--aliyun-primary);
  }
}

.product-name {
  color: #111827;
  font-size: 20px;
  font-weight: 700;
}

.product-mode {
  margin-top: 3px;
  color: #7b8494;
  font-size: 13px;
}

.purchase-steps {
  display: grid;
  grid-template-columns: auto minmax(36px, 88px) auto minmax(36px, 88px) auto;
  align-items: center;
  min-width: 510px;
}

.step {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  color: #8a94a6;
  font-size: 14px;

  span {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 26px;
    height: 26px;
    border: 1px solid #d2d7df;
    border-radius: 50%;
    background: #fff;
    font-size: 13px;
    font-weight: 600;
  }

  &.complete,
  &.active {
    color: #1f2937;
  }

  &.complete span {
    border-color: #ffb47f;
    background: var(--aliyun-tint-strong);
    color: var(--aliyun-primary);
  }

  &.active span {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-primary);
    color: #fff;
  }
}

.step-line {
  height: 1px;
  margin: 0 10px;
  background: #d7dce4;

  &.complete {
    background: #ffb47f;
  }
}

.error-alert {
  width: min(1540px, calc(100vw - 48px));
  margin: 0 auto 12px;
  border-radius: 0;
}

.purchase-shell {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  align-items: start;
  gap: 16px;
  width: min(1540px, calc(100vw - 48px));
  min-height: 480px;
  margin: 0 auto;
}

.config-panel,
.summary-card {
  border: 1px solid var(--aliyun-soft-border);
  background: var(--aliyun-surface);
}

.creation-result {
  grid-column: 1 / -1;
  padding: 48px;
  border: 1px solid var(--aliyun-soft-border);
  background: var(--aliyun-surface);
}

.result-status {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  padding-bottom: 30px;
  border-bottom: 1px solid #f1e5d2;

  h1 {
    margin: 1px 0 8px;
    color: #111827;
    font-size: 24px;
    font-weight: 700;
    letter-spacing: 0;
  }

  p {
    margin: 0;
    color: #6b7280;
    font-size: 14px;
    line-height: 1.7;
  }
}

.result-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  border-radius: 50%;
  background: #eaf7ef;
  color: #1d8a55;
  font-size: 24px;
}

.result-details {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin: 30px 0 24px;
  border-top: 1px solid #ebeef5;
  border-left: 1px solid #ebeef5;
}

.result-detail-item {
  min-width: 0;
  min-height: 84px;
  padding: 16px 18px;
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  background: #fff;

  span,
  strong {
    display: block;
  }

  span {
    margin-bottom: 8px;
    color: #8a94a6;
    font-size: 12px;
  }

  strong {
    overflow-wrap: anywhere;
    color: #303745;
    font-size: 14px;
    line-height: 1.5;
  }

  .status-processing {
    color: #1677ff;
  }

  .result-price {
    color: var(--aliyun-primary);
  }
}

.result-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 28px;

  :deep(.el-button) {
    min-width: 112px;
    height: 40px;
    margin: 0;
    border-radius: 0;
  }
}

.secondary-result-action {
  border-color: var(--aliyun-border);

  &:hover {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
  }
}

.primary-result-action {
  border-color: var(--aliyun-primary);
  background: var(--aliyun-primary);
  color: #fff;

  &:hover {
    border-color: var(--aliyun-primary-hover);
    background: var(--aliyun-primary-hover);
    color: #fff;
  }
}

.config-panel {
  min-width: 0;
}

.config-section {
  padding: 26px 28px;

  & + .config-section {
    border-top: 8px solid var(--aliyun-page-bg);
  }
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
}

.section-title {
  margin-bottom: 24px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.section-heading .section-title {
  margin-bottom: 5px;
}

.section-subtitle {
  color: #8a94a6;
  font-size: 13px;
  line-height: 1.5;
}

.edit-config-button {
  border-color: var(--aliyun-border);
  border-radius: 0;
  color: var(--aliyun-primary);

  &:hover {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
  }
}

.resource-overview {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 16px 18px;
  border: 1px solid var(--aliyun-border);
  background: var(--aliyun-tint);
}

.resource-identity {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;

  > div:last-child {
    min-width: 0;
  }

  strong,
  span {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  strong {
    color: #111827;
    font-size: 15px;
  }

  span {
    margin-top: 4px;
    color: #8a94a6;
    font-size: 12px;
  }
}

.resource-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  border: 1px solid var(--aliyun-primary);
  background: #fff;
  color: var(--aliyun-primary);
  font-size: 12px;
  font-weight: 700;
}

.overview-price {
  flex: 0 0 auto;
  text-align: right;

  span,
  strong {
    display: block;
  }

  span {
    color: #7b8494;
    font-size: 12px;
  }

  strong {
    margin-top: 3px;
    color: var(--aliyun-primary);
    font-size: 18px;
  }
}

.configuration-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: 18px;
  border-top: 1px solid #f1e5d2;
  border-left: 1px solid #f1e5d2;
}

.config-item {
  min-width: 0;
  min-height: 76px;
  padding: 14px 16px;
  border-right: 1px solid #f1e5d2;
  border-bottom: 1px solid #f1e5d2;
  background: #fff;

  span,
  strong {
    display: block;
  }

  span {
    margin-bottom: 7px;
    color: #8a94a6;
    font-size: 12px;
  }

  strong {
    overflow-wrap: anywhere;
    color: #303745;
    font-size: 14px;
    font-weight: 600;
    line-height: 1.45;
  }
}

.config-row {
  display: grid;
  grid-template-columns: 132px minmax(0, 1fr);
  gap: 18px;
  margin-bottom: 26px;

  &:last-child {
    margin-bottom: 0;
  }
}

.row-label {
  display: flex;
  flex-direction: column;
  gap: 7px;
  padding-top: 6px;

  strong {
    color: #111827;
    font-size: 15px;
  }

  span {
    color: #8a94a6;
    font-size: 12px;
    line-height: 1.45;
  }
}

.required-label strong::before {
  margin-right: 4px;
  color: #e53935;
  content: "*";
}

.row-control {
  min-width: 0;
}

.source-tabs {
  :deep(.el-radio-button__inner) {
    min-width: 108px;
    height: 38px;
    padding: 0 18px;
    border-color: var(--aliyun-border);
    border-radius: 0;
    box-shadow: none;
    line-height: 36px;
  }

  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
    box-shadow: -1px 0 0 0 var(--aliyun-primary);
  }
}

.hint-line {
  margin-top: 10px;
  padding: 9px 12px;
  border-left: 3px solid #ffb47f;
  background: var(--aliyun-tint);
  color: #6b7280;
  font-size: 13px;
  line-height: 1.55;
}

.base-mirror-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: min(100%, 720px);
  height: 42px;
  padding: 0 13px;
  border: 1px solid #d7dce4;
  background: #fff;
  color: #9aa1ac;
  cursor: pointer;
  transition: border-color 0.16s ease;

  &:hover {
    border-color: var(--aliyun-primary);
  }

  &.selected {
    border-color: #ffb47f;
    color: #1f2937;
  }

  span {
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .el-icon {
    flex: 0 0 auto;
  }
}

.mirror-select {
  width: min(100%, 520px);
}

.version-select {
  margin-top: 10px;
}

.empty-inline {
  width: min(100%, 520px);
  padding: 13px 14px;
  border: 1px dashed var(--aliyun-border);
  background: #fff;
  color: #8a94a6;
  font-size: 13px;
}

.storage-control {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}

.storage-line {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 42px;
  min-width: 0;

  > span {
    color: #8a94a6;
    font-size: 13px;
  }
}

.storage-type {
  min-width: 108px;
  padding: 9px 14px;
  border: 1px solid var(--aliyun-primary);
  background: var(--aliyun-tint);
  color: var(--aliyun-primary);
  text-align: center;
}

.expand-input {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #7b8494;
  font-size: 13px;
}

.confirmation-section {
  position: relative;
  padding-bottom: 22px;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;

  .section-title {
    margin-bottom: 16px;
  }

  :deep(.el-checkbox) {
    height: auto;
    align-items: flex-start;
    white-space: normal;
  }

  :deep(.el-checkbox__label) {
    color: #4b5563;
    line-height: 1.65;
    white-space: normal;
  }

  &.needs-agreement {
    border: 2px solid #ff6a00;
    background: #fff8ef;
    box-shadow: 0 0 0 4px rgba(255, 106, 0, 0.1);
  }

  &.agreement-prompt-active {
    animation: agreement-pulse 0.45s ease-in-out 2;
  }
}

.agreement-confirm-box {
  padding: 16px 18px;
  border: 1px solid #ffc58f;
  background: #fffdf9;
}

.agreement-copy {
  display: inline-flex;
  flex-direction: column;
  gap: 5px;
  color: #374151;
  font-size: 15px;
  line-height: 1.5;

  strong {
    color: #1f2937;
    font-size: 16px;
  }
}

.agreement-warning,
.agreement-confirmed {
  display: flex;
  align-items: center;
  gap: 7px;
  margin: 14px 0 0 28px;
  font-size: 14px;
}

.agreement-warning {
  color: #d94801;
}

.agreement-confirmed {
  color: #16834b;
}

.agreement-required-action {
  border-color: #d94801;
  background: #d94801;
  box-shadow: 0 4px 12px rgba(217, 72, 1, 0.24);
  animation: agreement-button-pulse 1.5s ease-in-out infinite;
}

@keyframes agreement-pulse {
  0%, 100% { box-shadow: 0 0 0 4px rgba(255, 106, 0, 0.1); }
  50% { box-shadow: 0 0 0 8px rgba(255, 106, 0, 0.22); }
}

@keyframes agreement-button-pulse {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-1px); }
}

.summary-panel {
  position: sticky;
  top: 102px;
  min-width: 0;
}

.summary-card {
  padding-bottom: 20px;
}

.summary-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 21px 22px 15px;
  border-bottom: 1px solid #f1e5d2;

  h3 {
    margin: 0;
    color: #111827;
    font-size: 18px;
  }
}

.confirm-badge {
  padding: 3px 8px;
  border: 1px solid #ffb47f;
  background: var(--aliyun-tint);
  color: var(--aliyun-primary);
  font-size: 12px;
}

.summary-block {
  padding: 18px 22px;
  border-bottom: 1px solid #f1e5d2;
}

.summary-block-title {
  margin-bottom: 13px;
  color: #111827;
  font-size: 14px;
  font-weight: 700;
}

.summary-line,
.account-line {
  display: grid;
  grid-template-columns: 84px minmax(0, 1fr);
  align-items: start;
  gap: 12px;
  padding: 5px 0;
  font-size: 13px;

  span {
    color: #7b8494;
  }

  strong {
    min-width: 0;
    overflow-wrap: anywhere;
    color: #303745;
    font-weight: 500;
    line-height: 1.45;
    text-align: right;
  }

  &.discount strong {
    color: #1d8a55;
  }
}

.image-line strong {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.price-block {
  position: relative;
}

.fee-detail-link {
  display: block;
  margin: 8px 0 0 auto;
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--aliyun-primary);
  font-size: 13px;
  cursor: pointer;
}

.account-line {
  margin: 16px 22px 0;
  padding: 10px 12px;
  background: #f8f9fb;
}

.summary-card :deep(.el-alert) {
  margin: 10px 22px 0;
  width: auto;
  border-radius: 0;
}

.balance-warning :deep(.el-alert__title) {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  line-height: 1.5;
}

.recharge-inline-link {
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--aliyun-primary);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;

  &:hover {
    color: var(--aliyun-primary-hover);
    text-decoration: underline;
  }
}

.summary-total {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 22px 0;
  color: #111827;
  font-size: 14px;

  strong {
    color: var(--aliyun-primary);
    font-size: 28px;
    line-height: 1;
  }

  small {
    margin-right: 3px;
    font-size: 14px;
  }
}

.daily-price {
  padding: 7px 22px 0;
  color: #8a94a6;
  font-size: 12px;
  text-align: right;
}

.summary-actions {
  display: grid;
  grid-template-columns: 108px minmax(0, 1fr);
  gap: 10px;
  padding: 20px 22px 0;

  :deep(.el-tooltip__trigger),
  :deep(.el-button) {
    width: 100%;
    margin: 0;
  }
}

.back-config-action,
.submit-action {
  height: 42px;
  border-radius: 0;
}

.back-config-action {
  border-color: var(--aliyun-border);

  &:hover {
    border-color: var(--aliyun-primary);
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
  }
}

.submit-action {
  border-color: var(--aliyun-primary);
  background: var(--aliyun-primary);
  color: #fff;
  font-weight: 600;

  &:not(.is-disabled):hover {
    border-color: var(--aliyun-primary-hover);
    background: var(--aliyun-primary-hover);
  }
}

.summary-note {
  margin: 12px 22px 0;
  color: #8a94a6;
  font-size: 12px;
  line-height: 1.55;
}

.error-state {
  grid-column: 1 / -1;
  padding: 64px 20px;
  border: 1px solid var(--aliyun-soft-border);
  background: var(--aliyun-surface);
}

.fee-detail-content {
  .fee-formula {
    margin-bottom: 16px;
    padding: 9px 12px;
    background: var(--aliyun-tint);
    color: #7b8494;
    font-size: 13px;
  }

  .fee-detail-header,
  .fee-detail-row {
    display: grid;
    grid-template-columns: 1.5fr 1fr 1fr 1fr;
    gap: 10px;
    padding: 11px 0;
    border-bottom: 1px solid #ebeef5;
    font-size: 13px;
  }

  .fee-detail-header {
    color: #8a94a6;
  }

  .fee-detail-row {
    color: #4b5563;

    .discount {
      color: #1d8a55;
    }

    .final {
      color: #111827;
      font-weight: 600;
    }
  }

  .summary-row {
    display: flex;
    justify-content: space-between;
    padding: 7px 0;
    color: #4b5563;
    font-size: 14px;
  }

  .summary-row.total {
    margin-top: 6px;
    color: #111827;
    font-weight: 600;
  }

  .final-price {
    color: var(--aliyun-primary);
    font-size: 20px;
  }
}

.create-progress-dialog {
  padding: 8px 0 4px;

  .create-progress-title {
    margin-top: 20px;
    color: #303745;
    font-size: 17px;
    font-weight: 600;
    text-align: center;
  }

  .create-progress-desc {
    margin-top: 10px;
    color: #6b7280;
    font-size: 14px;
    line-height: 1.7;
    text-align: center;
  }

  .create-progress-tip {
    margin-top: 14px;
    padding: 10px 12px;
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
    font-size: 13px;
    line-height: 1.6;
    text-align: center;
  }

  .create-progress-status {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
    margin-top: 14px;

    div {
      min-width: 0;
      padding: 8px 10px;
      background: #f8fafc;
      border: 1px solid #e5e7eb;

      span {
        display: block;
        margin-bottom: 4px;
        color: #667085;
        font-size: 12px;
      }

      strong {
        display: block;
        overflow: hidden;
        color: #111827;
        font-size: 13px;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .create-progress-message,
  .create-progress-events {
    margin-top: 10px;
    padding: 10px 12px;
    background: #111827;
    color: #e5e7eb;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    font-size: 12px;
    line-height: 1.6;
    overflow-wrap: anywhere;
  }

  .create-progress-events {
    max-height: 100px;
    overflow: auto;
    background: #1f2937;
  }
}

.base-mirror-cascader {
  display: grid;
  grid-template-columns: repeat(4, minmax(180px, 210px));
  min-height: 310px;
  max-height: 420px;
  overflow: auto;
  background: #fff;
}

.cascader-column {
  min-width: 0;
  overflow-y: auto;
  border-right: 1px solid #e4e7ed;

  &:last-child {
    border-right: 0;
  }
}

.cascader-title {
  display: flex;
  align-items: center;
  height: 42px;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
  color: #8a94a6;
  font-size: 13px;
}

.cascader-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
  min-height: 40px;
  padding: 8px 16px;
  border: 0;
  background: #fff;
  color: #4b5563;
  line-height: 1.35;
  text-align: left;
  cursor: pointer;

  span {
    min-width: 0;
    overflow-wrap: anywhere;
  }

  &:hover,
  &.active {
    background: var(--aliyun-tint);
    color: var(--aliyun-primary);
  }

  &.final {
    justify-content: flex-start;
  }
}

:global(.base-mirror-popover) {
  max-width: calc(100vw - 32px) !important;
  padding: 0 !important;
  overflow: auto !important;
  border-radius: 0 !important;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-button) {
  border-radius: 0;
}

@media (max-width: 1180px) {
  .purchase-shell {
    grid-template-columns: minmax(0, 1fr) 330px;
  }

  .configuration-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 920px) {
  .product-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
    padding-bottom: 8px;
  }

  .purchase-steps {
    width: 100%;
    min-width: 0;
  }

  .purchase-shell {
    grid-template-columns: minmax(0, 1fr);
  }

  .summary-panel {
    position: static;
  }
}

@media (max-width: 600px) {
  .purchase-confirm-page {
    padding-top: 86px;
  }

  .product-heading,
  .purchase-shell,
  .error-alert {
    width: calc(100vw - 24px);
  }

  .product-heading {
    min-height: 0;
  }

  .purchase-steps {
    grid-template-columns: auto minmax(12px, 1fr) auto minmax(12px, 1fr) auto;
  }

  .step {
    gap: 5px;
    font-size: 11px;

    span {
      width: 22px;
      height: 22px;
      font-size: 11px;
    }
  }

  .step-line {
    margin: 0 4px;
  }

  .config-section {
    padding: 22px 16px;
  }

  .creation-result {
    padding: 28px 16px;
  }

  .result-details {
    grid-template-columns: minmax(0, 1fr);
  }

  .result-actions {
    display: grid;
    grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  }

  .section-heading {
    align-items: center;
  }

  .section-subtitle {
    max-width: 190px;
  }

  .resource-overview {
    align-items: flex-start;
    flex-direction: column;
    gap: 14px;
  }

  .resource-identity {
    width: 100%;
  }

  .overview-price {
    padding-left: 54px;
    text-align: left;
  }

  .configuration-grid {
    grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  }

  .config-item {
    min-height: 82px;
    padding: 12px;
  }

  .config-row {
    grid-template-columns: minmax(0, 1fr);
    gap: 10px;
  }

  .row-label {
    padding-top: 0;
  }

  .source-tabs {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    width: 100%;

    :deep(.el-radio-button),
    :deep(.el-radio-button__inner) {
      width: 100%;
      min-width: 0;
    }

    :deep(.el-radio-button__inner) {
      padding: 0 6px;
    }
  }

  .base-mirror-selector,
  .mirror-select,
  .empty-inline {
    width: 100%;
  }

  .storage-line {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .storage-line > span {
    flex: 1 1 100%;
  }

  .expand-input {
    align-items: flex-start;
    flex-direction: column;
  }

  .summary-header,
  .summary-block {
    padding-right: 16px;
    padding-left: 16px;
  }

  .account-line {
    margin-right: 16px;
    margin-left: 16px;
  }

  .summary-total,
  .daily-price,
  .summary-actions {
    padding-right: 16px;
    padding-left: 16px;
  }

  .summary-note {
    margin-right: 16px;
    margin-left: 16px;
  }

  .summary-actions {
    grid-template-columns: 94px minmax(0, 1fr);
  }

  .fee-detail-content {
    overflow-x: auto;

    .fee-detail-table {
      min-width: 460px;
    }
  }
}
</style>

<style lang="scss">
.host-detail-popover {
  .host-detail-content {
    padding: 8px 4px;

    .detail-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      font-size: 15px;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .detail-label {
        color: #909399;
        flex-shrink: 0;
      }

      .detail-value {
        color: #303133;
        font-weight: 500;
        max-width: 160px;
        text-align: right;
        word-break: break-all;
      }
    }
  }
}
</style>
