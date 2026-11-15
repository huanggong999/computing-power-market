<template>
  <MainLayout>
    <div class="help-page">
      <div class="help-container">
        <div class="help-header">
          <h1>帮助中心</h1>
          <p>常见问题解答与使用指南</p>
        </div>

        <!-- 常见问题 -->
        <div class="faq-section">
          <h2 class="section-title">常见问题</h2>

          <a-collapse v-model:activeKey="activeKey" :bordered="false">
            <a-collapse-panel
              v-for="faq in faqs"
              :key="faq.id"
              :header="faq.question"
            >
              <p>{{ faq.answer }}</p>
            </a-collapse-panel>
          </a-collapse>
        </div>

        <!-- 使用指南 -->
        <div class="guide-section">
          <h2 class="section-title">使用指南</h2>

          <div class="guide-grid">
            <div
              v-for="guide in guides"
              :key="guide.id"
              class="guide-card"
              @click="showGuide(guide)"
            >
              <div class="guide-icon">
                <component :is="guide.icon" />
              </div>
              <h3>{{ guide.title }}</h3>
              <p>{{ guide.desc }}</p>
            </div>
          </div>
        </div>

        <!-- 联系支持 -->
        <div class="support-section">
          <h2 class="section-title">联系支持</h2>

          <div class="support-cards">
            <div class="support-card">
              <CustomerServiceOutlined class="support-icon" />
              <h3>在线客服</h3>
              <p>7x24小时在线，即时解答</p>
              <a-button type="primary">立即咨询</a-button>
            </div>

            <div class="support-card">
              <MailOutlined class="support-icon" />
              <h3>邮件支持</h3>
              <p>support@gpumarket.com</p>
              <a-button>发送邮件</a-button>
            </div>

            <div class="support-card">
              <PhoneOutlined class="support-icon" />
              <h3>电话支持</h3>
              <p>400-888-8888</p>
              <a-button>拨打热线</a-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref } from 'vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import {
  CustomerServiceOutlined,
  MailOutlined,
  PhoneOutlined,
  CloudServerOutlined,
  CreditCardOutlined,
  CodeOutlined,
  SafetyOutlined,
} from '@ant-design/icons-vue'

const activeKey = ref(['1'])

const faqs = [
  {
    id: '1',
    question: '如何租用GPU实例?',
    answer: '登录后进入算力市场，选择您需要的GPU型号，点击"租用"按钮，选择镜像配置后确认订单并完成支付即可。',
  },
  {
    id: '2',
    question: '如何连接GPU实例?',
    answer: '实例启动后，您可以在控制台查看SSH连接命令。使用终端执行SSH命令即可连接到实例。Windows用户可使用PuTTY或Xshell等工具。',
  },
  {
    id: '3',
    question: '计费方式是怎样的?',
    answer: '我们采用按秒计费的方式，开机时开始计费，关机后停止计费。最低消费0.01元。包月用户可享受85折优惠。',
  },
  {
    id: '4',
    question: '如何上传和下载数据?',
    answer: '推荐使用scp或rsync命令传输数据。也可以使用我们提供的对象存储服务，通过OSS控制台管理您的数据。',
  },
  {
    id: '5',
    question: '实例可以保留多久?',
    answer: '实例最长可保留30天。到期前系统会发送提醒，您可以选择续费或释放实例。未续费的实例将被自动释放。',
  },
]

const guides = [
  {
    id: '1',
    title: '快速入门',
    desc: '了解平台基本概念和操作流程',
    icon: CloudServerOutlined,
  },
  {
    id: '2',
    title: '计费说明',
    desc: '详细了解计费规则和优惠活动',
    icon: CreditCardOutlined,
  },
  {
    id: '3',
    title: '开发指南',
    desc: 'SSH连接、环境配置、代码部署',
    icon: CodeOutlined,
  },
  {
    id: '4',
    title: '安全规范',
    desc: '数据安全、访问控制、合规要求',
    icon: SafetyOutlined,
  },
]

function showGuide(guide) {
  // 显示指南详情
  console.log('Show guide:', guide)
}
</script>

<style scoped>
.help-page {
  padding: 40px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.help-header {
  text-align: center;
  margin-bottom: 40px;
}

.help-header h1 {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.help-header p {
  font-size: 16px;
  color: var(--text-secondary);
}

.faq-section,
.guide-section,
.support-section {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 32px;
  margin-bottom: 24px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.faq-section :deep(.ant-collapse) {
  background: transparent;
}

.faq-section :deep(.ant-collapse-item) {
  border-bottom: 1px solid var(--border-color);
}

.faq-section :deep(.ant-collapse-header) {
  color: var(--text-primary) !important;
  font-weight: 500;
  padding: 16px 0 !important;
}

.faq-section :deep(.ant-collapse-content) {
  background: transparent;
  border: none;
}

.faq-section :deep(.ant-collapse-content-box) {
  padding: 0 0 16px;
  color: var(--text-secondary);
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.guide-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(22, 119, 255, 0.05);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s;
}

.guide-card:hover {
  border-color: var(--primary-color);
  background: rgba(22, 119, 255, 0.1);
}

.guide-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(22, 119, 255, 0.1);
  border-radius: 10px;
  font-size: 24px;
  color: var(--primary-color);
  flex-shrink: 0;
}

.guide-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.guide-card p {
  font-size: 13px;
  color: var(--text-secondary);
}

.support-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.support-card {
  text-align: center;
  padding: 32px 24px;
  background: rgba(22, 119, 255, 0.05);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

.support-card:hover {
  border-color: var(--primary-color);
  transform: translateY(-4px);
}

.support-icon {
  font-size: 36px;
  color: var(--primary-color);
  margin-bottom: 12px;
}

.support-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.support-card p {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .help-page {
    padding: 20px 16px;
  }

  .guide-grid {
    grid-template-columns: 1fr;
  }

  .support-cards {
    grid-template-columns: 1fr;
  }

  .faq-section,
  .guide-section,
  .support-section {
    padding: 20px;
  }
}
</style>
