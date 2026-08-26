package com.lingyang.cloud.handler.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/17 11:39
 */
@Data
public class OnlinePayRefundsParam {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付单号
     */
    private String payNumber;

    /**
     * 退款金额
     */
    private BigDecimal amount;

    /**
     *  实际支付金额
     */
    private BigDecimal payTotalAmount;
    /**
     *  退款原因
     */
    private String reason;

}
