package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * GPU资源实时库存表
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_resource_stock")
@Schema(description = "GPU资源实时库存表")
public class GpuResourceStockEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("resource_id")
    @Schema(description = "关联资源ID")
    private Long resourceId;

    @TableField("available_count")
    @Schema(description = "空闲GPU数量")
    private Integer availableCount;

    @TableField("total_count")
    @Schema(description = "GPU总量")
    private Integer totalCount;

    @TableField("last_sync_time")
    @Schema(description = "上次同步时间")
    private LocalDateTime lastSyncTime;
}
