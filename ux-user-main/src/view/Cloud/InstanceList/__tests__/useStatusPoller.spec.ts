import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

vi.useFakeTimers()

vi.mock('@/store/modules/useInstance', () => ({
  useInstanceStore: vi.fn(() => ({
    fetchInstanceDetail: vi.fn()
  }))
}))

import { useStatusPoller } from '../composables/useStatusPoller'

describe('useStatusPoller', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.clearAllTimers()
  })

  it('should expose isPolling, startPolling, stopPolling, stopAllPolling', () => {
    const poller = useStatusPoller()

    expect(poller.isPolling).toBeDefined()
    expect(poller.startPolling).toBeDefined()
    expect(poller.stopPolling).toBeDefined()
    expect(poller.stopAllPolling).toBeDefined()
  })

  it('should not expose pollingIds directly', () => {
    const poller = useStatusPoller() as any

    expect(poller.pollingIds).toBeUndefined()
  })
})
