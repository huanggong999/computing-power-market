package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


/**
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_network_product")
public class SysNetworkProductEntity extends BaseEntity {


	/**
	 * 产品名称
	 */
	@TableField("name")
	@Schema(description = "产品名称")
	private String name;



	/**
	 * 产品描述
	 */
	@TableField("memo")
	@Schema(description = "产品描述")
	private String memo;


	/**
	 * 参考价
	 */
	@TableField("refer_price")
	@Schema(description = "参考价")
	private String referPrice;

	/**
	 * 售价
	 */
	@TableField("pay_price")
	@Schema(description = "售价")
	private BigDecimal payPrice;

	/**
	 * 划线价
	 */
	@TableField("crossed_price")
	@Schema(description = "划线价")
	private BigDecimal crossedPrice;

	/**
	 * 图片
	 */
	@TableField("image")
	@Schema(description = "图片")
	private String image;


	/**
	 * 咨询表单id
	 */
	@TableField("form_id")
	@Schema(description = "咨询表单id")
	private Long formId;

	/**
	 * 支付表单id
	 */
	@TableField("pay_form_id")
	@Schema(description = "支付表单id")
	private Long payFormId;


	/**
	 * 详情
	 */
	@TableField("detail")
	@Schema(description = "详情")
	private String detail;

	/**
	 * 序号
	 */
	@TableField("sort")
	@Schema(description = "序号")
	private Integer sort;


	/**
	 * 状态 1启用 2关闭
	 */
	@TableField("status")
	@Schema(description = "状态 1启用 2关闭")
	private Integer status;

	/**
	 * 带宽(M) （1 需要， 2 不需要）
	 */
	@TableField("bandwidth")
	@Schema(description = "带宽(M) （1 需要， 2 不需要）")
	private Integer bandwidth;

	/**
	 * 账户单价
	 */
	@TableField("account_price")
	@Schema(description = "账户单价")
	private BigDecimal accountPrice;

	/**
	 * 带宽单价
	 */
	@TableField("bandwidth_price")
	@Schema(description = "带宽单价")
	private BigDecimal bandwidthPrice;

	/**
	 * IP单价
	 */
	@TableField("ip_price")
	@Schema(description = "IP单价")
	private BigDecimal ipPrice;

	/**
	 * 1个月折扣（%）
	 */
	@TableField("one_month_discount")
	@Schema(description = "1个月折扣（%）")
	private Integer oneMonthDiscount;

	/**
	 * 2个月折扣（%）
	 */
	@TableField("two_month_discount")
	@Schema(description = "2个月折扣（%）")
	private Integer twoMonthDiscount;

	/**
	 * 3个月折扣（%）
	 */
	@TableField("three_month_discount")
	@Schema(description = "3个月折扣（%）")
	private Integer threeMonthDiscount;

	/**
	 * 4个月折扣（%）
	 */
	@TableField("four_month_discount")
	@Schema(description = "4个月折扣（%）")
	private Integer fourMonthDiscount;

	/**
	 * 5个月折扣（%）
	 */
	@TableField("five_month_discount")
	@Schema(description = "5个月折扣（%）")
	private Integer fiveMonthDiscount;

	/**
	 * 6个月折扣（%）
	 */
	@TableField("six_month_discount")
	@Schema(description = "6个月折扣（%）")
	private Integer sixMonthDiscount;

	/**
	 * 7个月折扣（%）
	 */
	@TableField("seven_month_discount")
	@Schema(description = "7个月折扣（%）")
	private Integer sevenMonthDiscount;

	/**
	 * 8个月折扣（%）
	 */
	@TableField("eight_month_discount")
	@Schema(description = "8个月")
	private Integer eightMonthDiscount;

	/**
	 * 9个月折扣（%）
	 */
	@TableField("nine_month_discount")
	@Schema(description = "9个月")
	private Integer nineMonthDiscount;

	/**
	 * 10个月折扣（%）
	 */
	@TableField("ten_month_discount")
	@Schema(description = "10个月")
	private Integer tenMonthDiscount;

	/**
	 * 11个月折扣（%）
	 */
	@TableField("eleven_month_discount")
	@Schema(description = "11个月")
	private Integer elevenMonthDiscount;

	/**
	 * 1年折扣（%）
	 */
	@TableField("one_year_discount")
	@Schema(description = "1年折扣（%）")
	private Integer oneYearDiscount;

	/**
	 * 2年折扣（%）
	 */
	@TableField("two_year_discount")
	@Schema(description = "2年折扣（%）")
	private Integer twoYearDiscount;

	/**
	 * 3年折扣（%）
	 */
	@TableField("three_year_discount")
	@Schema(description = "3年折扣（%）")
	private Integer threeYearDiscount;

	/**
	 *账户、IP是否绑定(0否 1是)
	 */
	@TableField("is_account_ip_binding")
	@Schema(description = "账户、IP是否绑定(0否 1是)")
	private Integer isAccountIpBinding;

	/**
	 * 是否显示IP(0否 1显示)
	 */
	@TableField("is_ip_display")
	@Schema(description = "是否显示IP(0否 1显示)")
	private Integer isIpDisplay;

	/**
	 * 是否显示带宽(0否 1显示)
	 */
	@TableField("is_bandwidth_display")
	@Schema(description = "是否显示带宽(0否 1显示)")
	private Integer isBandwidthDisplay;

	/**
	 * 是否显示IP个数（对应划线价字段）
	 */
	@TableField("is_ip_count_display")
	@Schema(description = "是否显示IP单价（对应划线价字段）(0否 1显示)")
	private Integer isIpCountDisplay;

	/**
	 * 国家地区
	 */
	@TableField("country")
	@Schema(description = "国家地区")
	private String country;


	/**
	 * 表单名称
	 */
	@TableField(exist = false)
	private String formName;

	/**
	 * 表单名称
	 */
	@TableField(exist = false)
	private String payFormName;


	@TableField(value = "department_id")
	@Schema(description = "飞连的部门id")
	private String departmentId;

}