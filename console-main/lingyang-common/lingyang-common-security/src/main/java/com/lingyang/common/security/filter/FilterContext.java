package com.lingyang.common.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.util.annotation.NonNull;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/19 14:53
 */
public interface FilterContext {


    /**
     * 过滤器执行之前处理，可用来处理请求之前的一些参数
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    default void filterBefore(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    }


    /**
     * 过滤器执行之后处理
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    default void filterAfter(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    }

    /**
     * 过滤器执行异常处理
     * <p>
     *     如果采用了 {@link org.springframework.web.bind.annotation.ExceptionHandler} 进行了统一异常配置，则过滤器无法拦截异常信息
     * </p>
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     * @param exception 异常
     */
    default void filterError(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Exception exception) {
    }

    /**
     * 最终处理，可用来释放资源
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param e        异常
     */
    default void filterFinal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Exception e) {
    }
}