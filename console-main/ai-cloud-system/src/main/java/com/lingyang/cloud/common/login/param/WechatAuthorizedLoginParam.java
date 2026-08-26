package com.lingyang.cloud.common.login.param;

import com.lingyang.common.security.model.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 15:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatAuthorizedLoginParam extends LoginParam {
    @Serial
    private static final long serialVersionUID = -6927783781298900735L;

    @Schema(description = "unionId")
    private String unionId;

    @Schema(description = "openId")
    private String openId;
}
