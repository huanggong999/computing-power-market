// ! 用户相关
import request from '@/utils/request'

// | /pc/customer/register  { 注册 }
export const registerAPI = (data: IRegisterParams): Res =>
  request.put('/pc/customer/register', data)

// | /code  { 获取验证码 }
export const getCodeAPI = (
  type: TCodeType = 'SMS',
  tmsg: string,
  phone?: string
): Res => request.get(`/code?type=${type}&tmsg=${tmsg}&phone=${phone}`)

// | /login  { 登录 }
export const loginAPI = (
  data: ILoginParams,
  type: TLoginType
): Res<ILoginData> =>
  request.post('/login', data, {
    headers: { source: type },
  })

// | /pc/customer/getLoginPcCodeUrl  { 获取登录二维码 }
export const getLoginPcCodeUrlAPI = (): Res =>
  request.get('/pc/customer/getLoginPcCodeUrl')

// | /pc/customer/getUnionId?code=  { 获取unionId }
export const getUnionIdAPI = (code: string): Res =>
  request.get(`/pc/customer/getUnionId?code=${code}`)

// | /pc/customer/info  { 获取用户信息 }
export const getUserInfoAPI = (): Res<IUserInfo> =>
  request.get('/pc/customer/info')

// | /pc/customer/updateCustomerInfo  { 修改用户信息 }
export const updateUserInfoAPI = (data: any): Res<IUserInfo> =>
  request.put('/pc/customer/updateCustomerInfo', data)

// | /pc/customer/updateCustomerPhone { 修改用户手机号 }
export const updateUserPhoneAPI = (data: any): Res =>
  request.put('/pc/customer/updateCustomerPhone', data)
// | /pc/customer/getCouponList  { 获取优惠券列表 }
export const getUserCouponListAPI = (pagination?: any, data?: any): Res =>
  request.post(
    `/pc/customer/getCouponList?pageNo=${pagination.pageNo}&pageSize=${pagination.pageSize}`,
    data ? data : {}
  )
// | /pc/customer/getVoucherList { 获取代金券列表 }
export const getUserVoucherListAPI = (params?: any): Res =>
  request.get('/pc/customer/getVoucherList', { params })

// | /pc/customer/updatePassword  { 修改密码 }  IUpdatePasswordParams
export const updatePasswordAPI = (data: any): Res =>
  request.put('/pc/customer/updatePassword', data)

// | /pc/customer/updatePassword  { 重置密码 }  resetPasswordAPI
export const resetPasswordAPI = (data: any): Res =>
  request.put('/pc/customer/resetPasswordByPhone', data)

// | /pc/customer/amount/page   获取金额记录日志列表
export const getAmountListAPI = (params: any) =>
  request.get('/pc/customer/amount/page', { params })

// | /pc/customer/companyVerify {实名认证}
export const companyVerifyAPI = (data: any) =>
  request.post('/pc/customer/companyVerify', data)
