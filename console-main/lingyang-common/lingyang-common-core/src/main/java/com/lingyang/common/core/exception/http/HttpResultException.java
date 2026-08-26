package com.lingyang.common.core.exception.http;

import com.lingyang.common.core.enums.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class HttpResultException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -384934461762596203L;

    private Integer code;

    public HttpResultException(HttpStatus httpStatus) {
        super(httpStatus.getMsg());
        this.code = httpStatus.getCode();
    }

    public HttpResultException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * 抛出当前异常
     */
    public void error() {
        throw this;
    }

}
