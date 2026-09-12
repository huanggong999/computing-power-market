interface IRormRule {
  required?: boolean
  trigger: 'blur' | 'change' | ['blur', 'change']
  validator?: Function
  message?: string
}

interface IFormColumnsProps {
  label?: string
  prop: string
  besideProp?: string
  itemLabelWidth?: number // 单行宽度
  maxLength?: number // 输入框最大值
  el: TFormType
  inlinePrompt?: boolean // switch 提示文字是否显示在单行
  activeText?: string // switch 开启时，按钮文字
  inactiveText?: string // switch 关闭时，按钮文字
  required?: boolean // 是否必填 默认 true
  validator?: Function // 自定义校验方法
  placeholder?: string // 占位符
  prefixIcon?: string | Component // 左侧图标
  suffixIcon?: string | Component // 右侧图标
  codePhone?: string // 手机号 code
  authUid?: string // 权限id
  codeType?: TCodeType // 验证码类型
  tmsg?: string // 验证码类型(1:登录 2:注册)
  radioList?: IRadioList[] // 单选
  selectLabel?: string //下拉选择
  selectValue?: string //下拉选择
  selectList?: IKeyValue[] //下拉选择列表
  selectMultiple?: boolean //下拉选择是否多选

  precision?: number // 价格小数点位数
  controls?: boolean // 是否显示加减按钮
  minNumber?: number // 最小值
  numberStep?: number // 每次点击改变的间隔

  dateType?: TDateType // 日期类型
  valueFormat?: string // 绑定值的格式
  timeFormat?: string // 展示的时间格式
  dateEnum?: string[] //  日期搜索枚举
  minTime?: string // 时间选择最小值
  timeSelectStart?: string // 时间选择开始时间
  timeSelectEnd?: string // 时间选择结束时间
  timeSelectStep?: string // 时间选择步长
  tips?: string //提示语
  tipWay?: undefined | 'follow' // 提示语样式（follow为在label后）
  api?: any // 图片上传api
  styke?: any // 样式
}
type TLabelPosition = 'left' | 'right' | 'top'

// 单选框
interface IRadioList {
  label: any
  value: string | number | boolean
  disabled?: boolean
}

type TFormType =
  | 'input'
  | 'password'
  | 'code'
  | 'textarea'
  | 'switch'
  | 'phone'
  | 'number'
  | 'price'
  | 'radio'
  | 'radioButton'
  | 'select'
  | 'selectPage'
  | 'img'
  | 'imgs'
  | 'date-picker'
  | 'time-select'
  | 'wangEditor'
  | 'slot'

type TDateType =
  | 'year'
  | 'month'
  | 'date'
  | 'dates'
  | 'datetime'
  | 'week'
  | 'datetimerange'
  | 'daterange'
  | 'monthrange'
