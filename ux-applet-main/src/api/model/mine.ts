import http from "@/utils/request";

// | /applets/console/income/list 收支明细列表
export const consoleIncomeList = (data: TKeyValue = {}): any =>
	http.post(`/applets/console/income/list`, data);

//#region ! 充值
// | /pc/active/getCouponList { 获取充值活动列表 }
export const getActiveCouponListApi = (): any =>
	http.get(`/pc/active/getCouponList`);

// | /pc/order/createOrder { 创建订单 }
export const createOrderApi = (data: TKeyValue = {}): any =>
	http.post(`/pc/order/createOrder`, data);

// | pc/remit/account { 获取收款账号 }
export const getRemitAccountApi = (): any => http.get(`/pc/remit/account`);

// | pc/remit/add { 对公打款 }
export const remitAddApi = (data: TKeyValue = {}): any =>
	http.post(`/pc/remit/add`, data);

// | /pc/extend/config-detail { 获取推广配置详情 }
export const getExtendConfigDetailApi = (): any =>
	http.get(`/pc/extend/config-detail`);

// | /pc/extend/detail  { 获取推广详情 }
export const getExtendDetailApi = (): any => http.get(`/pc/extend/detail`);

// | /pc/extend/apply { 推广申请 }
export const extendApplyApi = (data: TKeyValue = {}): any =>
	http.post(`/pc/extend/apply`, data);

// | /pc/extend/orderPage { 分佣订单 }
export const getExtendOrderPageApi = (params: TKeyValue = {}): any =>
	http.get(`/pc/extend/orderPage`, params);

// | /pc/extend/first-page { 我的团队 }
export const getExtendFirstPageApi = (params: TKeyValue = {}): any =>
	http.get(`/pc/extend/first-page`, params);

// | /pc/extend/updateBank { 编辑推广大使信息 ==> 修改银行卡信息 }
export const extendUpdateBankApi = (data: TKeyValue = {}): any =>
	http.post(`/pc/extend/updateBank`, data);

// | /pc/extend/with  { 提现 }
export const extendWithApi = (params: TKeyValue = {}): any =>
	http.get(`/pc/extend/with`, params);

// | pc/extend/withPage { 提现记录 }
export const extendWithPageApi = (params: TKeyValue = {}): any =>
	http.get(`/pc/extend/withPage`, params);

// | /pc/extend/config-detail { 查询推广配置 }
export const extendConfigDetailApi = (): any =>
	http.get(`/pc/extend/config-detail`);

// | /pc/customer/updateCustomerInfo { 更新用户信息 }
export const updateCustomerInfoApi = (data: TKeyValue = {}): any =>
	http.put(`/pc/customer/updateCustomerInfo`, data);

// | /pc/customer/companyVerify { 提交企业认证 }
export const companyVerifyApi = (data: TKeyValue = {}): any =>
	http.post(`/pc/customer/companyVerify`, data);
