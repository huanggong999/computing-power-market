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
  startedAt?: string
  stoppedAt?: string
  billingType: 'hourly' | 'monthly'
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
