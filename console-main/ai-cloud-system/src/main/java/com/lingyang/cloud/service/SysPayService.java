package com.lingyang.cloud.service;

import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.handler.model.OnlinePayParam;
import com.lingyang.cloud.handler.model.OnlinePayRefundsParam;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 15:50
 */
public interface SysPayService {

    Object pay(OnlinePayParam payParam);

    OrderOnlinePayEnum getPayType();

    void refunds(OnlinePayRefundsParam oa);
}
