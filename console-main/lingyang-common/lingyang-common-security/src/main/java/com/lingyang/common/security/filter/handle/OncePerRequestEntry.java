package com.lingyang.common.security.filter.handle;

import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.http.HttpResultException;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.security.filter.AbstractFilter;
import com.lingyang.common.security.filter.FilterContext;
import com.lingyang.common.security.reqeust.RepeatReadHttpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 过滤器入口，封装一些信息和全局异常处理
 * @Author: 王小龙
 * @Date: 2024/3/19 14:17
 */
@Slf4j
public class OncePerRequestEntry extends AbstractFilter {

    @Autowired(required = false)
    private List<FilterContext> filterContextList;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Exception exception = null;
        Optional.of(filterContextList)
                .ifPresent(contextList -> contextList.forEach(context -> context.filterBefore(request, response)));
        try {
            filterChain.doFilter(new RepeatReadHttpRequest(request), response);
            Optional.of(filterContextList)
                    .ifPresent(contextList -> contextList.forEach(context -> context.filterAfter(request, response)));
        } catch (Exception e) {
            exception = e;
            Optional.of(filterContextList)
                    .ifPresent(contextList -> contextList.forEach(context -> context.filterError(request, response, e)));
            if (exception instanceof AuthenticationException) {
                log.info("{} AuthenticationException : {}", ThreadLocalContext.getRequestLogId(), exception.getMessage());
                ServletUtils.response(response, Result.result(HttpStatus.NOT_LOGIN));
            } else if (exception instanceof AccessDeniedException) {
                log.info("{} AccessDeniedException: {}", ThreadLocalContext.getRequestLogId(), exception.getMessage());
                ServletUtils.response(response, Result.result(HttpStatus.NOT_PERMISSION));
            } else if (exception instanceof HttpResultException) {
                log.info("{} HttpResultException: {}", ThreadLocalContext.getRequestLogId(), exception.getMessage());
                ServletUtils.response(response, Result.error(e.getMessage()));
            } else {
                log.error("{} 请求处理异常：", ThreadLocalContext.getRequestLogId(), e);
                ServletUtils.response(response, Result.error(e.getMessage()));
            }
        } finally {
            Exception finalException = exception;
            Optional.of(filterContextList)
                    .ifPresent(contextList -> contextList.forEach(context -> context.filterFinal(request, response, finalException)));
        }
    }


    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }
}
