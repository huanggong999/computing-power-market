package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/17 15:02
 */
@Data
@TableName("sys_customer_image_repository")
public class SysCustomerImageRepositoryEntity implements Serializable {

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
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 实例名称
     */
    @Schema(description = "实例名称")
    @TableField("instance_name")
    private String instanceName;

    @Schema(description = "系统域名")
    @TableField("domain")
    private String domain;

    @Schema(description = "状态")
    @TableField("status")
    private ContainerStatusEnum status;

    @Schema(description = "计费类型")
    @TableField("charge_type")
    private SourceChargeTypeEnum chargeType;

    @Schema(description = "实例密码")
    @TableField("password")
    private String password;

    @Schema(description = "命名空间")
    @TableField("namespace")
    private String namespace;

    @Schema(description = "OCI制品仓库名称")
    @TableField("oci_name")
    private String ociName;

    @Schema(description = "版本")
    @TableField(exist = false)
    private String version = "标准版";

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
