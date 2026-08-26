package com.lingyang.cloud.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/5 16:10
 */
@Data
public class SysOpenProductUserPwdVO {

    @Schema(description = "用户账号")
    private String email;

    @Schema(description = "初始密码")
    private String pwd;

    @Schema(description = "内网IP")
    private String ip;

    @Schema(description = "公网IP")
    private String publicIp;

    @Schema(description = "IP地区")
    private String ipAddress;
}
