package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * GPU规格定义表
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_resource_spec")
@Schema(description = "GPU规格定义表")
public class GpuResourceSpecEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("model")
    @Schema(description = "规格名称/GPU型号")
    private String model;

    @TableField("vram")
    @Schema(description = "显存大小")
    private String vram;

    @TableField("architecture")
    @Schema(description = "架构")
    private String architecture;

    @TableField("description")
    @Schema(description = "型号描述")
    private String description;

    @TableField("tags")
    @Schema(description = "标签JSON")
    private String tags;

    @TableField("cluster_node_name")
    @Schema(description = "来源GPU集群节点名称")
    private String clusterNodeName;

    @TableField("cluster_status")
    @Schema(description = "GPU集群节点状态")
    private String clusterStatus;

    @TableField("gpu_count")
    @Schema(description = "GPU总量")
    private Integer gpuCount;

    @TableField("allocated_gpus")
    @Schema(description = "已分配GPU数量")
    private Integer allocatedGpus;

    @TableField("available_gpus")
    @Schema(description = "可用GPU数量")
    private Integer availableGpus;

    @TableField("cpu_total_cores")
    @Schema(description = "CPU总核数")
    private BigDecimal cpuTotalCores;

    @TableField("cpu_model")
    @Schema(description = "CPU型号")
    private String cpuModel;

    @TableField("memory_total_gi")
    @Schema(description = "内存总量(GiB)")
    private BigDecimal memoryTotalGi;

    @TableField("disk_total_gi")
    @Schema(description = "磁盘总量(GiB)")
    private BigDecimal diskTotalGi;

    @TableField("sort_order")
    @Schema(description = "排序")
    private Integer sortOrder;

    @TableField("status")
    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
