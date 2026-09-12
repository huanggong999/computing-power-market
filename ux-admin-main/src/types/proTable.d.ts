type TypeProps = "index" | "selection" | "expand" | "radio" | "none";

interface ColumnProps {
  prop: string;
  label: string;
  type?: TypeProps;
  align?: left | center | right;
  slot?: boolean;
  width?: number;
  search?: SearchProps; // 搜索项配置
  fieldNames?: FieldNamesProps; // 指定 label && value 的 key 值
  enum?: ((params?: any) => Res<any>) | any[] | import("vue").Ref<any[]>; // 下拉搜索
  pageFn?: TPromiseFn; // 分页下拉搜索
  noShow?: boolean; // 不展示 仅搜索
  value?: (row: any) => string | (() => Object); // 插槽数据
  fixed?: "left" | "right" | boolean;
}

type TDateType =
  | "year"
  | "month"
  | "date"
  | "dates"
  | "datetime"
  | "week"
  | "datetimerange"
  | "daterange"
  | "monthrange";

type SearchProps = {
  el: SearchType;
  key?: string; // 搜索参数名
  valueFormat?: string; // 绑定值的格式
  dateEnum?: string[]; //  日期搜索枚举
  dateType?: TDateType; // 日期类型
  placeholder?: string | any; // 非范围选择时的占位内容
  slotName?: string; // 插槽名
};

type SearchType = "input" | "select" | "selectPage" | "date-picker" | "slot";

type FieldNamesProps = {
  label: string | number;
  value: string | number;
};

interface PageData {
  pageNo: number;
  pageSize: number;
  total: number;
}

interface TableStateProps {
  tableData: any[]; // 表格数据
  totalParam: TKeyValue; // 总搜索数据
  pageData: PageData; // 分页数据
  searchParam: TKeyValue; // 搜索数据
  addOrEdit: boolean; // 新增/编辑状态
  dataForm: TKeyValue; // 新增/编辑数据
  popoverTitle: string; // 弹窗标题
  disabled: boolean; // 查看
  proFormRef: any; // 表单ref
  proTableRef: any; // 表格ref
}

interface IUseTableParams {
  api?: (params: any) => Promise<any> | any;
  title?: string;
  isPage?: boolean;
  requestAuto?: boolean;
  initParams?: TKeyValue;
}

interface ISubmitParams {
  addSubmitApi?: TPromiseFn;
  editSubmitApi?: TPromiseFn;
}
