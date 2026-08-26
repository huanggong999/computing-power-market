package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * GPU资源组件镜像关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_resource_component")
@Schema(description = "GPU资源组件镜像关联表")
public class GpuResourceComponentEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("resource_id")
    @Schema(description = "资源ID")
    private Long resourceId;

    @TableField("component_id")
    @Schema(description = "组件ID")
    private Long componentId;

    @TableField("component_name")
    @Schema(description = "组件名称")
    private String componentName;

    @TableField("base_image")
    @Schema(description = "基础镜像")
    private String baseImage;

    @TableField("image_address")
    @Schema(description = "镜像地址")
    private String imageAddress;

    @TableField("sort_order")
    @Schema(description = "排序")
    private Integer sortOrder;
}
