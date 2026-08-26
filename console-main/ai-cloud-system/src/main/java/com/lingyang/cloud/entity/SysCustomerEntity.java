package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.enums.system.SexEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-04-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_customer")
public class SysCustomerEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 5524403389689071551L;

    /**
     * 账号id
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 账号密钥key
     */
    @TableField("account_key")
    private String accountKey;
    /**
     * 账号密钥
     */
    @TableField("account_secret")
    private String accountSecret;
    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 手机号-登陆账号
     */
    @TableField("phone")
    private String phone;
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    /**
     * 客户头像
     */
    @TableField("avatar")
    private String avatar = "https://ai-cloud-system.tos-cn-beijing.volces.com/2025/03/05/61b815f439664a0bb277d1ec4640642a.png";

    /**
     * 性别
     */
    @TableField("gender")
    private SexEnum gender;

    /**
     * 余额
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 客户在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。
     */
    @TableField("union_id")
    private String unionId;

    /**
     * 客户的唯一标识
     */
    @TableField("open_id")
    private String openId;

    /**
     * 生日
     */
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * QQ号
     */
    @TableField("qq")
    private String qq;

    /**
     * 邀请人id
     */
    @TableField("inviter_id")
    private Long inviterId;

    /**
     * 邀请时间
     */
    @TableField("inviter_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inviterTime;

    /**
     * 状态
     */
    @TableField("status")
    private StatusEnum status;

    /**
     * 简介
     */
    @TableField("remark")
    private String remark;

    /**
     * 注册时间
     */
    @TableField("register_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;


    @TableField("`type`")
    @Schema(description = "身份类型（1 个人账号， 2 企业账号）")
    private Integer type;

    @TableField("company_name")
    @Schema(description = "企业名称")
    private String companyName;

    @TableField("company_code")
    @Schema(description = "企业统一社会信用代码")
    private String companyCode;

    @TableField("company_address")
    @Schema(description = "企业注册地址")
    private String companyAddress;


    @TableField("company_img")
    @Schema(description = "企业营业执照")
    private String companyImg;

    @TableField("company_contact_name")
    @Schema(description = "企业联系人名称")
    private String companyContactName;

    @TableField("company_contact_phone")
    @Schema(description = "企业联系人电话")
    private String companyContactPhone;

    @TableField("company_status")
    @Schema(description = "企业审核状态（1 未提交， 2 审核中， 3 已通过，4 未通过）")
    private Integer companyStatus;

    @TableField("company_verify_remark")
    @Schema(description = "企业审核备注")
    private String companyVerifyRemark;

    @TableField("company_verify_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "企业审核时间")
    private Date companyVerifyTime;

    @TableField("cert_status")
    @Schema(description = "认证状态（1未认证 2认证中 3已认证 4认证失败）")
    private Integer certStatus;

    @TableField("cert_type")
    @Schema(description = "认证类型（1个人认证 2企业认证）")
    private Integer certType;

    @TableField("cert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "认证时间")
    private Date certTime;


}
