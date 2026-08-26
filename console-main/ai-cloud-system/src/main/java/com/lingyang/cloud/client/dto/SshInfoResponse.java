package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 实例 SSH 连接信息响应
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SshInfoResponse {

    @JsonProperty("success")
    private boolean success = true;

    @JsonProperty("message")
    private String message;

    private String instanceId;

    private InstanceResponse.SshInfo sshInfo;
}
