package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.coupon.CouponRangeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 16:28
 */
@Data
public class SysActiveCouponDTO {
    @Schema(description = "优惠券id")
    private Long id;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "金额")
    private String deductionAmount;

    @Schema(description = "优惠券类型")
    private Integer type;

    @Schema(description = "适用范围")
    private CouponRangeEnum rangeType;

    @Schema(description = "已领取数量")
    private Integer receiveNum;

    /**
     * 优惠卷数量
     */
    @Schema(description = "优惠卷数量")
    private Integer count;

    /**
     * 使用开始时间
     */
    @Schema(description = "使用开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date useTimeStart;

    /**
     * 使用截至时间
     */
    @Schema(description = "使用截至时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date useTimeEnd;

    @Schema(description = "状态，正常 = 上架，停用=下架")
    private StatusEnum status;
}
