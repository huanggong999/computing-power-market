package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * 推广大使提现记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_extend_withdrawal_record")
public class SysExtendWithdrawalRecord extends BaseEntity {


	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	@TableField("user_id")
	private Long userId;

	/**
	 * 订单号
	 */
	@Schema(description = "订单号")
	@TableField("order_no")
	private String orderNo;

	/**
	 * 扣除金额
	 */
	@Schema(description = "扣除金额")
	@TableField("total_amount")
	private BigDecimal totalAmount;


	/**
	 * 提现金额
	 */
	@Schema(description = "提现金额")
	@TableField("withdrawal_amount")
	private BigDecimal withdrawalAmount;

	/**
	 * 手续费
	 */
	@Schema(description = "手续费")
	@TableField("service_amount")
	private BigDecimal serviceAmount;

	/**
	 * 手续费比例
	 */
	@Schema(description = "手续费比例")
	@TableField("service_rate")
	private BigDecimal serviceRate;


	/**
	 * 银行卡真实姓名
	 */
	@Schema(description = "银行卡真实姓名")
	@TableField("bank_user_name")
	private String bankUserName;


	/**
	 * 银行卡身份证
	 */
	@Schema(description = "银行卡身份证")
	@TableField("id_card")
	private String idCard;

	/**
	 * 开户地址
	 */
	@Schema(description = "开户地址")
	@TableField("address")
	private String address;

	/**
	 * 开户银行
	 */
	@Schema(description = "开户银行")
	@TableField("bank")
	private String bank;

	/**
	 * 银行账号
	 */
	@Schema(description = "银行账号")
	@TableField("bank_no")
	private String bankNo;



	@TableField(value = "verify_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "审核时间")
	private Date verifyTime;

	/**
	 * 审核备注
	 */
	@Schema(description = "审核备注")
	@TableField("verify_remark")
	private String verifyRemark;


	/**
	 * 打款图片
	 */
	@Schema(description = "打款图片")
	@TableField("with_image")
	private String withImage;


	/**
	 * 状态（1审核中，2通过，3不通过, 4 已打款）
	 */
	@Schema(description = "状态（1审核中，2通过，3不通过 4 已打款）")
	@TableField("status")
	private Integer status;


	/**
	 * 用户信息-名称
	 */
	@Schema(description = "用户信息-名称")
	@TableField(exist = false)
	private String userName;

	/**
	 * 用户信息-手机号
	 */
	@Schema(description = "用户信息-手机号")
	@TableField(exist = false)
	private String userPhone;


}