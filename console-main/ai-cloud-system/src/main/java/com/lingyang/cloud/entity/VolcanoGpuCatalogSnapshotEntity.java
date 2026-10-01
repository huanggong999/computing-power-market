package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("volcano_gpu_catalog_snapshot")
@Schema(description = "火山云GPU目录快照")
public class VolcanoGpuCatalogSnapshotEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("id")
    @Schema(description = "目录快照ID")
    private Long id;

    @TableField("billing_scope")
    @Schema(description = "计费范围")
    private String billingScope;

    @TableField("catalog_json")
    @Schema(description = "聚合目录JSON")
    private String catalogJson;

    @TableField("source")
    @Schema(description = "目录来源")
    private String source;

    @TableField("refresh_time")
    @Schema(description = "刷新时间")
    private LocalDateTime refreshTime;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
