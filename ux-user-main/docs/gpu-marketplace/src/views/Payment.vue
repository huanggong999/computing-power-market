<template>
  <MainLayout>
    <div class="payment-page">
      <div class="payment-container">
        <!-- 订单信息 -->
        <div class="order-section">
          <h2 class="section-title">订单支付</h2>

          <div v-if="order" class="order-detail">
            <div class="order-header">
              <span class="order-id">订单号: {{ order.id }}</span>
              <a-tag :color="getStatusColor(order.status)">{{ getStatusText(order.status) }}</a-tag>
            </div>

            <a-divider />

            <div class="gpu-info">
              <div class="info-row">
                <span class="label">GPU型号:</span>
                <span class="value">{{ order.gpuModel }}</span>
              </div>
              <div class="info-row">
                <span class="label">CPU:</span>
                <span class="value">{{ order.gpuInfo?.cpuCores }}</span>
              </div>
              <div class="info-row">
                <span class="label">内存:</span>
                <span class="value">{{ order.gpuInfo?.memory }}</span>
              </div>
              <div class="info-row">
                <span class="label">存储:</span>
                <span class="value">{{ order.gpuInfo?.disk }}</span>
              </div>
              <div class="info-row">
                <span class="label">区域:</span>
                <span class="value">{{ order.gpuInfo?.region }}</span>
              </div>
              <div class="info-row">
                <span class="label">镜像:</span>
                <span class="value">{{ order.config?.image }}</span>
              </div>
            </div>

            <a-divider />

            <div class="price-summary">
              <div class="price-row">
                <span>单价</span>
                <span>¥{{ order.price }}/小时</span>
              </div>
              <div class="price-row">
                <span>时长</span>
                <span>{{ order.duration }}小时</span>
              </div>
              <div class="price-row total">
                <span>应付总额</span>
                <span class="total-price">¥{{ order.totalAmount }}</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <InboxOutlined class="empty-icon" />
            <p>订单不存在或已过期</p>
            <a-button type="primary" @click="$router.push('/market')">去算力市场</a-button>
          </div>
        </div>

        <!-- 支付方式 -->
        <div v-if="order && order.status === 'unpaid'" class="payment-section">
          <h3 class="section-title">选择支付方式</h3>

          <div class="payment-methods">
            <div
              class="method-card"
              :class="{ active: paymentMethod === 'balance' }"
              @click="paymentMethod = 'balance'"
            >
              <WalletOutlined class="method-icon" />
              <div class="method-info">
                <span class="method-name">余额支付</span>
                <span class="method-desc">可用余额: ¥{{ userStore.balance.toFixed(2) }}</span>
              </div>
              <CheckCircleFilled v-if="paymentMethod === 'balance'" class="check-icon" />
            </div>

            <div
              class="method-card"
              :class="{ active: paymentMethod === 'alipay' }"
              @click="paymentMethod = 'alipay'"
            >
              <AlipayCircleOutlined class="method-icon" />
              <div class="method-info">
                <span class="method-name">支付宝</span>
                <span class="method-desc">推荐使用</span>
              </div>
              <CheckCircleFilled v-if="paymentMethod === 'alipay'" class="check-icon" />
            </div>

            <div
              class="method-card"
              :class="{ active: paymentMethod === 'wechat' }"
              @click="paymentMethod = 'wechat'"
            >
              <WechatOutlined class="method-icon" />
              <div class="method-info">
                <span class="method-name">微信支付</span>
                <span class="method-desc">快捷支付</span>
              </div>
              <CheckCircleFilled v-if="paymentMethod === 'wechat'" class="check-icon" />
            </div>
          </div>

          <div class="payment-actions">
            <a-button size="large" @click="$router.push('/market')">取消</a-button>
            <a-button
              type="primary"
              size="large"
              :loading="loading"
              :disabled="!canPay"
              @click="handlePay"
            >
              确认支付 ¥{{ order?.totalAmount }}
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/components/layout/MainLayout.vue'
import {
  InboxOutlined,
  WalletOutlined,
  AlipayCircleOutlined,
  WechatOutlined,
  CheckCircleFilled,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()
const userStore = useUserStore()

const loading = ref(false)
const paymentMethod = ref('balance')

const orderId = computed(() => route.query.orderId)
const order = computed(() => orderStore.getOrderById(orderId.value))

const canPay = computed(() => {
  if (!order.value) return false
  if (paymentMethod.value === 'balance') {
    return userStore.balance >= order.value.totalAmount
  }
  return true
})

function getStatusColor(status) {
  const map = {
    unpaid: 'orange',
    paid: 'blue',
    running: 'green',
    cancelled: 'default',
  }
  return map[status] || 'default'
}

function getStatusText(status) {
  const map = {
    unpaid: '待支付',
    paid: '已支付',
    running: '运行中',
    cancelled: '已取消',
  }
  return map[status] || status
}

async function handlePay() {
  if (!order.value) return

  loading.value = true
  try {
    const result = await orderStore.payOrder(order.value.id)
    if (result.success) {
      message.success('支付成功')
      router.push('/console')
    } else {
      message.error(result.error || '支付失败')
    }
  } catch (error) {
    message.error('支付失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!orderId.value) {
    message.error('订单ID不能为空')
    router.push('/market')
  }
})
</script>

<style scoped>
.payment-page {
  padding: 40px 24px;
  max-width: 1000px;
  margin: 0 auto;
}

.payment-container {
  display: grid;
  gap: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 24px;
}

.order-section,
.payment-section {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 32px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-id {
  font-size: 14px;
  color: var(--text-secondary);
}

.gpu-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid rgba(26, 58, 92, 0.3);
}

.info-row .label {
  color: var(--text-secondary);
  font-size: 14px;
}

.info-row .value {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
}

.price-summary {
  margin-top: 16px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.price-row.total {
  margin-top: 8px;
  padding-top: 16px;
  border-top: 2px solid var(--border-color);
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.total-price {
  color: var(--price-color);
  font-size: 24px;
  font-weight: 700;
}

/* Payment Methods */
.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.method-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(22, 119, 255, 0.05);
  border: 2px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-card:hover {
  border-color: var(--primary-color);
}

.method-card.active {
  border-color: var(--primary-color);
  background: rgba(22, 119, 255, 0.1);
}

.method-icon {
  font-size: 28px;
  color: var(--primary-color);
}

.method-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.method-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

.check-icon {
  font-size: 20px;
  color: var(--primary-color);
}

.payment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.payment-actions :deep(.ant-btn-primary) {
  height: 48px;
  padding: 0 40px;
  font-size: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  color: var(--text-secondary);
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  color: var(--text-secondary);
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .payment-page {
    padding: 20px 16px;
  }

  .order-section,
  .payment-section {
    padding: 20px;
  }

  .gpu-info {
    grid-template-columns: 1fr;
  }

  .payment-actions {
    flex-direction: column;
  }

  .payment-actions :deep(.ant-btn) {
    width: 100%;
  }
}
</style>
