/** * @description 弹窗标题映射 */

export enum popoverMap {
  add = "新增",
  check = "查看",
  edit = "编辑",
}

/**
 * @description 弹窗标题映射类型
 */
export type PopoverType = keyof typeof popoverMap;

const map = {
  // 是否显示 0 是  1 否
  isShowEnum: { 0: "是", 1: "否" },
  // ALL 全部数据权限 CUSTOMIZE 自定义数据权限  SELF  仅本人数据权限
  dataScopeEnum: {
    ALL: "全部数据权限",
    CUSTOMIZE: "自定义数据权限",
    SELF: "仅本人数据权限",
  },
  // 状态 OK 正常 DEACTIVATED 停用
  statusEnum: { OK: "正常", DEACTIVATED: "停用" },
  // 状态  1 启用  2 停用
  statusNumEnum: { 1: "启用", 2: "停用" },
  //
  yesOrNoEnum: { 1: "是", 2: "否" },
  // MAN 男 WOMAN 女 UNKNOWN 未知
  sexEnum: { MAN: "男", WOMAN: "女", UNKNOWN: "未知" },
  // 优惠卷类型  FULL_REDUCTION 满减卷
  couponTypeEnum: { FULL_REDUCTION: "满减券" },
  // 优惠券领取条件  UNCONDITIONAL 无条件领取 NEW_USER 新人领取
  couponReceiveTypeEnum: { UNCONDITIONAL: "无条件领取", NEW_USER: "新人领取" },
  // 优惠券使用范围  ALL 全部  SERVER 服务器  CONTAINER 容器  IMAGE_REPOSITORY 镜像仓库  OBJECT_STORAGE 对象存储   NETWORK 网络  AGIC AGI-C
  couponRangeEnum: {
    ALL: "全部",
    SERVER: "服务器",
    CONTAINER: "容器",
    IMAGE_REPOSITORY: "镜像仓库",
    OBJECT_STORAGE: "对象存储",
    NETWORK: "网络",
    AGIC: "AGI-C",
  },
  // 资源折扣比列 资源类型( ECS 服务器   CLOUD_STORAGE 云存储  CR 云镜像  CLOUD_OBJECT_STORAGE 对象存储  CLOUD_NETWORK 网络 CONTAINER 容器 )
  resourceDiscountEnum: {
    ECS: "服务器折扣",
    CLOUD_STORAGE: "云存储折扣",
    CR: "云镜像折扣",
    CLOUD_OBJECT_STORAGE: "对象存储折扣",
    CLOUD_NETWORK: "网络折扣",
    CONTAINER: "容器折扣",
    AGIC: "AGIC",
    VKE: "容器折扣",
    GPU_SERVER: "GPU服务器折扣",
  },
  // 优惠卷状态  NOT_USED 未使用  USED 已使用  EXPIRED已过期

  // 云服务器 可用区  regionsZones  cn-beijing 华北2（北京）  cn-shanghai 华东2（上海） cn-guangzhou 华南1（广州） ap-southeast-1  亚太东南（柔佛） cn-hongkong  香港
  regionEnum: {
    // "cn-beijing": "华北2（北京）",
    // "cn-shanghai": "华东2（上海）",
    // "cn-guangzhou": "华南1（广州）",
    // "ap-southeast-1": "亚太东南（柔佛）",
    // "cn-hongkong": "香港",
    CN_BEIJING: "华北2（北京）",
    CN_SHANGHAI: "华东2（上海）",
    CN_GUANGZHOU: "华南1（广州）",
    AP_SOUTHEAST_1: "亚太东南（柔佛）",
    CN_HONGKONG: "香港",
    //
    CN_SHENZHEN: "华南（深圳）",
    CN_SHAOGUAN: "华南（韶关）",
    CN_GUIYANG: "贵州（贵阳）",
    CN_ZHONGWEI: "宁夏（中卫）",
    CN_HUHEHAOTE: "内蒙（呼和浩特）",
    CN_TAIWAN: "中国（台湾）",
    JPN_TOKYO: "日本（东京）",
    VNM_HENEI: "越南（河内）",
    USA_LOS_ANGELES: "美国（洛杉矶）",
    MALAYSIA_JOHOR: "马来西亚（柔佛）",
  },
  // ecsType GENERAL_COMPUTE 通用型计算 COMPUTE 计算型  GENERAL 通用型  GPU GPU
  ecsTypeEnum: {
    GENERAL_COMPUTE: "通用型",
    COMPUTE: "计算型",
    GENERAL: "内存型",
    GPU: "GPU",
  },
  // 轮播图跳转位置 ACTIVITY 活动中心  COUPON 优惠券  BUDDY 合作伙伴
  bannerJumpPositionEnum: {
    ACTIVITY: "活动中心",
    COUPON: "优惠券",
    BUDDY: "合作伙伴",
  },
  // 轮播图位置
  bannerPositionEnum: { 1: "首页", 2: "AGI-C", 3: "合作中心" },
  // 订单类型
  orderTypeEnum: {
    NEW_RESOURCE: "新购资源订单",
    RENEW_RESOURCE: "续费资源订单",
    BALANCE: "余额充值订单",
    PRODUCT: "AGI-C购买订单",
    RENEW_PRODUCT: "AGI-C续费订单",
    UPGRADE_PRODUCT: "AGI-C升级订单",
  },
  // 订单状态  UNPAID 未支付  PAID 已支付  CANCELED 已取消  REFUNDED 已退款
  orderStatusEnum: {
    UNPAID: "待支付",
    PAID: "已支付",
    CANCELED: "已取消",
    REFUNDED: "已退款",
  },
  // 产品状态  1 未开通  2 已开通  3 已过期  4 已暂停 5 已撤线
  productStatusEnum: {
    1: "未开通",
    2: "运行中",
    3: "已过期",
    4: "已暂停",
    5: "已撤线",
  },
  // 产品支付状态 0 未支付  1 已支付
  productPayStatusEnum: { 0: "未支付", 1: "已支付" },

  // 计费类型  POSTPAID_BY_HOUR 按量付费  POSTPAID_BY_MONTH 包月  POSTPAID_BY_YEAR 包年  ONE_PAY 一次性付费
  billingTypeEnum: {
    POSTPAID_BY_HOUR: "按量付费",
    POSTPAID_BY_MONTH: "包月",
    POSTPAID_BY_YEAR: "包年",
    ONE_PAY: "一次性付费",
  },
  // 充值方式  ALI_PAY 支付宝支付  WECHAT_PAY 微信支付  REMIT_PAY 对公打款
  payTypeEnum: {
    ALI_PAY: "支付宝支付",
    WECHAT_PAY: "微信支付",
    REMIT_PAY: "对公打款",
  },
  // 文档级别 1 一级 2 二级 3 三级 4 四级
  documentLevelEnum: { 1: "一级", 2: "二级", 3: "三级", 4: "四级" },

  // status  审核状态 审核中  2 通过  3 不通过
  statusAuditEnum: { 1: "审核中", 2: "通过", 3: "不通过" },
  // 提现状态 1 审核中  2 待打款  3 不通过 4 已打款
  statusWithdrawalEnum: { 1: "审核中", 2: "待打款", 3: "不通过", 4: "已打款" },
  //

  // 返佣订单用户等级  1 新用户  2 激活用户  3 老用户）
  rebateOrderLevelEnum: { 1: "新用户", 2: "激活用户", 3: "老用户" },
  // 发票状态  1 开票中  2 已开票  3 开票失败
  invoiceStatusEnum: { 1: "开票中", 2: "已开票", 3: "开票失败" },
  // 发票类型  1 增值税普通发票  2 增值税专用发票
  invoiceTypeEnum: { 1: "增值税普通发票", 2: "增值税专用发票" },
  // 活动状态 1 未开始 2 进行中 3 已结束
  activityStatusEnum: { 1: "未开始", 2: "进行中", 3: "已结束" },
  // 充值奖励类型 1 充值活动奖励
  rechargeRewardTypeEnum: { 1: "充值活动奖励" },
  // 合同状态  1  草稿  2  待签署  3  已签署  4 已过期
  contractStatusEnum: { 1: "草稿", 2: "待签署", 3: "已签署", 4: "已过期" },

  //  代金券交易类型  RECHARGE 充值  PLATFORM_GRANT 平台发放 REFUND 退款  PAY_DISCOUNT 支付抵扣
  couponTradeTypeEnum: {
    RECHARGE: "充值",
    PLATFORM_GRANT: "平台发放",
    REFUND: "退款",
    PAY_DISCOUNT: "支付抵扣",
  },
  // 对公打款状态  1 待核实  2 已打款   3  未打款
  remitStatusEnum: { 1: "待核实", 2: "已打款", 3: "未打款" },
  // remitStatusTag: { 1: "warning", 2: "success", 3: "danger" },
  // 对公打款银行类型  1  国内   2 国外
  remitBankTypeEnum: { 1: "国内", 2: "国外" },
  // 企业认证状态  1 未提交， 2 审核中， 3 已通过，4 未通过
  enterpriseCertificationStatusEnum: {
    1: "未提交",
    2: "审核中",
    3: "已通过",
    4: "未通过",
  },
  // 实例状态
  instanceStatusEnum: {
    CREATING: "创建中",
    RUNNING: "运行中",
    STOPPING: "停止中",
    STOPPED: "已停止",
    REBOOTING: "重启中",
    STARTING: "启动中",
    REBUILDING: "重装中",
    RESIZING: "更配中",
    ERROR: "错误",
    DELETING: "删除中",
  },
  // 授信额合同 状态 1 待沟通  2 待签署 3 已签署  4 已过期
  // 1 待沟通,  2 待签署 ， 3 已签署 ，  4 已过期 ，  6 签署中,  7 已撤回
  creditContractStatusEnum: {
    1: "待沟通",
    2: "待签署",
    3: "已签署",
    4: "已过期",
    6: "签署中",
    7: "已撤回",
  },
  // 纸质合同 状态  1 待审核 。3 审核通过 5 审核不通过
  // 1 待乙方确认 ，2  待归档 ， 3  已签署 ， 5  审核不通过 ， 6  归档审核不通过   8 待甲方上传
  creditContractAuditStatusEnum: {
    1: "待乙方确认",
    2: "待归档",
    3: "已签署",
    5: "审核不通过",
    6: "归档审核不通过",
    8: "待甲方上传",
  },
  // 授信额合同类型   1 电子合同  2 纸质合同
  creditContractTypeEnum: { 1: "电子合同", 2: "纸质合同" },
  // 授信额用户类型   1 个人  2 企业
  creditContractUserTypeEnum: { 1: "个人", 2: "企业" },

  // 容器状态 CREATING 创建中  RUNNING 运行中  STOPPING 停止中  STOPPED 已停止  REBOOTING 重启中  STARTING 启动中  REBUILDING 重装中  RESIZING 更配中  ERROR 错误  DELETING 删除中
  containerStatusEnum: {
    CREATING: "创建中",
    RUNNING: "运行中",
    STOPPING: "停止中",
    STOPPED: "已停止",
    REBOOTING: "重启中",
    STARTING: "启动中",
    REBUILDING: "重装中",
    RESIZING: "更配中",
    ERROR: "错误",
    DELETING: "删除中",
  },
  // 轮播图类型 1 PC 2 小程序
  bannerTypeEnum: { 1: "PC", 2: "小程序" },
  // 消息类型 1  客户表单   2  购买表单  3 推广申请  4 发票申请  5  合同申请  6 IP库预警  7 自建服务器工单 8 续费通知 9 升级通知
  messageTypeEnum: {
    1: "客户表单",
    2: "购买表单",
    3: "推广申请",
    4: "发票申请",
    5: "合同申请",
    6: "IP库预警",
    7: "自建服务器工单",
    8: "续费通知",
    9: "升级通知",
  },
  // 消息状态 1 未读 2 已读
  messageStatusEnum: { 1: "未读", 2: "已读" },
  // 工单产品类型  1 火山云引擎 2 自建服务器
  workOrderProductTypeEnum: { 1: "火山云引擎", 2: "自建服务器" },
  // 工单运维状态  1 待开通 2 已开通 3 计算中 4 已停机 5 已退款 6 即将到期
  workOrderOpsStatusEnum: {
    1: "待开通",
    2: "已开通",
    3: "计算中",
    4: "已停机",
    5: "已退款",
    6: "即将到期",
  },
  // 工单用户控制状态 1 申请开通 2 申请停机 3 申请重启 4 申请续费 5 申请销毁
  workOrderUserControlStatusEnum: {
    1: "申请开通",
    2: "申请停机",
    3: "申请重启",
    4: "申请续费",
    5: "申请销毁",
  },
  // 镜像类型   public 公共镜像  private 自定义镜像 shared 共享镜像
  imageTypeEnum: {
    public: "公共镜像",
    private: "自定义镜像",
    shared: "共享镜像",
  },
  // 时长单位
  durationUnitEnum: {
    HOUR: "小时",
    CAPACITY: "GB",
    DAY: "天",
    MONTH: "月",
    YEAR: "年",
  },
  // 产品IP状态 0 未使用 1 已使用 2 等待中
  productIpStatusEnum: {
    0: "未使用",
    1: "已使用",
    2: "等待中",
  },
};

/**
 * @description 枚举映射值
 * @param type 枚举类型
 * @param val 枚举值
 * @returns {string} 枚举映射值
 */

export const enumType = (type: keyof typeof map, val: number | string) => {
  const mapType = map[type];
  if (Array.isArray(mapType)) return mapType[val as number] || "无";
  return mapType[val as keyof typeof val] || "无";
};
