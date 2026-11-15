<template>
  <div class="cpu-result-page">
    <header class="product-heading">
      <div class="heading-copy">
        <button class="back-button" type="button" title="返回 CPU 实例" @click="goToInstances">←</button>
        <div>
          <div class="product-name">算力服务器 CPU</div>
          <div class="product-mode">独立 CPU 实例</div>
        </div>
      </div>
      <div class="purchase-steps" aria-label="购买进度">
        <div class="step complete"><span>1</span>基础配置</div>
        <div class="step-line complete" />
        <div class="step active"><span>2</span>配置与确认</div>
        <div class="step-line" />
        <div class="step"><span>3</span>创建完成</div>
      </div>
    </header>

    <main class="result-shell">
      <section class="status-card">
        <div class="status-icon" :class="{ failed: status === 'failed', pending: status !== 'failed' }">{{ status === 'failed' ? '!' : '✣' }}</div>
        <div>
          <h1>{{ status === 'failed' ? '实例创建失败' : status === 'submitting' ? '订单提交中' : '资源创建中' }}</h1>
          <p>{{ statusMessage }}</p>
        </div>
      </section>

      <el-alert
        :title="status === 'failed' ? '本次 CPU 实例未创建成功，请返回购买页检查配置后重试。' : 'CPU 实例正在后台创建，页面已锁定本次提交，请勿重复购买。'"
        :type="status === 'failed' ? 'error' : 'info'"
        show-icon
        :closable="false"
      />

      <section class="summary-card">
        <div class="summary-header"><h2>配置概要</h2><span :class="['status-tag', status]">{{ statusLabel }}</span></div>
        <div class="summary-section">
          <h3>CPU 配置</h3>
          <div class="summary-grid">
            <div><span>地域</span><strong>{{ form.region || '-' }}</strong></div>
            <div><span>可用区</span><strong>{{ form.zone || '-' }}</strong></div>
            <div><span>实例规格</span><strong>{{ specLabel }}</strong></div>
            <div><span>计费方式</span><strong>{{ billingLabel }}</strong></div>
            <div><span>购买数量</span><strong>{{ form.count || 1 }} 台</strong></div>
            <div><span>购买时长</span><strong>{{ form.billingMode === 'on_demand' ? '按量付费' : `${form.duration || 1}${form.billingMode === 'monthly' ? '个月' : '年'}` }}</strong></div>
          </div>
        </div>
        <div class="summary-section">
          <h3>系统配置</h3>
          <div class="summary-grid">
            <div><span>系统盘</span><strong>{{ form.systemDiskGb || '-' }} GiB</strong></div>
            <div><span>数据盘</span><strong>{{ form.dataDiskGb || '-' }} GiB</strong></div>
            <div><span>创建任务</span><strong>{{ operationId || '提交处理中' }}</strong></div>
            <div><span>实例网络</span><strong>默认分配公网 IP</strong></div>
          </div>
        </div>
      </section>

      <div class="result-actions">
        <el-button @click="goToInstances">管理 CPU 实例</el-button>
        <el-button type="primary" @click="goToPurchase">返回 CPU 购买</el-button>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCpuCreationOperation } from '@/api/instanceV2'

const router = useRouter()
const route = useRoute()
const status = ref<'submitting' | 'creating' | 'failed'>('submitting')
const operationId = ref('')
const statusMessage = ref('CPU 实例已受理，正在创建中，预计需要几分钟。')
const form = reactive({ region: '', zone: '', instanceTypeId: '', billingMode: 'on_demand', duration: 1, systemDiskGb: 0, dataDiskGb: 0, count: 1, cpuCores: 0, memGib: 0 })
let pollTimer: ReturnType<typeof setTimeout> | undefined

const billingLabel = computed(() => ({ on_demand: '按量付费', monthly: '包月', yearly: '包年' }[form.billingMode] || form.billingMode))
const specLabel = computed(() => form.cpuCores || form.memGib ? `${form.instanceTypeId || '-'}（${form.cpuCores || '-'}核 / ${form.memGib || '-'}GiB）` : form.instanceTypeId || '-')
const statusLabel = computed(() => status.value === 'failed' ? '创建失败' : status.value === 'submitting' ? '提交中' : '创建中')

function readDraft() {
  try {
    const raw = sessionStorage.getItem('cpuPurchaseResult')
    if (!raw) return false
    const draft = JSON.parse(raw)
    Object.assign(form, draft.form || {})
    Object.assign(form, draft.spec || {})
    operationId.value = String(route.query.operationId || draft.result?.operation_id || '')
    if (draft.result?.success === false) {
      status.value = 'failed'
      statusMessage.value = draft.result.message || 'CPU 实例创建失败，请返回购买页重试。'
    }
    return true
  } catch {
    return false
  }
}

async function pollOperation() {
  if (!operationId.value) return
  try {
    const res = await getCpuCreationOperation(operationId.value)
    const raw = res.data as any
    const operationStatus = String(raw?.operation_status || raw?.status || '').toLowerCase()
    if (operationStatus === 'failed' || operationStatus === 'error') {
      status.value = 'failed'
      statusMessage.value = raw?.message || 'CPU 实例创建失败，请返回购买页重试。'
      return
    }
    if (operationStatus === 'running' || operationStatus === 'creating' || operationStatus === 'pending') {
      status.value = 'creating'
      statusMessage.value = raw?.message || 'CPU 实例已受理，正在创建中，预计需要几分钟。'
    }
    pollTimer = setTimeout(pollOperation, 5000)
  } catch {
    pollTimer = setTimeout(pollOperation, 8000)
  }
}

