package com.lingyang.cloud.enums.coupon;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:07
 */
@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    //满减卷
    FULL_REDUCTION(0, "满减卷"),
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }
}
