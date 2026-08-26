package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * SSH连接信息
 */
@Data
@Schema(description = "SSH连接信息")
public class SshInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主机地址")
    private String host;

    @Schema(description = "端口号")
    private Integer port;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "SSH连接命令")
    private String command;
}
