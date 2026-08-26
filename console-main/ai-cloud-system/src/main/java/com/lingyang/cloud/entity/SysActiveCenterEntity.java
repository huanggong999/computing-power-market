package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 14:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_active_center")
public class SysActiveCenterEntity extends BaseEntity {

    /**
     * 活动名称
     */
    @Schema(description = "活动名称")
    @NotNull(message = "活动名称不能为空")
    @TableField("name")
    private String name;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    @NotNull(message = "活动开始时间不能为空")
    @TableField("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    @NotNull(message = "活动结束时间不能为空")
    @TableField("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 活动封面图地址
     */
    @Schema(description = "活动封面图地址")
    @TableField("picture")
    private String picture;

    /**
     * 活动详情介绍
     */
    @Schema(description = "活动详情介绍")
    @TableField("details")
    private String details;

    /**
     * 活动状态
     */
    @Schema(description = "活动状态（1未开始 2进行中 3已结束）")
    @TableField("status")
    private Integer status;

    @Schema(description = "下单达标金额")
    @TableField("target_amount")
    private BigDecimal targetAmount;
}
