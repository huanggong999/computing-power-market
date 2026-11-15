package com.lingyang.cloud.model.vo.pc;

import lombok.Data;

import java.math.BigDecimal;
import com.alibaba.fastjson2.JSONObject;

@Data
public class GpuRentFeeVO {
    private BigDecimal unitPrice;
    private BigDecimal upstreamUnitPrice;
    private BigDecimal saleUnitPrice;
    private BigDecimal premiumUnitAmount;
    private Integer quantity;
    private Integer duration;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    /** 公网 IP 公开价目快照；按流量模式仅展示后付费单价，不计入预付金额。 */
    private JSONObject eipPricing;
}
