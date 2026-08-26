package com.lingyang.common.core.model.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "通用响应对象")
public class HttpResult<T> extends Result<T> {

    @Serial
    private static final long serialVersionUID = -8262871486601651476L;

    @Schema(description = "状态码：200 成功，401 未登陆，403 权限不足")
    private int code;

    @Schema(description = "响应消息")
    private String msg;

    public HttpResult(int code, String msg) {
        this(code, msg, null);
    }

    public HttpResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        super.setData(data);
    }
}
