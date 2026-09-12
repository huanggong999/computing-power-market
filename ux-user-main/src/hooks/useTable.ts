/**
 *
 * @param useTableParams.api 请求接口
 * @param useTableParams.title 表格标题
 * @param useTableParams.isPage 是否分页
 * @param useTableParams.initParams 初始化参数
 * @param useTableParams.requestAuto 是否自动请求数据
 */
export const useTable = (useTableParams: IUseTableParams) => {
  // title,
  const {
    requestApi,
    isPage = true,
    requestAuto = true,
    initParams,
  } = useTableParams

  const state = reactive<IStateProps>({
    tableData: [], // 表格数据
    pageData: { pageSize: 10, pageNo: 1, total: 1 },
    totalParam: {},
    searchParam: {},
  })
  // | 搜索数据
  const searchFn = () => {
    console.log('state', state)
    state.pageData.pageNo = 1
    getList()
  }
  const pageParams = computed(() => ({
    pageSize: state.pageData.pageSize,
    pageNo: state.pageData.pageNo,
  }))
  const getList = async (other?: boolean, signal?: any) => {
    Object.assign(state.totalParam, initParams, isPage ? pageParams.value : {})

    const { data } = await requestApi!(
      {
        ...state.searchParam,
        ...state.totalParam,
      },
      signal
    )
    if (!isPage) return (state.tableData = data)
    const { dataTotal, list, pageNo, pageSize } = data
    state.tableData = list ? list : data
    updatePageData({ pageSize, pageNo, total: +dataTotal })
    if (other) return data
  }
  // | 重置
  const resetFn = () => {
    state.pageData.pageNo = 1
    state.searchParam = {}
    getList()
  }

  /**
   * @description 更新分页信息
   * @param {Object} resPageTable 后台返回的分页数据
   * @return void
   * */
  const updatePageData = (resPageTable: PageData) =>
    Object.assign(state.pageData, resPageTable)
  if (requestAuto) getList()
  return {
    ...toRefs(state),
    getList,
    searchFn,
    resetFn,
    updatePageData,
    pageParams,
  }
}
