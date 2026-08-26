package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.customer.SysCustomerAmountType;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-12-03
 */

@Data
@TableName("sys_customer_amount_log")
public class SysCustomerAmountLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 86543114946069132L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    /**
     * 订单id
     */
    @TableField("order_id")
    @Schema(description = "订单id")
    private Long orderId;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 客户id
     */
    @Schema(description = "客户id")
    @TableField("customer_id")
    private Long customerId;

    /**
     * 金额类型，余额/代金卷
     */
    @Schema(description = "金额类型")
    @TableField("amount_type")
    private SysCustomerAmountType amountType;

    /**
     * 交易类型
     */
    @Schema(description = "交易类型")
    @TableField("transaction_type")
    private SysTransactionType transactionType;

    /**
     * 之前金额
     */
    @Schema(description = "之前金额")
    @TableField("before_amount")
    private BigDecimal beforeAmount;

    /**
     * 操作金额
     */
    @Schema(description = "操作金额")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 剩余金额
     */
    @Schema(description = "剩余金额")
    @TableField("after_amount")
    private BigDecimal afterAmount;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 手机号-登陆账号
     */
    @TableField(exist = false)
    private String phone;



}
