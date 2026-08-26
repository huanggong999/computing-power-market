package com.lingyang.cloud.model.vo.applets;

import com.lingyang.common.security.model.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 16:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppletsLoginBindVO extends LoginParam {

    @Serial
    private static final long serialVersionUID = -755267246048515465L;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "openId")
    private String openId;

    @Schema(description = "type 1验证码校验 2密码校验")
    private Integer type;

    @Schema(description = "密码")
    private String password;
}
