/** GPU billing labels used by the purchase and instance pages. */
export const gpuBillingLabelMap: Record<string, string> = {
  on_demand: '按量计费',
  hourly: '按小时',
  daily: '按天',
  weekly: '按周',
  monthly: '按月'
}

export function getGpuBillingLabel(value?: string | null): string {
  if (!value) return '-'
  return gpuBillingLabelMap[value] || value
}

export function isGpuPayAsYouGo(value?: string | null): boolean {
  return value === 'on_demand' || value === 'hourly'
}
