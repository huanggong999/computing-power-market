package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/10 16:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_invoice_title")
public class SysInvoiceTitleEntity extends BaseEntity {

	/**
	 * 客户ID
	 */
	@Schema(description = "客户id")
	@TableField("customer_id")
	private Long customerId;

	/**
	 * 开票类型
	 */
	@Schema(description = "开票类型（1个人 2企业）")
	@NotNull(message = "开票类型不能为空")
	@TableField("billing_type")
	private Integer billingType;


	/**
	 * 发票类型
	 */
	@Schema(description = "发票类型（1增值税普通发票 2增值税专用发票）")
	@NotNull(message = "发票类型不能为空")
	@TableField("invoice_type")
	private Integer invoiceType;

	/**
	 * 发票抬头
	 */
	@Schema(description = "发票抬头")
	@NotNull(message = "发票抬头不能为空")
	@TableField("invoice_title")
	private String invoiceTitle;

	/**
	 * 纳税人识别号
	 */
	@Schema(description = "纳税人识别号")
	@TableField("number")
	private String number;

	/**
	 * 基本开户银行
	 */
	@Schema(description = "基本开户银行")
	@TableField("deposit")
	private String deposit;

	/**
	 * 基本开户账户
	 */
	@Schema(description = "基本开户账户")
	@TableField("account")
	private String account;

	/**
	 * 企业注册地址
	 */
	@Schema(description = "企业注册地址")
	@TableField("address")
	private String address;

	/**
	 * 企业联系人
	 */
	@Schema(description = "企业联系人")
	@TableField("contact")
	private String contact;

	/**
	 * 企业注册电话
	 */
	@Schema(description = "企业电话")
	@TableField("phone_number")
	private String phoneNumber;

	@Schema(description = "联系人电话")
	@TableField("contact_phone")
	private String contactPhone;
}

