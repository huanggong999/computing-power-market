package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 计费类型单位枚举
 * @Author: 王小龙
 * @Date: 2024/11/12 16:39
 */
@AllArgsConstructor
@Getter
public enum SourceChargeUnitEnum {
    // 小时
    HOUR(1, "小时", ""),
    // 容量
    CAPACITY(2, "GB", ""),
    //天
    DAY(5, "天", "Day"),
    // 月
    MONTH(3, "月", "Month"),
    // 年
    YEAR(4, "年", "Year"),
    ;

    @EnumValue
    private final int code;

    private final String desc;
    private final String volcengineDesc;
    @Override
    public String toString() {
        return name() + " = " + desc;
    }

}
