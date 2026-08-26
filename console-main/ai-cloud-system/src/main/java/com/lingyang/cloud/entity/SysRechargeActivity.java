package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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

import java.util.Date;
import java.util.List;

/**
 * 充值活动
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_recharge_activity")
public class SysRechargeActivity extends BaseEntity {

	/**
	 * 名称
	 */
	@Schema(description = "名称")
	@TableField("name")
	private String name;


	@TableField(value = "recharge_start_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "充值奖励开始时间")
	private Date rechargeStartTime;

	@TableField(value = "recharge_end_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "充值奖励结束时间")
	private Date rechargeEndTime;

	@TableField(value = "coupon_start_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "奖励代金券开始时间")
	private Date couponStartTime;

	@TableField(value = "coupon_end_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "奖励代金券结束时间")
	private Date couponEndTime;

	/**
	 * 状态 1启用 2停用
	 */
	@Schema(description = "状态 1启用 2停用")
	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private List<SysRechargeActivityRewards> receivers;



}