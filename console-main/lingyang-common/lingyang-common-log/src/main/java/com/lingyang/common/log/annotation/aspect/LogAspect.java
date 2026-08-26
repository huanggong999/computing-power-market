package com.lingyang.common.log.annotation.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.core.utils.*;
import com.lingyang.common.log.annotation.Log;
import com.lingyang.common.log.annotation.LogTitle;
import com.lingyang.common.log.handler.LogSavaHandler;
import com.lingyang.common.log.model.LogModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/17 12:01
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    private final LogSavaHandler savaHandler;

    public LogAspect(@Autowired(required = false) LogSavaHandler savaHandler) {
        this.savaHandler = savaHandler;
    }

    @Around(value = "@annotation(logAnnotation)", argNames = "joinPoint,logAnnotation")
    public Object around(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        String ipAddr = IpUtils.getIpAddr();
        HttpServletRequest request = ServletUtils.getRequest();
        LogTitle logTitle = ClassUtils.getAnnotation(joinPoint.getTarget().getClass(), LogTitle.class);
        String title = logAnnotation.value();
        if (logTitle != null) {
            title = logTitle.title();
        }
        LogModel logModel = new LogModel();
        logModel.setRequestMethod(request.getMethod());
        logModel.setOperateUrl(request.getRequestURI());
        logModel.setRequestId(ThreadLocalContext.getRequestLogId());
        logModel.setOperateIp(ipAddr);
        logModel.setTitle(title);
        logModel.setBusinessType(logAnnotation.businessType());
        logModel.setRequestTime(DateUtils.getNowDate());
        logModel.setJsonRequest(logAnnotation.isSaveRequestData() ? JSONObject.toJSONString(ServletUtils.getParamMap(request)) : null);
        try {
            Object proceed = joinPoint.proceed();
            logModel.setStatus(0);
            logModel.setJsonResult(logAnnotation.isSaveResponseData() ? (proceed == null ? null : JSONObject.toJSONString(proceed)) : null);
            return proceed;
        } catch (Exception e) {
            logModel.setStatus(1);
            logModel.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            throw e;
        } finally {
            logModel.setResponseTime(DateUtils.getNowDate());
            if (savaHandler != null && logAnnotation.isSaveHandler()) {
                savaHandler.saveLog(logModel);
            }
        }
    }
}