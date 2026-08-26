package com.lingyang.common.security.filter.handle;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.ValidateCodeResult;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.security.filter.AbstractFilter;
import com.lingyang.common.security.utils.ValidateCodeUtils;
import com.lingyang.common.security.validate.ValidateCodeHandlerInterface;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 验证码过滤器
 * @Author: 王小龙
 * @Date: 2024/3/18 17:28
 */
@Slf4j
@ConditionalOnProperty(value = "security.captcha.enabled", havingValue = "true")
public class ValidateCodeFilter extends AbstractFilter {

    private final Map<String , ValidateCodeHandlerInterface<Object>> validateCodeHandlerInterfaceMap;

    public ValidateCodeFilter(List<ValidateCodeHandlerInterface> validateCodeHandlerInterfaces) {
        Objects.requireNonNull(validateCodeHandlerInterfaces, " validateCodeHandlerInterfaces is null");
        validateCodeHandlerInterfaceMap = new ConcurrentHashMap<>(validateCodeHandlerInterfaces.size());
        for (ValidateCodeHandlerInterface validateCodeHandlerInterface : validateCodeHandlerInterfaces) {
            validateCodeHandlerInterfaceMap.put(validateCodeHandlerInterface.type().getTypeName(), validateCodeHandlerInterface);
        }
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String type = request.getParameter("type");
        if (StringUtils.isEmpty(type)) {
            filterChain.doFilter(request, response);
            return;
        }
        ValidateCodeHandlerInterface<Object> validateCodeHandler = validateCodeHandlerInterfaceMap.get(type);
        if (ObjectUtils.isEmpty(validateCodeHandler)) {
            filterChain.doFilter(request, response);
            return;
        }
        Class<?> paramClass = validateCodeHandler.jsonClass();
        Object param = null;
        if (paramClass != null) {
            Map<String, String> paramMap = ServletUtils.getParamMap(request);
            if (paramClass.equals(String.class)) {
                param = JSONObject.toJSONString(paramMap);
            }else if (paramClass.equals(Map.class)) {
                param = paramMap;
            }else {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(paramMap));
                if (paramClass.equals(JSONObject.class)) {
                    param = jsonObject;
                }else {
                    param = jsonObject.to(paramClass);
                }
            }
        }
        Long expireTime = getSecurityProperties().getCaptcha().getExpireTime();
        ValidateCodeResult validateCodeResult = validateCodeHandler.handlerCode(param);
        validateCodeResult.setExpireTime(expireTime);
        ValidateCodeUtils.setValidateCode(validateCodeResult.getUid(), validateCodeResult.getCode(), expireTime);
        // todo 验证码不返回给前端
        validateCodeResult.setCode(null);
        ServletUtils.response(response, Result.success(validateCodeResult));
    }


    @Override
    public boolean enable() {
        return getSecurityProperties().getCaptcha().getEnabled();
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return !request.getMethod().equals(HttpMethod.GET.name())
                || !request.getServletPath().equals(getSecurityProperties().getCaptcha().getApiUrl());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
