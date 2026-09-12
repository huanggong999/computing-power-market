interface IBaseAuthParams {
  uid?: string
  code?: string
  phone?: string
  password?: string
  unionId?: string
  openId?: string
}
interface IRegisterParams extends IBaseAuthParams {
  customerName?: string
  gender?: TSex
  avatar?: string
  birthday?: string
  email?: string
  qq?: string
  remark?: string
  vcode?: string // 推广大使邀请码
  activityId?: string // 活动id
  inviterId?: string //活动邀请人id
}

interface ILoginParams extends IBaseAuthParams {
  username?: string
}

// 登录/注册
type TAuthType = 'login' | 'register'

// USERNAME_PASSWORD_LOGIN =  用户名密码登陆， SMS_LOGIN =短信登陆, WECHAT_LOGIN = 微信扫码登陆
type TLoginType = 'USERNAME_PASSWORD_LOGIN' | 'SMS_LOGIN' | 'WECHAT_LOGIN'

// SMS 短信验证码 math 数组计算 char 字符验证
type TCodeType = 'SMS' | 'math' | 'char'

//
interface Role {
  id: number
  roleName: string
  dataScope: 'ALL' | '自定义' // 假设只有这两种情况，可根据实际需求调整
  admin: boolean
}

interface Permission {
  permission: string
}

interface IUserInfo {
  userId: number | null // 用户id
  username: string
  superAdmin: boolean // 是否为超管用户
  customerName: string // 客户名称
  phone: string // 手机号-登陆账号
  balance: number // 余额
  avatar: string // 头像
  gender: TSex // 性别

  amount?: number // 可开发票额
  roleList?: Role[]
  permissions?: Permission[]
  menuIdList?: number[] // 菜单ID列表
  remark: string
  email?: string
  qq?: string
  birthday?: string // 可以考虑使用 Date 类型，但 JSON 默认不支持
  inviterId: number | null // 邀请ID
  inviterTime?: string // 邀请时间
  registerTime?: string // 注册时间
  unionId?: string
  openId?: string
  voucherBalance: number // 代金券余额
  arrearsAmount: number // 欠费金额
  creditAmount: number // 授信额
  couponNum: number // 优惠数量
  totalBalance: number // 总余额
  type: number
  realName?: string | null //  姓名（个人或法人）
  idCard?: string | null // 身份证号
  companyName?: string | null
  companyCode?: string | null
  companyAddress?: string | null
  companyImg?: string | null
  companyContactName?: string | null
  companyContactPhone?: string | null
  certStatus?: number | null
  certType?: number | null
  companyVerifyRemark?: string | null
  companyVerifyTime?: string | null
  registerTime?: string | null
}
// 包含 IUserInfo 的接口 + {token:string}
interface IUserInfoWithToken extends IUserInfo {
  token: string
}
