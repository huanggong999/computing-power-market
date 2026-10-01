package com.lingyang.cloud.model.dto;

import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import lombok.Data;

@Data
public class GpuRentCalculateDTO {
    private Long resourceId;
    private GpuPodCreateRequest podCreateRequest;
    private String billingType;
    private Integer quantity;
    private Integer duration;
    private String couponId;
}
