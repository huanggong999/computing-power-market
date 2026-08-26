package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 17:16
 */
@Data
public class AddVpnIpVO {

    @Schema(description = "VPN 服务器ID")
    private Integer vpnId;

    @Schema(description = "预留IP地址列表,IP 地址支持普通IP，CIDR,IP 范围，如192.168.1.2,10.2.3.0/24,192.168.1.1-192.168.1.255")
    private String[] fixedIps;

    @Schema(description = "用户ID列表，单次添加最大量为50")
    private String[] userIds;

    @Schema(description = "部门ID 列表，单次添加最大量为50")
    private String[] departmentIds;

    @Schema(description = "角色ID 列表，单次添加最大量为50")
    private String[] roleIds;

    @Schema(description = "设备ID 列表，单次添加最大量为50")
    private String[] deviceIds;

    @Schema(description = "设备分组ID 列表，单次添加最大量为50")
    private Integer[] deviceGroupIds;
}
