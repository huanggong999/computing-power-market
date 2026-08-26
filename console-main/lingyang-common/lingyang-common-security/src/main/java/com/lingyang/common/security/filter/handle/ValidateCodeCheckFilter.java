package com.lingyang.common.security.filter.handle;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Builder;
import com.lingyang.common.core.utils.ClassUtils;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.core.utils.UrlUtils;
import com.lingyang.common.security.config.CaptchaProperties;
import com.lingyang.common.security.filter.AbstractFilter;
import com.lingyang.common.security.model.LoginParam;
import com.lingyang.common.security.utils.ValidateCodeUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 17:30
 */
@ConditionalOnProperty(value = "security.captcha.enabled", havingValue = "true")
@Slf4j
public class ValidateCodeCheckFilter extends AbstractFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 获取参数
        String method = request.getMethod();
        LoginParam param;
        JSONObject jsonObject;
        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            // get / deleted 获取url参数
            param = Builder.of(LoginParam::new)
                    .set(LoginParam::setCode, request.getParameter("code"))
                    .set(LoginParam::setUid, request.getParameter("uid"))
                    .build();
        } else {
            // 获取body参数
            jsonObject = JSONObject.parseObject(ServletUtils.getBodyParam(request));
            if (ObjectUtils.isNotEmpty(jsonObject)) {
                param = jsonObject.to(LoginParam.class);
            } else {
                param = ClassUtils.newInstance(LoginParam.class);
            }
        }
        if (param == null) {
            ServletUtils.response(response, Result.result(HttpStatus.NOT_PARAMS));
            return;
        }
        String source = request.getHeader("source");
        log.info("登录source类型:{}", source);
        if (ObjectUtils.isEmpty(source)){
            ValidateCodeUtils.checkValidateCode(param.getUid(), param.getCode());
            ValidateCodeUtils.removeValidateCode(param.getUid());
        } else {
            if (!"WECHAT_LOGIN".equals(source)) {
                ValidateCodeUtils.checkValidateCode(param.getUid(), param.getCode());
                ValidateCodeUtils.removeValidateCode(param.getUid());
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        CaptchaProperties captcha = getSecurityProperties().getCaptcha();
        Map<String, String[]> ignoresHeads = captcha.getIgnoresHeads();
        boolean flag = true;
        if (ObjectUtils.isNotEmpty(ignoresHeads)) {
            for (Map.Entry<String, String[]> entry : ignoresHeads.entrySet()) {
                if (Arrays.asList(entry.getValue()).contains(request.getHeader(entry.getKey()))) {
                    flag = false;
                }
            }
        }
        return !(UrlUtils.matches(captcha.getCheckUrls(), request.getServletPath()) && flag);
    }


    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
