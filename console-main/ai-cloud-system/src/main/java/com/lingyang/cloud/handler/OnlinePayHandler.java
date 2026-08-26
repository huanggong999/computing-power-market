package com.lingyang.cloud.handler;

import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.handler.model.OnlinePayParam;
import com.lingyang.cloud.handler.model.OnlinePayRefundsParam;
import com.lingyang.cloud.service.SysPayService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.utils.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 15:56
 */
@Component
public class OnlinePayHandler {

    private final Map<OrderOnlinePayEnum, SysPayService> ONLINE_PAY_MAP = new ConcurrentHashMap<>(OrderOnlinePayEnum.values().length);

    public OnlinePayHandler(@Autowired(required = false) List<SysPayService> sysPayServices) {
        if (ObjectUtils.isNotEmpty(sysPayServices)) {
            for (SysPayService sysPayService : sysPayServices) {
                ONLINE_PAY_MAP.put(sysPayService.getPayType(), sysPayService);
            }
        }
    }


    public Object pay(OrderOnlinePayEnum payType, OnlinePayParam payParam) {
        return Optional.of(ONLINE_PAY_MAP.get(payType))
                .orElseThrow(() -> new HttpServiceException("占不支持当前支付方式"))
                .pay(payParam);
    }

    public void refunds(OrderOnlinePayEnum payType, OnlinePayRefundsParam payParam) {
         Optional.of(ONLINE_PAY_MAP.get(payType))
                .orElseThrow(() -> new HttpServiceException("占不支持当前支付方式"))
                .refunds(payParam);
    }
}
