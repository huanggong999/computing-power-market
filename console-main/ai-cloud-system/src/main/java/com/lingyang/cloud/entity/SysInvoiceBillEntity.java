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
@TableName("sys_invoice_bill")
public class SysInvoiceBillEntity extends BaseEntity {

	/**
	 * 发票管理id
	 */
	@Schema(description = "发票管理id")
	@NotNull(message = "发票管理id不能为空")
	@TableField("invoice_manage_id")
	private Long invoiceManageId;

	/**
	 * 账单ID
	 */
	@Schema(description = "账单ID")
	@NotNull(message = "账单ID不能为空")
	@TableField("customer_bill_id")
	private Long customerBillId;
}