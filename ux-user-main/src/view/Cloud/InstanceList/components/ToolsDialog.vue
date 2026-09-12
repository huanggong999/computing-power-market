<template>
  <el-dialog v-model="visible" title="快捷工具" width="500px" :close-on-click-modal="false" @open="loadTools">
    <div v-loading="loading" class="tools-dialog">
      <el-empty v-if="tools.length === 0 && !loading" description="暂无快捷工具" />
      <div v-else class="tool-list">
        <el-button
          v-for="tool in tools"
          :key="tool.url || tool.name"
          type="primary"
          plain
          @click="openTool(tool.url)"
        >
          {{ tool.name || '快捷工具' }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { Instance } from '@/types/instance'
import { useInstanceStore } from '@/store/modules/useInstance'

interface ToolInfo {
  name?: string
  url?: string
  icon?: string
}

const props = defineProps<{
  modelValue: boolean
  instance: Instance | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const instanceStore = useInstanceStore()
const loading = ref(false)
const tools = ref<ToolInfo[]>([])

const loadTools = async () => {
  if (!props.instance) return
  loading.value = true
  try {
    const res = await instanceStore.getInstanceTools(props.instance.id)
    tools.value = res.data?.tools || []
  } catch (error) {
    tools.value = []
    ElMessage.error('获取快捷工具失败')
  } finally {
    loading.value = false
  }
}

const openTool = (url?: string) => {
  if (!url) return
  window.open(url, '_blank')
}
</script>

<style scoped lang="scss">
.tools-dialog {
  min-height: 120px;

  .tool-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
}
</style>
