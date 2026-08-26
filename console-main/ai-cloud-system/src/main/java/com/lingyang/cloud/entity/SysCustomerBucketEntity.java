package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 
 * @Author: 吴思镇
 * @Date: 2025-02-19
 */

@Data
@TableName("sys_customer_bucket")
public class SysCustomerBucketEntity implements Serializable {

	@Serial 
	private static final long serialVersionUID =  9144880375116792770L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
   	@TableField("id")
	private Long id;

	/**
	 * 客户id
	 */
   	@TableField("customer_id")
	private Long customerId;

	/**
	 * 桶名称
	 */
   	@TableField("name")
	private String name;

	/**
	 * 地域
	 */
	@Schema(description = "地域")
	@TableField("region")
	private String region;

	/**
	 * 存储类型（默认=标准存储）
	 */
	@TableField("storage_type")
	private String storageType;

	/**
	 * 冗余类型（0单冗余 1多AZ冗余）
	 */
	@TableField("redundancy_type")
	private Integer redundancyType;

	/**
	 * 桶策略（0私有 1公共读 2公共读写）
	 */
	@TableField("bucket_strategy")
	private Integer bucketStrategy;

	/**
	 * 是否开启版本控制（0不开启 1开启）
	 */
	@TableField("is_version")
	private Integer isVersion;

	/**
	 * 所属项目
	 */
	@TableField(exist = false)
	private String projectName;

	@TableField(value = "create_time",fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
