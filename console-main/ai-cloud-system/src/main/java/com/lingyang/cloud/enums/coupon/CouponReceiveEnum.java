package com.lingyang.cloud.enums.coupon;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 优惠卷领取条件枚举
 * @Author: 王小龙
 * @Date: 2024/10/23 12:02
 */
@AllArgsConstructor
@Getter
public enum CouponReceiveEnum {
    UNCONDITIONAL(0, "无条件领取"),
    NEW_USER(1, "新人领取"),
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }
}
