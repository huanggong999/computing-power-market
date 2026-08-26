package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * GPU Pod 实例列表响应
 * 远端字段为 snake_case,通过 @JsonNaming 全局转换
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceListResponse {

    /**
     * 是否成功(远端不返回,客户端兜底使用)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("success")
    private boolean success = true;

    /**
     * 消息(远端不返回,客户端兜底使用)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("message")
    private String message;

    /**
     * 实例总数
     */
    private Long total;

    /**
     * 实例列表
     */
    private List<InstanceItem> instances;

    /**
     * 实例列表项
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InstanceItem {
        private String instanceId;
        private String instanceName;
        private String region;
        private String zone;
        private String status;
        /**
         * GPU 规格描述,例如 "L4 * 1卡"
         */
        private String gpuSpec;
        private Integer gpuCount;
        private String cpu;
        private String memory;
        private DiskInfo disk;
        private HealthInfo health;
        private String billingMode;
        private String billingStatus;
        private String releaseTime;
        private SshInfo sshInfo;
        private List<ToolInfo> tools;
        private String createTime;
        private String expireTime;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiskInfo {
        private BigDecimal systemDiskUsage;
        private BigDecimal dataDiskUsage;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthInfo {
        private String status;
        private BigDecimal cpuUsage;
        private BigDecimal memoryUsage;
        private BigDecimal gpuUsage;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SshInfo {
        private String command;
        private String passwordMasked;
        private String host;
        private Integer port;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolInfo {
        private String name;
        private String url;
        private String icon;
    }
}
