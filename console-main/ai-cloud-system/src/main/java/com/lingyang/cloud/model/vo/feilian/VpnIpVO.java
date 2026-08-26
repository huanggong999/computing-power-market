package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/16 14:53
 */
@Data
public class VpnIpVO {

    @Schema(description = "VPN 服务器ID")
    private Integer vpnId;

    @Schema(description = "用户ID，仅在fixed_type 为0 时生效")
    private String userId;

}
