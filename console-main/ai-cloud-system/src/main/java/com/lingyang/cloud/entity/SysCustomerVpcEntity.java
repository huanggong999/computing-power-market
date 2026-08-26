package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 
 * @Author: 王小龙
 * @Date: 2024-11-20 
 */

@Data
@TableName("sys_customer_vpc")
public class SysCustomerVpcEntity  implements Serializable {

	@Serial 
	private static final long serialVersionUID =  2985142113064593137L;

	@TableId(type = IdType.AUTO)
   	@TableField("id")
	private Long id;

	/**
	 * 客户id
	 */
   	@TableField("customer_id")
	private Long customerId;

	/**
	 * vpcId
	 */
   	@TableField("vpc_id")
	private String vpcId;

	/**
	 * vpc名称
	 */
   	@TableField("vpc_name")
	private String vpcName;

	/**
	 * vpc描述
	 */
   	@TableField("vpc_desc")
	private String vpcDesc;

	/**
	 * vpc状态 Creating：创建中, Pending：配置中,Available：可用
	 */
   	@TableField("status")
	private String status;

	/**
	 * 网段
	 */
   	@TableField("cidr_block")
	private String cidrBlock;

	/**
	 * 区域id
	 */
   	@TableField("region_id")
	private SourceRegionsEnum regionId;
}
