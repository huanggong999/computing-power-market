package com.lingyang.common.security.access.login;

import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.security.config.SecurityProperties;
import com.lingyang.common.security.exception.LoginException;
import com.lingyang.common.security.model.LoginParam;

import java.io.IOException;

/**
 * @Description: 登陆处理器，通过{@link SecurityProperties#getLoginUrl()}中配置
 * @Author: 王小龙
 * @Date: 2024/3/21 10:15
 */
public interface LoginHandler<T extends LoginParam> {

    /**
     * 登陆处理
     * @param bodyParam 继承{@link LoginParam}
     * @return 登陆用户
     * @throws LoginException 身份验证失败异常
     */
    LoginUserInfoDetail login(T bodyParam) throws LoginException, IOException;

    /**
     * 请求来源，可通过当前字段区分不同系统
     * @return Source
     */
    RequestSource source();

    Class<T> jsonClass();
}