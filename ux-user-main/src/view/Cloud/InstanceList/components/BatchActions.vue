<template>
  <div v-if="selected.length > 0" class="batch-actions">
    <div class="batch-info">
      <span class="selected-count">已选择 <strong>{{ selected.length }}</strong> 个实例</span>
      <el-button type="text" @click="clearSelection">清空选择</el-button>
    </div>

    <div class="batch-buttons">
      <el-button
        type="success"
        :disabled="!canStart"
        @click="handleBatchStart"
      >
        <el-icon><VideoPlay /></el-icon>
        批量开机
      </el-button>

      <el-button
        type="danger"
        :disabled="!canStop"
        @click="handleBatchStop"
      >
        <el-icon><VideoPause /></el-icon>
        批量关机
      </el-button>

      <el-button
        type="warning"
        :disabled="!canRestart"
        @click="handleBatchRestart"
      >
        <el-icon><Refresh /></el-icon>
        批量重启
      </el-button>

      <el-button
        type="primary"
        :disabled="selected.length === 0"
        @click="handleBatchRenew"
      >
        批量续费
      </el-button>

      <el-button
        type="info"
        :disabled="selected.length === 0"
        @click="handleBatchRelease"
      >
        <el-icon><Delete /></el-icon>
        批量释放
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { VideoPlay, VideoPause, Refresh, Delete } from '@element-plus/icons-vue'
import type { Instance } from '@/types/instance'

interface Props {
  selected: Instance[]
  instances: Instance[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:selected': [selected: Instance[]]
  batchStart: [ids: string[]]
  batchStop: [ids: string[]]
  batchRestart: [ids: string[]]
  batchRelease: [ids: string[]]
  batchRenew: [instances: Instance[]]
}>()

// 能否开机（已关机状态的实例）
const canStart = computed(() => {
  return props.selected.some(item => item.status === 'stopped')
})

// 能否关机（运行中状态的实例）
const canStop = computed(() => {
  return props.selected.some(item => item.status === 'running')
})

// 能否重启（运行中或已关机状态的实例）
const canRestart = computed(() => {
  return props.selected.some(
    item => item.status === 'running' || item.status === 'stopped'
  )
})

// 清空选择
const clearSelection = () => {
  emit('update:selected', [])
}

// 批量开机
const handleBatchStart = () => {
  const items = props.selected.filter(item => item.status === 'stopped')
  const ids = items.map(item => item.id)
  if (ids.length > 0) {
    emit('batchStart', ids)
  }
}

// 批量关机
const handleBatchStop = () => {
  const items = props.selected.filter(item => item.status === 'running')
  const ids = items.map(item => item.id)
  if (ids.length > 0) {
    emit('batchStop', ids)
  }
}

// 批量重启
const handleBatchRestart = () => {
  const items = props.selected.filter(
    item => item.status === 'running' || item.status === 'stopped'
  )
  const ids = items.map(item => item.id)
  if (ids.length > 0) {
    emit('batchRestart', ids)
  }
}

// 批量续费
const handleBatchRenew = () => {
  emit('batchRenew', props.selected)
}

// 批量释放
const handleBatchRelease = () => {
  const ids = props.selected.map(item => item.id)
  if (ids.length > 0) {
    ElMessageBox.confirm(
      `确定要释放选中的 ${ids.length} 个实例吗？释放后数据将不可恢复！`,
      '确认释放',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
      .then(() => {
        emit('batchRelease', ids)
      })
      .catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.batch-actions {
  background: #fff;
  border-radius: 8px;
  padding: 13px 16px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .batch-info {
    display: flex;
    align-items: center;
    gap: 16px;

    .selected-count {
      font-size: 15px;
      color: #606266;

      strong {
        color: #409eff;
        font-size: 19px;
      }
    }
  }

  .batch-buttons {
    display: flex;
    gap: 11px;

    .el-button {
      display: flex;
      align-items: center;
      gap: 5px;
      font-size: 15px;
      padding: 8px 13px;
    }
  }
}
</style>
