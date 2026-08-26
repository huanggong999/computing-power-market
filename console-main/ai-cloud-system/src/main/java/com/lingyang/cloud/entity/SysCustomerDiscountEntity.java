package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: 
 * @Author: 王小龙
 * @Date: 2024-11-13 
 */

@Data
@TableName("sys_customer_discount")
public class SysCustomerDiscountEntity  implements Serializable {

	@Serial 
	private static final long serialVersionUID =  2009953688959338355L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	@TableField("id")
	@Schema(description = "主键")
	private Long id;

	/**
	 * 客户id
	 */
   	@TableField("customer_id")
	@Schema(description = "客户id")
	@NotNull(message = "客户id不能为空")
	private Long customerId;

	/**
	 * 资源类型
	 */
   	@TableField("source_type")
	@Schema(description = "资源类型")
	@NotNull(message = "资源类型不能为空")
	private SourceTypeEnum sourceType;

	/**
	 * 折扣比列
	 */
   	@TableField("discount_ration")
	@Schema(description = "折扣比列")
	@NotNull(message = "折扣比列不能为空")
	private BigDecimal discountRation;

}
