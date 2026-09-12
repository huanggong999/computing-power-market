import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

vi.mock('@/store/modules/useInstance', () => ({
  useInstanceStore: vi.fn(() => ({
    startInstance: vi.fn(),
    stopInstance: vi.fn(),
    restartInstance: vi.fn(),
    releaseInstance: vi.fn(),
    batchOperate: vi.fn()
  }))
}))

vi.mock('../composables/useStatusPoller', () => ({
  useStatusPoller: vi.fn(() => ({
    startPolling: vi.fn()
  }))
}))

import { useInstanceOperation } from '../composables/useInstanceOperation'

describe('useInstanceOperation', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should expose executeOperation and executeBatchOperation', () => {
    const { executeOperation, executeBatchOperation } = useInstanceOperation()

    expect(executeOperation).toBeDefined()
    expect(executeBatchOperation).toBeDefined()
  })

  it('should track operatingIds', () => {
    const { operatingIds } = useInstanceOperation()

    expect(operatingIds.value).toBeInstanceOf(Set)
  })
})
