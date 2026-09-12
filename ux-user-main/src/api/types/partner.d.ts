/**
 * IPersonalExtendInfo 个人推广信息
 */
export interface IPersonalExtendInfo {
  /**
   * 开户地址
   */
  address?: string
  /**
   * 开户银行
   */
  bank?: string
  /**
   * 银行账号
   */
  bankNo?: string
  /**
   * 银行卡真实姓名
   */
  bankUserName?: string
  /**
   * 可提现金额
   */
  canWithdrawalAmount?: number
  /**
   * 创建者
   */
  createBy?: string
  /**
   * 创建者ID
   */
  createById?: number
  /**
   * 创建时间
   */
  createTime?: Date
  /**
   * 删除标志（0代表存在 2代表删除）
   */
  delFlag?: number
  /**
   * 一级用户数量
   */
  firstCount?: number
  /**
   * 一级佣金比例
   */
  firstScale?: number
  /**
   * 主键
   */
  id?: number
  /**
   * 银行卡身份证
   */
  idCard?: string
  /**
   * 推广链接
   */
  link?: string
  /**
   * 真实姓名
   */
  name?: string
  /**
   * 分佣订单数量
   */
  orderCount?: number
  /**
   * 上级用户id
   */
  parentUserId?: number
  /**
   * 联系电话
   */
  phone?: string
  /**
   * 贡献金额
   */
  sgAmount?: number
  /**
   * 贡献订单
   */
  sgOrder?: number
  /**
   * 分享key
   */
  shareKey?: string
  /**
   * 状态（1审核中，2通过，3不通过）
   */
  status?: number
  /**
   * 累计佣金
   */
  totalCommission?: number
  /**
   * 二级用户数量
   */
  twoCount?: number
  /**
   * 二级佣金比例
   */
  twoScale?: number
  /**
   * 类型（1 推广大使， 2 普通用户）
   */
  type?: number
  /**
   * 更新者
   */
  updateBy?: string
  /**
   * 更新者Id
   */
  updateById?: number
  /**
   * 更新时间
   */
  updateTime?: Date
  /**
   * 用户id
   */
  userId?: number
  /**
   * 用户信息-名称
   */
  userName?: string
  /**
   * 用户信息-手机号
   */
  userPhone?: string
  /**
   * 驳回理由
   */
  verifyRemark?: string
  [property: string]: any
}
