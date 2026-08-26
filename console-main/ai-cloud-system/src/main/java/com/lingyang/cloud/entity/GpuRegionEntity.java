package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * GPU地区配置表
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_region")
@Schema(description = "GPU地区配置表")
public class GpuRegionEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("region_code")
    @Schema(description = "地区编码")
    private String regionCode;

    @TableField("region_name")
    @Schema(description = "地区名称")
    private String regionName;

    @TableField("sort_order")
    @Schema(description = "排序")
    private Integer sortOrder;

    @TableField("status")
    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
