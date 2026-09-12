/**
 * @description: 表格多选数据操作
 * @param {string} rowKey 当表格可以多选时，所指定的 id
 */
export const useSelection = (rowKey: string = "id") => {
  const isSelected = ref<boolean>(false);
  const selectedList = ref<TKeyValue[]>([]);

  // 当前选中的所有 ids 数组
  const selectedListIds = computed(() =>
    selectedList.value.map((item) => item[rowKey])
  );

  /**
   * @description: 表格多选事件
   * @param {Array} rowArr 当前选中的数据
   * @return void
   */
  const selectionChange = (rowArr: TKeyValue[]) => {
    rowArr.length > 0 ? (isSelected.value = true) : (isSelected.value = false);
    selectedList.value = rowArr;
  };
  return {
    isSelected,
    selectedList,
    selectedListIds,
    selectionChange,
  };
};
