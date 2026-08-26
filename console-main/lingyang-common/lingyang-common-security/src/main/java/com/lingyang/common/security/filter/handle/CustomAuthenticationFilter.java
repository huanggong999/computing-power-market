package com.lingyang.common.security.filter.handle;

import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.authentication.MultipleSourceAuthenticationToken;
import com.lingyang.common.core.utils.UrlUtils;
import com.lingyang.common.security.TokenService;
import com.lingyang.common.security.filter.AbstractFilter;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import reactor.util.annotation.NonNull;

import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/14 18:33
 */
@Slf4j
public class CustomAuthenticationFilter extends AbstractFilter {
    @Resource
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            MultipleSourceAuthenticationToken authentication = SecurityContext.getAuthentication();
            Claims claims = tokenService.checkToken(request.getHeader(TokenService.AUTHORIZATION_HEAD));
            authentication.setSource(RequestSource.getSource(claims.get(TokenService.SOURCE_KEY, String.class)));
            authentication.setTokenKey(claims.get(TokenService.TOKEN_KEY, String.class));
            authentication.setDetails(tokenService.getUserInfo(claims));
            authentication.setAuthenticated(true);
            SecurityContext.setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }catch (AuthenticationException authenticationException) {
            if (UrlUtils.matches(getSecurityProperties().getIgnores(), request.getServletPath())) {
                filterChain.doFilter(request, response);
            }else {
                throw authenticationException;
            }
        }
    }


//    @Override
//    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
//        return UrlUtils.matches(getSecurityProperties().getIgnores(), request.getServletPath());
//    }

    @Override
    public int getOrder() {
        return 0;
    }
}