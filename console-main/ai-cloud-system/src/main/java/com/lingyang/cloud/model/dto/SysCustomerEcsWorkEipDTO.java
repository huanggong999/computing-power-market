package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/22 18:05
 */
@Data
public class SysCustomerEcsWorkEipDTO {

    @TableField("public_ip")
    @Schema(description = "公网ip")
    private String publicIp;

    @TableField("private_ip")
    @Schema(description = "私网ip")
    private String privateIp;

    @TableField("port")
    @Schema(description = "端口")
    private String port;

    @TableField("access_method")
    @Schema(description = "访问方式")
    private String accessMethod;
}
