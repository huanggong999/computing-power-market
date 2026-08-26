package com.lingyang.cloud.model.dto.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/16 14:05
 */
@Data
public class VpnInfoDTO {

    @Schema(description = "节点ID")
    private Integer id;

    @Schema(description = "节点名称")
    private String name;

    @Schema(description = "节点状态，0: 初始化1:测试中2:上线3:下线")
    private Integer status;

    @Schema(description = "VPN节点允许客户端最大连接数")
    private Integer allConnect;

    @Schema(description = "VPN节点已经连接的客户端数量")
    private Integer useConnect;

    @Schema(description = "图标信息")
    private String iconUrl;

    @Schema(description = "内网IP")
    private String internalIp;

    @Schema(description = "公网IP")
    private String publicIp;
}
