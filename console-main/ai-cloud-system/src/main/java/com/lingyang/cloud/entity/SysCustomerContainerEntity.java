package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 
 * @Author: 吴思镇
 * @Date: 2025-02-10
 */

@Data
@TableName("sys_customer_container")
public class SysCustomerContainerEntity implements Serializable {

	@Serial 
	private static final long serialVersionUID =  9144880375116792770L;

	/**
	 * 主键
	 */
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
	 * 客户id
	 */
   	@TableField("customer_id")
	private Long customerId;

	/**
	 * 集群id
	 */
   	@TableField("cluster_id")
	private String clusterId;

	/**
	 * 集群名称
	 */
	@TableField("cluster_name")
	private String clusterName;

	/**
	 * kubernetes版本号
	 */
	@TableField("kubernetes_version")
	private String kubernetesVersion;

	/**
	 * 是否公网访问
	 */
	@TableField("resource_public_access_default_enabled")
	private Boolean resourcePublicAccessDefaultEnabled;

	/**
	 * 是否API_Server公网访问
	 */
	@TableField("api_server_public_access_enabled")
	private Boolean apiServerPublicAccessEnabled;

	/**
	 * 节点池id
	 */
	@TableField("node_pool_id")
	private String nodePoolId;

	/**
	 * 节点池名称
	 */
	@TableField("node_pool_name")
	private String nodePoolName;

	/**
	 * 节点池数量
	 */
	@TableField("node_pool_number")
	private Integer nodePoolNumber;

	/**
	 * 多子网调度策略
	 */
	@TableField("subnet_policy")
	private String subnetPolicy;

	/**
	 * 客户实例id
	 */
	@TableField("customer_instances_id")
	private Long customerInstancesId;

	/**
	 * 客户子网id
	 */
	@TableField("customer_subnet_id")
	private Long customerSubnetId;

	/**
	 * 是否开启安全加固
	 */
	@TableField("is_open_security_hardening")
	private Boolean isOpenSecurityHardening;

	/**
	 * nat网关id
	 */
	@TableField("nat_id")
	private String natId;

	/**
	 * 负载均衡 id
	 */
	@TableField("clb_id")
	private String clbId;

	/**
	 * 容器状态
	 */
	@TableField("status")
	private ContainerStatusEnum status;

	@TableField(value = "create_time",fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
