package com.lingyang.cloud.entity;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.common.datasource.handler.JsonArrayTypeHandler;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_network_value", autoResultMap = true)
public class SysNetworkValueEntity extends BaseEntity {

	@TableField("agi_customer_name")
	@Schema(description = "客户名称（购买产品时补充字段）")
	private String agiCustomerName;


	/**
	 * 产品id
	 */
	@TableField("product_id")
	@Schema(description = "产品id")
	private Long productId;

	@Schema(description = "表单类型（1 咨询表单 2 购买表单）")
	private Integer formType;

	/**
	 * 表单id
	 */
	@TableField("form_id")
	@Schema(description = "表单id")
	private Long formId;


	/**
	 * 用户id
	 */
	@TableField("user_id")
	@Schema(description = "用户id")
	private Long userId;

	/**
	 * 产品名称
	 */
	@TableField("product_name")
	@Schema(description = "产品名称")
	private String productName;

	/**
	 * 表单名称
	 */
	@TableField("form_name")
	@Schema(description = "表单名称")
	private String formName;

	/**
	 * json
	 */
	@TableField("json")
	@Schema(description = "json")
	private String json;

	/**
	 * 带宽(M) 数量
	 */
	@Schema(description = "带宽(M) 数量")
	private Integer bandwidth;

	/**
	 * 产品天数
	 */
	@Schema(description = "产品天数")
	private Integer networkDay;

	/**
	 * 购买产品数量
	 */
	@Schema(description = "购买产品数量")
	private Integer networkCount;

	/**
	 * 购买IP数量
	 */
	@Schema(description = "购买IP数量")
	@TableField("ip_count")
	private Integer ipCount;


	/**
	 * 产品状态（1 未开通， 2 已开通， 3 已过期）
	 */
	@Schema(description = "产品状态（1 未开通， 2 已开通， 3 已过期， 4已停线，5已撤线）")
	private Integer actualStatus;

	/**
	 * 支付状态（0 未支付， 1 已支付）
	 */
	@Schema(description = "支付状态（0 未支付， 1 已支付）")
	private Integer payStatus;

	/**
	 * 原价
	 */
	@Schema(description = "原价")
	private BigDecimal originalAmount;

	/**
	 * 实付价
	 */
	@Schema(description = "实付价")
	private BigDecimal payAmount;

	/**
	 * 单价
	 */
	@Schema(description = "单价")
	private BigDecimal unitPrice;

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
	 * 实际产品开通时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "实际产品开通时间")
	private Date actualAgiOpenTime;

	/**
	 * 产品到期时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "产品到期时间")
	private Date actualAgiExpireTime;

	/**
	 * 联系方式
	 */
	@TableField("mobile")
	@Schema(description = "联系方式")
	private String mobile;

	/**
	 * 联系邮箱
	 */
	@TableField("email")
	@Schema(description = "联系邮箱")
	private String email;

	/**
	 * 备注
	 */
	@TableField("remark")
	@Schema(description = "备注")
	private String remark;

	/**
	 * 账号密码
	 */
	private String userpwd;

	@TableField(value = "userpwd_list", typeHandler = JsonArrayTypeHandler.class)
	@Schema(description = "账号密码集合")
	private JSONArray userpwdList;

	@TableField("is_auto_renew")
	@Schema(description = "是否自动续费（0否 1是）")
	private Integer isAutoRenew;

	@TableField("ip_address")
	@Schema(description = "IP地区")
	private String ipAddress;

	@TableField(value = "department_id")
	@Schema(description = "飞连的部门id")
	private String departmentId;

	@TableField("expire_status")
	@Schema(description = "到期状态（0：1天内到期、 1：1天到期、 2：2天到期、 3：3天到期、4：4天到期、 5：5天到期、 6：6天到期、 7：7天到期、8：大于7天）")
	private Integer expireStatus;


	/**
	 * 用户账号
	 */
	@TableField(exist = false)
	private String userName;

	/**
	 * 用户昵称
	 */
	@TableField(exist = false)
	private String nickName;

	/**
	 * 手机号码
	 */
	@TableField(exist = false)
	private String phone;

	/**
	 * 产品图片
	 */
	@TableField(exist = false)
	private String productImg;

	/**
	 * 参考价
	 */
	@TableField(exist = false)
	private String referPrice;

	/**
	 * 售价
	 */
	@TableField(exist = false)
	private BigDecimal payPrice;

	/**
	 * 划线价
	 */
	@TableField(exist = false)
	private BigDecimal crossedPrice;

	/**
	 * 产品支付时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "产品支付时间")
	@TableField(exist = false)
	private Date payTime;


    /**
     * 是否显示带宽(0否 1显示)
     */
    @TableField(exist = false)
    @Schema(description = "是否显示带宽(0否 1显示)")
    private Integer isBandwidthDisplay;

    /**
     * 是否显示IP个数（对应划线价字段）
     */
    @TableField(exist = false)
    @Schema(description = "是否显示IP单价（对应划线价字段）(0否 1显示)")
    private Integer isIpCountDisplay;



}