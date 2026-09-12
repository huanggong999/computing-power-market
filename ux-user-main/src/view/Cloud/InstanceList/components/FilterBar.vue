<template>
  <div class="filter-bar">
    <el-row :gutter="16">
      <!-- GPU类型筛选 -->
      <el-col :span="4">
        <el-select
          v-model="filters.gpuTypes"
          multiple
          collapse-tags
          placeholder="GPU类型"
          clearable
          @change="emitSearch"
        >
          <el-option
            v-for="type in gpuTypes"
            :key="type"
            :label="type"
            :value="type"
          />
        </el-select>
      </el-col>

      <!-- 区域筛选 -->
      <el-col :span="4">
        <el-select
          v-model="filters.regions"
          multiple
          collapse-tags
          placeholder="区域"
          clearable
          @change="emitSearch"
        >
          <el-option
            v-for="region in regions"
            :key="region.code"
            :label="region.name"
            :value="region.code"
          />
        </el-select>
      </el-col>

      <!-- 状态筛选 -->
      <el-col :span="4">
        <el-select
          v-model="selectedStatus"
          placeholder="状态"
          clearable
          @change="emitSearch"
        >
          <el-option
            v-for="(item, key) in StatusMap"
            :key="key"
            :label="item.label"
            :value="key"
          >
            <el-tag :type="item.type as any" size="small">{{ item.label }}</el-tag>
          </el-option>
        </el-select>
      </el-col>

      <!-- 关键词搜索 -->
      <el-col :span="6">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索实例名称/ID"
          clearable
          @keyup.enter="emitSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-col>

      <!-- 操作按钮 -->
      <el-col :span="6" class="filter-actions">
        <el-button type="primary" @click="emitSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="emitReset">
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { StatusMap } from '@/types/instance'
import type { InstanceFilters, InstanceStatus } from '@/types/instance'

interface Props {
  modelValue: InstanceFilters
  gpuTypes: string[]
  regions: { code: string; name: string }[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [val: InstanceFilters]
  search: []
  reset: []
}>()

const filters = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const selectedStatus = computed<InstanceStatus | ''>({
  get: () => filters.value.statuses?.[0] || '',
  set: (val) => {
    filters.value.statuses = val ? [val] : []
  }
})

const emitSearch = () => {
  emit('search')
}

const emitReset = () => {
  emit('reset')
}
</script>

<style scoped lang="scss">
.filter-bar {
  background: #fff;
  padding: 12px;
  border: 1px solid #d9e0ea;
  border-bottom: 0;
  border-radius: 2px 2px 0 0;

  :deep(.el-select),
  :deep(.el-input) {
    font-size: 12px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    min-height: 30px;
    border-radius: 2px;
  }

  :deep(.el-select__tags) {
    font-size: 12px;
  }

  :deep(.el-tag) {
    font-size: 12px;
  }

  .filter-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;

    :deep(.el-button) {
      height: 30px;
      border-radius: 2px;
      font-size: 12px;
      padding: 6px 12px;
    }
  }
}
</style>
