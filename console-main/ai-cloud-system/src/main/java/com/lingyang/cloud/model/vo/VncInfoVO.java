package com.lingyang.cloud.model.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * VNC连接信息
 */
@Data
@Schema(description = "VNC连接信息")
public class VncInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("instance_id")
    @JsonAlias("instanceId")
    @Schema(description = "实例ID")
    private String instanceId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @JsonProperty("websocket_url")
    @JsonAlias("websocketUrl")
    @Schema(description = "WebSocket连接地址")
    private String websocketUrl;

    @JsonProperty("vnc_password")
    @JsonAlias("vncPassword")
    @Schema(description = "VNC密码")
    private String vncPassword;

    @JsonProperty("node_host")
    @JsonAlias("nodeHost")
    @Schema(description = "节点地址")
    private String nodeHost;

    @JsonProperty("node_port")
    @JsonAlias("nodePort")
    @Schema(description = "节点端口")
    private Integer nodePort;

    @Schema(description = "提示信息")
    private String message;
}
