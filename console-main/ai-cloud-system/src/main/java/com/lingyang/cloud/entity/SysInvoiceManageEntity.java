package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/10 16:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_invoice_manage")
public class SysInvoiceManageEntity extends BaseEntity {

	/**
	 * 客户ID
	 */
	@Schema(description = "客户ID")
	@NotNull(message = "客户ID不能为空")
	@TableField("customer_id")
	private Long customerId;

	/**
	 * 发票抬头ID
	 */
	@Schema(description = "发票抬头ID")
	@NotNull(message = "发票抬头ID不能为空")
	@TableField("invoice_title_id")
	private Integer invoiceTitleId;

	/**
	 * 电子邮箱ID
	 */
	@Schema(description = "电子邮箱ID")
	@NotNull(message = "电子邮箱ID不能为空")
	@TableField("invoice_email_id")
	private Integer invoiceEmailId;

	/**
	 * 发票编号
	 */
	@Schema(description = "发票编号")
	@TableField("invoice_number")
	private String invoiceNumber;

	/**
	 * 发票金额
	 */
	@Schema(description = "发票金额")
	@TableField("invoice_price")
	private BigDecimal invoicePrice;

	/**
	 * 申请时间
	 */
	@Schema(description = "申请时间")
	@TableField("application_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date applicationTime;


	/**
	 * 开票类型
	 */
	@Schema(description = "开票类型（1按账期开票 2按消费时间开票）")
	@TableField("type")
	private Integer type;

	/**
	 * 发票状态
	 */
	@Schema(description = "发票状态（1开票中 2已开票 3开票失败）")
	@TableField("status")
	private Integer status;

	/**
	 * 发票备注
	 */
	@Schema(description = "发票备注")
	@TableField("remark")
	private String remark;

	/**
	 * 发票凭证
	 */
	@Schema(description = "发票凭证")
	@TableField("proof")
	private String proof;
}