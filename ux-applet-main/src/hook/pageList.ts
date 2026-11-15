interface IPageStateProps {
	dataList: any[];
	pageParams: { pageNo: number; pageSize: number }; // 初始化数据
	searchParam: TKeyValue; // 搜索数据
	totalParams: TKeyValue;
	total: number;
	loadText: string;
	triggered: boolean; // 是否开启下拉刷新
}

interface IPageParams {
	api?: (params: any) => Promise<any> | any; // 请求api
	// IsPage?: boolean; // 是否分页
	requestAuto?: boolean; // 是否自动请求
	initParams?: TKeyValue; // 默认参数
}

export const usePageList = (Params: IPageParams) => {
	const { api, initParams, requestAuto = true } = Params;
	const state = reactive<IPageStateProps>({
		dataList: [],
		pageParams: { pageNo: 1, pageSize: 15 }, // 初始化数据
		searchParam: {}, // 搜索数据
		total: 0,
		loadText: "下拉加载",
		totalParams: {},
		triggered: false,
	});

	const getList = async (IsPage = true) => {
		state.loadText = "正在加载";
		uni.showLoading({ title: "加载中", mask: true });
		Object.assign(
			state.totalParams,
			initParams,
			IsPage ? state.pageParams : {}
		);
		const { data } = await api!({
			...state.searchParam,
			...state.totalParams,
		});
		uni.hideLoading();
		if (data.list)
			state.dataList = IsPage
				? state.dataList.concat(data.list)
				: data.list;
		else state.dataList = IsPage ? state.dataList.concat(data) : data;
		state.total = +data.dataTotal;
		if (state.total) {
			state.loadText =
				state.total === state.dataList.length ? "到底了" : "下拉加载";
		} else {
			state.loadText = "到底了";
		}
	};
	if (requestAuto) getList();

	/**
	 * @param fn 刷新完成后的回调
	 */
	const pullToRefresh = (fn?: Function) => {
		onPullDownRefresh(() => {
			state.pageParams.pageNo = 1;
			state.dataList = [];
			getList(false);
			fn && fn();
			uni.stopPullDownRefresh();
		});
	};

	// 下拉触底
	onReachBottom(() => {
		if (state.loadText === "到底了") return false;
		state.pageParams.pageNo++;
		getList();
	});

	const refreshData = async () => {
		state.dataList = [];
		state.pageParams.pageNo = 1;
		state.triggered = true;
		await getList();
		state.triggered = false;
	};
	const reachBottom = () => {
		if (state.loadText === "到底了") return false;
		state.pageParams.pageNo++;
		getList();
	};
	const searchFn = () => {
		state.pageParams.pageNo = 1;
		getList(false);
	};

	return {
		...toRefs(state),
		onReachBottom,
		pullToRefresh,
		getList,
		refreshData,
		reachBottom,
		searchFn,
	};
};
