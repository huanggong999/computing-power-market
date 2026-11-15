// 实例状态枚举
export enum InstanceStatus {
  CREATING = 'creating',       // 创建中
  RUNNING = 'running',         // 运行中
  STOPPED = 'stopped',         // 已关机
  STARTING = 'starting',       // 开机中
  STOPPING = 'stopping',       // 关机中
  RESTARTING = 'restarting',   // 重启中
  RELEASING = 'releasing',     // 释放中
  RELEASED = 'released',       // 已释放
  EXPIRED = 'expired',         // 已过期
  ERROR = 'error'              // 错误
}

// 状态映射
export const StatusMap: Record<InstanceStatus, { label: string; type: string; color: string }> = {
  [InstanceStatus.CREATING]: { label: '创建中', type: 'info', color: '#909399' },
  [InstanceStatus.RUNNING]: { label: '运行中', type: 'success', color: '#67C23A' },
  [InstanceStatus.STOPPED]: { label: '已关机', type: 'info', color: '#909399' },
  [InstanceStatus.STARTING]: { label: '开机中', type: 'warning', color: '#E6A23C' },
  [InstanceStatus.STOPPING]: { label: '关机中', type: 'warning', color: '#E6A23C' },
  [InstanceStatus.RESTARTING]: { label: '重启中', type: 'warning', color: '#E6A23C' },
  [InstanceStatus.RELEASING]: { label: '释放中', type: 'danger', color: '#F56C6C' },
  [InstanceStatus.RELEASED]: { label: '已释放', type: 'info', color: '#909399' },
  [InstanceStatus.EXPIRED]: { label: '已过期', type: 'danger', color: '#F56C6C' },
  [InstanceStatus.ERROR]: { label: '错误', type: 'danger', color: '#F56C6C' }
}

// 实例数据模型
export interface Instance {
  id: string
  uuid: string
  name: string
  description?: string
  status: InstanceStatus
  statusMessage?: string
  origin?: InstanceOrigin
  gpuType: string
  gpuCount: number
  gpuMemory: number
  cpuCores: number
  cpuModel?: string
  memory: number
  systemDisk: number
  dataDisk: number
  systemDiskSize?: number
  dataDiskSize?: number
  region: string
  regionCode: string
  privateIp?: string
  publicIp?: string
  sshPort?: number
  sshCommand?: string
  rootPassword?: string
  jupyterUrl?: string
  createdAt: string
  expiredAt?: string
  expireTime?: string
  startedAt?: string
  stoppedAt?: string
  billingType: string
  pricePerHour: number
  diskUsage?: {
    systemDiskUsage?: number
    dataDiskUsage?: number
  }
  healthStatus?: {
    status?: string
    cpuUsage?: number
    memoryUsage?: number
    gpuUsage?: number
  }
}

export interface SshInfo {
  host?: string
  port?: number
  username?: string
  password?: string
  command?: string
}

export interface VncInfo {
  instanceId?: string
  instance_id?: string
  enabled?: boolean
  websocketUrl?: string
  websocket_url?: string
  vncPassword?: string
  vnc_password?: string
  nodeHost?: string
  node_host?: string
  nodePort?: number
  node_port?: number
  message?: string
}

// 筛选条件
export interface InstanceFilters {
  gpuTypes?: string[]
  regions?: string[]
  statuses?: InstanceStatus[]
  keyword?: string
}

