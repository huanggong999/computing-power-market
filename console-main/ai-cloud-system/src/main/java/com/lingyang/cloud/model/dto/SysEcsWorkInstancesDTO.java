package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/4 15:49
 */
@Data
public class SysEcsWorkInstancesDTO {

    @Schema(description = "实例id")
    private String instanceId;

    @Schema(description = "用户控制状态（1申请开通 2申请停机 3申请重启 4申请续费 5申请销毁）")
    private Integer userStatus;

    @Schema(description = "运维状态（1待开通 2已开通 3计算中 4已停机 5已退款 6已过期）")
    private Integer operationStatus;

    @Schema(description = "公网ip")
    private String publicIp;

    @Schema(description = "私网ip")
    private String privateIp;

    @Schema(description = "端口")
    private String port;

    @Schema(description = "访问方式")
    private String accessMethod;
}
