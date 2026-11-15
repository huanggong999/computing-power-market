// 定义状态项通用类型
interface StatusItem {
	tip: string;
	num: number | string;
	color: string;
}
// 定义卡片类型枚举
type CardType =
	| "cloudServer"
	| "containerCluster"
	| "imageRepository"
	| "IsStorage"
	| "networkProduct";

export type ConsoleCardItem = {
	type: CardType;
	showIcon: boolean;
	currentCard: {
		title: string;
		label: string;
		count: string;
		countKey: string;
		status: StatusItem[];
	};
};
