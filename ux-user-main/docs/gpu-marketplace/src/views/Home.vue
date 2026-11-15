<template>
  <MainLayout>
    <div class="home-page">
      <!-- Hero 区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">
            <span class="gradient-text">弹性、好用、省钱</span>
            <br />
            GPU算力零售价格新标杆
          </h1>
          <p class="hero-subtitle">
            专业的GPU云服务，秒级计费、稳定好用，高规格机房，7x24小时服务<br />
            更有AI镜像、AI应用技术社区，开箱即用、一键复现算法
          </p>
          <div class="hero-actions">
            <a-button
              type="primary"
              size="large"
              class="btn-primary-large"
              @click="$router.push('/market')"
            >
              <ShoppingOutlined />
              立即租用
            </a-button>
            <a-button
              size="large"
              class="btn-secondary-large"
              @click="$router.push('/console')"
            >
              <DashboardOutlined />
              控制台
            </a-button>
          </div>
        </div>

        <!-- 装饰动画 -->
        <div class="hero-decoration">
          <div class="glow-circle circle-1"></div>
          <div class="glow-circle circle-2"></div>
          <div class="gpu-icon">
            <img src="/gpu-icon.svg" alt="GPU" />
          </div>
        </div>
      </section>

      <!-- 统计数据 -->
      <section class="stats-section">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-number">10,000+</div>
            <div class="stat-label">活跃GPU实例</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">50,000+</div>
            <div class="stat-label">累计用户</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">99.9%</div>
            <div class="stat-label">服务可用性</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">7×24</div>
            <div class="stat-label">小时技术支持</div>
          </div>
        </div>
      </section>

      <!-- 热门GPU推荐 -->
      <section class="recommend-section">
        <div class="section-header">
          <h2 class="section-title">热门GPU推荐</h2>
          <p class="section-subtitle">精选高性价比GPU实例，满足不同场景需求</p>
        </div>

        <div class="gpu-cards-grid">
          <div
            v-for="gpu in hotGPUs"
            :key="gpu.id"
            class="gpu-card"
          >
            <div class="card-header">
              <div class="gpu-model">
                <img :src="getGPUImage(gpu.model)" :alt="gpu.model" class="gpu-icon" />
                <span class="model-name">{{ gpu.model }}</span>
              </div>
              <div class="gpu-tags">
                <span
                  v-for="tag in gpu.tags"
                  :key="tag"
                  class="tag"
                >
                  {{ tag }}
                </span>
              </div>
            </div>

            <div class="card-body">
              <div class="spec-row">
                <span class="spec-label">显存</span>
                <span class="spec-value">{{ gpu.vram }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">CPU</span>
                <span class="spec-value">{{ gpu.cpuCores }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">内存</span>
                <span class="spec-value">{{ gpu.memory }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">区域</span>
                <span class="spec-value">{{ gpu.region }}</span>
              </div>
            </div>

            <div class="card-footer">
              <div class="price">
                <span class="price-value">¥{{ gpu.price }}</span>
                <span class="price-unit">{{ gpu.unit }}</span>
              </div>
              <a-button
                type="primary"
                size="large"
                @click="handleRent(gpu)"
              >
                立即租用
              </a-button>
            </div>
          </div>
        </div>

        <div class="view-all">
          <a-button type="link" size="large" @click="$router.push('/market')">
            查看更多GPU
            <RightOutlined />
          </a-button>
        </div>
      </section>

      <!-- 功能特性 -->
      <section class="features-section">
        <div class="section-header">
          <h2 class="section-title">为什么选择我们</h2>
          <p class="section-subtitle">专业、稳定、高性价比的GPU算力服务</p>
        </div>

        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <ThunderboltOutlined />
            </div>
            <h3>秒级启动</h3>
            <p>GPU实例秒级启动，无需等待，即刻开始训练</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <DollarOutlined />
            </div>
            <h3>弹性计费</h3>
            <p>按秒计费，用多少付多少，关机不收费</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <SafetyOutlined />
            </div>
            <h3>高可用性</h3>
            <p>99.9%可用性保障，数据多副本存储，安全可靠</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <CustomerServiceOutlined />
            </div>
            <h3>7x24支持</h3>
            <p>专业技术团队7x24小时在线，随时为您解决问题</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <CloudOutlined />
            </div>
            <h3>丰富镜像</h3>
            <p>预装TensorFlow、PyTorch等深度学习框架，开箱即用</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <GlobalOutlined />
            </div>
            <h3>多地域覆盖</h3>
            <p>北京、上海、深圳等多地域节点，就近选择低延迟</p>
          </div>
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useGPUStore } from '@/stores/gpu'
import MainLayout from '@/components/layout/MainLayout.vue'
import {
  ShoppingOutlined,
  DashboardOutlined,
  RightOutlined,
  ThunderboltOutlined,
  DollarOutlined,
  SafetyOutlined,
  CustomerServiceOutlined,
  CloudOutlined,
  GlobalOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const router = useRouter()
const gpuStore = useGPUStore()

// 热门GPU（前6个）
const hotGPUs = computed(() => {
  return gpuStore.gpuList.slice(0, 6)
})

function getGPUImage(model) {
  // 根据型号返回对应的图标
  if (model.includes('4090')) return '/icons/rtx4090.svg'
  if (model.includes('3090')) return '/icons/rtx3090.svg'
  if (model.includes('A100')) return '/icons/a100.svg'
  if (model.includes('A10')) return '/icons/a10.svg'
  if (model.includes('H100')) return '/icons/h100.svg'
  return '/icons/gpu-default.svg'
}

function handleRent(gpu) {
  if (gpu.status === 'outofstock') {
    message.warning('该型号暂时缺货')
    return
  }
  if (gpu.status === 'running') {
    message.info('您已有该型号实例在运行中')
    router.push('/console')
    return
  }
  // 跳转到租用页面
  router.push({
    path: '/market',
    query: { rent: gpu.id }
  })
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  position: relative;
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  overflow: hidden;
  background: radial-gradient(ellipse at center, rgba(22, 119, 255, 0.15) 0%, transparent 70%);
}

.hero-content {
  text-align: center;
  max-width: 900px;
  z-index: 2;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: 24px;
  color: var(--text-primary);
}

.gradient-text {
  background: linear-gradient(135deg, #1677ff, #00d4ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
  line-height: 1.8;
  margin-bottom: 40px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.btn-primary-large {
  height: 56px;
  padding: 0 40px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #1677ff, #00d4ff);
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(22, 119, 255, 0.4);
}

.btn-secondary-large {
  height: 56px;
  padding: 0 40px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-secondary-large:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.hero-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  pointer-events: none;
}

.glow-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
}

.circle-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(22, 119, 255, 0.5), transparent);
  top: -200px;
  right: -200px;
}

.circle-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.4), transparent);
  bottom: -100px;
  left: -100px;
}

