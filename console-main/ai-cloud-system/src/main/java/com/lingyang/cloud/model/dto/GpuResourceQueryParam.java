package com.lingyang.cloud.model.dto;

import lombok.Data;

import java.util.List;

/**
 * GPU资源查询参数（内部DTO，用于Mapper）
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
public class GpuResourceQueryParam {

    private String billingType;
    private String regionCode;
    private String zoneCode;
    private List<String> gpuModels;
    private Integer gpuCount;
    private String sortBy;
    private String sortOrder;
    private Integer status;
    private Integer pageNo;
    private Integer pageSize;
}
