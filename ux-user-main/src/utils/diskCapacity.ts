export const MIN_DISK_CAPACITY_GB = 100
export const MIN_DISK_CAPACITY_MESSAGE = '磁盘资源最低需要 100G'

export function totalDiskCapacityGb(
  systemDiskGb: unknown,
  dataDiskGb: unknown,
  extraDataDiskGb: unknown = 0,
): number {
  return [systemDiskGb, dataDiskGb, extraDataDiskGb].reduce<number>((total, value) => {
    const capacity = Number(value)
    return total + (Number.isFinite(capacity) && capacity > 0 ? capacity : 0)
  }, 0)
}

export function hasMinimumDiskCapacity(
  systemDiskGb: unknown,
  dataDiskGb: unknown,
  extraDataDiskGb: unknown = 0,
): boolean {
  return totalDiskCapacityGb(systemDiskGb, dataDiskGb, extraDataDiskGb) >= MIN_DISK_CAPACITY_GB
}
