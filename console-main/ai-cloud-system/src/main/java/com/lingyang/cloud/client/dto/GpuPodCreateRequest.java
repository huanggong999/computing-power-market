package com.lingyang.cloud.client.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建 GPU Pod 请求（对接外部 GPU 调度服务 /api/v1/gpu/pod/create）
 */
@Data
public class GpuPodCreateRequest {

    private String tenantId;
    private String tenantName;
    private GpuSpec gpuSpec;
    private String image;
    private Billing billing;
    private Resource resource;
    private Pricing pricing;
    private String podName;
    private String region;
    private String zone;
    private String machineId;
    private String gpuDriver;
    private String cudaVersion;

    @Data
    public static class GpuSpec {
        private String model;
        private Integer count;
        private String gpuMemory;
    }

    @Data
    public static class Billing {
        private String mode;
        private Integer duration;
    }

    @Data
    public static class Resource {
        private String cpu;
        private String cpuModel;
        private String memory;
        private String systemDisk;
        private String dataDisk;
        private String dataDiskExpandable;
    }

    @Data
    public static class Pricing {
        private BigDecimal unitPrice;
        private BigDecimal discountUnitPrice;
        private BigDecimal totalCost;
        private BigDecimal discountTotalCost;
        private String currency;
        private String unit;
        private BigDecimal pricePerHour;
        private BigDecimal discountPrice;
    }
}
