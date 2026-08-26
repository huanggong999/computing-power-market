package com.lingyang.cloud.enums.customer;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 16:49
 */
@Getter
@AllArgsConstructor
public enum SysTransactionType {
    // 充值
    RECHARGE(0, "充值", false),
    // 平台发放
    PLATFORM_GRANT(1, "平台发放", false),
    // 退款
    REFUND(2, "退款", false),
    // 支付抵扣
    PAY_DISCOUNT(3, "支付抵扣", true),
    ;

    @EnumValue
    private final Integer code;
    private final String desc;
    private final boolean isPay;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
