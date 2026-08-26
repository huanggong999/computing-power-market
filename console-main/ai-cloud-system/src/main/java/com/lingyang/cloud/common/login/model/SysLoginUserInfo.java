package com.lingyang.cloud.common.login.model;

import com.lingyang.cloud.enums.system.SexEnum;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 13:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLoginUserInfo extends LoginUserInfoDetail {
    @Serial
    private static final long serialVersionUID = -7315060439252691108L;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phone;

    /**
     * 用户性别
     */
    @Schema(description = "用户性别")
    private SexEnum sex;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    private String avatar;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
