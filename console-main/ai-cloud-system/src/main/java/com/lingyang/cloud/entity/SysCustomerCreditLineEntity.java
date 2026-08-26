package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 
 * @Author: 吴思镇
 * @Date: 2025-01-23
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_customer_credit_line")
public class  SysCustomerCreditLineEntity extends BaseEntity {

	@Serial 
	private static final long serialVersionUID =  4169766853970441269L;

	/**
	 * 客户id
	 */
	@TableField("customer_id")
	@Schema(description = "客户id")
	@NotNull(message = "客户id不能为空")
	private Long customerId;

	/**
	 * 总金额
	 */
   	@TableField("total_amount")
	@Schema(description = "总金额")
	@NotNull(message = "总金额不能为空")
	private BigDecimal totalAmount;

	/**
	 * 已使用金额
	 */
   	@TableField("use_amount")
	private BigDecimal useAmount;

	/**
	 * 状态，0 可使用，1 已到期
	 */
   	@TableField("status")
	private StatusEnum status;

	/**
	 * 使用开始时间
	 */
   	@TableField("use_time_start")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date useTimeStart;

	/**
	 * 使用截至时间
	 */
   	@TableField("use_time_end")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "使用截至时间")
	@NotNull(message = "使用截至时间不能为空")
	private Date useTimeEnd;
}