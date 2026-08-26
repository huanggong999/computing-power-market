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
@TableName("sys_customer_security_group")
public class SysCustomerSecurityGroupEntity  implements Serializable {

	@Serial 
	private static final long serialVersionUID =  5506779851759984000L;

	@TableId(type = IdType.AUTO)
   	@TableField("id")
	private Long id;

	/**
	 * 订单id
	 */
   	@TableField("order_id")
	private Long orderId;

	/**
	 * 订单资源uid
	 */
   	@TableField("order_source_uid")
	private String orderSourceUid;

	/**
	 * 客户实列id
	 */
   	@TableField("customer_instance_id")
	private Long customerInstanceId;

	/**
	 * 客户id
	 */
   	@TableField("customer_id")
	private Long customerId;

	/**
	 * vpc id
	 */
   	@TableField("vpc_id")
	private String vpcId;

	/**
	 * 安全组id
	 */
   	@TableField("security_group_id")
	private String securityGroupId;

	/**
	 * 安全组名称
	 */
   	@TableField("security_group_name")
	private String securityGroupName;

	/**
	 * 安全组描述
	 */
   	@TableField("security_group_desc")
	private String securityGroupDesc;

	/**
	 * 安全组状态 Pending：表示配置中。Available：表示可用。
	 */
   	@TableField("status")
	private String status;

	/**
	 * 可用IP数量
	 */
   	@TableField("available_ip_address_count")
	private Long availableIpAddressCount;

	/**
	 * 区域id
	 */
   	@TableField("region_id")
	private SourceRegionsEnum regionId;

	/**
	 * 可用区id
	 */
   	@TableField("zone_id")
	private String zoneId;

}
