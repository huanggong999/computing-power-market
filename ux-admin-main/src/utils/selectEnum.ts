/** * @description 下拉选择枚举 */

// 状态  OK  正常 DEACTIVATED  停用
export const statusSelectEnum: FieldNamesProps[] = [
  { value: "OK", label: "正常" },
  { value: "DEACTIVATED", label: "停用" },
];
// 状态  0  正常 1  停用
export const statusSelectNumEnum: FieldNamesProps[] = [
  { value: "0", label: "正常" },
  { value: "1", label: "停用" },
];
// 状态  1  启用 2  停用
export const rechargeActivitiesSelectEnum: FieldNamesProps[] = [
  { value: "1", label: "启用" },
  { value: "2", label: "停用" },
];
// 是否首页展示 1  是  2  否
export const isHomeSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "是" },
  { value: 2, label: "否" },
];
// 订单类型  NEW_RESOURCE: "新购资源订单"  RENEW_RESOURCE: "续费资源订单", BALANCE: "余额充值订单",
export const orderTypeSelectEnum: FieldNamesProps[] = [
  { value: "NEW_RESOURCE", label: "新购资源订单" },
  { value: "RENEW_RESOURCE", label: "续费资源订单" },
  { value: "BALANCE", label: "余额充值订单" },
  { value: "PRODUCT", label: "AGI-C购买订单" },
];
// 订单状态  UNPAID 未支付  PAID 已支付  CANCELED 已取消  REFUNDED 已退款
export const orderStatusSelectEnum: FieldNamesProps[] = [
  { value: "UNPAID", label: "未支付" },
  { value: "PAID", label: "已支付" },
  { value: "CANCELED", label: "已取消" },
  { value: "REFUNDED", label: "已退款" },
];

// 服务器类型  GENERAL_COMPUTE 通用型计算  COMPUTE 计算型  GENERAL 通用型  GPU = GPU;
export const serverTypeSelectEnum: FieldNamesProps[] = [
  { value: "GENERAL_COMPUTE", label: "通用型" },
  { value: "COMPUTE", label: "计算型" },
  { value: "GENERAL", label: "内存型" },
  { value: "GPU", label: "GPU" },
];
// 地域
// "cn-beijing": "华北2（北京）",
// "cn-shanghai": "华东2（上海）",
// "cn-guangzhou": "华南1（广州）",
// "ap-southeast-1": "亚太东南（柔佛）",
// "cn-hongkong": "香港",
export const regionSelectEnum: FieldNamesProps[] = [
  { value: "CN_BEIJING", label: "华北2（北京）" },
  { value: "CN_SHANGHAI", label: "华东2（上海）" },
  { value: "CN_GUANGZHOU", label: "华南1（广州）" },
  // { value: "AP_SOUTHEAST_1", label: "亚太东南（柔佛）" },
  { value: "CN_HONGKONG", label: "香港" },
  //
  { value: "CN_SHENZHEN", label: "华南（深圳）" },
  { value: "CN_SHAOGUAN", label: "华南（韶关）" },
  { value: "CN_GUIYANG", label: "贵州（贵阳）" },
  // { value: "", label: "" },
  // { value: "", label: "" },
  // { value: "", label: "" },
  // { value: "", label: "" },
  // { value: "", label: "" },

  // { value: "cn-beijing", label: "华北2（北京）" },
  // { value: "cn-shanghai", label: "华东2（上海）" },
  // { value: "cn-guangzhou", label: "华南1（广州）" },
  // { value: "ap-southeast-1", label: "亚太东南（柔佛）" },
  // { value: "cn-hongkong", label: "香港" },
];

// 返佣订单用户等级  1 新用户  2 激活用户  3 老用户）
export const rebateOrderLevelSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "新用户" },
  { value: 2, label: "激活用户" },
  { value: 3, label: "老用户" },
];
// 活动状态 1 未开始 2 进行中 3 已结束
export const activityStatusSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "未开始" },
  { value: 2, label: "进行中" },
  { value: 3, label: "已结束" },
];

// 对公打款状态  1 待核实  2 已打款   3  未打款
export const remitStatusSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "待核实" },
  { value: 2, label: "已打款" },
  { value: 3, label: "未打款" },
];

// 企业认证状态  1 未提交， 2 审核中， 3 已通过，4 未通过
export const enterpriseCertificationStatusSelectEnum: FieldNamesProps[] = [
  { value: 2, label: "审核中" },
  { value: 3, label: "已通过" },
  { value: 4, label: "未通过" },
];

export const volcanoCloudServerAvailabilityZone: FieldNamesProps[] = [
  { value: "CN_BEIJING", label: "华北2（北京）" },
  { value: "CN_SHANGHAI", label: "华东2（上海）" },
];

// 云服务器可用区
export const ecsAvailableZoneSelectEnum: FieldNamesProps[] = [
  // { value: "华北2（北京）", label: "华北2（北京）" },
  // { value: "华东2（上海）", label: "华东2（上海）" },
  { value: "CN_BEIJING", label: "华北2（北京）" },
  { value: "CN_SHANGHAI", label: "华东2（上海）" },
  //
  { value: "CN_GUANGZHOU", label: "华南（广州）" },
  { value: "CN_SHENZHEN", label: "华南（深圳）" },
  { value: "CN_SHAOGUAN", label: "华南（韶关）" },
  { value: "CN_GUIYANG", label: "贵州（贵阳）" },
  { value: "CN_ZHONGWEI", label: "宁夏（中卫）" },
  { value: "CN_HUHEHAOTE", label: "内蒙（呼和浩特）" },
  { value: "CN_HONGKONG", label: "中国（香港）" },
  { value: "CN_TAIWAN", label: "中国（台湾）" },
  { value: "JPN_TOKYO", label: "日本（东京）" },
  { value: "VNM_HENEI", label: "越南（河内）" },
  { value: "USA_LOS_ANGELES", label: "美国（洛杉矶）" },
  { value: "MALAYSIA_JOHOR", label: "马来西亚（柔佛）" },
];

// 轮播图类型 1 PC 2 小程序
export const bannerTypeSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "PC" },
  { value: 2, label: "小程序" },
];
// 消息通知状态 1 未读 2 已读
export const messageStatusSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "未读" },
  { value: 2, label: "已读" },
];

// 1 火山云引擎 2 自建服务器
export const productTypSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "火山云引擎" },
  { value: 2, label: "自建服务器" },
];

// 产品状态  1 未开通  2 运行中  3 已过期  4 已暂停 5 已撤线
export const productStatusSelectEnum: FieldNamesProps[] = [
  { value: 1, label: "未开通" },
  { value: 2, label: "运行中" },
  { value: 3, label: "已过期" },
  { value: 4, label: "已暂停" },
  { value: 5, label: "已撤线" },
];

// 产品IP状态 0 未使用 1 已使用 2 等待中
export const productIpStatusSelectEnum: FieldNamesProps[] = [
  { value: 0, label: "未使用" },
  { value: 1, label: "已使用" },
  { value: 2, label: "等待中" },
];
// 产品支付状态 0 未支付 1 已支付
export const productPayStatusSelectEnum: FieldNamesProps[] = [
  { value: 0, label: "未支付" },
  { value: 1, label: "已支付" },
];
