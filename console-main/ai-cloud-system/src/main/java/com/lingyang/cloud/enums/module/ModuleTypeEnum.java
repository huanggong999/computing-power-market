package com.lingyang.cloud.enums.module;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleTypeEnum {

    MODEL(1, "模型"),
    DATA(2, "数据")
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }

}
