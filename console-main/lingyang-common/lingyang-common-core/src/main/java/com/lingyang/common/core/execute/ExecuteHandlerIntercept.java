package com.lingyang.common.core.execute;

import com.lingyang.common.core.execute.model.AbsExecuteHandlerContext;

/**
 * @Description: 执行处理器接口
 * @Author: 王小龙
 * @Date: 2023/10/9 11:42
 */
public interface ExecuteHandlerIntercept<T extends AbsExecuteHandlerContext> {
    /**
     * 执行
     * @param context 上下文
     * @return 上下文
     */
    T execute(T context);

    /**
     * 组，执行组
     * @return 组名称
     */
    String group();

    /**
     * 顺序，从小到大依次执行
     * @return 顺序
     */
    int soft();

}