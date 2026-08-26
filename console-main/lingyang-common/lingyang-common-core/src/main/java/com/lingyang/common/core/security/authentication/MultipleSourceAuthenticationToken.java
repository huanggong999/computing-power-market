package com.lingyang.common.core.security.authentication;

import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;

/**
 * @Description: 多用户体认证对象
 * @Author: 王小龙
 * @Date: 2024/3/15 14:49
 */
@Setter
@Getter
public class MultipleSourceAuthenticationToken extends MultipleSourceToken {
    @Serial
    private static final long serialVersionUID = 1754393402546415962L;
    /**
     * 来源
     */
    private RequestSource source;

    /**
     * tokenKey
     */
    private String tokenKey;

    /**
     * body参数
     */
    private String bodyParam;

    private MultipleSourceAuthenticationToken(LoginUserInfoDetail loginUser, boolean authenticated) {
        super(loginUser == null ? null : loginUser.getAuthorities());
        super.setDetails(loginUser);
        setAuthenticated(authenticated);
    }

    /**
     * 构建一个未授权的用户信息
     *
     * @param loginUser 登陆用户 （可为空）
     * @param <T>       用户信息类型
     * @return MultipleSourceAuthenticationToken
     */
    public static <T> MultipleSourceAuthenticationToken unauthenticated(LoginUserInfoDetail loginUser) {
        return new MultipleSourceAuthenticationToken(loginUser, false);
    }

    /**
     * 构建一个已授权的用户信息
     *
     * @param loginUser 登陆用户 （不可以为空）
     * @param <T>       用户信息类型
     * @return MultipleSourceAuthenticationToken
     */
    public static <T> MultipleSourceAuthenticationToken authenticated(LoginUserInfoDetail loginUser) {
        return new MultipleSourceAuthenticationToken(loginUser, true);
    }

    /**
     * 证明主体正确的凭据
     *
     * @return 用户id
     */
    @Override
    public Object getCredentials() {
        return getInfo().getUserId();
    }

    /**
     * 正在验证的主体的标识
     *
     * @return 用户信息
     */
    @Override
    public Object getPrincipal() {
        return getDetails();
    }

    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated() && ObjectUtils.isNotEmpty(getInfo());
    }

    /**
     * 获取用户信息
     *
     * @param <T> 用户信息
     * @return 用户信息
     */
    @SuppressWarnings("unchecked")
    public <T extends LoginUserInfoDetail> T getInfo() {
        Object principal = getPrincipal();
        if (principal instanceof LoginUserInfoDetail userInfo) {
            return (T) userInfo;
        }
        return null;
    }

    /**
     * 修改用户信息
     *
     * @param <T> 用户信息
     */
    public <T extends LoginUserInfoDetail> void setUserInfo(T t) {
        super.setDetails(t);
    }
}