function goToInstances() { router.push('/cloud/gpuInstance?type=cpu') }
function goToPurchase() { router.push('/computeListNew?type=cpu') }

onMounted(() => {
  if (!readDraft()) return goToPurchase()
  void pollOperation()
})
onBeforeUnmount(() => { if (pollTimer) clearTimeout(pollTimer) })
</script>

<style scoped lang="scss">
.cpu-result-page { --aliyun-primary:#ff6a00; --aliyun-page-bg:#fff3dc; --aliyun-surface:#fffdf8; --aliyun-tint-strong:#fff0d4; --aliyun-border:#f1d3a1; --aliyun-soft-border:#f5dfba; min-height:100vh; padding:102px 0 48px; overflow-x:clip; background:var(--aliyun-page-bg); color:#1f2937; }
.product-heading { display:flex; align-items:center; justify-content:space-between; gap:32px; width:min(1540px,calc(100vw - 48px)); min-height:58px; margin:0 auto 12px; padding:0 2px; }
.heading-copy { display:flex; align-items:center; gap:12px; flex:0 0 auto; }.back-button { display:inline-flex; align-items:center; justify-content:center; width:36px; height:36px; padding:0; border:1px solid var(--aliyun-border); border-radius:0; background:#fff; color:#4b5563; cursor:pointer; font-size:20px; }.back-button:hover { border-color:var(--aliyun-primary); color:var(--aliyun-primary); }
.product-name { color:#111827; font-size:20px; font-weight:700; }.product-mode { margin-top:3px; color:#7b8494; font-size:13px; }
.purchase-steps { display:grid; grid-template-columns:auto minmax(36px,88px) auto minmax(36px,88px) auto; align-items:center; min-width:510px; }.step { display:inline-flex; align-items:center; gap:8px; white-space:nowrap; color:#8a94a6; font-size:14px; }.step span { display:inline-flex; align-items:center; justify-content:center; width:26px; height:26px; border:1px solid #d2d7df; border-radius:50%; background:#fff; font-size:13px; font-weight:600; }.step.complete,.step.active { color:#1f2937; }.step.complete span { border-color:#ffb47f; background:var(--aliyun-tint-strong); color:var(--aliyun-primary); }.step.active span { border-color:var(--aliyun-primary); background:var(--aliyun-primary); color:#fff; }.step-line { height:1px; margin:0 10px; background:#d7dce4; }.step-line.complete { background:#ffb47f; }
.result-shell { width:min(1540px,calc(100vw - 48px)); min-height:480px; margin:0 auto; }.status-card { display:flex; align-items:flex-start; gap:18px; padding:48px; border:1px solid var(--aliyun-soft-border); background:var(--aliyun-surface); }.status-icon { display:inline-flex; align-items:center; justify-content:center; width:44px; height:44px; flex:0 0 44px; border-radius:50%; background:#e8f1ff; color:#1677ff; font-size:24px; animation:result-icon-pulse 1.2s ease-in-out infinite; }.status-icon.failed { background:#fff0f0; color:#d34f4f; animation:none; } @keyframes result-icon-pulse { 0%,100%{opacity:1;transform:scale(1)} 50%{opacity:.65;transform:scale(.94)} }
h1 { margin:1px 0 8px; color:#111827; font-size:24px; font-weight:700; }.status-card p { margin:0; color:#6b7280; font-size:14px; line-height:1.7; }.status-card + .el-alert { margin-top:20px; border-radius:0; }
.summary-card { margin-top:20px; border:1px solid var(--aliyun-soft-border); background:var(--aliyun-surface); }.summary-header { display:flex; align-items:center; justify-content:space-between; padding:24px 28px; border-bottom:1px solid #f1e5d2; }.summary-header h2 { margin:0; color:#111827; font-size:20px; }.status-tag { padding:5px 11px; border:1px solid #ffb47f; color:var(--aliyun-primary); font-size:13px; }.status-tag.failed { border-color:#f18b8b; color:#d34f4f; }
.summary-section { padding:22px 28px 28px; border-bottom:1px solid #f1e5d2; }.summary-section:last-child { border-bottom:0; }.summary-section h3 { margin:0 0 18px; color:#303745; font-size:15px; }.summary-grid { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:18px 64px; }.summary-grid div { display:flex; justify-content:space-between; gap:24px; }.summary-grid span { color:#8a94a6; font-size:14px; }.summary-grid strong { color:#303745; font-size:14px; text-align:right; }
.result-actions { display:flex; justify-content:flex-end; gap:10px; margin-top:28px; }.result-actions :deep(.el-button) { min-width:112px; height:40px; margin:0; border-radius:0; }.result-actions :deep(.el-button--primary) { border-color:var(--aliyun-primary); background:var(--aliyun-primary); }.result-actions :deep(.el-button--primary:hover) { border-color:#ff7a1a; background:#ff7a1a; }:deep(.el-alert),:deep(.el-button) { border-radius:0; }
@media (max-width:920px) { .product-heading { align-items:flex-start; flex-direction:column; gap:16px; }.purchase-steps { width:100%; min-width:0; }.result-shell { width:calc(100vw - 24px); } } @media (max-width:600px) { .cpu-result-page { padding-top:86px; }.product-heading,.result-shell { width:calc(100vw - 24px); }.purchase-steps { grid-template-columns:auto minmax(12px,1fr) auto minmax(12px,1fr) auto; }.step { gap:5px; font-size:11px; }.step span { width:22px; height:22px; font-size:11px; }.step-line { margin:0 4px; }.status-card { padding:28px 16px; }.summary-grid { grid-template-columns:minmax(0,1fr); }.result-actions { display:grid; grid-template-columns:minmax(0,1fr) minmax(0,1fr); } }
</style>