.gpu-icon {
  position: absolute;
  right: 10%;
  bottom: 20%;
  opacity: 0.1;
}

.gpu-icon img {
  width: 300px;
  height: auto;
}

/* 统计数据 */
.stats-section {
  padding: 80px 24px;
  background: linear-gradient(180deg, var(--bg-dark) 0%, rgba(22, 119, 255, 0.05) 50%, var(--bg-dark) 100%);
}

.stats-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.stat-card {
  text-align: center;
  padding: 40px 20px;
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(22, 119, 255, 0.2);
  border-color: rgba(22, 119, 255, 0.3);
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #1677ff, #00d4ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

/* 推荐GPU */
.recommend-section {
  padding: 80px 24px;
  max-width: 1440px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 50px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.section-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
}

.gpu-cards-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.gpu-card {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  overflow: hidden;
  transition: all 0.3s;
}

.gpu-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  border-color: rgba(22, 119, 255, 0.3);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.1), transparent);
}

.gpu-model {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.gpu-icon {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.model-name {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.gpu-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 2px 8px;
  background: rgba(22, 119, 255, 0.2);
  color: var(--primary-color);
  font-size: 12px;
  border-radius: 4px;
}

.card-body {
  padding: 16px 20px;
}

.spec-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(26, 58, 92, 0.3);
}

.spec-row:last-child {
  border-bottom: none;
}

.spec-label {
  color: var(--text-secondary);
  font-size: 13px;
}

.spec-value {
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
}

.card-footer {
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, transparent, rgba(22, 119, 255, 0.05));
}

.price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--price-color);
}

.price-unit {
  font-size: 13px;
  color: var(--text-secondary);
}

.view-all {
  text-align: center;
}

.view-all :deep(.ant-btn-link) {
  color: var(--primary-color);
  font-size: 16px;
}

.view-all :deep(.ant-btn-link:hover) {
  color: var(--primary-hover);
}

/* 功能特性 */
.features-section {
  padding: 80px 24px;
  background: linear-gradient(180deg, var(--bg-dark), rgba(22, 119, 255, 0.03));
}

.features-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.feature-card {
  text-align: center;
  padding: 40px 30px;
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(22, 119, 255, 0.15);
  border-color: rgba(22, 119, 255, 0.3);
}

.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.2), rgba(0, 212, 255, 0.1));
  border-radius: 16px;
  font-size: 28px;
  color: var(--primary-color);
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.feature-card p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .gpu-cards-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 15px;
  }

  .hero-actions {
    flex-direction: column;
  }

  .gpu-cards-grid {
    grid-template-columns: 1fr;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .section-title {
    font-size: 28px;
  }

  .footer-main {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
