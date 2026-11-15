<template>
  <MainLayout>
    <div class="user-page">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="user-info">
          <div class="avatar">
            <UserOutlined v-if="!userStore.avatar" />
            <img v-else :src="userStore.avatar" alt="avatar" />
          </div>
          <div class="user-detail">
            <h2>{{ userStore.username }}</h2>
            <p>{{ userStore.userInfo?.email }}</p>
          </div>
        </div>
        <div class="user-stats">
          <div class="stat">
            <span class="stat-value">¥{{ userStore.balance.toFixed(2) }}</span>
            <span class="stat-label">账户余额</span>
          </div>
          <a-button type="primary" @click="$router.push('/recharge')">充值</a-button>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="orders-section">
        <h3 class="section-title">我的订单</h3>

        <a-table
          :dataSource="orderStore.orderHistory"
          :columns="orderColumns"
          :pagination="{ pageSize: 10 }"
          row-key="id"
          class="orders-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'gpuModel'">
              <span>{{ record.gpuModel }}</span>
            </template>

            <template v-else-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
            </template>

            <template v-else-if="column.key === 'totalAmount'">
              <span class="price">¥{{ record.totalAmount }}</span>
            </template>

            <template v-else-if="column.key === 'createTime'">
              <span>{{ formatTime(record.createTime) }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <a-button
                v-if="record.status === 'unpaid'"
                type="primary"
                size="small"
                @click="handlePay(record)"
              >去支付</a-button>
              <a-button
                v-else-if="record.status === 'running'"
                size="small"
                @click="$router.push('/console')"
>管理</a-button>
              <span v-else>--</span>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useOrderStore } from '@/stores/order'
import MainLayout from '@/components/layout/MainLayout.vue'
import { UserOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const orderStore = useOrderStore()

const orderColumns = [
  { title: '订单号', dataIndex: 'id', key: 'id' },
  { title: 'GPU型号', dataIndex: 'gpuModel', key: 'gpuModel' },
  { title: '金额', dataIndex: 'totalAmount', key: 'totalAmount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '操作', key: 'action' },
]

function getStatusColor(status) {
  const map = {
    unpaid: 'orange',
    paid: 'blue',
    running: 'green',
    cancelled: 'default',
    failed: 'red',
  }
  return map[status] || 'default'
}

function getStatusText(status) {
  const map = {
    unpaid: '待支付',
    paid: '已支付',
    running: '运行中',
    cancelled: '已取消',
    failed: '失败',
  }
  return map[status] || status
}

function formatTime(timestamp) {
  if (!timestamp) return '--'
  return new Date(timestamp).toLocaleString()
}

function handlePay(record) {
  router.push({
    path: '/payment',
    query: { orderId: record.id },
  })
}
</script>

<style scoped>
.user-page {
  padding: 40px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.user-card {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: white;
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-detail h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.user-detail p {
  color: var(--text-secondary);
  font-size: 14px;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--price-color);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.orders-section {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.price {
  color: var(--price-color);
  font-weight: 500;
}

@media (max-width: 768px) {
  .user-page {
    padding: 20px 16px;
  }

  .user-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .user-info {
    flex-direction: column;
  }

  .user-stats {
    flex-direction: column;
    width: 100%;
  }

  .stat {
    align-items: center;
  }
}
</style>
