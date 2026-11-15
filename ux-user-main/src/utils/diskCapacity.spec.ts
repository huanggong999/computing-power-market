import { describe, expect, it } from 'vitest'
import { MIN_DISK_CAPACITY_GB, hasMinimumDiskCapacity, totalDiskCapacityGb } from './diskCapacity'

describe('disk capacity', () => {
  it('sums system, data and expanded data disks', () => {
    expect(totalDiskCapacityGb(30, 50)).toBe(80)
    expect(totalDiskCapacityGb(30, 50, 20)).toBe(100)
    expect(totalDiskCapacityGb('30', null, 70)).toBe(100)
  })

  it('requires exactly at least 100G', () => {
    expect(MIN_DISK_CAPACITY_GB).toBe(100)
    expect(hasMinimumDiskCapacity(30, 50, 20)).toBe(true)
    expect(hasMinimumDiskCapacity(100, 1)).toBe(true)
    expect(hasMinimumDiskCapacity(50, 49)).toBe(false)
  })
})
