package com.lingyang.cloud.handler.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 16:00
 */
@Data
public class OnlinePayParam {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 描述
     */
    private String description;
    /**
     * 订单失效时间
     */
    private Date timeExpire;

    /**
     * 是不是小程序支付
     */
    private Boolean isApplet = false;


    private String openid;

    private String ip;

}
