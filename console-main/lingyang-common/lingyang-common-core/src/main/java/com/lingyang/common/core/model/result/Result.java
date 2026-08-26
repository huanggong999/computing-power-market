package com.lingyang.common.core.model.result;

import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.http.HttpResultException;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.core.utils.ClassUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:10
 */
@Data
public abstract class Result<T> implements Serializable {

    @Schema(description = "请求id")
    private Long requestId;

    @Schema(description = "响应数据")
    private T data;

    @Serial
    private static final long serialVersionUID = -2090059594899995795L;

    public Result() {
        this.requestId = ThreadLocalContext.getRequestLogId();
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> result(int rows) {
        return result(rows > 0);
    }

    public static <T> Result<T> result(boolean flag) {
        return flag ? success() : fail();
    }

    /**
     * 操作失败
     */
    public static <T> Result<T> fail() {
        return result(HttpStatus.FAIL);
    }

    /**
     * 操作成功
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 结果
     */
    public static <T> Result<T> success(T data) {
        return result(HttpStatus.OK, data);
    }

    public static <T> Result<T> error(String msg) {
        return result(HttpStatus.ERROR.getCode(), msg, null);
    }

    /**
     * 响应结果
     *
     * @param httpStatus 状态码
     * @param <T>        响应数据类型
     * @return 结果
     */
    public static <T> Result<T> result(HttpStatus httpStatus) {
        return result(httpStatus, null);
    }

    /**
     * 响应结果
     *
     * @param httpStatus 状态码
     * @param data       数据
     * @param <T>        响应数据类型
     * @return 结果
     */
    public static <T> Result<T> result(HttpStatus httpStatus, T data) {
        return result(httpStatus.getCode(), httpStatus.getMsg(), data);
    }

    /**
     * 响应结果
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     * @param <T>  响应数据类型
     * @return 结果
     */
    public static <T> Result<T> result(int code, String msg, T data) {
        return new HttpResult<>(code, msg, data);
    }

    /**
     * 异常
     *
     * @param httpStatus 状态码
     */
    public static void throwsError(HttpStatus httpStatus) {
        buildError(HttpServiceException.class, httpStatus).error();
    }

    /**
     * 异常
     *
     * @param errorMsg 异常消息
     */
    public static void throwsError(String errorMsg) {
        buildError(HttpServiceException.class, errorMsg).error();
    }


    public static <T extends HttpResultException> void throwsError(Class<T> exceptionClass, Object... args) {
        throw buildError(exceptionClass, args);
    }

    /**
     * 构建异常
     *
     * @param exceptionClass 异常类型
     * @param args           创建异常对象的参数
     * @return 异常对象
     */
    public static <T extends HttpResultException> T buildError(Class<T> exceptionClass, Object... args) {
        return ClassUtils.newInstance(exceptionClass, args);
    }
}