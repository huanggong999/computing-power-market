package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * GPU节点池列表项
 */
@Data
@Schema(description = "GPU节点池列表项")
public class GpuNodePoolVO {

    @Schema(description = "节点池名称")
    private String poolName;

    @Schema(description = "GPU型号")
    private String gpuModel;

    @Schema(description = "节点总数")
    private Integer nodeCount;

    @Schema(description = "Ready节点数")
    private Integer readyNodeCount;

    @Schema(description = "GPU总量")
    private Integer gpuTotal;

    @Schema(description = "已分配GPU")
    private Integer allocatedGpus;

    @Schema(description = "可用GPU")
    private Integer availableGpus;

    @Schema(description = "CPU总核数")
    private BigDecimal cpuTotalCores;

    @Schema(description = "内存总量GiB")
    private BigDecimal memoryTotalGi;

    @Schema(description = "磁盘总量GiB")
    private BigDecimal diskTotalGi;

    @Schema(description = "状态")
    private String status;
}
