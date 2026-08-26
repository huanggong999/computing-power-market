package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建GPU Pod实例请求
 */
@Data
@Schema(description = "创建GPU Pod实例请求")
public class CreateGpuPodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "区域不能为空")
    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String region;

    @Schema(description = "可用区编码")
    private String zone;

    @NotNull(message = "GPU规格不能为空")
    @Schema(description = "GPU规格", requiredMode = Schema.RequiredMode.REQUIRED)
    private GpuSpec gpuSpec;

    @NotBlank(message = "镜像地址不能为空")
    @Schema(description = "镜像地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String image;

    @NotNull(message = "计费配置不能为空")
    @Schema(description = "计费配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private Billing billing;

    @Schema(description = "资源配置")
    private Resource resource;

    @Schema(description = "自定义Pod名称")
    private String podName;

    @Data
    @Schema(description = "GPU规格")
    public static class GpuSpec {

        @NotBlank(message = "GPU型号不能为空")
        @Schema(description = "GPU型号", requiredMode = Schema.RequiredMode.REQUIRED)
        private String model;

        @Schema(description = "GPU数量", defaultValue = "1")
        private int count = 1;
    }

    @Data
    @Schema(description = "计费配置")
    public static class Billing {

        @NotBlank(message = "计费模式不能为空")
        @Schema(description = "计费模式: on_demand, hourly, daily, weekly, monthly, yearly", requiredMode = Schema.RequiredMode.REQUIRED)
        private String mode;

        @Schema(description = "购买时长（包周期模式必填）")
        private Integer duration;
    }

    @Data
    @Schema(description = "资源配置")
    public static class Resource {

        @Schema(description = "CPU核数", defaultValue = "4")
        private String cpu;

        @Schema(description = "内存大小", defaultValue = "16Gi")
        private String memory;

        @Schema(description = "系统盘大小", defaultValue = "30Gi")
        private String systemDisk;

        @Schema(description = "数据盘大小", defaultValue = "50Gi")
        private String dataDisk;
    }
}
