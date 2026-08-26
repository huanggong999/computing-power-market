package com.lingyang.common.security.filter.handle;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.security.filter.AbstractFilter;
import com.lingyang.common.security.reqeust.RepeatReadHttpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/20 14:17
 */
@Slf4j
public class RequestLogFilter extends AbstractFilter {

    private static final String MDC_LOG_ID = "logId";
    private static final String REQUEST_ID_HEADER = "requestId";
    private static final String REQUEST_ID_HEADER_LINE = "request-id";
    private static final String X_REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Long requestLogId = IdUtils.nextId();
        ThreadLocalContext.setRequestLogId(requestLogId);
        String requestId = resolveRequestId(request, requestLogId);
        ThreadLocalContext.setRequestId(requestId);
        MDC.put(MDC_LOG_ID, requestId);
        HttpServletRequest requestWrapper = new RepeatReadHttpRequest(request);
        String url = requestWrapper.getServletPath();
        Map<String, String> params = ServletUtils.getParamMap(requestWrapper);
        String bodyParam = ServletUtils.getBodyParam(requestWrapper);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            log.info("requestId: [{}], 请求接口: [{} {}], 请求参数: {}, 返回结果: {}",
                    requestId,
                    requestWrapper.getMethod(),
                    url,
                    getRequestParams(params, bodyParam),
                    getResponseBody(responseWrapper));
            responseWrapper.copyBodyToResponse();
            MDC.remove(MDC_LOG_ID);
            ThreadLocalContext.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request, Long fallbackRequestLogId) {
        String requestId = StringUtils.firstNonBlank(
                request.getHeader(REQUEST_ID_HEADER),
                request.getHeader(REQUEST_ID_HEADER_LINE),
                request.getHeader(X_REQUEST_ID_HEADER));
        return StringUtils.defaultIfBlank(requestId, String.valueOf(fallbackRequestLogId));
    }

    private String getRequestParams(Map<String, String> queryParams, String bodyParam) {
        if (ObjectUtils.isEmpty(queryParams) && ObjectUtils.isEmpty(bodyParam)) {
            return "";
        }
        JSONObject requestParams = new JSONObject();
        if (!ObjectUtils.isEmpty(queryParams)) {
            requestParams.put("query", queryParams);
        }
        if (!ObjectUtils.isEmpty(bodyParam)) {
            requestParams.put("body", parseJsonIfPossible(bodyParam));
        }
        return requestParams.toJSONString();
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        return normalizeJsonText(new String(content, StandardCharsets.UTF_8));
    }

    private Object parseJsonIfPossible(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        try {
            return JSON.parse(text);
        } catch (Exception ignored) {
            return text;
        }
    }

    private String normalizeJsonText(String text) {
        Object parsed = parseJsonIfPossible(text);
        return parsed instanceof String ? (String) parsed : JSON.toJSONString(parsed);
    }


    @Override
    public boolean enable() {
        return getSecurityProperties().isLogEnable();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
