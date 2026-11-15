<template>
  <div class="gpu-card-horizontal" :class="{ 'out-of-stock': gpu.status === 'outofstock' }">
    <!-- 上半部分 -->
    <div class="card-top">
      <div class="left-info">
        <!-- 标题行 -->
        <div class="title-row">
          <h3 class="gpu-title">{{ gpu.model }} / {{ gpu.vram }}</h3>
          <div class="tag-list">
            <span
              v-for="tag in gpu.tags"
              :key="tag"
              class="tag"
              :class="getTagClass(tag)"
            >
              <FireOutlined v-if="tag === '热门'" />
              <ThunderboltOutlined v-else-if="tag === '特价'" />
              {{ tag }}
            </span>
          </div>
        </div>

        <!-- 可租赁数量 -->
        <div class="available-text">
          可租赁 {{ gpu.availableCards }} 卡
        </div>

        <!-- 规格信息 -->
        <div class="specs-row">
          <div class="spec-item">
            <span class="spec-label">CPU:</span>
            <span class="spec-value">{{ gpu.cpuDetail || gpu.cpuCores }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">内存:</span>
            <span class="spec-value">{{ gpu.memory }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">硬盘:</span>
            <span class="spec-value">系统盘: {{ gpu.systemDisk }} 数据盘: {{ gpu.dataDisk }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">机房:</span>
            <span class="spec-value room-link">{{ gpu.room }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧价格区 -->
      <div class="right-price">
        <div class="price-main">¥{{ currentPrice.toFixed(2) }}</div>
        <div class="price-original">¥{{ (gpu.originalPrice * selectedCount).toFixed(2) }}</div>
        <div class="price-unit">元/小时</div>
        <a-button
          v-if="gpu.status === 'outofstock'"
          disabled
          class="rent-btn"
        >
          缺货
        </a-button>
        <a-button
          v-else-if="gpu.status === 'running'"
          class="rent-btn"
          @click="$emit('manage', gpu)"
        >
          管理
        </a-button>
        <a-button
          v-else
          type="primary"
          class="rent-btn"
          @click="handleRent"
        >
          租用
        </a-button>
      </div>
    </div>

    <!-- 分割线 -->
    <div class="card-divider" />

    <!-- 下半部分 -->
    <div class="card-bottom">
      <div class="gpu-config">
        <span class="config-label">GPU配置:</span>
        <a-radio-group
          v-model:value="selectedCount"
          size="small"
          class="config-radio-group"
        >
          <a-radio-button
            v-for="n in gpu.gpuCount"
            :key="n"
            :value="n"
            :disabled="n > gpu.availableCards"
          >
            {{ n }}卡
          </a-radio-button>
        </a-radio-group>
      </div>

      <div class="total-price">
        <span class="total-label">总价:</span>
        <span class="total-value">¥{{ currentPrice.toFixed(2) }}</span>
        <span class="total-unit">/小时</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { FireOutlined, ThunderboltOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  gpu: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['rent', 'manage'])

const selectedCount = ref(1)

const currentPrice = computed(() => {
  return props.gpu.price * selectedCount.value
})

function getTagClass(tag) {
  if (tag === '热门') return 'tag-hot'
  if (tag === '特价') return 'tag-sale'
  return 'tag-normal'
}

function handleRent() {
  emit('rent', { ...props.gpu, selectedCount: selectedCount.value })
}
</script>

<style scoped>
.gpu-card-horizontal {
  background: var(--bg-card);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  padding: 24px;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.gpu-card-horizontal:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  border-color: rgba(22, 119, 255, 0.3);
}

.gpu-card-horizontal.out-of-stock {
  opacity: 0.7;
}

/* 上半部分 */
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.left-info {
  flex: 1;
  min-width: 0;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.gpu-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.tag-list {
  display: flex;
  gap: 8px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tag-hot {
  background: rgba(255, 77, 79, 0.15);
  color: #ff4d4f;
}

.tag-sale {
  background: rgba(250, 173, 20, 0.15);
  color: #faad14;
}

.tag-normal {
  background: rgba(22, 119, 255, 0.15);
  color: var(--primary-color);
}

.available-text {
  font-size: 14px;
  color: var(--primary-color);
  margin-bottom: 16px;
}

.specs-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 40px;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.spec-label {
  color: var(--text-secondary);
  white-space: nowrap;
}

.spec-value {
  color: var(--text-primary);
}

.room-link {
  color: var(--primary-color);
  cursor: pointer;
}

/* 右侧价格 */
.right-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 140px;
  gap: 4px;
}

.price-main {
  font-size: 28px;
  font-weight: 700;
  color: #ff4d4f;
  line-height: 1.2;
}

.price-original {
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: line-through;
}

.price-unit {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.rent-btn {
  min-width: 100px;
  height: 40px;
  font-size: 15px;
  border-radius: 8px;
}

/* 分割线 */
.card-divider {
  height: 1px;
  background: var(--border-color);
  margin: 20px 0;
}

/* 下半部分 */
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.gpu-config {
  display: flex;
  align-items: center;
  gap: 12px;
}

.config-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.config-radio-group :deep(.ant-radio-button-wrapper) {
  background: transparent;
  border-color: var(--border-color);
  color: var(--text-secondary);
}

.config-radio-group :deep(.ant-radio-button-wrapper-checked) {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.config-radio-group :deep(.ant-radio-button-wrapper:not(:first-child)::before) {
  background-color: var(--border-color);
}

.total-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.total-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.total-value {
  font-size: 20px;
  font-weight: 700;
  color: #ff4d4f;
}

.total-unit {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 响应式 */
@media (max-width: 768px) {
  .card-top {
    flex-direction: column;
  }

  .right-price {
    align-items: flex-start;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px 16px;
    width: 100%;
  }

  .specs-row {
    grid-template-columns: 1fr;
  }

  .card-bottom {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
