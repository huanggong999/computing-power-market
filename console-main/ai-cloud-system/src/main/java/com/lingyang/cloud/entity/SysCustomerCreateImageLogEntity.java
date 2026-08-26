package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/5 15:09
 */
@Data
@TableName("sys_customer_create_image_log")
public class SysCustomerCreateImageLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID =  9144880375116792770L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 镜像id
     */
    @TableField("image_id")
    private String imageId;

    /**
     * 镜像名称
     */
    @TableField("image_name")
    private String imageName;


    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
