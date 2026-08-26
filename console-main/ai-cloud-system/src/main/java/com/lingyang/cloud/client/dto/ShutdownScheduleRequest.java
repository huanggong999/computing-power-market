package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 设置关机计划请求
 */
@Data
public class ShutdownScheduleRequest {

    /**
     * ISO格式关机时间，null表示取消定时关机
     */
    @JsonProperty("shutdown_time")
    private String shutdownTime;
}
