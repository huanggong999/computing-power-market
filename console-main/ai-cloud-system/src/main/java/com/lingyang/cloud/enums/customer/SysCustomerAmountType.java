package com.lingyang.cloud.enums.customer;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 16:49
 */

@AllArgsConstructor
@Getter
public enum SysCustomerAmountType {
    //余额
    BALANCE(0, "余额"),
    // 代金卷
    VOUCHER(1, "代金卷"),
    //授信额
    CREDIT(2, "授信额"),
    ;
    @EnumValue
    private final Integer code;
    private final String desc;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
