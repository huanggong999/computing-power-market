import { describe, expect, it } from 'vitest'
import { resolveRegionDisplayName } from './regionDisplay'

describe('region display names', () => {
  it('translates Volcano region codes to Chinese names', () => {
    expect(resolveRegionDisplayName('cn-beijing', 'cn-beijing')).toBe('华北2（北京）')
    expect(resolveRegionDisplayName('cn-shanghai')).toBe('华东2（上海）')
    expect(resolveRegionDisplayName('cn-guangzhou', 'CN-GUANGZHOU')).toBe('华南（广州）')
    expect(resolveRegionDisplayName('ap-southeast-1', 'ap-southeast-1')).toBe('亚太东南（柔佛）')
    expect(resolveRegionDisplayName('ap-southeast-3', 'ap-southeast-3')).toBe('亚太东南（雅加达）')
  })

  it('keeps Chinese labels returned by the backend', () => {
    expect(resolveRegionDisplayName('northwest-b', '西北B区')).toBe('西北B区')
    expect(resolveRegionDisplayName('cn-beijing', '北京')).toBe('北京')
  })

  it('translates an English-only label when the code is known', () => {
    expect(resolveRegionDisplayName('cn-beijing', 'Beijing')).toBe('华北2（北京）')
  })

  it('safely falls back for unknown regions', () => {
    expect(resolveRegionDisplayName('custom-region-1', 'Custom Region')).toBe('Custom Region')
    expect(resolveRegionDisplayName('custom-region-1')).toBe('custom-region-1')
    expect(resolveRegionDisplayName()).toBe('--')
  })
})
