<template>
  <MainLayout>
    <div class="recharge-page">
      <div class="recharge-container">
        <!-- 余额卡片 -->
        <div class="balance-card">
          <div class="balance-info">
            <span class="balance-label">当前余额</span>
            <span class="balance-value">¥{{ userStore.balance.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 充值金额选择 -->
        <div class="amount-section">
          <h3 class="section-title">选择充值金额</h3>

          <div class="amount-grid">
            <div
              v-for="amount in presetAmounts"
              :key="amount"
              class="amount-card"
              :class="{ active: selectedAmount === amount }"
              @click="selectedAmount = amount"
            >
              <span class="amount-value">¥{{ amount }}</span>
              <span v-if="amount >= 1000" class="amount-bonus">送¥{{ (amount * 0.05).toFixed(0) }}</span>
            </div>
          </div>

          <div class="custom-amount">
            <span class="label">自定义金额:</span>
            <a-input-number
              v-model:value="customAmount"
              :min="10"
              :max="100000"
              :step="10"
              placeholder="请输入充值金额"
              @change="selectedAmount = null"
            />
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-section">
          <h3 class="section-title">支付方式</h3>

          <div class="payment-methods">
            <div
              class="method-card"
              :class="{ active: paymentMethod === 'alipay' }"
              @click="paymentMethod = 'alipay'"
            >
              <AlipayCircleOutlined class="method-icon" />
              <span>支付宝</span>
              <CheckCircleFilled v-if="paymentMethod === 'alipay'" class="check-icon" />
            </div>

            <div
              class="method-card"
              :class="{ active: paymentMethod === 'wechat' }"
              @click="paymentMethod = 'wechat'"
            >
              <WechatOutlined class="method-icon" />
              <span>微信支付</span>
              <CheckCircleFilled v-if="paymentMethod === 'wechat'" class="check-icon" />
            </div>
          </div>
        </div>

        <!-- 确认充值 -->
        <div class="submit-section">
          <div class="amount-summary">
            <span>充值金额:</span>
            <span class="total-amount">¥{{ finalAmount }}</span>
          </div>

          <a-button
            type="primary"
            size="large"
            block
            :loading="loading"
            :disabled="finalAmount <= 0"
            @click="handleRecharge"
          >
            立即充值
          </a-button>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import MainLayout from '@/components/layout/MainLayout.vue'
import {
  AlipayCircleOutlined,
  WechatOutlined,
  CheckCircleFilled,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const userStore = useUserStore()

const presetAmounts = [100, 500, 1000, 2000, 5000, 10000]
const selectedAmount = ref(100)
const customAmount = ref(null)
const paymentMethod = ref('alipay')
const loading = ref(false)

const finalAmount = computed(() => {
  if (selectedAmount.value) return selectedAmount.value
  return customAmount.value || 0
})

async function handleRecharge() {
  if (finalAmount.value <= 0) {
    message.warning('请选择或输入充值金额')
    return
  }

  loading.value = true
  try {
    // 模拟充值
    await new Promise(resolve => setTimeout(resolve, 1500))
    userStore.recharge(finalAmount.value)
    message.success(`成功充值 ¥${finalAmount.value}`)
    selectedAmount.value = 100
    customAmount.value = null
  } catch (error) {
    message.error('充值失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.recharge-page {
  padding: 40px 24px;
  max-width: 800px;
  margin: 0 auto;
}

.recharge-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.balance-card {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
  border-radius: 12px;
  padding: 32px;
  text-align: center;
}

.balance-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.balance-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.balance-value {
  font-size: 36px;
  font-weight: 700;
  color: white;
}

.amount-section,
.payment-section,
.submit-section {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.amount-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.amount-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: rgba(22, 119, 255, 0.05);
  border: 2px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.amount-card:hover {
  border-color: var(--primary-color);
}

.amount-card.active {
  border-color: var(--primary-color);
  background: rgba(22, 119, 255, 0.1);
}

.amount-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.amount-bonus {
  font-size: 12px;
  color: #52c41a;
  margin-top: 4px;
}

.custom-amount {
  display: flex;
  align-items: center;
  gap: 12px;
}

.custom-amount .label {
  color: var(--text-secondary);
  font-size: 14px;
  white-space: nowrap;
}

.custom-amount :deep(.ant-input-number) {
  flex: 1;
}

.payment-methods {
  display: flex;
  gap: 12px;
}

.method-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: rgba(22, 119, 255, 0.05);
  border: 2px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.method-card:hover {
  border-color: var(--primary-color);
}

.method-card.active {
  border-color: var(--primary-color);
  background: rgba(22, 119, 255, 0.1);
}

.method-icon {
  font-size: 24px;
  color: var(--primary-color);
}

.check-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 16px;
  color: var(--primary-color);
}

.submit-section {
  text-align: center;
}

.amount-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: rgba(22, 119, 255, 0.05);
  border-radius: 8px;
}

.amount-summary span:first-child {
  color: var(--text-secondary);
  font-size: 14px;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--price-color);
}

.submit-section :deep(.ant-btn) {
  height: 48px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .recharge-page {
    padding: 20px 16px;
  }

  .amount-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .payment-methods {
    flex-direction: column;
  }
}
</style>