// 列表参数
export interface InstanceListParams {
  page: number
  pageSize: number
  filters?: InstanceFilters
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

// 列表结果
export interface InstanceListResult {
  list: Instance[]
  total: number
  page: number
  pageSize: number
}

// 监控数据点
export interface MetricDataPoint {
  timestamp: number
  value: number
}

// 实例监控数据
export interface InstanceMetrics {
  instanceId: string
  timeRange: [number, number]
  gpuUtilization: MetricDataPoint[]
  gpuMemoryUsed: MetricDataPoint[]
  gpuMemoryTotal: number
  gpuTemperature?: MetricDataPoint[]
  cpuUtilization: MetricDataPoint[]
  cpuLoad?: MetricDataPoint[]
  memoryUsed: MetricDataPoint[]
  memoryTotal: number
  diskRead?: MetricDataPoint[]
  diskWrite?: MetricDataPoint[]
  networkIn?: MetricDataPoint[]
  networkOut?: MetricDataPoint[]
}

// v2.1 GPU resize and independent CPU instance contracts
export type InstanceBacking = 'volc' | 'cluster' | string
export type InstanceOrigin = 'gpu_pod' | 'cpu_instance' | string
export type ResizeTaskStatus = 'pending' | 'running' | 'success' | 'failed' | string

export interface ResourceLimit { min?: number; max?: number }
export interface ResizeCapabilities {
  volc_sync?: boolean
  disk_expand?: boolean
  disk_shrink?: boolean
  cpu_scale?: boolean
  memory_scale?: boolean
  cpu_scale_up_only?: boolean
  requires_stop_for_spec?: boolean
  bandwidth_change?: boolean
  gpu_scale?: boolean
  gpu_scale_up_only?: boolean
  gpu_model_fixed?: boolean
}
export interface ResizeGpuTarget {
  instance_type_id?: string
  gpu_count: number
  cpu_cores?: number
  mem_gib?: number
  stock_status?: string
  selectable?: boolean
}
export interface ResizeOptions {
  instance_id: string
  backing?: InstanceBacking
  volc_instance_id?: string
  current: {
    cpu_cores?: number
    memory?: string
    memory_mib?: number
    system_disk_gib?: number
    data_disk_gib?: number
    bandwidth_mbps?: number
    public_ip_allocation_id?: string
    gpu_model?: string
    gpu_count?: number
  }
  gpu_targets?: ResizeGpuTarget[]
  capabilities: ResizeCapabilities
  limits: {
    bandwidth_mbps?: ResourceLimit
    system_disk_gib?: ResourceLimit
    data_disk_gib?: ResourceLimit
  }
  note?: string
}
export interface ResizeRequest {
  diskGb?: number
  cpu?: string
  memory?: string
  instanceTypeId?: string
  gpuCount?: number
}
export interface ResizeTaskStep {
  step?: string
  ok?: boolean
  message?: string
  ts?: number
}
export interface ResizeTask {
  task_id: string
  instance_id: string
  kind?: string
  params?: Record<string, unknown>
  status: ResizeTaskStatus
  steps?: ResizeTaskStep[]
  message?: string
  created_at?: number
  updated_at?: number
  finished_at?: number | null
}
export interface ResizeSubmitResult { success: boolean; message?: string; task_id?: string; status?: ResizeTaskStatus; task_url?: string }
export interface ResizeTaskListResult { instance_id: string; total: number; tasks: ResizeTask[] }
export interface BandwidthRequest { bandwidthMbps: number }
export interface BandwidthResult { success: boolean; instance_id?: string; allocation_id?: string; bandwidth_mbps?: number; message?: string }

export interface CpuRegion { region: string; label?: string; zones: string[] }
export interface CpuZoneDetail {
  region?: string
  zone: string
  name?: string
  subnetCidr?: string
  orderable?: boolean
}
export interface CpuZoneCatalog { success?: boolean; zones?: CpuZoneDetail[] }
export interface CpuInstanceType { instanceTypeId: string; family?: string; cpuCores: number; cpuModel?: string; memGib: number }
export interface CpuPriceObject { available?: boolean; pricePending?: boolean; unitPrice?: number; currency?: string }
export interface CpuPriceSpec extends CpuInstanceType {
  price?: CpuPriceObject | null
  priceMonthly?: CpuPriceObject | null
  salePrice?: number | null
  salePriceMonthly?: number | null
  priceVersion?: string
  hourlyPriceVersion?: string
  monthlyPriceVersion?: string
}
export interface CpuPriceCatalog { success: boolean; region?: string; specs: CpuPriceSpec[]; priceVersion?: string }
export interface CpuSshInfo { host?: string; port?: number; username?: string; password?: string; password_masked?: string; command?: string }
export interface CpuSshResponse {
  success?: boolean
  message?: string
  instance_id?: string
  status?: string
  public_ip?: string
  private_ip?: string
  ssh_info?: CpuSshInfo
}
export interface CpuInstanceAction {
  label?: string
  method?: string
  path?: string
  enabled?: boolean
  reason?: string
}
export interface CpuInstance {
  id?: string
  instance_id?: string
  name?: string
  instance_name?: string
  region?: string
  zone?: string
  status: InstanceStatus | string
  origin: 'cpu_instance' | string
  operation_id?: string
  message?: string
  status_message?: string
  instance_type_id?: string
  cpu_cores?: number
  cpu_model?: string
  memory?: string
  system_disk?: string
  data_disk?: string
  billing_mode?: 'on_demand' | 'monthly' | 'yearly' | string
  public_ip?: string
  private_ip?: string
  volc_instance_id?: string
  create_time?: string
  expire_time?: string | null
  ssh_info?: CpuSshInfo
  actions?: Record<string, CpuInstanceAction>
}
export interface CpuPurchaseRequest {
  region: string; zone: string; instanceTypeId: string
  billingMode: 'on_demand' | 'monthly' | 'yearly'
  duration?: number; count?: number; systemDiskGb?: number; dataDiskGb?: number
  requestKey?: string
  pricing?: { upstreamPrice?: number; salePrice?: number; unit_price?: number; currency?: string; unit?: string; billingType?: string; quantity?: number; duration?: number; priceVersion?: string }
}
export interface CpuPurchaseResult {
  success: boolean; message?: string
  accepted?: boolean; statusMessage?: string; operation_id?: string; operation_status?: string
  checks?: Array<{ name?: string; ok?: boolean; message?: string }>
  instances?: Array<Record<string, unknown>>
  errors?: Array<Record<string, unknown> | string>
}
export interface CpuListResult { success: boolean; message?: string; tenant_id?: string; total: number; instances: CpuInstance[] }

export function normalizeBusinessError(payload: any): string {
  return payload?.message || payload?.msg || payload?.detail || '服务请求失败'
}
export function isResizeTaskTerminal(status?: string): boolean { return status === 'success' || status === 'failed' }
