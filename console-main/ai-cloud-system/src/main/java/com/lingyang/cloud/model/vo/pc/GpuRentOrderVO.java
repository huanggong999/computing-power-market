package com.lingyang.cloud.model.vo.pc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GpuRentOrderVO {
    private String orderId;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private Pricing pricing;
    private LocalDateTime createTime;
    private InstanceInfo instanceInfo;

    @Data
    public static class InstanceInfo {
        private String instanceId;
        private String instanceName;
        private String podName;
        private String tenantId;
        private String status;
    }

    @Data
    public static class Pricing {
        @JsonProperty("unit_price")
        private BigDecimal unitPrice;
        @JsonProperty("discount_unit_price")
        private BigDecimal discountUnitPrice;
        @JsonProperty("total_cost")
        private BigDecimal totalCost;
        @JsonProperty("discount_total_cost")
        private BigDecimal discountTotalCost;
        private String currency;
        private String unit;
    }
}
