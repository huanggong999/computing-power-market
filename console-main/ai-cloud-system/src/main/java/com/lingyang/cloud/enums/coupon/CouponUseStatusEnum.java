package com.lingyang.cloud.enums.coupon;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 优惠卷可使用状态
 * @Author: 王小龙
 * @Date: 2024/10/24 11:15
 */
@AllArgsConstructor
@Getter
public enum CouponUseStatusEnum {
    // 未开始，待使用， 已使用，失效
    UNUSED(0,"未开始"),
    WAITING(1,"待使用"),
    USED(2,"已使用"),
    EXPIRED(3,"已过期"),
    ;
    @EnumValue
    private final int code;

    private final String desc;
}
