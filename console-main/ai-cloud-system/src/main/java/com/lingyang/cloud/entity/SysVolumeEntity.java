package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-11-05
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_volume")
public class SysVolumeEntity extends SysPriceEntity {

    @Serial
    private static final long serialVersionUID = 3589786767369053180L;

    /**
     * 云盘属性， system 系统盘，data 数据盘
     */
    @TableField("kind")
    private String kind;

    /**
     * 云盘类型 essd
     */
    @TableField("volume_type")
    private String volumeType;

    /**
     * 云盘规格
     */
    @TableField("volume_scale")
    private String volumeScale;

    /**
     * 云盘容量
     */
    @TableField("volume_capacity")
    private Integer volumeCapacity;

    /**
     * io_ps
     */
    @TableField("io_ps")
    private Integer ioPs;

    @TableField("io_ps_step_size")
    private BigDecimal ioPsStepSize;

    /**
     * 吞吐量
     */
    @TableField("throughput")
    private BigDecimal throughput;

    @TableField("throughput_step_size")
    private BigDecimal throughputStepSize;

    /**
     * 可用区
     */
    @TableField("regions_zones")
    private String regionsZones;
}
