package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import com.lingyang.common.web.serializer.CustomerBigDecimalSerialize;
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
 * @Date: 2024-11-28 
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_customer_bill")
public class SysCustomerBillEntity extends BaseEntity {

	@Serial 
	private static final long serialVersionUID =  7285882694510392145L;
	@Schema(description = "客户名称")
	@TableField(exist = false)
	private String customerName;

	@Schema(description = "客户手机号")
	@TableField(exist = false)
	private String phone;
	/**
	 * 客户ID
	 */
	@Schema(description = "客户id")
   	@TableField("customer_id")
	private Long customerId;

	/**
	 * 账期
	 */
	@Schema(description = "账期")
   	@TableField("bill")
	private String bill;

	/**
	 * 账单号
	 */
	@Schema(description = "账单号")
   	@TableField("bill_no")
	private String billNo;

	/**
	 * 账期时间
	 */
	@Schema(description = "账期时间")
   	@TableField("bill_date")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date billDate;

	/**
	 * 资源类型
	 */
	@Schema(description = "资源类型")
   	@TableField("source_type")
	private SourceTypeEnum sourceType;

	/**
	 * 资源ID
	 */
	@Schema(description = "资源ID")
   	@TableField("source_id")
	private Long sourceId;

	/**
	 * 计费方式
	 */
	@Schema(description = "计费方式")
   	@TableField("charge_type")
	private SourceChargeTypeEnum chargeType;

	/**
	 * 使用时长
	 */
	@Schema(description = "使用时长")
   	@TableField("duration")
	private Integer duration;

	/**
	 * 计费单位
	 */
	@Schema(description = "计费单位")
   	@TableField("charge_unit")
	private String chargeUnit;

	/**
	 * 账单类型
	 */
	@Schema(description = "账单类型")
   	@TableField("bill_type")
	private String billType;

	/**
	 * 结算类型
	 */
	@Schema(description = "结算类型")
   	@TableField("settle_type")
	private String settleType;

	/**
	 * 实例ID
	 */
	@Schema(description = "实例ID")
   	@TableField("instance_id")
	private String instanceId;

	/**
	 * 实例名称
	 */
	@Schema(description = "实例名称")
   	@TableField("instance_name")
	private String instanceName;

	/**
	 * 计费单元
	 */
	@Schema(description = "计费单元")
   	@TableField("unit_id")
	private String unitId;

	/**
	 * 地域
	 */
	@Schema(description = "地域")
   	@TableField("region")
	private String region;

	/**
	 * 可用区
	 */
	@Schema(description = "可用区")
   	@TableField("zone")
	private String zone;

	/**
	 * 单价价格类型
	 */
	@Schema(description = "单价价格类型")
   	@TableField("price_type")
	private String priceType;

	/**
	 * 单价
	 */
	@Schema(description = "单价")
   	@TableField("price")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal price;

	/**
	 * 用量
	 */
	@Schema(description = "用量")
   	@TableField("`usage`")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal usage;

	/**
	 * 用量单位
	 */
	@Schema(description = "用量单位")
   	@TableField("usage_unit")
	private String usageUnit;

	/**
	 * 原价
	 */
	@Schema(description = "原价")
   	@TableField("original_price")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal originalPrice;

	/**
	 * 溢价价格
	 */
	@Schema(description = "溢价价格")
   	@TableField("premium_price")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal premiumPrice;

	/**
	 * 用户折扣金额
	 */
	@Schema(description = "用户折扣金额")
   	@TableField("user_discount_amount")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal userDiscountAmount;

	/**
	 * 应付价格
	 */
	@Schema(description = "应付价格")
   	@TableField("pay_price")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal payPrice;

	/**
	 * 代金券金额
	 */
	@Schema(description = "代金券金额")
   	@TableField("voucher_amount")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal voucherAmount;

	/**
	 * 授信额金额
	 */
	@TableField("credit_line_amount")
	@Schema(description = "授信额金额")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal creditLineAmount;

	/**
	 * 余额支付金额
	 */
	@Schema(description = "余额支付金额")
   	@TableField("balance_pay_amount")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal balancePayAmount;

	/**
	 * 欠费金额
	 */
	@Schema(description = "欠费金额")
   	@TableField("arrears_amount")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal arrearsAmount;

	/**
	 * 支付状态
	 */
	@Schema(description = "支付状态")
   	@TableField("pay_status")
	private OrderStatusEnum payStatus;

	/**
	 * 交易时间
	 */
	@Schema(description = "交易时间")
   	@TableField("pay_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date payTime;

	/**
	 * 账单开始时间
	 */
	@Schema(description = "账单开始时间")
   	@TableField("bill_start_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date billStartTime;

	/**
	 * 账单结束时间
	 */
	@Schema(description = "账单结束时间")
   	@TableField("bill_end_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date billEndTime;

}