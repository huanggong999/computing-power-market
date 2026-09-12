<template>
  <div class="status-tag">
    <el-tag
      :type="statusInfo.type as any"
      :size="size"
      :effect="'light'"
      :style="{ backgroundColor: statusInfo.color + '20', borderColor: statusInfo.color, color: statusInfo.color }"
    >
      <span v-if="showDot" class="status-dot" :style="{ backgroundColor: statusInfo.color }"></span>
      {{ showText ? statusInfo.label : '' }}
    </el-tag>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { StatusMap, InstanceStatus } from '@/types/instance'

interface Props {
  status: InstanceStatus
  showText?: boolean
  showDot?: boolean
  size?: 'default' | 'small' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  showText: true,
  showDot: true,
  size: 'default'
})

const statusInfo = computed(() => {
  return StatusMap[props.status] || { label: '未知', type: 'info', color: '#909399' }
})
</script>

<style scoped lang="scss">
.status-tag {
  display: inline-block;

  :deep(.el-tag) {
    font-size: 14px;
    padding: 4px 10px;
    height: auto;
    line-height: 1.5;
  }

  .status-dot {
    display: inline-block;
    width: 7px;
    height: 7px;
    border-radius: 50%;
    margin-right: 4px;
  }
}
</style>
