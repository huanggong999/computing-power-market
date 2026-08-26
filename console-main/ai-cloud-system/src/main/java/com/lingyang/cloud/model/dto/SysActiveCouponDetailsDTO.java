package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 16:28
 */
@Data
public class SysActiveCouponDetailsDTO extends SysActiveCouponDTO {
    /**
     * 赠卷类型（1注册送 2注册后达标送）
     */
    @Schema(description = "赠卷类型（1注册送 2注册后达标送）")
    private Integer couponType;

    /**
     * 下单达标金额
     */
    @Schema(description = "下单达标金额")
    private String targetAmount;
}
