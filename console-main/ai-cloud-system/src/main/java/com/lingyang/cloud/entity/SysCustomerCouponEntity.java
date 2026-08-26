package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.coupon.CouponUseStatusEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 
 * @Author: 王小龙
 * @Date: 2024-10-24 
 */

@Data
@TableName("sys_customer_coupon")
public class SysCustomerCouponEntity  implements Serializable {

	@Serial 
	private static final long serialVersionUID =  9144880375116792770L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
   	@TableField("id")
	private Long id;

	/**
	 * 优惠卷id
	 */
   	@TableField("coupon_id")
	private Long couponId;

	/**
	 * 客户id
	 */
   	@TableField("customer_id")
	private Long customerId;

	/**
	 * 状态， 1 已使用，0 未使用, 2 已失效
	 */
   	@TableField("status")
	private CouponUseStatusEnum status;

	/**
	 * 获取时间/领取时间
	 */
   	@TableField("create_time")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 可使用开始时间
	 */
   	@TableField("use_time_start")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date useTimeStart;

	/**
	 * 可使用结束时间
	 */
   	@TableField("use_time_end")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date useTimeEnd;

}
