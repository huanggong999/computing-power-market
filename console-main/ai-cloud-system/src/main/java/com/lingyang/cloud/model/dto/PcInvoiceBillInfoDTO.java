package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/13 10:57
 */
@Data
public class PcInvoiceBillInfoDTO {

    @Schema(description = "发票申请用户id")
    private Long customerId;

    @Schema(description = "发票申请用户名称")
    private String customerName;

    @Schema(description = "发票申请用户手机号")
    private String phone;

    @Schema(description = "发票id")
    private Long id;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applicationTime;

    /**
     * 发票抬头
     */
    @Schema(description = "发票抬头")
    private String invoiceTitle;

    /**
     * 发票类型
     */
    @Schema(description = "发票类型（1增值税普通发票 2增值税专用发票）")
    private Integer invoiceType;

    /**
     * 发票介质
     */
    @Schema(description = "发票介质")
    private String medium = "电子发票";

    /**
     * 发票编号
     */
    @Schema(description = "发票编号")
    private String invoiceNumber;

    /**
     * 发票金额
     */
    @Schema(description = "发票金额")
    private BigDecimal amount;

    /**
     * 发票备注
     */
    @Schema(description = "发票备注")
    private String remark;

    /**
     * 发票状态
     */
    @Schema(description = "发票状态（1开票中 2已开票 3开票失败）")
    private Integer status;

    /**
     * 发票凭证
     */
    @Schema(description = "发票凭证")
    private String proof;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业统一社会信用代码（纳税人识别号）")
    private String companyCode;

    @Schema(description = "企业注册地址")
    private String companyAddress;

    @Schema(description = "企业电话")
    private String companyContactPhone;

    @Schema(description = "企业营业执照")
    private String companyImg;

    @Schema(description = "企业联系人名称")
    private String companyContactName;

    @Schema(description = "联系人电话")
    private String contactPhone;
}
