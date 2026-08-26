package com.lingyang.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:17
 */
@Getter
@AllArgsConstructor
public enum HttpStatus{

    OK(200, "成功"),
    NOT_REQUEST_HANDLER(404, "请求地址不存在"),
    NOT_LOGIN(401, "未登录"),
    NOT_PERMISSION(403, "权限不足"),
    ERROR(500, "服务器繁忙，请稍后再试"),
    UNREGISTERED(501, "未注册"),
    FAIL(1001, "操作失败"),
    REQUEST_ERROR(1002, "错误的请求"),
    NOT_PARAMS(1002, "缺少请求参数"),
    CAPTCHA_ERROR(1003, "验证码错误或已失效"),
    HTTP_CLIENT_ERROR(1004, "第三方服务请求失败"),
    WECHAT_UNREGISTERED(1005, "微信账户已绑定有手机号"),
    WECHAT_NOT_REGISTERED(1006, "微信账户未在平台注册，请您先注册"),
    ;

    private final int code;

    private final String msg;
}
