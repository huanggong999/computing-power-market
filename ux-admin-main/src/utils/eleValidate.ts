// ? Element 常用表单校验规则

/**
 * @description: 手机号
 */
export const checkPhoneNumber = (_rule: any, value: any, callback: any) => {
  const regexp = /^(?:(?:\+|00)86)?1[3-9]\d{9}$/;
  if (!regexp.test(value)) return callback(new Error("请输入正确的手机号"));
  callback();
};

/**
 * @description: 非零的金额
 */
export const checkAmount = (_rule: any, value: any, callback: any) => {
  const regexp = /^(0|[1-9][0-9]*)(\.[0-9]{1,2})?$/;
  if (!regexp.test(value) || value == 0)
    return callback(new Error("请输入非零的金额，最多保留两位小数"));
  callback();
};
