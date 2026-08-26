package com.lingyang.cloud.model.vo.active;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 16:15
 */
@Data
public class SysActiveVO {

    @Schema(description = "活动ID")
    private Long id;

    /**
     * 活动名称
     */
    @Schema(description = "活动名称")
    private String name;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 活动封面图地址
     */
    @Schema(description = "活动封面图地址")
    private String picture;

    /**
     * 活动详情介绍
     */
    @Schema(description = "活动详情介绍")
    private String details;

    /**
     * 注册送的优惠卷ID集合
     */
    @Schema(description = "注册送的优惠卷ID集合")
    private List<Long> couponIds;

    /**
     * 达标送的优惠卷ID集合
     */
    @Schema(description = "达标送的优惠卷ID集合")
    private List<Long> couponIds2;

    /**
     * 下单达标金额
     */
    @Schema(description = "下单达标金额")
    private BigDecimal targetAmount;
}
