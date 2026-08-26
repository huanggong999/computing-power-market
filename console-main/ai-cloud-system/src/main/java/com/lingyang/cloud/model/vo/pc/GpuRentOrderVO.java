package com.lingyang.cloud.model.vo.pc;

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
}
