import { ref, computed, onMounted } from 'vue'
import { useInstanceStore } from '@/store/modules/useInstance'
import type { Instance, InstanceFilters, InstanceListParams } from '@/types/instance'

export function useInstanceList() {
  const instanceStore = useInstanceStore()

  // State
  const filters = ref<InstanceFilters>({})
  const selectedInstances = ref<Instance[]>([])
  const pagination = ref({
    currentPage: 1,
    pageSize: 10
  })
  const sortBy = ref('')
  const sortOrder = ref<'asc' | 'desc' | ''>('')

  // Computed
  const instanceList = computed(() => instanceStore.instanceList)
  const loading = computed(() => instanceStore.loading)
  const total = computed(() => instanceStore.total)
  const gpuTypes = computed(() => instanceStore.gpuTypes)
  const regions = computed(() => instanceStore.regions)

  const instanceStats = computed(() => {
    const list = instanceList.value
    return {
      total: list.length,
      running: list.filter(i => i.status === 'running').length,
      stopped: list.filter(i => i.status === 'stopped').length,
      other: list.filter(i => !['running', 'stopped'].includes(i.status)).length
    }
  })

  const paginationWithTotal = computed(() => ({
    currentPage: pagination.value.currentPage,
    pageSize: pagination.value.pageSize,
    total: total.value
  }))

  // Methods
  const fetchList = async () => {
    const params: InstanceListParams = {
      page: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      filters: filters.value,
      ...(sortBy.value ? { sortBy: sortBy.value, sortOrder: sortOrder.value as 'asc' | 'desc' } : {})
    }
    await instanceStore.fetchInstanceList(params)
  }

  const handleSearch = () => {
    pagination.value.currentPage = 1
    fetchList()
  }

  const handleReset = () => {
    filters.value = {}
    pagination.value.currentPage = 1
    fetchList()
  }

  const handlePageChange = (page: number) => {
    pagination.value.currentPage = page
    fetchList()
  }

  const handleSizeChange = (size: number) => {
    pagination.value.pageSize = size
    pagination.value.currentPage = 1
    fetchList()
  }

  const handleSortChange = ({ prop, order }: { prop?: string; order?: 'ascending' | 'descending' | null }) => {
    sortBy.value = prop || ''
    sortOrder.value = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
    fetchList()
  }

  // Auto-fetch on mount
  onMounted(() => {
    fetchList()
    instanceStore.fetchGpuTypes()
    instanceStore.fetchRegions()
  })

  return {
    filters,
    selectedInstances,
    pagination,
    paginationWithTotal,
    sortBy,
    sortOrder,
    instanceList,
    loading,
    total,
    gpuTypes,
    regions,
    instanceStats,
    fetchList,
    handlePageChange,
    handleSizeChange,
    handleSortChange,
    handleSearch,
    handleReset
  }
}
