package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDate;

/**
 * GPU资源主表
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_resource")
@Schema(description = "GPU资源主表")
public class GpuResourceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("resource_no")
    @Schema(description = "资源编号")
    private String resourceNo;

    @TableField("machine_id")
    @Schema(description = "机器编号")
    private String machineId;

    @TableField("machine_uuid")
    @Schema(description = "机器唯一标识")
    private String machineUuid;

    @TableField("region_code")
    @Schema(description = "地区编码")
    private String regionCode;

    @TableField("zone_code")
    @Schema(description = "专区编码")
    private String zoneCode;

    @TableField("cluster_id")
    @Schema(description = "来源GPU集群ID")
    private String clusterId;

    @TableField("cluster_name")
    @Schema(description = "来源GPU集群名称")
    private String clusterName;

    @TableField("cluster_node_name")
    @Schema(description = "来源GPU集群节点名称")
    private String clusterNodeName;

    @TableField("spec_id")
    @Schema(description = "关联规格ID")
    private Long specId;

    @TableField("gpu_count")
    @Schema(description = "单台机器GPU数量")
    private Integer gpuCount;

    @TableField("gpu_driver")
    @Schema(description = "GPU驱动版本")
    private String gpuDriver;

    @TableField("cuda_version")
    @Schema(description = "CUDA版本")
    private String cudaVersion;

    @TableField("cache_optimized")
    @Schema(description = "是否缓存优化 0否 1是")
    private Integer cacheOptimized;

    @TableField("cpu_cores")
    @Schema(description = "CPU核心数")
    private Integer cpuCores;

    @TableField("cpu_model")
    @Schema(description = "CPU型号全称")
    private String cpuModel;

    @TableField("memory_size")
    @Schema(description = "内存大小")
    private String memorySize;

    @TableField("system_disk")
    @Schema(description = "系统盘")
    private String systemDisk;

    @TableField("data_disk")
    @Schema(description = "数据盘")
    private String dataDisk;

    @TableField("expandable")
    @Schema(description = "可扩容容量")
    private String expandable;

    @TableField("rentable_until")
    @Schema(description = "可租用截止日期")
    private LocalDate rentableUntil;

    @TableField("status")
    @Schema(description = "状态 1上架 2下架 3维护中")
    private Integer status;
}
