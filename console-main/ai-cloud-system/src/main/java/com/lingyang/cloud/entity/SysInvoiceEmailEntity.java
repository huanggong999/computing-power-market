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
@TableName("sys_invoice_email")
public class SysInvoiceEmailEntity extends BaseEntity {

	/**
	 * 客户ID
	 */
	@Schema(description = "客户id")
	@TableField("customer_id")
	private Long customerId;

	/**
	 * 电子邮箱
	 */
	@Schema(description = "电子邮箱")
	@NotNull(message = "电子邮箱不能为空")
	@TableField("email")
	private String email;

	/**
	 * 是否默认邮箱
	 */
	@Schema(description = "是否默认邮箱(1是 0否)")
	@TableField("is_default")
	private Integer isDefault;
}