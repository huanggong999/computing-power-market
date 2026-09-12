import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Mock the store before importing the composable
vi.mock('@/store/modules/useInstance', () => ({
  useInstanceStore: vi.fn(() => ({
    instanceList: [],
    loading: false,
    total: 0,
    gpuTypes: ['RTX 4090', 'A100'],
    regions: [{ code: 'bj', name: '北京' }],
    fetchInstanceList: vi.fn(),
    fetchGpuTypes: vi.fn(),
    fetchRegions: vi.fn()
  }))
}))

import { useInstanceList } from '../composables/useInstanceList'

describe('useInstanceList', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should initialize with default state', () => {
    const { filters, pagination, selectedInstances } = useInstanceList()

    expect(filters.value).toEqual({})
    expect(pagination.value).toEqual({ currentPage: 1, pageSize: 10 })
    expect(selectedInstances.value).toEqual([])
  })

  it('should reset pagination on search', () => {
    const { pagination, handleSearch } = useInstanceList()

    pagination.value.currentPage = 5
    handleSearch()

    expect(pagination.value.currentPage).toBe(1)
  })

  it('should reset filters and pagination on reset', () => {
    const { filters, pagination, handleReset } = useInstanceList()

    filters.value = { keyword: 'test' }
    pagination.value.currentPage = 3
    handleReset()

    expect(filters.value).toEqual({})
    expect(pagination.value.currentPage).toBe(1)
  })

  it('should calculate instance stats correctly', () => {
    const { instanceStats } = useInstanceList()

    expect(instanceStats.value).toEqual({
      total: 0,
      running: 0,
      stopped: 0,
      other: 0
    })
  })
})
