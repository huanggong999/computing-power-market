package com.lingyang.cloud.common.login.param;

import com.lingyang.common.security.model.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/17 17:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UsernamePasswordLoginParam extends LoginParam {
    @Serial
    private static final long serialVersionUID = 1339778436075448541L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户id，扫码授权直接登录")
    private Long id;

    @Schema(description = "unionId")
    private String unionId;

    @Schema(description = "openId")
    private String openId;

}