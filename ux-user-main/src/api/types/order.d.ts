interface IEcsParams {
  pageNo: number
  pageSize: number
  sourceRegions: string
  ecsTypeEnum: string
}

export interface IEcsImgParams {
  /**
   * 实例的规格ID，传入本参数时，将返回该规格可用的镜像ID列表
   */
  instanceTypeId?: string
  /**
   * 分页查询时设置的每页行数。
   * 取值范围：1 ~ 100
   * 默认值：15
   */
  maxResults?: number
  /**
   * 分页查询凭证，用于标记分页的位置，初次调用该接口时无需设置。下次查询时，取值为上一次API调用返回的NextToken参数值。
   */
  nextToken?: string
  /**
   * 操作系统类型。取值：
   * Linux
   * Windows
   */
  osType?: string
  /**
   * 镜像操作系统的发行版本。取值：
   * CentOS
   * Debian
   * veLinux
   * Windows Server
   * Fedora
   * OpenSUSE
   * Ubuntu
   */
  platform?: string
  /**
   * public：公共镜像  private：自定义镜像 shared：共享镜像
   */
  visibility?: string
  region?: string
}
/**
 * ISourceRegion
 */
export interface ISourceRegion {
  /**
   * 区域id
   */
  id?: string
  /**
   * 区域名称
   */
  name?: string
  /**
   * 区域枚举
   */
  regions?: string
  [property: string]: any
}
// 构建订单参数

export interface IBuildOrderParams {
  /**
   * 金额，充值订单需要
   */
  amount?: number
  /**
   * 余额支付
   */
  balancePay?: boolean
  /**
   * 优惠卷id
   */
  couponId?: number
  /**
   * 在线支付方式
   */
  onlinePayType?: string
  /**
   * 订单号，创建续费订单需要
   */
  orderNo?: string
  /**
   * 订单资源（云服务器，云盘，网络等）
   */
  orderSource?: SysOrderSourceCreateDTO[]
  /**
   * 订单类型
   */
  orderType: string
  /**
   * 代金卷支付
   */
  voucherPay?: boolean
  [property: string]: any
}

/**
 * SysOrderSourceCreateDTO，订单资源（云服务器，云盘，网络等）
 */
export interface SysOrderSourceCreateDTO {
  /**
   * 计费类型
   */
  chargeType?: string
  /**
   * 配置详情， 为各个配置的列表参数
   */
  configDetail?: string
  /**
   * 时长
   */
  duration?: number
  /**
   * 时长单位
   */
  durationUnit?: string
  /**
   * 地区
   */
  regionsId?: string
  /**
   * 资源类型
   */
  sourceType?: string
  /**
   * uid，区别多个服务器下单，一台服务器必须有系统盘，网络，uid一致前端生成
   */
  uid?: string
  [property: string]: any
}
