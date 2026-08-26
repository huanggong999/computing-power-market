package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceToolsResponse {
    private boolean success = true;
    private String message;
    @JsonAlias("instance_id")
    private String instanceId;
    private List<ToolInfo> tools;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolInfo {
        private String name;
        private String url;
        private String icon;
    }
}
