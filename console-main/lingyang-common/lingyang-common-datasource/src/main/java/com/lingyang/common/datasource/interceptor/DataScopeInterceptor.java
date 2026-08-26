package com.lingyang.common.datasource.interceptor;


import cn.hutool.core.util.ArrayUtil;
import com.lingyang.common.core.datasource.annotation.DataScope;
import com.lingyang.common.core.datasource.enums.DataScopeType;
import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.model.BaseRole;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.datasource.model.InvocationParseObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/5/30 下午3:04
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
@Slf4j
public class DataScopeInterceptor implements Interceptor {
    private final Map<DataScopeType, DataScopeInterface> dataScopeInterfaceMap;

    public DataScopeInterceptor(List<DataScopeInterface> dataScopeInterceptor) {
        if (dataScopeInterceptor == null) {
            throw new MethodExecutionException("dataScopeInterceptor is null");
        }
        dataScopeInterfaceMap = new ConcurrentHashMap<>(dataScopeInterceptor.size());
        for (DataScopeInterface dataScopeInterface : dataScopeInterceptor) {
            dataScopeInterfaceMap.put(dataScopeInterface.dataSourceType(), dataScopeInterface);
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        InvocationParseObject invocationParseObject = InvocationParseObject.build(invocation);
        DataScope dataScope = invocationParseObject.getAnnotation(DataScope.class);
        if (notInterceptor(dataScope)) {
            return invocation.proceed();
        }
        // 拦截数据权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ) {
            throw new AccessDeniedException("authentication is null");
        }
        List<DataScopeType> dataScopeTypeList = SecurityContext.getUserInfo().getRoleList().stream()
                .map(BaseRole::getDataScope)
                .sorted(Comparator.comparingInt(DataScopeType::getWeight))
                .toList();
        if (dataScopeTypeList.contains(DataScopeType.ALL) && !dataScopeInterfaceMap.containsKey(DataScopeType.ALL)) {
            // 当前无ALL 权限处理器
            return invocation.proceed();
        }
        // 获取权重最高的
        DataScopeType dataScopeType = dataScopeTypeList.get(0);
        dataScopeTypeList = dataScopeTypeList.stream()
                .filter(type -> type.getWeight() == dataScopeType.getWeight())
                .toList();
        String sql = invocationParseObject.getSql();
        for (DataScopeType scopeType : dataScopeTypeList) {
            DataScopeInterface dataScopeInterface = dataScopeInterfaceMap.get(scopeType);
            if (ObjectUtils.isEmpty(dataScope)) {
                throw new MethodExecutionException("dataScopeInterceptor is null: " + dataScope);
            }
            sql = dataScopeInterface.resetSql(sql, dataScope);
        }
        invocationParseObject.setSql(sql);
        return invocationParseObject.getInvocation().proceed();
    }

    private boolean notInterceptor(DataScope dataScope){
        return ObjectUtils.isEmpty(dataScope) &&
                ObjectUtils.isNotEmpty(dataScope.requestSource()) &&
                (
                        SecurityContext.isAdmin() ||
                        dataScopeInterfaceMap.isEmpty() ||
                        !ArrayUtil.contains(dataScope.requestSource(), SecurityContext.getAuthentication().getSource().getSourceKey())
                )
                ;
    }
}