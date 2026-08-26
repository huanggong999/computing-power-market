package com.lingyang.common.log.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.log.enums.BusinessType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/17 11:56
 */
@Data
public class LogModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -1889169916046381631L;
    /**
     * 请求id
     */
    private Long requestId;

    /**
     * ip地址
     */
    private String operateIp;
    /**
     * 模块
     */
    private String title;
    /**
     * 功能
     */
    private BusinessType businessType;

    /**
     * 请求方式
     */
    private String requestMethod;
    /**
     * 请求地址
     */
    private String operateUrl;
    /**
     * 请求参数json
     */
    private String jsonRequest;
    /**
     * 响应参数json
     */
    private String jsonResult;
    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;
    /**
     * 失败消息
     */
    private String errorMsg;
    /**
     * 请求时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requestTime;
    /**
     * 响应时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date responseTime;
    /**
     * 耗时，单位毫秒
     */
    private Long costTime;


    public Long getCostTime() {
        if (costTime == null && requestTime != null && responseTime != null) {
            return responseTime.getTime() - requestTime.getTime();
        }
        return costTime;
    }
}
