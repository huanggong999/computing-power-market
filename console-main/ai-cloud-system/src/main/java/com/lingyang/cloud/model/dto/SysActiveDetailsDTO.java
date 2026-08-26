package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.entity.SysActiveCenterEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 16:28
 */
@Data
public class SysActiveDetailsDTO {
    @Schema(description = "活动id")
    private Long id;

    @Schema(description = "活动名称")
    private String name;


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

    @Schema(description = "注册送优惠券列表")
    private List<SysActiveCouponDTO> list;

    /**
     * 下单达标金额
     */
    @Schema(description = "下单达标金额")
    private BigDecimal targetAmount;

    @Schema(description = "达标送优惠券列表")
    private List<SysActiveCouponDTO> list2;

    @Schema(description = "分享链接")
    private String link;

    @Schema(description = "推荐活动列表")
    private List<SysActiveCenterEntity> recommendedList;
}
