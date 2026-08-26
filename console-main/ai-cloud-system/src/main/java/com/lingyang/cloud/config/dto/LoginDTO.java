package com.lingyang.cloud.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小程序登录参数
 */
@Data
public class LoginDTO {


    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String portrait;

    /**
     * 用户性别，0: 未知；1:男性；2:女性（有就传）
     */
    @Schema(description = "用户性别，0: 未知；1:男性；2:女性（有就传）")
    private Integer gender;

    /**
     * 用户手机号（有就传）
     */
    @Schema(description = "用户手机号（有就传）")
    private String phone;


    /**
     * 用户国家（有就传）
     */
    @Schema(description = " 用户国家（有就传）")
    private String country;

    /**
     * 用户省份（有就传）
     */
    @Schema(description = "用户省份（有就传）")
    private String province;

    /**
     * 用户城市（有就传）
     */
    @Schema(description = "用户城市（有就传）")
    private String city;

    /**
     * openId
     */
    @Schema(description = "openId")
    private String openId;

    /**
     * encryptedData
     */
    @Schema(description = "encryptedData")
    private String encryptedData;

    /**
     * iv
     */
    @Schema(description = "iv")
    private String iv;

    /**
     * session key （有就传）
     */
    @Schema(description = " session key （有就传）")
    private String sessionKey;

    /**
     * code（有就传）
     */
    @Schema(description = "code（有就传）")
    private String code;

    /**
     * phoneCode （获取手机号的Code）
     */
    @Schema(description = "phoneCode （获取手机号的Code）")
    private String phoneCode;


}
