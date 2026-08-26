package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.coupon.CouponRangeEnum;
import com.lingyang.cloud.enums.coupon.CouponReceiveEnum;
import com.lingyang.cloud.enums.coupon.CouponTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-10-23
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_coupon")
public class SysCouponEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1887605274145760110L;

    /**
     * 优惠卷名称
     */
    @Schema(description = "优惠卷名称")
    @TableField("name")
    @NotEmpty(message = "优惠卷名称为空")
    private String name;

    /**
     * 优惠卷类型
     */
    @NotNull(message = "优惠卷类型为空")
    @Schema(description = "优惠卷类型")
    @TableField("type")
    private CouponTypeEnum type;

    /**
     * 满足金额
     */
    @Schema(description = "满足金额")
    @TableField("threshold_amount")
    private BigDecimal thresholdAmount;

    /**
     * 抵扣金额
     */
    @Schema(description = "抵扣金额")
    @TableField("deduction_amount")
    private BigDecimal deductionAmount;

    /**
     * 领取次数
     */
    @Schema(description = "领取次数")
    @TableField("receive_num")
    private Integer receiveNum;

    /**
     * 领取开始时间
     */
    @NotNull(message = "领取开始时间为空")
    @Schema(description = "领取开始时间")
    @TableField("receive_time_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTimeStart;

    /**
     * 领取结束时间
     */
    @NotNull(message = "领取结束时间为空")
    @Schema(description = "领取结束时间")
    @TableField("receive_time_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTimeEnd;

    /**
     * 使用开始时间
     */
    @NotNull(message = "使用开始时间为空")
    @Schema(description = "使用开始时间")
    @TableField("use_time_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTimeStart;

    /**
     * 使用截至时间
     */
    @NotNull(message = "使用截至时间为空")
    @Schema(description = "使用截至时间")
    @TableField("use_time_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTimeEnd;

    /**
     * 领取条件，1 无条件，2 新人
     */
    @NotNull(message = "领取条件为空")
    @Schema(description = "领取条件")
    @TableField("receive_type")
    private CouponReceiveEnum receiveType;

    /**
     * 优惠卷数量
     */
    @NotNull(message = "优惠卷数量为空")
    @Schema(description = "优惠卷数量")
    @TableField("count")
    private Integer count;

    /**
     * 范围类型 枚举定义
     */
    @NotNull(message = "范围类型为空")
    @Schema(description = "使用范围")
    @TableField("range_type")
    private CouponRangeEnum rangeType;

    /**
     * 1上架 0下架
     */
    @NotNull(message = "状态为空")
    @Schema(description = "状态，正常 = 上架，停用=下架")
    @TableField("status")
    private StatusEnum status;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    /**
     * 优惠卷备注
     */
    @Schema(description = "优惠卷备注")
    @TableField("coupon_remark")
    private String couponRemark;

    @Schema(description = "当前客户是否领取, true 已领取，false 未领取")
    @TableField(exist = false)
    private boolean customerReceive;
}