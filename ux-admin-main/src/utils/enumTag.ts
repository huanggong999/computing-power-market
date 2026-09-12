const map = {
  // 授信额合同 状态 1 待沟通  2 待签署 3 已签署  4 已过期  5 审核不通过
  creditContractStatusTag: [
    "",
    "warning",
    "info",
    "success",
    "danger",
    "danger",
    "danger",
    "info",
    "info",
  ],
  //  状态 OK 正常 DEACTIVATED 停用
  statusTag: { OK: "success", DEACTIVATED: "danger" },
  // 状态  1 启用  2 停用
  statusNumTag: ["", "success", "danger"],
  // 是否显示 0 是  1 否
  isShowTag: ["success", "danger"],
  // 订单 tag 颜色
  orderStatusTag: {
    UNPAID: "warning",
    PAID: "success",
    CANCELED: "primary",
    REFUNDED: "danger",
  },
  // 对公打款状态
  remitStatusTag: ["", "warning", "success", "danger"],
  // 产品状态标签 1 未开通  2 已开通  3 已过期  4 已暂停 5 已撤线
  productStatusTag: ["", "warning", "success", "danger", "primary", "info"],
  // 订单 tag 颜色
  productPayStatusTag: ["warning", "success"],
  // 审核状态
  statusAuditTag: ["", "warning", "success", "danger"],
  // 提现状态
  statusWithdrawalTag: ["", "warning", "info", "danger", "success"],
  // 企业认证状态标签
  enterpriseCertificationStatusTag: [
    "",
    "warning",
    "info",
    "success",
    "danger",
  ],
  // 消息通知状态标签 1 未读 2 已读
  messageStatusTag: ["", "warning", "success"],
  // 工单运维状态  1 待开通 2 已开通 3 计算中 4 已停机 5 已退款 6 即将到期
  workOrderStatusTag: [
    "",
    "warning",
    "success",
    "primary",
    "danger",
    "danger",
    "danger",
  ],
  // 工单用户控制状态 1 申请开通 2 申请停机 3 申请重启 4 申请续费 5 申请销毁
  workOrderUserControlStatusTag: [
    "",
    "warning",
    "danger",
    "primary",
    "info",
    "danger",
  ],
  // 产品IP状态 0 未使用 1 已使用 2 等待中
  productIpStatusTag: ["success", "danger", "warning"],
};

type PopoverType = keyof typeof map;
type TagType = "warning" | "info" | "success" | "danger" | "primary";
export const enumTag = (type: PopoverType, val: number | string): TagType => {
  const mapType = map[type];
  if (Array.isArray(mapType)) {
    const index = typeof val === "number" ? val : parseInt(val, 10);
    return (mapType[index] as TagType) || "success";
  }
  return (mapType as Record<number | string, TagType>)[val] ?? "success";
};
