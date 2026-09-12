<template>
  <div class="modelscope-page">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo">
          <img src="/images/modelZoo/logo.png" alt="ModelScope" />
          <span class="logo-text">ModelScope</span>
        </div>

        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="搜索模型、数据集、创空间..."
            prefix-icon="Search"
            class="search-input"
          />
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <a href="#" class="nav-item active">首页</a>
          <a href="#" class="nav-item">模型库</a>
          <a href="#" class="nav-item">数据集</a>
          <a href="#" class="nav-item">创空间</a>
          <a href="#" class="nav-item">文档</a>
          <a href="#" class="nav-item">社区</a>
        </nav>

        <!-- 右侧工具栏 -->
        <div class="header-tools">
          <el-button type="primary" class="create-btn">
            <el-icon><Plus /></el-icon>
            创建
          </el-button>
          <div class="user-avatar-small">
            <img src="/images/modelZoo/avatars/default.png" alt="avatar" />
          </div>
        </div>
      </div>
    </header>

    <!-- 热门搜索 -->
    <div class="hot-search">
      <div class="hot-search-container">
        <span class="hot-label">热门搜索</span>
        <div class="hot-tags">
          <span v-for="tag in hotSearchTags" :key="tag" class="hot-tag">{{ tag }}</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 左侧边栏 -->
      <aside class="left-sidebar">
        <!-- 用户信息卡片 -->
        <div class="user-card">
          <div class="user-avatar-large">
            <img src="/images/modelZoo/avatars/default.png" alt="avatar" />
          </div>
          <div class="user-name">
            modeljx
            <el-icon class="verified-icon"><CircleCheck /></el-icon>
          </div>
          <el-button type="primary" plain size="small" class="profile-btn">
            个人主页 <el-icon><ArrowRight /></el-icon>
          </el-button>

          <!-- 勋章区域 -->
          <div class="medals-section">
            <div class="medals-row">
              <el-tooltip content="创空间达人 Lv3" placement="top">
                <img src="/images/modelZoo/medals/studio-lv3.png" class="medal-icon" />
              </el-tooltip>
              <el-tooltip content="数据集达人 Lv3" placement="top">
                <img src="/images/modelZoo/medals/dataset-lv3.png" class="medal-icon" />
              </el-tooltip>
              <el-tooltip content="AIGC达人 Lv3" placement="top">
                <img src="/images/modelZoo/medals/aigc-lv3.png" class="medal-icon" />
              </el-tooltip>
              <el-tooltip content="数据集达人 Lv2" placement="top">
                <img src="/images/modelZoo/medals/dataset-lv2.png" class="medal-icon" />
              </el-tooltip>
              <el-tooltip content="数据集达人 Lv1" placement="top">
                <img src="/images/modelZoo/medals/dataset-lv1.png" class="medal-icon" />
              </el-tooltip>
            </div>
            <div class="medal-notice">
              <div class="notice-title">社区勋章全面上线</div>
              <div class="notice-desc">勋章送好礼，快来加入～</div>
              <el-button type="primary" size="small">立即加入</el-button>
            </div>
          </div>
        </div>

        <!-- 菜单列表 -->
        <nav class="menu-list">
          <div class="menu-item active">
            <el-icon><HomeFilled /></el-icon>
            <span>概览</span>
          </div>
          <div class="menu-item">
            <el-icon><User /></el-icon>
            <span>个人主页</span>
          </div>
          <div class="menu-item">
            <el-icon><Collection /></el-icon>
            <span>我创建的</span>
          </div>
          <div class="menu-item">
            <el-icon><Star /></el-icon>
            <span>我喜欢的</span>
          </div>
          <div class="menu-item">
            <el-icon><Notebook /></el-icon>
            <span>我的Notebook</span>
          </div>
          <div class="menu-item">
            <el-icon><Service /></el-icon>
            <span>模型服务</span>
            <el-tag size="small" type="success">评测服务</el-tag>
          </div>
          <div class="menu-item">
            <el-icon><SetUp /></el-icon>
            <span>MCP部署服务</span>
          </div>
          <div class="menu-item">
            <el-icon><Lock /></el-icon>
            <span>访问控制</span>
          </div>
        </nav>
      </aside>

      <!-- 中间内容区 -->
      <main class="content-area">
        <!-- Banner轮播 -->
        <div class="banner-section">
          <el-carousel height="180px" :interval="5000" indicator-position="outside">
            <el-carousel-item v-for="(banner, index) in banners" :key="index">
              <div class="banner-item">
                <img :src="banner.image" :alt="banner.title" />
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <div class="action-card">
            <div class="action-icon">
              <img src="/images/modelZoo/logo.png" />
            </div>
            <span class="action-name">创建模型</span>
          </div>
          <div class="action-card">
            <div class="action-icon">
              <img src="/images/modelZoo/logo.png" />
            </div>
            <span class="action-name">创建数据集</span>
          </div>
          <div class="action-card">
            <div class="action-icon">
              <img src="/images/modelZoo/logo.png" />
            </div>
            <span class="action-name">创建创空间</span>
          </div>
        </div>

        <!-- 内容流 -->
        <div class="content-section">
          <div class="content-header">
            <div class="header-title">相关推荐</div>
            <div class="tabs">
              <span
                v-for="tab in contentTabs"
                :key="tab.key"
                :class="['tab-item', { active: activeTab === tab.key }]"
                @click="activeTab = tab.key"
              >
                {{ tab.label }}
              </span>
            </div>
          </div>

          <div class="content-list">
            <div v-for="item in filteredContentList" :key="item.id" class="content-card">
              <div class="content-left">
                <div v-if="item.tag" class="content-tag">{{ item.tag }}</div>
                <div class="content-title">{{ item.title }}</div>
                <div class="content-meta">
                  <template v-if="item.author">
                    <span class="author">{{ item.author }}</span>
                    <span class="action">{{ item.action }}</span>
                  </template>
                  <template v-if="item.organizer">
                    <span class="organizer-label">主办方：</span>
                    <img v-if="item.organizerLogo" :src="item.organizerLogo" class="organizer-logo" />
                    <span class="organizer-name">{{ item.organizer }}</span>
                  </template>
                </div>
                <div v-if="item.description" class="content-desc">{{ item.description }}</div>
                <div v-if="item.participants" class="participants">
                  <span>参与人数：{{ item.participants }}</span>
                </div>
              </div>
              <div class="content-right">
                <span class="time">{{ item.time }}</span>
                <span v-if="item.downloads !== undefined" class="stats">
                  <el-icon><Download /></el-icon> {{ item.downloads }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 社区开源动向 -->
        <div class="trend-section">
          <div class="section-header">
            <div class="section-title">社区开源动向</div>
            <div class="sub-tabs">
              <span
                v-for="tab in trendTabs"
                :key="tab.key"
                :class="['sub-tab', { active: activeTrendTab === tab.key }]"
                @click="activeTrendTab = tab.key"
              >
                {{ tab.label }}
              </span>
            </div>
          </div>
          <div class="trend-list">
            <div v-for="item in filteredTrendList" :key="item.id" class="trend-item">
              <div class="trend-left">
                <div class="trend-name">{{ item.name }}</div>
                <span class="trend-org">@{{ item.org }}</span>
              </div>
              <div v-if="item.cover" class="trend-cover">
                <img :src="item.cover" />
              </div>
              <div class="trend-stats">
                <span class="stat-item">
                  <el-icon><Download /></el-icon> {{ item.downloads }}
                </span>
                <span v-if="item.likes" class="stat-item">
                  <el-icon><Star /></el-icon> {{ item.likes }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </main>

      <!-- 右侧边栏 -->
      <aside class="right-sidebar">
        <!-- 组织信息 -->
        <div class="info-card">
          <div class="card-title">组织信息</div>
          <div class="card-content">
            <p>暂未属于任何组织</p>
            <el-button type="primary" plain size="small">创建/加入组织</el-button>
          </div>
        </div>

        <!-- 相关推荐 -->
        <div class="info-card">
          <div class="card-title">相关推荐</div>
          <div class="link-list">
            <div class="link-item">
              <el-icon><Document /></el-icon>
              <span>精选文章</span>
            </div>
            <div class="link-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>社区论坛</span>
            </div>
            <div class="link-item">
              <el-icon><Link /></el-icon>
              <span>GitHub</span>
            </div>
          </div>
        </div>

        <!-- 社区公告 -->
        <div class="info-card">
          <div class="card-title">
            社区公告
            <span class="more-link">查看更多</span>
          </div>
          <div class="notice-list">
            <div
              v-for="notice in notices"
              :key="notice.id"
              class="notice-item"
            >
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-date">{{ notice.date }}</div>
            </div>
          </div>
        </div>

        <!-- 品牌专区 -->
        <div class="info-card brand-card">
          <div class="brand-list">
            <div v-for="brand in brands" :key="brand.id" class="brand-item">
              <div class="brand-name">{{ brand.name }}</div>
              <div class="brand-count">{{ brand.count }}</div>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2022-2026 ModelScope.cn 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  HomeFilled,
  User,
  Collection,
  Star,
  Notebook,
  Service,
  SetUp,
  Lock,
  Document,
  ChatDotRound,
  Link,
  CircleCheck,
  ArrowRight,
  Download,
  Plus,
  Search
} from '@element-plus/icons-vue'

// 搜索关键词
const searchQuery = ref('')

// 热门搜索标签
const hotSearchTags = ['Qwen3.6', 'qwen', 'qwen3.5', 'indextts2', 'DeepSeek', 'qwen3', 'glm', 'glm5.2', 'wan2.2', 'api']

// Banner数据
const banners = ref([
  { title: 'X宝开源医疗社区黑客松', image: 'https://image.modelscope.cn/banner/710fe080-42b8-4487-9de2-4405903244a6.jpg', link: 'https://modelscope.cn/events/189' },
  { title: 'AIGC视频创作大赛', image: 'https://image.modelscope.cn/banner/73251306-909e-4d5e-9482-cf9af6977985.png', link: 'https://modelscope.cn/active/ai-video-voting' },
  { title: '智元机器人活动', image: 'https://resources.modelscope.cn/banners/55b0223d-c46e-48cc-94a2-3c1a9f5db546.jpg', link: 'https://modelscope.cn/events/265' },
  { title: '英特尔AI PC专区', image: 'https://resources.modelscope.cn/banners/e0597f44-7cef-4e6f-92cb-9949a83e81ff.jpg', link: 'https://lps.eqxiul.com/ls/MM2LS2H2' },
  { title: 'GLM-5.2 发布', image: 'https://resources.modelscope.cn/banners/f5e8d865-7eae-49d9-93c9-be8fab25573e.jpg', link: 'https://modelscope.cn/collections/ZhipuAI/GLM-52' },
  { title: 'AI学习专区', image: 'https://resources.modelscope.cn/banners/7fb1aba6-63a5-4f99-83cd-084ea7ab85a7.jpg', link: 'https://modelscope.cn/learn/3166' },
  { title: 'MiniMax-M3', image: 'https://image.modelscope.cn/banner/bc2df96e-05a3-4dbf-a517-89fc42b13194.png', link: 'https://www.modelscope.cn/collections/MiniMax/MiniMax-M3' },
  { title: 'Kimi-K2.7-Code', image: 'https://resources.modelscope.cn/banners/c7b43f8a-1980-4a8a-8e58-5a385641e71d.jpg', link: 'https://modelscope.cn/models/moonshotai/Kimi-K2.7-Code/summary' },
  { title: 'PP-OCRv6', image: 'https://resources.modelscope.cn/banners/54b180dd-2228-498a-971b-76e9a1c081f6.jpg', link: 'https://modelscope.cn/collections/PaddlePaddle/PP-OCRv6' },
  { title: 'Nemotron-3x', image: 'https://image.modelscope.cn/banner/4a1d71a3-7364-40d5-a7d4-e2b49546636f.jpeg', link: 'https://modelscope.cn/collections/nv-community/Nemotron-3x' },
])

// 内容标签
const contentTabs = [
  { key: 'all', label: '全部' },
  { key: 'mine', label: '我的内容' },
  { key: 'following', label: '我关注的内容' },
]
const activeTab = ref('all')

// 内容列表
const contentList = ref([
  {
    id: 1,
    tag: '',
    title: '智谱',
    author: 'modeljx',
    authorLink: '',
    action: '创建了模型',
    time: '5小时前',
    downloads: 0,
    link: '',
    type: 'all'
  },
  {
    id: 2,
    tag: 'text-classification',
    title: '',
    author: 'modeljx',
    authorLink: '',
    action: '创建了模型',
    time: '5小时前',
    downloads: 0,
    link: '',
    type: 'all'
  },
  {
    id: 3,
    tag: '活动',
    title: '【杭州站】智元AIMA开发者城市行活动',
    author: '',
    action: '',
    time: '21小时前',
    downloads: 0,
    description: '本站活动依托AWS CommunityDay 2026平台，为开发者带来多元领域的硬核内容，让开发者们亲身体验云端算力与具身智能在真实场景中的激情碰撞',
    organizer: '智元',
    organizerLogo: '/images/modelZoo/organizers/zhiyuan.png',
    participants: '0',
    link: '',
    type: 'all'
  },
  {
    id: 4,
    tag: '竞赛',
    title: 'GPASS AI 眼镜智能体开发者大赛',
    author: '',
    action: '',
    time: '1天前',
    downloads: 0,
    description: '蚂蚁集团联合 Rokid、雷鸟创新，面向广大开发者发起 GPASS AI 眼镜智能体开发者大赛。在「百宝箱」平台 - GPASS 智能眼镜应用模板上，用工作流从 0 搭建专属智能体，无需关心眼镜硬件底层，就能把它真正发布、运行在 AI 眼镜上 —— 让创意不止于屏幕，而是走进真实可穿戴场景。',
    organizer: 'GPASS',
    organizerLogo: '/images/modelZoo/organizers/gpass.png',
    link: '',
    type: 'all'
  },
  {
    id: 5,
    tag: '活动',
    title: 'WaytoAGI-AI切磋小会',
    author: '',
    action: '',
    time: '2天前',
    downloads: 0,
    description: '6月28日，来云谷中心参加AI切磋小会——亲子Al，AI教育专场！',
    organizer: 'WaytoAGI',
    organizerLogo: '/images/modelZoo/organizers/waytoagi.png',
    link: '',
    type: 'all'
  },
  {
    id: 6,
    tag: '竞赛',
    title: '「亲橙光合」AIGC视频创作大赛作品征集启动！',
    author: '',
    action: '',
    time: '3天前',
    downloads: 0,
    description: '本届大赛由云谷中心主办，呜哩AI、魔搭社区、阿里巴巴亲橙联合主办，并与CDSA国际媒体艺术创意大赛合作。作为亲橙光合青年艺术家计划年度项目，延续"空间内容供给+生态资源联动"理念，设"理想的附近""液态共同体"双赛道，面向AI创作者及数字艺术爱好者，征集AI视频作品。',
    organizer: '云谷中心',
    organizerLogo: '/images/modelZoo/organizers/yungu.png',
    link: '',
    type: 'all'
  },
  {
    id: 7,
    tag: '活动',
    title: 'In-Cite 智元 AGIBOT WORLD 开发者日活动',
    author: '',
    action: '',
    time: '3天前',
    downloads: 0,
    description: '以技术全栈之势，汇聚全球极客On-site，致意每一次 Insight 的闪现，生态共创，具身生长',
    organizer: '智元',
    organizerLogo: '/images/modelZoo/organizers/zhiyuan2.png',
    link: '',
    type: 'all'
  },
  {
    id: 8,
    tag: '竞赛',
    title: '泉客松 | 济南首届全域 AI 黑客松大赛',
    author: '',
    action: '',
    time: '3天前',
    downloads: 0,
    description: '济南首届全域 AI 黑客松，一座城市与 AI 的共同生长：青年创造，济南主场',
    organizer: 'AI·Spring',
    organizerLogo: '/images/modelZoo/organizers/aispring.png',
    link: '',
    type: 'all'
  },
])

const filteredContentList = computed(() => {
  return contentList.value.filter(item => item.type === 'all')
})

// 开源动向标签
const trendTabs = [
  { key: 'all', label: '全部' },
  { key: 'model', label: '模型' },
  { key: 'dataset', label: '数据集' },
  { key: 'studio', label: '创空间' },
]
const activeTrendTab = ref('all')

// 开源动向列表
const trendList = ref([
  { id: 1, name: 'GLM-5.2', org: '智谱.AI', orgLink: '', downloads: '20.7k', likes: 80, type: 'model', link: '' },
  { id: 2, name: 'Kimi-K2.7-Code', org: 'Moonshot AI', orgLink: '', downloads: '10.5k', likes: 27, type: 'model', link: '' },
  { id: 3, name: 'DeepSeek-V4-Flash', org: 'DeepSeek', orgLink: '', downloads: '552.2k', likes: 285, type: 'model', link: '' },
  { id: 4, name: 'IndexTTS2，GPU加速版', org: 'xmccln', orgLink: '', downloads: '325.9k', likes: 821, type: 'studio', cover: '/images/modelZoo/studio-covers/indextts2.jpg', link: '' },
  { id: 5, name: 'AgentWorldBench', org: '千问', orgLink: '', downloads: '116', likes: 1, type: 'dataset', link: '' },
  { id: 6, name: 'Unlimited-OCR', org: '飞桨PaddlePaddle', orgLink: '', downloads: '6.2k', likes: 48, type: 'model', link: '' },
  { id: 7, name: '零基础快速接入具身交互智能 SDK——实时交互AI数字人', org: 'Xingyun3DXmov', orgLink: '', downloads: '1.8k', likes: 1, type: 'studio', cover: '/images/modelZoo/studio-covers/avatar-sdk.png', link: '' },
  { id: 8, name: 'VoxCPM2-Demo', org: 'OpenBMB', orgLink: '', downloads: '30.9k', likes: 143, type: 'studio', cover: '/images/modelZoo/studio-covers/voxcpm2.jpg', link: '' },
  { id: 9, name: 'ScarfBench', org: 'ibm-research', orgLink: '', downloads: '5.7k', likes: 2, type: 'dataset', link: '' },
  { id: 10, name: 'Qwen-Image-Bench', org: '千问', orgLink: '', downloads: '24.6k', likes: 13, type: 'dataset', link: '' },
])

const filteredTrendList = computed(() => {
  if (activeTrendTab.value === 'all') return trendList.value
  return trendList.value.filter(item => item.type === activeTrendTab.value)
})

// 公告列表
const notices = ref([
  { id: 1, title: '关于人工智能生成合成内容标识合规的重要通知', date: '2026-05-22', link: '' },
  { id: 2, title: '关于魔搭社区模型备案信息的公示', date: '2026-05-22', link: '' },
  { id: 3, title: '硅碳 AI 诊疗挑战赛，10w奖金池等你赢！', date: '2026-06-04', link: '' },
  { id: 4, title: '魔搭共建者计划上线！一起造、一起玩、一起聊', date: '2026-05-20', link: '' },
])

// 品牌专区
const brands = ref([
  { id: 1, name: 'AMDCommunity', count: '10865', link: '' },
  { id: 2, name: 'Embodied', count: '284352', link: '' },
  { id: 3, name: 'AI_PC', count: '51408', link: '' },
  { id: 4, name: 'CodetheUnseenAIforGood', count: '1768', link: '' },
  { id: 5, name: 'EAI-100', count: '461868', link: '' },
  { id: 6, name: 'Medal_Introduction', count: '821114', link: '' },
  { id: 7, name: 'AIGC', count: '152683', link: '' },
])

// 打开链接
const openLink = (url?: string) => {
  // 内部页面跳转，不再打开外部链接
  console.log('Clicked:', url)
}

// 显示创建创空间对话框
const showCreateStudioDialog = () => {
  console.log('Create studio clicked')
}
</script>

<style scoped lang="scss">
.modelscope-page {
  min-height: 100vh;
  background: #f5f7fa;
}

// 顶部导航栏
.header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 100;

  .header-container {
    display: flex;
    align-items: center;
    max-width: 1400px;
    margin: 0 auto;
    padding: 12px 20px;
    gap: 24px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;

    img {
      width: 32px;
      height: 32px;
    }

    .logo-text {
      font-size: 20px;
      font-weight: 600;
      color: #1677ff;
    }
  }

  .search-box {
    flex: 1;
    max-width: 400px;

    .search-input {
      :deep(.el-input__wrapper) {
        border-radius: 20px;
        background: #f5f5f5;
        box-shadow: none;

        &:hover, &:focus {
          background: #fff;
          box-shadow: 0 0 0 1px #1677ff;
        }
      }
    }
  }

  .nav-menu {
    display: flex;
    gap: 24px;

    .nav-item {
      font-size: 14px;
      color: #595959;
      text-decoration: none;
      padding: 8px 0;
      position: relative;
      transition: color 0.3s;

      &:hover {
        color: #1677ff;
      }

      &.active {
        color: #1677ff;
        font-weight: 500;

        &::after {
          content: '';
          position: absolute;
          bottom: -12px;
          left: 50%;
          transform: translateX(-50%);
          width: 20px;
          height: 2px;
          background: #1677ff;
        }
      }
    }
  }

  .header-tools {
    display: flex;
    align-items: center;
    gap: 16px;

    .create-btn {
      border-radius: 20px;
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .user-avatar-small {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      overflow: hidden;
      cursor: pointer;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

// 热门搜索
.hot-search {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding: 12px 0;

  .hot-search-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .hot-label {
    font-size: 13px;
    color: #8c8c8c;
    flex-shrink: 0;
  }

  .hot-tags {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .hot-tag {
    font-size: 13px;
    color: #595959;
    cursor: pointer;
    transition: color 0.3s;

    &:hover {
      color: #1677ff;
    }
  }
}

// 主容器
.main-container {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  gap: 20px;
  padding: 20px;
}

// 左侧边栏
.left-sidebar {
  width: 260px;
  flex-shrink: 0;

  .user-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    text-align: center;
    margin-bottom: 16px;

    .user-avatar-large {
      width: 80px;
      height: 80px;
      margin: 0 auto 12px;
      border-radius: 50%;
      overflow: hidden;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .user-name {
      font-size: 18px;
      font-weight: 600;
      color: #262626;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;

      .verified-icon {
        color: #1677ff;
        font-size: 16px;
      }
    }

    .profile-btn {
      margin-bottom: 16px;
    }

    .medals-section {
      .medals-row {
        display: flex;
        justify-content: center;
        gap: 6px;
        margin-bottom: 12px;

        .medal-icon {
          width: 28px;
          height: 28px;
        }
      }

      .medal-notice {
        background: #f5f7fa;
        border-radius: 8px;
        padding: 12px;

        .notice-title {
          font-size: 14px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 4px;
        }

        .notice-desc {
          font-size: 12px;
          color: #8c8c8c;
          margin-bottom: 8px;
        }
      }
    }
  }

  .menu-list {
    background: #fff;
    border-radius: 12px;
    padding: 8px 0;

    .menu-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 20px;
      cursor: pointer;
      font-size: 14px;
      color: #595959;
      transition: all 0.3s;

      &:hover {
        color: #1677ff;
        background: #f5f7fa;
      }

      &.active {
        color: #1677ff;
        background: #e6f4ff;
      }

      .el-icon {
        font-size: 18px;
      }

      .el-tag {
        margin-left: auto;
      }
    }
  }
}

// 中间内容区
.content-area {
  flex: 1;
  min-width: 0;
}

// Banner
.banner-section {
  margin-bottom: 20px;

  .banner-item {
    height: 180px;
    border-radius: 12px;
    overflow: hidden;
    display: block;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

// 快捷操作
.quick-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;

  .action-card {
    flex: 1;
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    transition: all 0.3s;
    text-decoration: none;

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }

    .action-icon {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
      }
    }

    .action-name {
      font-size: 15px;
      font-weight: 500;
      color: #262626;
    }
  }
}

// 内容区域
.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;

  .content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .header-title {
      font-size: 16px;
      font-weight: 600;
      color: #262626;
    }

    .tabs {
      display: flex;
      gap: 20px;

      .tab-item {
        font-size: 14px;
        color: #8c8c8c;
        cursor: pointer;
        padding: 4px 0;
        transition: all 0.3s;

        &:hover {
          color: #1677ff;
        }

        &.active {
          color: #1677ff;
          font-weight: 500;
          border-bottom: 2px solid #1677ff;
        }
      }
    }
  }

  .content-list {
    .content-card {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 16px 0;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        .content-title {
          color: #1677ff;
        }
      }

      .content-left {
        flex: 1;

        .content-tag {
          display: inline-block;
          padding: 2px 8px;
          background: #e6f7ff;
          color: #1677ff;
          font-size: 12px;
          border-radius: 4px;
          margin-bottom: 6px;
        }

        .content-title {
          font-size: 15px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 6px;
          transition: color 0.3s;
        }

        .content-meta {
          font-size: 13px;
          color: #8c8c8c;
          display: flex;
          align-items: center;
          gap: 8px;

          .author {
            color: #1677ff;

            &:hover {
              text-decoration: underline;
            }
          }

          .organizer-label {
            color: #8c8c8c;
          }

          .organizer-logo {
            width: 20px;
            height: 20px;
            border-radius: 4px;
          }

          .organizer-name {
            color: #262626;
            font-weight: 500;
          }
        }

        .content-desc {
          font-size: 13px;
          color: #595959;
          margin-top: 8px;
          line-height: 1.5;
        }

        .participants {
          font-size: 13px;
          color: #8c8c8c;
          margin-top: 8px;
        }
      }

      .content-right {
        text-align: right;
        color: #8c8c8c;
        font-size: 13px;

        .time {
          display: block;
          margin-bottom: 4px;
        }

        .stats {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

// 开源动向
.trend-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #262626;
    }

    .sub-tabs {
      display: flex;
      gap: 12px;

      .sub-tab {
        font-size: 13px;
        color: #8c8c8c;
        cursor: pointer;
        padding: 4px 8px;
        border-radius: 4px;
        transition: all 0.3s;

        &:hover {
          color: #1677ff;
        }

        &.active {
          color: #1677ff;
          background: #e6f4ff;
        }
      }
    }
  }

  .trend-list {
    .trend-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        .trend-name {
          color: #1677ff;
        }
      }

      .trend-left {
        flex: 1;

        .trend-name {
          font-size: 14px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 2px;
          transition: color 0.3s;
        }

        .trend-org {
          font-size: 12px;
          color: #1677ff;

          &:hover {
            text-decoration: underline;
          }
        }
      }

      .trend-cover {
        width: 60px;
        height: 40px;
        border-radius: 4px;
        overflow: hidden;
        margin: 0 12px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .trend-stats {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #8c8c8c;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

// 右侧边栏
.right-sidebar {
  width: 300px;
  flex-shrink: 0;

  .info-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 16px;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #262626;
      margin-bottom: 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .more-link {
        font-size: 13px;
        color: #1677ff;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .card-content {
      text-align: center;
      color: #8c8c8c;
      font-size: 14px;

      p {
        margin-bottom: 12px;
      }
    }

    .link-list {
      .link-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 10px 0;
        color: #595959;
        text-decoration: none;
        font-size: 14px;
        transition: color 0.3s;

        &:hover {
          color: #1677ff;
        }

        .el-icon {
          font-size: 18px;
        }
      }
    }

    .notice-list {
      .notice-item {
        padding: 10px 0;
        border-bottom: 1px solid #f0f0f0;
        text-decoration: none;
        display: block;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          .notice-title {
            color: #1677ff;
          }
        }

        .notice-title {
          font-size: 14px;
          color: #262626;
          margin-bottom: 4px;
          transition: color 0.3s;
          line-height: 1.5;
        }

        .notice-date {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }

    &.brand-card {
      .brand-list {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;

        .brand-item {
          text-decoration: none;
          padding: 12px;
          background: #f5f7fa;
          border-radius: 8px;
          text-align: center;
          transition: all 0.3s;

          &:hover {
            background: #e6f4ff;
          }

          .brand-name {
            font-size: 13px;
            color: #595959;
            margin-bottom: 4px;
          }

          .brand-count {
            font-size: 14px;
            font-weight: 600;
            color: #1677ff;
          }
        }
      }
    }
  }
}

// 页脚
.footer {
  background: #fff;
  border-top: 1px solid #e8e8e8;
  padding: 20px 0;
  margin-top: 40px;

  .footer-content {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
    text-align: center;

    p {
      font-size: 13px;
      color: #8c8c8c;
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }

  .left-sidebar,
  .right-sidebar {
    width: 100%;
  }
}
</style>
