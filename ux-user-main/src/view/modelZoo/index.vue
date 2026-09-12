<template>
  <div class="model-zoo-page">
    <div class="page-container">
      <!-- 左侧边栏 -->
      <aside class="sidebar">
        <div class="user-card">
          <div class="user-avatar">
            <img src="/images/modelZoo/avatars/default.png" alt="avatar" />
          </div>
          <div class="user-name">
            modeljx
            <el-icon class="verified-icon"><CircleCheck /></el-icon>
          </div>
          <el-button type="primary" plain size="small" class="profile-btn">
            个人主页 <el-icon><ArrowRight /></el-icon>
          </el-button>

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

        <nav class="menu-list">
          <div class="menu-item" @click="goToModelScope">
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

      <!-- 主内容区 -->
      <main class="main-content">
        <!-- Banner 轮播 -->
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
            <div class="action-icon"><img src="/images/modelZoo/avatars/default.png" /></div>
            <span class="action-name">创建模型</span>
          </div>
          <div class="action-card">
            <div class="action-icon"><img src="/images/modelZoo/avatars/default.png" /></div>
            <span class="action-name">创建数据集</span>
          </div>
          <div class="action-card">
            <div class="action-icon"><img src="/images/modelZoo/avatars/default.png" /></div>
            <span class="action-name">创建创空间</span>
          </div>
        </div>

        <!-- 内容流 -->
        <div class="content-section">
          <div class="content-header">
            <div class="header-title">相关推荐</div>
            <div class="tabs">
              <span class="tab-item active">全部</span>
              <span class="tab-item">我的内容</span>
              <span class="tab-item">我关注的内容</span>
            </div>
          </div>

          <div class="content-list">
            <div v-for="item in contentList" :key="item.id" class="content-card">
              <div class="content-left">
                <div class="content-tag">{{ item.tag }}</div>
                <div class="content-title">{{ item.title }}</div>
                <div class="content-meta">
                  <span class="author">{{ item.author }}</span>
                  <span class="action">{{ item.action }}</span>
                </div>
              </div>
              <div class="content-right">
                <span class="time">{{ item.time }}></span>
                <span class="stats"><el-icon><Download /></el-icon> {{ item.downloads }}</span>
              </div>
            </div>
          </div>
        </div>
      </main>

      <!-- 右侧信息栏 -->
      <aside class="right-sidebar">
        <div class="info-card">
          <div class="card-title">组织信息</div>
          <div class="card-content">
            <p>暂未属于任何组织</p>
            <el-button type="primary" plain size="small">创建/加入组织</el-button>
          </div>
        </div>

        <div class="info-card">
          <div class="card-title">相关推荐</div>
          <div class="link-list">
            <a href="#" class="link-item">
              <el-icon><Document /></el-icon>
              <span>精选文章</span>
            </a>
            <a href="#" class="link-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>社区论坛</span>
            </a>
            <a href="#" class="link-item">
              <el-icon><Link /></el-icon>
              <span>GitHub</span>
            </a>
          </div>
        </div>

        <div class="info-card">
          <div class="card-title">社区公告</div>
          <el-link type="primary" class="more-link">查看更多</el-link>
          <div class="notice-list">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-date">{{ notice.date }}</div>
            </div>
          </div>
        </div>

        <div class="info-card">
          <div class="card-title">社区开源动向</div>
          <div class="sub-tabs">
            <span class="sub-tab active">全部</span>
            <span class="sub-tab">模型</span>
            <span class="sub-tab">数据集</span>
            <span class="sub-tab">创空间</span>
          </div>
          <div class="trend-list">
            <div v-for="item in trendList" :key="item.id" class="trend-item">
              <div class="trend-name">{{ item.name }}</div>
              <div class="trend-org">@{{ item.org }}</div>
              <div class="trend-stats">
                <span>{{ item.downloads }}</span>
                <span>{{ item.likes }}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
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
  Download
} from '@element-plus/icons-vue'

const router = useRouter()

// 跳转到魔搭社区页面
const goToModelScope = () => {
  router.push('/modelscope')
}

// Banner 数据
const banners = ref([
  { title: 'X宝开源医疗社区黑客松', image: 'https://image.modelscope.cn/banner/710fe080-42b8-4487-9de2-4405903244a6.jpg' },
  { title: 'AIGC视频创作大赛', image: 'https://image.modelscope.cn/banner/73251306-909e-4d5e-9482-cf9af6977985.png' },
  { title: 'GLM-5.2 发布', image: 'https://resources.modelscope.cn/banners/f5e8d865-7eae-49d9-93c9-be8fab25573e.jpg' },
  { title: '英特尔AI PC专区', image: 'https://image.modelscope.cn/banner/267a1708-adb0-4a0e-b324-14543b87944a.jpg' },
  { title: 'MiniMax-M3', image: 'https://image.modelscope.cn/banner/bc2df96e-05a3-4dbf-a517-89fc42b13194.png' },
])

// 内容列表
const contentList = ref([
  {
    id: 1, tag: '', title: '智谱',
    author: 'modeljx', action: '创建了模型',
    time: '1小时前', downloads: 0
  },
  {
    id: 2, tag: 'text-classification', title: '',
    author: 'modeljx', action: '创建了模型',
    time: '1小时前', downloads: 0
  },
  {
    id: 3, tag: '活动', title: '【杭州站】智元AIMA开发者城市行活动',
    author: '', action: '',
    time: '17小时前', downloads: 0
  },
  {
    id: 4, tag: '竞赛', title: 'GPASS AI 眼镜智能体开发者大赛',
    author: '', action: '',
    time: '1天前', downloads: 0
  },
  {
    id: 5, tag: '活动', title: 'WaytoAGI-AI切磋小会',
    author: '', action: '',
    time: '2天前', downloads: 0
  },
])

