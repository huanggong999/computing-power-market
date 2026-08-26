package com.lingyang.common.core.function;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * @Description: 方法执行
 * @Author: 王小龙
 * @Date: 2023/9/27 18:47
 */
@Service
public class FunctionExecute {

    /**
     * 异步方法执行
     * @param param 参数
     * @param consumer 函数式接口
     * @param <T> 参数类型
     */
    @Async
    public <T> void asyncHandler(T param, Consumer<T> consumer) {
        consumer.accept(param);
    }
}
