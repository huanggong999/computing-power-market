package com.lingyang.cloud.enums.order;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:27
 */
@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    CANCELED(2, "已取消"),
    REFUNDED(3, "已退款"),
    ;
    @EnumValue
    private final int code;

    private final String desc;
    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
