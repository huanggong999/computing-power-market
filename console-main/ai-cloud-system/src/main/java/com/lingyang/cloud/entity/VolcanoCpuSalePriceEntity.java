package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("volcano_cpu_sale_price")
public class VolcanoCpuSalePriceEntity extends BaseEntity {
    @TableField("region_code") private String regionCode;
    @TableField("instance_type_id") private String instanceTypeId;
    @TableField("family") private String family;
    @TableField("cpu_cores") private Integer cpuCores;
    @TableField("cpu_model") private String cpuModel;
    @TableField("mem_gib") private BigDecimal memGib;
    @TableField("billing_type") private String billingType;
    @TableField("upstream_price") private BigDecimal upstreamPrice;
    @TableField("sale_price") private BigDecimal salePrice;
    @TableField("currency") private String currency;
    @TableField("status") private Integer status;
    @TableField("remark") private String remark;
    @TableField("price_version") private String priceVersion;
}
