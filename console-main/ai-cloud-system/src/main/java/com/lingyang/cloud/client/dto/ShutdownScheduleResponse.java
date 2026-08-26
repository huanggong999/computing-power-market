package com.lingyang.cloud.client.dto;

import lombok.Data;

/**
 * 关机计划响应
 */
@Data
public class ShutdownScheduleResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息
     */
    private String message;

    /**
     * 实例 ID
     */
    private String instanceId;

    /**
     * 关机计划信息
     */
    private ScheduleInfo schedule;

    /**
     * 关机计划信息
     */
    @Data
    public static class ScheduleInfo {
        /**
         * 是否启用
         */
        private Boolean enabled;

        /**
         * 关机时间（分钟）
         */
        private Integer shutdownMinutes;

        /**
         * 关机类型：shutdown、release
         */
        private String actionType;

        /**
         * 计划执行时间
         */
        private String scheduledTime;

        /**
         * 创建时间
         */
        private String createTime;

        /**
         * 更新时间
         */
        private String updateTime;

        /**
         * 描述
         */
        private String description;
    }
}
