<template>
  <div class="gpu-card-autodl">
    <!-- 卡片顶部信息栏 -->
    <div class="card-top-bar">
      <div class="top-bar-left">
        <span class="location-item">{{ gpu.region || '--' }}</span>
        <span class="divider">/</span>
        <span class="location-item">{{ gpu.zone || gpu.zoneCode || '--' }}</span>
        <span class="divider">/</span>
        <span class="machine-id">{{ gpu.machineId }}</span>
        <span class="uuid">{{ gpu.machineUuid }}</span>
        <span class="rentable">可租用至：{{ gpu.rentableUntil || '--' }}</span>
      </div>
      <div class="top-bar-right">
        <span class="cache-tag" v-if="gpu.cacheOptimized">
          <el-icon><Check /></el-icon>
          缓存优化
        </span>
      </div>
    </div>

    <!-- 标题行 -->
    <div class="card-title-row">
      <div class="title-left">
        <h3 class="gpu-title">{{ gpu.model }} / {{ gpu.vram }}</h3>
        <span class="availability">空闲/总量</span>
        <span class="avail-num">{{ gpu.availableCount }}</span>
        <span class="avail-divider">/</span>
        <span class="total-num">{{ gpu.totalCount }}</span>
      </div>
    </div>

    <!-- 三栏信息布局 -->
    <div class="card-body">
      <div class="info-col">
        <div class="col-title">每GPU分配</div>
        <div class="col-content">
          <div class="info-row">
            <span class="info-label">CPU:</span>
            <span class="info-value">{{ gpu.cpuCores }} 核，{{ gpu.cpuModel }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">内存:</span>
            <span class="info-value">{{ gpu.memory }}</span>
          </div>
        </div>
      </div>

      <div class="info-col">
        <div class="col-title">硬盘</div>
        <div class="col-content">
          <div class="info-row">
            <span class="info-label">系统盘:</span>
            <span class="info-value">{{ gpu.systemDisk }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">数据盘:</span>
            <span class="info-value">{{ gpu.dataDisk }}</span>
            <span class="expandable" v-if="gpu.expandable && gpu.expandable !== '0 GB'">，可扩容 {{ gpu.expandable }}</span>
          </div>
        </div>
      </div>

      <div class="info-col">
        <div class="col-title">其它</div>
        <div class="col-content">
          <div class="info-row" v-if="!hasGpuRuntime">
            <span class="info-value">CPU 实例</span>
          </div>
          <div class="info-row" v-if="hasGpuDriver">
            <span class="info-label">GPU驱动:</span>
            <span class="info-value">{{ gpu.gpuDriver }}</span>
          </div>
          <div class="info-row" v-if="hasCudaVersion">
            <span class="info-label">CUDA版本:</span>
            <span class="info-value">{{ gpu.cudaVersion }}</span>
            <el-tooltip
              effect="dark"
              placement="top"
              trigger="click"
              content="CUDA 是 GPU 计算运行环境版本，请选择与镜像、深度学习框架兼容的版本。"
            >
              <el-icon class="help-icon" @click.stop><QuestionFilled /></el-icon>
            </el-tooltip>
          </div>
        </div>
      </div>

      <!-- 右侧价格区 -->
      <div class="price-col">
        <div class="price-main">
          <span class="price-symbol">¥</span>
          <span class="price-num">{{ gpu.price || '--' }}</span>
          <span class="price-unit">/{{ billingUnit }}</span>
        </div>
        <div class="price-discount" v-if="gpu.discountPrice && gpu.discountPrice !== '0' && gpu.discountPrice !== '0.00'">
          会员最低享{{ gpu.discountRate }}折 ¥{{ gpu.discountPrice }}/{{ billingUnit }}
        </div>
        <el-button
          type="primary"
          class="rent-btn"
          :disabled="gpu.rentableCount <= 0"
          @click="handleRent"
        >
          {{ gpu.rentableCount > 0 ? `${gpu.rentableCount}卡可租` : '已租完' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Check, QuestionFilled } from '@element-plus/icons-vue'
import type { GpuMarketItem } from '@/api/gpuMarket'

const props = defineProps<{
  gpu: GpuMarketItem
  billingType: string
}>()

const emit = defineEmits(['rent'])

const billingUnit = computed(() => {
  const units: Record<string, string> = {
    hourly: '时',
    daily: '日',
    weekly: '周',
    monthly: '月',
  }
  return units[props.billingType] || '时'
})

const hasGpuDriver = computed(() => Boolean(props.gpu.gpuDriver && props.gpu.gpuDriver !== 'N/A'))
const hasCudaVersion = computed(() => Boolean(props.gpu.cudaVersion && props.gpu.cudaVersion !== 'N/A'))
const hasGpuRuntime = computed(() => hasGpuDriver.value || hasCudaVersion.value)

function handleRent() {
  if (props.gpu.rentableCount <= 0) return
  emit('rent', props.gpu)
}
</script>

<style scoped lang="scss">
.gpu-card-autodl {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
}

/* 顶部信息栏 */
.card-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.top-bar-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #909399;
}

.location-item {
  color: #303133;
  font-weight: 500;
}

.divider {
  color: #c0c4cc;
}

.machine-id {
  color: #606266;
}

.uuid {
  color: #c0c4cc;
  font-size: 14px;
}

.rentable {
  color: #909399;
  font-size: 14px;
}

.top-bar-right {
  display: flex;
  align-items: center;
}

.cache-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #f56c6c;
  font-size: 14px;
}

/* 标题行 */
.card-title-row {
  padding: 16px 24px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-left {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.gpu-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  margin-right: 24px;
}

.availability {
  font-size: 15px;
  color: #606266;
}

.avail-num {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.avail-divider {
  font-size: 15px;
  color: #909399;
}

.total-num {
  font-size: 15px;
  color: #909399;
}

/* 三栏布局 */
.card-body {
  display: flex;
  padding: 16px 24px 24px;
  gap: 40px;
}

.info-col {
  flex: 1;
  min-width: 0;
}

.col-title {
  font-size: 15px;
  color: #909399;
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-label {
  color: #606266;
  white-space: nowrap;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.expandable {
  color: #409eff;
  font-size: 15px;
}

.help-icon {
  font-size: 14px;
  color: #c0c4cc;
  cursor: pointer;

  &:hover {
    color: #909399;
  }
}

/* 价格区 */
.price-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  min-width: 180px;
  gap: 8px;
}

.price-main {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-symbol {
  font-size: 20px;
  color: #f56c6c;
  font-weight: 600;
}

.price-num {
  font-size: 32px;
  color: #f56c6c;
  font-weight: 700;
}

.price-unit {
  font-size: 16px;
  color: #606266;
}

.price-discount {
  font-size: 14px;
  color: #909399;
}

.rent-btn {
  min-width: 100px;
  height: 40px;
  font-size: 16px;
  border-radius: 4px;
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .card-body {
    flex-direction: column;
    gap: 20px;
  }

  .price-col {
    align-items: flex-start;
    border-top: 1px solid #ebeef5;
    padding-top: 16px;
  }
}
</style>
