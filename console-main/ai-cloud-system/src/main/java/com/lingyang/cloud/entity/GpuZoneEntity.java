package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * GPU专区配置表
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_zone")
@Schema(description = "GPU专区配置表")
public class GpuZoneEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("zone_code")
    @Schema(description = "专区编码")
    private String zoneCode;

    @TableField("zone_name")
    @Schema(description = "专区名称")
    private String zoneName;

    @TableField("region_code")
    @Schema(description = "所属地区编码")
    private String regionCode;

    @TableField("sort_order")
    @Schema(description = "排序")
    private Integer sortOrder;

    @TableField("status")
    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
