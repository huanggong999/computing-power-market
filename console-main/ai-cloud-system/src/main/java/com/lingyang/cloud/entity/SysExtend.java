package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推广大师
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_extend")
public class SysExtend extends BaseEntity {

	/**
	 * 类型（1 推广大使， 2 普通用户）
	 */
	@Schema(description = "类型（1 推广大使， 2 普通用户）")
	@TableField("type")
	private Integer type;

	/**
	 * 等级（1 新用户，2 激活用户， 3 老用户）
	 */
	@Schema(description = "等级（1 新用户，2 激活用户， 3 老用户）")
	@TableField("level")
	private Integer level;

	/**
	 * 真实姓名
	 */
	@Schema(description = "真实姓名")
	@TableField("name")
	private String name;
	/**
	 * 联系电话
	 */
	@Schema(description = "联系电话")
	@TableField("phone")
	private String phone;

	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	@TableField("user_id")
	private Long userId;

	/**
	 * 分享key
	 */
	@Schema(description = "分享key")
	@TableField("share_key")
	private String shareKey;


	/**
	 * 推广链接
	 */
	@Schema(description = "推广链接")
	@TableField("link")
	private String link;


	/**
	 * 推广小程序二维码
	 */
	@Schema(description = "推广小程序二维码")
	@TableField("qr_code_link")
	private String qrCodeLink;

	/**
	 * 一级佣金比例
	 */
	@Schema(description = "一级佣金比例")
	@TableField("first_scale")
	private BigDecimal firstScale;


	/**
	 * 二级佣金比例
	 */
	@Schema(description = "二级佣金比例")
	@TableField("two_scale")
	private BigDecimal twoScale;


	/**
	 * 一级用户数量
	 */
	@Schema(description = "一级用户数量")
	@TableField("first_count")
	private Integer firstCount;



	/**
	 * 二级用户数量
	 */
	@Schema(description = "二级用户数量")
	@TableField("two_count")
	private Integer twoCount;

	/**
	 * 分佣订单数量
	 */
	@Schema(description = "分佣订单数量")
	@TableField("order_count")
	private Integer orderCount;


	/**
	 * 累计佣金
	 */
	@Schema(description = "累计佣金")
	@TableField("total_commission")
	private BigDecimal totalCommission;


	/**
	 * 状态（1审核中，2通过，3不通过）
	 */
	@Schema(description = "状态（1审核中，2通过，3不通过）")
	@TableField("status")
	private Integer status;

	/**
	 * 驳回理由
	 */
	@Schema(description = "驳回理由")
	@TableField("verify_remark")
	private String verifyRemark;

	/**
	 * 上级用户id
	 */
	@Schema(description = "上级用户id")
	@TableField("parent_user_id")
	private Long parentUserId;


	/**
	 * 可提现金额
	 */
	@Schema(description = "可提现金额")
	@TableField("can_withdrawal_amount")
	private BigDecimal canWithdrawalAmount;

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

	/**
	 * 用户信息-头像
	 */
	@Schema(description = "用户信息-头像")
	@TableField(exist = false)
	private String avatar;

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


	/**
	 * 上级用户信息-名称
	 */
	@Schema(description = "上级用户信息-名称")
	@TableField(exist = false)
	private String parentUserName;

	/**
	 * 上级用户信息-手机号
	 */
	@Schema(description = "上级用户信息-手机号")
	@TableField(exist = false)
	private String parentUserPhone;

	/**
	 * 真实姓名
	 */
	@Schema(description = "真实姓名")
	@TableField(exist = false)
	private String parentName;
	/**
	 * 联系电话
	 */
	@Schema(description = "联系电话")
	@TableField(exist = false)
	private String parentPhone;

	/**
	 * 贡献订单
	 */
	@Schema(description = "贡献订单")
	@TableField(exist = false)
	private Long sgOrder;

	/**
	 * 贡献金额
	 */
	@Schema(description = "贡献金额")
	@TableField(exist = false)
	private BigDecimal sgAmount;

	/**
	 * 推广客户数
	 */
	@Schema(description = "推广客户数")
	@TableField(exist = false)
	private Long extendCustomerCount = 0L;

	/**
	 * 成交客户数
	 */
	@Schema(description = "成交客户数")
	@TableField(exist = false)
	private Long cjCustomerCount = 0L;







}