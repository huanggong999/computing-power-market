package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 计费类型枚举
 * @Author: 王小龙
 * @Date: 2024/11/12 16:38
 */
@AllArgsConstructor
@Getter
public enum SourceChargeTypeEnum {

    POSTPAID_BY_HOUR(1, "按量计费", "PostPaid"),
    POSTPAID_BY_MONTH(2, "包年包月", "PrePaid"),
    POSTPAID_BY_YEAR(3, "包年包月", "PrePaid"),
    ONE_PAY(4, "一次性购买", "OnePay")
    ;

    @EnumValue
    private final int code;

    private final String desc;

    private final String volcengineDesc;
    @Override
    public String toString() {
        return name() + " = " + desc;
    }

    public static SourceChargeTypeEnum getTypeByVolcengineDesc(String desc) {
        for (SourceChargeTypeEnum type : SourceChargeTypeEnum.values()) {
            if (type.volcengineDesc.equals(desc)) {
                return type;
            }
        }
        return null;
    }

    public static SourceChargeTypeEnum getByDesc(String desc) {
        for (SourceChargeTypeEnum type : SourceChargeTypeEnum.values()) {
            if (type.getDesc().equals(desc)) {
                return type;
            }
        }
        return null;
    }
}
