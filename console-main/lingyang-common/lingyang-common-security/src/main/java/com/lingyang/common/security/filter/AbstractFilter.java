package com.lingyang.common.security.filter;

import com.lingyang.common.security.config.SecurityProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.util.annotation.NonNull;

/**
 * @Description: 过滤器抽象实现
 * <p>
 *     基于 {@link org.springframework.web.filter.OncePerRequestFilter},实现过滤拦截
 *  </p>
 * <p>
 *     基于 {@link org.springframework.core.Ordered}进行过滤器优先级处理，getOrder越小，优先执行
 * </p>
 * @Author: 王小龙
 * @Date: 2024/3/19 14:34
 */
@Getter
public abstract class AbstractFilter extends OncePerRequestFilter implements Ordered {

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 是否开启当前过滤器
     * @return true 开启
     */
    public boolean enable() {
        return true;
    }

    /**
     * 自定义拦截条件，返回 true，处理当前拦截
     * @param request 当前 HTTP 请求
     * @return 是否 不应过滤 给定的请求
     * @throws ServletException  如果出现错误
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return !enable();
    }
}
