package com.lingyang.cloud.enums.carousel;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarouselTypeEnum {

    HOME(1, "首页")
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }

}
