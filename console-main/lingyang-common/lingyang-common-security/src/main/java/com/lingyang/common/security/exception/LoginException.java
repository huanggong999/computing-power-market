package com.lingyang.common.security.exception;

import com.lingyang.common.core.enums.HttpStatus;
import lombok.Getter;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/21 10:59
 */
@Getter
public class LoginException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3220196697710876752L;

    private HttpStatus httpStatus;

    public LoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginException(HttpStatus httpStatus) {
        super(httpStatus.getMsg());
        this.httpStatus = httpStatus;
    }

    public LoginException(String msg) {
        super(msg);
    }

}
