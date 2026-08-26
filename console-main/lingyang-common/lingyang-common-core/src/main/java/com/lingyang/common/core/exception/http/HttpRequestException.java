package com.lingyang.common.core.exception.http;

import com.lingyang.common.core.enums.HttpStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * @Description: 请求错误处理
 * @Author: 王小龙
 * @Date: 2023/8/7 11:01
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class HttpRequestException extends HttpResultException{
    @Serial
    private static final long serialVersionUID = -3872595701314209471L;

    public HttpRequestException(HttpStatus httpStatus) {
        super(httpStatus);
    }
}
