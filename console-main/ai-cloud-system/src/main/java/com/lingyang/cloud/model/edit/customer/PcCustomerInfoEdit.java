package com.lingyang.cloud.model.edit.customer;

import com.lingyang.common.security.model.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 17:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcCustomerInfoEdit extends LoginParam {
    @Serial
    private static final long serialVersionUID = -755267246048515465L;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    private String customerName;

    /**
     * 手机号-登陆账号
     */
    @Schema(description = "手机号-登陆账号")
    private String phone;

    /**
     * 客户头像
     */
    @Schema(description = "客户头像")
    private String avatar;

    @Schema(description = "验证码")
    private String smsCode;
}
