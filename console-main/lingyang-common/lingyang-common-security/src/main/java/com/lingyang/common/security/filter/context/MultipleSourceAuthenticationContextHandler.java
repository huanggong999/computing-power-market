package com.lingyang.common.security.filter.context;

import com.github.pagehelper.PageHelper;
import com.lingyang.common.core.model.RequestSource;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.authentication.MultipleSourceAuthenticationToken;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.security.config.SecurityProperties;
import com.lingyang.common.security.filter.FilterContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.util.annotation.NonNull;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/19 16:32
 */
public class MultipleSourceAuthenticationContextHandler implements FilterContext {
    @Resource
    private SecurityProperties securityProperties;
    @Override
    public void filterBefore(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response) {
        MultipleSourceAuthenticationToken authentication = MultipleSourceAuthenticationToken.unauthenticated(null);
        authentication.setSource(RequestSource.getSource(request.getHeader(securityProperties.getRequestSourceHeadKey())));
        authentication.setAuthenticated(false);
        SecurityContext.setAuthentication(authentication);
    }

    @Override
    public void filterFinal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Exception e) {
        response.setStatus(200);
        SecurityContext.clear();
        PageHelper.clearPage();
        ThreadLocalContext.clear();
    }
}
