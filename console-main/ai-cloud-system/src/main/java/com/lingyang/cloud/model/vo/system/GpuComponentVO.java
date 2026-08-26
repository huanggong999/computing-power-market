package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU组件管理列表项
 */
@Data
@Schema(description = "GPU组件管理列表项")
public class GpuComponentVO {

    @Schema(description = "组件ID")
    private Long id;

    @Schema(description = "组件名称")
    private String componentName;

    @Schema(description = "组件版本")
    private String componentVersion;

    @Schema(description = "Python版本")
    private String pythonVersion;

    @Schema(description = "操作系统名称")
    private String osName;

    @Schema(description = "操作系统版本")
    private String osVersion;

    @Schema(description = "CUDA版本")
    private String cudaVersion;

    @Schema(description = "基础镜像")
    private String baseImage;

    @Schema(description = "镜像地址")
    private String imageAddress;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
