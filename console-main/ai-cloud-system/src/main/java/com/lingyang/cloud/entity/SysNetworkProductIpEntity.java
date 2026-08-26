package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/4 14:31
 */
@Data
@TableName("sys_network_product_ip")
public class SysNetworkProductIpEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 6632232653150648813L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("product_id")
    @Schema(description = "AGI-C产品id")
    private Long productId;

    @TableField("ip")
    @Schema(description = "内网IP")
    private String ip;

    @TableField("public_ip")
    @Schema(description = "公网IP")
    private String publicIp;

    @TableField("ip_address")
    @Schema(description = "ip地址")
    private String ipAddress;

    @TableField("user_id")
    @Schema(description = "用户id")
    private Long userId;

    @TableField("email")
    @Schema(description = "使用的飞连用户账户")
    private String email;

    @TableField("status")
    @Schema(description = "使用状态（0未使用 1已使用 2等待中）")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "用户名称")
    private String nickname;

    /**
     * 创建时间
     */
    @TableField(value = "use_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "启用时间")
    private Date useTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

}
