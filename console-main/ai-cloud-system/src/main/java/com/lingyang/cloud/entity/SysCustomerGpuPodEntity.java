package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lingyang.common.datasource.model.BaseEntity;
import com.lingyang.common.web.serializer.CustomerBigDecimalSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 客户GPU Pod实例表
 * @Author: 王小龙
 * @Date: 2025-06-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_customer_gpu_pod")
public class SysCustomerGpuPodEntity extends BaseEntity {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 客户ID
	 */
	@Schema(description = "客户ID")
	@TableField("customer_id")
	private Long customerId;

	/**
	 * 关联订单ID
	 */
	@Schema(description = "关联订单ID")
	@TableField("order_id")
	private Long orderId;

	/**
	 * 订单资源UID
	 */
	@Schema(description = "订单资源UID")
	@TableField("order_source_uid")
	private String orderSourceUid;

	/**
	 * GPU Pod实例ID
	 */
	@Schema(description = "GPU Pod实例ID")
	@TableField("instance_id")
	private String instanceId;

	/**
	 * 实例名称
	 */
	@Schema(description = "实例名称")
	@TableField("instance_name")
	private String instanceName;

	/**
	 * 状态
	 */
	@Schema(description = "状态")
	@TableField("status")
	private String status;

	/**
	 * 地区编码
	 */
	@Schema(description = "地区编码")
	@TableField("region_code")
	private String regionCode;

	/**
	 * 地区名称
	 */
	@Schema(description = "地区名称")
	@TableField("region_name")
	private String regionName;

	/**
	 * 可用区编码
	 */
	@Schema(description = "可用区编码")
	@TableField("zone_code")
	private String zoneCode;

	/**
	 * 可用区名称
	 */
	@Schema(description = "可用区名称")
	@TableField("zone_name")
	private String zoneName;

	/**
	 * GPU型号
	 */
	@Schema(description = "GPU型号")
	@TableField("gpu_model")
	private String gpuModel;

	/**
	 * GPU数量
	 */
	@Schema(description = "GPU数量")
	@TableField("gpu_count")
	private Integer gpuCount;

	/**
	 * GPU显存
	 */
	@Schema(description = "GPU显存")
	@TableField("gpu_memory")
	private String gpuMemory;

	/**
	 * CPU核数
	 */
	@Schema(description = "CPU核数")
	@TableField("cpu_cores")
	private Integer cpuCores;

	/**
	 * 内存大小
	 */
	@Schema(description = "内存大小")
	@TableField("memory_size")
	private String memorySize;

	/**
	 * 系统盘大小(GB)
	 */
	@Schema(description = "系统盘大小(GB)")
	@TableField("system_disk_size")
	private Integer systemDiskSize;

	/**
	 * 数据盘大小(GB)
	 */
	@Schema(description = "数据盘大小(GB)")
	@TableField("data_disk_size")
	private Integer dataDiskSize;

	/**
	 * 镜像地址
	 */
	@Schema(description = "镜像地址")
	@TableField("image_url")
	private String imageUrl;

	/**
	 * 计费模式
	 */
	@Schema(description = "计费模式")
	@TableField("billing_mode")
	private String billingMode;

	/**
	 * 计费状态
	 */
	@Schema(description = "计费状态")
	@TableField("billing_status")
	private String billingStatus;

	/**
	 * 每小时价格
	 */
	@Schema(description = "每小时价格")
	@TableField("price_per_hour")
	@JsonSerialize(using = CustomerBigDecimalSerialize.class)
	private BigDecimal pricePerHour;

	/**
	 * 启动时间
	 */
	@Schema(description = "启动时间")
	@TableField("start_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/**
	 * 停止时间
	 */
	@Schema(description = "停止时间")
	@TableField("stop_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date stopTime;

	/**
	 * 释放时间
	 */
	@Schema(description = "释放时间")
	@TableField("release_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date releaseTime;

	/**
	 * 到期时间
	 */
	@Schema(description = "到期时间")
	@TableField("expire_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireTime;

	/**
	 * 定时关机时间
	 */
	@Schema(description = "定时关机时间")
	@TableField("scheduled_shutdown_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date scheduledShutdownTime;

	/**
	 * SSH主机
	 */
	@Schema(description = "SSH主机")
	@TableField("ssh_host")
	private String sshHost;

	/**
	 * SSH端口
	 */
	@Schema(description = "SSH端口")
	@TableField("ssh_port")
	private Integer sshPort;

	/**
	 * SSH用户名
	 */
	@Schema(description = "SSH用户名")
	@TableField("ssh_username")
	private String sshUsername;

	/**
	 * SSH密码（加密）
	 */
	@Schema(description = "SSH密码（加密）")
	@TableField("ssh_password")
	private String sshPassword;

	/**
	 * 扩展信息JSON
	 */
	@Schema(description = "扩展信息JSON")
	@TableField("extra_info")
	private String extraInfo;

}
