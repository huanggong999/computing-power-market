<template>
  <MainLayout>
    <div class="market-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">算力市场</h1>
          <p class="page-desc">海量GPU资源，按需租用，秒级启动</p>
        </div>

        <!-- 排序选项 -->
        <div class="sort-options">
          <span class="sort-label">排序:</span>
          <a-radio-group v-model:value="sortBy" size="small">
            <a-radio-button value="price">价格</a-radio-button>
            <a-radio-button value="vram">显存</a-radio-button>
            <a-radio-button value="model">型号</a-radio-button>
          </a-radio-group>
          <a-button
            type="text"
            size="small"
            class="sort-order-btn"
            @click="toggleSortOrder"
          >
            <ArrowUpOutlined v-if="sortOrder === 'asc'" />
            <ArrowDownOutlined v-else />
          </a-button>
        </div>
      </div>

      <!-- 主体内容 -->
      <div class="main-content">
        <!-- 左侧筛选面板 -->
        <aside class="filter-sidebar">
          <FilterPanel
            v-model:filters="filters"
            @change="handleFilterChange"
            @reset="handleFilterReset"
          />
        </aside>

        <!-- 右侧GPU列表 -->
        <div class="gpu-list-container">
          <!-- 结果统计 -->
          <div class="results-header">
            <span class="results-count">
              共 <strong>{{ filteredTotal }}</strong> 个GPU实例
            </span>
            <span class="results-desc">符合条件的结果</span>
          </div>

          <!-- GPU卡片列表 -->
          <div class="gpu-list">
            <GPUCard
              v-for="gpu in paginatedList"
              :key="gpu.id"
              :gpu="gpu"
              @rent="handleRent"
              @manage="handleManage"
            />
          </div>

          <!-- 空状态 -->
          <div v-if="paginatedList.length === 0" class="empty-state">
            <InboxOutlined class="empty-icon" />
            <p>暂无符合条件的GPU实例</p>
            <a-button type="primary" @click="handleFilterReset">清除筛选条件</a-button>
          </div>

          <!-- 分页 -->
          <div v-if="filteredTotal > pageSize" class="pagination-wrapper">
            <a-pagination
              v-model:current="currentPage"
              v-model:pageSize="pageSize"
              :total="filteredTotal"
              :page-size-options="['12', '24', '48']"
              show-size-changer
              show-quick-jumper
              @change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 租用弹窗 -->
    <RentModal
      v-model:visible="rentModalVisible"
      :gpu="selectedGPU"
      @success="handleRentSuccess"
    />
    <!-- 调试: Modal状态 {{ rentModalVisible }} -->
  </MainLayout>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useGPUStore } from '@/stores/gpu'
import MainLayout from '@/components/layout/MainLayout.vue'
import FilterPanel from '@/components/market/FilterPanel.vue'
import GPUCard from '@/components/market/GPUCard.vue'
import RentModal from '@/components/market/RentModal.vue'
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  InboxOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const gpuStore = useGPUStore()

// 筛选条件
const filters = ref({
  gpuModel: [],
  vram: [],
  cpuCores: [],
  memory: [],
  disk: [],
  region: [],
  priceRange: [0, 20],
  status: 'all',
})

// 排序
const sortBy = ref('price')
const sortOrder = ref('asc')

// 分页
const currentPage = ref(1)
const pageSize = ref(12)

// 弹窗控制
const rentModalVisible = ref(false)
const selectedGPU = ref(null)

// 计算属性
const filteredList = computed(() => {
  let result = [...gpuStore.gpuList]

  // 应用筛选逻辑
  if (filters.value.gpuModel.length > 0) {
    result = result.filter(item =>
      filters.value.gpuModel.some(model =>
        item.model.toLowerCase().includes(model.toLowerCase())
      )
    )
  }

  if (filters.value.vram.length > 0) {
    result = result.filter(item => filters.value.vram.includes(item.vram))
  }

  if (filters.value.status !== 'all') {
    result = result.filter(item => item.status === filters.value.status)
  }

  // 价格筛选
  result = result.filter(item =>
    item.price >= filters.value.priceRange[0] &&
    item.price <= filters.value.priceRange[1]
  )

  // 排序
  result.sort((a, b) => {
    let compare = 0
    if (sortBy.value === 'price') compare = a.price - b.price
    else if (sortBy.value === 'vram') compare = parseInt(a.vram) - parseInt(b.vram)
    else if (sortBy.value === 'model') compare = a.model.localeCompare(b.model)
    return sortOrder.value === 'asc' ? compare : -compare
  })

  return result
})

const filteredTotal = computed(() => filteredList.value.length)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

// 方法
function handleFilterChange() {
  currentPage.value = 1
}

function handleFilterReset() {
  filters.value = {
    gpuModel: [],
    vram: [],
    cpuCores: [],
    memory: [],
    disk: [],
    region: [],
    priceRange: [0, 20],
    status: 'all',
  }
  currentPage.value = 1
}

function toggleSortOrder() {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

function handlePageChange(page, size) {
  currentPage.value = page
  pageSize.value = size
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handleRent(gpu) {
  selectedGPU.value = gpu
  rentModalVisible.value = true
}

function handleManage(gpu) {
  router.push('/console')
}

function handleRentSuccess() {
  rentModalVisible.value = false
  // 路由跳转已在 RentModal 中处理
}

// 处理URL参数中的租用请求
onMounted(() => {
  if (route.query.rent) {
    const gpu = gpuStore.getGPUById(route.query.rent)
    if (gpu && gpu.status === 'available') {
      selectedGPU.value = gpu
      rentModalVisible.value = true
    }
  }
})
</script>

<style scoped>
.market-page {
  padding: 40px 24px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-desc {
  font-size: 15px;
  color: var(--text-secondary);
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.sort-order-btn {
  padding: 0 8px;
  color: var(--text-secondary);
}

.sort-order-btn:hover {
  color: var(--primary-color);
}

/* Main Content */
.main-content {
  display: flex;
  gap: 24px;
}

.filter-sidebar {
  width: 280px;
  flex-shrink: 0;
  position: sticky;
  top: 84px;
  height: fit-content;
}

.gpu-list-container {
  flex: 1;
  min-width: 0;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: var(--bg-card);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.results-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.results-count strong {
  color: var(--primary-color);
  font-weight: 600;
}

.results-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

.gpu-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px dashed var(--border-color);
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

/* Pagination */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .filter-sidebar {
    display: none;
  }

  .gpu-list {
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .market-page {
    padding: 20px 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .sort-options {
    width: 100%;
    justify-content: flex-start;
  }

  .gpu-list {
    gap: 12px;
  }

  .results-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>
