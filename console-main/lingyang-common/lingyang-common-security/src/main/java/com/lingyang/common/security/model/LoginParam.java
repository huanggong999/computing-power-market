package com.lingyang.common.security.model;

import com.lingyang.common.security.access.login.LoginHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 基础登陆参数封装，{@link LoginHandler} 中使用
 * @Author: 王小龙
 * @Date: 2024/3/21 15:40
 */
@Data
public class LoginParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -4009406257534560733L;
    /**
     * 验证码uid
     */
    @Schema(description = "验证码uid")
    private String uid;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;


}