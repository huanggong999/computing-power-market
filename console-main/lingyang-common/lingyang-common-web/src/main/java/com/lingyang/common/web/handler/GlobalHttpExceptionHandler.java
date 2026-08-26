package com.lingyang.common.web.handler;

import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.exception.http.HttpResultException;
import com.lingyang.common.core.model.result.HttpResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.thread.ThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/7 9:55
 */
@RestControllerAdvice
@Slf4j
public class GlobalHttpExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(HttpResultException.class)
    public Result<Void> httpResultException(HttpResultException e) {
        log.info("request_id：[{}], 请求异常：{}", ThreadLocalContext.getRequestLogId(), e.getMessage());
        return new HttpResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 内部方法执行异常
     */
    @ExceptionHandler(MethodExecutionException.class)
    public Result<Void> methodExecutionException(MethodExecutionException e) {
        log.error("request_id：[{}], 内部方法执行异常：", ThreadLocalContext.getRequestLogId(), e);
        return Result.result(HttpStatus.ERROR);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        return Result.error("数据已存在");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<Void> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        return Result.error("数据已存在");
    }

    /**
     * 请求处理失败
     */
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            NoHandlerFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class
    })
    public Result<Void> requestException(Exception e) {
        if (e instanceof MethodArgumentNotValidException validException) {
            FieldError fieldError = validException.getBindingResult().getFieldError();
            String msg;
            if (fieldError != null) {
                msg = fieldError.getDefaultMessage();
            } else {
                msg = validException.getBindingResult().toString();
            }
            log.info("request_id：[{}], 缺少请求参数：{}", ThreadLocalContext.getRequestLogId(), msg);
            return Result.result(HttpStatus.NOT_PARAMS.getCode(), msg, null);
        }
        if (e instanceof NoHandlerFoundException noHandlerFoundException) {
            log.info("request_id：[{}], 请求地址不存在：{}", ThreadLocalContext.getRequestLogId(), noHandlerFoundException.getRequestURL());
            return Result.result(HttpStatus.NOT_REQUEST_HANDLER);
        }
        log.error("request_id：[{}], 请求处理异常：", ThreadLocalContext.getRequestLogId(), e);
        return Result.result(HttpStatus.REQUEST_ERROR);
    }


    /**
     * 未登录
     */
    @ExceptionHandler(AuthenticationServiceException.class)
    public Result<Void> authenticationServiceException(AuthenticationServiceException e) {
        log.info("request_id：[{}], 请求异常：{}", ThreadLocalContext.getRequestLogId(), e.getMessage());
        return  Result.result(HttpStatus.NOT_LOGIN);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> exception(Exception e) throws Exception {
        if (e instanceof AccessDeniedException || e instanceof AuthenticationException) {
            throw e;
        }
        log.error("request_id：[{}], 请求处理异常：", ThreadLocalContext.getRequestLogId(), e);
        return Result.result(HttpStatus.ERROR);
    }
}