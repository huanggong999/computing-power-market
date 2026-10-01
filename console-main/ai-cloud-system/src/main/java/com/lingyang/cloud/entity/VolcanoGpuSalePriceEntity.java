package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 火山云 GPU 平台售价配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("volcano_gpu_sale_price")
@Schema(description = "火山云GPU平台售价配置")
public class VolcanoGpuSalePriceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("region_code")
    @Schema(description = "火山云地域编码")
    private String regionCode;

    @TableField("instance_type_id")
    @Schema(description = "火山云实例规格ID")
    private String instanceTypeId;

    @TableField("gpu_model")
    @Schema(description = "GPU型号快照")
    private String gpuModel;

    @TableField("gpu_memory")
    @Schema(description = "GPU显存快照")
    private String gpuMemory;

    @TableField("gpu_count")
    @Schema(description = "实例GPU卡数快照")
    private Integer gpuCount;

    @TableField("billing_type")
    @Schema(description = "计费类型 on_demand/hourly/daily/weekly/monthly")
    private String billingType;

    @TableField("upstream_price")
    @Schema(description = "保存时火山实际价快照")
    private BigDecimal upstreamPrice;

    @TableField("sale_price")
    @Schema(description = "平台售价")
    private BigDecimal salePrice;

    @TableField("currency")
    @Schema(description = "币种")
    private String currency;

    @TableField("status")
    @Schema(description = "状态 1启用 0停用")
    private Integer status;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;
}
