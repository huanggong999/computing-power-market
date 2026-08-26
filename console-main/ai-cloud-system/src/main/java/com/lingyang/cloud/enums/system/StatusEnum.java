package com.lingyang.cloud.enums.system;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/27 16:37
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    OK(0, "正常"),
    DEACTIVATED(1, "停用"),
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }
}
