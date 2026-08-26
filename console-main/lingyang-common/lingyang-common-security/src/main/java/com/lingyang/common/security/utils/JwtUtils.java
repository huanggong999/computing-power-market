package com.lingyang.common.security.utils;

import com.lingyang.common.core.utils.SpringUtils;
import com.lingyang.common.security.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * Jwt工具类
 *
 * @author scrm
 */
public class JwtUtils {
    private static final SecurityProperties SECURITY_PROPERTIES;

    static {
        SECURITY_PROPERTIES = SpringUtils.getBean(SecurityProperties.class);
    }
    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, SECURITY_PROPERTIES.getTokenSecret()).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(SECURITY_PROPERTIES.getTokenSecret()).parseClaimsJws(token).getBody();
    }
}
