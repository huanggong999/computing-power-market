package com.lingyang.common.core.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * @Description: 方法执行异常 状态码500
 * @Author: 王小龙
 * @Date: 2023/8/4 18:40
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MethodExecutionException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 7094539640379632453L;

    public MethodExecutionException(String errorMsg) {
        super(errorMsg);
    }


    public MethodExecutionException(Exception e) {
        super(e);
    }
    public MethodExecutionException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }
}
