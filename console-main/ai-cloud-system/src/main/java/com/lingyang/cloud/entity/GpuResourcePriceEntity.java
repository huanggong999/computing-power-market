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
 * GPU资源价格表
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gpu_resource_price")
@Schema(description = "GPU资源价格表")
public class GpuResourcePriceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("resource_id")
    @Schema(description = "关联资源ID")
    private Long resourceId;

    @TableField("billing_type")
    @Schema(description = "计费方式 on_demand/hourly/daily/weekly/monthly")
    private String billingType;

    @TableField("unit_price")
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @TableField("discount_price")
    @Schema(description = "折扣价")
    private BigDecimal discountPrice;

    @TableField("discount_rate")
    @Schema(description = "折扣率")
    private String discountRate;

    @TableField("currency")
    @Schema(description = "币种")
    private String currency;
}