// 公告列表
const notices = ref([
  { id: 1, title: '关于人工智能生成合成内容标识合规的重要通知', date: '2026-05-22' },
  { id: 2, title: '关于魔搭社区模型备案信息的公示', date: '2026-05-22' },
  { id: 3, title: '硅碳AI诊疗挑战赛，10w奖金池等你赢！', date: '2026-06-04' },
  { id: 4, title: '魔搭共建者计划上线！一起造、一起玩、一起聊', date: '2026-05-20' }
])

// 开源动向
const trendList = ref([
  { id: 1, name: 'GLM-5.2', org: '智谱.AI', downloads: '20.3k', likes: 80 },
  { id: 2, name: 'Kimi-K2.7-Code', org: 'Moonshot AI', downloads: '10.3k', likes: 27 },
  { id: 3, name: 'DeepSeek-V4-Flash', org: 'DeepSeek', downloads: '552.1k', likes: 285 },
  { id: 4, name: 'AgentWorldBench', org: '千问', downloads: '115', likes: 1 },
  { id: 5, name: 'Unlimited-OCR', org: '飞桨PaddlePaddle', downloads: '4.8k', likes: 46 },
])
</script>

<style scoped lang="scss">
.model-zoo-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-top: 100px;

  .page-container {
    display: flex;
    max-width: 1400px;
    margin: 0 auto;
    gap: 20px;
    padding: 0 20px;
  }
}

// 左侧边栏
.sidebar {
  width: 240px;
  flex-shrink: 0;

  .user-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    text-align: center;
    margin-bottom: 16px;

    .user-avatar {
      width: 72px;
      height: 72px;
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
      color: #303133;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;

      .verified-icon {
        color: #3972fd;
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
          color: #303133;
          margin-bottom: 4px;
        }

        .notice-desc {
          font-size: 12px;
          color: #909399;
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
      color: #606266;
      transition: all 0.3s;

      &:hover {
        color: #3972fd;
        background: #f5f7fa;
      }

      &.active {
        color: #3972fd;
        background: #eef2ff;
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

// 主内容区
.main-content {
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

    &:hover {
      box-shadow: 0 4px 16px rgba(0,0,0,0.08);
    }

    .action-icon {
      width: 36px;
      height: 36px;
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
      color: #303133;
    }
  }
}

// 内容区域
.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;

  .content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .header-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .tabs {
      display: flex;
      gap: 20px;

      .tab-item {
        font-size: 14px;
        color: #606266;
        cursor: pointer;
        padding: 4px 0;
        transition: all 0.3s;

        &:hover {
          color: #3972fd;
        }

        &.active {
          color: #3972fd;
          font-weight: 500;
          border-bottom: 2px solid #3972fd;
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

      &:last-child {
        border-bottom: none;
      }

      .content-left {
        flex: 1;

        .content-tag {
          display: inline-block;
          padding: 2px 8px;
          background: #e6f7ff;
          color: #1890ff;
          font-size: 12px;
          border-radius: 4px;
          margin-bottom: 6px;
        }

        .content-title {
          font-size: 15px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 6px;
        }

        .content-meta {
          font-size: 13px;
          color: #606266;

          .author {
            color: #3972fd;
            margin-right: 8px;
          }
        }
      }

      .content-right {
        text-align: right;
        color: #909399;
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

// 右侧边栏
.right-sidebar {
  width: 280px;
  flex-shrink: 0;

  .info-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 16px;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
    }

    .card-content {
      text-align: center;
      color: #909399;
      font-size: 14px;

      p {
        margin-bottom: 12px;
      }
    }

    .more-link {
      float: right;
      margin-top: -36px;
      font-size: 13px;
    }

    .link-list {
      .link-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 10px 0;
        color: #606266;
        text-decoration: none;
        font-size: 14px;
        transition: color 0.3s;

        &:hover {
          color: #3972fd;
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
        cursor: pointer;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          .notice-title {
            color: #3972fd;
          }
        }

        .notice-title {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
          transition: color 0.3s;
          line-height: 1.5;
        }

        .notice-date {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .sub-tabs {
      display: flex;
      gap: 12px;
      margin-bottom: 12px;

      .sub-tab {
        font-size: 13px;
        color: #606266;
        cursor: pointer;
        padding: 4px 8px;
        border-radius: 4px;
        transition: all 0.3s;

        &:hover {
          color: #3972fd;
        }

        &.active {
          color: #3972fd;
          background: #eef2ff;
        }
      }
    }

    .trend-list {
      .trend-item {
        padding: 10px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .trend-name {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 2px;
        }

        .trend-org {
          font-size: 12px;
          color: #3972fd;
          margin-bottom: 4px;
        }

        .trend-stats {
          font-size: 12px;
          color: #909399;
          display: flex;
          gap: 12px;
        }
      }
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .model-zoo-page {
    .page-container {
      flex-direction: column;
    }
  }

  .sidebar,
  .right-sidebar {
    width: 100%;
  }
}
</style>
