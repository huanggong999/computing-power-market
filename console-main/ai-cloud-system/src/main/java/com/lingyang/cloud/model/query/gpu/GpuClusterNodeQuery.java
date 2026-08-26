package com.lingyang.cloud.model.query.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU集群节点查询参数
 */
@Data
@Schema(description = "GPU集群节点查询参数")
public class GpuClusterNodeQuery {

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "GPU型号")
    private String gpuModel;

    @Schema(description = "状态")
    private String status;
}
