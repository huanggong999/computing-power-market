package com.lingyang.common.security;

import com.lingyang.common.cache.CacheService;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.config.SecurityProperties;
import com.lingyang.common.security.model.TokenVO;
import com.lingyang.common.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/8 10:26
 */
@Service
public class TokenService {
    public static final String TOKEN_KEY = "login_user_token_uid";
    public static final String DETAILS_USER_ID = "login_user_id";
    public static final String LOGIN_USER_CACHE_VALID_PREFIX = "login_user_token_keys:";
    public static final String AUTHORIZATION_HEAD = "authorization";
    public static final String SOURCE_KEY = "source";

    public static final String AUTHORIZATION_PREFIX = "Bearer ";

    @Resource
    private CacheService cacheService;

    @Resource
    private SecurityProperties securityProperties;

    public <T extends LoginUserInfoDetail> TokenVO createToken(T userInfo) {
        String token = IdUtils.simpleUUID();
        Long tokenExpireTime = securityProperties.getTokenExpireTime();
        RequestSource source = SecurityContext.getAuthentication().getSource();
        if (ObjectUtils.isEmpty(source)) {
            Result.buildError(HttpParamsException.class,"未知请求");
            return null;
        }
        Map<String, Object> claimsMap = new HashMap<>(3);
        claimsMap.put(TOKEN_KEY, token);
        claimsMap.put(SOURCE_KEY, source.getSourceKey());
        claimsMap.put(DETAILS_USER_ID, new String(Base64.getEncoder().encode(userInfo.getUserId().toString().getBytes(StandardCharsets.UTF_8))));
        TokenVO tokenVO = TokenVO.builder()
                .token(JwtUtils.createToken(claimsMap))
                .expTime(tokenExpireTime)
                .build();
        cacheService.setCacheObject(getCacheKey(source, token), userInfo, tokenExpireTime, TimeUnit.SECONDS);
        return tokenVO;
    }

    /**
     * 验证token
     *
     * @param token token
     * @return Claims 载荷
     * @throws AuthenticationException 认证异常
     */
    public Claims checkToken(String token) throws AuthenticationException {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationServiceException("token is empty");
        }
        if (token.startsWith(AUTHORIZATION_PREFIX)) {
            token = token.replaceFirst(TokenService.AUTHORIZATION_PREFIX, "");
        }
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token);
        } catch (Throwable e) {
            throw new AuthenticationServiceException("parseToken error ", e);
        }
        if (claims == null) {
            throw new AuthenticationServiceException("claims is null ");
        }
        return claims;
    }

    /**
     * 获取用户信息
     *
     * @param claims 载荷
     * @return 用户信息
     */
    public <T extends LoginUserInfoDetail> T getUserInfo(Claims claims) throws AuthenticationException {
        String tokenKey = claims.get(TOKEN_KEY, String.class);
        RequestSource source = RequestSource.getSource(claims.get(SOURCE_KEY, String.class));
        if (ObjectUtils.isEmpty(source)) {
            throw new AuthenticationServiceException("source is empty ");
        }
        T authentication = cacheService.getCacheObject(getCacheKey(source, tokenKey));
        if (authentication == null) {
            throw new AuthenticationServiceException("authentication is null");
        }
        return authentication;
    }

    /**
     * 获取用户信息
     *
     * @param token token
     * @return 登陆用户信息
     * @throws AuthenticationException 认证异常
     */
    public <T extends LoginUserInfoDetail> T getUserInfo(String token) throws AuthenticationException {
        return getUserInfo(checkToken(token));
    }


    private String getCacheKey(RequestSource source, String tokenKey) {
        return LOGIN_USER_CACHE_VALID_PREFIX + source.getSourceKey() + ":" + tokenKey;
    }

    public void clearToken(RequestSource requestSource, Long userId) {
        Optional.of(cacheService.keys(getCacheKey(requestSource, "*")))
                .ifPresent(tokenKeys -> {
                    for (String tokenKey : tokenKeys) {
                        LoginUserInfoDetail oldLoginUserInfo = cacheService.getCacheObject(tokenKey);
                        if (oldLoginUserInfo != null) {
                            if (oldLoginUserInfo.getUserId().equals(userId)) {
                                cacheService.deleteObject(tokenKey);
                            }
                        }

                    }
                });
    }

    /**
     * 刷新token
     *
     * @param source      用户来源
     * @param newUserInfo 新的用户信息
     * @param <T>         用户信息
     */
    public <T extends LoginUserInfoDetail> void refreshToken(RequestSource source, T newUserInfo) {
        String cacheKey = getCacheKey(source, SecurityContext.getTokenKey());
        Optional.of((LoginUserInfoDetail) cacheService.getCacheObject(cacheKey))
                .ifPresent(loginUserInfoDetail -> {
                    cacheService.setCacheObject(cacheKey, newUserInfo, cacheService.getExpire(cacheKey, TimeUnit.SECONDS), TimeUnit.SECONDS);
                    SecurityContext.updateUserInfo(newUserInfo);
                });
    }
}