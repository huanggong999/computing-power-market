package com.lingyang.cloud.enums.carousel;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarouselSkipTypeEnum {

    ACTIVITY(1, "活动中心"),
    COUPON(2, "优惠券"),
    BUDDY(3, "合作伙伴")
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }

}
