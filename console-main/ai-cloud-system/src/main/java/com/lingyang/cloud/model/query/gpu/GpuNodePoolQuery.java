package com.lingyang.cloud.model.query.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU节点池查询参数
 */
@Data
@Schema(description = "GPU节点池查询参数")
public class GpuNodePoolQuery {

    @Schema(description = "GPU型号")
    private String gpuModel;

    @Schema(description = "状态")
    private String status;
}
