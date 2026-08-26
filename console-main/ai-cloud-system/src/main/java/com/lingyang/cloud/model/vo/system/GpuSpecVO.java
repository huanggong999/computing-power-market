package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * GPU规格管理VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU规格管理列表项")
public class GpuSpecVO {

    @Schema(description = "规格ID")
    private Long id;

    @Schema(description = "规格名称")
    private String name;

    @Schema(description = "GPU型号")
    private String model;

    @Schema(description = "显存")
    private String vram;

    @Schema(description = "架构")
    private String architecture;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "标签")
    private List<String> tags;

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
