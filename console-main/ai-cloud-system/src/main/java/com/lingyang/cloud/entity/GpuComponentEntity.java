package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * GPU基础组件镜像配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_component")
@Schema(description = "GPU基础组件镜像配置表")
public class GpuComponentEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("component_name")
    @Schema(description = "组件名称")
    private String componentName;

    @TableField("component_version")
    @Schema(description = "组件版本")
    private String componentVersion;

    @TableField("python_version")
    @Schema(description = "Python版本")
    private String pythonVersion;

    @TableField("os_name")
    @Schema(description = "操作系统名称")
    private String osName;

    @TableField("os_version")
    @Schema(description = "操作系统版本")
    private String osVersion;

    @TableField("cuda_version")
    @Schema(description = "CUDA版本")
    private String cudaVersion;

    @TableField("base_image")
    @Schema(description = "基础镜像")
    private String baseImage;

    @TableField("image_address")
    @Schema(description = "镜像地址")
    private String imageAddress;

    @TableField("description")
    @Schema(description = "说明")
    private String description;

    @TableField("sort_order")
    @Schema(description = "排序")
    private Integer sortOrder;

    @TableField("status")
    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
