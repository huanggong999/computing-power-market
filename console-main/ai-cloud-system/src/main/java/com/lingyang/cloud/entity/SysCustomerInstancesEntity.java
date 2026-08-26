package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-11-20
 */

@Data
@TableName("sys_customer_instances")
public class SysCustomerInstancesEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6632232653150648813L;

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
     * 实例id
     */
    @TableField("instance_id")
    private String instanceId;


    /**
     * ecs类型
     */
    @TableField("ecs_type")
    @Schema(description = "ecs类型")
    private EcsTypeEnum ecsType;

    /**
     * 规格
     */
    @TableField("ecs_scale")
    private String ecsScale;

    /**
     * cpu数量
     */
    @TableField("cpu_number")
    private Long cpuNumber;

    /**
     * 内存大小
     */
    @TableField("memory_size")
    private Long memorySize;

    /**
     * cpu型号
     */
    @TableField("cpu_model")
    private String cpuModel;

    /**
     * 显卡型号
     */
    @TableField("gpu_model")
    private String gpuModel;

    /**
     * 显卡显存
     */
    @TableField("gpu_memory")
    private String gpuMemory;

    /**
     * 实列描述
     */
    @TableField("description")
    private String description;

    /**
     * 实列名称
     */
    @TableField("instance_name")
    private String instanceName;

    /**
     * 主机名称
     */
    @TableField("host_name")
    private String hostName;

    /**
     * 镜像id
     */
    @TableField("image_id")
    private String imageId;

    /**
     * 是否开启installRunCommandAgent
     */
    @TableField("command_agent")
    private Boolean commandAgent;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 区域
     */
    @TableField("region")
    private SourceRegionsEnum region;

    /**
     * 可用区id
     */
    @TableField("zone_id")
    private String zoneId;
    /**
     * 资源密码
     */
    @TableField("password")
    private String password;

    /**
     * 计费类型：按量计费（后付费），包年包月（先付费）
     */
    @TableField("charge_type")
    private SourceChargeTypeEnum chargeType;

    @TableField("duration")
    private Integer duration;

    /**
     * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
     */
    @TableField("duration_unit")
    private SourceChargeUnitEnum durationUnit;
    /**
     * 实列状态
     */
    @TableField("status")
    private EcsStatusEnum status;

    /**
     * 启动时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 停止时间
     */
    @TableField("stop_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date stopTime;

    /**
     * 到期时间
     */
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 产品类型（1火山云引擎 2自建服务器）
     */
    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    @TableField("product_type")
    private Integer productType;

    /**
     * 带宽(M) 数量
     */
    @TableField("bandwidth")
    @Schema(description = "带宽(M) 数量")
    private Integer bandwidth;


    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
