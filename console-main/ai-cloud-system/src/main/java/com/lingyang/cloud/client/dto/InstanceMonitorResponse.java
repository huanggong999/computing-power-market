package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceMonitorResponse {
    private boolean success = true;
    private String message;
    @JsonAlias("instance_id")
    private String instanceId;
    private Health health;
    private List<Point> gpuUtilization;
    private List<Point> gpuMemoryUsed;
    private List<Point> cpuUtilization;
    private List<Point> memoryUsed;
    private Double gpuMemoryTotal;
    private Double memoryTotal;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Health {
        private String status;
        @JsonAlias("cpu_usage")
        private Double cpuUsage;
        @JsonAlias("memory_usage")
        private Double memoryUsage;
        @JsonAlias("gpu_usage")
        private Double gpuUsage;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Point {
        private Long timestamp;
        private Double value;
    }
}
