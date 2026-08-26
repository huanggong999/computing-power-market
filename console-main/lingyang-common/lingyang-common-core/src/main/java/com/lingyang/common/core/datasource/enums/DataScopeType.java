package com.lingyang.common.core.datasource.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/10 18:08
 */
@AllArgsConstructor
@Getter
public enum DataScopeType{

    ALL(1, Integer.MAX_VALUE,"全部数据权限"),
    CUSTOMIZE(2, 0,"自定义数据权限"),
    SELF(3, Integer.MIN_VALUE,"仅本人数据权限"),
    ;

    @EnumValue
    private final int code;
    /**
     * 权重，权重越大，权限越高
     * 相同权重下，都会拦截处理
     */
    private final int weight;

    private final String desc;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}