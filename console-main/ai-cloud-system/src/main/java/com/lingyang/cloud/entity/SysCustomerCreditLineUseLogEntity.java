package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025-02-05
 */

@Data
@TableName("sys_customer_credit_line_use_log")
public class SysCustomerCreditLineUseLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1736712432924978110L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    /**
     * 客户代金卷id
     */
    @TableField("customer_credit_id")
    private Long customerCreditId;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 使用金额
     */
    @TableField("use_amount")
    private BigDecimal useAmount;
}
