package com.lingyang.cloud.enums.coupon;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 优惠卷使用范围枚举
 * @Author: 王小龙
 * @Date: 2024/10/23 12:01
 */
@AllArgsConstructor
@Getter
public enum CouponRangeEnum {
    ALL(0, "全部"),
    SERVER(1, "服务器"),
    CONTAINER(2, "容器"),
    IMAGE_REPOSITORY(3, "镜像仓库"),
    OBJECT_STORAGE(4, "对象存储"),
    NETWORK(5, "网络"),
    AGIC(6, "AGIC")
    ;

    @EnumValue
    private final int code;

    private final String str;

    @Override
    public String toString() {
        return name() + " = " + str;
    }
}
