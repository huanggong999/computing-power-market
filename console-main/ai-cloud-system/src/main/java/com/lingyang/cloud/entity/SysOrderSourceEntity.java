package com.lingyang.cloud.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.*;
import com.lingyang.common.datasource.handler.JsonObjectTypeHandler;
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
 * @Date: 2024-11-12 
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_order_source", autoResultMap = true)
public class SysOrderSourceEntity extends BaseEntity {

	@Serial 
	private static final long serialVersionUID =  8779613631552007559L;

	@TableField("uid")
	@Schema(description = "uid")
	private String uid;
	/**
	 * 订单id
	 */
   	@TableField("order_id")
	@Schema(description = "订单id")
	private Long orderId;

	/**
	 * 订单号
	 */
   	@TableField("order_no")
	@Schema(description = "订单号")
	private String orderNo;

	/**
	 * 地区id
	 */
   	@TableField("regions_id")
	@Schema(description = "地区id")
	private SourceRegionsEnum regionsId;

	/**
	 * 资源id
	 */
   	@TableField("source_id")
	@Schema(description = "资源id")
	private String sourceId;

	/**
	 * 资源类型
	 */
   	@TableField("source_type")
	@Schema(description = "资源类型")
	private SourceTypeEnum sourceType;

	/**
	 * 资源名称
	 */
   	@TableField("source_name")
	@Schema(description = "资源名称")
	private String sourceName;

	/**
	 * ecs类型
	 */
	@TableField("ecs_type")
	@Schema(description = "ecs类型")
	private EcsTypeEnum ecsType;

	/**
	 * 产品名称
	 */
	@Schema(description = "产品名称")
   	@TableField("product_name")
	private String productName;

	/**
	 * 产品类型（1火山云引擎 2自建服务器）
	 */
	@Schema(description = "产品类型（1火山云引擎 2自建服务器）")
	@TableField("product_type")
	private Integer productType;

	/**
	 * 配置详情
	 */
	@Schema(description = "配置详情")
   	@TableField(value = "config_detail", typeHandler = JsonObjectTypeHandler.class)
	private JSONObject configDetail;

	/**
	 * 计费类型：按量计费（后付费），包年包月（先付费）
	 */
	@Schema(description = "计费类型：按量计费（后付费），包年包月（先付费）")
   	@TableField("charge_type")
	private SourceChargeTypeEnum chargeType;

	/**
	 * 时长
	 */
	@Schema(description = "时长")
   	@TableField("duration")
	private Integer duration;

	/**
	 * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
	 */
   	@TableField("duration_unit")
	@Schema(description = "时长单位")
	private SourceChargeUnitEnum durationUnit;

	/**
	 * 到期时间
	 */
	@Schema(description = "到期时间")
   	@TableField("expires_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expiresTime;

	/**
	 * 数量
	 */
	@Schema(description = "数量")
   	@TableField("number")
	private Integer number = 1;

	/**
	 * 单价金额 火山价格
	 */
	@Schema(description = "单价金额")
   	@TableField("unit_price")
	private BigDecimal unitPrice;

	/**
	 * 溢价价格 平台溢价价格，单价*溢价比例
	 */
	@Schema(description = "溢价价格")
   	@TableField("premium_price")
	private BigDecimal premiumPrice;

	/**
	 * 用户折扣金额 溢价价格*折扣比列
	 */
	@Schema(description = "用户折扣金额")
   	@TableField("user_discount_amount")
	private BigDecimal userDiscountAmount;

	/**
	 * 代金卷抵扣金额
	 */
	@Schema(description = "代金卷抵扣金额")
	@TableField("voucher_discount_amount")
	private BigDecimal voucherDiscountAmount;

	/**
	 * 优惠卷抵扣金额
	 */
	@Schema(description = "优惠卷抵扣金额")
   	@TableField("coupon_discount_amount")
	private BigDecimal couponDiscountAmount;

	/**
	 * 最终单价
	 */
	@Schema(description = "最终单价")
   	@TableField("final_unit_price")
	private BigDecimal finalUnitPrice;

	/**
	 * 账户单价
	 */
	@TableField(exist = false)
	@Schema(description = "账户单价")
	private BigDecimal accountPrice;

	/**
	 * 带宽单价
	 */
	@TableField(exist = false)
	@Schema(description = "带宽单价")
	private BigDecimal bandwidthPrice;

	/**
	 * IP单价
	 */
	@TableField(exist = false)
	@Schema(description = "IP单价")
	private BigDecimal ipPrice;
}