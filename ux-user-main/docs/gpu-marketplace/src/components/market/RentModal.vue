<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    :width="680"
    :destroy-on-close="true"
    class="rent-modal"
    @cancel="handleCancel"
  >
    <div class="modal-header">
      <div class="gpu-icon" :style="getGPUIconStyle(gpu?.model)">
        {{ getGPUIcon(gpu?.model) }}
      </div>
      <div class="header-info">
        <h2 class="gpu-title">租用 {{ gpu?.model }} - {{ gpu?.vram }}</h2>
        <p class="gpu-desc">{{ gpu?.description }}</p>
      </div>
    </div>

    <a-divider />

    <div class="modal-body">
      <!-- 实例规格 -->
      <div class="config-section">
        <h3 class="section-title">
          <AppstoreOutlined />
          实例规格
        </h3>

        <div class="specs-display">
          <div class="spec-item">
            <span class="spec-label">GPU</span>
            <span class="spec-value">{{ gpu?.model }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">显存</span>
            <span class="spec-value">{{ gpu?.vram }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">CPU</span>
            <span class="spec-value">{{ gpu?.cpuCores }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">内存</span>
            <span class="spec-value">{{ gpu?.memory }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">存储</span>
            <span class="spec-value">{{ gpu?.disk }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">区域</span>
            <span class="spec-value">{{ gpu?.region }}</span>
          </div>
        </div>
      </div>

      <!-- 镜像配置 -->
      <div class="config-section">
        <h3 class="section-title">
          <ContainerOutlined />
          系统镜像
        </h3>

        <a-radio-group v-model:value="selectedImage" class="image-options">
          <a-radio value="ubuntu2204">
            <div class="image-option">
              <span class="image-name">Ubuntu 22.04 LTS</span>
              <span class="image-desc">推荐 · 稳定 · 长期支持</span>
            </div>
          </a-radio>

          <a-radio value="ubuntu2004">
            <div class="image-option">
              <span class="image-name">Ubuntu 20.04 LTS</span>
              <span class="image-desc">经典版本</span>
            </div>
          </a-radio>

          <a-radio value="pytorch">
            <div class="image-option">
              <span class="image-name">PyTorch深度学习镜像</span>
              <span class="image-desc">预装 PyTorch + CUDA</span>
            </div>
          </a-radio>

          <a-radio value="tensorflow">
            <div class="image-option">
              <span class="image-name">TensorFlow深度学习镜像</span>
              <span class="image-desc">预装 TensorFlow + CUDA</span>
            </div>
          </a-radio>
        </a-radio-group>
      </div>

      <!-- 计费信息 -->
      <div class="config-section billing-section">
        <h3 class="section-title">
          <ClockCircleOutlined />
          计费信息
        </h3>

        <div class="billing-info">
          <div class="billing-item">
            <span class="billing-label">单价</span>
            <span class="billing-value">¥{{ gpu?.price }}/小时</span>
          </div>

          <div class="billing-item highlight">
            <span class="billing-label">预付费折扣</span>
            <span class="billing-value discount">包月享8.5折</span>
          </div>

          <div class="billing-notice">
            <InfoCircleOutlined />
            <span>按秒计费，关机停止计费，最低消费0.01元</span>
          </div>
        </div>
      </div>
    </div>

    <a-divider />

    <div class="modal-footer">
      <div class="footer-info">
        <span class="info-item">
          <span class="info-label">配置费用:</span>
          <span class="info-value">¥{{ gpu?.price }}/小时</span>
        </span>
        <span class="info-item">
          <span class="info-label">预估月费:</span>
          <span class="info-value highlight">~¥{{ ((gpu?.price || 0) * 730 * 0.85).toFixed(2) }}</span>
        </span>
      </div>
      <div class="footer-actions">
        <a-button size="large" @click="handleCancel">取消</a-button>
        <a-button
          type="primary"
          size="large"
          :loading="loading"
          @click="handleSubmit"
        >
          确认租用
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useGPUStore } from '@/stores/gpu'
import { useUserStore } from '@/stores/user'
import { useOrderStore } from '@/stores/order'
import {
  AppstoreOutlined,
  ContainerOutlined,
  ClockCircleOutlined,
  InfoCircleOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  gpu: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:visible', 'success', 'cancel'])

const router = useRouter()
const gpuStore = useGPUStore()
const userStore = useUserStore()
const orderStore = useOrderStore()

// 本地状态
const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const loading = ref(false)
const selectedImage = ref('ubuntu2204')

// 方法
function getGPUIcon(model) {
  if (!model) return 'GPU'
  if (model.includes('RTX')) return 'RTX'
  if (model.includes('A100') || model.includes('A10')) return 'A'
  if (model.includes('H100')) return 'H'
  if (model.includes('V100')) return 'V'
  return 'GPU'
}

function getGPUIconStyle(model) {
  if (!model) return {}
  let gradient = 'linear-gradient(135deg, #1677ff, #00d4ff)'
  if (model.includes('4090') || model.includes('3090')) {
    gradient = 'linear-gradient(135deg, #ff6b6b, #ff8e8e)'
  } else if (model.includes('A100') || model.includes('H100')) {
    gradient = 'linear-gradient(135deg, #ffd93d, #ff6b6b)'
  }
  return { background: gradient }
}

function getStatusText(status) {
  const map = {
    available: '可租用',
    running: '运行中',
    outofstock: '缺货',
  }
  return map[status] || status
}

function handleCancel() {
  visible.value = false
  emit('cancel')
}

async function handleSubmit() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  loading.value = true
  try {
    // 创建订单
    const result = await orderStore.createOrder(props.gpu.id, 1, {
      image: selectedImage.value,
    })

    if (result.success) {
      message.success('订单创建成功')
      visible.value = false
      emit('success')
      // 跳转到支付页面
      router.push({
        path: '/payment',
        query: { orderId: result.order.id },
      })
    } else {
      message.error(result.error || '创建订单失败')
    }
  } catch (error) {
    message.error('操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.rent-modal :deep(.ant-modal-content) {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
}

.rent-modal :deep(.ant-modal-close) {
  color: var(--text-secondary);
}

.rent-modal :deep(.ant-modal-close:hover) {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.1);
}

.modal-header {
  display: flex;
  gap: 20px;
  padding: 8px 0 16px;
}

.gpu-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.header-info {
  flex: 1;
}

.gpu-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.gpu-desc {
  font-size: 14px;
  color: var(--text-secondary);
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.available {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
}

.modal-body {
  padding: 8px 0;
}

.config-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title :deep(.anticon) {
  color: var(--primary-color);
}

/* Specs Display */
.specs-display {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
  background: rgba(22, 119, 255, 0.05);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.spec-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.spec-item:not(:last-child) {
  border-bottom: 1px solid rgba(26, 58, 92, 0.3);
}

.spec-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.spec-value {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

/* Image Options */
.image-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.image-options :deep(.ant-radio-wrapper) {
  margin-right: 0;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  transition: all 0.3s;
}

.image-options :deep(.ant-radio-wrapper:hover) {
  border-color: var(--primary-color);
  background: rgba(22, 119, 255, 0.05);
}

.image-options :deep(.ant-radio-wrapper-checked) {
  border-color: var(--primary-color);
  background: rgba(22, 119, 255, 0.1);
}

.image-option {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.image-name {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.image-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

/* Billing Section */
.billing-section {
  background: rgba(22, 119, 255, 0.05);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid var(--border-color);
}

.billing-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.billing-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.billing-item.highlight {
  padding: 8px;
  background: rgba(82, 196, 26, 0.1);
  border-radius: 4px;
}

.billing-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.billing-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.billing-value.discount {
  color: #52c41a;
}

.billing-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
  font-size: 12px;
  color: var(--text-secondary);
}

/* Modal Footer */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.info-value.highlight {
  color: var(--price-color);
  font-size: 18px;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

.footer-actions :deep(.ant-btn-primary) {
  height: 44px;
  padding: 0 32px;
  font-size: 15px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .modal-header {
    flex-direction: column;
    text-align: center;
  }

  .specs-display {
    grid-template-columns: 1fr;
  }

  .modal-footer {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .footer-actions {
    flex-direction: column;
  }

  .footer-actions :deep(.ant-btn) {
    width: 100%;
  }
}
</style>
