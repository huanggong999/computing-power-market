package com.lingyang.cloud.enums.order;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:23
 */
@AllArgsConstructor
@Getter
public enum OrderTypeEnum {
    // 新购资源订单
    NEW_RESOURCE(0, "新购资源订单"),
    // 续费资源订单
    RENEW_RESOURCE(1, "续费资源订单"),
    // 余额充值订单
    BALANCE(2, "余额充值订单"),
    // 网络产品支付订单
    PRODUCT(3, "网络产品支付订单"),
    RENEW_PRODUCT(4, "续费网络产品订单"),
    // 升级网络产品（账户数、带宽、IP）
    UPGRADE_PRODUCT(5, "升级网络产品"),
    ;
    @EnumValue
    private final int code;
    private final String desc;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
