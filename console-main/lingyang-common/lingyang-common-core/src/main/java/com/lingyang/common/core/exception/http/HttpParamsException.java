package com.lingyang.common.core.exception.http;

import com.lingyang.common.core.enums.HttpStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * @Description: 参数异常 缺少参数，参数数据不对
 * @Author: 王小龙
 * @Date: 2023/8/7 10:47
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class HttpParamsException extends HttpResultException{

    @Serial
    private static final long serialVersionUID = -5264955533847341960L;

    public HttpParamsException() {
        super(HttpStatus.NOT_PARAMS.getCode(), HttpStatus.NOT_PARAMS.getMsg());
    }

    public HttpParamsException(String msg) {
        super(HttpStatus.NOT_PARAMS.getCode(), msg);
    }
}