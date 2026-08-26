package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("sys_customer_network")
public class SysCustomerNetwork extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 产品id
     */
    @TableField("network_id")
    @Schema(description = "产品id")
    private Long networkId;

    /**
     * 折扣比例
     */
    @TableField("discount_ration")
    @Schema(description = "折扣比例")
    private BigDecimal discountRation;

    /**
     * 产品名称
     */
    @TableField(exist = false)
    @Schema(description = "产品名称")
    private String networkName;

}
