package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * GPU Pod 实例响应
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceResponse {

    @JsonProperty("success")
    private boolean success = true;

    @JsonProperty("message")
    private String message;

    private String instanceId;

    private String instanceName;

    private String region;

    private String zone;

    private String machineId;

    private String status;

    private GpuSpec gpuSpec;

    private CpuInfo cpu;

    private String memory;

    private DiskInfo disk;

    private HealthInfo health;

    private String billingMode;

    private String billingStatus;

    private BigDecimal pricePerHour;

    private BigDecimal discountPrice;

    private String releaseTime;

    private ShutdownScheduleInfo shutdownSchedule;

    private SshInfo sshInfo;

    private List<ToolInfo> tools;

    private String createTime;

    private String startTime;

    private String expireTime;

    private String image;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GpuSpec {
        private String model;
        private String memory;
        private Integer count;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CpuInfo {
        private Integer cores;
        private String model;
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
    public static class ShutdownScheduleInfo {
        private Boolean enabled;
        private String scheduledTime;
        private Integer shutdownMinutes;
        private String actionType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SshInfo {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String command;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolInfo {
        private String name;
        private String url;
        private String icon;
    }
}
