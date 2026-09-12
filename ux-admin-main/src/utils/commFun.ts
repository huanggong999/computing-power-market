/**
 * @description 详情描述
 * @param {Array} list 描述列表
 * @param {Object} map 数据源
 * @returns {Array}
 */
export const descriptionEnum = (list: IDescriptionsItem[], map: any) => {
  list.forEach((item: IDescriptionsItem) => {
    if (!item.slot) item.value = map[item.label] || "--";
  });
  return list;
};
