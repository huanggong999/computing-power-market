package com.lingyang.cloud.model.dto;

import lombok.Data;

@Data
public class GpuRentCalculateDTO {
    private Long resourceId;
    private String billingType;
    private Integer quantity;
    private Integer duration;
    private String couponId;
}
