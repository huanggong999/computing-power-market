/** @description: 单选类型  */

// 菜单类型 M 目录 C 菜单 F 按钮
export const menuTypeEnum: IRadioList[] = [
  { label: "M", description: "目录" },
  { label: "C", description: "菜单" },
  { label: "F", description: "按钮" },
];

//  是否  0 是 1 否
export const yesOrNoEnum: IRadioList[] = [
  { label: 0, description: "是" },
  { label: 1, description: "否" },
];

// 是否 1 是 0 否
export const isShowEnum: IRadioList[] = [
  { label: 0, description: "否" },
  { label: 1, description: "是" },
];

//  是否首页展示 1 是 2 否
export const isHomeEnum: IRadioList[] = [
  { label: 1, description: "是" },
  { label: 2, description: "否" },
];

// 状态 1 正常 2 停用
export const statusNumberEnum: IRadioList[] = [
  { label: 1, description: "正常" },
  { label: 2, description: "停用" },
];

// 菜单状态 OK 正常 DEACTIVATED 停用
export const StatusEnum: IRadioList[] = [
  { label: "OK", description: "正常" },
  { label: "DEACTIVATED", description: "停用" },
];

// 菜单显示状态 OK 显示 DEACTIVATED 隐藏
export const menuVisibleEnum: IRadioList[] = [
  { label: "OK", description: "显示" },
  { label: "DEACTIVATED", description: "隐藏" },
];

// 菜单 是否缓存 0 缓存 1 不缓存
export const menuIsCacheEnum: IRadioList[] = [
  { label: 0, description: "缓存" },
  { label: 1, description: "不缓存" },
];
// 性别 MAN 男 WOMAN 女 UNKNOWN 未知
export const sexRadioEnum: IRadioList[] = [
  { label: "MAN", description: "男" },
  { label: "WOMAN", description: "女" },
  { label: "UNKNOWN", description: "未知" },
];
// 优惠卷类型  FULL_REDUCTION 满减卷
export const couponTypeRadioEnum: IRadioList[] = [
  { label: "FULL_REDUCTION", description: "满减券" },
];
// 优惠券领取条件  UNCONDITIONAL 无条件领取 NEW_USER 新人领取
export const couponConditionRadioEnum: IRadioList[] = [
  { label: "UNCONDITIONAL", description: "无条件领取" },
  { label: "NEW_USER", description: "新人领取" },
];
// 优惠券使用范围  ALL 全部  SERVER 服务器  CONTAINER 容器  IMAGE_REPOSITORY 镜像仓库  OBJECT_STORAGE 对象存储   NETWORK 网络
export const couponScopeRadioEnum: IRadioList[] = [
  { label: "ALL", description: "全部" },
  { label: "SERVER", description: "服务器" },
  { label: "CONTAINER", description: "容器" },
  { label: "IMAGE_REPOSITORY", description: "镜像仓库" },
  { label: "OBJECT_STORAGE", description: "对象存储" },
  { label: "NETWORK", description: "网络" },
  { label: "AGIC", description: "AGI-C" },
];
// 轮播图位置 1 首页
export const bannerPositionEnum: IRadioList[] = [
  { label: 1, description: "首页" },
  { label: 2, description: "AGI-C" },
  { label: 3, description: "合作中心" },
];
// 轮播图跳转位置 1 ACTIVITY 活动中心 2 COUPON 优惠券 3 BUDDY 合作伙伴
export const bannerJumpPositionEnum: IRadioList[] = [
  { label: "ACTIVITY", description: "活动中心" },
  { label: "COUPON", description: "优惠券" },
  { label: "BUDDY", description: "合作伙伴" },
];

// 服务器类型   GENERAL_COMPUTE 通用型计算  COMPUTE 计算型  GENERAL 通用型  GPU GPU
export const serverTypeEnum: IRadioList[] = [
  { label: "GENERAL_COMPUTE", description: "通用型" },
  { label: "COMPUTE", description: "计算型" },
  { label: "GENERAL", description: "内存型" },
  { label: "GPU", description: "GPU" },
];
// 1 火山云引擎 2 自建服务器
export const productTypeEnum: IRadioList[] = [
  { label: 1, description: "火山云引擎" },
  { label: 2, description: "自建服务器" },
];

// 产品规格
export const productSpecificationEnum: IRadioList[] = [
  { label: 1, description: "开启带宽(M)" },
  { label: 2, description: "关闭带宽(M)" },
];

// 轮播图类型 1 PC 2 小程序
export const bannerTypeEnum: IRadioList[] = [
  { label: 1, description: "PC" },
  { label: 2, description: "小程序" },
];

// 产品IP状态 0 未使用 1 已使用  2 等待中
export const productIpStatusEnum: IRadioList[] = [
  { label: 0, description: "未使用" },
  { label: 1, description: "已使用" },
  { label: 2, description: "等待中" },
];
