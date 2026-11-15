import { describe, expect, it, vi } from 'vitest'

const mocks = vi.hoisted(() => ({ get: vi.fn(() => Promise.resolve({ data: {} })), post: vi.fn(() => Promise.resolve({ data: {} })), del: vi.fn(() => Promise.resolve({ data: {} })) }))
vi.mock('@/utils/request', () => ({ default: mocks }))

import { changeBandwidth, getCpuInstanceTypes, getCpuInstances, getCpuZones, getResizeOptions, purchaseCpu } from './instanceV2'

describe('v2.1 instance APIs', () => {
  it('uses portal adapter paths and payloads', async () => {
    await getResizeOptions('gpu-1')
    expect(mocks.get).toHaveBeenCalledWith('/pc/instance-resource/instances/gpu-1/resize/options')
    await changeBandwidth('gpu-1', { bandwidthMbps: 20 })
    expect(mocks.post).toHaveBeenCalledWith('/pc/instance-resource/instances/gpu-1/bandwidth', { bandwidthMbps: 20 })
    await getCpuInstanceTypes('cn-beijing')
    expect(mocks.get).toHaveBeenCalledWith('/pc/cpu-instance/instance-types', { params: { region: 'cn-beijing' } })
    await getCpuZones('cn-beijing')
    expect(mocks.get).toHaveBeenCalledWith('/pc/cpu-instance/zones', { params: { region: 'cn-beijing', scene: 'cpu' }, suppressErrorMessage: true })
    await getCpuInstances()
    expect(mocks.get).toHaveBeenCalledWith('/pc/cpu-instance/instances', { params: { includeReleased: false }, suppressErrorMessage: true })
    await purchaseCpu({ region: 'cn-beijing', zone: 'a', instanceTypeId: 'cpu.large', billingMode: 'monthly' })
    expect(mocks.post).toHaveBeenCalledWith('/pc/cpu-instance/instances', expect.objectContaining({ billingMode: 'monthly' }), expect.objectContaining({ timeout: 30000 }))
  })

  it('preserves HTTP 200 business failures from the adapter', async () => {
    mocks.get.mockResolvedValueOnce({ data: { success: false, error: { code: 'INSTANCE_NOT_FOUND' } } })
    const result = await getResizeOptions('missing-gpu')
    expect(result.data.success).toBe(false)
    expect(result.data.error.code).toBe('INSTANCE_NOT_FOUND')

    mocks.post.mockResolvedValueOnce({ data: { success: false, error: { code: 'BANDWIDTH_UNAVAILABLE' } } })
    const bandwidth = await changeBandwidth('gpu-1', { bandwidthMbps: 20 })
    expect(bandwidth.data.success).toBe(false)
    expect(bandwidth.data.error.code).toBe('BANDWIDTH_UNAVAILABLE')
  })

  it('propagates adapter HTTP 404 and 403 errors to callers', async () => {
    const notFound = Object.assign(new Error('Not Found'), { response: { status: 404 } })
    mocks.get.mockRejectedValueOnce(notFound)
    await expect(getCpuInstanceTypes('missing-region')).rejects.toBe(notFound)

    const forbidden = Object.assign(new Error('Forbidden'), { response: { status: 403 } })
    mocks.post.mockRejectedValueOnce(forbidden)
    await expect(purchaseCpu({ region: 'cn-beijing', zone: 'a', instanceTypeId: 'cpu.large', billingMode: 'monthly' })).rejects.toBe(forbidden)
  })
})

describe('CPU independent price catalog', () => {
  it('requests prices separately from CPU instance types', async () => {
    const { getCpuPrices } = await import('./instanceV2')
    await getCpuPrices('cn-beijing', 'ecs.c3a.large', 'yearly')
    expect(mocks.get).toHaveBeenCalledWith('/pc/cpu-instance/prices', { params: { region: 'cn-beijing', instanceTypeId: 'ecs.c3a.large', billingMode: 'yearly' } })
  })
})
