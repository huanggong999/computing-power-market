/**
 * Human-readable names for cloud regions used by the compute purchase flow.
 * Keep this aligned with SourceRegionsEnum on the backend, because the
 * Volcano GPU catalog only returns region codes.
 */
const REGION_NAME_BY_CODE: Readonly<Record<string, string>> = Object.freeze({
  'cn-beijing': '华北2（北京）',
  'cn-shanghai': '华东2（上海）',
  'cn-guangzhou': '华南（广州）',
  'cn-shenzhen': '华南（深圳）',
  'cn-shaoguan': '华南（韶关）',
  'cn-guiyang': '贵州（贵阳）',
  'cn-zhongwei': '宁夏（中卫）',
  'cn-huhehaote': '内蒙（呼和浩特）',
  'cn-hongkong': '中国（香港）',
  'cn-taiwan': '中国（台湾）',
  'ap-southeast-1': '亚太东南（柔佛）',
  'ap-southeast-3': '亚太东南（雅加达）',
  'jpn-tokyo': '日本（东京）',
  'vnm-henei': '越南（河内）',
  'usa-los-angeles': '美国（洛杉矶）',
  'malaysia_johor': '马来西亚（柔佛）',
})

const CHINESE_TEXT_PATTERN = /[\u3400-\u9fff]/

export function hasChineseText(value: string): boolean {
  return CHINESE_TEXT_PATTERN.test(value)
}

/**
 * Resolve a region name for display while preserving labels supplied by the
 * backend. Codes or English-only labels are translated using the known catalog;
 * unknown regions safely fall back to the original name/code.
 */
export function resolveRegionDisplayName(regionCode?: string | null, regionName?: string | null): string {
  const code = String(regionCode ?? '').trim()
  const name = String(regionName ?? '').trim()

  if (name && hasChineseText(name)) return name

  const nameMapping = name ? REGION_NAME_BY_CODE[name] || REGION_NAME_BY_CODE[name.toLowerCase()] : undefined
  if (nameMapping) return nameMapping

  const codeMapping = REGION_NAME_BY_CODE[code] || REGION_NAME_BY_CODE[code.toLowerCase()]
  if (codeMapping) return codeMapping

  return name || code || '--'
}
