import { describe, it, expect } from 'vitest'
import { cpuDisplayPrice, cpuPricingSnapshot } from './cpuPricing'
import type { CpuPriceSpec, CpuPurchaseRequest } from '@/types/instance'
const spec: CpuPriceSpec = { instanceTypeId: 'ecs.c3a.large', cpuCores: 2, memGib: 4, price: { available: true, currency: 'CNY', unitPrice: 0.28 }, priceMonthly: { available: true, currency: 'CNY', unitPrice: 128 }, salePrice: 0.5, salePriceMonthly: 200, priceVersion: 'catalog-1', monthlyPriceVersion: 'sale-1' }
describe('CPU 价格显示', () => {
  it('uses the platform price for the selected mode and the monthly price for yearly', () => {
    expect(cpuDisplayPrice(spec, 'on_demand')).toBe(0.5)
    expect(cpuDisplayPrice(spec, 'monthly')).toBe(200)
    expect(cpuDisplayPrice(spec, 'yearly')).toBe(200)
  })
  it('falls back to upstream when the platform configuration is absent', () => {
    expect(cpuDisplayPrice({ ...spec, salePrice: undefined }, 'on_demand')).toBe(0.28)
    expect(cpuDisplayPrice({ ...spec, salePriceMonthly: null }, 'monthly')).toBe(128)
  })
  it('prefers a configured platform price while upstream is unavailable', () => {
    expect(cpuDisplayPrice(undefined, 'monthly')).toBeNull()
    expect(cpuDisplayPrice({ ...spec, price: null }, 'on_demand')).toBe(0.5)
    expect(cpuDisplayPrice({ ...spec, price: { ...spec.price, pricePending: true } }, 'on_demand')).toBe(0.5)
    expect(cpuDisplayPrice({ ...spec, price: { ...spec.price, available: false } }, 'on_demand')).toBe(0.5)
    expect(cpuDisplayPrice({ ...spec, salePrice: undefined, price: null }, 'on_demand')).toBeNull()
    expect(cpuDisplayPrice({ ...spec, priceMonthly: { ...spec.priceMonthly, unitPrice: 0 }, salePriceMonthly: undefined }, 'yearly')).toBeNull()
  })
  it('does not display an under-cost or invalid platform price', () => {
    expect(cpuDisplayPrice({ ...spec, salePrice: 0.1 }, 'on_demand')).toBe(0.28)
    expect(cpuDisplayPrice({ ...spec, salePriceMonthly: NaN }, 'monthly')).toBe(128)
  })
  it('preserves dual monthly unit prices in the yearly purchase snapshot without a client total', () => {
    const form: CpuPurchaseRequest = { region: 'cn-a', zone: 'a', instanceTypeId: spec.instanceTypeId, billingMode: 'yearly', duration: 2, count: 3 }
    expect(cpuPricingSnapshot(spec, form)).toEqual({ upstreamPrice: 128, salePrice: 200, currency: 'CNY', unit: 'MONTH', billingType: 'yearly', quantity: 3, duration: 2, priceVersion: 'sale-1:catalog-1' })
    expect(cpuPricingSnapshot(spec, form)).not.toHaveProperty('total_cost')
  })
})
