import { describe, expect, it } from 'vitest'
import { DATA_DISK_CAPACITY_MESSAGE, normalizeCpuPurchaseResult, normalizeCpuStatus, validateCpuPurchase } from './cpuPurchase'
import { MIN_DISK_CAPACITY_MESSAGE } from './diskCapacity'

const regions = [{ region: 'cn-beijing', zones: ['a', 'b'] }]
const instanceTypes = [{ instanceTypeId: 'cpu.large', cpuCores: 4, memGib: 8 }]

const valid = { region: 'cn-beijing', zone: 'a', instanceTypeId: 'cpu.large', billingMode: 'on_demand' as const, systemDiskGb: 100, dataDiskGb: 0, count: 1, duration: 1 }

describe('CPU purchase validation', () => {
  it('rejects missing selections and invalid zone/catalog', () => {
    expect(validateCpuPurchase({}, { regions, instanceTypes })).toMatchObject({ region: '请选择地域', zone: '请选择可用区', instanceTypeId: '请选择纯 CPU 规格' })
    expect(validateCpuPurchase({ ...valid, zone: 'x', instanceTypeId: 'other' }, { regions, instanceTypes })).toMatchObject({ zone: expect.stringContaining('不属于'), instanceTypeId: expect.stringContaining('当前地域') })
  })

  it('accepts no data disk or at least 20G and rejects 1-19G before submission', () => {
    expect(validateCpuPurchase(valid, { regions, instanceTypes })).toEqual({})
    expect(validateCpuPurchase({ ...valid, dataDiskGb: 20 }, { regions, instanceTypes })).toEqual({})
    expect(validateCpuPurchase({ ...valid, systemDiskGb: 2048, dataDiskGb: 8192, count: 10 }, { regions, instanceTypes })).toEqual({})
    expect(validateCpuPurchase({ ...valid, dataDiskGb: 1 }, { regions, instanceTypes })).toMatchObject({ dataDiskGb: DATA_DISK_CAPACITY_MESSAGE })
    expect(validateCpuPurchase({ ...valid, dataDiskGb: 19 }, { regions, instanceTypes })).toMatchObject({ dataDiskGb: DATA_DISK_CAPACITY_MESSAGE })
  })

  it('requires at least 100G total disk capacity', () => {
    expect(validateCpuPurchase({ ...valid, systemDiskGb: 20, dataDiskGb: 80 }, { regions, instanceTypes })).toEqual({})
    expect(validateCpuPurchase({ ...valid, systemDiskGb: 20, dataDiskGb: 79 }, { regions, instanceTypes })).toMatchObject({ diskCapacity: MIN_DISK_CAPACITY_MESSAGE })
  })

  it('requires prepaid duration but ignores it for on demand', () => {
    expect(validateCpuPurchase({ ...valid, billingMode: 'monthly', duration: undefined }, { regions, instanceTypes })).toHaveProperty('duration')
    expect(validateCpuPurchase({ ...valid, duration: undefined }, { regions, instanceTypes })).toEqual({})
  })
})

describe('CPU status normalization', () => {
  it('maps provisioning and terminal statuses', () => {
    expect(normalizeCpuStatus('pending')).toBe('creating')
    expect(normalizeCpuStatus('running')).toBe('running')
    expect(normalizeCpuStatus('stopped')).toBe('stopped')
    expect(normalizeCpuStatus('failed')).toBe('failed')
    expect(normalizeCpuStatus('unknown', '创建失败')).toBe('error')
  })

  it('keeps accepted records creating and preserves business failures', () => {
    const accepted = normalizeCpuPurchaseResult({ success: true, instances: [{ instance_id: 'cpu-1', status: 'pending' }] })
    expect(accepted.accepted).toBe(true)
    expect(accepted.instances?.[0].status).toBe('creating')
    const failed = normalizeCpuPurchaseResult({ success: false, message: '资源不足', checks: [{ name: 'quota', ok: false, message: '配额不足' }] })
    expect(failed.accepted).toBe(false)
    expect(failed.statusMessage).toBe('资源不足')
    expect(failed.checks?.[0].message).toBe('配额不足')
  })
})
