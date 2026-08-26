package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * GPU集群列表项
 */
@Data
@Schema(description = "GPU集群列表项")
public class GpuClusterVO {

    @Schema(description = "集群ID")
    private String clusterId;

    @Schema(description = "集群名称")
    private String clusterName;

    @Schema(description = "地域")
    private String region;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "节点总数")
    private Integer nodeCount;

    @Schema(description = "Ready节点数")
    private Integer readyNodeCount;

    @Schema(description = "GPU总量")
    private Integer gpuTotal;

    @Schema(description = "可用GPU")
    private Integer gpuAvailable;

    @Schema(description = "GPU型号")
    private String gpuModels;

    @Schema(description = "标签")
    private String labels;

    @Schema(description = "GPU使用量")
    private String usageGpu;

    @Schema(description = "内存使用量GiB")
    private String usageMemoryGi;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "操作")
    private List<String> operations;

    @Schema(description = "状态")
    private String status;
}
