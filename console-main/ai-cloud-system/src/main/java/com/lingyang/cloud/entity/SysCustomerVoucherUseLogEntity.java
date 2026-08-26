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
 * @Author: 王小龙
 * @Date: 2024-11-14
 */

@Data
@TableName("sys_customer_voucher_use_log")
public class SysCustomerVoucherUseLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1736712432924978110L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    /**
     * 客户代金卷id
     */
    @TableField("customer_voucher_id")
    private Long customerVoucherId;

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
