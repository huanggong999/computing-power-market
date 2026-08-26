package com.lingyang.cloud.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lingyang.cloud.client.serializer.LocalDateTimeJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "GPU Pod实例详情")
public class GpuPodInstanceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "实例ID")
    private String id;

    @Schema(description = "实例名称")
    private String name;

    @Schema(description = "区域编码")
    private String region;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "可用区编码")
    private String zone;

    @Schema(description = "可用区名称")
    private String zoneName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态中文")
    private String statusText;

    @Schema(description = "GPU型号/规格")
    private String gpuType;

    @Schema(description = "GPU数量")
    private Integer gpuCount;

    @Schema(description = "GPU显存")
    private String gpuMemory;

    @Schema(description = "CPU核数")
    private Integer cpuCores;

    @Schema(description = "CPU描述,例如 '4核, Xeon(R) Platinum'")
    private String cpu;

    @Schema(description = "内存大小")
    private String memory;

    @Schema(description = "系统盘大小(GB)")
    private Integer systemDisk;

    @Schema(description = "数据盘大小(GB)")
    private Integer dataDisk;

    @Schema(description = "磁盘使用情况")
    private DiskUsage diskUsage;

    @Schema(description = "镜像地址")
    private String imageUrl;

    @Schema(description = "计费类型")
    private String billingType;

    @Schema(description = "计费类型中文")
    private String billingTypeText;

    @Schema(description = "计费状态")
    private String billingStatus;

    @Schema(description = "每小时价格")
    private BigDecimal pricePerHour;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime createTime;

    @Schema(description = "启动时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime startTime;

    @Schema(description = "停止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime stopTime;

    @Schema(description = "释放时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime releaseTime;

    @Schema(description = "到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime expireTime;

    @Schema(description = "定时关机时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime scheduledShutdownTime;

    @Schema(description = "SSH信息")
    private SshInfoVO sshInfo;

    @Schema(description = "健康状态")
    private HealthStatus healthStatus;

    @Schema(description = "工具列表")
    private List<ToolInfo> tools;

    @Data
    public static class HealthStatus {

        @Schema(description = "状态")
        private String status;

        @Schema(description = "CPU使用率")
        private Double cpuUsage;

        @Schema(description = "内存使用率")
        private Double memoryUsage;

        @Schema(description = "GPU使用率")
        private Double gpuUsage;

        @Schema(description = "消息")
        private String message;
    }

    @Data
    public static class DiskUsage {

        @Schema(description = "系统盘使用率(%)")
        private BigDecimal systemDiskUsage;

        @Schema(description = "数据盘使用率(%)")
        private BigDecimal dataDiskUsage;
    }

    @Data
    public static class ToolInfo {

        @Schema(description = "工具名称")
        private String name;

        @Schema(description = "工具访问地址")
        private String url;

        @Schema(description = "图标")
        private String icon;
    }
}

