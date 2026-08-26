package com.lingyang.common.core.exception.http;

import com.lingyang.common.core.enums.HttpStatus;

import java.io.Serial;

/**
 * @Description: 权限异常
 * @Author: 王小龙
 * @Date: 2023/8/7 16:46
 */
public class HttpPermissionException extends HttpResultException{

    @Serial
    private static final long serialVersionUID = 286446307202681758L;

    public HttpPermissionException() {
        super(HttpStatus.NOT_PERMISSION);
    }
}
