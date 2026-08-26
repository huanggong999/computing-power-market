package com.lingyang.common.core.exception.http;

import com.lingyang.common.core.enums.HttpStatus;
import lombok.*;

import java.io.Serial;

/**
 * @Description: 业务异常，状态码为 0
 * @Author: 王小龙
 * @Date: 2023/8/4 18:30
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class HttpServiceException extends HttpResultException{
    @Serial
    private static final long serialVersionUID = 2854300871945103681L;
    public HttpServiceException(String msg) {
        super(HttpStatus.FAIL.getCode(), msg);
    }
    public HttpServiceException(HttpStatus httpStatus) {
        super(httpStatus);
    }

}