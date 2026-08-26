package com.lingyang.cloud.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchRenewDTO {
    private List<String> instanceIds;
    private String billingMode;
    private Integer duration;
}
