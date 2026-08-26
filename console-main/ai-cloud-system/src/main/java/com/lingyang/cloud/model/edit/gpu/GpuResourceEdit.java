package com.lingyang.cloud.model.edit.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * GPU资源编辑参数
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU资源编辑参数")
public class GpuResourceEdit {

    @Schema(description = "资源ID，null=新增")
    private Long id;

    @Schema(description = "机器编号")
    @NotBlank(message = "机器编号不能为空")
    private String machineId;

    @Schema(description = "机器UUID")
    @NotBlank(message = "机器UUID不能为空")
    private String machineUuid;

    @Schema(description = "地区编码")
    @NotBlank(message = "地区编码不能为空")
    private String regionCode;

    @Schema(description = "专区编码")
    private String zoneCode;

    @Schema(description = "来源GPU集群ID")
    private String clusterId;

    @Schema(description = "来源GPU集群名称")
    private String clusterName;

    @Schema(description = "来源GPU集群节点名称")
    private String clusterNodeName;

    @Schema(description = "规格ID")
    @NotNull(message = "规格ID不能为空")
    private Long specId;

    @Schema(description = "GPU数量")
    @NotNull(message = "GPU数量不能为空")
    @Min(value = 1, message = "GPU数量至少为1")
    private Integer gpuCount;

    @Schema(description = "GPU驱动版本")
    private String gpuDriver;

    @Schema(description = "CUDA版本")
    private String cudaVersion;

    @Schema(description = "是否缓存优化")
    private Boolean cacheOptimized;

    @Schema(description = "CPU核心数")
    @NotNull(message = "CPU核心数不能为空")
    @Min(value = 1, message = "CPU核心数至少为1")
    private Integer cpuCores;

    @Schema(description = "CPU型号")
    private String cpuModel;

    @Schema(description = "内存大小")
    @NotBlank(message = "内存大小不能为空")
    private String memorySize;

    @Schema(description = "系统盘")
    @NotBlank(message = "系统盘不能为空")
    private String systemDisk;

    @Schema(description = "数据盘")
    @NotBlank(message = "数据盘不能为空")
    private String dataDisk;

    @Schema(description = "可扩容容量")
    private String expandable;

    @Schema(description = "可租用截止日期")
    @NotNull(message = "可租用截止日期不能为空")
    private LocalDate rentableUntil;

    @Schema(description = "状态 1上架 2下架 3维护中")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "价格配置")
    @NotEmpty(message = "价格配置不能为空")
    private List<GpuPriceEdit> prices;

    @Schema(description = "GPU总量")
    @NotNull(message = "GPU总量不能为空")
    private Integer totalCount;

    @Schema(description = "空闲数量")
    @NotNull(message = "空闲数量不能为空")
    private Integer availableCount;

    @Schema(description = "组件配置")
    private List<GpuComponentItemEdit> components;

    @Data
    @Schema(description = "GPU资源组件配置")
    public static class GpuComponentItemEdit {

        @Schema(description = "组件ID")
        private Long componentId;

        @Schema(description = "组件名称")
        private String componentName;

        @Schema(description = "基础镜像")
        private String baseImage;

        @Schema(description = "镜像地址")
        private String imageAddress;

        @Schema(description = "排序")
        private Integer sortOrder;
    }
}
