type SearchType = "input" | "select" | "selectPage" | "date-picker" | "slot";

type SearchProps = {
  el: SearchType;
  valueFormat?: string; // 绑定值的格式
  dateEnum?: string[]; //  日期搜索枚举
  dateType?: TDateType; // 日期类型
  placeholder?: string | any; // 非范围选择时的占位内容
  slotName?: string; // 插槽名
};

type FieldNamesProps = {
  label: string | number;
  value: string | number;
};
