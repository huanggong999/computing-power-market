type TypeProps = "index" | "selection" | "expand" | "radio" | "none";

interface ColumnProps {
  prop: string;
  label: string;
  isSearchLabel?: boolean; // 是否展示搜索label
  type?: TypeProps;
  align?: left | center | right;
  slot?: boolean;
  width?: number;
  fixed?: "left" | "right" | boolean;
  search?: SearchProps; // 搜索项配置
  fieldNames?: FieldNamesProps; // 指定 label && value 的 key 值
  enum?: ((params?: any) => Res<any>) | any[]; // 下拉搜索
  pageFn?: TPromiseFn; // 分页下拉搜索
  noShow?: boolean; // 不展示 仅搜索
  value?: (row: any) => string | (() => Object); // 插槽数据
}
