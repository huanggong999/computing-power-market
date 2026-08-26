package com.lingyang.cloud.model.edit.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * GPU规格编辑参数
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU规格编辑参数")
public class GpuSpecEdit {

    @Schema(description = "规格ID，null=新增")
    private Long id;

    @Schema(description = "GPU型号")
    @NotBlank(message = "GPU型号不能为空")
    private String model;

    @Schema(description = "显存")
    @NotBlank(message = "显存不能为空")
    private String vram;

    @Schema(description = "架构")
    private String architecture;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "标签JSON字符串，如 [\"热门\",\"最新\"]")
    private String tags;

    @Schema(description = "来源GPU集群节点名称")
    private String clusterNodeName;

    @Schema(description = "GPU集群节点状态")
    private String clusterStatus;

    @Schema(description = "GPU总量")
    private Integer gpuCount;

    @Schema(description = "已分配GPU数量")
    private Integer allocatedGpus;

    @Schema(description = "可用GPU数量")
    private Integer availableGpus;

    @Schema(description = "CPU总核数")
    private BigDecimal cpuTotalCores;

    @Schema(description = "CPU型号")
    private String cpuModel;

    @Schema(description = "内存总量(GiB)")
    private BigDecimal memoryTotalGi;

    @Schema(description = "磁盘总量(GiB)")
    private BigDecimal diskTotalGi;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
