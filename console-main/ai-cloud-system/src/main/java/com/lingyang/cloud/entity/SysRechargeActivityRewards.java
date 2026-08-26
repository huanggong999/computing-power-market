package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值活动梯度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_recharge_activity_rewards")
public class SysRechargeActivityRewards extends BaseEntity {

	/**
	 * 活动id
	 */
	@Schema(description = "活动id")
	@TableField("activity_id")
	private Long activityId;


	/**
	 * 几级权益
	 */
	@Schema(description = "几级权益")
	@TableField("level")
	private Integer level;

	/**
	 * 充值金额
	 */
	@Schema(description = "充值金额")
	@TableField("recharge_amount")
	private BigDecimal rechargeAmount;

	/**
	 * 代金券金额
	 */
	@Schema(description = "代金券金额")
	@TableField("coupon_amount")
	private BigDecimal couponAmount;



}