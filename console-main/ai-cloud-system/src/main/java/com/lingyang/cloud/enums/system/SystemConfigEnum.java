package com.lingyang.cloud.enums.system;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.model.config.EcsSystemVolumeConfigModel;
import com.lingyang.cloud.model.config.EipAddressConfigModel;
import com.lingyang.cloud.model.config.SystemPriceRationConfigModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @Description: 系统配置枚举
 * @Author: 王小龙
 * @Date: 2024/10/18 17:21
 */
@Getter
@AllArgsConstructor
public enum SystemConfigEnum {
    SELL_PRICE_RATIO(1, new SystemPriceRationConfigModel(BigDecimal.valueOf(0.1)), "价格溢价比例"),
    RETURN_PRICE_RATIO(2, new SystemPriceRationConfigModel(BigDecimal.valueOf(15)), "退款抽成比例"),
    ECS_SYSTEM_VOLUME(3, new EcsSystemVolumeConfigModel(), "服务器系统云盘"),
    EIP_ADDRESS(4, new EipAddressConfigModel(), "公网ip默认配置"),
    ;

    private final int code;

    private final JSONObject defaultValue;


    private final String str;

    <T> SystemConfigEnum(int code, T defaultValue, String str) {
        this.code = code;
        this.defaultValue = JSONObject.parseObject(JSONObject.toJSONString(defaultValue));
        this.str = str;
    }

    @Override
    public String toString() {
        return name() + " = " + str;
    }


}