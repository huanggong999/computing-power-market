package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-11-20
 */

@Data
@TableName("sys_customer_volume")
public class SysCustomerVolumeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8171848688182500560L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

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
     * 实例id
     */
    @TableField("instance_id")
    private String instanceId;
    /**
     * 云盘实例id
     */
    @TableField("volume_id")
    @Schema(description = "云盘实例id")
    private String volumeId;

    /**
     * 云盘类型: system 系统盘，data 数据盘
     */
    @TableField("kind")
    @Schema(description = "云盘类型: system 系统盘，data 数据盘")
    private String kind;

    /**
     * 云盘类型
     */
    @TableField("volume_type")
    @Schema(description = "云盘类型")
    private String volumeType;

    /**
     * 容量
     */
    @Schema(description = "容量")
    @TableField("size")
    private Integer size;

    /**
     * 区域id
     */
    @Schema(description = "区域id")
    @TableField("region_id")
    private SourceRegionsEnum regionId;

    /**
     * 可用区id
     */
    @Schema(description = "可用区id")
    @TableField("zone_id")
    private String zoneId;


    /**
     * 计费方式
     */
    @Schema(description = "计费方式")
    @TableField("charge_type")
    private SourceChargeTypeEnum chargeType;
}
