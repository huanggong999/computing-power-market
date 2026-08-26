package com.lingyang.cloud.model.vo.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.coupon.CouponRangeEnum;
import com.lingyang.cloud.enums.coupon.CouponReceiveEnum;
import com.lingyang.cloud.enums.coupon.CouponTypeEnum;
import com.lingyang.cloud.enums.coupon.CouponUseStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/24 15:14
 */
@Data
public class SysCustomerCouponListVO {
    @Schema(description = "客户优惠卷id")
    private Long id;

    @Schema(description = "优惠卷id")
    private Long couponId;
    /**
     * 优惠卷名称
     */
    @Schema(description = "优惠卷名称")
    private String name;

    /**
     * 优惠卷类型
     */
    @Schema(description = "优惠卷类型")
    private CouponTypeEnum type;

    /**
     * 满足金额
     */
    @Schema(description = "满足金额")
    private BigDecimal thresholdAmount;

    /**
     * 抵扣金额
     */
    @Schema(description = "抵扣金额")
    private BigDecimal deductionAmount;

    /**
     * 使用开始时间
     */
    @Schema(description = "使用开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTimeStart;

    /**
     * 使用截至时间
     */
    @Schema(description = "使用截至时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTimeEnd;

    /**
     * 领取条件，1 无条件，2 新人
     */
    @Schema(description = "领取条件")
    private CouponReceiveEnum receiveType;

    /**
     * 优惠卷数量
     */
    @Schema(description = "优惠卷数量")
    private Integer count;

    /**
     * 范围类型 枚举定义
     */
    @Schema(description = "使用范围")
    private CouponRangeEnum rangeType;

    /**
     * 1上架 0下架
     */
    @Schema(description = "状态")
    private CouponUseStatusEnum status;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 优惠卷备注
     */
    @Schema(description = "优惠卷备注")
    private String couponRemark;

}
