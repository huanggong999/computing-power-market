package com.lingyang.cloud.model.query.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU集群查询参数
 */
@Data
@Schema(description = "GPU集群查询参数")
public class GpuClusterQuery {

    @Schema(description = "集群名称")
    private String clusterName;

    @Schema(description = "地域")
    private String region;

    @Schema(description = "状态")
    private String status;
}
