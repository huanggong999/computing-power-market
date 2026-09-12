import { isType } from "@/utils";
import { popoverMap, PopoverType } from "@/utils/Enum";
import { useHandleData } from "./useHandleData";
/**
 *
 * @param {Object}
 * @param useTableParams.api 请求接口
 * @param useTableParams.title 表格标题
 * @param useTableParams.isPage 是否分页
 * @param useTableParams.requestAuto 是否自动请求
 * @returns
 */
export const useTable = (useTableParams: IUseTableParams) => {
  const {
    api,
    title,
    isPage = true,
    requestAuto = true,
    initParams,
  } = useTableParams;
  const state = reactive<TableStateProps>({
    tableData: [],
    pageData: { pageSize: 10, pageNo: 1, total: 1 },
    searchParam: {}, // 搜索数据
    addOrEdit: false,
    dataForm: {},
    popoverTitle: "",
    disabled: false,
    totalParam: {}, // 总参数
    proFormRef: null,
    proTableRef: null,
  });

  const pageParams = computed(() => {
    return { pageSize: state.pageData.pageSize, pageNo: state.pageData.pageNo };
  });

  // | 获取数据
  const getList = async () => {
    Object.assign(state.totalParam, initParams, isPage ? pageParams.value : {});
    const { data } = await api!({ ...state.searchParam, ...state.totalParam });
    if (!isPage) return (state.tableData = data);
    const { dataTotal, list, pageNo, pageSize } = data;
    state.tableData = list;
    updatePageData({ pageSize, pageNo, total: +dataTotal });
  };
  // | 刷新数据
  const refreshFn = () => {
    state.pageData.pageNo = 1;
    state.pageData.pageSize = 10;
    state.searchParam = {};
    getList();
  };
  // | 搜索数据
  const searchFn = () => {
    state.pageData.pageNo = 1;
    getList();
  };

  // | 重置
  const resetFn = () => {
    state.pageData.pageNo = 1;
    state.searchParam = {};
    getList();
  };

  /**
   * @description 更新分页信息
   * @param {Object} resPageTable 后台返回的分页数据
   * @return void
   * */
  const updatePageData = (resPageTable: PageData) =>
    Object.assign(state.pageData, resPageTable);

  /**
   * @param removeApi  删除方法
   * @param params 删除ID
   * @param message 提示语
   */
  const removeFn = async (
    removeApi: TPromiseFn,
    params: any = {},
    message: string = ""
  ) => {
    let msg = "删除" + `${message ? "【" + message + "】" : ""}`;
    await useHandleData(removeApi, params, msg);
    state.proTableRef && state.proTableRef.tableRef.clearSelection();
    getList();
  };

  /**
   * @description: 打开弹窗
   * @param {PopoverType} type 类型
   * @param row 参数 / 请求方法
   * @param {string | number} id  请求ID
   */
  const openPopover = async (
    type: PopoverType,
    row: {} | TPromiseFn = {},
    id?: string | number
  ) => {
    state.popoverTitle = popoverMap[type] + title;
    state.disabled = false;
    if (type === "check") state.disabled = true;
    if (isType(row) === "object")
      state.dataForm = JSON.parse(JSON.stringify(row));
    if (isType(row) === "function") {
      const { data } = await (row as TPromiseFn)(id);
      state.dataForm = data;
    }
    if (!state.dataForm.id && !!localStorage.getItem(title!)) {
      console.log("localStorage", localStorage.getItem(title!));
      state.dataForm = JSON.parse(localStorage.getItem(title!)!);
    }
    state.addOrEdit = true;
  };
  // | 关闭弹窗
  const closePopover = () => (state.addOrEdit = false);
  watchEffect(() => {
    if (!state.addOrEdit) state.dataForm = {};
  });

  /**
   * @description: 新增 / 编辑
   * @param formEl  参数校验
   * @param submitParams.addSubmitApi 新增请求 API
   * @param submitParams.editSubmitApi 编辑请求 API
   */
  const submit = (submitParams: ISubmitParams) => {
    const { addSubmitApi, editSubmitApi } = submitParams;
    if (!state.proFormRef.formRef) return;
    state.proFormRef.formRef.validate(async (valid: boolean, msg: any) => {
      if (!valid) {
        try {
          document
            .querySelector(`#Key_${Object.keys(msg)[0] || ""}`)
            ?.scrollIntoView({ behavior: "smooth" });
        } catch (e) {}
        return ElMessage.warning("请填写完整");
      }
      if (!state.dataForm.id) await addSubmitApi!(state.dataForm);
      if (!!state.dataForm.id) await editSubmitApi!(state.dataForm);
      localStorage.removeItem(title!);
      ElMessage.success("操作成功");
      getList();
      closePopover();
    });
  };

  // & 保存
  const save = () => {
    localStorage.setItem(title!, JSON.stringify(state.dataForm));
    closePopover();
  };

  if (requestAuto) getList();

  return {
    ...toRefs(state),
    getList,
    refreshFn,
    resetFn,
    searchFn,
    removeFn,
    openPopover,
    closePopover,
    submit,
    save,
  };
};
