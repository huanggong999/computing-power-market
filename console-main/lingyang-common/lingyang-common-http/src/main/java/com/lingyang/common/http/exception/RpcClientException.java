package com.lingyang.common.http.exception;

import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.http.HttpResultException;
import com.lingyang.common.core.utils.StringUtils;

import java.io.Serial;

/**
 * @Deprecated
 * @Author 王小龙
 * @Time 2023/5/14 23:42
 */
public class RpcClientException extends HttpResultException {
    @Serial
    private static final long serialVersionUID = -1652249670359267752L;

    public RpcClientException(String message) {
        super(HttpStatus.HTTP_CLIENT_ERROR.getCode(), StringUtils.isEmpty(message) ? HttpStatus.HTTP_CLIENT_ERROR.getMsg() : HttpStatus.HTTP_CLIENT_ERROR.getMsg() + ": " + message);
    }
}
