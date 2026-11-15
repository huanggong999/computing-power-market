import { describe, expect, it } from 'vitest'
import { isAllowedCpuType, isResizeTaskTerminal, validateResizeRequest } from './instanceResource'

const options: any = {
  current: { data_disk_gib: 200, cpu_cores: 8 },
  capabilities: { cpu_scale_up_only: true },
  limits: { data_disk_gib: { max: 500 } },
  backing: 'cluster'
}

describe('instance resource validation', () => {
  it('rejects data disk shrink and equal target', () => {
    expect(validateResizeRequest(options, { diskGb: 100 })).toContain('只支持扩容')
    expect(validateResizeRequest(options, { diskGb: 200 })).toContain('只支持扩容')
  })
  it('enforces limits and cluster scale-up rule', () => {
    expect(validateResizeRequest(options, { diskGb: 600 })).toContain('500')
    expect(validateResizeRequest(options, { cpu: '4' })).toContain('当前 CPU 为 8 核')
  })
  it('accepts GB memory input when it is larger than current memory', () => {
    const memoryOptions = { ...options, current: { ...options.current, memory: '32Gi', memory_mib: 32768 } }
    expect(validateResizeRequest(memoryOptions, { memory: '64GB' })).toBeNull()
  })
  it('accepts service returned cpu type IDs only', () => {
    expect(isAllowedCpuType('ecs.cpu.large', [{ instanceTypeId: 'ecs.cpu.large' }])).toBe(true)
    expect(isAllowedCpuType('ecs.gpu.large', [{ instanceTypeId: 'ecs.cpu.large' }])).toBe(false)
  })
  it('recognizes terminal task statuses', () => {
    expect(isResizeTaskTerminal('success')).toBe(true)
    expect(isResizeTaskTerminal('failed')).toBe(true)
    expect(isResizeTaskTerminal('running')).toBe(false)
  })
})
