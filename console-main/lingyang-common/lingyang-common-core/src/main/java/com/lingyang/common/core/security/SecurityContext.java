package com.lingyang.common.core.security;

import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.authentication.MultipleSourceAuthenticationToken;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/15 15:29
 */
public class SecurityContext {

    /**
     * 获取登陆用户
     *
     * @param <T> 登陆用户信息类型
     * @return 登陆用户
     */
    public static <T extends LoginUserInfoDetail> T getUserInfoNo() {
        T userInfo = getAuthentication().getInfo();
        return userInfo;
    }

    /**
     * 获取登陆用户
     *
     * @param <T> 登陆用户信息类型
     * @return 登陆用户
     */
    public static <T extends LoginUserInfoDetail> T getUserInfo() {
        T userInfo = getAuthentication().getInfo();
        if (ObjectUtils.isEmpty(userInfo)) {
            throw new AuthenticationServiceException("userInfo is empty");
        }
        return userInfo;
    }

    public static MultipleSourceAuthenticationToken getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationServiceException("Authentication is null");
        }
        if (!(authentication instanceof MultipleSourceAuthenticationToken)) {
            authentication = MultipleSourceAuthenticationToken.unauthenticated(null);
            setAuthentication(authentication);
        }
        return (MultipleSourceAuthenticationToken) authentication;
    }

    public static void setAuthentication(Authentication authenticationToken) {
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public static <T extends LoginUserInfoDetail> void updateUserInfo(T newUserInfo) {
        if (getUserInfo().getUserId().equals(newUserInfo.getUserId())) {
            getAuthentication().setUserInfo(newUserInfo);
        }
    }

    public static RequestSource getSource() {
        return getAuthentication().getSource();
    }

    public static String getTokenKey() {
        return getAuthentication().getTokenKey();
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }

    public static boolean isAdmin() {
        return getUserInfo().isSuperAdmin();
    }
}
