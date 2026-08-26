package com.lingyang.cloud.model.vo.order;

import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/14 15:16
 */
@Data
public class OrderCreateVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "在线支付类型，为null 表示不需要在线支付")
    private OrderOnlinePayEnum onlinePay;

    @Schema(description = "在线支付参数")
    private Object onlinePayParam;
}
