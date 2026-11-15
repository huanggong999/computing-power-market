import { describe, expect, it } from 'vitest'
import { cpuActionState, cpuCanAction, cpuCloudInstanceId, cpuDetailTarget, cpuRowKey, isCpuInstance, isGpuInstance } from './instanceSource'

describe('instance source isolation', () => {
  it('keeps GPU and CPU rows mutually exclusive', () => {
    const cpu = { id: 'cpu-1', origin: 'cpu_instance', status: 'running' as const }
    const gpu = { id: 'gpu-1', origin: 'gpu_pod', status: 'running' as const }
    expect(isCpuInstance(cpu)).toBe(true)
    expect(isGpuInstance(cpu)).toBe(false)
    expect(isGpuInstance(gpu)).toBe(true)
    expect(isCpuInstance(gpu)).toBe(false)
  })

  it('allows release while creating and refuses invalid lifecycle actions', () => {
    expect(cpuCanAction({ origin: 'cpu_instance', status: 'creating', instance_id: 'cpu-1' }, 'release')).toBe(true)
    expect(cpuCanAction({ origin: 'cpu_instance', status: 'creating', instance_id: 'cpu-1', operation_id: 'op-1' }, 'release')).toBe(true)
    expect(cpuCanAction({ origin: 'cpu_instance', status: 'creating', instance_id: 'cpu-1' }, 'stop')).toBe(false)
    expect(cpuCanAction({ origin: 'cpu_instance', status: 'failed' }, 'start')).toBe(false)
    expect(cpuCanAction({ origin: 'cpu_instance', status: 'failed' }, 'clear')).toBe(true)
    expect(cpuCanAction({ origin: 'cpu_instance', status: 'stopped', instance_id: 'cpu-1' }, 'start')).toBe(true)
  })

  it('keeps a creating instance with an ID releasable even when actions.release is disabled', () => {
    for (const status of ['creating', 'pending', 'provisioning']) {
      const instance = {
        origin: 'cpu_instance',
        status,
        instance_id: 'cpu-1',
        actions: { release: { enabled: false, reason: '' } },
      }
      expect(cpuActionState(instance, 'release')).toEqual({ enabled: true, reason: '' })
    }
    expect(cpuActionState({ origin: 'cpu_instance', status: 'creating' }, 'release'))
      .toEqual({ enabled: false, reason: '实例正在创建中，请等创建完成后再释放' })
  })

  it('prefers actions returned by the CPU detail API', () => {
    const instance = {
      origin: 'cpu_instance',
      status: 'running',
      instance_id: 'cpu-1',
      actions: {
        restart: { enabled: false, reason: '有进行中的变配任务' },
        ssh: { enabled: true },
      },
    }
    expect(cpuActionState(instance, 'restart')).toEqual({ enabled: false, reason: '有进行中的变配任务' })
    expect(cpuActionState(instance, 'ssh')).toEqual({ enabled: true, reason: '' })
  })

  it('falls back to lifecycle rules for restart, ssh, password, rename and resize', () => {
    const running = { origin: 'cpu_instance', status: 'running', instance_id: 'cpu-1' }
    const stopped = { origin: 'cpu_instance', status: 'stopped', instance_id: 'cpu-1' }
    const creating = { origin: 'cpu_instance', status: 'creating', instance_id: 'cpu-1' }
    expect(cpuCanAction(running, 'restart')).toBe(true)
    expect(cpuCanAction(running, 'ssh')).toBe(true)
    expect(cpuCanAction(running, 'resetPassword')).toBe(true)
    expect(cpuCanAction(running, 'rename')).toBe(true)
    expect(cpuCanAction(running, 'resize')).toBe(true)
    expect(cpuCanAction(stopped, 'restart')).toBe(false)
    expect(cpuCanAction(stopped, 'ssh')).toBe(false)
    expect(cpuCanAction(stopped, 'resetPassword')).toBe(true)
    expect(cpuCanAction(creating, 'rename')).toBe(false)
    expect(cpuCanAction(creating, 'resize')).toBe(false)
  })

  it('never treats a creation operation ID as a cloud instance ID', () => {
    const pending = { id: 'op-1', operation_id: 'op-1', origin: 'cpu_instance', status: 'creating' }
    expect(cpuCloudInstanceId(pending)).toBe('')
    expect(cpuRowKey(pending)).toBe('op-1')
    expect(cpuDetailTarget(pending)).toEqual({ kind: 'operation', id: 'op-1' })
    expect(cpuDetailTarget({ id: 'cpu-1', instance_id: 'cpu-1', operation_id: 'op-1', origin: 'cpu_instance', status: 'creating' }))
      .toEqual({ kind: 'instance', id: 'cpu-1' })
  })

  it('keeps operation-only creation records readable and clearable', () => {
    const pending = { origin: 'cpu_instance', status: 'creating', operation_id: 'op-1' }
    const failed = { origin: 'cpu_instance', status: 'failed', operation_id: 'op-1' }
    expect(cpuActionState(pending, 'detail').enabled).toBe(true)
    expect(cpuActionState(pending, 'release').enabled).toBe(false)
    expect(cpuActionState(pending, 'start').enabled).toBe(false)
    expect(cpuActionState(failed, 'clear').enabled).toBe(true)
    expect(cpuActionState(failed, 'release').enabled).toBe(false)
  })
})
