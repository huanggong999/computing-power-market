package com.lingyang.common.security.filter.handle;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.ClassUtils;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.security.TokenService;
import com.lingyang.common.security.access.login.LoginHandler;
import com.lingyang.common.security.exception.LoginException;
import com.lingyang.common.security.filter.AbstractFilter;
import com.lingyang.common.security.model.LoginParam;
import com.lingyang.common.security.model.TokenVO;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 统一登陆
 * @Author: 王小龙
 * @Date: 2024/3/18 14:41
 */
@Slf4j
public class LoginFilter extends AbstractFilter {

    private final Map<RequestSource, LoginHandler<LoginParam>> loginHandlerMap;
    @Resource
    private TokenService tokenService;

    public LoginFilter(@Autowired(required = false) List<LoginHandler> loginHandler) {
        Map<RequestSource, LoginHandler<LoginParam>> loginHandlerMap;
        if (ObjectUtils.isEmpty(loginHandler)) {
            loginHandlerMap = Collections.emptyMap();
        } else {
            loginHandlerMap = new ConcurrentHashMap<>(loginHandler.size());
            for (LoginHandler handler : loginHandler) {
                loginHandlerMap.put(handler.source(), handler);
            }
        }
        this.loginHandlerMap = loginHandlerMap;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        LoginHandler<LoginParam> loginHandler = loginHandlerMap.get(SecurityContext.getAuthentication().getSource());
        if (ObjectUtils.isEmpty(loginHandler)) {
            filterChain.doFilter(request, response);
            return;
        }
        LoginParam param;
        Class<LoginParam> paramClass = loginHandler.jsonClass();
        JSONObject jsonObject;
        if (ObjectUtils.isEmpty(paramClass)) {
            param = null;
        } else {
            jsonObject = JSONObject.parseObject(ServletUtils.getBodyParam(request));
            if (ObjectUtils.isNotEmpty(jsonObject)) {
                param = jsonObject.to(paramClass);
            } else {
                param = ClassUtils.newInstance(paramClass);
            }
        }
        LoginUserInfoDetail login;
        try {
            login = loginHandler.login(param);
        } catch (LoginException loginException) {
            // 处理登陆
            HttpStatus httpStatus = loginException.getHttpStatus();
            Result<Object> error;
            if (httpStatus != null ) {
                error = Result.result(httpStatus);
            }else {
                error = Result.error(loginException.getMessage());
            }
            ServletUtils.response(response, error);
            return;
        }
        if (login == null ) {
            ServletUtils.response(response, Result.result(HttpStatus.NOT_REQUEST_HANDLER));
            return;
        }
        // 删除之前的token
        if (!getSecurityProperties().getRepeatedLogin()) {
            tokenService.clearToken(SecurityContext.getAuthentication().getSource(), login.getUserId());
        }
        TokenVO token = tokenService.createToken(login);
        // 处理登陆
        ServletUtils.response(response, Result.success(token));
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return !request.getMethod().equals(HttpMethod.POST.name())
                || !request.getServletPath().equals(getSecurityProperties().getLoginUrl())
                || ObjectUtils.isEmpty(SecurityContext.getAuthentication().getSource());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 3;
    }
}