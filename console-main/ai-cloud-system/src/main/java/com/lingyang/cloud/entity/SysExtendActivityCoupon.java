package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@TableName(value = "sys_extend_activity_coupon", autoResultMap = true)
@Data
public class SysExtendActivityCoupon extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "id")
    private Long id;

    @TableField("activity_id")
    @Schema(description = "活动id")
    private Long activityId;

    @TableField("coupon_id")
    @Schema(description = "优惠券id")
    private Long couponId;


}