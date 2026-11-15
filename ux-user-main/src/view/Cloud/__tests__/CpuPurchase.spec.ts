import { describe, it, expect, vi, beforeEach } from 'vitest'
import { shallowMount, flushPromises } from '@vue/test-utils'
import CpuPurchase from '../CpuPurchase.vue'
import { DATA_DISK_CAPACITY_MESSAGE } from '@/utils/cpuPurchase'
const api = vi.hoisted(() => ({ getCpuRegions: vi.fn(), getCpuZones: vi.fn(), getCpuInstanceTypes: vi.fn(), getCpuPrices: vi.fn(), purchaseCpu: vi.fn() }))
vi.mock('@/api/instanceV2', () => api)
vi.mock('vue-router', () => ({ useRouter: () => ({ push: vi.fn() }) }))
vi.mock('element-plus', () => ({ ElMessage: { error: vi.fn(), success: vi.fn(), warning: vi.fn() } }))
const priced = { instanceTypeId: 'ecs.cpu', cpuCores: 2, memGib: 4, price: { available: true, currency: 'CNY', unitPrice: 0.28 }, salePrice: 0.5, priceMonthly: { available: true, currency: 'CNY', unitPrice: 128 }, salePriceMonthly: 200 }
const options = { global: { renderStubDefaultSlot: true, directives: { loading: () => {} }, stubs: ['el-card', 'el-form', 'el-form-item', 'el-select', 'el-option', 'el-radio-group', 'el-radio-button', 'el-input-number', 'el-alert', 'el-button', 'el-empty', 'el-descriptions', 'el-descriptions-item'] } }
beforeEach(() => {
  vi.clearAllMocks()
  api.getCpuRegions.mockResolvedValue({ data: { success: true, regions: [{ region: 'cn-a', zones: ['cn-a-1'] }] } })
  api.getCpuZones.mockResolvedValue({ data: { success: true, zones: [{ region: 'cn-a', zone: 'cn-a-1', name: '可用区A' }] } })
  api.getCpuInstanceTypes.mockResolvedValue({ data: { success: true, instance_types: [priced, { instanceTypeId: 'no-price', cpuCores: 4, memGib: 8 }] } })
  api.getCpuPrices.mockResolvedValue({ data: { success: true, specs: [priced] } })
  api.purchaseCpu.mockResolvedValue({ data: { success: true, operation_id: 'op-1' } })
})
describe('CPU purchase prices', () => {
  it('shows the Chinese zone name while keeping the raw zone code as the option value', async () => {
    const wrapper = shallowMount(CpuPurchase, options)
    await flushPromises()
    const zoneOption = wrapper.findAll('el-option-stub').find(option => option.attributes('value') === 'cn-a-1')
    expect(zoneOption?.attributes('label')).toBe('可用区A')
    wrapper.unmount()
  })

  it('falls back to a readable zone label when zone metadata is unavailable', async () => {
    api.getCpuRegions.mockResolvedValueOnce({ data: { success: true, regions: [{ region: 'ap-southeast-1', zones: ['ap-southeast-1a'] }] } })
    api.getCpuZones.mockRejectedValueOnce(new Error('Not Found'))
    const wrapper = shallowMount(CpuPurchase, options)
    await flushPromises()
    const zoneOption = wrapper.findAll('el-option-stub').find(option => option.attributes('value') === 'ap-southeast-1a')
    expect(zoneOption?.attributes('label')).toBe('可用区A')
    wrapper.unmount()
  })

  it('hides specifications without prices and displays only the selected unit price', async () => {
    const wrapper = shallowMount(CpuPurchase, options)
    await flushPromises()
    const selects = wrapper.findAllComponents({ name: 'el-select' })
    selects[2].vm.$emit('update:modelValue', 'ecs.cpu')
    await flushPromises()
    expect(wrapper.get('[data-testid="cpu-unit-price"]').text()).toBe('¥0.5000 / 小时')
    expect(wrapper.findAll('el-option-stub').some(option => option.attributes('value') === 'no-price')).toBe(false)
    expect(wrapper.text()).not.toContain('合计')
    wrapper.unmount()
  })
  it('blocks submission before the purchase API when data disk is 1-19G', async () => {
    const wrapper = shallowMount(CpuPurchase, options)
    await flushPromises()
    wrapper.findAllComponents({ name: 'el-select' })[2].vm.$emit('update:modelValue', 'ecs.cpu')
    await flushPromises()
    wrapper.findAllComponents({ name: 'el-input-number' })[1].vm.$emit('update:modelValue', 1)
    await flushPromises()
    const submit = wrapper.findAll('el-button-stub').find(item => item.text().includes('立即购买'))
    await submit!.trigger('click')
    await flushPromises()
    expect(api.purchaseCpu).not.toHaveBeenCalled()
    const dataDiskItem = wrapper.findAll('el-form-item-stub').find(item => item.attributes('label') === '数据盘 (GiB)')
    expect(dataDiskItem?.attributes('error')).toBe(DATA_DISK_CAPACITY_MESSAGE)
    wrapper.unmount()
  })
  it('does not display CPU specifications for yearly billing', async () => {
    const wrapper = shallowMount(CpuPurchase, options)
    await flushPromises()
    wrapper.findComponent({ name: 'el-radio-group' }).vm.$emit('update:modelValue', 'yearly')
    await flushPromises()
    expect(wrapper.findAll('el-option-stub').some(option => option.attributes('value') === 'ecs.cpu')).toBe(false)
    expect(wrapper.get('[data-testid="cpu-unit-price"]').text()).toBe('--')
    expect(api.getCpuPrices).toHaveBeenCalledTimes(1)
    wrapper.unmount()
  })
})
