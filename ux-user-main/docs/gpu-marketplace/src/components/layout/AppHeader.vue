<template>
  <header class="app-header">
    <div class="header-content">
      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        <img src="/logo.svg" alt="Logo" class="logo-icon" />
        <span class="logo-text">算力市场</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :style="{ background: 'transparent', border: 'none' }"
        >
          <a-menu-item key="/" @click="$router.push('/')">
            <HomeOutlined />
            首页
          </a-menu-item>

          <a-menu-item key="/market" @click="$router.push('/market')">
            <ShoppingOutlined />
            算力市场
          </a-menu-item>

          <a-menu-item key="/console" @click="$router.push('/console')">
            <DashboardOutlined />
            控制台
          </a-menu-item>

          <a-menu-item key="/user" @click="$router.push('/user')">
            <UserOutlined />
            用户中心
          </a-menu-item>

          <a-menu-item key="/help" @click="$router.push('/help')">
            <QuestionCircleOutlined />
            帮助中心
          </a-menu-item>
        </a-menu>
      </nav>

      <!-- 右侧功能区 -->
      <div class="header-actions">
        <!-- 余额显示 -->
        <template v-if="userStore.isLoggedIn">
          <div class="balance">
            <WalletOutlined />
            <span class="balance-text">余额: </span>
            <span class="balance-amount">¥{{ userStore.balance.toFixed(2) }}</span>
          </div>

          <a-button type="primary" size="small" @click="$router.push('/recharge')">
            充值
          </a-button>

          <a-dropdown>
            <a-button type="text" class="user-dropdown">
              <UserOutlined />
              {{ userStore.username }}
              <DownOutlined />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="$router.push('/user')">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-item @click="$router.push('/console')">
                  <DashboardOutlined />
                  我的实例
                </a-menu-item>
                <a-menu-item @click="$router.push('/recharge')">
                  <WalletOutlined />
                  充值中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>

        <template v-else>
          <a-button type="primary" @click="$router.push('/login')">
            登录 / 注册
          </a-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'
import {
  HomeOutlined,
  ShoppingOutlined,
  DashboardOutlined,
  UserOutlined,
  QuestionCircleOutlined,
  WalletOutlined,
  DownOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const selectedKeys = ref([route.path])

// 监听路由变化更新选中项
watch(() => route.path, (path) => {
  selectedKeys.value = [path]
})

const handleLogout = () => {
  userStore.logout()
  message.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: linear-gradient(135deg, rgba(0, 21, 41, 0.95), rgba(0, 33, 64, 0.95));
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(26, 58, 92, 0.6);
  z-index: 1000;
}

.header-content {
  max-width: 1440px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 40px;
  height: 40px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #1677ff, #4096ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 40px;
}

.nav-menu :deep(.ant-menu-item) {
  color: var(--text-secondary);
  font-size: 15px;
  padding: 0 20px !important;
  transition: all 0.3s;
}

.nav-menu :deep(.ant-menu-item:hover) {
  color: var(--text-primary);
}

.nav-menu :deep(.ant-menu-item-selected) {
  color: var(--primary-color) !important;
  background: transparent !important;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.balance {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(22, 119, 255, 0.1);
  border-radius: 6px;
  border: 1px solid rgba(22, 119, 255, 0.2);
}

.balance-text {
  color: var(--text-secondary);
  font-size: 13px;
}

.balance-amount {
  color: var(--price-color);
  font-weight: 600;
  font-size: 15px;
}

.user-dropdown {
  color: var(--text-primary) !important;
  padding: 4px 12px !important;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.1) !important;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .nav-menu {
    display: none;
  }

  .header-content {
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .balance {
    display: none;
  }

  .logo-text {
    display: none;
  }
}
</style>
