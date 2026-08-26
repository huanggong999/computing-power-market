package com.lingyang.common.core.execute;

import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.execute.model.AbsExecuteHandlerContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: 执行器工厂
 * @Author: 王小龙
 * @Date: 2023/10/9 11:47
 */
@Component
public class ExecuteHandlerFactory<T extends AbsExecuteHandlerContext> {

    private final Map<String, List<ExecuteHandlerIntercept<T>>> handlerMap;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public ExecuteHandlerFactory(@Autowired(required = false) List<ExecuteHandlerIntercept<T>> handlerList) {
        if (ObjectUtils.isNotEmpty(handlerList)) {
            handlerMap = new HashMap<>(handlerList.size());
            handlerList.sort(Comparator.comparingInt(ExecuteHandlerIntercept::soft));
            for (ExecuteHandlerIntercept<T> executeHandlerIntercept : handlerList) {
                List<ExecuteHandlerIntercept<T>> executeHandlerIntercepts = handlerMap.get(executeHandlerIntercept.group());
                if (ObjectUtils.isEmpty(executeHandlerIntercepts)) {
                    executeHandlerIntercepts = new ArrayList<>();
                }
                executeHandlerIntercepts.add(executeHandlerIntercept);
                handlerMap.put(executeHandlerIntercept.group(), executeHandlerIntercepts);
            }
        } else {
            handlerMap = null;
        }
    }

    public T execute(String group, T context) {
        List<ExecuteHandlerIntercept<T>> executeHandlerIntercepts = getHandler(group);
        for (ExecuteHandlerIntercept<T> executeHandlerIntercept : executeHandlerIntercepts) {
            context = executeHandlerIntercept.execute(context);
        }
        return context;
    }

    public void asyncExecute(String group, T context) {
        initThreadPool();
        threadPoolTaskExecutor.execute(() -> execute(group, context));
    }

    private List<ExecuteHandlerIntercept<T>> getHandler(String group) {
        if (handlerMap == null) {
            throw new MethodExecutionException("ExecuteHandlerIntercept is null");
        }
        List<ExecuteHandlerIntercept<T>> executeHandlerIntercepts = handlerMap.get(group);
        if (ObjectUtils.isEmpty(executeHandlerIntercepts)) {
            throw new MethodExecutionException(group + " is not ExecuteHandlerIntercept");
        }
        return executeHandlerIntercepts;
    }


    private void initThreadPool() {
        if (this.handlerMap == null) {
            throw new MethodExecutionException("ExecuteHandlerIntercept is null");
        }
        if (this.threadPoolTaskExecutor == null ) {
            this.threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            this.threadPoolTaskExecutor.setCorePoolSize(handlerMap.size());// 设置核心线程数
            this.threadPoolTaskExecutor.setMaxPoolSize(handlerMap.keySet().size());// 配置最大线程数
            this.threadPoolTaskExecutor.setQueueCapacity(handlerMap.keySet().size() * 4);// 配置队列容量（这里设置成最大线程数的四倍）
            this.threadPoolTaskExecutor.setThreadNamePrefix("asyncExecute-thread");// 给线程池设置名称
            this.threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());// 设置任务的拒绝策略
            this.threadPoolTaskExecutor.initialize();
        }
    }
}
