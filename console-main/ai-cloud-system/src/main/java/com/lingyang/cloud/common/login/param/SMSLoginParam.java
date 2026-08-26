package com.lingyang.cloud.common.login.param;

import com.lingyang.common.security.model.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/17 17:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SMSLoginParam extends LoginParam {
    @Serial
    private static final long serialVersionUID = -6927783781298900735L;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "unionId")
    private String unionId;

    @Schema(description = "openId")
    private String openId;
}
