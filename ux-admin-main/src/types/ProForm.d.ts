interface IFormColumnsProps {
  el: TFormType;
  required?: boolean; // 是否必填 默认 true
  label: string;
  prop: string;
  itemLabelWidth?: number; // 单行宽度
  visible?: (row: TKeyValue) => boolean;
  maxLength?: number; // 输入框最大值
  radioList?: IRadioList[]; // 单选
  selectLabel?: string; // 下拉选择
  selectValue?: string; // 下拉选择
  selectList?: TKeyValue[]; // 下拉选择
  pageFn?: TPromiseFn; // 分页下拉搜索
  selectParams?: TKeyValue; // 分页下拉搜索参数
  selectMultiple?: boolean; // 下拉是否多选
  validator?: Function; // 自定义校验方法
  dateType?: TDateType; // 日期类型
  valueFormat?: string; // 绑定值的格式
  timeFormat?: string; // 展示的时间格式
  dateEnum?: string[]; //  日期搜索枚举
  limit?: number; // 限制上传图片数量
  disabled?: boolean; // 是否禁用
  //
  disabledDate?: (date: Date) => boolean;
}

type TFormType =
  | "input"
  | "textarea"
  | "phone"
  | "number"
  | "price"
  | "radio"
  | "checkbox"
  | "select"
  | "selectPage"
  | "img"
  | "imgs"
  | "date-picker"
  | "wangEditor"
  | "markdownEditor"
  | "password"
  | "slot";

// 单选框
interface IRadioList {
  label: any;
  description: string;
}

interface IRormRule {
  required?: boolean;
  trigger: "blur" | "change" | ["blur", "change"];
  validator?: Function;
  message?: string;
}
