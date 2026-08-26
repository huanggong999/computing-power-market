package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRenewRequest {
    @JsonProperty("instance_ids")
    private List<String> instanceIds;
    @JsonProperty("billing_mode")
    private String billingMode;
    private Integer duration;
}
