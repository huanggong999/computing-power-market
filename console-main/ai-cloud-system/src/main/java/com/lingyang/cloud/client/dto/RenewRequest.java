package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RenewRequest {
    @JsonProperty("billing_mode")
    private String billingMode;
    private Integer duration;
}
