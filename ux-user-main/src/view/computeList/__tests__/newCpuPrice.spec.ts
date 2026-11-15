import { describe, expect, it, vi, beforeEach } from 'vitest'
import { flushPromises, shallowMount } from '@vue/test-utils'

const api = vi.hoisted(() => ({
  getCpuRegions: vi.fn(), getCpuZones: vi.fn(), getCpuInstanceTypes: vi.fn(), getCpuPrices: vi.fn(), purchaseCpu: vi.fn(),
  getGpuMarketList: vi.fn(), getGpuMarketMeta: vi.fn(),
}))
vi.mock('@/api/instanceV2', () => api)
vi.mock('@/api/gpuMarket', () => ({
  getGpuMarketList: api.getGpuMarketList,
  getGpuMarketMeta: api.getGpuMarketMeta,
}))
vi.mock('@/api/volcanoGpu', () => ({ getVolcanoGpuCatalog: vi.fn() }))
vi.mock('@/utils/gpuBilling', () => ({ gpuBillingLabelMap: {} }))
vi.mock('vue-router', () => ({
  useRoute: () => ({ query: { type: 'cpu' } }),
  useRouter: () => ({ push: vi.fn() }),
}))
vi.mock('element-plus', () => ({ ElMessage: { error: vi.fn(), success: vi.fn(), warning: vi.fn() } }))

const priced = {
  instanceTypeId: 'ecs.c3a.xlarge', cpuCores: 4, memGib: 8,
  price: { available: true, currency: 'CNY', unitPrice: 0.28 },
  priceMonthly: { available: true, currency: 'CNY', unitPrice: 128 },
  salePrice: 0.5, salePriceMonthly: 200,
}
const options = {
  global: {
    renderStubDefaultSlot: true,
    directives: { loading: () => {} },
    stubs: ['el-select', 'el-option', 'el-input-number', 'el-alert', 'el-button', 'el-table', 'el-table-column', 'el-pagination', 'el-radio-group', 'el-radio-button', 'el-switch', 'el-empty', 'el-descriptions', 'el-descriptions-item'],
  },
}

beforeEach(() => {
  vi.clearAllMocks()
  api.getGpuMarketList.mockResolvedValue({ code: 200, data: { records: [] } })
  api.getGpuMarketMeta.mockResolvedValue({ code: 200, data: { regions: [], zones: [], gpuModels: [], gpuCounts: [], billingTypes: [] } })
  api.getCpuRegions.mockResolvedValue({ data: { success: true, regions: [{ region: 'ap-southeast-1', label: '亚太·新加坡', zones: ['ap-southeast-1a'] }] } })
  api.getCpuZones.mockResolvedValue({ data: { success: true, zones: [{ region: 'ap-southeast-1', zone: 'ap-southeast-1a', name: '可用区A' }] } })
  api.getCpuInstanceTypes.mockResolvedValue({ data: { success: true, instance_types: [priced, { instanceTypeId: 'ecs.c3a.24xlarge', cpuCores: 96, memGib: 192 }] } })
  api.getCpuPrices.mockResolvedValue({ data: { success: true, specs: [priced] } })
})

describe('computeListNew CPU price display', () => {
  it('shows the Chinese zone name while keeping the raw zone code as the option value', async () => {
    const { default: Page } = await import('../new.vue')
    const wrapper = shallowMount(Page, options)
    await flushPromises()
    const zoneOption = wrapper.findAll('el-option-stub').find(option => option.attributes('value') === 'ap-southeast-1a')
    expect(zoneOption?.attributes('label')).toBe('可用区A')
    expect(api.getCpuZones).toHaveBeenCalledWith()
    wrapper.unmount()
  })

  it('falls back to a readable zone label when zone metadata is unavailable', async () => {
    api.getCpuZones.mockRejectedValueOnce(new Error('Not Found'))
    const { default: Page } = await import('../new.vue')
    const wrapper = shallowMount(Page, options)
    await flushPromises()
    const zoneOption = wrapper.findAll('el-option-stub').find(option => option.attributes('value') === 'ap-southeast-1a')
    expect(zoneOption?.attributes('label')).toBe('可用区A')
    wrapper.unmount()
  })

  it('loads the independent price catalog and displays the configured monthly unit price', async () => {
    const { default: Page } = await import('../new.vue')
    const wrapper = shallowMount(Page, options)
    await flushPromises()
    expect(api.getCpuPrices).toHaveBeenCalledWith('ap-southeast-1', undefined, 'on_demand')
    expect(wrapper.get('[data-testid="cpu-unit-price"]').text()).toContain('¥0.5000 / 小时')
    expect(wrapper.findAll('el-option-stub').some(option => option.attributes('value') === 'ecs.c3a.24xlarge')).toBe(false)

    const billingButtons = wrapper.findAll('button.option-card')
    await billingButtons.find(button => button.text().includes('包月'))?.trigger('click')
    await flushPromises()
    expect(api.getCpuPrices).toHaveBeenLastCalledWith('ap-southeast-1', undefined, 'monthly')
    expect(wrapper.get('[data-testid="cpu-unit-price"]').text()).toContain('¥200.0000 / 月')
    const priceCallsBeforeYearly = api.getCpuPrices.mock.calls.length
    await billingButtons.find(button => button.text().includes('包年'))?.trigger('click')
    await flushPromises()
    expect(wrapper.findAll('el-option-stub').some(option => option.attributes('value') === 'ecs.c3a.xlarge')).toBe(false)
    expect(wrapper.get('[data-testid="cpu-unit-price"]').text()).toContain('--')
    expect(wrapper.text()).not.toContain('包年按包月单价展示')
    expect(api.getCpuPrices).toHaveBeenCalledTimes(priceCallsBeforeYearly)
    wrapper.unmount()
  })
})
