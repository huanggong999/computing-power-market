const map = {
	sourceType: {
		ECS: "服务器折扣",
		CLOUD_STORAGE: "云存储折扣",
		AGIC: "AGIC产品",
		CLOUD_IMAGE: "云镜像折扣",
		CLOUD_OBJECT_STORAGE: "对象存储折扣",
		CLOUD_NETWORK: "网络折扣",
		CONTAINER: "容器折扣",
	},
	// 计费类型  POSTPAID_BY_HOUR 按量付费  POSTPAID_BY_MONTH 包月  POSTPAID_BY_YEAR 包年
	billingTypeEnum: {
		POSTPAID_BY_HOUR: "按量付费",
		POSTPAID_BY_MONTH: "包月",
		POSTPAID_BY_YEAR: "包年",
		ONE_PAY: "一次性付费",
	},
	// 订单状态  UNPAID 未支付  PAID 已支付  CANCELED 已取消  REFUNDED 已退款
	orderStatusEnum: {
		UNPAID: "待支付",
		PAID: "已支付",
		CANCELED: "已取消",
		REFUNDED: "已退款",
	},
	// 交易类型  RECHARGE = 充值 PLATFORM_GRANT = 平台发放  REFUND = 退款  PAY_DISCOUNT = 支付抵扣
	tradeTypeEnum: {
		RECHARGE: "充值",
		PLATFORM_GRANT: "平台发放",
		REFUND: "退款",
		PAY_DISCOUNT: "支付抵扣",
	},
};
type PopoverType = keyof typeof map;
export const enumType = (type: PopoverType, val: number | string): string => {
	const mapType = map[type];
	if (Array.isArray(mapType)) {
		const index = typeof val === "number" ? val : parseInt(val, 10);
		return mapType[index] || "--";
	}
	return (mapType as Record<string, string>)[val] ?? "--";
};
