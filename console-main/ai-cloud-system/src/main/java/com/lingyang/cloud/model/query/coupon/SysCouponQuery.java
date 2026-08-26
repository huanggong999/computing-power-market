package com.lingyang.cloud.model.query.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.coupon.CouponTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collection;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:06
 */
@Data
public class SysCouponQuery {

    @Schema(description = "id列表")
    private Collection<Long> couponIdList;

    @Schema(description = "name")
    private String name;

    /**
     * 优惠卷类型
     */
    @Schema(description = "优惠卷类型")
    private CouponTypeEnum type;

    @Schema(description = "状态")
    private StatusEnum status;

    @Schema(description = "有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useStartTime;

    @Schema(description = "有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useEndTime;

    /**
     * 领取开始时间
     */
    @Schema(description = "领取开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTimeStart;

    /**
     * 领取结束时间
     */
    @Schema(description = "领取结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTimeEnd;


}
