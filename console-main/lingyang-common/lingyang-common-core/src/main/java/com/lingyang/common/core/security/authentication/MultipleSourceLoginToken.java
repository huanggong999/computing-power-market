package com.lingyang.common.core.security.authentication;

import java.io.Serial;

/**
 * @Description: 登陆参数传递
 * @Author: 王小龙
 * @Date: 2024/3/18 15:06
 */
public class MultipleSourceLoginToken extends MultipleSourceToken {
    @Serial
    private static final long serialVersionUID = 2921753608561324517L;


    /**
     * 证明主体正确的凭据。这通常是一个密码，但可以是与 AuthenticationManager.调用方应填充凭据。
     * @return ： 证明 Principal
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 正在验证的主体的标识。对于带有用户名和密码的身份验证请求，这将是用户名。调用方应填充身份验证请求的主体。
     * @return ： Principal正在验证或身份验证后的已验证主体。
     */
    @Override
    public Object getPrincipal() {
        return null;
    }
}
