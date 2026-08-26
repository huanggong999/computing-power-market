package com.lingyang.cloud.model.edit.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.system.SexEnum;
import com.lingyang.common.security.model.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 17:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcCustomerEdit extends LoginParam {
    @Serial
    private static final long serialVersionUID = -755267246048515465L;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    @NotEmpty(message = "用户名称为空")
    private String customerName;

    /**
     * 手机号-登陆账号
     */
    @Schema(description = "手机号-登陆账号")
    @NotEmpty(message = "手机号为空")
    private String phone;
    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotEmpty(message = "密码为空")
    private String password;
    /**
     * 客户头像
     */
    @Schema(description = "客户头像")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private SexEnum gender;


    /**
     * 生日
     */
    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * QQ号
     */
    @Schema(description = "QQ号")
    private String qq;

    /**
     * 邀请人id
     */
    @Schema(description = "邀请人id")
    private Long inviterId;

    /**
     * 简介
     */
    @TableField("remark")
    private String remark;


    /**
     * 推广大使邀请码
     */
    @Schema(description = "推广大使邀请码")
    private  String vcode;


    /**
     * 活动id
     */
    @Schema(description = "活动id")
    private Long activityId;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private  String smsCode;

    @Schema(description = "openId")
    private String openId;
}
