package com.lingyang.cloud.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/27 16:30
 */
@AllArgsConstructor
@Getter
public enum SexEnum {

    MAN(0,"男"),
    WOMAN(1,"女"),
    UNKNOWN(2,"未知"),
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }
}
