package com.lingyang.cloud.model.query.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU组件查询参数
 */
@Data
@Schema(description = "GPU组件查询参数")
public class GpuComponentQuery {

    @Schema(description = "组件名称")
    private String componentName;

    @Schema(description = "组件名称，兼容前端旧查询参数")
    private String name;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
