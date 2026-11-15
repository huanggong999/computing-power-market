import type { CpuPriceSpec, CpuPurchaseRequest } from '@/types/instance'

export function cpuDisplayPrice(spec: CpuPriceSpec | undefined, billingMode: CpuPurchaseRequest['billingMode']): number | null {
  const monthly = billingMode !== 'on_demand'
  const price = monthly ? spec?.priceMonthly : spec?.price
  const sale = monthly ? spec?.salePriceMonthly : spec?.salePrice
  const upstream = price && price.available === true && !price.pricePending && price.currency === 'CNY'
    && Number.isFinite(Number(price.unitPrice)) && Number(price.unitPrice) > 0
    ? Number(price.unitPrice) : null
  // The enabled platform price is the customer-facing price. Keep showing it
  // even while the upstream catalog is temporarily pending; the server still
  // rechecks the upstream price before accepting an order.
  if (sale != null && Number.isFinite(Number(sale)) && Number(sale) > 0 && (upstream === null || Number(sale) >= upstream)) {
    return Number(sale)
  }
  return upstream
}

export function cpuPricingSnapshot(spec: CpuPriceSpec, form: CpuPurchaseRequest): NonNullable<CpuPurchaseRequest['pricing']> {
  const monthly = form.billingMode !== 'on_demand'
  const upstreamPrice = Number((monthly ? spec.priceMonthly : spec.price)?.unitPrice)
  return {
    upstreamPrice: Number.isFinite(upstreamPrice) ? upstreamPrice : undefined,
    salePrice: cpuDisplayPrice(spec, form.billingMode) ?? undefined,
    currency: 'CNY', unit: monthly ? 'MONTH' : 'HOUR', billingType: form.billingMode,
    quantity: form.count, duration: monthly ? form.duration : 1,
    priceVersion: `${(monthly ? spec.monthlyPriceVersion : spec.hourlyPriceVersion) || 'default'}:${spec.priceVersion || ''}`,
  }
}
