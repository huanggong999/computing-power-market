package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-11-20
 */

@Data
@TableName("sys_customer_eip")
public class SysCustomerEipEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5660648293819682913L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单资源uid
     */
    @TableField("order_source_uid")
    private String orderSourceUid;

    /**
     * 客户实列id
     */
    @TableField("customer_instance_id")
    private Long customerInstanceId;

    /**
     * 实列id
     */
    @TableField("instance_id")
    private String instanceId;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 公网id
     */
    @TableField("eip_id")
    private String eipId;

    /**
     * 公网名称
     */
    @TableField("eip_name")
    private String eipName;

    /**
     * 公网描述
     */
    @TableField("eip_desc")
    private String eipDesc;

    /**
     * 线路类型
     */
    @TableField("isp")
    private String isp;

    /**
     * 公网地址
     */
    @TableField("eip_address")
    private String eipAddress;

    /**
     * 计费方式
     */
    @TableField("billing_type")
    private String billingType;

    /**
     * 计费周期
     */
    @TableField("period_unit")
    private Long periodUnit;

    /**
     * 带宽
     */
    @TableField("bandwidth")
    private Integer bandwidth;

}
