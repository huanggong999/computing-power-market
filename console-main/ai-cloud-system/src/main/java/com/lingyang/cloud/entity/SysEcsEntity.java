package com.lingyang.cloud.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 
 * @Author: 王小龙
 * @Date: 2024-11-01 
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ecs")
public class SysEcsEntity extends SysPriceEntity {

	@Serial 
	private static final long serialVersionUID =  5304555177684046142L;


	/**
	 * 产品类型（1火山云引擎 2自建服务器）
	 */
	@Schema(description = "产品类型（1火山云引擎 2自建服务器）")
	@TableField("product_type")
	private Integer productType;

	/**
	 * ecs类型
	 */
   	@TableField("ecs_type")
	@Schema(description = "ecs类型")
	private EcsTypeEnum ecsType;

	/**
	 * 服务器规格
	 */
	@Schema(description = "服务器规格")
   	@TableField("ecs_scale")
	@ExcelProperty("实例规格")
	private String ecsScale;

	/**
	 * cpu数量
	 */
	@Schema(description = "cpu数量")
   	@TableField("cpu_number")
	@ExcelProperty("vCPU")
	private Integer cpuNumber;

	/**
	 * 内存大小
	 */
	@Schema(description = "内存大小")
   	@TableField("memory_size")
	@ExcelProperty("内存 (GiB)")
	private Integer memorySize;

	/**
	 * cpu型号
	 */
	@Schema(description = "cpu型号")
   	@TableField("cpu_model")
	@ExcelProperty("处理器型号")
	private String cpuModel;

	/**
	 * gpu型号
	 */
	@Schema(description = "gpu型号")
   	@TableField("gpu_model")
	@ExcelProperty("GPU型号")
	private String gpuModel;

	/**
	 * gpu内存
	 */
	@Schema(description = "gpu内存")
   	@TableField("gpu_memory")
	@ExcelProperty("GPU显存(GB)")
	private String gpuMemory;


	/**
	 * 可用区
	 */
	@ExcelProperty("地域")
	@Schema(description = "可用区")
   	@TableField("regions_zones")
	private SourceRegionsEnum regionsZones;

	@ExcelProperty("公网费用（按量计费下才有）")
	@TableField("ip_price")
	private BigDecimal ipPrice;

	@Schema(description = "可下单可以用区列表")
	@TableField(exist = false)
	private List<SourceRegionZoneVO>  zoneList;
}