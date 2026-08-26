package com.lingyang.common.core.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/18 15:06
 */
public abstract class MultipleSourceToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 2876618482339570265L;

    public MultipleSourceToken() {
        super(null);
    }

    public MultipleSourceToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }
}