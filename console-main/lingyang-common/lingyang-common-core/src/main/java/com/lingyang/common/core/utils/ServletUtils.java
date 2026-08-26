package com.lingyang.common.core.utils;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.common.core.enums.HttpContextType;
import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.model.result.Result;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/17 11:41
 */
public class ServletUtils {

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            throw new MethodExecutionException("获取 RequestAttributes异常：", e);
        }
    }

    public static void responseJson(ServletResponse response, Object data) throws IOException {
        response(response, Result.success(data));
    }

    public static void response(ServletResponse response, Result<Object> result) throws IOException {
        response(response, HttpContextType.JSON, JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8));
    }

    public static void response(ServletResponse response, HttpContextType contentType, byte[] data) throws IOException {
        response(response, contentType, null, data);
    }

    public static void response(ServletResponse response, HttpContextType contentType, Charset charset, byte[] data) throws IOException {
        contentType = contentType == null ? HttpContextType.JSON : contentType;
        charset = charset == null ? StandardCharsets.UTF_8 : charset;
        response.setCharacterEncoding(charset.displayName());
        response.setContentType(contentType.getValue());
        response.getOutputStream().write(data);
    }

    /**
     * 获取请求参数
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String[]> queryParam = getParams(request);
        if (ObjectUtils.isEmpty(queryParam)) {
            return null;
        }
        Map<String, String> params = new HashMap<>(queryParam.size());
        for (Map.Entry<String, String[]> entry : queryParam.entrySet()) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
        }
        return params;
    }

    /**
     * 获取body参数
     * @param request 请求对象
     * @return body参数
     */
    public static String getBodyParam(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
       return stringBuilder.toString();
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        if (ObjectUtils.isEmpty(map)) {
            return null;
        }
        return Collections.unmodifiableMap(map);
    }


    /**
     * 获取String请求参数 路径 ? 后面的参数
     *
     * @param paramName 参数名称
     * @return 参数
     */
    public static String getQueryParamToString(String paramName) {
        return Convert.toStr(getRequest().getParameter(paramName));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) {
        return Convert.toBool(getRequest().getParameter(name));
    }
}