package com.lingyang.cloud.enums.order;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:26
 */
@AllArgsConstructor
@Getter
public enum OrderOnlinePayEnum {

    ALI_PAY(0, "支付宝支付"),

    WECHAT_PAY(1, "微信支付"),
    REMIT_PAY(2, "线下打款");;

    @EnumValue
    private final int code;

    private final String desc;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
