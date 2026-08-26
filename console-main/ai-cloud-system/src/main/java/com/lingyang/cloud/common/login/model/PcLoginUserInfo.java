package com.lingyang.cloud.common.login.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.system.SexEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
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
 * @Date: 2024/10/21 16:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcLoginUserInfo extends LoginUserInfoDetail {
    @Serial
    private static final long serialVersionUID = 3402620583562002834L;

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
     * 个人认证姓名
     */
    @Schema(description = "个人认证姓名")
    private String realName;

    /**
     * 个人认证身份证号
     */
    @Schema(description = "个人认证身份证号")
    private String idCard;

    @Schema(description = "总余额")
    private BigDecimal totalBalance;
    /**
     * 余额
     */
    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "代金卷余额")
    private BigDecimal voucherBalance;

    @Schema(description = "优惠劵总数")
    private Integer couponNum;

    @Schema(description = "可开票总额")
    private BigDecimal invoiceTotalAmount;

    /**
     * 信用额
     */
    @Schema(description = "信用额")
    private BigDecimal creditAmount;

    @Schema(description = "欠费金额")
    private BigDecimal arrearsAmount;

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
     * 客户在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。
     */
    @Schema(description = "客户在微信开放平台的唯一身份标识")
    private String unionId;

    /**
     * 客户的唯一标识
     */
    @Schema(description = "客户的唯一标识")
    private String openId;

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
     * 邀请时间
     */
    @Schema(description = "邀请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inviterTime;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private StatusEnum status;

    /**
     * 简介
     */
    @Schema(description = "简介")
    private String remark;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;


    @Schema(description = "身份类型（1 个人账号， 2 企业账号）")
    private Integer type;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业统一社会信用代码")
    private String companyCode;

    @Schema(description = "企业注册地址")
    private String companyAddress;


    @Schema(description = "企业营业执照")
    private String companyImg;

    @Schema(description = "企业联系人名称")
    private String companyContactName;

    @Schema(description = "企业联系人电话")
    private String companyContactPhone;

    @Schema(description = "认证状态（1未认证 2认证中 3已认证 4认证失败）")
    private Integer certStatus;

    @Schema(description = "认证类型（1个人认证 2企业认证）")
    private Integer certType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "认证时间")
    private Date certTime;

}
