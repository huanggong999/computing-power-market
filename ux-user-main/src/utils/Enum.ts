const map = {
  statusMap: {
    WAIT_PAY: "statusMap.waitPay.", // 待支付 -> 等待支付  （支付 / 取消）
    WAIT_RECEIVE: "statusMap.waitReceive", // 待接收 -> 未接取任务 （接取任务）
    WAIT_CONFIRM: "statusMap.waitConfirm", // 待确认 -> 等发布者确认 （通过报名/驳回报名）
    WAIT_COMPLETE: "statusMap.waitComplete", // 待完成 -> 等用户完成 （上传任务）
    WAIT_CHECK: "statusMap.waitCheck", //  待验收 - 等用户验收 （结束任务）
    COMPLETE: "statusMap.complete", // 已完成
    CANCEL: "statusMap.cancel", // 已取消
  },
  // 交易类型（ recharge  充值， withdraw  提现， taskPay  任务支付  taskIncome  任务报酬  taskCancel: 任务取消 ）
  transactionType: {
    recharge: "transactionType.topUp",
    withdraw: "transactionType.withdrawal",
    taskPay: "transactionType.taskPayment",
    taskIncome: "transactionType.taskRewards",
    taskCancel: "transactionType.taskCancellation",
  },

  // 优惠券类型
  couponType: { FULL_REDUCTION: "满减卷" },
  // 订单类型
  orderTypeEnum: {
    NEW_RESOURCE: "新购资源订单",
    RENEW_RESOURCE: "续费资源订单",
    BALANCE: "余额充值订单",
    PRODUCT: "网络产品订单",
    RENEW_PRODUCT: "网络产品续费订单",
    UPGRADE_PRODUCT: "网络产品升级订单",
  },
  // 订单状态  UNPAID 未支付  PAID 已支付  CANCELED 已取消  REFUNDED 已退款
  orderStatusEnum: {
    UNPAID: "待支付",
    PAID: "已支付",
    CANCELED: "已取消",
    REFUNDED: "已退款",
  },
  // 订单 tag 颜色
  orderStatusTag: {
    UNPAID: "warning",
    PAID: "success",
    CANCELED: "",
    REFUNDED: "danger",
  },

  // 计费类型  POSTPAID_BY_HOUR 按量付费  POSTPAID_BY_MONTH 包月  POSTPAID_BY_YEAR 包年
  billingTypeEnum: {
    POSTPAID_BY_HOUR: "按量付费",
    POSTPAID_BY_MONTH: "包月",
    POSTPAID_BY_YEAR: "包年",
    ONE_PAY: "一次性付费",
  },
  // 资源折扣比列 资源类型( ECS 服务器   CLOUD_STORAGE 云存储  CLOUD_IMAGE 云镜像  CLOUD_OBJECT_STORAGE 对象存储  CLOUD_NETWORK 网络 CONTAINER 容器 )
  resourceDiscountEnum: {
    ECS: "服务器折扣",
    CLOUD_STORAGE: "云存储折扣",
    AGIC: "AGIC产品",
    CLOUD_IMAGE: "云镜像折扣",
    CLOUD_OBJECT_STORAGE: "对象存储折扣",
    CLOUD_NETWORK: "网络折扣",
    CONTAINER: "容器折扣",
  },
  // 交易类型  RECHARGE 充值  PLATFORM_GRANT 平台发放  REFUND 退款  PAY_DISCOUNT 支付抵扣
  transactionTypeEnum: {
    RECHARGE: "充值",
    PLATFORM_GRANT: "平台发放",
    REFUND: "退款",
    PAY_DISCOUNT: "抵扣",
  },
  // 在线支付方式
  onlinePaymentEnum: {
    ALI_PAY: "支付宝支付",
    WECHAT_PAY: "微信支付",
  },
  actualStatusEnum: {
    1: "未开通",
    2: "运行中",
    3: "已过期",
    4: "已暂停",
    5: "已撤线",
  },
  expireStatusEnum: {
    0: "1天内到期",
    1: "1天到期",
    2: "2天到期",
    3: "3天到期",
    4: "4天到期",
    5: "5天到期",
    6: "6天到期",
    7: "7天到期",
    8: "运行中",
  },
  actualStatusTag: {
    1: "warning",
    2: "success",
    3: "info",
    4: "primary",
    5: "danger",
  },
  // 支付状态
  payStatusEnum: {
    0: "未支付",
    1: "已支付",
  },
  payStatusTag: {
    0: "info",
    1: "success",
  },
  certStatusEnum: ["", "未认证", "认证中", "已认证", "认证失败"],
};

/**
 * @description 枚举映射值
 * @param type 枚举类型
 * @param val 枚举值
 * @returns {string} 枚举映射值
 */

export const enumType = (type: keyof typeof map, val: number | string) => {
  const mapType = map[type];
  if (Array.isArray(mapType)) return mapType[val as number] || "---";
  return mapType[val as keyof typeof val] || "---";
};
