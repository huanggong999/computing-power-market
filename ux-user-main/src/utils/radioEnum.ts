// Kubernetes 版本
export const kubernetesVersionTypeEnum: IRadioList[] = [
  { label: 'v1.30.4-vke.3', value: '1.30' },
  { label: 'v1.28.15-vke.19', value: '1.28' },
  { label: 'v1.26.15-vke.19', value: '1.26' },
  { label: 'v1.24.17-vke.38', value: '1.24' },
]
// Worker 节点
export const workerNodeTypeEnum: IRadioList[] = [
  { label: '立即创建', value: true },
  { label: '暂不创建', value: false },
]
// 节点来源
export const nodeSourceTypeEnum: IRadioList[] = [
  { label: '创建节点', value: true },
]
// 计费类型
export const billingTypeEnum: IRadioList[] = [
  { label: '按量计费', value: '' },
  { label: '包年包月', value: '' },
]
// 可用区
export const regionEnum: IRadioList[] = [{ label: '随机可用区', value: true }]
// 多子网调度策略
export const subnetPolicyTypeEnum: IRadioList[] = [
  { label: '均衡策略', value: '' },
  { label: '优先级策略', value: '' },
]
// 计算规格
export const ecsTypeEnum: IRadioList[] = [
  { label: '通用型计算', value: 'GENERAL_COMPUTE' },
  { label: '计算型', value: 'COMPUTE' },
  { label: '通用型', value: 'GENERAL' },
  { label: 'GPU', value: 'GPU' },
]
// 镜像系统
export const platformEnum: IRadioList[] = [
  { label: 'veLinux', value: 'veLinux' },
  { label: 'Ubuntu', value: 'Ubuntu' },
]
// 安全组
export const securityGroupTypeEnum: IRadioList[] = [
  { label: '自动创建并绑定默认安全组', value: true },
]
// 登录方式
export const loginTypeEnum: IRadioList[] = [{ label: '密码登录', value: true }]

// 订单类型 NEW_RESOURCE 新购资源订单  RENEW_RESOURCE 续费资源订单  BALANCE 余额充值订单
export const orderTypeEnum: IRadioList[] = [
  { label: '新购资源订单', value: 'NEW_RESOURCE' },
  { label: '续费资源订单', value: 'RENEW_RESOURCE' },
  { label: '余额充值订单', value: 'BALANCE' },
  { label: '网络产品订单', value: 'PRODUCT' },
]
// 订单状态   UNPAID 未支付  PAID 已支付  CANCELED 已取消  REFUNDED 已退款
export const orderStatusEnum: IRadioList[] = [
  { label: '未支付', value: 'UNPAID' },
  { label: '已支付', value: 'PAID' },
  { label: '已取消', value: 'CANCELED' },
  { label: '已退款', value: 'REFUNDED' },
]
