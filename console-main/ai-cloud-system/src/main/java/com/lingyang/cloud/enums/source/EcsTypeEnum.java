package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 14:31
 */
@Getter
@AllArgsConstructor
public enum EcsTypeEnum {
    // 通用型
    GENERAL_COMPUTE(1, "通用型计算"),
    // 计算型
    COMPUTE(2, "计算型"),
    // 内存型
    GENERAL(3, "通用型"),
    // GPU计算型
    GPU(4, "GPU"),

    ;

    @EnumValue
    private final int code;

    private final String desc;
    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
