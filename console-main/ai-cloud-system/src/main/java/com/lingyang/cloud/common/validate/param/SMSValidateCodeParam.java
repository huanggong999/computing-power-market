package com.lingyang.cloud.common.validate.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/17 17:44
 */
@Data
public class SMSValidateCodeParam {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "验证码类型（1 登录， 2 注册，3 修改手机号）")
    private Integer tmsg;

}
