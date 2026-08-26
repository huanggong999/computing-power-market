package com.lingyang.cloud.model.dto;

import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import lombok.Data;

@Data
public class GpuRentOrderDTO extends GpuRentCalculateDTO {
    private String mirrorId;
    private String mirrorVersionId;
    private Boolean agreeProtocol;
    private GpuPodCreateRequest podCreateRequest;
}
