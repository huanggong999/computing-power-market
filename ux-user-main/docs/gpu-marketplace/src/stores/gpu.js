import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'

// 模拟GPU数据
const mockGPUData = [
  {
    id: 'gpu-001',
    model: 'RTX 4090',
    vram: '24 GB',
    cpu: 'AMD EPYC 7763',
    cpuCores: '64核',
    cpuDetail: '16核(算上超线程32核)',
    memory: '125 GB',
    disk: '2TB SSD',
    systemDisk: '50GB',
    dataDisk: '2TB',
    diskType: 'NVMe',
    bandwidth: '10Gbps',
    network: 'BGP多线',
    region: '北京一区',
    room: '高性能 - 国内机房A4',
    price: 8.5,
    originalPrice: 10.0,
    unit: '元/小时',
    status: 'available',
    tags: ['热门'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 4,
    description: '顶级消费级显卡，适合深度学习训练',
  },
  {
    id: 'gpu-002',
    model: 'RTX 4090',
    vram: '24 GB',
    cpu: 'AMD EPYC 7763',
    cpuCores: '64核',
    cpuDetail: '16核(算上超线程32核)',
    memory: '125 GB',
    disk: '2TB SSD',
    systemDisk: '50GB',
    dataDisk: '2TB',
    diskType: 'NVMe',
    bandwidth: '10Gbps',
    network: 'BGP多线',
    region: '上海一区',
    room: '标准 - 国内机房B2',
    price: 8.5,
    originalPrice: 10.0,
    unit: '元/小时',
    status: 'available',
    tags: ['热门'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 4,
    description: '顶级消费级显卡，适合深度学习训练',
  },
  {
    id: 'gpu-003',
    model: 'RTX 4090',
    vram: '24 GB',
    cpu: 'AMD EPYC 7763',
    cpuCores: '64核',
    cpuDetail: '16核(算上超线程32核)',
    memory: '125 GB',
    disk: '2TB SSD',
    systemDisk: '50GB',
    dataDisk: '2TB',
    diskType: 'NVMe',
    bandwidth: '10Gbps',
    network: 'BGP多线',
    region: '深圳一区',
    room: '高速 - 上海机房',
    price: 8.8,
    originalPrice: 9.2,
    unit: '元/小时',
    status: 'available',
    tags: ['特价'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 2,
    description: '顶级消费级显卡，适合深度学习训练',
  },
  {
    id: 'gpu-004',
    model: 'RTX 3090',
    vram: '24GB',
    cpu: 'Intel Xeon Gold 6348',
    cpuCores: '28核',
    cpuDetail: '14核(算上超线程28核)',
    memory: '128GB',
    disk: '1TB SSD',
    systemDisk: '50GB',
    dataDisk: '1TB',
    diskType: 'NVMe',
    bandwidth: '5Gbps',
    network: 'BGP多线',
    region: '上海一区',
    room: '标准 - 国内机房A1',
    price: 4.78,
    originalPrice: 5.5,
    unit: '元/小时',
    status: 'available',
    tags: ['高性价比'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 4,
    description: '大显存训练卡，性价比之选',
  },
  {
    id: 'gpu-005',
    model: 'A100 80GB',
    vram: '80GB',
    cpu: 'AMD EPYC 7763',
    cpuCores: '64核',
    cpuDetail: '32核(算上超线程64核)',
    memory: '512GB',
    disk: '4TB SSD',
    systemDisk: '50GB',
    dataDisk: '4TB',
    diskType: 'NVMe',
    bandwidth: '25Gbps',
    network: 'BGP多线',
    region: '深圳一区',
    room: '高性能 - 国内机房A4',
    price: 15.8,
    originalPrice: 18.0,
    unit: '元/小时',
    status: 'available',
    tags: ['专业级', '大显存'],
    image: 'Ubuntu 22.04',
    gpuCount: 8,
    availableCards: 8,
    description: 'NVIDIA数据中心级GPU，最强算力',
  },
  {
    id: 'gpu-006',
    model: 'A100 40GB',
    vram: '40GB',
    cpu: 'AMD EPYC 7713',
    cpuCores: '64核',
    cpuDetail: '32核(算上超线程64核)',
    memory: '256GB',
    disk: '2TB SSD',
    systemDisk: '50GB',
    dataDisk: '2TB',
    diskType: 'NVMe',
    bandwidth: '25Gbps',
    network: 'BGP多线',
    region: '北京一区',
    room: '标准 - 国内机房B2',
    price: 10.2,
    originalPrice: 12.0,
    unit: '元/小时',
    status: 'running',
    tags: ['专业级'],
    image: 'Ubuntu 22.04',
    gpuCount: 8,
    availableCards: 0,
    description: 'NVIDIA数据中心级GPU',
  },
  {
    id: 'gpu-007',
    model: 'A10',
    vram: '24GB',
    cpu: 'Intel Xeon Silver 4314',
    cpuCores: '32核',
    cpuDetail: '16核(算上超线程32核)',
    memory: '128GB',
    disk: '1TB SSD',
    systemDisk: '50GB',
    dataDisk: '1TB',
    diskType: 'NVMe',
    bandwidth: '5Gbps',
    network: 'BGP多线',
    region: '上海二区',
    room: '标准 - 国内机房A1',
    price: 3.5,
    originalPrice: 4.0,
    unit: '元/小时',
    status: 'available',
    tags: ['推理优选'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 4,
    description: '适合推理部署，显存大功耗低',
  },
  {
    id: 'gpu-008',
    model: 'RTX 4080',
    vram: '16GB',
    cpu: 'AMD EPYC 7302',
    cpuCores: '32核',
    cpuDetail: '16核(算上超线程32核)',
    memory: '64GB',
    disk: '500GB SSD',
    systemDisk: '50GB',
    dataDisk: '500GB',
    diskType: 'NVMe',
    bandwidth: '1Gbps',
    network: 'BGP多线',
    region: '广州一区',
    room: '标准 - 国内机房B1',
    price: 2.8,
    originalPrice: 3.2,
    unit: '元/小时',
    status: 'available',
    tags: ['入门级'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 4,
    description: '中高端消费级显卡',
  },
  {
    id: 'gpu-009',
    model: 'RTX 3080',
    vram: '10GB',
    cpu: 'Intel Xeon Silver 4214',
    cpuCores: '24核',
    cpuDetail: '12核(算上超线程24核)',
    memory: '64GB',
    disk: '500GB SSD',
    systemDisk: '50GB',
    dataDisk: '500GB',
    diskType: 'NVMe',
    bandwidth: '1Gbps',
    network: 'BGP多线',
    region: '北京二区',
    room: '标准 - 国内机房A2',
    price: 1.8,
    originalPrice: 2.0,
    unit: '元/小时',
    status: 'outofstock',
    tags: ['缺货'],
    image: 'Ubuntu 22.04',
    gpuCount: 4,
    availableCards: 0,
    description: '上一代高端卡，性能够用',
  },
  {
    id: 'gpu-010',
    model: 'V100 32GB',
    vram: '32GB',
    cpu: 'Intel Xeon Platinum 8280',
    cpuCores: '28核',
    cpuDetail: '14核(算上超线程28核)',
    memory: '192GB',
    disk: '1TB SSD',
    systemDisk: '50GB',
    dataDisk: '1TB',
    diskType: 'NVMe',
    bandwidth: '10Gbps',
    network: 'BGP多线',
    region: '深圳一区',
    room: '高性能 - 国内机房A4',
    price: 6.5,
    originalPrice: 7.5,
    unit: '元/小时',
    status: 'available',
    tags: ['经典训练卡'],
    image: 'Ubuntu 22.04',
    gpuCount: 8,
    availableCards: 8,
    description: '上一代数据中心训练卡',
  },
  {
    id: 'gpu-011',
    model: 'H100 80GB',
    vram: '80GB',
    cpu: 'AMD EPYC 9654',
    cpuCores: '96核',
    cpuDetail: '48核(算上超线程96核)',
    memory: '1TB',
    disk: '8TB SSD',
    systemDisk: '50GB',
    dataDisk: '8TB',
    diskType: 'NVMe',
    bandwidth: '100Gbps',
    network: 'BGP多线',
    region: '北京一区',
    room: '旗舰 - 国内机房S1',
    price: 25.8,
    originalPrice: 30.0,
    unit: '元/小时',
    status: 'available',
    tags: ['旗舰级', '最新'],
    image: 'Ubuntu 22.04',
    gpuCount: 8,
    availableCards: 8,
    description: 'NVIDIA最新旗舰数据中心GPU',
  },
  {
    id: 'gpu-012',
    model: 'RTX 4090',
    vram: '24 GB',
    cpu: 'AMD EPYC 7763',
    cpuCores: '64核',
    cpuDetail: '16核(算上超线程32核)',
    memory: '125 GB',
    disk: '2TB SSD',
    systemDisk: '50GB',
    dataDisk: '2TB',
    diskType: 'NVMe',
    bandwidth: '10Gbps',
    network: 'BGP多线',
    region: '上海一区',
    room: '高性能 - 国内机房A4',
    price: 2.5,
    originalPrice: 3.0,
    unit: '元/小时',
    status: 'running',
    tags: ['高性能'],
    image: 'Ubuntu 22.04',
    gpuCount: 8,
    availableCards: 0,
    description: '顶级消费级显卡，适合深度学习训练',
  },
]

export const useGPUStore = defineStore('gpu', () => {
  // 状态
  const gpuList = ref([...mockGPUData])
  const loading = ref(false)
  const currentPage = ref(1)
  const pageSize = ref(12)
  const total = ref(mockGPUData.length)

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

  // 获取器
  const filteredGPUList = computed(() => {
    let result = [...gpuList.value]

    // 筛选GPU型号
    if (filters.value.gpuModel.length > 0) {
      result = result.filter(item => filters.value.gpuModel.some(model =>
        item.model.toLowerCase().includes(model.toLowerCase())
      ))
    }

    // 筛选显存
    if (filters.value.vram.length > 0) {
      result = result.filter(item => filters.value.vram.includes(item.vram))
    }

    // 筛选CPU核心数
    if (filters.value.cpuCores.length > 0) {
      result = result.filter(item => {
        const cores = parseInt(item.cpuCores)
        return filters.value.cpuCores.some(range => {
          if (range === '32') return cores <= 32
          if (range === '64') return cores > 32 && cores <= 64
          if (range === '96') return cores > 64
          return false
        })
      })
    }

    // 筛选内存
    if (filters.value.memory.length > 0) {
      result = result.filter(item => {
        const mem = parseInt(item.memory)
        return filters.value.memory.some(range => {
          if (range === '64') return mem <= 64
          if (range === '128') return mem > 64 && mem <= 128
          if (range === '256') return mem > 128 && mem <= 256
          if (range === '512') return mem > 256
          return false
        })
      })
    }

    // 筛选区域
    if (filters.value.region.length > 0) {
      result = result.filter(item => filters.value.region.some(region =>
        item.region.includes(region)
      ))
    }

    // 筛选价格区间
    result = result.filter(item =>
      item.price >= filters.value.priceRange[0] &&
      item.price <= filters.value.priceRange[1]
    )

    // 筛选状态
    if (filters.value.status !== 'all') {
      result = result.filter(item => item.status === filters.value.status)
    }

    // 排序
    result.sort((a, b) => {
      let compareResult = 0
      if (sortBy.value === 'price') {
        compareResult = a.price - b.price
      } else if (sortBy.value === 'vram') {
        compareResult = parseInt(a.vram) - parseInt(b.vram)
      } else if (sortBy.value === 'model') {
        compareResult = a.model.localeCompare(b.model)
      }
      return sortOrder.value === 'asc' ? compareResult : -compareResult
    })

    return result
  })

  const paginatedList = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filteredGPUList.value.slice(start, end)
  })

  const runningMachines = computed(() => {
    return gpuList.value.filter(item => item.status === 'running')
  })

  // 方法
  function setFilters(newFilters) {
    filters.value = { ...filters.value, ...newFilters }
    currentPage.value = 1
  }

  function resetFilters() {
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

  function setSort(by, order) {
    sortBy.value = by
    sortOrder.value = order
  }

  function setPage(page) {
    currentPage.value = page
  }

  function setPageSize(size) {
    pageSize.value = size
    currentPage.value = 1
  }

  async function rentGPU(gpuId, duration) {
    loading.value = true
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))

      const gpu = gpuList.value.find(item => item.id === gpuId)
      if (gpu) {
        gpu.status = 'running'
        message.success(`成功租用 ${gpu.model} GPU`)
        return { success: true, orderId: `ORD${Date.now()}` }
      }
      return { success: false, error: 'GPU不存在' }
    } catch (error) {
      message.error('租用失败，请重试')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  async function stopGPU(gpuId) {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 800))

      const gpu = gpuList.value.find(item => item.id === gpuId)
      if (gpu) {
        gpu.status = 'available'
        message.success('已停止GPU实例')
        return { success: true }
      }
      return { success: false, error: 'GPU不存在' }
    } catch (error) {
      message.error('操作失败')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  async function destroyGPU(gpuId) {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 600))

      const gpu = gpuList.value.find(item => item.id === gpuId)
      if (gpu) {
        gpu.status = 'available'
        message.success('已销毁GPU实例')
        return { success: true }
      }
      return { success: false, error: 'GPU不存在' }
    } catch (error) {
      message.error('操作失败')
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  function getGPUById(id) {
    return gpuList.value.find(item => item.id === id)
  }

  return {
    gpuList,
    loading,
    currentPage,
    pageSize,
    total,
    filters,
    sortBy,
    sortOrder,
    filteredGPUList,
    paginatedList,
    runningMachines,
    setFilters,
    resetFilters,
    setSort,
    setPage,
    setPageSize,
    rentGPU,
    stopGPU,
    destroyGPU,
    getGPUById,
  }
})
